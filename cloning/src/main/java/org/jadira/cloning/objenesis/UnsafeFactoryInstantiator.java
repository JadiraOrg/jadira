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

import org.jadira.cloning.unsafe.UnsafeOperations;
import org.objenesis.ObjenesisException;
import org.objenesis.instantiator.ObjectInstantiator;

/**
 * Provides the Objenesis API an instantiator implementation that can use Unsafe You may use this
 * class independently of the cloning framework.
 */
public class UnsafeFactoryInstantiator implements ObjectInstantiator {

	private Class<?> type;

	private static final UnsafeOperations UNSAFE_OPERATIONS = UnsafeOperations.getUnsafeOperations();

	public UnsafeFactoryInstantiator(Class<?> type) {
		this.type = type;
	}

	public Object newInstance() {
		try {
			return UNSAFE_OPERATIONS.allocateInstance(type);
		} catch (IllegalStateException e) {
			throw new ObjenesisException(e.getCause());
		}
	}
}
