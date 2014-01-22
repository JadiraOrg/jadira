/*
 *  Copyright 2013 Christopher Pheby
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
package org.jadira.reflection.access.unsafe;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.IdentityHashMap;

import org.jadira.reflection.core.misc.ClassUtils;

/**
 * A set of utility methods for working with sun.misc.Unsafe. Address shallow and deep copying,
 * field access and field manipulation.
 */
@SuppressWarnings("restriction")
public final class UnsafeOperations {

	private static final int REFERENCE_STACK_LIMIT = 150;

	private static final int SIZE_BYTES_BOOLEAN = 1;
	private static final int SIZE_BYTES_BYTE = 1;
	private static final int SIZE_BYTES_CHAR = 2;
	private static final int SIZE_BYTES_SHORT = 2;
	private static final int SIZE_BYTES_INT = 4;
	private static final int SIZE_BYTES_LONG = 8;
	private static final int SIZE_BYTES_FLOAT = 4;
	private static final int SIZE_BYTES_DOUBLE = 8;

	/**
	 * The size of a page that an object will be placed in (always 8 bytes currently) (NB for
	 * HotSpot can be retrieved using ObjectAlignmentInBytes in HotSpotDiagnosticMXBean, but
	 * as this is always 8 for existing JVMs this is hardcoded).
	 */
	private static final int SIZE_BYTES_PAGE_FOR_OBJECT_ALIGNMENT = 8;

	private static final int MIN_SIZE = 16;

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

	/**
	 * Returns the (singleton) UnsafeOperations instance
	 * @return UnsafeOperations
	 */
	public static final UnsafeOperations getUnsafeOperations() {
		if (isUnsafeAvailable()) {
			return INSTANCE;
		} else {
			throw new IllegalStateException("Unsafe is not available");
		}
	}

	/**
	 * Check whether the Unsafe API is accessible
	 * @return True if available
	 */
	public static boolean isUnsafeAvailable() {
		return IS_UNSAFE_AVAILABLE;
	}

	/**
	 * Construct and allocate on the heap an instant of the given class, without calling the class constructor
	 * @param clazz Class to create instant for
	 * @return The new instance
	 * @throws IllegalStateException Indicates a problem occurred
	 */
	public final <T> T allocateInstance(Class<T> clazz) throws IllegalStateException {

		try {
			@SuppressWarnings("unchecked")
			final T result = (T) THE_UNSAFE.allocateInstance(clazz);
			return result;
		} catch (InstantiationException e) {
			throw new IllegalStateException("Cannot allocate instance: " + e.getMessage(), e);
		}
	}

	/**
	 * Gets an offset for the given field relative to the field base. Any particular field will always have the 
	 * same offset, and no two distinct fields of the same class will ever have the same offset.
	 * @param f The Field to determine the offset for
	 * @return The offset represented as a long
	 */
	public final long getObjectFieldOffset(Field f) {
		return THE_UNSAFE.objectFieldOffset(f);
	}

	/**
	 * Performs a shallow copy of the given object - a new instance is allocated with the same contents. Any object
	 * references inside the copy will be the same as the original object.
	 * @param obj Object to copy
	 * @return A new instance, identical to the original
	 */
	public final <T> T shallowCopy(T obj) {
		long size = shallowSizeOf(obj);
		long address = THE_UNSAFE.allocateMemory(size);
		long start = toAddress(obj);
		THE_UNSAFE.copyMemory(start, address, size);

		@SuppressWarnings("unchecked")
		final T result = (T) fromAddress(address);
		return result;
	}

	/**
	 * Convert the object reference to a memory address represented as a signed long
	 * @param obj The object
	 * @return A long representing the address of the object
	 */
	public final long toAddress(Object obj) {
		Object[] array = new Object[] { obj };
		long baseOffset = THE_UNSAFE.arrayBaseOffset(Object[].class);
		return normalize(THE_UNSAFE.getInt(array, baseOffset));
	}

	/**
	 * Returns the object located at the given memory address
	 * @param address The address (a signed long) for the object
	 * @return The Object at the given address
	 */
	public final Object fromAddress(long address) {
		Object[] array = new Object[] { null };
		long baseOffset = THE_UNSAFE.arrayBaseOffset(Object[].class);
		THE_UNSAFE.putLong(array, baseOffset, address);
		return array[0];
	}

