/*
 *  Copyright 2010, 2011 Chris Pheby
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
package org.jadira.bindings.core.binder;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.inject.Typed;

import org.jadira.bindings.core.annotation.BindingScope;
import org.jadira.bindings.core.annotation.DefaultBinding;
import org.jadira.bindings.core.api.Binding;
import org.jadira.bindings.core.api.Converter;
import org.jadira.bindings.core.api.FromUnmarshaller;
import org.jadira.bindings.core.api.ToMarshaller;
import org.jadira.bindings.core.general.binding.CompositeBinding;
import org.jadira.bindings.core.general.binding.InverseCompositeBinding;
import org.jadira.bindings.core.general.converter.FromUnmarshallerConverter;
import org.jadira.bindings.core.general.converter.ToMarshallerConverter;
import org.jadira.bindings.core.general.marshaller.ConverterToMarshaller;
import org.jadira.bindings.core.general.marshaller.MethodToMarshaller;
import org.jadira.bindings.core.general.unmarshaller.ConstructorFromUnmarshaller;
import org.jadira.bindings.core.general.unmarshaller.ConverterFromUnmarshaller;
import org.jadira.bindings.core.general.unmarshaller.MethodFromUnmarshaller;
import org.jadira.bindings.core.jdk.AtomicBooleanStringBinding;
import org.jadira.bindings.core.jdk.AtomicIntegerStringBinding;
import org.jadira.bindings.core.jdk.AtomicLongStringBinding;
import org.jadira.bindings.core.jdk.BigDecimalStringBinding;
import org.jadira.bindings.core.jdk.BigIntegerStringBinding;
import org.jadira.bindings.core.jdk.BooleanStringBinding;
import org.jadira.bindings.core.jdk.ByteStringBinding;
import org.jadira.bindings.core.jdk.CalendarStringBinding;
import org.jadira.bindings.core.jdk.CharSequenceStringBinding;
import org.jadira.bindings.core.jdk.CharacterStringBinding;
import org.jadira.bindings.core.jdk.ClassStringBinding;
import org.jadira.bindings.core.jdk.CurrencyStringBinding;
import org.jadira.bindings.core.jdk.DateStringBinding;
import org.jadira.bindings.core.jdk.DoubleStringBinding;
import org.jadira.bindings.core.jdk.FileStringBinding;
import org.jadira.bindings.core.jdk.FloatStringBinding;
import org.jadira.bindings.core.jdk.InetAddressStringBinding;
import org.jadira.bindings.core.jdk.IntegerStringBinding;
import org.jadira.bindings.core.jdk.LocaleStringBinding;
import org.jadira.bindings.core.jdk.LongStringBinding;
import org.jadira.bindings.core.jdk.PackageStringBinding;
import org.jadira.bindings.core.jdk.ShortStringBinding;
import org.jadira.bindings.core.jdk.StringBufferStringBinding;
import org.jadira.bindings.core.jdk.StringBuilderStringBinding;
import org.jadira.bindings.core.jdk.StringStringBinding;
import org.jadira.bindings.core.jdk.TimeZoneStringBinding;
import org.jadira.bindings.core.jdk.URIStringBinding;
import org.jadira.bindings.core.jdk.URLStringBinding;
import org.jadira.bindings.core.jdk.UUIDStringBinding;
import org.jadira.bindings.core.loader.BindingConfiguration;
import org.jadira.bindings.core.loader.BindingConfigurationEntry;
import org.jadira.bindings.core.loader.BindingXmlLoader;
import org.jadira.bindings.core.loader.Extension;
import org.jadira.bindings.core.loader.Provider;
import org.jadira.bindings.core.spi.ConverterProvider;
import org.jadira.bindings.core.utils.lang.IterableEnumeration;
import org.jadira.bindings.core.utils.reflection.ClassLoaderUtils;

/**
 * Core binding capability.
 * Typically you would obtain an instance of this class either
 * by direct construction, or in JDK6 and above by using 
 * <code>java.util.ServiceLoader</code>.
 * 
 * Default construction of this class registers bindings for core
 * JDK classes, as well as any bindings specified using bindings.xml
 * 
 * You can also register additional bindings programmatically.
 * @see Binding Binding is used to define a binding
 */
@Typed({})
public class BasicBinder implements Binder, RegisterableBinder {

	/**
	 * ConverterProviders are used to resolve converters using annotations on the target class.
	 * Because additional converter providers can be registered they provide a mechanism for extending the framework.
	 */
	private final List<ConverterProvider> converterProviders = new ArrayList<ConverterProvider>();
	
	/**
	 * A map of registered converters
	 */
	private ConcurrentHashMap<ConverterKey<?,?>, Converter<?,?>> registeredConverters = new ConcurrentHashMap<ConverterKey<?,?>, Converter<?,?>>();

	/**
	 * A map of registered converters
	 */
	private ConcurrentHashMap<Class<?>, Object> extendedBinders = new ConcurrentHashMap<Class<?>, Object>();
	
	/**
	 * A reference of classes which have already been introspected for conversion annotations.
	 */
    private final Set<Class<?>> inspectedClasses = new HashSet<Class<?>>();
	
    /**
     * Creates a new instance, initialised with standard, and registered bindings
     */
    public BasicBinder() {
        this(true);
    }

    /**
     * Creates a new instance
     * @param includeBuiltInBindings If true, initialise with standard, and registered bindings
     */
    public BasicBinder(boolean includeBuiltInBindings) {

    	initExtendedBinders();
    	
        if (includeBuiltInBindings) {

            initJdkBindings();

            // Before we load other configuration, load the binding
            // configuration from
            // jadira-bindings' jar to ensure no-one overrides our built-in
            // mappings.
            initBuiltInBindings();

            // Init bindings registered from other jars
            initRegisteredBindings();
        }
    }
	
    /******************
     *                *
     * Bootstrapping  * 
     *                *
     ******************/

	/**
	 *  Initialise the instance with built in extended binders
	 */
	private void initExtendedBinders() {
		extendedBinders.put(StringBinder.class, this);
	}
    
