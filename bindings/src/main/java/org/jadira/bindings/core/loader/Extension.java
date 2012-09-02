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

/**
 * Represents a provider in the bindings configuration file
 */
public class Extension<T> {

	/**
	 * The provider's type
	 */
    private Class<T> extensionClass;
    /**
     * The implementation of the provider which will be registered
     */
	private Class<? extends T> implementationClass;

    /**
     * Creates a new instance
     * @param providerClass The provider's class
     * @param implementationClass The implementation class
     */
    public Extension(Class<T> extensionClass, Class<? extends T> implementationClass) {
        this.extensionClass = extensionClass;
        this.implementationClass = implementationClass;
    }

    /**
     * @return The provider's class
     */
    public Class<T> getExtensionClass() {
        return extensionClass;
    }    
    
    public Class<? extends T> getImplementationClass() {
    	return implementationClass;
    }
}
