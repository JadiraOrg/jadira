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
package org.jadira.usertype.spi.utils.reflection;

/**
 * Utility methods related to {@link ClassLoader} resolution and the like
 */
public final class ClassLoaderUtils {

    private ClassLoaderUtils() {
    }
    
    /**
     * Gets an appropriate {@link ClassLoader} for the current thread
     * @return
     */
    public static ClassLoader getClassLoader() {

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        if (cl == null) {
            cl = ClassLoaderUtils.class.getClassLoader();
        }

        return cl;
    }
    
    /**
     * Attempts to instantiate the named class, using the context ClassLoader for the current thread,
     * or Class.forName if this does not work
     * @param name The class name
     * @return The Class<?> instance
     * @throws ClassNotFoundException If the class cannot be found
     */
	public static Class<?> classForName(String name) throws ClassNotFoundException {
		
		ClassLoader cl = getClassLoader();
		try {
			if (cl != null) {
				return cl.loadClass(name);
			}
		} catch (ClassNotFoundException e) {
			// Ignore and try using Class.forName()
		}
		return Class.forName(name);
	}
}
