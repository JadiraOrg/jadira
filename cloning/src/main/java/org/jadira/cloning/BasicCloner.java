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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import org.jadira.cloning.annotation.Immutable;
import org.jadira.cloning.annotation.NonCloneable;
import org.jadira.cloning.annotation.Transient;
import org.jadira.cloning.api.CloneDriver;
import org.jadira.cloning.api.CloneImplementor;
import org.jadira.cloning.api.CloneStrategy;
import org.jadira.cloning.api.Cloner;
import org.jadira.cloning.collection.FastIdentityHashSet;
import org.jadira.cloning.implementor.PortableCloneStrategy;
import org.jadira.cloning.implementor.UnsafeCloneStrategy;
import org.jadira.cloning.implementor.types.ArrayListImplementor;
import org.jadira.cloning.implementor.types.ConcurrentHashMapImplementor;
import org.jadira.cloning.implementor.types.GregorianCalendarImplementor;
import org.jadira.cloning.implementor.types.HashMapImplementor;
import org.jadira.cloning.implementor.types.HashSetImplementor;
import org.jadira.cloning.implementor.types.LinkedListImplementor;
import org.jadira.cloning.implementor.types.TreeMapImplementor;
import org.jadira.cloning.unsafe.FeatureDetection;

/**
 * This class is for performing deep clones. <br />
 * 
 * Inspired by the Rits Deep Cloning API, this library uses Unsafe to provide maximum performance
 * and a variety of extensibility and annotation configuration strategies.
 * 
 * Most users will interact with the cloning library via this class and the {@link Cloner}
 * interface. This is the most functional {@link Cloner} implementation built in to Jadira Cloning.
 * The alternative, {@link MinimalCloner} is recommended for situations where only basic
 * deep-cloning functionality is required and maximum throughput desirable. <br/>
 * 
 * BasicCloner is a fully configurable cloning implementation. <br />
 * 
 * The class features two pluggable {@link CloneStrategy}, with implementations available in the
 * library. {@link UnsafeCloneStrategy} makes use of sun.misc.Unsafe and delivers fast performance,
 * particularly when using Server VMs with Java 6 and later. However, it requires sun.misc.Unsafe to
 * be available. VMs providing this class include Oracle and IBM's JVMs. A fully functional Unsafe
 * implementation is not available on Android. For platforms where UnsafeCloneStrategy is not
 * suitable, {@link PortableCloneStrategy} should be used instead. In general this delivers good,
 * but lower performance, giving about half the cloning throughput. Functionality however it is
 * identical. If you use the PortableCloneStrategy, Objenesis must be included on the classpath. <br />
 * 
 * There are a variety of ways to customise cloning. This class can be configured with
 * {@link CloneImplementor}s which can be implemented to override the normal cloning behaviour for
 * particular classes. Using these, must be enabled by setting {@link #useCloneImplementors} if
 * required to be used. <br />
 * 
 * Specific classes can be indicated as being immutable and/or non-cloneable - these classes do not
 * need to be copied during the clone. Cloning of immutables can be disabled or enabled using
 * {@link #setCloneImmutable(boolean)}, whilst non-cloneable classes may never be cloned. <br />
 * 
 * Setting {@link #cloneTransientFields} to false will prevent the cloning of transient fields which
 * will be set to their null (or default primitive) values instead of being cloned. <br />
 * 
 * BasicCloner can detect classes that implement {@link java.lang.Cloneable} and invoke their
 * clone() method. To enable this function, set {@link #useCloneable}. <br />
 * 
 * Finally, the operation of the class can be customised using annotations.
 * {@link org.jadira.cloning.annotation.Cloneable} annotation can be used to customise the treatment
 * of particular classes being cloned. {@link @Cloner} can be used to specify a particular method
 * within a class to be used to fulfil the clone for that specific class. {@link NonCloneable}
 * indicates that a class should not be cloned. Finally {@link Transient} annotation can be used on
 * any class field to indicate that the field is transient. In the case of this last annotation, use
 * {{@link #cloneTransientAnnotatedFields} to enable or disable the capability (by default these
 * fields are not cloned). <br />
 * 
 * {@link Immutable} or {@link javax.annotation.concurrent.Immutable} provide an alternative
 * mechanism for indicating that a class is immutable. <br />
 * 
 * If the Mutability Detector library is on the classpath this will automatically be used to
 * determine immutability for all classes being cloned. If a class is identified as IMMUTABLE or
 * EFFECTIVELY_IMMUTABLE by Mutability Detector it will be treated as immutable.
 * 
 * @see {@link MinimalCloner} A most optimised implementation of Cloner that only provides basic
 *      deep cloning functionality but no configurability.
 */
