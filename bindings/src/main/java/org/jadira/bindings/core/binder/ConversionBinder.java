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

public interface ConversionBinder {

	/**
	 * Convert an object to the given target class
	 * This method infers the source type for the conversion from the runtime type of object.
	 * @param output The target class to convert the object to
	 * @param object The object to be converted
	 */
	<S,T> T convertTo(Class<T> output, Object object);
	
	/**
	 * Convert an object to the given target class
	 * This method infers the source type for the conversion from the runtime type of object.
	 * @param output The target class to convert the object to
	 * @param object The object to be converted
	 * @param qualifier The qualifier for which the binding must be registered
	 */
	<S,T> T convertTo(Class<T> output, Object object, Class<? extends Annotation> qualifier);
	
	/**
	 * TODO Add find methods equating to convertTo
	 */
    
	/**
	 * Convert an object which is an instance of source class to the given target class
	 * @param input The class of the object to be converted
	 * @param output The target class to convert the object to
	 * @param object The object to be converted
	 */
	<S, T> T convertTo(Class<S> input, Class<T> output, Object object);

	/**
	 * Convert an object which is an instance of source class to the given target class
	 * @param input The class of the object to be converted
	 * @param output The target class to convert the object to
	 * @param object The object to be converted
	 * @param qualifier Match the converter with the given qualifier
	 */
	<S, T> T convertTo(Class<S> input, Class<T> output, Object object, Class<? extends Annotation> qualifier);

	/**
	 * Convert an object which is an instance of source class to the given target class
	 * @param key The converter key
	 * @param object The object to be converted
	 */
	<S, T> T convertTo(ConverterKey<S,T> key, Object object);
}
