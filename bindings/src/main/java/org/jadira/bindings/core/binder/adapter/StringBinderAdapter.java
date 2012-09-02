/*
 *  Copyright 2011 Christopher Pheby
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
package org.jadira.bindings.core.binder.adapter;

import java.lang.annotation.Annotation;

import org.jadira.bindings.core.binder.ConversionBinder;
import org.jadira.bindings.core.binder.StringBinder;

public class StringBinderAdapter implements StringBinder {

	private ConversionBinder binder;

	public StringBinderAdapter(ConversionBinder binder) {
		this.binder = binder;
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public <T> T convertFromString(Class<T> output, String object) {
		return binder.convertTo(String.class, output, object);
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public <T> T convertFromString(Class<T> output, String object, Class<? extends Annotation> qualifier) {
		return binder.convertTo(String.class, output, object, qualifier);
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public String convertToString(Object object) {
		return binder.convertTo(String.class, object);
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public String convertToString(Object object, Class<? extends Annotation> qualifier) {
		return binder.convertTo(String.class, object, qualifier);
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public <S> String convertToString(Class<S> input, Object object) {
		return binder.convertTo(input, String.class, object);
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public <S> String convertToString(Class<S> input, Object object,
			Class<? extends Annotation> qualifier) {
		return binder.convertTo(input, String.class, object, qualifier);
	}

	/**
	 * {@inheritDoc}
	 */
	/* @Override */
	public ConversionBinder getAssociatedBinder() {
		return binder;
	}
}
