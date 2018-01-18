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
package org.jadira.reflection.cloning.objenesis;

import org.jadira.reflection.core.platform.FeatureDetection;
import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.StdInstantiatorStrategy;

/**
 * Provides the Objenesis API a strategy for using sun.misc.Unsafe as a class construction
 * technique. If unsafe is unavailable, delegates to the standard built in strategy.
 * You may use this class independently of the cloning framework.
 */
public class UnsafeInstantiatorStrategy extends StdInstantiatorStrategy {
	/**
	 * Return an {@link ObjectInstantiator} allowing to create instance without any constructor
	 * being called.
	 * @param type Class to instantiate
	 * @return The ObjectInstantiator for the class
	 */
	public <T> ObjectInstantiator<T> newInstantiatorOf(Class<T> type) {

		if (FeatureDetection.hasUnsafe()) {
			return new UnsafeFactoryInstantiator<T>(type);
		}
		return super.newInstantiatorOf(type);
	}
}
