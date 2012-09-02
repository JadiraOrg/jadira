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

public interface StringBinder {

	/**
	 * Convert a String to the given target class
	 * @param output The target class to convert the object to
	 * @param object The String to be converted
	 */
	<T> T convertFromString(Class<T> output, String object);
	
	/**
	 * Convert a String to the given target class
	 * @param output The target class to convert the object to
	 * @param object The String to be converted
	 * @param qualifier The qualifier for which the binding must be registered
	 */
	<T> T convertFromString(Class<T> output, String object, Class<? extends Annotation> qualifier);    

	/**
	 * Convert an object to String
	 * This method infers the source type for the conversion from the runtime type of object.
	 * @param object The object to be converted
	 */
	String convertToString(Object object);
	
	/**
	 * Convert an object to String
	 * This method infers the source type for the conversion from the runtime type of object.
	 * @param object The object to be converted
	 * @param qualifier The qualifier for which the binding must be registered
	 */
	String convertToString(Object object, Class<? extends Annotation> qualifier);
	
	/**
	 * Convert an object which is an instance of source class to String
	 * @param input The class of the object to be converted
	 * @param output The target class to convert the object to
	 * @param object The object to be converted
	 */
	<S> String convertToString(Class<S> input, Object object);

	/**
	 * Convert an object which is an instance of source class to String
	 * @param input The class of the object to be converted
	 * @param object The object to be converted
	 * @param qualifier Match the converter with the given qualifier
	 */
	<S> String convertToString(Class<S> input, Object object, Class<? extends Annotation> qualifier);
}
