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
package org.jadira.bindings.core.general.binding;

import org.jadira.bindings.core.api.Binding;
import org.jadira.bindings.core.api.FromUnmarshaller;
import org.jadira.bindings.core.api.ToMarshaller;

/**
 * Binding that supports a to contract, and from contract
 * @param <S> Source type for the conversion
 * @param <T> Target type
 */
public class CompositeBinding<S, T> implements Binding<S, T> {

    private final ToMarshaller<S, T> marshal;
    
    private final FromUnmarshaller<S, T> unmarshal;
    
	/**
	 * Create a new instance of a {@link Binding} wrapping the given {@link ToMarshaller} and {@link FromUnmarshaller} instances
	 * @param marshal The ToMarshaller to wrap
	 * @param unmarshal The FromUnmarshaller to wrap
	 */
    public CompositeBinding(ToMarshaller<S, T> marshal, FromUnmarshaller<S, T> unmarshal) {
        
        if (unmarshal == null) {
            throw new IllegalStateException("Unmarshaller may not be null");
        }
        if (marshal == null) {
            throw new IllegalStateException("Marshaller may not be null");
        }
        
        this.unmarshal = unmarshal;
        this.marshal = marshal;
    }

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
    public T marshal(S object) {
        return marshal.marshal(object);
    }

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
    public S unmarshal(T inputString) {
        return unmarshal.unmarshal(inputString);
    }

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public Class<S> getBoundClass() {
		return unmarshal.getBoundClass();
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public Class<T> getTargetClass() {
		return unmarshal.getTargetClass();
	}    
}