    /**
     * Initialises standard bindings for Java built in types
     */
    private void initJdkBindings() {
        
        registerBinding(AtomicBoolean.class, String.class, new AtomicBooleanStringBinding());
        registerBinding(AtomicInteger.class, String.class, new AtomicIntegerStringBinding());
        registerBinding(AtomicLong.class, String.class, new AtomicLongStringBinding());
        registerBinding(BigDecimal.class, String.class, new BigDecimalStringBinding());
        registerBinding(BigInteger.class, String.class, new BigIntegerStringBinding());
        registerBinding(Boolean.class, String.class, new BooleanStringBinding());
        registerBinding(Byte.class, String.class, new ByteStringBinding());
        registerBinding(Calendar.class, String.class, new CalendarStringBinding());
        registerBinding(Character.class, String.class, new CharacterStringBinding());
        registerBinding(CharSequence.class, String.class, new CharSequenceStringBinding());
        registerBinding(Class.class, String.class, new ClassStringBinding());
        registerBinding(Currency.class, String.class, new CurrencyStringBinding());
        registerBinding(Date.class, String.class, new DateStringBinding());
        registerBinding(Double.class, String.class, new DoubleStringBinding());
        registerBinding(File.class, String.class, new FileStringBinding());
        registerBinding(Float.class, String.class, new FloatStringBinding());
        registerBinding(InetAddress.class, String.class, new InetAddressStringBinding());
        registerBinding(Integer.class, String.class, new IntegerStringBinding());
        registerBinding(Locale.class, String.class, new LocaleStringBinding());
        registerBinding(Long.class, String.class, new LongStringBinding());
        registerBinding(Package.class, String.class, new PackageStringBinding());
        registerBinding(Short.class, String.class, new ShortStringBinding());
        registerBinding(StringBuffer.class, String.class, new StringBufferStringBinding());
        registerBinding(StringBuilder.class, String.class, new StringBuilderStringBinding());
        registerBinding(String.class, String.class, new StringStringBinding());
        registerBinding(TimeZone.class, String.class, new TimeZoneStringBinding());
        registerBinding(URI.class, String.class, new URIStringBinding());
        registerBinding(URL.class, String.class, new URLStringBinding());
        registerBinding(UUID.class, String.class, new UUIDStringBinding());
    }
 
    /**
     * Initialises bindings registered with the framework included bindings.xml
     * This includes the built in support for converters for annotation processing and Joda Convert annotations.
     */
    private void initBuiltInBindings() {

        final URL builtInBindingsUrl = getBuiltInBindingsURL();
        registerConfiguration(builtInBindingsUrl);
    }

	private URL getBuiltInBindingsURL() {

		String classResource = BasicBinder.class.getCanonicalName().replace('.', '/') + ".class";
        
        URL bindingClassUrl = Thread.currentThread().getContextClassLoader().getResource(classResource);
        
        String classPrefix = bindingClassUrl.toString().substring(0,
                bindingClassUrl.toString().indexOf(classResource));

        final URL builtInBindingsUrl;
        try {
            builtInBindingsUrl = new URL(classPrefix + "META-INF/bindings.xml");
        } catch (IOException e) {
            throw new IllegalStateException("Error registering bindings: " + e.getMessage(), e);
        }
		return builtInBindingsUrl;
	}
    
    /**
     * Initialises any bindings bundled with other (third-party / user) jars
     */
    private void initRegisteredBindings() {
        
        Enumeration<URL> bindingsConfiguration;
        try {
            bindingsConfiguration = ClassLoaderUtils.getClassLoader().getResources("META-INF/bindings.xml");
        } catch (IOException e) {
            throw new IllegalStateException("Error registering bindings: " + e.getMessage(), e);
        }

        registerConfigurations(bindingsConfiguration);
    }

    /**
     * Registers a set of configurations for the given list of URLs. 
     * This is typically used to process all the various bindings.xml files discovered in
     * jars on the classpath. It is given protected scope to allow subclasses to register
     * additional configurations
     * @param bindingsConfiguration An enumeration of the URLs to process
     */
    protected <X> void registerConfigurations(Enumeration<URL> bindingsConfiguration) {
        List<BindingConfiguration> configs = new ArrayList<BindingConfiguration>();
        for (URL nextLocation : IterableEnumeration.wrapEnumeration(bindingsConfiguration)) {
            
        	// Filter built in bindings - these are already registered by calling registerConfiguration directly
        	URL builtIn = getBuiltInBindingsURL();
        	if (!builtIn.toString().equals(nextLocation.toString())) {
        		configs.add(BindingXmlLoader.load(nextLocation));
        	}
        }
        
        for (BindingConfiguration nextConfig : configs) {
                for (Provider nextProvider : nextConfig.getProviders()) {
            
	                try {
	                    registerConverterProvider(nextProvider.getProviderClass().newInstance());
	                } catch (InstantiationException e) {
	                    throw new IllegalStateException("Cannot instantiate binding provider class: " + nextProvider.getProviderClass().getName());
	                } catch (IllegalAccessException e) {
	                    throw new IllegalStateException("Cannot access binding provider class: " + nextProvider.getProviderClass().getName());
	                }
                }
                
                for (Extension<?> nextExtension : nextConfig.getExtensions()) {
            		try {
            			@SuppressWarnings("unchecked") Extension<X> myExtension = (Extension<X>)nextExtension;
            			@SuppressWarnings("unchecked") X myImplementation = (X) nextExtension.getImplementationClass().newInstance();
            			registerExtendedBinder(myExtension.getExtensionClass(), myImplementation);
	                } catch (InstantiationException e) {
	                    throw new IllegalStateException("Cannot instantiate binder extension class: " + nextExtension.getExtensionClass().getName());
	                } catch (IllegalAccessException e) {
	                    throw new IllegalStateException("Cannot access binder extension class: " + nextExtension.getExtensionClass().getName());
	                }
                }
        
            registerBindingConfigurationEntries(nextConfig.getBindingEntries());
        }
    }
    
