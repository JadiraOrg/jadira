/*
 *  Copyright 2010, 2011 Christopher Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.bindings.core.loader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.jadira.bindings.core.annotation.BindingScope;
import org.jadira.bindings.core.annotation.DefaultBinding;
import org.jadira.bindings.core.spi.ConverterProvider;
import org.jadira.bindings.core.utils.lang.IterableNodeList;
import org.jadira.bindings.core.utils.reflection.ClassLoaderUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * A class capable of reading configuration from a given URL and producing the
 * resultant {@link BindingConfiguration} representation
 */
public final class BindingXmlLoader {

    private static final String BINDINGS_NAMESPACE = "http://org.jadira.bindings/xml/ns/binding";

	private BindingXmlLoader() {
    }

    /**
     * Given a configuration URL, produce the corresponding configuration
     * @param location The URL
     * @return The relevant {@link BindingConfiguration}
     * @throws IllegalStateException If the configuration cannot be parsed
     */
    public static BindingConfiguration load(URL location) throws IllegalStateException {
    	
        Document doc;
        try {
            doc = loadDocument(location);
        } catch (IOException e) {
            throw new IllegalStateException("Cannot load " + location.toExternalForm(), e);
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("Cannot initialise parser for " + location.toExternalForm(), e);
        } catch (SAXException e) {
            throw new IllegalStateException("Cannot parse " + location.toExternalForm(), e);
        }
        BindingConfiguration configuration = parseDocument(doc);
        return configuration;
    }

    /**
     * Helper method to load a DOM Document from the given configuration URL
     * @param location The configuration URL
     * @return A W3C DOM Document
     * @throws IOException If the configuration cannot be read
     * @throws ParserConfigurationException If the DOM Parser cannot be initialised
     * @throws SAXException If the configuraiton cannot be parsed
     */
    private static Document loadDocument(URL location) throws IOException, ParserConfigurationException, SAXException {

        InputStream inputStream = null;

        if (location != null) {
            URLConnection urlConnection = location.openConnection();
            urlConnection.setUseCaches(false);
            inputStream = urlConnection.getInputStream();
        }
        if (inputStream == null) {
        	if (location == null) {
        		throw new IOException("Failed to obtain InputStream for named location: null");
        	} else {
        		throw new IOException("Failed to obtain InputStream for named location: " + location.toExternalForm());
        	}
        }

        InputSource inputSource = new InputSource(inputStream);

        List<SAXParseException> errors = new ArrayList<SAXParseException>();
        DocumentBuilder docBuilder = constructDocumentBuilder(errors);

        Document document = docBuilder.parse(inputSource);
        if (!errors.isEmpty()) {
        	if (location == null) {
        		throw new IllegalStateException("Invalid File: null", (Throwable) errors.get(0));
        	} else {
        		throw new IllegalStateException("Invalid file: " + location.toExternalForm(), (Throwable) errors.get(0));
        	}
        }
        return document;
    }

    /**
     * Helper used to construct a document builder
     * @param errors A list for holding any errors that take place
     * @return JAXP {@link DocumentBuilder}
     * @throws ParserConfigurationException If the parser cannot be initialised
     */
    private static DocumentBuilder constructDocumentBuilder(List<SAXParseException> errors)
            throws ParserConfigurationException {

        DocumentBuilderFactory documentBuilderFactory = constructDocumentBuilderFactory();
        DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();
        docBuilder.setEntityResolver(new BindingXmlEntityResolver());
        docBuilder.setErrorHandler(new BindingXmlErrorHandler(errors));
        return docBuilder;
    }

