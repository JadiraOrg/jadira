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

import org.jadira.bindings.core.api.Binding;
import org.jadira.bindings.core.api.Converter;
import org.jadira.bindings.core.api.FromUnmarshaller;
import org.jadira.bindings.core.api.ToMarshaller;

public interface SearchableBinder {

	/**
	 * Resolve a Binding with the given source and target class.
	 * A binding unifies a marshaller and an unmarshaller and both must be available to resolve a binding.
	 * 
	 * The source class is considered the owning class of the binding. The source can be marshalled
	 * into the target class. Similarly, the target can be unmarshalled to produce an instance of the source type.
	 * @param key The converter key
	 */
    <S, T> Binding<S, T> findBinding(ConverterKey<S,T> key);
    
	/**
	 * Resolve a Marshaller with the given source and target class.
	 * The marshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param key The converter key 
	 */
    <S, T> ToMarshaller<S, T> findMarshaller(ConverterKey<S,T> key);
    
	/**
	 * Resolve a Converter with the given input and output classes. Instances of the input class can be converted into 
	 * instances of the output class
	 * @param key The converter key  
	 */
    <S, T> Converter<S, T> findConverter(ConverterKey<S,T> key);
    
	/**
	 * Resolve an UnMarshaller with the given source and target class.
	 * The unmarshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param key The converter key 
	 */
    <S, T> FromUnmarshaller<S, T> findUnmarshaller(ConverterKey<S,T> key);

	
	/**
	 * Resolve a Binding with the given source and target class.
	 * A binding unifies a marshaller and an unmarshaller and both must be available to resolve a binding.
	 * 
	 * The source class is considered the owning class of the binding. The source can be marshalled
	 * into the target class. Similarly, the target can be unmarshalled to produce an instance of the source type.
	 * @param source The source (owning) class
	 * @param target The target (foreign) class
	 */
    <S, T> Binding<S, T> findBinding(final Class<S> source, final Class<T> target);
    
	/**
	 * Resolve a Marshaller with the given source and target class.
	 * The marshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param source The source (input) class
	 * @param target The target (output) class 
	 */
    <S, T> ToMarshaller<S, T> findMarshaller(final Class<S> source, final Class<T> target);
    
	/**
	 * Resolve a Converter with the given input and output classes. Instances of the input class can be converted into 
	 * instances of the output class
	 * @param input The input class
	 * @param output The output class  
	 */
    <S, T> Converter<S, T> findConverter(final Class<S> source, final Class<T> target);
    
	/**
	 * Resolve an UnMarshaller with the given source and target class.
	 * The unmarshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param source The source (input) class
	 * @param target The target (output) class 
	 */
    <S, T> FromUnmarshaller<S, T> findUnmarshaller(final Class<S> source, final Class<T> target);
    
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
    <S, T> Binding<S, T> findBinding(final Class<S> source, final Class<T> target, final Class<? extends Annotation> qualifier);
    
	/**
	 * Resolve a Marshaller with the given source and target class.
	 * The marshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param source The source (input) class
	 * @param target The target (output) class
	 * @param qualifier The qualifier for which the marshaller must be registered 
	 */
    <S, T> ToMarshaller<S, T> findMarshaller(final Class<S> source, final Class<T> target, final Class<? extends Annotation> qualifier);
    
	/**
	 * Resolve a Converter with the given input and output classes. Instances of the input class can be converted into 
	 * instances of the output class
	 * @param input The input class
	 * @param output The output class
	 * @param qualifier The qualifier for which the marshaller must be registered   
	 */
    <S, T> Converter<S, T> findConverter(final Class<S> source, final Class<T> target, final Class<? extends Annotation> qualifier);
    
	/**
	 * Resolve an UnMarshaller with the given source and target class.
	 * The unmarshaller is used as follows: Instances of the source can be marshalled into the target class.
	 * @param source The source (input) class
	 * @param target The target (output) class 
	 * @param qualifier The qualifier for which the unmarshaller must be registered 
	 */
    <S, T> FromUnmarshaller<S, T> findUnmarshaller(final Class<S> source, final Class<T> target, final Class<? extends Annotation> qualifier);
}