    /**
     * Register the configuration file (bindings.xml) at the given URL 
     */
    public final void registerConfiguration(URL nextLocation) {
        
        BindingConfiguration configuration = BindingXmlLoader.load(nextLocation);

        for (Provider nextProvider : configuration.getProviders()) {
            
            try {
                registerConverterProvider(nextProvider.getProviderClass().newInstance());
            } catch (InstantiationException e) {
                throw new IllegalStateException("Cannot instantiate binding provider class: " + nextProvider.getProviderClass().getName());
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot access binding provider class: " + nextProvider.getProviderClass().getName());
            }
        }
        
        registerBindingConfigurationEntries(configuration.getBindingEntries());
    }
    
    /**
     * Method used to register one or more converter providers. Converters extend the binding framework to provide 
     * additional mechanisms for discovering and registering bindings (e.g. using Joda Convert).
     * This method is protected so that subclasses can register additional providers.
     * @param providers A collection of providers
     */
    public void registerConverterProviders(ConverterProvider... providers) {

        for (ConverterProvider nextProvider : providers) {
            registerConverterProvider(nextProvider);
        }
    }
    
    /**
     * Register a single converter provider
     * @param provider The provider.
     */
    public void registerConverterProvider(ConverterProvider provider) {
        this.converterProviders.add(provider);
    }
    
    /**
     * Registers a list of binding configuration entries. A binding configuration entry described in a section of a bindings.xml file 
     * and describes the use of a particular method for databinding.
     * @param bindings The entries to register
     */
    protected void registerBindingConfigurationEntries(Iterable<BindingConfigurationEntry> bindings) {

        for (BindingConfigurationEntry nextBinding : bindings) {
        	try {
        		registerBindingConfigurationEntry(nextBinding);
        	} catch (IllegalStateException e) {
        		// Ignore this - it can happen when introspecting class mappings
        	}
        }
    }
    
    /**
     * Register a particular binding configuration entry.
     * @param theBinding The entry to be registered
     */
	protected <S,T> void registerBindingConfigurationEntry(BindingConfigurationEntry theBinding) {
		
        /*
         * BindingConfigurationEntry has two possible configurations:
         * 
         * bindingClass with an optional qualifier (this defaults to DefaultBinding)
         * 
         *  OR
         *  
         * sourceClass and
         * targetClass and
         * optional qualifier (defaults to DefaultBinding)
         * with at least one of either
         * toMethod and/or
         * fromMethod and/or
         * fromConstructor         
         * 
         * Depending on which components are populated the entry is interpreted differently.
         */
		
		if (Binding.class.isAssignableFrom(theBinding.getBindingClass())) {
        	
        	/*
         	 * If the binding class is an instance of the Binding interface then register it.
         	 * When the binding class is an interface, you must define source and target class if they cannot be
         	 * determined from introspecting the interface.
         	 * 
         	 * You can optionally supply a qualifier so that the binding is associated with a qualifier
         	 */
        	try {
        		@SuppressWarnings("unchecked")
				Binding<S,T> binding = (Binding<S,T>)theBinding.getBindingClass().newInstance();
                registerBinding(binding.getBoundClass(), binding.getTargetClass(), binding, theBinding.getQualifier());
            } catch (InstantiationException e) {
                throw new IllegalStateException("Cannot instantiate binding class: " + theBinding.getBindingClass().getName());
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot access binding class: " + theBinding.getBindingClass().getName());
            }
        } else if (FromUnmarshaller.class.isAssignableFrom(theBinding.getBindingClass())) {
        	
        	/*
         	 * If the binding class is an instance of the FromUnmarshaller interface then register it.
         	 * When the class is an interface, you must define source and target class if they cannot be
         	 * determined from introspecting the interface.
         	 * 
         	 * You can optionally supply a qualifier so that the binding is associated with a qualifier
         	 */
        	try {
        		@SuppressWarnings("unchecked")
				FromUnmarshaller<S,T> fromUnmarshaller = (FromUnmarshaller<S,T>)theBinding.getBindingClass().newInstance();
                registerUnmarshaller(fromUnmarshaller.getBoundClass(), fromUnmarshaller.getTargetClass(), fromUnmarshaller, theBinding.getQualifier());
            } catch (InstantiationException e) {
                throw new IllegalStateException("Cannot instantiate binding class: " + theBinding.getBindingClass().getName());
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot access binding class: " + theBinding.getBindingClass().getName());
            }
		} else if (ToMarshaller.class.isAssignableFrom(theBinding.getBindingClass())) {
        	
        	/*
         	 * If the binding class is an instance of the ToMarshaller interface then register it.
         	 * When the class is an interface, you must define source and target class if they cannot be
         	 * determined from introspecting the interface.
         	 * 
         	 * You can optionally supply a qualifier so that the binding is associated with a qualifier
         	 */
        	try {
        		@SuppressWarnings("unchecked")
				ToMarshaller<S,T> toMarshaller = (ToMarshaller<S,T>)theBinding.getBindingClass().newInstance();
                registerMarshaller(toMarshaller.getBoundClass(), toMarshaller.getTargetClass(), toMarshaller, theBinding.getQualifier());
            } catch (InstantiationException e) {
                throw new IllegalStateException("Cannot instantiate binding class: " + theBinding.getBindingClass().getName());
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot access binding class: " + theBinding.getBindingClass().getName());
            }
		} else if (Converter.class.isAssignableFrom(theBinding.getBindingClass())) {
        	
        	/*
         	 * If the binding class is an instance of the Converter interface then register it.
         	 * When the class is an interface, you must define source and target class if they cannot be
         	 * determined from introspecting the interface.
         	 * 
         	 * You can optionally supply a qualifier so that the binding is associated with a qualifier
         	 */
        	try {
        		@SuppressWarnings("unchecked")
				Converter<S,T> converter = (Converter<S,T>)theBinding.getBindingClass().newInstance();
                registerConverter(converter.getInputClass(), converter.getOutputClass(), converter, theBinding.getQualifier());
            } catch (InstantiationException e) {
                throw new IllegalStateException("Cannot instantiate binding class: " + theBinding.getBindingClass().getName());
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Cannot access binding class: " + theBinding.getBindingClass().getName());
            }
		} else if (theBinding.getBindingClass() != null) {

    		/*
    		 * If only the binding class is supplied, then inspect it for bindings, gathering all bindings identified
    		 */
			registerAnnotatedClasses(theBinding.getBindingClass());
        } else {
        	
    		/*
    		 * Register the binding using the explicit method details provided 
    		 */
        	@SuppressWarnings("unchecked")
        	ConverterKey<S,T> converterKey = new ConverterKey<S,T>((Class<S>)theBinding.getSourceClass(), (Class<T>)theBinding.getTargetClass(), theBinding.getQualifier());
        	@SuppressWarnings("unchecked")
        	Constructor<S> fromConstructor = (Constructor<S>)theBinding.getFromConstructor();
        	
            registerForMethods(converterKey, theBinding.getToMethod(), theBinding.getFromMethod(), fromConstructor);
        }
    }
    
