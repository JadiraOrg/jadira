/*
 *  Copyright 2013 Chris Pheby
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
package org.jadira.cloning.api;

import java.util.IdentityHashMap;

/**
 * A CloneImplementor represents an atomic cloning capability. The interface is implemented by
 * classes that can clone a specific type, as well as by {@link CloneStrategy} and
 * {@link CloneDriver} implementations. In general users do not work with the methods on this class,
 * using {@link Cloner} instead
 */
public interface CloneImplementor {

	/**
	 * Create a new, uninitialised instance of the given class (if supported). Depending on
	 * strategy, this may or may not initialise instance fields, and may or may not invoke
	 * constructors.
	 * 
	 * @param c Type to create instance of
	 * @return The new instance
	 */
	<T> T newInstance(final Class<T> c);

	/**
	 * True if this implementor can clone the given class
	 * 
	 * @param clazz The class
	 * @return True if cloneable, false otherwise
	 */
	boolean canClone(Class<?> clazz);

	/**
	 * Performs a clone.
	 * 
	 * @param obj Object to clone
	 * @param context The CloneDriver that initiated the request
	 * @param referencesToReuse Any references for objects already cloned to ensure reference
	 *            identity is preserved
	 * @return The cloned object
	 */
	<T> T clone(T obj, CloneDriver context, IdentityHashMap<Object, Object> referencesToReuse);
}
