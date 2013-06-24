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

import org.jadira.cloning.BasicCloner;
import org.jadira.cloning.MinimalCloner;

/**
 * A Cloner represents the capability for Cloning. Its the interface via which users of the library
 * in general will interact with it.
 * @see {@link BasicCloner} and {@link MinimalCloner} 
 */
public interface Cloner {

	/**
	 * Clones the supplied object
	 * 
	 * @param obj The object
	 * @return A clone of the object
	 */
	<T> T clone(T obj);

	/**
	 * This method is included simply so that cold starts can be avoided. The method performs any
	 * initialisation needed in order to be able to subsequently process the class. If it is not
	 * called ahead of time the initialisation will be performed as part of the cloning operation.
	 * You only need to use this method in situations where a cold start is unacceptable
	 * 
	 * @param classes Classes to perform initialisation for.
	 */
	public void initialiseFor(Class<?> classes);
}
