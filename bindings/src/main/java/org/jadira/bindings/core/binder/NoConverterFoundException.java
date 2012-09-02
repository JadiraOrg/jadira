/*
 *  Copyright 2011 Christopher Pheby
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
package org.jadira.bindings.core.binder;

/**
 * Runtime exception thrown when a {@link ConverterKey} cannot be resolved
 */
public class NoConverterFoundException extends RuntimeException {

	private static final long serialVersionUID = -8269600136904935415L;

	private ConverterKey<?,?> converterKey;

	/**
	 * Creates a new instance for the given {@link ConverterKey}
	 * @param converterKey
	 */
	public NoConverterFoundException(ConverterKey<?,?> converterKey) {
		super("No converter found for: " + converterKey.toString());
		this.converterKey = converterKey;
	}
	
	/**
	 * Get the {@link ConverterKey} used to resolve unsuccessfully
	 * @return {@link ConverterKey}
	 */
	public ConverterKey<?,?> getConverterKey() {
		return converterKey;
	}
}