	/**
	 * Inspect each of the supplied classes, processing any of the annotated methods found
	 * @param classesToInspect
	 */
	public void registerAnnotatedClasses(Class<?>... classesToInspect) {
		
		for (Class<?> nextClass : classesToInspect) {
			
			Class<?> loopClass = nextClass;
			while ((loopClass != Object.class) && (!inspectedClasses.contains(loopClass))) {
	
				attachForAnnotations(loopClass);
	
				loopClass = loopClass.getSuperclass();
			}		
		}
	}

    /**********************
     *                    *
     * Extended Binders   * 
     *                    *
     **********************/    
	
    /**
     * Register a custom, typesafe binder implementation which can be retrieved later
     * @param provider The implementation.
     */
    protected <I, T extends I> void registerExtendedBinder(Class<I> iface, T provider) {
        extendedBinders.put(iface, provider);
    }
    
    /**
     * Retrieves an extended binder
     * @param provider The implementation.
     */
    @SuppressWarnings("unchecked")
	protected <I> I getExtendedBinder(Class<I> cls) {
        return (I) extendedBinders.get(cls);
    }
	
    /******************
     *                *
     * Registration   * 
     *                *
     ******************/
    
	/**
	 * Register a Binding with the given source and target class.
	 * A binding unifies a marshaller and an unmarshaller and both must be available to resolve a binding.
	 * 
	 * The source class is considered the owning class of the binding. The source can be marshalled
	 * into the target class. Similarly, the target can be unmarshalled to produce an instance of the source type.
	 * @param source The source (owning) class
	 * @param target The target (foreign) class
	 * @param converter The binding to be registered
	 */
	public final <S, T> void registerBinding(Class<S> source, Class<T> target, Binding<S, T> converter) {
        Class<? extends Annotation> scope = matchImplementationToScope(converter.getClass());
        registerBinding(new ConverterKey<S,T>(source, target, scope == null ? DefaultBinding.class : scope), converter);	
	}

	/**
	 * Register an UnMarshaller with the given source and target class.
	 * The unmarshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param source The source (input) class
	 * @param target The target (output) class
	 * @param converter The FromUnmarshaller to be registered  
	 */
	public final <S, T> void registerUnmarshaller(Class<S> source, Class<T> target, FromUnmarshaller<S, T> converter) {
        Class<? extends Annotation> scope = matchImplementationToScope(converter.getClass());
        registerUnmarshaller(new ConverterKey<S,T>(source, target, scope == null ? DefaultBinding.class : scope), converter);	
	}

	/**
	 * Register a Marshaller with the given source and target class.
	 * The marshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param source The source (input) class
	 * @param target The target (output) class
	 * @param converter The ToMarshaller to be registered
	 */
	public final <S, T> void registerMarshaller(Class<S> source, Class<T> target, ToMarshaller<S, T> converter) {
        Class<? extends Annotation> scope = matchImplementationToScope(converter.getClass());
        registerMarshaller(new ConverterKey<S,T>(source, target, scope == null ? DefaultBinding.class : scope), converter);	
	}
	
	/**
	 * Register a Converter with the given input and output classes. Instances of the input class can be converted into 
	 * instances of the output class
	 * @param input The input class
	 * @param output The output class
	 * @param converter The Converter to be registered   
	 */
	public final <S, T> void registerConverter(Class<S> input, Class<T> output, Converter<S, T> converter) {
        Class<? extends Annotation> scope = matchImplementationToScope(converter.getClass());
        registerConverter(new ConverterKey<S,T>(input, output, scope == null ? DefaultBinding.class : scope), converter);
    }

	/**
	 * Register a Binding with the given source and target class.
	 * A binding unifies a marshaller and an unmarshaller and both must be available to resolve a binding.
	 * 
	 * The source class is considered the owning class of the binding. The source can be marshalled
	 * into the target class. Similarly, the target can be unmarshalled to produce an instance of the source type.
	 * @param source The source (owning) class
	 * @param target The target (foreign) class
	 * @param converter The binding to be registered
	 * @param qualifier The qualifier for which the binding must be registered
	 */
	public final <S, T> void registerBinding(Class<S> source, Class<T> target, Binding<S, T> converter, Class<? extends Annotation> qualifier) {
		registerBinding(new ConverterKey<S,T>(source, target, qualifier == null ? DefaultBinding.class : qualifier), converter);
	}

