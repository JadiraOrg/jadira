/*
 *  Copyright 2011 Chris Pheby
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
package org.jadira.bindings.core.api;

/**
 * Interface for explicitly defining conversions from I to a destination
 * object type O.
 * <p>
 * We utilise super-type tokens to avoid having to include the class type on the
 * defined method
 * <p>
 * Implementations of this class should be idempotent (behaviour should not vary
 * between calls) and thread-safe.
 * @param <I> Input type for the conversion
 * @param <O> Output type
 */
public interface Converter<I, O> {

    /**
     * Converts from the given I into the specified object
     * @param inputObject I to transform, not null
     * @return transformed instance of O (this can be null if appropriate)
     */
    O convert(I inputObject);
    
    /**
     * Obtain the bound class
     * @return Class<I>
     */
    Class<I> getInputClass();
    
    /**
     * Obtain the bound class
     * @return Class<O>
     */
    Class<O> getOutputClass();
}
