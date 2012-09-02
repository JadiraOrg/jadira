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
package org.jadira.bindings.core.general.unmarshaller;

import org.jadira.bindings.core.api.Converter;
import org.jadira.bindings.core.api.FromUnmarshaller;

/**
 * Unmarshaller supports constructor parameterised by a target object
 * @param <S> Source type for the conversion
 * @param <T> Target type
 */
public final class ConverterFromUnmarshaller<S, T> implements FromUnmarshaller<S, T> {

    private final Converter<T,S> converter;
    
	/**
	 * Create a new instance for the given converter
	 * @param converter The converter to be wrapped
	 */
    public ConverterFromUnmarshaller(Converter<T,S> converter) {
        
    	if (converter == null) {
    		throw new IllegalStateException("converter must not be null");
    	}
        this.converter = converter;        
    }

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
    public S unmarshal(T object) {
    	
    	return converter.convert(object);
    }
    
	/**
	 * {@inheritDoc}
	 */
	/* @Override */
    public Class<S> getBoundClass() {
        return converter.getOutputClass();
    }
    
	/**
	 * {@inheritDoc}
	 */
	/* @Override */
    public Class<T> getTargetClass() {
        return converter.getInputClass();
    }
}
