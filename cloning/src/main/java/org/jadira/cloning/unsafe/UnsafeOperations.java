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
package org.jadira.cloning.unsafe;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.IdentityHashMap;

import org.jadira.cloning.model.UnsafeClassModel;
import org.jadira.cloning.model.UnsafeFieldModel;
import org.jadira.cloning.portable.ClassUtils;
import org.jadira.cloning.portable.FieldType;

/**
 * A set of utility methods for working with sun.misc.Unsafe. Address shallow and deep copying,
 * field access and field manipulation.
 */
@SuppressWarnings("restriction")
public final class UnsafeOperations {

    private static final sun.misc.Unsafe THE_UNSAFE;
    private static final boolean IS_UNSAFE_AVAILABLE;

    private static final UnsafeOperations INSTANCE = new UnsafeOperations();

    static {
        boolean isUnsafeAvailable = true;
        sun.misc.Unsafe theUnsafe = null;
        try {
            Class.forName("android.os.Process");
            isUnsafeAvailable = false;

        } catch (ClassNotFoundException e) {
            // Ignored
        } finally {

            if (isUnsafeAvailable) {
                try {
                    Field f = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
                    f.setAccessible(true);
                    theUnsafe = (sun.misc.Unsafe) f.get(null);
                } catch (ClassNotFoundException e) {
                    isUnsafeAvailable = false;
                } catch (IllegalArgumentException e) {
                    isUnsafeAvailable = false;
                } catch (IllegalAccessException e) {
                    isUnsafeAvailable = false;
                } catch (NoSuchFieldException e) {
                    isUnsafeAvailable = false;
                } catch (SecurityException e) {
                    isUnsafeAvailable = false;
                }
            }
        }
        IS_UNSAFE_AVAILABLE = isUnsafeAvailable;
        THE_UNSAFE = theUnsafe;
    }

    private UnsafeOperations() {
    }

    public static final UnsafeOperations getUnsafeOperations() {
        if (isUnsafeAvailable()) {
            return INSTANCE;
        } else {
            throw new IllegalStateException("Unsafe is not available");
        }
    }

    public static boolean isUnsafeAvailable() {
        return IS_UNSAFE_AVAILABLE;
    }

    public final <T> T allocateInstance(Class<T> clazz) throws IllegalStateException {

        try {
            @SuppressWarnings("unchecked")
            final T result = (T) THE_UNSAFE.allocateInstance(clazz);
            return result;
        } catch (InstantiationException e) {
            throw new IllegalStateException("Cannot allocate instance: " + e.getMessage(), e);
        }
    }

    public final long getObjectFieldOffset(Field f) {
        return THE_UNSAFE.objectFieldOffset(f);
    }

    public final <T> T shallowCopy(T obj) {
        long size = shallowSizeOf(obj);
        long address = THE_UNSAFE.allocateMemory(size);
        long start = toAddress(obj);
        THE_UNSAFE.copyMemory(start, address, size);

        @SuppressWarnings("unchecked")
        final T result = (T) fromAddress(address);
        return result;
    }

    public final long toAddress(Object obj) {
        Object[] array = new Object[] { obj };
        long baseOffset = THE_UNSAFE.arrayBaseOffset(Object[].class);
        return normalize(THE_UNSAFE.getInt(array, baseOffset));
    }

    public final Object fromAddress(long address) {
        Object[] array = new Object[] { null };
        long baseOffset = THE_UNSAFE.arrayBaseOffset(Object[].class);
        THE_UNSAFE.putLong(array, baseOffset, address);
        return array[0];
    }

    public final void copyPrimitiveField(Object obj, Object copy, Field field) {
        copyPrimitiveAtOffset(obj, copy, field.getType(), getObjectFieldOffset(field));
    }