public class BasicCloner implements Cloner, CloneDriver, CloneImplementor {

	private final CloneStrategy cloneStrategy;

	private Map<Class<?>, CloneImplementor> builtInImplementors = new IdentityHashMap<Class<?>, CloneImplementor>();
	private Map<Class<?>, CloneImplementor> allImplementors = new IdentityHashMap<Class<?>, CloneImplementor>();
	private Map<Class<?>, CloneImplementor> annotationImplementors = new IdentityHashMap<Class<?>, CloneImplementor>();

	private Map<Class<?>, MethodHandle> cloneMethods = new IdentityHashMap<Class<?>, MethodHandle>();

	private Set<Class<?>> immutableClasses = new FastIdentityHashSet<Class<?>>();
	private Set<Class<?>> nonCloneableClasses = new FastIdentityHashSet<Class<?>>();

	private boolean useCloneable = false;
	private boolean useCloneImplementors = true;
	private boolean cloneTransientFields = true;
	private boolean cloneTransientAnnotatedFields = false;
	private boolean cloneImmutable = false;
	private boolean cloneSyntheticFields = false;

	private Map<Class<?>, Object> builtInImmutableInstances = new HashMap<Class<?>, Object>();
	private IdentityHashMap<Object, Object> allImmutableInstances = new IdentityHashMap<Object, Object>();
	private Object[] allImmutableInstancesArray;

	/**
	 * Create a new instance with {@link UnsafeCloneStrategy}, unless it is not available in which
	 * case {@link PortableCloneStrategy} will be used.
	 */
	public BasicCloner() {
		if (FeatureDetection.hasUnsafe()) {
			this.cloneStrategy = UnsafeCloneStrategy.getInstance();
		} else if (FeatureDetection.hasObjenesis()) {
			this.cloneStrategy = PortableCloneStrategy.getInstance();
		} else {
			throw new IllegalStateException("Couldn't launch BasicCloner with built in strategies as neither Unsafe or Objenesis could be found");
		}

		initialize();
	}

	/**
	 * Creates a new instance with the given {@link CloneStrategy}
	 * 
	 * @param cloneStrategy
	 */
	public BasicCloner(final CloneStrategy cloneStrategy) {
		this.cloneStrategy = cloneStrategy;

		initialize();
	}

	/**
	 * Initialise a new instance
	 */
	private void initialize() {
		initializeBuiltInImplementors();
		initializeBuiltInImmutableInstances();
	}

	/**
	 * Initialise a set of built in CloneImplementors for commonly used JDK types
	 */
	private void initializeBuiltInImplementors() {
		builtInImplementors.put(ArrayList.class, new ArrayListImplementor());
		builtInImplementors.put(ConcurrentHashMap.class, new ConcurrentHashMapImplementor());
		builtInImplementors.put(GregorianCalendar.class, new GregorianCalendarImplementor());
		builtInImplementors.put(HashMap.class, new HashMapImplementor());
		builtInImplementors.put(HashSet.class, new HashSetImplementor());
		builtInImplementors.put(LinkedList.class, new LinkedListImplementor());
		builtInImplementors.put(TreeMap.class, new TreeMapImplementor());
		allImplementors.putAll(builtInImplementors);
	}

