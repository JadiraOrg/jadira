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
package org.jadira.reflection.cloning.implementor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.IdentityHashMap;

import org.jadira.reflection.access.model.ClassModel;
import org.jadira.reflection.access.model.FieldModel;
import org.jadira.reflection.access.model.FieldType;
import org.jadira.reflection.cloning.MinimalCloner;
import org.jadira.reflection.cloning.api.CloneDriver;
import org.jadira.reflection.cloning.api.CloneImplementor;
import org.jadira.reflection.cloning.api.CloneStrategy;
import org.jadira.reflection.cloning.api.NoCloneImplementor;
import org.jadira.reflection.core.misc.ClassUtils;

/**
 * A Base {@link CloneStrategy} implementation providing functionality which is
 * common across class and field access mechanisms.
 */
public abstract class AbstractCloneStrategy implements CloneStrategy {

	private static final int REFERENCE_STACK_LIMIT = 150;

	@Override
	public abstract <T> T newInstance(Class<T> c);

	@Override
	public boolean canClone(Class<?> clazz) {
		return true;
	}

	@Override
	public <T> T clone(T obj, CloneDriver context, IdentityHashMap<Object, Object> referencesToReuse, long stackDepth) {

		/**
		 * To avoid unnecessary recursion and potential stackoverflow errors, we use an internal
		 * stack
		 */
		stackDepth++;

		final Deque<WorkItem> stack;
		if (REFERENCE_STACK_LIMIT <= stackDepth) {
			stack = new ArrayDeque<WorkItem>();
		} else {
			stack = null;
		}

		Object objectInput;

		// T parentOutput = null;
		WorkItem nextWork = null;

		while (true) {

			Object objectResult;

			if (nextWork == null) {
				objectInput = obj;
			} else {
				objectInput = getFieldValue(nextWork.getSource(), nextWork.getFieldModel());
			}

			if (objectInput == null) {
				objectResult = null;
			}

			else if (context.isImmutableInstance(objectInput)) {
				objectResult = objectInput;
			}

			else {
				objectResult = doCloneStep(objectInput, context, referencesToReuse, stack, stackDepth);
			}

			if (nextWork == null) {
				nextWork = (stack == null ? null : stack.pollFirst());
				if (nextWork == null) {
					@SuppressWarnings("unchecked")
					final T convertedResult = (T) objectResult;
					return convertedResult;
				}
			} else {
				putFieldValue(nextWork.getTarget(), nextWork.getFieldModel(), objectResult);
				nextWork = (stack == null ? null : stack.pollFirst());
			}
		}
	}