    public final void copyPrimitiveAtOffset(Object obj, Object copy, Class<?> type, long offset) {

        if (java.lang.Boolean.TYPE.isAssignableFrom(type)) {
            boolean origFieldValue = THE_UNSAFE.getBoolean(obj, offset);
            THE_UNSAFE.putBoolean(copy, offset, origFieldValue);
        } else if (java.lang.Byte.TYPE.isAssignableFrom(type)) {
            byte origFieldValue = THE_UNSAFE.getByte(obj, offset);
            THE_UNSAFE.putByte(copy, offset, origFieldValue);
        } else if (java.lang.Character.TYPE.isAssignableFrom(type)) {
            char origFieldValue = THE_UNSAFE.getChar(obj, offset);
            THE_UNSAFE.putChar(copy, offset, origFieldValue);
        } else if (java.lang.Short.TYPE.isAssignableFrom(type)) {
            short origFieldValue = THE_UNSAFE.getShort(obj, offset);
            THE_UNSAFE.putShort(copy, offset, origFieldValue);
        } else if (java.lang.Integer.TYPE.isAssignableFrom(type)) {
            int origFieldValue = THE_UNSAFE.getInt(obj, offset);
            THE_UNSAFE.putInt(copy, offset, origFieldValue);
        } else if (java.lang.Long.TYPE.isAssignableFrom(type)) {
            long origFieldValue = THE_UNSAFE.getLong(obj, offset);
            THE_UNSAFE.putLong(copy, offset, origFieldValue);
        } else if (java.lang.Float.TYPE.isAssignableFrom(type)) {
            float origFieldValue = THE_UNSAFE.getFloat(obj, offset);
            THE_UNSAFE.putFloat(copy, offset, origFieldValue);
        } else if (java.lang.Double.TYPE.isAssignableFrom(type)) {
            double origFieldValue = THE_UNSAFE.getDouble(obj, offset);
            THE_UNSAFE.putDouble(copy, offset, origFieldValue);
        }
    }

    public final void putPrimitiveDefaultAtOffset(Object copy, Class<?> type, long offset) {

        if (java.lang.Boolean.TYPE.isAssignableFrom(type)) {
            THE_UNSAFE.putBoolean(copy, offset, false);
        } else if (java.lang.Byte.TYPE.isAssignableFrom(type)) {
            THE_UNSAFE.putByte(copy, offset, (byte) 0);
        } else if (java.lang.Character.TYPE.isAssignableFrom(type)) {
            THE_UNSAFE.putChar(copy, offset, '\u0000');
        } else if (java.lang.Short.TYPE.isAssignableFrom(type)) {
            THE_UNSAFE.putShort(copy, offset, (short) 0);
        } else if (java.lang.Integer.TYPE.isAssignableFrom(type)) {
            THE_UNSAFE.putInt(copy, offset, 0);
        } else if (java.lang.Long.TYPE.isAssignableFrom(type)) {
            THE_UNSAFE.putLong(copy, offset, 0L);
        } else if (java.lang.Float.TYPE.isAssignableFrom(type)) {
            THE_UNSAFE.putFloat(copy, offset, 0.0f);
        } else if (java.lang.Double.TYPE.isAssignableFrom(type)) {
            THE_UNSAFE.putDouble(copy, offset, 0.0d);
        }
    }

    public <T> T deepCopy(final T obj) {
        return deepCopy(obj, new IdentityHashMap<Object, Object>(10));
    }

    public <T> T deepCopy(final T obj2, IdentityHashMap<Object, Object> referencesToReuse) {

        /**
         * To avoid unnecessary recursion and potential stackoverflow errors, we use an internal stack
         */
        Deque<WorkItem> stack = new ArrayDeque<WorkItem>();

        Object objectInput;

        T parentOutput = null;
        WorkItem nextWork = null;
        
        do {
            Object objectResult;

            if (nextWork == null) {
                objectInput = obj2;
            } else {
                objectInput = getObject(nextWork.getSource(), nextWork.getOffset());
            }

    		if (objectInput == null) {
    		    
    			objectResult = null;
    			
    		} else {
    
    		    Class<?> clazz = objectInput.getClass();
    
    		    if (clazz.isPrimitive()) {
    		        objectResult = objectInput;
    		    } else if (clazz.isArray()) {
        			objectResult = deepCopyArray(objectInput, referencesToReuse);
        		} else if (ClassUtils.isJdkImmutable(clazz) || ClassUtils.isWrapper(clazz) || clazz.isEnum()) {
					objectResult = objectInput;
    		    } else if (referencesToReuse.containsKey(objectInput)) {
        			objectResult = referencesToReuse.get(objectInput);
        		} else {
        
            		UnsafeClassModel model = UnsafeClassModel.get(objectInput.getClass());
            
                    objectResult = allocateInstance(objectInput.getClass());
            		referencesToReuse.put(objectInput, objectResult);
            
            		for (UnsafeFieldModel f : model.getModelFields()) {
            
            			if (f.getFieldType() == FieldType.PRIMITIVE) {
            				copyPrimitiveAtOffset(objectInput, objectResult, f.getFieldClass(), f.getOffset());
            			} else if (f.getFieldType() == FieldType.ARRAY) {
            				deepCopyArrayAtOffset(objectInput, objectResult, f.getFieldClass(), f.getOffset(), referencesToReuse);
            			} else {
            			    stack.addFirst(new WorkItem(objectInput, objectResult, f.getOffset()));
            			}
            		}
        		}
    		}
    		
            if (nextWork == null) {
                @SuppressWarnings("unchecked")
                final T convertedResult = (T) objectResult;
                parentOutput = convertedResult;
            } else {
                
                if (objectResult == null) {

                    putNullObject(nextWork.getTarget(), nextWork.getOffset());
                } else {
                    putObject(nextWork.getTarget(), nextWork.getOffset(), objectResult);
                }
            }
        } while ((nextWork = stack.pollFirst()) != null);

        return parentOutput;
	}