	/**
	 * Copy the value from the given field from the source into the target.
	 * The field specified must contain a primitive
	 * @param source The object to copy from
	 * @param copy The target object
	 * @param field Field to be copied
	 */
	public final void copyPrimitiveField(Object source, Object copy, Field field) {
		copyPrimitiveAtOffset(source, copy, field.getType(), getObjectFieldOffset(field));
	}

	/**
	 * Copies the primitive of the specified type from the given field offset in the source object 
	 * to the same location in the copy
	 * @param source The object to copy from
	 * @param copy The target object
	 * @param type The type of primitive at the given offset - e.g. java.lang.Boolean.TYPE
	 * @param offset The offset to copy from
	 */
	public final void copyPrimitiveAtOffset(Object source, Object copy, Class<?> type, long offset) {

		if (java.lang.Boolean.TYPE == type) {
			boolean origFieldValue = THE_UNSAFE.getBoolean(source, offset);
			THE_UNSAFE.putBoolean(copy, offset, origFieldValue);
		} else if (java.lang.Byte.TYPE == type) {
			byte origFieldValue = THE_UNSAFE.getByte(source, offset);
			THE_UNSAFE.putByte(copy, offset, origFieldValue);
		} else if (java.lang.Character.TYPE == type) {
			char origFieldValue = THE_UNSAFE.getChar(source, offset);
			THE_UNSAFE.putChar(copy, offset, origFieldValue);
		} else if (java.lang.Short.TYPE == type) {
			short origFieldValue = THE_UNSAFE.getShort(source, offset);
			THE_UNSAFE.putShort(copy, offset, origFieldValue);
		} else if (java.lang.Integer.TYPE == type) {
			int origFieldValue = THE_UNSAFE.getInt(source, offset);
			THE_UNSAFE.putInt(copy, offset, origFieldValue);
		} else if (java.lang.Long.TYPE == type) {
			long origFieldValue = THE_UNSAFE.getLong(source, offset);
			THE_UNSAFE.putLong(copy, offset, origFieldValue);
		} else if (java.lang.Float.TYPE == type) {
			float origFieldValue = THE_UNSAFE.getFloat(source, offset);
			THE_UNSAFE.putFloat(copy, offset, origFieldValue);
		} else if (java.lang.Double.TYPE == type) {
			double origFieldValue = THE_UNSAFE.getDouble(source, offset);
			THE_UNSAFE.putDouble(copy, offset, origFieldValue);
		}
	}

	/**
	 * Restores the primitive at the given field to its default value. Default value is defined as the 
	 * value that the field would hold if it was a new, uninitialised value (e.g. false for a boolean).
	 * @param copy The target object
	 * @param type The type of primitive at the given offset - e.g. java.lang.Boolean.TYPE
	 * @param offset The offset to reset to its default value
	 */
	public final void putPrimitiveDefaultAtOffset(Object copy, Class<?> type, long offset) {

		if (java.lang.Boolean.TYPE == type) {
			THE_UNSAFE.putBoolean(copy, offset, false);
		} else if (java.lang.Byte.TYPE == type) {
			THE_UNSAFE.putByte(copy, offset, (byte) 0);
		} else if (java.lang.Character.TYPE == type) {
			THE_UNSAFE.putChar(copy, offset, '\u0000');
		} else if (java.lang.Short.TYPE == type) {
			THE_UNSAFE.putShort(copy, offset, (short) 0);
		} else if (java.lang.Integer.TYPE == type) {
			THE_UNSAFE.putInt(copy, offset, 0);
		} else if (java.lang.Long.TYPE == type) {
			THE_UNSAFE.putLong(copy, offset, 0L);
		} else if (java.lang.Float.TYPE == type) {
			THE_UNSAFE.putFloat(copy, offset, 0.0f);
		} else if (java.lang.Double.TYPE == type) {
			THE_UNSAFE.putDouble(copy, offset, 0.0d);
		}
	}

	/**
	 * Performs a deep copy of the object. With a deep copy all references from the object are also copied.
	 * The identity of referenced objects is preserved, so, for example, if the object graph contains two 
	 * references to the same object, the cloned object will preserve this structure.
	 * @param obj The object to perform a deep copy for.
	 * @return A deep copy of the original object.
	 */
	public <T> T deepCopy(final T obj) {
		return deepCopy(obj, new IdentityHashMap<Object, Object>(10));
	}

