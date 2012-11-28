/*
 *  Copyright 2011 Christopher Pheby
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

import java.lang.annotation.Annotation;
import java.net.URL;

import org.jadira.bindings.core.api.Binding;
import org.jadira.bindings.core.api.Converter;
import org.jadira.bindings.core.api.FromUnmarshaller;
import org.jadira.bindings.core.api.ToMarshaller;

public interface RegisterableBinder {

    /**
     * Register the configuration file (bindings.xml) at the given URL 
     */    
    void registerConfiguration(URL nextLocation);
	
	/**
	 * Register a Binding with the given source and target class.
	 * A binding unifies a marshaller and an unmarshaller and both must be available to resolve a binding.
	 * 
	 * The source class is considered the owning class of the binding. The source can be marshalled
	 * into the target class. Similarly, the target can be unmarshalled to produce an instance of the source type.
	 * @param key The converter key
	 * @param converter The binding to be registered
	 */
    <S, T> void registerBinding(ConverterKey<S,T> key, Binding<S, T> converter);
    
	/**
	 * Register an UnMarshaller with the given source and target class.
	 * The unmarshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param key The converter key
	 * @param converter The FromUnmarshaller to be registered  
	 */
	<S, T> void registerUnmarshaller(ConverterKey<S,T> key, FromUnmarshaller<S, T> converter);
	
	/**
	 * Register a Marshaller with the given source and target class.
	 * The marshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param key The converter key
	 * @param converter The ToMarshaller to be registered 
	 */
	<S, T> void registerMarshaller(ConverterKey<S,T> key, ToMarshaller<S, T> converter);
    
	/**
	 * Register a Converter with the given input and output classes. Instances of the input class can be converted into 
	 * instances of the output class
	 * @param key The converter key
	 * @param converter The Converter to be registered   
	 */
    <S, T> void registerConverter(ConverterKey<S,T> key, Converter<S, T> converter);
	
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
    <S, T> void registerBinding(final Class<S> sourceClass, Class<T> targetClass, Binding<S, T> converter);

	/**
	 * Register an UnMarshaller with the given source and target class.
	 * The unmarshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param source The source (input) class
	 * @param target The target (output) class
	 * @param converter The FromUnmarshaller to be registered  
	 */
	<S, T> void registerUnmarshaller(Class<S> sourceClass, Class<T> targetClass, FromUnmarshaller<S, T> converter);
	
	/**
	 * Register a Marshaller with the given source and target class.
	 * The marshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param source The source (input) class
	 * @param target The target (output) class
	 * @param converter The ToMarshaller to be registered
	 */
	<S, T> void registerMarshaller(Class<S> sourceClass, Class<T> targetClass, ToMarshaller<S, T> converter);
    
	/**
	 * Register a Converter with the given input and output classes. Instances of the input class can be converted into 
	 * instances of the output class
	 * @param input The input class
	 * @param output The output class
	 * @param converter The Converter to be registered   
	 */
    <S, T> void registerConverter(final Class<S> sourceClass, Class<T> targetClass, Converter<S, T> converter);

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
    <S, T> void registerBinding(final Class<S> sourceClass, Class<T> targetClass, Binding<S, T> converter, Class<? extends Annotation> qualifier);
    
	/**
	 * Register an UnMarshaller with the given source and target class.
	 * The unmarshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param source The source (input) class
	 * @param target The target (output) class
	 * @param converter The FromUnmarshaller to be registered
	 * @param qualifier The qualifier for which the unmarshaller must be registered  
	 */
	<S, T> void registerUnmarshaller(Class<S> sourceClass, Class<T> targetClass, FromUnmarshaller<S, T> converter, Class<? extends Annotation> qualifier);
	
	/**
	 * Register a Marshaller with the given source and target class.
	 * The marshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param source The source (input) class
	 * @param target The target (output) class
	 * @param converter The ToMarshaller to be registered
	 * @param qualifier The qualifier for which the marshaller must be registered 
	 */
	<S, T> void registerMarshaller(Class<S> sourceClass, Class<T> targetClass, ToMarshaller<S, T> converter, Class<? extends Annotation> qualifier);
    
	/**
	 * Register a Converter with the given input and output classes. Instances of the input class can be converted into 
	 * instances of the output class
	 * @param input The input class
	 * @param output The output class
	 * @param converter The Converter to be registered
	 * @param qualifier The qualifier for which the converter must be registered   
	 */
    <S, T> void registerConverter(final Class<S> sourceClass, Class<T> targetClass, Converter<S, T> converter, Class<? extends Annotation> qualifier);
    
	/**
	 * Inspect each of the supplied classes, processing any of the annotated methods found
	 * @param classesToInspect
	 */
	void registerAnnotatedClasses(Class<?>... classesToInspect);
	
    
	/**
	 * Return an iterable collection of ConverterKeys, one for each currently registered conversion
	 */
	Iterable<ConverterKey<?, ?>> getConverterEntries();
}
