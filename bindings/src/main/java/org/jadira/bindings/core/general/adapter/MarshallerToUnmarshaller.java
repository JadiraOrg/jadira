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
package org.jadira.bindings.core.general.adapter;

import org.jadira.bindings.core.api.FromUnmarshaller;
import org.jadira.bindings.core.api.ToMarshaller;

/**
 * An unmarshaller that wraps a marshaller to convert the marshaller for type T into an unmarshaller for type S
 * @param <S> source type
 * @param <T> output type for the marshaller
 */
public class MarshallerToUnmarshaller<S, T> implements FromUnmarshaller<S, T> {

	private ToMarshaller<T, S> marshaller;

	/**
	 * Create a new instance of a {@link FromUnmarshaller} wrapping the given {@link ToMarshaller}
	 * @param marshaller The ToMarshaller to wrap
	 */
	public MarshallerToUnmarshaller(ToMarshaller<T, S> marshaller) {
		this.marshaller = marshaller;
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public S unmarshal(T object) {
		return marshaller.marshal(object);
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public Class<S> getBoundClass() {
		return marshaller.getTargetClass();
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public Class<T> getTargetClass() {
		return marshaller.getBoundClass();
	}
}