    /**
     * Helper used to construct a {@link DocumentBuilderFactory} with schema validation configured
     * @return {@link DocumentBuilderFactory}
     */
    private static DocumentBuilderFactory constructDocumentBuilderFactory() {

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        documentBuilderFactory.setValidating(true);
        documentBuilderFactory.setNamespaceAware(true);

        try {
            documentBuilderFactory.setAttribute("http://apache.org/xml/features/validation/schema", true);
        } catch (IllegalArgumentException e) {
            // Ignore
        }
        documentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                "http://www.w3.org/2001/XMLSchema");
        documentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource",
                "classpath:/jadira-bindings.xsd");
        return documentBuilderFactory;
    }

    /**
     * Walk the parsed {@link Document} and produce a {@link BindingConfiguration}
     * @param doc Document being Parsed
     * @return The resultant {@link BindingConfiguration}
     */
    private static BindingConfiguration parseDocument(Document doc) {

        BindingConfiguration result = new BindingConfiguration();

        Element docRoot = doc.getDocumentElement();
        for (Node next : new IterableNodeList(docRoot.getChildNodes())) {
            if (Node.ELEMENT_NODE == next.getNodeType()) {
                Element element = (Element) next;

                if (BINDINGS_NAMESPACE.equals(element.getNamespaceURI())
                        && "provider".equals(element.getLocalName())) {

                    Provider provider = parseProviderElement(element);
                    result.addProvider(provider);
                }

                if (BINDINGS_NAMESPACE.equals(element.getNamespaceURI())
                        && "extension".equals(element.getLocalName())) {

                    Extension<?> extension = parseBinderExtensionElement(element);
                    result.addExtension(extension);
                }
                
                if (BINDINGS_NAMESPACE.equals(element.getNamespaceURI())
                        && "binding".equals(element.getLocalName())) {

                    BindingConfigurationEntry binding = parseBindingConfigurationEntryElement(element);
                    result.addBindingEntry(binding);
                }
            }
        }
        return result;
    }

    /**
     * Parse the 'provider' element and its children
     * @param element The element
     * @return A {@link Provider} instance for the element
     */
    private static Provider parseProviderElement(Element element) {

        Class<?> providerClass = lookupClass(element.getAttribute("class"));

        if (providerClass == null) {
            throw new IllegalStateException("Referenced class {" + element.getAttribute("class")
                    + "} could not be found");
        }
        if (!ConverterProvider.class.isAssignableFrom(providerClass)) {
            throw new IllegalStateException("Referenced class {" + element.getAttribute("class")
                    + "} did not implement BindingProvider");
        }

        @SuppressWarnings("unchecked")
        final Class<ConverterProvider> typedProviderClass = (Class<ConverterProvider>) providerClass;
        return new Provider((Class<ConverterProvider>) typedProviderClass);
    }

    /**
     * Parse the 'extension' element
     * @param element The element
     * @return A {@link Extension} instance for the element
     */
    private static <T> Extension<T> parseBinderExtensionElement(Element element) {

        @SuppressWarnings("unchecked")
		Class<T> providerClass = (Class<T>)lookupClass(element.getAttribute("class"));
        
        Class<?> implementationClass = lookupClass(element.getAttribute("implementationClass"));

        if (providerClass == null) {
            throw new IllegalStateException("Referenced class {" + element.getAttribute("class")
                    + "} could not be found");
        }
        if (implementationClass == null) {
            throw new IllegalStateException("Referenced implementation class {" + element.getAttribute("implementationClass")
                    + "} could not be found");
        }
        if (providerClass.isAssignableFrom(implementationClass)) {
            throw new IllegalStateException("Referenced class {" + element.getAttribute("class")
                    + "} did not implement BindingProvider");
        }

		try {
			@SuppressWarnings("unchecked")
			final Class<? extends T> myImplementationClass = (Class<T>) implementationClass.newInstance();
			return new Extension<T>(providerClass, myImplementationClass);
		} catch (InstantiationException e) {
            throw new IllegalStateException("Referenced implementation class {" + element.getAttribute("implementationClass")
                    + "} could not be instantiated");
		} catch (IllegalAccessException e) {
            throw new IllegalStateException("Referenced implementation class {" + element.getAttribute("implementationClass")
                    + "} could not be accessed");
		}
    }
    
    /**
     * Parse the {@link BindingConfigurationEntry} element
     * @param element The element
     * @return A {@link BindingConfigurationEntry} element
     */
    @SuppressWarnings("unchecked")
	private static BindingConfigurationEntry parseBindingConfigurationEntryElement(Element element) {

    	Class<?> bindingClass = null;
    	Class<?> sourceClass = null;
    	Class<?> targetClass = null;
        Method toMethod = null;
        Method fromMethod = null;
        Constructor<?> fromConstructor = null;
        Class<? extends Annotation> qualifier = DefaultBinding.class;
        
        if (element.getAttribute("class").length() > 0) {
        	bindingClass = lookupClass(element.getAttribute("class"));
        }
        
        if (element.getAttribute("sourceClass").length() > 0) {
            sourceClass = lookupClass(element.getAttribute("sourceClass"));
        }
        if (element.getAttribute("targetClass").length() > 0) {
        	targetClass = lookupClass(element.getAttribute("targetClass"));
        }

        if (element.getAttribute("qualifier").length() > 0) {
        	
            qualifier = (Class<? extends Annotation>) lookupClass(element.getAttribute("qualifier"));

            if (qualifier.getAnnotation(BindingScope.class) == null) {
                    throw new IllegalStateException("Qualifier class {" + element.getAttribute("qualifier")
                            + "} was not marked as BindingScope");
            }
        }

        if (bindingClass != null) {
	        for (Node next : new IterableNodeList(element.getChildNodes())) {
	            if (Node.ELEMENT_NODE == next.getNodeType()) {
	                Element childElement = (Element) next;
	                if (BINDINGS_NAMESPACE.equals(element.getNamespaceURI())
	                        && "toMethod".equals(element.getLocalName())) {
	
	                    String toMethodName = childElement.getTextContent();
	
	                    try {
	                        toMethod = bindingClass.getMethod(toMethodName, new Class[] { targetClass });
	                    } catch (SecurityException e) {
	                    } catch (NoSuchMethodException e) {
	                    }
	                    if (toMethod != null && (!String.class.equals(toMethod.getReturnType())
	                            || !Modifier.isStatic(toMethod.getModifiers()))) {
	                        toMethod = null;
	                    }
	                    if (toMethod == null && bindingClass.equals(targetClass)) {
	                        try {
	                            toMethod = bindingClass.getMethod(toMethodName, new Class[] {});
	                        } catch (SecurityException e) {
	                        } catch (NoSuchMethodException e) {
	                        }
	                        if (toMethod != null && Modifier.isStatic(toMethod.getModifiers())) {
	                            toMethod = null;
	                        }
	                    }
	
	                } else if (BINDINGS_NAMESPACE.equals(element.getNamespaceURI())
	                        && "fromMethod".equals(element.getLocalName())) {
	
	                    String fromMethodName = childElement.getTextContent();
	
	                    try {
	                        fromMethod = bindingClass.getMethod(fromMethodName, new Class[] { String.class });
	                    } catch (SecurityException e) {
	                    } catch (NoSuchMethodException e) {
	                    }
	                    if (fromMethod != null && ((targetClass != null && !targetClass.isAssignableFrom(fromMethod.getReturnType()))
	                    	|| !Modifier.isStatic(fromMethod.getModifiers()))) {
	                    	fromMethod = null;
	                    }
	
	                } else if (BINDINGS_NAMESPACE.equals(element.getNamespaceURI())
	                        && "fromConstructor".equals(element.getLocalName())) {
	
	                    try {
	                        fromConstructor = bindingClass.getConstructor(new Class[] { String.class });
	                    } catch (SecurityException e) {
	                    } catch (NoSuchMethodException e) {
	                    }
	                }
	            }
	        }
        }

        if (bindingClass == null) {

        	if (sourceClass == null) {
        		throw new IllegalStateException("If bindingClass is not populated, sourceClass must be present");
        	}
        	if (targetClass == null) {
        		throw new IllegalStateException("If bindingClass is not populated, targetClass must be present");
        	}
        	if (fromMethod != null && fromConstructor != null) {
        		throw new IllegalStateException("If fromMethod is populated, fromConstructor must not be present");
        	}

        	if (fromMethod == null) {
        	
        		return new BindingConfigurationEntry(sourceClass, targetClass, qualifier, toMethod, fromConstructor);
        	} else {
        		
        		return new BindingConfigurationEntry(sourceClass, targetClass, qualifier, toMethod, fromMethod);
        	}
        } else {
        	if (sourceClass != null) {
        		throw new IllegalStateException("If bindingClass is populated, sourceClass must not be present");
        	}
        	if (targetClass != null) {
        		throw new IllegalStateException("If bindingClass is populated, targetClass must not be present");
        	}
        	if (toMethod != null) {
        		throw new IllegalStateException("If bindingClass is populated, toMethod must not be present");
        	}
        	if (fromMethod != null) {
        		throw new IllegalStateException("If bindingClass is populated, fromMethod must not be present");
        	}
        	if (fromConstructor != null) {
        		throw new IllegalStateException("If bindingClass is populated, fromConstructor must not be present");
        	}
        	
        	return new BindingConfigurationEntry(bindingClass, qualifier);
        }
    }

    /**
     * Helper method that given a class-name will create the appropriate Class instance
     * @param elementName The class name
     * @return Instance of Class
     */
    private static Class<?> lookupClass(String elementName) {

        Class<?> clazz = null;
        try {
            clazz = ClassLoaderUtils.getClassLoader().loadClass(elementName);
        } catch (ClassNotFoundException e) {
            return null;
        }

        return clazz;
    }

    /**
     * SAX {@link ErrorHandler} that collects errors
     */
    private static class BindingXmlErrorHandler implements ErrorHandler {

        private List<SAXParseException> errors;

        /**
         * Create a new instance with the given error list for collecting errors
         * @param errors Error list to use
         */
        BindingXmlErrorHandler(List<SAXParseException> errors) {
            this.errors = errors;
        }

        /**
         * {@inheritDoc}
         */
        /* @Override */
        public void error(SAXParseException error) {
            errors.add(error);
        }

        /**
         * {@inheritDoc}
         */
        /* @Override */
        public void fatalError(SAXParseException error) {
            errors.add(error);
        }

        /**
         * {@inheritDoc}
         */
        /* @Override */
        public void warning(SAXParseException warn) {
            // ignore
        }
    }
}