    public final void deepCopyObjectAtOffset(Object obj, Object copy, Class<?> fieldClass, long offset) {
        deepCopyObjectAtOffset(obj, copy, fieldClass, offset, new IdentityHashMap<Object, Object>(100));
    }

    public final void deepCopyObjectAtOffset(Object obj, Object copy, Class<?> fieldClass, long offset,
            IdentityHashMap<Object, Object> referencesToReuse) {

        Object origFieldValue = THE_UNSAFE.getObject(obj, offset);

        if (origFieldValue == null) {

            putNullObject(copy, offset);
        } else {

            Class<?> clazz = origFieldValue.getClass();

            final Object copyFieldValue;
            if (ClassUtils.isJdkImmutable(clazz) || ClassUtils.isWrapper(clazz)) {
                copyFieldValue = origFieldValue;
            } else {
                copyFieldValue = deepCopy(origFieldValue, referencesToReuse);
            }

            UnsafeOperations.THE_UNSAFE.putObject(copy, offset, copyFieldValue);
        }
    }

    public final void deepCopyObjectField(Object obj, Object copy, Field field,
            IdentityHashMap<Object, Object> referencesToReuse) {

        deepCopyObjectAtOffset(obj, copy, field.getType(), getObjectFieldOffset(field), referencesToReuse);
    }

    public final void deepCopyObjectField(Object obj, Object copy, Field field) {

        deepCopyObjectAtOffset(obj, copy, field.getType(), getObjectFieldOffset(field),
                new IdentityHashMap<Object, Object>(100));
    }

    public final void deepCopyArrayAtOffset(Object obj, Object copy, Class<?> fieldClass, long offset) {
        deepCopyArrayAtOffset(obj, copy, fieldClass, offset, new IdentityHashMap<Object, Object>(100));
    }

    public final void deepCopyArrayAtOffset(Object obj, Object copy, Class<?> fieldClass, long offset,
            IdentityHashMap<Object, Object> referencesToReuse) {

        Object origFieldValue = THE_UNSAFE.getObject(obj, offset);

        if (origFieldValue == null) {

            putNullObject(copy, offset);
        } else {

            final Object copyFieldValue = deepCopyArray(origFieldValue, referencesToReuse);
            UnsafeOperations.THE_UNSAFE.putObject(copy, offset, copyFieldValue);
        }
    }

    public final void deepCopyArrayField(Object obj, Object copy, Field field,
            IdentityHashMap<Object, Object> referencesToReuse) {

        deepCopyArrayAtOffset(obj, copy, field.getType(), getObjectFieldOffset(field), referencesToReuse);
    }

    public final void deepCopyArrayField(Object obj, Object copy, Field field) {

        deepCopyArrayAtOffset(obj, copy, field.getType(), getObjectFieldOffset(field),
                new IdentityHashMap<Object, Object>(100));
    }

    public final Object deepCopyArray(Object origFieldValue, IdentityHashMap<Object, Object> visited) {

        if (visited.containsKey(origFieldValue)) {
            return visited.get(origFieldValue);
        }

        final Class<?> componentType = origFieldValue.getClass().getComponentType();

        Object result = null;

        if (componentType.getName().length() <= 7) {

            if (java.lang.Boolean.TYPE.isAssignableFrom(componentType)) {
                result = Arrays.copyOf((boolean[]) origFieldValue, ((boolean[]) origFieldValue).length);
            } else if (java.lang.Byte.TYPE.isAssignableFrom(componentType)) {
                result = Arrays.copyOf((byte[]) origFieldValue, ((byte[]) origFieldValue).length);
            } else if (java.lang.Character.TYPE.isAssignableFrom(componentType)) {
                result = Arrays.copyOf((char[]) origFieldValue, ((char[]) origFieldValue).length);
            } else if (java.lang.Short.TYPE.isAssignableFrom(componentType)) {
                result = Arrays.copyOf((short[]) origFieldValue, ((short[]) origFieldValue).length);
            } else if (java.lang.Integer.TYPE.isAssignableFrom(componentType)) {
                result = Arrays.copyOf((int[]) origFieldValue, ((int[]) origFieldValue).length);
            } else if (java.lang.Long.TYPE.isAssignableFrom(componentType)) {
                result = Arrays.copyOf((long[]) origFieldValue, ((long[]) origFieldValue).length);
            } else if (java.lang.Float.TYPE.isAssignableFrom(componentType)) {
                result = Arrays.copyOf((float[]) origFieldValue, ((float[]) origFieldValue).length);
            } else if (java.lang.Double.TYPE.isAssignableFrom(componentType)) {
                result = Arrays.copyOf((double[]) origFieldValue, ((double[]) origFieldValue).length);
            }
        }

        if (result == null) {
            Object[] array = Arrays.copyOf((Object[]) origFieldValue, ((Object[]) origFieldValue).length);
            if (array.length > 0) {

                if (componentType.isArray()) {
                    for (int i = 0; i < array.length; i++) {
                        array[i] = deepCopyArray(array[i], visited);
                    }
                } else {
                    for (int i = 0; i < array.length; i++) {
                        Object component = deepCopy(((Object[])origFieldValue)[i], visited);
                        array[i] = component;
                    }
                }
            }
            result = array;
        }

        visited.put(origFieldValue, result);
        return result;
    }