	/**
	 * Performs a deep copy of the object. With a deep copy all references from the object are also copied.
	 * The identity of referenced objects is preserved, so, for example, if the object graph contains two 
	 * references to the same object, the cloned object will preserve this structure.
	 * @param obj The object to perform a deep copy for.
	 * @param referencesToReuse An identity map of references to reuse - this is further populated as the copy progresses.
	 * The key is the original object reference - the value is the copied instance for that original.
	 * @return A deep copy of the original object.
	 */
	public <T> T deepCopy(final T o, IdentityHashMap<Object, Object> referencesToReuse) {

		/**
		 * To avoid unnecessary recursion and potential stackoverflow errors, we use an internal
		 * stack
		 */

		final Deque<WorkItem<?>> stack;
		if (referencesToReuse.size() >= REFERENCE_STACK_LIMIT) {
			stack = new ArrayDeque<WorkItem<?>>();
		} else {
			stack = null;
		}

		Object objectInput;

		WorkItem<?> nextWork = null;

		while (true) {

			Object objectResult;

			if (nextWork == null) {
				objectInput = o;
			} else {
				objectInput = getObject(nextWork.getSource(), nextWork.getFieldAccess().fieldOffset());
			}

			if (objectInput == null) {
				objectResult = null;
			} else {

			    if (String.class.isAssignableFrom(objectInput.getClass())) {
			        objectInput = ((String)objectInput);
			    }
			    
				Class<?> clazz = objectInput.getClass();

				if (clazz.isPrimitive() || clazz.isEnum()) {
					objectResult = objectInput;
				} else if (ClassUtils.isJdkImmutable(clazz) || ClassUtils.isWrapper(clazz)) {
					objectResult = objectInput;
				} else {

					final Object result = referencesToReuse.get(objectInput);
					if (result != null) {
						objectResult = result;
					} else {
						if (clazz.isArray()) {
							objectResult = deepCopyArray(objectInput, referencesToReuse);
						} else {

							UnsafeClassAccess<?> classAccess = UnsafeClassAccess.get(clazz);
							objectResult = allocateInstance(objectInput.getClass());

							referencesToReuse.put(objectInput, objectResult);

							for (@SuppressWarnings("rawtypes") UnsafeFieldAccess f : classAccess.getFieldAccessors()) {
								if (f.fieldClass().isPrimitive()) {
									copyPrimitiveAtOffset(objectInput, objectResult, f.fieldClass(), f.fieldOffset());
								} else if (stack == null) {
									deepCopyObjectAtOffset(objectInput, objectResult, f.fieldClass(), f.fieldOffset(), referencesToReuse);
								} else {
									@SuppressWarnings({ "unchecked", "rawtypes" })
									final WorkItem item = new WorkItem(objectInput, objectResult, f);
									stack.addFirst(item);
								}
							}
						}
					}
				}
			}

			if (nextWork == null) {
				nextWork = (stack == null ? null : stack.pollFirst());
				if (nextWork == null) {
					@SuppressWarnings("unchecked")
					final T convertedResult = (T) objectResult;
					return convertedResult;
				}
			} else if (nextWork != null) {
				if (objectResult == null) {
					putNullObject(nextWork.getTarget(), nextWork.getFieldAccess().fieldOffset());
				} else {
					putObject(nextWork.getTarget(), nextWork.getFieldAccess().fieldOffset(), objectResult);
				}
				nextWork = (stack == null ? null : stack.pollFirst());
			}
		}
	}

	/**
	 * Copies the object of the specified type from the given field offset in the source object 
	 * to the same location in the copy, visiting the object during the copy so that its fields are also copied
	 * @param source The object to copy from
	 * @param copy The target object
	 * @param fieldClass The declared type of object at the given offset
	 * @param offset The offset to copy from
	 */	
	public final void deepCopyObjectAtOffset(Object source, Object copy, Class<?> fieldClass, long offset) {
		deepCopyObjectAtOffset(source, copy, fieldClass, offset, new IdentityHashMap<Object, Object>(100));
	}

