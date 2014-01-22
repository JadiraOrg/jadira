/*
 *  Copyright 2013 Christopher Pheby
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
package org.jadira.reflection.access.api;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Defines a mechanism for accessing the capabilities of a class
 * @param <C> The class to be access
 */
public interface ClassAccess<C> {

	/**
	 * Create a new, uninitialised instance of the given class (if supported). Depending on
	 * strategy, this may or may not initialise instance fields, and may or may not invoke
	 * constructors.
	 * @return The new instance of the type, C.
	 */
	C newInstance();

	/**
	 * Get the Class being accessed
	 * @return The class
	 */
	Class<C> getType();
	
	/**
	 * Gets a field accessor for each field in the class
	 * @return Array of FieldAccess
	 */
	FieldAccess<C>[] getFieldAccessors();
	
	/**
	 * Get a field accessor for the given field
	 * @param f The Field to be accessed
	 * @return The matching FieldAccess
	 */
	FieldAccess<C> getFieldAccess(Field f);
	
	/**
	 * Gets a method accessor for each method in the class
	 * @return Array of MethodAccess
	 */
	MethodAccess<C>[] getMethodAccessors();
	
	/**
	 * Get a method accessor for the given method
	 * @param m The Method to be accessed
	 * @return The matching MethodAccess
	 */
	MethodAccess<C> getMethodAccess(Method f);
}