	/**
	 * Register a Binding with the given source and target class.
	 * A binding unifies a marshaller and an unmarshaller and both must be available to resolve a binding.
	 * 
	 * The source class is considered the owning class of the binding. The source can be marshalled
	 * into the target class. Similarly, the target can be unmarshalled to produce an instance of the source type.
	 * @param key Converter Key to use
	 * @param converter The binding to be registered
	 */
	public final <S, T> void registerBinding(ConverterKey<S,T> key, Binding<S, T> converter) {
		registerConverter(key.invert(), new FromUnmarshallerConverter<S,T>(converter));
		registerConverter(key, new ToMarshallerConverter<S,T>(converter));
	}

	
	/**
	 * Register an UnMarshaller with the given source and target class.
	 * The unmarshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param source The source (input) class
	 * @param target The target (output) class
	 * @param converter The FromUnmarshaller to be registered
	 * @param qualifier The qualifier for which the unmarshaller must be registered  
	 */
	public final <S, T> void registerUnmarshaller(Class<S> source, Class<T> target, FromUnmarshaller<S, T> converter, Class<? extends Annotation> qualifier) {
		registerUnmarshaller(new ConverterKey<S,T>(source, target, qualifier == null ? DefaultBinding.class : qualifier), converter);
	}

	/**
	 * Register an UnMarshaller with the given source and target class.
	 * The unmarshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param key Converter Key to use
	 * @param converter The FromUnmarshaller to be registered
	 */
	public final <S, T> void registerUnmarshaller(ConverterKey<S,T> key, FromUnmarshaller<S, T> converter) {
		registerConverter(key.invert(), new FromUnmarshallerConverter<S,T>(converter));
	}
	
	/**
	 * Register a Marshaller with the given source and target class.
	 * The marshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param source The source (input) class
	 * @param target The target (output) class
	 * @param converter The ToMarshaller to be registered
	 * @param qualifier The qualifier for which the marshaller must be registered 
	 */
	public final <S, T> void registerMarshaller(Class<S> source, Class<T> target, ToMarshaller<S, T> converter, Class<? extends Annotation> qualifier) {
		registerMarshaller(new ConverterKey<S,T>(source, target, qualifier == null ? DefaultBinding.class : qualifier), converter);
	}

	/**
	 * Register a Marshaller with the given source and target class.
	 * The marshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param key Converter Key to use
	 * @param converter The ToMarshaller to be registered
	 */
	public final <S, T> void registerMarshaller(ConverterKey<S,T> key, ToMarshaller<S, T> converter) {
		registerConverter(key, new ToMarshallerConverter<S,T>(converter));
	}
	
	/**
	 * Register a Converter with the given input and output classes. Instances of the input class can be converted into 
	 * instances of the output class
	 * @param input The input class
	 * @param output The output class
	 * @param converter The Converter to be registered
	 * @param qualifier The qualifier for which the converter must be registered   
	 */
	public final <S, T> void registerConverter(Class<S> input, Class<T> output, Converter<S, T> converter, Class<? extends Annotation> qualifier) {
		registerConverter(new ConverterKey<S,T>(input, output, qualifier == null ? DefaultBinding.class : qualifier), converter);
	}
		
	/**
	 * Register a Converter with the given input and output classes. Instances of the input class can be converted into 
	 * instances of the output class
	 * @param key Converter Key to use
	 * @param converter The Converter to be registered
	 */
	public final <S, T> void registerConverter(ConverterKey<S,T> key, Converter<S, T> converter) {	

        if (key.getInputClass() == null) {
            throw new IllegalArgumentException("Input Class must not be null");
        }
        if (key.getOutputClass() == null) {
            throw new IllegalArgumentException("Output Class must not be null");
        }
        if (converter == null) {
            throw new IllegalArgumentException("Converter must not be null");
        }

        if (key.getQualifierAnnotation() == null) {
            throw new IllegalArgumentException("Qualifier must not be null");
        }

        Converter<?,?> old = registeredConverters.putIfAbsent(key, converter);
        if (old != null && (!isSameConverter(old, converter))) {
        	throw new IllegalStateException("Converter already registered for key: " + key);
        }
    }
	
	private boolean isSameConverter(Converter<?,?> old, Converter<?,?> converter) {
		
		if (old.getClass().equals(converter.getClass())) {
			return true;
		}

		// Special cases which arises when processing the identity function
		if (old instanceof FromUnmarshallerConverter && converter instanceof ToMarshallerConverter
				&& (((FromUnmarshallerConverter<?, ?>)old).getUnmarshaller().equals(((ToMarshallerConverter<?, ?>)converter).getMarshaller()))) {
			return true;
		}
		if (old instanceof ToMarshallerConverter && converter instanceof FromUnmarshallerConverter
				&& (((ToMarshallerConverter<?, ?>)old).getMarshaller().equals(((FromUnmarshallerConverter<?, ?>)converter).getUnmarshaller()))) {
			return true;
		}
		return false;
	}
	
    /******************
     *                *
     * Conversion API * 
     *                *
     ******************/
	
	/**
	 * Convert an object to the given target class
	 * This method infers the source type for the conversion from the runtime type of object.
	 * @param output The target class to convert the object to
	 * @param object The object to be converted
	 */
	public <S, T> T convertTo(Class<T> output, Object object) {
		return convertTo(output, object, DefaultBinding.class);
	}
	
	/**
	 * Convert an object to the given target class
	 * This method infers the source type for the conversion from the runtime type of object.
	 * @param output The target class to convert the object to
	 * @param object The object to be converted
	 * @param qualifier The qualifier for which the binding must be registered
	 */
	public <S, T> T convertTo(Class<T> output, Object object, Class<? extends Annotation> qualifier) {

		if (object == null) {
			return null;
		}
		
		@SuppressWarnings("unchecked")
		Converter<S, T> conv = (Converter<S, T>) determineConverter(object.getClass(), output, qualifier == null ? DefaultBinding.class : qualifier);
		
		if (conv == null) {
			@SuppressWarnings("unchecked")
			Class<S> inputClass = (Class<S>)object.getClass();
			throw new NoConverterFoundException(new ConverterKey<S,T>(inputClass, output, qualifier == null ? DefaultBinding.class : qualifier));
		}
		
		@SuppressWarnings("unchecked")
		S myObject = (S)object;
		return conv.convert(myObject);
	}
		
