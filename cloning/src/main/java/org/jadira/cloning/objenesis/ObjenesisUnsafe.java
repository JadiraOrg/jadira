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
package org.jadira.cloning.objenesis;

import org.objenesis.ObjenesisBase;

/**
 * Objenesis implementation using the {@link UnsafeInstantiatorStrategy}. This class provides the
 * Objenesis API a strategy for using sun.misc.Unsafe as a class construction technique.
 * 
 * You may use this class independently of the cloning framework.
 */
public class ObjenesisUnsafe extends ObjenesisBase {

	/**
	 * Default constructor using the {@link UnsafeInstantiatorStrategy}
	 */
	public ObjenesisUnsafe() {
		super(new UnsafeInstantiatorStrategy());
	}

	/**
	 * Instance using the {@link UnsafeInstantiatorStrategy} with or without caching of
	 * {@link org.objenesis.instantiator.ObjectInstantiator}.
	 * 
	 * @param useCache If {@link org.objenesis.instantiator.ObjectInstantiator}s should be cached
	 */
	public ObjenesisUnsafe(boolean useCache) {
		super(new UnsafeInstantiatorStrategy(), useCache);
	}
}