	/**
	 * Copies the object of the specified type from the given field offset in the source object 
	 * to the same location in the copy, visiting the object during the copy so that its fields are also copied
	 * @param source The object to copy from
	 * @param copy The target object
	 * @param fieldClass The declared type of object at the given offset
	 * @param offset The offset to copy from
	 * @param referencesToReuse An identity map of references to reuse - this is further populated as the copy progresses.
	 * The key is the original object reference - the value is the copied instance for that original.
	 */	
	public final void deepCopyObjectAtOffset(Object source, Object copy, Class<?> fieldClass, long offset, IdentityHashMap<Object, Object> referencesToReuse) {

		Object origFieldValue = THE_UNSAFE.getObject(source, offset);

		if (origFieldValue == null) {

			putNullObject(copy, offset);
		} else {

			final Object copyFieldValue = deepCopy(origFieldValue, referencesToReuse);
			UnsafeOperations.THE_UNSAFE.putObject(copy, offset, copyFieldValue);
		}
	}

	/**
	 * Copies the object of the specified type from the given field in the source object 
	 * to the same field in the copy, visiting the object during the copy so that its fields are also copied
	 * @param source The object to copy from
	 * @param copy The target object
	 * @param field Field to be copied
	 * @param referencesToReuse An identity map of references to reuse - this is further populated as the copy progresses.
	 * The key is the original object reference - the value is the copied instance for that original.
	 */	
	public final void deepCopyObjectField(Object source, Object copy, Field field, IdentityHashMap<Object, Object> referencesToReuse) {

		deepCopyObjectAtOffset(source, copy, field.getType(), getObjectFieldOffset(field), referencesToReuse);
	}

	/**
	 * Copies the object of the specified type from the given field in the source object 
	 * to the same field in the copy, visiting the object during the copy so that its fields are also copied
	 * @param source The object to copy from
	 * @param copy The target object
	 * @param field Field to be copied
	 */	
	public final void deepCopyObjectField(Object obj, Object copy, Field field) {

		deepCopyObjectAtOffset(obj, copy, field.getType(), getObjectFieldOffset(field), new IdentityHashMap<Object, Object>(100));
	}

	/**
	 * Copies the array of the specified type from the given field offset in the source object 
	 * to the same location in the copy, visiting the array during the copy so that its contents are also copied
	 * @param source The object to copy from
	 * @param copy The target object
	 * @param fieldClass The declared type of array at the given offset
	 * @param offset The offset to copy from
	 */	
	public final void deepCopyArrayAtOffset(Object source, Object copy, Class<?> fieldClass, long offset) {
		deepCopyArrayAtOffset(source, copy, fieldClass, offset, new IdentityHashMap<Object, Object>(100));
	}

	/**
	 * Copies the array of the specified type from the given field offset in the source object 
	 * to the same location in the copy, visiting the array during the copy so that its contents are also copied
	 * @param source The object to copy from
	 * @param copy The target object
	 * @param fieldClass The declared type of array at the given offset
	 * @param offset The offset to copy from
	 * @param referencesToReuse An identity map of references to reuse - this is further populated as the copy progresses.
	 * The key is the original object reference - the value is the copied instance for that original.
	 */	
	public final void deepCopyArrayAtOffset(Object source, Object copy, Class<?> fieldClass, long offset, IdentityHashMap<Object, Object> referencesToReuse) {

		Object origFieldValue = THE_UNSAFE.getObject(source, offset);

		if (origFieldValue == null) {

			putNullObject(copy, offset);
		} else {

			final Object copyFieldValue = deepCopyArray(origFieldValue, referencesToReuse);
			UnsafeOperations.THE_UNSAFE.putObject(copy, offset, copyFieldValue);
		}
	}

	/**
	 * Copies the array of the specified type from the given field in the source object 
	 * to the same field in the copy, visiting the array  during the copy so that its contents are also copied
	 * @param source The object to copy from
	 * @param copy The target object
	 * @param field Field to be copied
	 * @param referencesToReuse An identity map of references to reuse - this is further populated as the copy progresses.
	 * The key is the original object reference - the value is the copied instance for that original.
	 */	
	public final void deepCopyArrayField(Object obj, Object copy, Field field, IdentityHashMap<Object, Object> referencesToReuse) {

		deepCopyArrayAtOffset(obj, copy, field.getType(), getObjectFieldOffset(field), referencesToReuse);
	}

