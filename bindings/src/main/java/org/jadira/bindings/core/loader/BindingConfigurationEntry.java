/*
 *  Copyright 2010, 2011 Christopher Pheby
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
package org.jadira.bindings.core.loader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.jadira.bindings.core.annotation.DefaultBinding;

/**
 * Represents a BindingConfigurationEntry - a specification of a particular
 * binding by configuration 
 * 
 * The configuration entry either consists of a) a
 * binding class implementing either {@link org.jadira.bindings.core.api.Binding}, 
 * {@link org.jadira.bindings.core.api.ToMarshaller},
 * {@link org.jadira.bindings.core.api.FromUnmarshaller}, 
 * or {@link org.jadira.bindings.core.api.Converter} together with an optional
 * qualifier, b) an arbitrary class to be inspected for annotations or other
 * configuration meaningful to a {@link org.jadira.bindings.core.api.Provider}, 
 * or c) an explicit definitionof a method for marshalling or unmarshalling 
 * and/or constructor (either from method or from constructor can be defined but 
 * not both).
 * 
 * @author Chris
 */
public class BindingConfigurationEntry {

	private final Class<?> bindingClass;
	private final Class<?> sourceClass;
	private final Class<?> targetClass;
	private final Class<? extends Annotation> qualifier;
	private final Method toMethod;
	private final Method fromMethod;
	private final Constructor<?> fromConstructor;

	/**
	 * Create a new entry for the given binding class
	 * @param bindingClass The binding class
	 */
	public BindingConfigurationEntry(Class<?> bindingClass) {
		this(bindingClass, null);
	}

	/**
	 * Create a new entry for the given binding class and qualifier
	 * @param bindingClass The binding class
	 * @param qualifier The qualifier
	 */
	public BindingConfigurationEntry(Class<?> bindingClass, Class<? extends Annotation> qualifier) {
		
		this.bindingClass = bindingClass;
		this.qualifier = qualifier == null ? DefaultBinding.class : qualifier;
		
		this.sourceClass = null;
		this.targetClass = null;
		this.toMethod = null;
		this.fromMethod = null;
		this.fromConstructor = null;
	}
	
	/**
	 * Create a new entry for the given options
	 * @param sourceClass The source class to be bound
	 * @param targetClass The foreign side of the relationship
	 * @param qualifier The qualifier
	 * @param toMethod The to method to be bound
	 * @param fromMethod The from method to be bound
	 */
	public BindingConfigurationEntry(Class<?> sourceClass, Class<?> targetClass,
			Class<? extends Annotation> qualifier, Method toMethod,
			Method fromMethod) {
		
		this.sourceClass = sourceClass;
		this.targetClass = targetClass;
		this.qualifier = qualifier == null ? DefaultBinding.class : qualifier;
		this.toMethod = toMethod;
		this.fromMethod = fromMethod;
		this.fromConstructor = null;
		
		this.bindingClass = null;
	}
	
	/**
	 * Create a new entry for the given options
	 * @param sourceClass The source class to be bound
	 * @param targetClass The foreign side of the relationship
	 * @param qualifier The qualifier
	 * @param toMethod The to method to be bound
	 * @param fromConstructor The from constructor to be bound
	 */
	public BindingConfigurationEntry(Class<?> sourceClass, Class<?> targetClass,
			Class<? extends Annotation> qualifier, Method toMethod,
			Constructor<?> fromConstructor) {
		
		this.sourceClass = sourceClass;
		this.targetClass = targetClass;
		this.qualifier = qualifier == null ? DefaultBinding.class : qualifier;
		this.toMethod = toMethod;
		this.fromConstructor = fromConstructor;
		this.fromMethod = null;
		
		this.bindingClass = null;
	}

	/**
	 * @return The Binding Class, if any
	 */
	public Class<?> getBindingClass() {
		return bindingClass;
	}

	/**
	 * @return The Source Class, if any
	 */
	public Class<?> getSourceClass() {
		return sourceClass;
	}

	/**
	 * @return The Target Class, if any
	 */
	public Class<?> getTargetClass() {
		return targetClass;
	}

	/**
	 * @return The Qualifier Annotation, if any
	 */
	public Class<? extends Annotation> getQualifier() {
		return qualifier;
	}

	/** 
	 * @return The To Method, if any
	 */
	public Method getToMethod() {
		return toMethod;
	}

	/** 
	 * @return The From Method, if any
	 */
	public Method getFromMethod() {
		return fromMethod;
	}

	/** 
	 * @return The From Constructor, if any
	 */
	public Constructor<?> getFromConstructor() {
		return fromConstructor;
	}
}