	private <S, T> Converter<S, T> determineConverter(Class<S> candidateClass, Class<T> output, Class<? extends Annotation> qualifier) {
		
		if (!candidateClass.equals(Object.class)) {
			Converter<S, T> match = findConverter(candidateClass, output, qualifier);
			if (match != null) {
				return match;
			}

			@SuppressWarnings("unchecked")	
			Class<S>[] interfaces = (Class<S>[])candidateClass.getInterfaces();
			for (Class<S> candidateInterface : interfaces) {
				match = determineConverter(candidateInterface, output, qualifier);
				if (match != null) {
					return match;
				}	
			}
			
			Class<? super S> superClass = (Class<? super S>)candidateClass.getSuperclass();
			
			@SuppressWarnings("unchecked")
			Converter<S,T> superMatch = (Converter<S, T>) determineConverter(superClass, output, qualifier);
			return superMatch;
		} else {
			return null;
		}
	}
	
	/**
	 * Convert an object which is an instance of source class to the given target class
	 * @param input The class of the object to be converted
	 * @param output The target class to convert the object to
	 * @param object The object to be converted
	 */
	public <S, T> T convertTo(Class<S> input, Class<T> output, Object object) {
		return convertTo(new ConverterKey<S,T>(input, output, DefaultBinding.class), object);
	}

	/**
	 * Convert an object which is an instance of source class to the given target class
	 * @param input The class of the object to be converted
	 * @param output The target class to convert the object to
	 * @param object The object to be converted
	 * @param qualifier Match the converter with the given qualifier
	 */
	public <S, T> T convertTo(Class<S> input, Class<T> output, Object object, Class<? extends Annotation> qualifier) {

		return convertTo(new ConverterKey<S,T>(input, output, qualifier), object);
	}

	/**
	 * Convert an object which is an instance of source class to the given target class
	 * @param key The converter key to use
	 * @param object The object to be converted
	 */
	public <S, T> T convertTo(ConverterKey<S,T> key, Object object) {

		if (object == null) {
			return null;
		}
		
		Converter<S, T> conv = findConverter(key.getInputClass(), key.getOutputClass(), key.getQualifierAnnotation() == null ? DefaultBinding.class : key.getQualifierAnnotation());
		
		if (conv == null) {
			throw new NoConverterFoundException(key);
		}
		
		@SuppressWarnings("unchecked")
		S myObject = (S)object;
		return conv.convert(myObject);
	}
	
    /*************************
     *                       *
     * Conversion Resolution * 
     *                       *
     *************************/
	
	/**
	 * Resolve a Binding with the given source and target class.
	 * A binding unifies a marshaller and an unmarshaller and both must be available to resolve a binding.
	 * 
	 * The source class is considered the owning class of the binding. The source can be marshalled
	 * into the target class. Similarly, the target can be unmarshalled to produce an instance of the source type.
	 * @param source The source (owning) class
	 * @param target The target (foreign) class
	 */
	public <S, T> Binding<S, T> findBinding(Class<S> source, Class<T> target) {
		return findBinding(new ConverterKey<S,T>(source, target, DefaultBinding.class));
	}

	/**
	 * Resolve a Marshaller with the given source and target class.
	 * The marshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param source The source (input) class
	 * @param target The target (output) class 
	 */
	public <S, T> ToMarshaller<S, T> findMarshaller(Class<S> source, Class<T> target) {
		return findMarshaller(new ConverterKey<S,T>(source, target, DefaultBinding.class));
	}

	/**
	 * Resolve a Converter with the given input and output classes. Instances of the input class can be converted into 
	 * instances of the output class
	 * @param input The input class
	 * @param output The output class  
	 */
	public <S, T> Converter<S, T> findConverter(Class<S> input, Class<T> output) {
		return findConverter(new ConverterKey<S,T>(input, output, DefaultBinding.class));
	}

	/**
	 * Resolve an UnMarshaller with the given source and target class.
	 * The unmarshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param source The source (input) class
	 * @param target The target (output) class 
	 */
	public <S, T> FromUnmarshaller<S, T> findUnmarshaller(Class<S> source, Class<T> target) {
		return findUnmarshaller(new ConverterKey<S,T>(source, target, DefaultBinding.class));
	}

	/**
	 * Resolve a Binding with the given source and target class.
	 * A binding unifies a marshaller and an unmarshaller and both must be available to resolve a binding.
	 * 
	 * The source class is considered the owning class of the binding. The source can be marshalled
	 * into the target class. Similarly, the target can be unmarshalled to produce an instance of the source type.
	 * @param source The source (owning) class
	 * @param target The target (foreign) class
	 * @param qualifier The qualifier for which the binding must be registered
	 */
	public <S, T> Binding<S, T> findBinding(Class<S> source, Class<T> target, Class<? extends Annotation> qualifier) {
		return findBinding(new ConverterKey<S,T>(source, target, qualifier == null ? DefaultBinding.class : qualifier));
	}
	
