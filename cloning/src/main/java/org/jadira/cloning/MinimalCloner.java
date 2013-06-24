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
package org.jadira.cloning;

import java.lang.invoke.MethodHandle;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import org.jadira.cloning.api.CloneDriver;
import org.jadira.cloning.api.CloneImplementor;
import org.jadira.cloning.api.Cloner;
import org.jadira.cloning.implementor.PortableCloneStrategy;
import org.jadira.cloning.implementor.UnsafeCloneStrategy;
import org.jadira.cloning.unsafe.UnsafeOperations;
import org.objenesis.ObjenesisException;

/**
 * This is a highly reduced version of {@link Cloner}. It can deliver somewhat better throughput
 * than {@link UnsafeCloneStrategy} using the Server JVM, and around twice the throughput of
 * the {@link PortableCloneStrategy}. Unlike BasicCloner, this cloner does not offer any
 * configuration options. Due to lack of configuration and special functions, this cloner is
 * unsuitable for many datatypes.
 * 
 * Note that MinimalCloner can be slower for certain kinds of data structure. For example, when
 * cloning a linked list, MinimalCloner must recurse through the entire data structure, whereas
 * other Cloners can use a CloneImplementor to perform cloning of this structure iteratively.
 * 
 * In general, you are recommended to use MinimalCloner judiciously. Typically you can use it by
 * designating a data type as @Cloneable(implementor=MinimalCloner.class) so that the type known to
 * benefit from this cloner makes use of it.
 */
public class MinimalCloner implements Cloner, CloneDriver, CloneImplementor {

	private static final UnsafeOperations UNSAFE_OPERATIONS = UnsafeOperations.getUnsafeOperations();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T newInstance(Class<T> c) {
		try {
			return UNSAFE_OPERATIONS.allocateInstance(c);
		} catch (IllegalStateException e) {
			throw new ObjenesisException(e.getCause());
		}
	}

	/**
	 * Always returns true
	 */
	@Override
	public boolean canClone(Class<?> clazz) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T clone(T obj, CloneDriver context, IdentityHashMap<Object, Object> referencesToReuse) {

		T copy = UNSAFE_OPERATIONS.deepCopy(obj, referencesToReuse);
		return copy;
	}

	/**
	 * This implementation does not delegate to implementors
	 */
	@Override
	public CloneImplementor getImplementor(Class<?> clazz) {
		return null;
	}

	/**
	 * This implementation cannot invoke clone()
	 */
	@Override
	public boolean isUseCloneable() {
		return false;
	}

	/**
	 * Transient fields are always cloned
	 */
	@Override
	public boolean isCloneTransientFields() {
		return true;
	}

	/**
	 * Transient annotated fields are always cloned
	 */
	@Override
	public boolean isCloneTransientAnnotatedFields() {
		return true;
	}

	/**
	 * Immutable fields are always cloned, but object reference identity in the new structure is
	 * preserved
	 */
	@Override
	public boolean isCloneImmutable() {
		return true;
	}

	/**
	 * No Immutable classes are identified
	 */
	@Override
	public Set<Class<?>> getImmutableClasses() {
		return Collections.emptySet();
	}

	/**
	 * No Non-Cloneable classes are identified
	 */
	@Override
	public Set<Class<?>> getNonCloneableClasses() {
		return Collections.emptySet();
	}

	/**
	 * CloneImplementors may not be used
	 */
	@Override
	public boolean isUseCloneImplementors() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T clone(T obj) {
		return clone(obj, this, new IdentityHashMap<Object, Object>());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialiseFor(Class<?> classes) {
	}

	/**
	 * Cloneable annotation is not supported
	 */
	@Override
	public CloneImplementor getAnnotationImplementor(Class<?> clazz) {
		return null;
	}

	/**
	 * Cloneable interface is not supported
	 */
	@Override
	public MethodHandle getCloneMethod(Class<?> clazz) {
		return null;
	}

	/**
	 * Does not clone synthetic fields
	 */
	@Override
	public boolean isCloneSyntheticFields() {
		return false;
	}

	@Override
	public void putAnnotationImplementor(Class<?> clazz, CloneImplementor implementor) {
		// No-op
	}

	@Override
	public void putCloneMethod(Class<?> clazz, MethodHandle handle) {
		// No-op
	}

	@Override
	public CloneImplementor getBuiltInImplementor(Class<?> clazz) {
		return null;
	}

	@Override
	public boolean isImmutableInstance(Object instance) {
		return false;
	}

	@Override
	public void putImmutableInstance(Object instance) {
		// no-op
	}
}
