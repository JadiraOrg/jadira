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
package org.jadira.bindings.core.general.binding;

import org.jadira.bindings.core.api.Binding;
import org.jadira.bindings.core.api.FromUnmarshaller;
import org.jadira.bindings.core.api.ToMarshaller;

/**
 * Binding that supports a to contract, and from contract. This binding reverses the interpretation
 * of the binding it wraps so that the source and target relationships are reversed.
 * @param <S> Source type for the conversion
 * @param <T> Target type
 */
public class InverseCompositeBinding<S, T> implements Binding<S, T> {

    private final ToMarshaller<T, S> marshal;
    
    private final FromUnmarshaller<T, S> unmarshal;
    
    /**
     * Create a binding that reverses the interpretation of the supplied binding
     * @param binding The binding to reverse
     */
    public InverseCompositeBinding(Binding<T, S> binding) {
        
        if (binding == null) {
            throw new IllegalStateException("Binding may not be null");
        }
        
        this.unmarshal = binding;
        this.marshal = binding;
    }    

    /**
     * Create a binding that reverses the interpretation of the supplied Marshaller and Unmarshaller
     * @param marshal The Marshaller to be interpreted as an Unmarshaller
     * @param unmarshal The Unmarshaller to be interpreted as an Marshaller
     */
    public InverseCompositeBinding(ToMarshaller<T, S> marshal, FromUnmarshaller<T, S> unmarshal) {
        
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
    	return unmarshal.unmarshal(object);
    }

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
    public S unmarshal(T input) {
    	return marshal.marshal(input);
    }

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public Class<S> getBoundClass() {
		return marshal.getTargetClass();
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public Class<T> getTargetClass() {
		return marshal.getBoundClass();
	}
}