	/**
	 * Resolve a Binding with the given source and target class.
	 * A binding unifies a marshaller and an unmarshaller and both must be available to resolve a binding.
	 * 
	 * The source class is considered the owning class of the binding. The source can be marshalled
	 * into the target class. Similarly, the target can be unmarshalled to produce an instance of the source type.
	 * @param key The key to look up
	 */
	public <S, T> Binding<S, T> findBinding(ConverterKey<S,T> key) {

		FromUnmarshaller<?,?> fromUnmarshaller = null;
		ToMarshaller<?,?> toMarshaller = null;
		
		Converter<S, T> toTarget = findConverter(key);
		if (toTarget instanceof FromUnmarshallerConverter<?, ?>) {
			fromUnmarshaller = ((FromUnmarshallerConverter<?, ?>) toTarget).getUnmarshaller();
		} else if (toTarget instanceof ToMarshallerConverter<?, ?>) {
			toMarshaller = ((ToMarshallerConverter<?, ?>) toTarget).getMarshaller();
		}
		Converter<T, S> toSource = findConverter(key.invert());
		if (toSource instanceof FromUnmarshallerConverter<?, ?>) {
			fromUnmarshaller = ((FromUnmarshallerConverter<?, ?>) toSource).getUnmarshaller();
		} else if (toSource instanceof ToMarshallerConverter<?, ?>) {
			toMarshaller = ((ToMarshallerConverter<?, ?>) toSource).getMarshaller();
		}
		
		if (fromUnmarshaller != null && toMarshaller != null) {
			
			if (fromUnmarshaller.equals(toMarshaller) && Binding.class.isAssignableFrom(fromUnmarshaller.getClass())) {
				Binding<?,?> theBinding = (Binding<?,?>)fromUnmarshaller;
				if (theBinding.getBoundClass().equals(key.getInputClass())) {
					@SuppressWarnings("unchecked")
					final Binding<S, T>myBinding = (Binding<S, T>)theBinding;
					return myBinding;
				}
			}
			
			if (fromUnmarshaller.getBoundClass().equals(key.getInputClass())) {
				@SuppressWarnings("unchecked")
				final Binding<S, T>myBinding = new CompositeBinding<S,T>((ToMarshaller<S,T>)toMarshaller, (FromUnmarshaller<S,T>)fromUnmarshaller);
				return myBinding;
			} else {
				@SuppressWarnings("unchecked")
				final Binding<S, T> myBinding = new InverseCompositeBinding<S,T>((ToMarshaller<T,S>)toMarshaller, (FromUnmarshaller<T,S>)fromUnmarshaller);
				return myBinding;
			}
		}
		return null;
	}

	/**
	 * Resolve a Marshaller with the given source and target class.
	 * The marshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param source The source (input) class
	 * @param target The target (output) class
	 * @param qualifier The qualifier for which the marshaller must be registered 
	 */
	public <S, T> ToMarshaller<S, T> findMarshaller(Class<S> source, Class<T> target, Class<? extends Annotation> qualifier) {
		return findMarshaller(new ConverterKey<S,T>(source, target, qualifier == null ? DefaultBinding.class : qualifier));
	}
	
	/**
	 * Resolve a Marshaller with the given source and target class.
	 * The marshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param key The key to look up
	 */
	public <S, T> ToMarshaller<S, T> findMarshaller(ConverterKey<S,T> key) {

		Converter<S,T> converter = findConverter(key);
		
		if (converter == null) {
			return null;
		}
		
		if (ToMarshallerConverter.class.isAssignableFrom(converter.getClass())) {
			return ((ToMarshallerConverter<S, T>)converter).getMarshaller();
		} else {
			return new ConverterToMarshaller<S, T>(converter);
		}
	}

	/**
	 * Resolve a Converter with the given input and output classes. Instances of the input class can be converted into 
	 * instances of the output class
	 * @param input The input class
	 * @param output The output class
	 * @param qualifier The qualifier for which the marshaller must be registered   
	 */
	public <S, T> Converter<S, T> findConverter(Class<S> source, Class<T> target, Class<? extends Annotation> qualifier) {
		return findConverter(new ConverterKey<S,T>(source, target, qualifier == null ? DefaultBinding.class : qualifier));
	}
	
	/**
	 * Resolve a Converter with the given input and output classes. Instances of the input class can be converted into 
	 * instances of the output class
	 * @param key The key to look up
	 */
	public <S, T> Converter<S, T> findConverter(ConverterKey<S,T> key) {
		
		 // We check once before attempting introspection so we avoid that if possible
		@SuppressWarnings("unchecked")
		Converter<S,T> converter = (Converter<S, T>) registeredConverters.get(key);
		if (converter != null) {
			return converter;
		}
		
		// Now try introspecting the relevant class
		registerAnnotatedClasses(key.getInputClass(), key.getOutputClass());

		@SuppressWarnings("unchecked")
		Converter<S,T> myConverter = (Converter<S, T>) registeredConverters.get(key);
		return myConverter;
	}

	/**
	 * Resolve an UnMarshaller with the given source and target class.
	 * The unmarshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param source The source (input) class
	 * @param target The target (output) class 
	 * @param qualifier The qualifier for which the unmarshaller must be registered 
	 */
	public <S, T> FromUnmarshaller<S, T> findUnmarshaller(Class<S> source, Class<T> target, Class<? extends Annotation> qualifier) {
		return findUnmarshaller(new ConverterKey<S,T>(source, target, qualifier == null ? DefaultBinding.class : qualifier));
	}
	
	/**
	 * Resolve an UnMarshaller with the given source and target class.
	 * The unmarshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param key The key to look up 
	 */
	public <S, T> FromUnmarshaller<S, T> findUnmarshaller(ConverterKey<S,T> key) {

		Converter<T,S> converter = findConverter(key.invert());
		
		if (converter == null) {
			return null;
		}
		
		if (FromUnmarshallerConverter.class.isAssignableFrom(converter.getClass())) {
			return ((FromUnmarshallerConverter<S, T>)converter).getUnmarshaller();
		} else {
			return new ConverterFromUnmarshaller<S, T>(converter);
		}
	}
	
	/**
	 * Return an iterable collection of ConverterKeys, one for each currently registered conversion
	 */
	public Iterable<ConverterKey<?,?>> getConverterEntries() {
		return registeredConverters.keySet();
	}

    /******************
     *                *
     * Helper Methods * 
     *                *
     ******************/

	/**
	 * This method assists in matching a given implementation class to its (programmer) defined scope.
	 * The method searches for an annotation on the class defined as a binding scope. If one is found 
	 * the implementation is matched to the scope. Note that the implementation scope can be overridden 
	 * using bindings.xml configuration
	 * @param implementation The class to examine
	 * @return The found scope annotation
	 */
    private <T> Class<? extends Annotation> matchImplementationToScope(Class<?> implementation) {

        for (Annotation next : implementation.getAnnotations()) {
            Class<? extends Annotation> nextType = next.annotationType();
            if (nextType.getAnnotation(BindingScope.class) != null) {
                return nextType;
            }
        }
        return null;
    }
	
