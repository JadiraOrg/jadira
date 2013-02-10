/*
 *  Copyright 2012 Chris Pheby
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
package org.jadira.scanner.core.api;

/**
 * Instances of classes that implement this interface are used to filter resolution.
 */
public interface Filter<T> {
	
    Class<T> targetType();
	
	/**
     * Tests if a specified item should be included.
     * @param filterParameter the item to filter on
     * @return <code>true</code> if and only if the item should be included in the list; <code>false</code> otherwise.
     */
    boolean accept(T filterParameter);
}