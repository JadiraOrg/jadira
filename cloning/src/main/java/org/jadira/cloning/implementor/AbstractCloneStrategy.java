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
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.IdentityHashMap;

import org.jadira.cloning.MinimalCloner;
import org.jadira.cloning.api.CloneDriver;
import org.jadira.cloning.api.CloneImplementor;
import org.jadira.cloning.api.CloneStrategy;
import org.jadira.cloning.portable.ClassUtils;
import org.jadira.cloning.portable.FieldType;
import org.jadira.cloning.spi.ClassModel;
import org.jadira.cloning.spi.FieldModel;

public abstract class AbstractCloneStrategy<P extends ClassModel<F>, F extends FieldModel> implements CloneStrategy {

	private static final int REFERENCE_STACK_LIMIT = 150;
    
    @Override
    public abstract <T> T newInstance(Class<T> c);

    @Override
    public boolean canClone(Class<?> clazz) {
        return true;
    }

    @Override
    public <T> T clone(T obj, CloneDriver context, IdentityHashMap<Object, Object> referencesToReuse) {

        /**
         * To avoid unnecessary recursion and potential stackoverflow errors, we use an internal stack
         */

        final Deque<WorkItem> stack;
        if (REFERENCE_STACK_LIMIT <= referencesToReuse.size()) {
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
                objectInput = getFieldValue(nextWork.getSource(), nextWork.getFieldModel(), referencesToReuse);
            }

            if (objectInput == null) {
                objectResult = null;
            }

            else if (context.isImmutableInstance(objectInput)) {
                objectResult = objectInput;
            }

            else {
                objectResult = doCloneStep(objectInput, context, referencesToReuse, stack);
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

	private Object doCloneStep(Object objectInput, CloneDriver context, IdentityHashMap<Object, Object> referencesToReuse, final Deque<WorkItem> stack) {
		Object objectResult;
		@SuppressWarnings("unchecked")
		final Class<Object> clazz = (Class<Object>) objectInput.getClass();

		if (clazz.isPrimitive() || clazz.isEnum()) {
		    objectResult = objectInput;
		} else if (ClassUtils.isJdkImmutable(clazz) || ClassUtils.isWrapper(clazz)
		        || context.getImmutableClasses().contains(clazz)
		        || context.getNonCloneableClasses().contains(clazz)) {
		    objectResult = objectInput;
		} else {

		    final Object result = referencesToReuse.get(objectInput);
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
		            Object copy = cloneImplementor.clone(objectInput, context, referencesToReuse);
		            referencesToReuse.put(objectInput, copy);
		            objectResult = copy;
		        } else {

		            P model = getClassModel(objectInput.getClass());

		            if (model.isDetectedAsImmutable() || model.isNonCloneable()) {
		                objectResult = objectInput;
		            } else {

		                final org.jadira.cloning.annotation.Cloneable cloneableAnnotation = clazz
		                        .getAnnotation(org.jadira.cloning.annotation.Cloneable.class);
		                if (cloneableAnnotation != null
		                        && !void.class.equals(cloneableAnnotation.implementor())) {
		                    final Object copy = handleCloneImplementor(objectInput, context, referencesToReuse,
		                            clazz, cloneableAnnotation);
		                    referencesToReuse.put(objectInput, copy);
		                    objectResult = copy;
		                }

		                else if (model.getCloneImplementor() != null) {
		                    final Object copy = model.getCloneImplementor().clone(objectInput, context,
		                            referencesToReuse);
		                    referencesToReuse.put(objectInput, copy);
		                    objectResult = copy;
		                } else if (context.isUseCloneable() && Cloneable.class.isAssignableFrom(clazz)) {
		                    final Object copy = handleCloneableCloneMethod(objectInput, context,
		                            referencesToReuse, clazz, cloneableAnnotation);
		                    referencesToReuse.put(objectInput, copy);
		                    objectResult = copy;
		                } else if (clazz.isArray()) {
		                    final Object copy = handleArray(objectInput, context, referencesToReuse);
		                    referencesToReuse.put(objectInput, copy);
		                    objectResult = copy;
		                } else {

		                    objectResult = newInstance(clazz);
		                    referencesToReuse.put(objectInput, objectResult);

		                    for (F f : model.getModelFields()) {

		                        if (!context.isCloneTransientFields() && f.isTransientField()) {
		                            handleTransientField(objectResult, f);
		                        } else if (!context.isCloneTransientAnnotatedFields()
		                                && f.isTransientAnnotatedField()) {
		                            handleTransientField(objectResult, f);
		                        } else {
		                            if (stack == null) {
		                                handleCloneField(objectInput, objectResult, context, f,
		                                        referencesToReuse);
		                            } else {
		                                if (f.getFieldType() == FieldType.PRIMITIVE) {
		                                    handleClonePrimitiveField(objectInput, objectResult, context, f,
		                                            referencesToReuse);
		                                } else {
		                                    if (!context.isCloneSyntheticFields() && f.isSynthetic()) {
		                                        Object fieldObject = getFieldValue(objectInput, f,
		                                                referencesToReuse);
		                                        referencesToReuse.put(fieldObject, fieldObject);
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

    private <T> T handleCloneImplementor(T obj, CloneDriver context, IdentityHashMap<Object, Object> referencesToReuse,
            final Class<T> clazz, org.jadira.cloning.annotation.Cloneable cloneableAnnotation) {

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

    private <T> T handleCloneableCloneMethod(T obj, CloneDriver context,
            IdentityHashMap<Object, Object> referencesToReuse, final Class<T> clazz,
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
        final T castResult = (T) result;
        return castResult;
    }

    protected <T> void handleCloneField(T obj, T copy, CloneDriver driver, F f,
            IdentityHashMap<Object, Object> referencesToReuse) {

        final Class<?> clazz = f.getFieldClass();
        
        if (clazz.isPrimitive()) {
            handleClonePrimitiveField(obj, copy, driver, f, referencesToReuse);
        } else if (!driver.isCloneSyntheticFields() && f.isSynthetic()) {
            putFieldValue(copy, f, getFieldValue(obj, f, referencesToReuse));
        } else {

            Object fieldObject;
            fieldObject = getFieldValue(obj, f, referencesToReuse);
            final Object fieldObjectClone = clone(fieldObject, driver, referencesToReuse);
            putFieldValue(copy, f, fieldObjectClone);
        }
    }
    
    protected abstract <T> void handleTransientField(T copy, F f);

    protected abstract <T> void handleClonePrimitiveField(T obj, T copy, CloneDriver driver, F f,
            IdentityHashMap<Object, Object> referencesToReuse);

    protected abstract <T> Object getFieldValue(T obj, F f, IdentityHashMap<Object, Object> referencesToReuse);

    protected abstract <T> void putFieldValue(T obj, F f, Object value);

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

    private class WorkItem {

        private final Object source;
        private final Object target;
        private final F fieldModel;

        public WorkItem(Object source, Object target, F fieldModel) {
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

        private F getFieldModel() {
            return fieldModel;
        }
    }
}