    public final long shallowSizeOf(Object obj) {
        return shallowSizeOf(obj.getClass());
    }

    public final long shallowSizeOf(Class<?> clazz) {

        if (clazz == null) {
            return 0;
        }

        Field[] fields = ClassUtils.collectFields(clazz);

        // get offset
        long maxSize = 0;
        for (Field f : fields) {
            long offset = THE_UNSAFE.objectFieldOffset(f);
            if (offset > maxSize) {
                maxSize = offset;
            }
        }

        return ((maxSize / 8) + 1) * 8; // padding
    }

    public final long deepSizeOf(Object o) {

        IdentityHashMap<Object, Boolean> seenObjects = new IdentityHashMap<Object, Boolean>(10);
        return doDeepSizeOf(o, seenObjects);
    }

    private long doDeepSizeOf(Object o2, IdentityHashMap<Object, Boolean> seenObjects) {

        /**
         * To avoid unnecessary recursion and potential stackoverflow errors, we use an internal stack
         */
        Deque<WorkItem> stack = new ArrayDeque<WorkItem>();

        Object objectInput;

        long bytesCount = 0L;
        long arrayItemsCount = 0L;
        WorkItem nextWork = null;
        
        do {

            if (nextWork == null) {
                objectInput = o2;
            } else {
                objectInput = getObject(nextWork.getSource(), nextWork.getOffset());
            }

            if (objectInput != null) {
            
                long size = shallowSizeOf(objectInput);
        
                Field[] fields = ClassUtils.collectFields(objectInput.getClass());
        
                if (objectInput.getClass().isArray() && !objectInput.getClass().getComponentType().isPrimitive()) {
                    Object[] objectArray = (Object[])objectInput;
                    for (int i=0; i< objectArray.length; i++) {
                        if (objectArray[i] != null && !seenObjects.containsKey(objectArray[i])) {
                            seenObjects.put(objectArray[i], Boolean.TRUE);
                            arrayItemsCount += doDeepSizeOf(objectArray[i], seenObjects);
                        }
                    }                    
                }
                for (Field f : fields) {
                    if (!f.getType().isPrimitive()) {
                        long itemOffset = THE_UNSAFE.objectFieldOffset(f);
                        Object item = getObject(objectInput, itemOffset);
                        if (item != null && !seenObjects.containsKey(item)) {
                            seenObjects.put(item, Boolean.TRUE);
                            stack.addFirst(new WorkItem(objectInput, item, itemOffset));
                        }
                    }
                }
                bytesCount += (((size / 8) + 1) * 8); // padding
            }
        } while ((nextWork = stack.pollFirst()) != null);

        return bytesCount + arrayItemsCount;
    }

    private static long normalize(int value) {
        if (value >= 0) {
            return value;
        }
        return (~0L >>> 32) & value;
    }

    public final Object getObject(Object parent, long offset) {
        return THE_UNSAFE.getObject(parent, offset);
    }

    public final void putNullObject(Object parent, long offset) {
        THE_UNSAFE.putObject(parent, offset, null);
    }

    public final void putObject(Object parent, long offset, Object value) {
        THE_UNSAFE.putObject(parent, offset, value);
    }

    private class WorkItem {

        private final Object source;
        private final Object target;
        private final long offset;

        protected WorkItem(Object source, Object target, long offset) {
            this.source = source;
            this.target = target;
            this.offset = offset;
        }

        public Object getSource() {
            return source;
        }

        public Object getTarget() {
            return target;
        }

        private long getOffset() {
            return offset;
        }
    }
}
