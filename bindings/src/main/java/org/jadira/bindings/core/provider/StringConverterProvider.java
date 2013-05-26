/*
 *  Copyright 2010, 2011 Chris Pheby
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
package org.jadira.bindings.core.provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.jadira.bindings.core.annotation.typesafe.FromString;
import org.jadira.bindings.core.annotation.typesafe.ToString;
import org.jadira.bindings.core.spi.ConverterProvider;

/**
 * Provider that is aware of {@link org.jadira.bindings.core.annotation.typesafe.ToString} and {@link org.jadira.bindings.core.annotation.typesafe.FromString} annotations
 */
public class StringConverterProvider extends AbstractAnnotationMatchingConverterProvider<ToString, FromString> implements ConverterProvider {
	
	/**
	 * Subclasses can override this template method with their own matching strategy
	 * @param method The method to be determined
	 * @return True if match
	 */
	protected boolean isToMatch(Method method) {
		return String.class.equals(method.getReturnType());
	}

	/**
	 * Subclasses can override this template method with their own matching strategy
	 * @param method The constructor to be determined
	 * @return True if match
	 */
	protected boolean isFromMatch(Constructor<?> constructor) {
		return String.class.equals(constructor.getParameterTypes()[0]);
	}

	/**
	 * Subclasses can override this template method with their own matching strategy
	 * @param method The method to be determined
	 * @return True if match
	 */
	protected boolean isFromMatch(Method method) {
		return String.class.equals(method.getParameterTypes()[0]);
	}
}
