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

/**
 * Binding that supports a from contract but not to
 * @param <S> Source type for the conversion
 * @param <T> Target type
 */
public class FromBinding<S, T> implements Binding<S, T> {

    private final FromUnmarshaller<S, T> unmarshal;
    
	/**
	 * Create a new instance of a {@link Binding} wrapping the given {@link FromUnmarshaller} instance
	 * @param unmarshal The FromUnmarshaller to wrap
	 */
    public FromBinding(FromUnmarshaller<S, T> unmarshal) {
        
        if (unmarshal == null) {
            throw new IllegalStateException("Unmarshaller may not be null");
        }
        
        this.unmarshal = unmarshal;
    }

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
    public T marshal(S object) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("FromBinding does not support marshalling");
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