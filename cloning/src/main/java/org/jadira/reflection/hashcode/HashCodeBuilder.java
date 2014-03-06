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
package org.jadira.reflection.hashcode;

import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;

import org.jadira.reflection.access.api.ClassAccess;
import org.jadira.reflection.access.api.ClassAccessFactory;
import org.jadira.reflection.access.api.FieldAccess;
import org.jadira.reflection.access.api.MethodAccess;
import org.jadira.reflection.access.portable.PortableClassAccessFactory;
import org.jadira.reflection.access.unsafe.UnsafeClassAccessFactory;
import org.jadira.reflection.core.platform.FeatureDetection;

/**
 * This class is based on the capabilities provided by Commons-Lang's
 * HashCodeBuilder. It is not a complete "drop-in" replacement, but is near enough
 * in functionality and in terms of its API that it should be quite
 * straightforward to switch from one class to the other.
 * 
 * HashCodeBuilder makes use of Jadira's reflection capabilities. This means, like
 * Cloning there is a brief warm-up time entailed in preparing introspection
 * when a class is first processed. If you are already performing cloning on a
 * type you will not be impacted by this as any necessary warm-up will have
 * already taken place.
 * 
 * Once the initial warmup has been performed, you will, usually, find enhanced
 * performance compared to standard reflection based approaches. The strategy to
 * be used can be configured.
 *
 * Where objects that are reachable from the object being compared do not
 * override hashCode(), the default behaviour is to rely on
 * Object#hashCode(Object) which uses the object identity for those
 * objects. This is also the behaviour of Commons Lang. You can modify this to
 * reflect into any reachable object that does not override equals by enabling
 * the defaultDeepReflect property.
 * 
 * Transient fields are not compared by the current implementation of this
 * class.
 */
public class HashCodeBuilder {

	private static final ThreadLocal<HashCodeBuilder> reflectionBuilder = new ThreadLocal<HashCodeBuilder>() {
		protected HashCodeBuilder initialValue() {
			return new HashCodeBuilder(37, 17);
		};
	};

	private IdentityHashMap<Object, Object> seenReferences;

	/**
	 * Constant to use in building the hashCode.
	 */
	private final int constant;

	/**
	 * Running total of the hashCode.
	 */
	private int computedTotal;

	private final int seed;

	private ClassAccessFactory classAccessFactory;

	private boolean defaultDeepReflect = false;

	private <C> void reflectionAppend(C object) {

		if (seenReferences.containsKey(object)) {
			return;
		}
		seenReferences.put(object, object);

		@SuppressWarnings("unchecked")
		ClassAccess<? super C> classAccess = (ClassAccess<C>) classAccessFactory
				.getClassAccess(object.getClass());

		while ((classAccess.getType() != Object.class)
				&& (!classAccess.providesHashCode())) {

			ClassAccess<? super C> classAccessInHierarchy = classAccess;
						
			for (FieldAccess<? super C> fieldAccess : classAccessInHierarchy.getDeclaredFieldAccessors()) {

				if ((fieldAccess.field().getName().indexOf('$') == -1)
						&& (!Modifier.isTransient(fieldAccess.field().getModifiers()))
						&& (!Modifier.isStatic(fieldAccess.field().getModifiers()))) {
				
					Class<?> type = fieldAccess.fieldClass();
					
					if (type.isPrimitive()) {
						if (java.lang.Boolean.TYPE == type) {
							append(fieldAccess.getBooleanValue(object));
						} else if (java.lang.Byte.TYPE == type) {
							append(fieldAccess.getByteValue(object));
						} else if (java.lang.Character.TYPE == type) {
							append(fieldAccess.getCharValue(object));
						} else if (java.lang.Short.TYPE == type) {
							append(fieldAccess.getShortValue(object));
						} else if (java.lang.Integer.TYPE == type) {
							append(fieldAccess.getIntValue(object));
						} else if (java.lang.Long.TYPE == type) {
							append(fieldAccess.getLongValue(object));
						} else if (java.lang.Float.TYPE == type) {
							append(fieldAccess.getFloatValue(object));
						} else if (java.lang.Double.TYPE == type) {
							append(fieldAccess.getDoubleValue(object));
						}
					} else {
						final Object value = fieldAccess.getValue(object);
						append(value);
					}
				}
			}

			classAccessInHierarchy = classAccessInHierarchy.getSuperClassAccess();
		}
		if (classAccess.getType() != Object.class) {

			final MethodAccess<Object> methodAccess;
			try {
				@SuppressWarnings("unchecked")
				final MethodAccess<Object> myMethodAccess = (MethodAccess<Object>) classAccess
						.getDeclaredMethodAccess(classAccess.getType().getMethod(
								"hashCode"));
				methodAccess = myMethodAccess;
			} catch (NoSuchMethodException e) {
				throw new IllegalStateException("Cannot find hashCode() method");
			} catch (SecurityException e) {
				throw new IllegalStateException("Cannot find hashCode() method");
			}
			append(((Integer) (methodAccess.invoke(object))).intValue());
		}
	}

