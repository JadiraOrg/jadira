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
import org.jadira.bindings.core.api.ToMarshaller;

/**
 * A converter that wraps a marshaller to convert the marshaller for type S into a converter for type S to type S
 * @param <S> input type
 * @param <T> output type for the marshaller
 */
public class ToMarshallerConverter<S,T> implements Converter<S,T> {

	private ToMarshaller<S,T> marshaller;

	/**
	 * Create a new instance of a {@link Converter} wrapping the given {@link ToMarshaller}
	 * @param unmarshaller The ToMarshaller to wrap
	 */
	public ToMarshallerConverter(ToMarshaller<S,T> marshaller) {
		this.marshaller = marshaller;
	}
	
	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public T convert(S inputObject) {
		return marshaller.marshal(inputObject);
	}

	/**
	 * Get the wrapped marshaller
	 * @return the wrapped marshaller
	 */
	public ToMarshaller<S, T> getMarshaller() {
		return marshaller;
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public Class<S> getInputClass() {
		return marshaller.getBoundClass();
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public Class<T> getOutputClass() {
		return marshaller.getTargetClass();
	}
}	