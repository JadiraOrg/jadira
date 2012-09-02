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
package org.jadira.bindings.core.general.converter;

import org.jadira.bindings.core.api.Converter;
import org.jadira.bindings.core.api.FromUnmarshaller;

/**
 * A converter that wraps an unmarshaller to convert the unmarshaller for type S into a converter for type T to type S
 * @param <S> input type
 * @param <T> output type for the marshaller
 */
public class FromUnmarshallerConverter<S,T> implements Converter<T,S> {

	private FromUnmarshaller<S,T> unmarshaller;

	/**
	 * Create a new instance of a {@link Converter} wrapping the given {@link FromUnmarshaller}
	 * @param unmarshaller The FromUnmarshaller to wrap
	 */
	public FromUnmarshallerConverter(FromUnmarshaller<S,T> unmarshaller) {
		this.unmarshaller = unmarshaller;
	}
	
	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public S convert(T inputObject) {
		return unmarshaller.unmarshal(inputObject);
	}
	
	/**
	 * Get the wrapped unmarshaller
	 * @return the wrapped unmarshaller
	 */
	public FromUnmarshaller<S,T> getUnmarshaller() {
		return unmarshaller;
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public Class<T> getInputClass() {
		return unmarshaller.getTargetClass();
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public Class<S> getOutputClass() {
		return unmarshaller.getBoundClass();
	}
}