	public static <T> int reflectionHashCode(T object) {
		return reflectionHashCode(37, 17, object);
	}

	public static <T> int reflectionHashCode(int initialNonZeroOddNumber,
			int multiplierNonZeroOddNumber, T object) {

		if (object == null) {
			throw new IllegalArgumentException(
					"Cannot build hashcode for null object");
		}

		HashCodeBuilder builder = reflectionBuilder.get();
		// In case we recurse into this method
		reflectionBuilder.set(null);

		if (builder.seed == initialNonZeroOddNumber
				&& builder.constant == multiplierNonZeroOddNumber) {
			builder.reset();
		} else {
			builder = new HashCodeBuilder(initialNonZeroOddNumber,
					multiplierNonZeroOddNumber);
		}

		builder.reflectionAppend(object);

		final int hashCode = builder.toHashCode();
		reflectionBuilder.set(builder);

		return hashCode;
	}

	public HashCodeBuilder() {

		seenReferences = new IdentityHashMap<Object, Object>();

		constant = 37;
		computedTotal = seed = 17;

		if (FeatureDetection.hasUnsafe()) {
			this.classAccessFactory = UnsafeClassAccessFactory.get();
		} else {
			this.classAccessFactory = PortableClassAccessFactory.get();
		}
	}

	public HashCodeBuilder(int initialNonZeroOddNumber,
			int multiplierNonZeroOddNumber) {

		seenReferences = new IdentityHashMap<Object, Object>();

		if ((initialNonZeroOddNumber % 2 == 0)
				|| (initialNonZeroOddNumber == 0)) {
			throw new IllegalArgumentException(
					"Initial Value must be odd and non-zero but was: "
							+ initialNonZeroOddNumber);
		} else if ((multiplierNonZeroOddNumber % 2 == 0)
				|| (multiplierNonZeroOddNumber == 0)) {
			throw new IllegalArgumentException(
					"Multiplier must be odd and non-zero but was: "
							+ multiplierNonZeroOddNumber);
		}

		constant = multiplierNonZeroOddNumber;
		computedTotal = seed = initialNonZeroOddNumber;

		if (FeatureDetection.hasUnsafe()) {
			this.classAccessFactory = UnsafeClassAccessFactory.get();
		} else {
			this.classAccessFactory = PortableClassAccessFactory.get();
		}
	}

	public HashCodeBuilder withDefaultDeepReflect(boolean newDefaultDeepReflect) {
		this.defaultDeepReflect = newDefaultDeepReflect;
		return this;
	}

	public HashCodeBuilder withClassAccessFactory(
			ClassAccessFactory classAccessFactory) {
		this.classAccessFactory = classAccessFactory;
		return this;
	}

	public HashCodeBuilder append(boolean value) {
		computedTotal = computedTotal * constant + (value ? 0 : 1);
		return this;
	}

	public HashCodeBuilder append(boolean[] array) {
		if (seenReferences.containsKey(array)) {
			return this;
		}
		seenReferences.put(array, array);

		if (array == null) {
			appendNull();
		} else {
			for (boolean element : array) {
				append(element);
			}
		}
		return this;
	}

	public HashCodeBuilder append(byte value) {
		computedTotal = (computedTotal * constant) + value;
		return this;
	}

	public HashCodeBuilder append(byte[] array) {
		if (seenReferences.containsKey(array)) {
			return this;
		}
		seenReferences.put(array, array);

		if (array == null) {
			appendNull();
		} else {
			for (byte element : array) {
				append(element);
			}
		}
		return this;
	}

