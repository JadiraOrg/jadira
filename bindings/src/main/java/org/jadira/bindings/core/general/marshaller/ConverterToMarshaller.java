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
package org.jadira.bindings.core.general.marshaller;

import org.jadira.bindings.core.api.Converter;
import org.jadira.bindings.core.api.ToMarshaller;

/**
 * Base class providing capability to perform marshalling of source object type
 * to target. This class uses reflection.
 * <p>
 * The marshal method must either
 * </p>
 * <p>
 * a) be instance scoped and defined as part of class S. It must accept no
 * parameters and return a type of T. For example:
 * </p>
 * <p>
 * {@code public String marshal()}
 * </p>
 * <p>
 * b) be statically scoped. It must accept a single parameter of type S and
 * return a type of T. For example:
 * </p>
 * <p>
 * {@code public static String marshal(BoundType param)}
 * </p>
 * @param <S> Source type for the conversion
 * @param <T> Source type
 */
public class ConverterToMarshaller<S, T> implements ToMarshaller<S, T> {

	private final Converter<S,T> converter;
    
	/**
	 * Create a new instance for the given converter
	 * @param converter The converter to be wrapped
	 */
    public ConverterToMarshaller(Converter<S,T> converter) {
        
    	if (converter == null) {
    		throw new IllegalStateException("converter must not be null");
    	}
        this.converter = converter;     
    }

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
    public T marshal(S object) {

        return converter.convert(object);
    }
   
	/**
	 * {@inheritDoc}
	 */
	/* @Override */
    public Class<S> getBoundClass() {
        return converter.getInputClass();
    }

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public Class<T> getTargetClass() {
		return converter.getOutputClass();
	}
}
