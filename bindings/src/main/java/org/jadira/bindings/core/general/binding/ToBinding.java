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
import org.jadira.bindings.core.api.ToMarshaller;

/**
 * Binding that supports a to contract
 * @param <T> the type of the class for the conversion
 */
public class ToBinding<S, T> implements Binding<S, T> {

    private final ToMarshaller<S, T> marshal;
    
	/**
	 * Create a new instance of a {@link Binding} wrapping the given {@link ToMarshaller}
	 * @param marshal The ToMarshaller to wrap
	 */
    public ToBinding(ToMarshaller<S, T> marshal) {
        
        if (marshal == null) {
            throw new IllegalStateException("Marshaller may not be null");
        }
        
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
        throw new UnsupportedOperationException("ToBinding does not support unmarshalling");
    }

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public Class<S> getBoundClass() {
		return marshal.getBoundClass();
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public Class<T> getTargetClass() {
		return marshal.getTargetClass();
	}
}