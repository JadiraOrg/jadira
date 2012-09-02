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
 * A marshaller that wraps an unmarshaller to convert the unmarshaller for type T into a marshaller for type S
 * @param <S> source type
 * @param <T> output type for the marshaller
 */
public class UnmarshallerToMarshaller<S, T> implements ToMarshaller<S, T> {

	private FromUnmarshaller<T, S> unmarshaller;

	/**
	 * Create a new instance of a {@link ToMarshaller} wrapping the given {@link FromUnmarshaller}
	 * @param marshaller The FromUnmarshaller to wrap
	 */
	public UnmarshallerToMarshaller(FromUnmarshaller<T, S> unmarshaller) {
		this.unmarshaller = unmarshaller;
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public T marshal(S object) {
		return unmarshaller.unmarshal(object);
	}
	
	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public Class<S> getBoundClass() {
		return unmarshaller.getTargetClass();
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public Class<T> getTargetClass() {
		return unmarshaller.getBoundClass();
	}
}