	/**
	 * Copies the array of the specified type from the given field in the source object 
	 * to the same field in the copy, visiting the array  during the copy so that its contents are also copied
	 * @param source The object to copy from
	 * @param copy The target object
	 * @param field Field to be copied
	 */	
	public final void deepCopyArrayField(Object obj, Object copy, Field field) {

		deepCopyArrayAtOffset(obj, copy, field.getType(), getObjectFieldOffset(field), new IdentityHashMap<Object, Object>(100));
	}

	/**
	 * Performs a deep copy of the array. With a deep copy all references from the array are also copied.
	 * The identity of referenced objects is preserved, so, for example, if the object graph contains two 
	 * references to the same object, the cloned object will preserve this structure.
	 * @param arrayOriginal The array to perform a deep copy for.
	 * @param referencesToReuse An identity map of references to reuse - this is further populated as the copy progresses.
	 * The key is the original object reference - the value is the copied instance for that original.
	 * @return A deep copy of the original array.
	 */
	public final Object deepCopyArray(Object arrayOriginal, IdentityHashMap<Object, Object> visited) {

		if (visited.containsKey(arrayOriginal)) {
			return visited.get(arrayOriginal);
		}

		final Class<?> componentType = arrayOriginal.getClass().getComponentType();

		Object result = null;

		if (componentType.isPrimitive()) {

			if (java.lang.Boolean.TYPE == componentType) {
				result = Arrays.copyOf((boolean[]) arrayOriginal, ((boolean[]) arrayOriginal).length);
			} else if (java.lang.Byte.TYPE == componentType) {
				result = Arrays.copyOf((byte[]) arrayOriginal, ((byte[]) arrayOriginal).length);
			} else if (java.lang.Character.TYPE == componentType) {
				result = Arrays.copyOf((char[]) arrayOriginal, ((char[]) arrayOriginal).length);
			} else if (java.lang.Short.TYPE == componentType) {
				result = Arrays.copyOf((short[]) arrayOriginal, ((short[]) arrayOriginal).length);
			} else if (java.lang.Integer.TYPE == componentType) {
				result = Arrays.copyOf((int[]) arrayOriginal, ((int[]) arrayOriginal).length);
			} else if (java.lang.Long.TYPE == componentType) {
				result = Arrays.copyOf((long[]) arrayOriginal, ((long[]) arrayOriginal).length);
			} else if (java.lang.Float.TYPE == componentType) {
				result = Arrays.copyOf((float[]) arrayOriginal, ((float[]) arrayOriginal).length);
			} else if (java.lang.Double.TYPE == componentType) {
				result = Arrays.copyOf((double[]) arrayOriginal, ((double[]) arrayOriginal).length);
			}
		}

		if (result == null) {
			Object[] arrayCopy = Arrays.copyOf((Object[]) arrayOriginal, ((Object[]) arrayOriginal).length);
			if (arrayCopy.length > 0) {

				if (componentType.isArray()) {
					for (int i = 0; i < arrayCopy.length; i++) {
						arrayCopy[i] = deepCopyArray(arrayCopy[i], visited);
					}
				} else {
					for (int i = 0; i < arrayCopy.length; i++) {
						Object component = deepCopy(arrayCopy[i], visited);
						arrayCopy[i] = component;
					}
				}
			}
			result = arrayCopy;
		}

		visited.put(arrayOriginal, result);
		return result;
	}

	/**
	 * Determines the shallow memory size of an instance of the given class
	 * @param clazz The class to calculate the shallow size for
	 * @return Size in bytes
	 */
	public final long shallowSizeOf(Class<?> clazz) {
		return doShallowSizeOfClass(clazz);
	}

	/**
	 * Determines the shallow memory size of the given object (object or array)
	 * @param clazz The object instance to calculate the shallow size for
	 * @return Size in bytes
	 */
	public final long shallowSizeOf(Object obj) {

		if (obj == null) {
			return 0;
		}

		if (obj.getClass().isArray()) {
			return doShallowSizeOfArray(obj);
		} else {
			return doShallowSizeOfClass(obj.getClass());
		}
	}

