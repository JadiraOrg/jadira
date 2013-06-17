/*
 *  Copyright 2013 Chris Pheby
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
package org.jadira.cloning.api;

import java.lang.invoke.MethodHandle;
import java.util.Set;

import org.jadira.cloning.annotation.Transient;

/**
 * A clone driver defines the features of a class that is used to bootstrap and provide context to a
 * cloning operation, typically (but not always), a {@link Cloner} * 
 */
public interface CloneDriver extends CloneImplementor {

	/**
	 * Gets the built in implementor for the given class 
	 */
	public CloneImplementor getBuiltInImplementor(Class<?> clazz);
	
	/**
	 * Retrieves the registered implementor for the given class
	 * 
	 * @param clazz
	 * @return
	 */
	CloneImplementor getImplementor(Class<?> clazz);

	/**
	 * Retrieves the registered implementor for the given class
	 * 
	 * @param clazz
	 * @return
	 */
	CloneImplementor getAnnotationImplementor(Class<?> clazz);

	/** 
	 * Put the registered implementor for the given class
	 * 
	 * 
	 * @param clazz
	 * @param implementor
	 */
	void putAnnotationImplementor(Class<?> clazz, CloneImplementor implementor);

	
	/**
	 * If true, then any class that implements {@link Cloneable} will be cloned using the
	 * {@link Object#clone()} method.
	 * 
	 * @return Default value is false
	 */
	boolean isUseCloneable();
	
	/**
	 * Returns the clone() method for the indicated class
	 * @param clazz The class to obtain the method for
	 * @return The related MethodHandle
	 */
	MethodHandle getCloneMethod(Class<?> clazz);

	/**
	 * Put the clone() method for the indicated class
	 * @param clazz The class to obtain the method for
	 * @param handle The related MethodHandle
	 */
	void putCloneMethod(Class<?> clazz, MethodHandle handle);
	
	/**
	 * If false, indicates that fields modified by the transient keyword should not be cloned,
	 * instead being replaced with null.
	 * 
	 * @return Default value is true
	 */
	boolean isCloneTransientFields();

	/**
	 * If false, indicates that fields annotated by the {@link Transient} annotation should not be
	 * cloned, instead being replaced with null.
	 * 
	 * @return Default value is false
	 */
	boolean isCloneTransientAnnotatedFields();

	/**
	 * If false, indicates that classes known to be immutable should be not cloned. Immutables are
	 * identified by the @Immutable annotation; are one of a select set of known JDK immutable
	 * classes; or are detected as immutable by the Mutability Detector tool (if it was found on the
	 * classpath).
	 * 
	 * @return Default value is false
	 */
	boolean isCloneImmutable();

	/**
	 * A list of classes that should be treated as immutable.
	 * 
	 * @return immutableClasses The immutable classes
	 */
	Set<Class<?>> getImmutableClasses();

	/**
	 * A list of classes that should not be cloned.
	 * 
	 * @return immutableClasses The classes which should be be cloned
	 */
	Set<Class<?>> getNonCloneableClasses();

	/**
	 * Indicates whether custom clone implementors are enabled
	 * 
	 * @return Default value is true
	 */
	boolean isUseCloneImplementors();
	
	/**
	 * Indicates whether synthetic fields should be cloned
	 * 
	 * @return  cloneSyntheticFields Default is false
	 */
    boolean isCloneSyntheticFields();
    
	/**
	 * Indicates whether the given object <em>instance</em> should be treated as immutable
	 */
	boolean isImmutableInstance(Object instance);
	
	/** 
	 * Stores an object instance to be treated as immutable
	 */
	void putImmutableInstance(Object instance);
}
