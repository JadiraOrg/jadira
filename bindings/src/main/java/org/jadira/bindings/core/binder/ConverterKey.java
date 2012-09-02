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
package org.jadira.bindings.core.binder;

import java.lang.annotation.Annotation;

import org.jadira.bindings.core.annotation.DefaultBinding;

/**
 * A {@link ConverterKey} is used to look up a registered converter
 */
public class ConverterKey<I,O> {
	
	private final Class<I> input;
	private final Class<O> output;
	private final Class<? extends Annotation> qualifier;

	/**
	 * Creates a new ConverterKey instance
	 * @param input The Input class
	 * @param output The output class
	 */
	public ConverterKey(Class<I> input, Class<O> output, Class<? extends Annotation> qualifier) {
		this.input = input;
		this.output = output;
		this.qualifier = qualifier == null ? DefaultBinding.class : qualifier;
	}
	
	/**
	 * Gets the configured input class
	 * @return The input class
	 */
	public Class<I> getInputClass() {
		return input;
	}

	/**
	 * Gets the configured output class
	 * @return The output class
	 */
	public Class<O> getOutputClass() {
		return output;
	}
	
	/**
	 * Gets the configured qualifier
	 * @return The qualifier
	 */
	public Class<? extends Annotation> getQualifierAnnotation() {
		return qualifier;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
        if (obj == null) {
        	return false;
        }
        if (!this.getClass().equals(obj.getClass())) {
        	return false;
        }
       
        final ConverterKey<?,?> obj2 = (ConverterKey<?,?>)obj;
        if (this.getInputClass().equals(obj2.getInputClass()) 
        		&& this.getOutputClass().equals(obj2.getOutputClass())
        		&& this.getQualifierAnnotation().equals(obj2.getQualifierAnnotation())) {
            return true;
        }
        
        return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return getInputClass().hashCode() * 3 + getOutputClass().hashCode() * 5 + getQualifierAnnotation().hashCode() * 7;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "input: {" + getInputClass().getName() + "}, output: {" + getOutputClass().getName() + "}, qualifier: {" + getQualifierAnnotation().getName() + "}";
	}
	
	public ConverterKey<O,I> invert() {
		return new ConverterKey<O,I>(output, input, qualifier);
	}
}