	private long doShallowSizeOfArray(Object array) {

		long size = getSizeOfArrayHeader();
		final int length = Array.getLength(array);
		if (length > 0) {

			Class<?> type = array.getClass().getComponentType();
			if (type.isPrimitive()) {

				if (java.lang.Boolean.TYPE == type) {
					size = size + (length * SIZE_BYTES_BOOLEAN);
				} else if (java.lang.Byte.TYPE == type) {
					size = size + (length * SIZE_BYTES_BYTE);
				} else if (java.lang.Character.TYPE == type) {
					size = size + (length * SIZE_BYTES_CHAR);
				} else if (java.lang.Short.TYPE == type) {
					size = size + (length * SIZE_BYTES_SHORT);
				} else if (java.lang.Integer.TYPE == type) {
					size = size + (length * SIZE_BYTES_INT);
				} else if (java.lang.Long.TYPE == type) {
					size = size + (length * SIZE_BYTES_LONG);
				} else if (java.lang.Float.TYPE == type) {
					size = size + (length * SIZE_BYTES_FLOAT);
				} else if (java.lang.Double.TYPE == type) {
					size = size + (length * SIZE_BYTES_DOUBLE);
				}

			} else {
				size = size + (length * getSizeOfObjectHeader());
			}
		}

		size = size + SIZE_BYTES_PAGE_FOR_OBJECT_ALIGNMENT - 1L;
		return size - (size % SIZE_BYTES_PAGE_FOR_OBJECT_ALIGNMENT);
	}

	private long doShallowSizeOfClass(Class<?> clazz) {

		if (clazz.isArray()) {
			throw new IllegalArgumentException("Shallow Size of cannot be calculated for arrays classes as component length is needed");
		}
		if (clazz.isPrimitive()) {

			return getSizeForPrimitive(clazz);
		}
		if (clazz == Object.class) {
			return MIN_SIZE;
		}

		long size = getSizeOfObjectHeader();

		Field[] fields = ClassUtils.collectInstanceFields(clazz);

		for (Field f : fields) {

			Class<?> fieldClass = f.getType();
			final int fieldSize = fieldClass.isPrimitive() ? getSizeForPrimitive(fieldClass) : getSizeOfObjectHeader();
			final long offsetPlusSize = getObjectFieldOffset(f) + fieldSize;

			if (offsetPlusSize > size) {
				size = offsetPlusSize;
			}
		}

		size = size + SIZE_BYTES_PAGE_FOR_OBJECT_ALIGNMENT - 1L;
		return size - (size % SIZE_BYTES_PAGE_FOR_OBJECT_ALIGNMENT);
	}

	/**
	 * Determines the deep memory size of the given object (object or array), visiting all its references
	 * @param clazz The object instance to calculate the deep size for
	 * @return Size in bytes
	 */
	public final long deepSizeOf(Object o) {

		IdentityHashMap<Object, Boolean> seenObjects = new IdentityHashMap<Object, Boolean>(10);
		return doDeepSizeOf(o, seenObjects);
	}

	private long doDeepSizeOf(Object o, IdentityHashMap<Object, Boolean> seenObjects) {

		if (o == null) {
			return 0;
		}

		Class<?> clazz = o.getClass();
		if (clazz.isPrimitive()) {
			return getSizeForPrimitive(clazz);
		}

		seenObjects.put(o, Boolean.TRUE);

		if (clazz.isArray()) {

			long size = doShallowSizeOfArray(o);

			if (!clazz.getComponentType().isPrimitive()) {

				Object[] array = (Object[]) o;
				for (int i = 0; i < array.length; i++) {
					Object nextObject = array[i];
					if (nextObject != null && !seenObjects.containsKey(nextObject)) {
						size = size + doDeepSizeOf(nextObject, seenObjects);
					}
				}
			}

			return size;

		} else {

			if (clazz == Object.class) {
				return MIN_SIZE;
			}

			long size = getSizeOfObjectHeader();
			long additionalSize = 0;

			Field[] fields = ClassUtils.collectInstanceFields(clazz);

			for (Field f : fields) {

				long objectFieldOffset = getObjectFieldOffset(f);

				Class<?> fieldClass = f.getType();
				final int fieldSize = fieldClass.isPrimitive() ? getSizeForPrimitive(fieldClass) : getSizeOfObjectHeader();
				final long offsetPlusSize = objectFieldOffset + fieldSize;

				if (offsetPlusSize > size) {
					size = offsetPlusSize;
				}

				if (!fieldClass.isPrimitive()) {
					Object fieldObject = getObject(o, objectFieldOffset);
					if (fieldObject != null && !seenObjects.containsKey(fieldObject)) {
						additionalSize += doDeepSizeOf(fieldObject, seenObjects);
					}
				}
			}

			size = size + SIZE_BYTES_PAGE_FOR_OBJECT_ALIGNMENT - 1L;
			size = size - (size % SIZE_BYTES_PAGE_FOR_OBJECT_ALIGNMENT);

			return size + additionalSize;
		}
	}

