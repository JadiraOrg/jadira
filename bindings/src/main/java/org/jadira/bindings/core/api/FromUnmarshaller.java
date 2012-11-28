/*
 *  Copyright 2010, 2011 Sousan Rassoul
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
 * Interface for explicitly defining conversions from T to a target
 * object type S.
 * <p>
 * Implementations of this class should be idempotent (behaviour should not vary
 * between calls) and thread-safe.
 * @param <S> Source type for the conversion
 * @param <T> Target type
 */
public interface FromUnmarshaller<S, T> {

    /**
     * Converts from the given String into the specified object
     * @param inputString String to transform, not null
     * @return transformed instance of T (this can be null if appropriate)
     */
    S unmarshal(T inputObject);
    
    /**
     * Obtain the bound class
     * @return Class<S>
     */
    Class<S> getBoundClass();
    
    /**
     * Obtain the bound class
     * @return Class<T>
     */
    Class<T> getTargetClass();
}