	private Object doCloneStep(Object objectInput, CloneDriver context, IdentityHashMap<Object, Object> referencesToReuse, final Deque<WorkItem> stack, long stackDepth) {
		Object objectResult;

		@SuppressWarnings("unchecked")
		final Class<Object> clazz = (Class<Object>) objectInput.getClass();

		if (clazz.isPrimitive() || clazz.isEnum()) {
			objectResult = objectInput;
		} else if (clazz.isArray()) {
			final Object copy = handleArray(objectInput, context, referencesToReuse, stackDepth);
			if (referencesToReuse != null) {
				referencesToReuse.put(objectInput, copy);
			}
			objectResult = copy;
		} else if (ClassUtils.isJdkImmutable(clazz) || ClassUtils.isWrapper(clazz) || context.getImmutableClasses().contains(clazz) || context.getNonCloneableClasses().contains(clazz)) {
			objectResult = objectInput;
		} else {

			final ClassModel<Object> model = getClassModel(clazz);
			if (model.isFlat()) {
				referencesToReuse = null;
			}

			final Object result = referencesToReuse == null ? null : referencesToReuse.get(objectInput);
			if (result != null) {
				objectResult = result;
			} else {

				final CloneImplementor cloneImplementor;
				if (context.isUseCloneImplementors()) {
					cloneImplementor = context.getImplementor(clazz);
				} else {
					cloneImplementor = context.getBuiltInImplementor(clazz);
				}
				if (cloneImplementor != null) {
					Object copy = cloneImplementor.clone(objectInput, context, referencesToReuse, stackDepth);
					if (referencesToReuse != null) {
						referencesToReuse.put(objectInput, copy);
					}
					objectResult = copy;
				} else {

					if (model.isDetectedAsImmutable() || model.isNonCloneable()) {
						objectResult = objectInput;
					} else {

						final org.jadira.reflection.cloning.annotation.Cloneable cloneableAnnotation = clazz.getAnnotation(org.jadira.reflection.cloning.annotation.Cloneable.class);
						if (cloneableAnnotation != null && !NoCloneImplementor.class.equals(cloneableAnnotation.implementor())) {
							final Object copy = handleCloneImplementor(objectInput, context, referencesToReuse, clazz, cloneableAnnotation, stackDepth);
							if (referencesToReuse != null) {
								referencesToReuse.put(objectInput, copy);
							}
							objectResult = copy;
						}

						else if (model.getCloneImplementor() != null) {
							final Object copy = model.getCloneImplementor().clone(objectInput, context, referencesToReuse, stackDepth);
							if (referencesToReuse != null) {
								referencesToReuse.put(objectInput, copy);
							}
							objectResult = copy;
						} else if (context.isUseCloneable() && Cloneable.class.isAssignableFrom(clazz)) {
							final Object copy = handleCloneableCloneMethod(objectInput, context, referencesToReuse, clazz, cloneableAnnotation);
							if (referencesToReuse != null) {
								referencesToReuse.put(objectInput, copy);
							}
							objectResult = copy;
						} else {

							objectResult = newInstance(clazz);
							if (referencesToReuse != null) {
								referencesToReuse.put(objectInput, objectResult);
							}

							for (FieldModel<Object> f : model.getModelFields()) {

								if (!context.isCloneTransientFields() && f.isTransientField()) {
									handleTransientField(objectResult, f);
								} else if (!context.isCloneTransientAnnotatedFields() && f.isTransientAnnotatedField()) {
									handleTransientField(objectResult, f);
								} else {
									if (stack == null) {
										handleCloneField(objectInput, objectResult, context, f, referencesToReuse, stackDepth);
									} else {
										if (f.getFieldType() == FieldType.PRIMITIVE) {
											handleClonePrimitiveField(objectInput, objectResult, context, f, referencesToReuse);
										} else {
											if (!context.isCloneSyntheticFields() && f.isSynthetic()) {
												Object fieldObject = getFieldValue(objectInput, f);
												if (referencesToReuse != null) {
													referencesToReuse.put(fieldObject, fieldObject);
												}
											} else {
												stack.addFirst(new WorkItem(objectInput, objectResult, f));
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return objectResult;
	}

	private <T> T handleCloneImplementor(T obj, CloneDriver context, IdentityHashMap<Object, Object> referencesToReuse, final Class<T> clazz,
			org.jadira.reflection.cloning.annotation.Cloneable cloneableAnnotation, long stackDepth) {

		CloneImplementor cloneImplementor = context.getAnnotationImplementor(clazz);
		if (cloneImplementor == null) {
			cloneImplementor = (CloneImplementor) newInstance(cloneableAnnotation.implementor());
			context.putAnnotationImplementor(clazz, cloneImplementor);
		}
		if (MinimalCloner.class.equals(cloneImplementor.getClass())) {
			T copy = cloneImplementor.clone(obj, (MinimalCloner) cloneImplementor, referencesToReuse, stackDepth);
			referencesToReuse.put(obj, copy);
			return copy;
		} else {
			T copy = cloneImplementor.clone(obj, context, referencesToReuse, stackDepth);
			referencesToReuse.put(obj, copy);
			return copy;
		}
	}

	private <T> T handleCloneableCloneMethod(T obj, CloneDriver context, IdentityHashMap<Object, Object> referencesToReuse, final Class<T> clazz,
			org.jadira.reflection.cloning.annotation.Cloneable cloneableAnnotation) {

		MethodHandle handle = context.getCloneMethod(clazz);
		if (handle == null) {
			try {
				Method cloneMethod = clazz.getMethod("clone");
				handle = MethodHandles.lookup().unreflect(cloneMethod);
			} catch (IllegalAccessException e) {
				throw new IllegalStateException("Cannot access clone() method for: " + clazz.getName(), e);
			} catch (NoSuchMethodException e) {
				throw new IllegalStateException("Cannot find clone() method for: " + clazz.getName(), e);
			} catch (SecurityException e) {
				throw new IllegalStateException("Cannot invoke clone() method for: " + clazz.getName(), e);
			}

			context.putCloneMethod(clazz, handle);
		}
		T copy = performCloneForCloneableMethod(obj, context);
		referencesToReuse.put(obj, copy);
		return copy;
	}

	/**
	 * Helper method for performing cloning for objects of classes implementing java.lang.Cloneable
	 * @param object The object to be cloned.
	 * @param context The CloneDriver to be used
	 * @return The cloned object
	 */
	protected <T> T performCloneForCloneableMethod(T object, CloneDriver context) {

		Class<?> clazz = object.getClass();

		final T result;
		try {
			MethodHandle handle = context.getCloneMethod(clazz);
			result = (T) handle.invoke(object);
		} catch (Throwable e) {
			throw new IllegalStateException("Could not invoke clone() for instance of: " + clazz.getName(), e);
		}
		return result;
	}

	/**
	 * Obtain a ClassModel instance for the given class
	 * @param clazz Class to model
	 * @return The ClassModel
	 */
	protected abstract <W> ClassModel<W> getClassModel(Class<W> clazz);

	/**
	 * Clone an array
	 * @param origFieldValue The original value
	 * @param context The CloneDriver
	 * @param visited Used for tracking objects that have already been seen
	 * @param stackDepth The current depth of the stack - used to switch from recursion to iteration if the stack grows too deep.
	 * @return A clone of the array
	 */
	protected <T> T handleArray(T origFieldValue, CloneDriver context, IdentityHashMap<Object, Object> visited, long stackDepth) {

		if (visited != null) {
			@SuppressWarnings("unchecked")
			final T castResult = (T) visited.get(origFieldValue);

			if (castResult != null) {
				return castResult;
			}
		}

		final Class<?> componentType = origFieldValue.getClass().getComponentType();

		Object result = null;

		if (componentType.isPrimitive()) {

			if (java.lang.Boolean.TYPE == componentType) {
				result = Arrays.copyOf((boolean[]) origFieldValue, ((boolean[]) origFieldValue).length);
			} else if (java.lang.Byte.TYPE == componentType) {
				result = Arrays.copyOf((byte[]) origFieldValue, ((byte[]) origFieldValue).length);
			} else if (java.lang.Character.TYPE == componentType) {
				result = Arrays.copyOf((char[]) origFieldValue, ((char[]) origFieldValue).length);
			} else if (java.lang.Short.TYPE == componentType) {
				result = Arrays.copyOf((short[]) origFieldValue, ((short[]) origFieldValue).length);
			} else if (java.lang.Integer.TYPE == componentType) {
				result = Arrays.copyOf((int[]) origFieldValue, ((int[]) origFieldValue).length);
			} else if (java.lang.Long.TYPE == componentType) {
				result = Arrays.copyOf((long[]) origFieldValue, ((long[]) origFieldValue).length);
			} else if (java.lang.Float.TYPE == componentType) {
				result = Arrays.copyOf((float[]) origFieldValue, ((float[]) origFieldValue).length);
			} else if (java.lang.Double.TYPE == componentType) {
				result = Arrays.copyOf((double[]) origFieldValue, ((double[]) origFieldValue).length);
			}
		}

		if (result == null) {
			Object[] array = Arrays.copyOf((Object[]) origFieldValue, ((Object[]) origFieldValue).length);
			if (array.length > 0) {

				if (componentType.isArray()) {
					for (int i = 0; i < array.length; i++) {
						stackDepth++;
						array[i] = handleArray(array[i], context, visited, stackDepth);
					}
				} else {
					for (int i = 0; i < array.length; i++) {
						array[i] = clone(array[i], context, visited, stackDepth);
					}
				}
			}
			result = array;
		}

		if (visited != null) {
			visited.put(origFieldValue, result);
		}

		@SuppressWarnings("unchecked")
		final T castResult = (T) result;
		return castResult;
	}

	/**
	 * Clone a Field
	 * @param orig The original object
	 * @param copy The destination object
	 * @param driver The CloneDriver
	 * @param f The FieldModel for the target field
	 * @param referencesToReuse Used for tracking objects that have already been seen
	 * @param stackDepth The current depth of the stack - used to switch from recursion to iteration if the stack grows too deep.
	 */
	protected <T> void handleCloneField(T obj, T copy, CloneDriver driver, FieldModel<T> f, IdentityHashMap<Object, Object> referencesToReuse, long stackDepth) {

		final Class<?> clazz = f.getFieldClass();

		if (clazz.isPrimitive()) {
			handleClonePrimitiveField(obj, copy, driver, f, referencesToReuse);
		} else if (!driver.isCloneSyntheticFields() && f.isSynthetic()) {
			putFieldValue(copy, f, getFieldValue(obj, f));
		} else {

			Object fieldObject;
			fieldObject = getFieldValue(obj, f);
			final Object fieldObjectClone = clone(fieldObject, driver, referencesToReuse, stackDepth);
			putFieldValue(copy, f, fieldObjectClone);
		}
	}

	/**
	 * Implementations should ensure that transient fields are left with the correct default (unset) value
	 * @param copy The target object
	 * @param f The FieldModel for the Field that should stay as a default
	 */
	protected abstract <T> void handleTransientField(T copy, FieldModel<T> f);

	/**
	 * Method should clone the given primitive field
	 * @param obj Source object
	 * @param copy The target object
	 * @param driver The CloneDriver to use
	 * @param f The FieldModel for the Field that should stay as a default
	 * @param referencesToReuse Used for tracking objects that have already been seen
	 */
	protected abstract <T> void handleClonePrimitiveField(T obj, T copy, CloneDriver driver, FieldModel<T> f, IdentityHashMap<Object, Object> referencesToReuse);

	/**
	 * Method to retrieve the value of a particular field
	 * @param obj Source object
	 * @param f The FieldModel for the Field that should stay as a default
	 * @return The value in the given field
	 */
	protected abstract <T> Object getFieldValue(T obj, FieldModel<T> f);

	/**
	 * Put the given value into the target field
	 * @param obj Source object
	 * @param f The FieldModel for the Field that should stay as a default
	 * @param value The value to put
	 */
	protected abstract <T> void putFieldValue(T obj, FieldModel<T> f, Object value);

	@Override
	public void initialiseFor(Class<?>... classes) {

		IdentityHashMap<Class<?>, Boolean> seenClasses = new IdentityHashMap<Class<?>, Boolean>(classes.length * 10);

		for (Class<?> clazz : classes) {
			doInitialiseFor(clazz, seenClasses);
		}
	}

	private void doInitialiseFor(Class<?> clazz, IdentityHashMap<Class<?>, Boolean> seenClasses) {

		getClassModel(clazz);
		seenClasses.put(clazz, Boolean.TRUE);

		Field[] fields = ClassUtils.collectInstanceFields(clazz);
		for (Field f : fields) {

			Class<?> type = f.getType();

			if (seenClasses.containsKey(type)) {
				continue;
			}

			if (type.isPrimitive()) {
				continue;
			} else if (type.isArray() && !(type.getComponentType().isPrimitive())) {
				doInitialiseFor(type.getComponentType(), seenClasses);
				seenClasses.put(type.getComponentType(), Boolean.TRUE);
			} else if (!type.isArray() && !type.isPrimitive() && !type.isEnum() && !type.isInterface() && !ClassUtils.isWrapper(type) && !ClassUtils.isJdkImmutable(type)) {
				doInitialiseFor(type, seenClasses);
				seenClasses.put(type, Boolean.TRUE);
			}
		}
	}

	private class WorkItem {

		private final Object source;
		private final Object target;
		private final FieldModel<Object> fieldModel;

		public WorkItem(Object source, Object target, FieldModel<Object> fieldModel) {
			this.source = source;
			this.target = target;
			this.fieldModel = fieldModel;
		}

		public Object getSource() {
			return source;
		}

		public Object getTarget() {
			return target;
		}

		private FieldModel<Object> getFieldModel() {
			return fieldModel;
		}
	}
}
