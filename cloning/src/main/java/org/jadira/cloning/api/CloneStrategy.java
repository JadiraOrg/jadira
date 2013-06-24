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
package org.jadira.cloning.api;

/**
 * A specific kind of {@link CloneImplementor} that is intended for plugging into a {@link Cloner}.
 * CloneStrategies must be capable of working with all available classes, whereas standard
 * {@link CloneImplementor}s are typically written for cloning a specific class type.
 */
public interface CloneStrategy extends CloneImplementor {

	/**
	 * 
	 * @param classes
	 */
	void initialiseFor(Class<?>... classes);
}
