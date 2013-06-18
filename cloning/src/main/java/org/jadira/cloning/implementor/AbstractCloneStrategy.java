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
package org.jadira.cloning.implementor;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.IdentityHashMap;

import org.jadira.cloning.MinimalCloner;
import org.jadira.cloning.api.CloneDriver;
import org.jadira.cloning.api.CloneImplementor;
import org.jadira.cloning.api.CloneStrategy;
import org.jadira.cloning.portable.ClassUtils;
import org.jadira.cloning.spi.ClassModel;
import org.jadira.cloning.spi.FieldModel;

public abstract class AbstractCloneStrategy<P extends ClassModel<F>, F extends FieldModel> implements CloneStrategy {

	@Override
	public abstract <T> T newInstance(Class<T> c);

	@Override
	public boolean canClone(Class<?> clazz) {
		return true;
	}

	@Override
	public <T> T clone(T obj, CloneDriver context, IdentityHashMap<Object, Object> referencesToReuse) {

		if (obj == null) {
			return null;
		}

		if (context.isImmutableInstance(obj)) {
			return obj;
		}

		@SuppressWarnings("unchecked")
		final Class<T> clazz = (Class<T>) obj.getClass();

		if (clazz.isPrimitive() || clazz.isEnum()) {
			return obj;
		}

		if (ClassUtils.isJdkImmutable(clazz) || ClassUtils.isWrapper(clazz) || context.getImmutableClasses().contains(clazz) || context.getNonCloneableClasses().contains(clazz)) {
			return obj;
		}

		@SuppressWarnings("unchecked")
		final T result = (T) referencesToReuse.get(obj);
		if (result != null) {
			return result;
		}

		final CloneImplementor cloneImplementor;
		if (context.isUseCloneImplementors()) {
			cloneImplementor = context.getImplementor(clazz);
		} else {
			cloneImplementor = context.getBuiltInImplementor(clazz);
		}
		if (cloneImplementor != null) {
			T copy = cloneImplementor.clone(obj, context, referencesToReuse);
			referencesToReuse.put(obj, copy);
			return copy;
		}

		P model = getClassModel(obj.getClass());

		if (model.isDetectedAsImmutable() || model.isNonCloneable()) {
			return obj;
		}

		final org.jadira.cloning.annotation.Cloneable cloneableAnnotation = clazz.getAnnotation(org.jadira.cloning.annotation.Cloneable.class);
		if (cloneableAnnotation != null && !void.class.equals(cloneableAnnotation.implementor())) {
			final T copy = handleCloneImplementor(obj, context, referencesToReuse, clazz, cloneableAnnotation);
			referencesToReuse.put(obj, copy);
			return result;
		}

		if (model.getCloneImplementor() != null) {
			final T copy = model.getCloneImplementor().clone(obj, context, referencesToReuse);
			referencesToReuse.put(obj, copy);
			return result;
		}

		if (context.isUseCloneable() && Cloneable.class.isAssignableFrom(clazz)) {
			final T copy = handleCloneableCloneMethod(obj, context, referencesToReuse, clazz, cloneableAnnotation);
			referencesToReuse.put(obj, copy);
			return result;
		}

		if (clazz.isArray()) {
			final T copy = handleArray(obj, context, referencesToReuse);
			referencesToReuse.put(obj, copy);
			return copy;
		}

		final T copy = newInstance(clazz);
		referencesToReuse.put(obj, copy);

		for (F f : model.getModelFields()) {

			if (!context.isCloneTransientFields() && f.isTransientField()) {
				handleTransientField(copy, f);
			} else if (!context.isCloneTransientAnnotatedFields() && f.isTransientAnnotatedField()) {
				handleTransientField(copy, f);
			} else {
				handleCloneField(obj, copy, context, f, referencesToReuse);
			}
		}

		return copy;
	}

	private <T> T handleCloneImplementor(T obj, CloneDriver context, IdentityHashMap<Object, Object> referencesToReuse, final Class<T> clazz,
			org.jadira.cloning.annotation.Cloneable cloneableAnnotation) {

		CloneImplementor cloneImplementor = context.getAnnotationImplementor(clazz);
		if (cloneImplementor == null) {
			cloneImplementor = (CloneImplementor) newInstance(cloneableAnnotation.implementor());
			context.putAnnotationImplementor(clazz, cloneImplementor);
		}
		if (MinimalCloner.class.equals(cloneImplementor.getClass())) {
			T copy = cloneImplementor.clone(obj, (MinimalCloner) cloneImplementor, referencesToReuse);
			referencesToReuse.put(obj, copy);
			return copy;
		} else {
			T copy = cloneImplementor.clone(obj, context, referencesToReuse);
			referencesToReuse.put(obj, copy);
			return copy;
		}
	}

	private <T> T handleCloneableCloneMethod(T obj, CloneDriver context, IdentityHashMap<Object, Object> referencesToReuse, final Class<T> clazz,
			org.jadira.cloning.annotation.Cloneable cloneableAnnotation) {

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
	 * 
	 * @param object The object to be cloned.
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

	protected abstract P getClassModel(Class<?> clazz);

    protected <T> T handleArray(T origFieldValue, CloneDriver context, IdentityHashMap<Object, Object> visited) {

        if (visited.containsKey(origFieldValue)) {
            
            @SuppressWarnings("unchecked")
            final T castResult = (T) visited.get(origFieldValue);
            return castResult;
        }

        final Class<?> componentType = origFieldValue.getClass().getComponentType();

        Object result = null;

        if (componentType.getName().length() <= 7) {

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
                        array[i] = handleArray(array[i], context, visited);
                    }
                } else {
                    for (int i = 0; i < array.length; i++) {
                        array[i] = clone(array[i], context, visited);
                    }
                }
            }
            result = array;
        }

        visited.put(origFieldValue, result);

        @SuppressWarnings("unchecked")
        final T castResult = (T)result;
        return castResult;
    }

	protected abstract <T> void handleTransientField(T copy, F f);

	protected abstract <T> void handleCloneField(T obj, T copy, CloneDriver driver, F f, IdentityHashMap<Object, Object> referencesToReuse);
	
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

        Field[] fields = ClassUtils.collectFields(clazz);
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
            } else if (!type.isArray() && !type.isPrimitive() && !type.isEnum() && !type.isInterface()
                    && !ClassUtils.isWrapper(type) && !ClassUtils.isJdkImmutable(type)) {
                doInitialiseFor(type, seenClasses);
                seenClasses.put(type, Boolean.TRUE);
            }
        }
    }
}
