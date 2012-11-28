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
package org.jadira.bindings.core.spi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

import org.jadira.bindings.core.binder.ConverterKey;

/**
 * Defines an extension point to the binding framework for adding new binding capabilities
 */
public interface ConverterProvider {
    
    /**
     * Match methods that can perform a to operation
     * @param cls The class being inspected
     * @param previouslySeenKeys Keys that have already been matched to a method for this class
     * @return The methods
     */
	<I,O> Map<ConverterKey<?, ?>, Method> matchToMethods(Class<?> cls);
 
    /**
     * Match constructors that can perform a from operation
     * @param cls The class being inspected
     * @return The constructors
     */
    <I,O> Map<ConverterKey<?, ?>, Constructor<O>> matchFromConstructors(Class<O> cls);
    
    /**
     * Match methods that can perform a from operation
     * @param cls The class being inspected
     * @return The methods
     */
    <I,O> Map<ConverterKey<?, ?>, Method> matchFromMethods(Class<?> cls);
}