	public HashCodeBuilder append(char value) {
		computedTotal = (computedTotal * constant) + value;
		return this;
	}

	public HashCodeBuilder append(char[] array) {
		if (seenReferences.containsKey(array)) {
			return this;
		}
		seenReferences.put(array, array);

		if (array == null) {
			appendNull();
		} else {
			for (char element : array) {
				append(element);
			}
		}
		return this;
	}

	public HashCodeBuilder append(double value) {
		return append(Double.doubleToLongBits(value));
	}

	public HashCodeBuilder append(double[] array) {
		if (seenReferences.containsKey(array)) {
			return this;
		}
		seenReferences.put(array, array);

		if (array == null) {
			appendNull();
		} else {
			for (double element : array) {
				append(element);
			}
		}
		return this;
	}

	public HashCodeBuilder append(float value) {
		computedTotal = (computedTotal * constant)
				+ Float.floatToIntBits(value);
		return this;
	}

	public HashCodeBuilder append(float[] array) {
		if (seenReferences.containsKey(array)) {
			return this;
		}
		seenReferences.put(array, array);

		if (array == null) {
			appendNull();
		} else {
			for (float element : array) {
				append(element);
			}
		}
		return this;
	}

	public HashCodeBuilder append(int value) {
		computedTotal = (computedTotal * constant) + value;
		return this;
	}

	public HashCodeBuilder append(int[] array) {
		if (seenReferences.containsKey(array)) {
			return this;
		}
		seenReferences.put(array, array);

		if (array == null) {
			appendNull();
		} else {
			for (int element : array) {
				append(element);
			}
		}
		return this;
	}

	public HashCodeBuilder append(long value) {
		computedTotal = computedTotal * constant
				+ (int) (value ^ (value >>> 32));
		return this;
	}

	public HashCodeBuilder append(long[] array) {
		if (seenReferences.containsKey(array)) {
			return this;
		}
		seenReferences.put(array, array);

		if (array == null) {
			appendNull();
		} else {
			for (long element : array) {
				append(element);
			}
		}
		return this;
	}

	public HashCodeBuilder append(Object object) {

		if (object == null) {
			appendNull();
			return this;
		} else {
			if (object.getClass() == null) {
				appendNull();
			} else if (object.getClass().isArray()) {

				if (object instanceof long[]) {
					append((long[]) object);
				} else if (object instanceof int[]) {
					append((int[]) object);
				} else if (object instanceof short[]) {
					append((short[]) object);
				} else if (object instanceof char[]) {
					append((char[]) object);
				} else if (object instanceof byte[]) {
					append((byte[]) object);
				} else if (object instanceof double[]) {
					append((double[]) object);
				} else if (object instanceof float[]) {
					append((float[]) object);
				} else if (object instanceof boolean[]) {
					append((boolean[]) object);
				} else {
					append((Object[]) object);
				}
			} else {
				computedTotal = (computedTotal * constant);
				if (defaultDeepReflect) {
					reflectionAppend(object);
				} else {
					computedTotal = computedTotal + object.hashCode();
				}
			}
		}
		return this;
	}

	public HashCodeBuilder append(Object[] array) {
		if (seenReferences.containsKey(array)) {
			return this;
		}
		seenReferences.put(array, array);

		if (array == null) {
			appendNull();
		} else {
			for (Object element : array) {
				append(element);
			}
		}
		return this;
	}

	public HashCodeBuilder append(short value) {
		computedTotal = (computedTotal * constant) + value;
		return this;
	}

	public HashCodeBuilder append(short[] array) {
		if (seenReferences.containsKey(array)) {
			return this;
		}
		seenReferences.put(array, array);

		if (array == null) {
			appendNull();
		} else {
			for (short element : array) {
				append(element);
			}
		}
		return this;
	}

	public HashCodeBuilder appendSuper(int superHashCode) {
		computedTotal = (computedTotal * constant) + superHashCode;
		return this;
	}

	protected void appendNull() {
		computedTotal = computedTotal * constant;
	}

	public int toHashCode() {
		return computedTotal;
	}

	public int hashCode() {
		return toHashCode();
	}

	public void reset() {
		seenReferences = new IdentityHashMap<Object, Object>();
		computedTotal = seed;
	}
}
