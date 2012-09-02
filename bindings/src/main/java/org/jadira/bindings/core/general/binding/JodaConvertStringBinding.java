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
import org.joda.convert.StringConverter;

/**
 * Binding that adapts a Joda Convert {@link StringConverter}
 * @param <S> The type this Binding converts to and from a String
 */
public class JodaConvertStringBinding<S> implements Binding<S, String>, StringConverter<S> {

    private Class<S> boundClass;
    private StringConverter<S> converter;

    /**
     * Creates a new instance
     * @param boundClass The class that can be converted to and from a String
     * @param converter The StringConverter instance to be wrapped
     */
    public JodaConvertStringBinding(Class<S> boundClass, StringConverter<S> converter) {
        this.boundClass = boundClass;
        this.converter = converter;
    }

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
    public String marshal(S object) {
        return converter.convertToString(object);
    }

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
    public S unmarshal(String inputString) {
        return converter.convertFromString(boundClass, inputString);
    }

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
    public String convertToString(S object) {
        return converter.convertToString(object);
    }

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
    public S convertFromString(Class<? extends S> boundClass, String inputString) {
        return converter.convertFromString(boundClass, inputString);
    }

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public Class<S> getBoundClass() {
		return boundClass;
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public Class<String> getTargetClass() {
		return String.class;
	}
}