	/**
	 * Memory address payload is an unsigned value - we need to normalise the 'sign' to get a
	 * meaningful value back - when we do this we need to store it into a long
	 * @param value The value to normalise
	 * @return The normalised value as a long
	 */
	private static long normalize(int value) {
		if (value >= 0) {
			return value;
		}
		return (~0L >>> 32) & value;
	}

	/**
	 * Get the size in bytes of a native pointer - either 4 or 8 (for 32-bit or 64-bit JRE).
	 * For primitive types, the size is determined by their data type rather than this value.
	 * @return The address size
	 */
	public final int getAddressSize() {
		return THE_UNSAFE.addressSize();
	}	
	
	/**
	 * Retrieve the object at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 * @return The retrieved object
	 */
	public final Object getObject(Object parent, long offset) {
		return THE_UNSAFE.getObject(parent, offset);
	}

	/**
	 * Retrieve the value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 * @return The retrieved boolean
	 */
	public final boolean getBoolean(Object parent, long offset) {
		return THE_UNSAFE.getBoolean(parent, offset);
	}

	/**
	 * Retrieve the value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 * @return The retrieved char
	 */
	public final char getChar(Object parent, long offset) {
		return THE_UNSAFE.getChar(parent, offset);
	}
	
	/**
	 * Retrieve the value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 * @return The retrieved short
	 */
	public final short getShort(Object parent, long offset) {
		return THE_UNSAFE.getShort(parent, offset);
	}
	
	/**
	 * Retrieve the value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 * @return The retrieved int
	 */
	public final int getInt(Object parent, long offset) {
		return THE_UNSAFE.getInt(parent, offset);
	}
	
	/**
	 * Retrieve the value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 * @return The retrieved long
	 */
	public final long getLong(Object parent, long offset) {
		return THE_UNSAFE.getLong(parent, offset);
	}
	
	/**
	 * Retrieve the value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 * @return The retrieved float
	 */
	public final float getFloat(Object parent, long offset) {
		return THE_UNSAFE.getFloat(parent, offset);
	}
	
	/**
	 * Retrieve the value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 * @return The retrieved double
	 */
	public final double getDouble(Object parent, long offset) {
		return THE_UNSAFE.getDouble(parent, offset);
	}

	/**
	 * Retrieve the value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 * @return The retrieved byte
	 */
	public final byte getByte(Object parent, long offset) {
		return THE_UNSAFE.getByte(parent, offset);
	}

	/**
	 * Write a null value to the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 */
	public final void putNullObject(Object parent, long offset) {
		THE_UNSAFE.putObject(parent, offset, null);
	}

	/**
	 * Resets to false the boolean value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 */
	public final void putDefaultBoolean(Object parent, long offset) {
		THE_UNSAFE.putBoolean(parent, offset, false);
	}

	/**
	 * Resets to u0000 the char value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 */
	public final void putDefaultChar(Object parent, long offset) {
		THE_UNSAFE.putChar(parent, offset, '\u0000');
	}
	
	/**
	 * Resets to 0 the short value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 */
	public final void putDefaultShort(Object parent, long offset) {
		THE_UNSAFE.putShort(parent, offset, (short)0);
	}

	/**
	 * Resets to 0 the int value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 */
	public final void putDefaultInt(Object parent, long offset) {
		THE_UNSAFE.putInt(parent, offset, 0);
	}
	
	/**
	 * Resets to 0 the long value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 */
	public final void putDefaultLong(Object parent, long offset) {
		THE_UNSAFE.putLong(parent, offset, 0L);
	}
	
	/**
	 * Resets to 0.0f the float value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 */
	public final void putDefaultFloat(Object parent, long offset) {
		THE_UNSAFE.putFloat(parent, offset, 0.0F);
	}
	
	/**
	 * Resets to 0.0d the double value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 */
	public final void putDefaultDouble(Object parent, long offset) {
		THE_UNSAFE.putDouble(parent, offset, 0.0D);
	}
	
	/**
	 * Resets to 0 the byte value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 */
	public final void putDefaultByte(Object parent, long offset) {
		THE_UNSAFE.putByte(parent, offset, (byte)0);
	}