	private void initializeBuiltInImmutableInstances() {
		try {
			Field field = TreeSet.class.getDeclaredField("PRESENT");
			field.setAccessible(true);
			Object treeSetPresent = field.get(null);
			builtInImmutableInstances.put(TreeSet.class, treeSetPresent);
			putImmutableInstance(treeSetPresent);

			field = HashSet.class.getDeclaredField("PRESENT");
			field.setAccessible(true);
			Object hashSetPresent = field.get(null);
			builtInImmutableInstances.put(HashSet.class, hashSetPresent);
			putImmutableInstance(hashSetPresent);
		} catch (final SecurityException e) {
			throw new IllegalStateException(e);
		} catch (final NoSuchFieldException e) {
			throw new IllegalStateException(e);
		} catch (final IllegalArgumentException e) {
			throw new IllegalStateException(e);
		} catch (final IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T clone(T obj) {
		return clone(obj, this, new IdentityHashMap<Object, Object>(10));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T newInstance(Class<T> c) {
		return cloneStrategy.newInstance(c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canClone(Class<?> clazz) {
		return cloneStrategy.canClone(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T clone(T obj, CloneDriver context, IdentityHashMap<Object, Object> referencesToReuse) {

		return cloneStrategy.clone(obj, context, referencesToReuse);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CloneImplementor getBuiltInImplementor(Class<?> clazz) {

		return allImplementors.get(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CloneImplementor getImplementor(Class<?> clazz) {

		return allImplementors.get(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CloneImplementor getAnnotationImplementor(Class<?> clazz) {

		return annotationImplementors.get(clazz);
	}

	/**
	 * Sets CloneImplementors to be used.
	 * 
	 * @param implementors
	 */
	public void setImplementors(Map<Class<?>, CloneImplementor> implementors) {
		// this.implementors = implementors;

		this.allImplementors = new HashMap<Class<?>, CloneImplementor>();
		allImplementors.putAll(builtInImplementors);
		allImplementors.putAll(implementors);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Class<?>> getImmutableClasses() {
		return immutableClasses;
	}

	/**
	 * Indicates classes which are immutable.
	 * 
	 * @param immutableClasses
	 */
	public void setImmutableClasses(Set<Class<?>> immutableClasses) {
		this.immutableClasses = immutableClasses;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<Class<?>> getNonCloneableClasses() {
		return nonCloneableClasses;
	}

	/**
	 * Indicates classes that are not Cloneable.
	 * 
	 * @param nonCloneableClasses
	 */
	public void setNonCloneableClasses(Set<Class<?>> nonCloneableClasses) {
		this.nonCloneableClasses = nonCloneableClasses;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUseCloneable() {
		return useCloneable;
	}

	/**
	 * If true, the clone() method of classes implementing {@link java.lang.Cloneable} will be
	 * delegated to where appropriate
	 * 
	 * @param cloneTransientAnnotatedFields
	 */
	public void setUseCloneable(boolean useCloneable) {
		this.useCloneable = useCloneable;
	}

	@Override
	public MethodHandle getCloneMethod(Class<?> clazz) {
		return cloneMethods.get(clazz);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCloneTransientFields() {
		return cloneTransientFields;
	}

	/**
	 * If true, fields marked with the transient keyword should be cloned
	 * 
	 * @param cloneTransientAnnotatedFields
	 */
	public void setCloneTransientFields(boolean cloneTransientFields) {
		this.cloneTransientFields = cloneTransientFields;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCloneTransientAnnotatedFields() {
		return cloneTransientAnnotatedFields;
	}

	/**
	 * If true, fields annotated with @Transient should be cloned
	 * 
	 * @param cloneTransientAnnotatedFields
	 */
	public void setCloneTransientAnnotatedFields(boolean cloneTransientAnnotatedFields) {
		this.cloneTransientAnnotatedFields = cloneTransientAnnotatedFields;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCloneImmutable() {
		return cloneImmutable;
	}

	/**
	 * If true, immutable classes should be cloned.
	 * 
	 * @param cloneImmutable
	 */
	public void setCloneImmutable(boolean cloneImmutable) {
		this.cloneImmutable = cloneImmutable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialiseFor(Class<?> classes) {
		cloneStrategy.initialiseFor(classes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUseCloneImplementors() {
		return useCloneImplementors;
	}

	/**
	 * If true, cloning may be delegates to clone implementors for specific tasks.
	 * 
	 * @param useCloneImplementors
	 */
	public void setUseCloneImplementors(boolean useCloneImplementors) {
		this.useCloneImplementors = useCloneImplementors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCloneSyntheticFields() {
		return cloneSyntheticFields;
	}

	/**
	 * If true, synthetic fields should be cloned
	 * 
	 * @param cloneSyntheticFields
	 */
	public void setCloneSyntheticFields(boolean cloneSyntheticFields) {
		this.cloneSyntheticFields = cloneSyntheticFields;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putAnnotationImplementor(Class<?> clazz, CloneImplementor implementor) {
		this.annotationImplementors.put(clazz, implementor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putCloneMethod(Class<?> clazz, MethodHandle handle) {
		this.cloneMethods.put(clazz, handle);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isImmutableInstance(Object instance) {
		
		if (allImmutableInstances != null) {
			for (int i = 0; i < allImmutableInstancesArray.length; i++) {
				if (allImmutableInstancesArray[i] == instance) {
					return true;
				}
			}
			return false;
		} else {
			return allImmutableInstances.containsKey(instance);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void putImmutableInstance(Object instance) {
		this.allImmutableInstances.put(instance, Boolean.TRUE);
		Set<Object> keySet = this.allImmutableInstances.keySet();
		this.allImmutableInstancesArray = new Object[keySet.size()];
		
		int i = 0;
		for (Object next: keySet) {
			this.allImmutableInstancesArray[i] = next;
			i++;
			if (i >= allImmutableInstancesArray.length) {
				break;
			}
		}
	}
}