	private <S, T> void attachForAnnotations(final Class<?> target) {

		// Only apply an annotation once
		synchronized(inspectedClasses) {
			if (inspectedClasses.contains(target)) {
				return;
			}
			inspectedClasses.add(target);
		}

		// NB Don't worry about superclasses - that happens elsewhere
		Set<ConverterKey<?,?>> previouslySeenKeys = new HashSet<ConverterKey<?,?>>();
		
		Map<ConverterKey<?,?>, Method> toMethods = new HashMap<ConverterKey<?,?>, Method>();
		Map<ConverterKey<?,?>, Method> fromMethods = new HashMap<ConverterKey<?,?>, Method>();
		Map<ConverterKey<?,?>, Constructor<?>> fromConstructors = new HashMap<ConverterKey<?,?>, Constructor<?>>();
		
		matchTo(target, previouslySeenKeys, toMethods);
		
		matchFrom(target, previouslySeenKeys, fromMethods, fromConstructors);
		
		// 2 Pass all the results into registerForMethods
		for (ConverterKey<?,?> next : previouslySeenKeys) {
			
			@SuppressWarnings("unchecked")
			ConverterKey<S,T> nextKey = (ConverterKey<S,T>)next;
			
			ConverterKey<?,?> inverse = next.invert();
			@SuppressWarnings("unchecked") Constructor<S> fromConstructor = (Constructor<S>) fromConstructors.get(inverse);
			
			registerForMethods(nextKey, toMethods.get(next), fromMethods.get(inverse), fromConstructor);
		}
	}

	private void matchTo(Class<?> target, Set<ConverterKey<?,?>> previouslySeenKeys, Map<ConverterKey<?,?>, Method> toMethods) {

		for (ConverterProvider nextConverter : converterProviders) {
			Map<ConverterKey<?,?>, Method> nextMethods = nextConverter.matchToMethods(target);
			for(ConverterKey<?,?> currentKey : nextMethods.keySet()) {
				if (previouslySeenKeys.contains(currentKey)) {
					throw new IllegalStateException("Method is resolved by two converters: " + currentKey.toString());
				}
				previouslySeenKeys.add(currentKey);
			}
			toMethods.putAll(nextMethods);
		}
	}
	
	private <T> void matchFrom(Class<T> target, Set<ConverterKey<?,?>> previouslySeenKeys, Map<ConverterKey<?,?>, Method> fromMethods, Map<ConverterKey<?,?>, Constructor<?>> fromConstructors) {

		for (ConverterProvider nextConverter : converterProviders) {
			Map<ConverterKey<?,?>, Method> nextMethods = nextConverter.matchFromMethods(target);
			for (ConverterKey<?,?> currentKey : nextMethods.keySet()) {
				if (previouslySeenKeys.contains(currentKey)) {
					throw new IllegalStateException("Method is resolved by two converters: " + currentKey.toString());
				}
				previouslySeenKeys.add(currentKey);
			}
			fromMethods.putAll(nextMethods);
			
			Map<ConverterKey<?,?>, Constructor<T>> nextConstructors = nextConverter.matchFromConstructors(target);
			for (ConverterKey<?,?> currentKey : nextConstructors.keySet()) {
				if (previouslySeenKeys.contains(currentKey)) {
					throw new IllegalStateException("ConverterKey is resolved by two converters: " + currentKey.toString());
				}
				previouslySeenKeys.add(currentKey);
			}
			fromConstructors.putAll(nextConstructors);
		}
	}
    
    private <I,O> void registerForMethods(ConverterKey<I,O> key, Method toMethod, Method fromMethod, Constructor<I> con) {
    
        if (toMethod != null) {
            
            MethodToMarshaller<I,O> toMarshaller = new MethodToMarshaller<I,O>(key.getInputClass(), key.getOutputClass(), toMethod);
            if (con != null) {
    
                ConstructorFromUnmarshaller<I,O> fromUnmarshaller = new ConstructorFromUnmarshaller<I,O>(con); 
                registerBinding(key.getInputClass(), key.getOutputClass(), new CompositeBinding<I,O>(toMarshaller, fromUnmarshaller), key.getQualifierAnnotation());               
            } else if (fromMethod != null) {
                
                MethodFromUnmarshaller<I,O> fromUnmarshaller = new MethodFromUnmarshaller<I,O>(key.getInputClass(), fromMethod);
                registerBinding(key.getInputClass(), key.getOutputClass(), new CompositeBinding<I,O>(toMarshaller, fromUnmarshaller), key.getQualifierAnnotation());
            } else {
                
            	registerMarshaller(key.getInputClass(), key.getOutputClass(), toMarshaller, key.getQualifierAnnotation());
            }
        } else {
            if (con != null) {
                
                registerUnmarshaller(key.getInputClass(), key.getOutputClass(), new ConstructorFromUnmarshaller<I,O>(con), key.getQualifierAnnotation());
            } else if (fromMethod != null) {
                
                registerUnmarshaller(key.getInputClass(), key.getOutputClass(), new MethodFromUnmarshaller<I,O>(key.getInputClass(), fromMethod), key.getQualifierAnnotation());
            }
        }
    }

    /******************
     *                *
     * String Binder  * 
     *                *
     ******************/
    
	public <T> T convertFromString(Class<T> output, String object) {
		return convertTo(String.class, output, object);
	}

	public <T> T convertFromString(Class<T> output, String object,
			Class<? extends Annotation> qualifier) {
		return convertTo(String.class, output, object, qualifier);
	}

	public String convertToString(Object object) {
		return convertTo(String.class, object);
	}

	public String convertToString(Object object,
			Class<? extends Annotation> qualifier) {
		return convertTo(String.class, object, qualifier);
	}

	public <S> String convertToString(Class<S> input, Object object) {
		return convertTo(input, String.class, object);
	}

	public <S> String convertToString(Class<S> input, Object object,
			Class<? extends Annotation> qualifier) {
		return convertTo(input, String.class, object, qualifier);
	}
}