	/**
	 * Puts the object's reference at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 * @param value Object to be put
	 */
	public final void putObject(Object parent, long offset, Object value) {
		THE_UNSAFE.putObject(parent, offset, value);
	}

	/**
	 * Puts the value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 * @param value boolean to be put
	 */
	public final void putBoolean(Object parent, long offset, boolean value) {
		THE_UNSAFE.putBoolean(parent, offset, value);
	}

	/**
	 * Puts the value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 * @param value char to be put
	 */
	public final void putChar(Object parent, long offset, char value) {
		THE_UNSAFE.putChar(parent, offset, value);
	}
	
	/**
	 * Puts the value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 * @param value short to be put
	 */
	public final void putShort(Object parent, long offset, short value) {
		THE_UNSAFE.putShort(parent, offset, value);
	}
	
	/**
	 * Puts the value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 * @param value int to be put
	 */
	public final void putInt(Object parent, long offset, int value) {
		THE_UNSAFE.putInt(parent, offset, value);
	}
	
	/**
	 * Puts the value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 * @param value long to be put
	 */
	public final void putLong(Object parent, long offset, long value) {
		THE_UNSAFE.putLong(parent, offset, value);
	}
	
	/**
	 * Puts the value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 * @param value float to be put
	 */
	public final void putFloat(Object parent, long offset, float value) {
		THE_UNSAFE.putFloat(parent, offset, value);
	}
	
	/**
	 * Puts the value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 * @param value double to be put
	 */
	public final void putDouble(Object parent, long offset, double value) {
		THE_UNSAFE.putDouble(parent, offset, value);
	}
	
	/**
	 * Puts the value at the given offset of the supplied parent object
	 * @param parent The Object's parent
	 * @param offset The offset
	 * @param value byte to be put
	 */
	public final void putByte(Object parent, long offset, byte value) {
		THE_UNSAFE.putByte(parent, offset, value);
	}
	
	private class WorkItem<P> {

		private final Object source;
		private final Object target;
		private final UnsafeFieldAccess<P> fieldAccess;

		public WorkItem(Object source, Object target, UnsafeFieldAccess<P> fieldAccess) {
			this.source = source;
			this.target = target;
			this.fieldAccess = fieldAccess;
		}

		public Object getSource() {
			return source;
		}

		public Object getTarget() {
			return target;
		}

		public UnsafeFieldAccess<P> getFieldAccess() {
			return fieldAccess;
		}
	}

	@SuppressWarnings("unused")
	private static final class SingleFieldHolder {
		public int field;
	}

	private final int getSizeOfArrayHeader() {
		return THE_UNSAFE.arrayBaseOffset(byte[].class);
	}

	private final int getSizeForPrimitive(Class<?> clazz) {

		if (java.lang.Boolean.TYPE.isAssignableFrom(clazz)) {
			return SIZE_BYTES_BOOLEAN;
		} else if (java.lang.Byte.TYPE.isAssignableFrom(clazz)) {
			return SIZE_BYTES_BYTE;
		} else if (java.lang.Character.TYPE.isAssignableFrom(clazz)) {
			return SIZE_BYTES_CHAR;
		} else if (java.lang.Short.TYPE.isAssignableFrom(clazz)) {
			return SIZE_BYTES_SHORT;
		} else if (java.lang.Integer.TYPE.isAssignableFrom(clazz)) {
			return SIZE_BYTES_INT;
		} else if (java.lang.Long.TYPE.isAssignableFrom(clazz)) {
			return SIZE_BYTES_LONG;
		} else if (java.lang.Float.TYPE.isAssignableFrom(clazz)) {
			return SIZE_BYTES_FLOAT;
		} else if (java.lang.Double.TYPE.isAssignableFrom(clazz)) {
			return SIZE_BYTES_DOUBLE;
		}
		throw new IllegalArgumentException("Class " + clazz.getName() + " is not primitive");
	}

	private final int getSizeOfObjectHeader() {
		try {
			return (int) THE_UNSAFE.objectFieldOffset(SingleFieldHolder.class.getDeclaredField("field"));
		} catch (NoSuchFieldException e) {
			throw new IllegalStateException("Cannot determine size of object header", e);
		} catch (SecurityException e) {
			throw new IllegalStateException("Cannot determine size of object header", e);
		}
	}
}
