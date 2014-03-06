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
package org.jadira.reflection.equals;

import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;

import org.jadira.reflection.access.api.ClassAccess;
import org.jadira.reflection.access.api.ClassAccessFactory;
import org.jadira.reflection.access.api.FieldAccess;
import org.jadira.reflection.access.api.MethodAccess;
import org.jadira.reflection.access.portable.PortableClassAccessFactory;
import org.jadira.reflection.access.unsafe.UnsafeClassAccessFactory;
import org.jadira.reflection.core.identity.Tuple;
import org.jadira.reflection.core.platform.FeatureDetection;

/**
 * This class is based on the capabilities provided by Commons-Lang's
 * EqualsBuilder. It is not a complete "drop-in" replacement, but is near enough
 * in functionality and in terms of its API that it should be quite
 * straightforward to switch from one class to the other.
 * 
 * EqualsBuilder makes use of Jadira's reflection capabilities. This means, like
 * Cloning there is a brief warm-up time entailed in preparing introspection
 * when a class is first processed. If you are already performing cloning on a
 * type you will not be impacted by this as any necessary warm-up will have already taken place.
 * 
 * Once the initial warmup has been performed, you will, usually, find enhanced performance
 * compared to standard reflection based approaches. The strategy to be used can be configured.
 *
 * Where objects that are reachable from the object being compared do not override equals(),
 * the default behaviour is to rely on {@link Object#equals(Object)} which compares object
 * identity for those objects. This is also the behaviour of Commons Lang. You
 * can modify this to reflect into any reachable object that does not override equals by
 * enabling the defaultDeepReflect property.
 * 
 * Transient fields are not compared by the current implementation of this
 * class.
 */
public class EqualsBuilder {

	private static final ThreadLocal<EqualsBuilder> reflectionBuilder = new ThreadLocal<EqualsBuilder>() {
		protected EqualsBuilder initialValue() {
			return new EqualsBuilder();
		};
	};

	private IdentityHashMap<Tuple<Object, Object>, Tuple<Object, Object>> seenReferences;

	/**
	 * If the fields tested are equal. The default value is <code>true</code>.
	 */
	private boolean isEquals = true;

	private boolean defaultDeepReflect = false;

	private ClassAccessFactory classAccessFactory;

	public EqualsBuilder() {
		seenReferences = new IdentityHashMap<Tuple<Object, Object>, Tuple<Object, Object>>();

		if (FeatureDetection.hasUnsafe()) {
			this.classAccessFactory = UnsafeClassAccessFactory.get();
		} else {
			this.classAccessFactory = PortableClassAccessFactory.get();
		}
	}

	public EqualsBuilder withDefaultDeepReflect(boolean newDefaultDeepReflect) {
		this.defaultDeepReflect = newDefaultDeepReflect;
		return this;
	}

	public EqualsBuilder withClassAccessFactory(
			ClassAccessFactory classAccessFactory) {
		this.classAccessFactory = classAccessFactory;
		return this;
	}

	public static boolean reflectionEquals(Object lhs, Object rhs) {

		if (lhs == rhs) {
			return true;
		}
		if (lhs == null || rhs == null) {
			return false;
		}

		Class<?> lhsClass = lhs.getClass();
		Class<?> rhsClass = rhs.getClass();

		Class<?> testClass;
		if (lhsClass.isInstance(rhs)) {
			if (rhsClass.isInstance(lhs)) {
				testClass = lhsClass;
			} else {
				testClass = rhsClass;
			}
		} else if (rhsClass.isInstance(lhs)) {
			if (lhsClass.isInstance(rhs)) {
				testClass = rhsClass;
			} else {
				testClass = lhsClass;
			}
		} else {
			return false;
		}

		EqualsBuilder equalsBuilder = reflectionBuilder.get();
		// In case we recurse into this method
		reflectionBuilder.set(null);

		equalsBuilder.reset();

		ClassAccess<?> classAccess = equalsBuilder.classAccessFactory
				.getClassAccess(testClass);

		try {
			while ((classAccess.getType() != Object.class)
					&& (!classAccess.providesEquals())) {
				equalsBuilder.reflectionAppend(lhs, rhs, classAccess);
				classAccess = classAccess.getSuperClassAccess();
			}
		} catch (IllegalArgumentException e) {
			return false;
		}

		if (classAccess.getType() != Object.class) {

			final MethodAccess<Object> methodAccess;
			try {
				@SuppressWarnings("unchecked")
				final MethodAccess<Object> myMethodAccess = (MethodAccess<Object>) classAccess.getDeclaredMethodAccess(classAccess.getType().getMethod(
								"equals", Object.class));
				methodAccess = myMethodAccess;
			} catch (NoSuchMethodException e) {
				throw new IllegalStateException("Cannot find equals() method");
			} catch (SecurityException e) {
				throw new IllegalStateException("Cannot find equals() method");
			}
			equalsBuilder.setEquals(((Boolean) methodAccess.invoke(lhs, rhs))
					.booleanValue());
		}

		final boolean isEqual = equalsBuilder.isEquals();
		reflectionBuilder.set(equalsBuilder);

		return isEqual;
	}

	private void reflectionAppend(Object lhs, Object rhs,
			ClassAccess<?> classAccess) {

		Tuple<Object, Object> t = Tuple.of(lhs, rhs);
		if (seenReferences.containsKey(t)) {
			return;
		}
		seenReferences.put(t, t);

		@SuppressWarnings("unchecked")
		ClassAccess<Object> classAccessInHierarchy = (ClassAccess<Object>) classAccess;
		
		while (classAccessInHierarchy != null) {
		
			FieldAccess<Object>[] fields = (FieldAccess<Object>[]) classAccessInHierarchy.getDeclaredFieldAccessors();
	
			for (int i = 0; (i < fields.length) && (isEquals); i++) {
				FieldAccess<Object> f = fields[i];
				if ((f.field().getName().indexOf('$') == -1)
						&& (!Modifier.isTransient(f.field().getModifiers()))
						&& (!Modifier.isStatic(f.field().getModifiers()))) {
	
					Class<?> type = f.fieldClass();
					if (type.isPrimitive()) {
						if (java.lang.Boolean.TYPE == type) {
							append(f.getBooleanValue(lhs), f.getBooleanValue(rhs));
						} else if (java.lang.Byte.TYPE == type) {
							append(f.getByteValue(lhs), f.getByteValue(rhs));
						} else if (java.lang.Character.TYPE == type) {
							append(f.getCharValue(lhs), f.getCharValue(rhs));
						} else if (java.lang.Short.TYPE == type) {
							append(f.getShortValue(lhs), f.getShortValue(rhs));
						} else if (java.lang.Integer.TYPE == type) {
							append(f.getIntValue(lhs), f.getIntValue(rhs));
						} else if (java.lang.Long.TYPE == type) {
							append(f.getLongValue(lhs), f.getLongValue(rhs));
						} else if (java.lang.Float.TYPE == type) {
							append(f.getFloatValue(lhs), f.getFloatValue(rhs));
						} else if (java.lang.Double.TYPE == type) {
							append(f.getDoubleValue(lhs), f.getDoubleValue(rhs));
						}
					} else {
						append(f.getValue(lhs), f.getValue(rhs));
					}
				}
			}
			
			classAccessInHierarchy = classAccessInHierarchy.getSuperClassAccess();
		}
	}

	public EqualsBuilder append(Object lhs, Object rhs) {

		preCheckObject(lhs, rhs);

		Class<?> lhsClass = lhs.getClass();
		if (!lhsClass.isArray()) {
			if (defaultDeepReflect) {
				reflectionEquals(lhs, rhs);
			} else {
				this.setEquals(lhs.equals(rhs));
			}
		} else if (lhs.getClass() != rhs.getClass()) {
			this.setEquals(false);
		} else if (lhs instanceof long[]) {
			append((long[]) lhs, (long[]) rhs);
		} else if (lhs instanceof int[]) {
			append((int[]) lhs, (int[]) rhs);
		} else if (lhs instanceof short[]) {
			append((short[]) lhs, (short[]) rhs);
		} else if (lhs instanceof char[]) {
			append((char[]) lhs, (char[]) rhs);
		} else if (lhs instanceof byte[]) {
			append((byte[]) lhs, (byte[]) rhs);
		} else if (lhs instanceof double[]) {
			append((double[]) lhs, (double[]) rhs);
		} else if (lhs instanceof float[]) {
			append((float[]) lhs, (float[]) rhs);
		} else if (lhs instanceof boolean[]) {
			append((boolean[]) lhs, (boolean[]) rhs);
		} else {
			append((Object[]) lhs, (Object[]) rhs);
		}

		return this;
	}

	public EqualsBuilder append(long lhs, long rhs) {
		if (isEquals == false) {
			return this;
		}
		isEquals = (lhs == rhs);
		return this;
	}

	public EqualsBuilder append(int lhs, int rhs) {
		if (isEquals == false) {
			return this;
		}
		isEquals = (lhs == rhs);
		return this;
	}

	public EqualsBuilder append(short lhs, short rhs) {
		if (isEquals == false) {
			return this;
		}
		isEquals = (lhs == rhs);
		return this;
	}

	public EqualsBuilder append(char lhs, char rhs) {
		if (isEquals == false) {
			return this;
		}
		isEquals = (lhs == rhs);
		return this;
	}

	public EqualsBuilder append(byte lhs, byte rhs) {
		if (isEquals == false) {
			return this;
		}
		isEquals = (lhs == rhs);
		return this;
	}

	public EqualsBuilder append(double lhs, double rhs) {
		if (isEquals == false) {
			return this;
		}
		return append(Double.doubleToLongBits(lhs),
				Double.doubleToLongBits(rhs));
	}

	public EqualsBuilder append(float lhs, float rhs) {

		if (isEquals == false) {
			return this;
		}
		return append(Float.floatToIntBits(lhs), Float.floatToIntBits(rhs));
	}

	public EqualsBuilder append(boolean lhs, boolean rhs) {
		if (isEquals == false) {
			return this;
		}
		isEquals = (lhs == rhs);
		return this;
	}

	public EqualsBuilder append(Object[] lhs, Object[] rhs) {

		preCheckObject(lhs, rhs);
		if (!isEquals) {
			return this;
		}

		if (lhs.length != rhs.length) {
			this.setEquals(false);
			return this;
		}

		for (int i = 0; i < lhs.length && isEquals; i++) {
			append(lhs[i], rhs[i]);
		}
		return this;
	}

	public EqualsBuilder append(long[] lhs, long[] rhs) {

		preCheckObject(lhs, rhs);
		if (!isEquals) {
			return this;
		}

		if (lhs.length != rhs.length) {
			this.setEquals(false);
			return this;
		}

		for (int i = 0; i < lhs.length && isEquals; i++) {
			append(lhs[i], rhs[i]);
		}
		return this;
	}

	public EqualsBuilder append(int[] lhs, int[] rhs) {

		preCheckObject(lhs, rhs);
		if (!isEquals) {
			return this;
		}

		if (lhs.length != rhs.length) {
			this.setEquals(false);
			return this;
		}

		for (int i = 0; i < lhs.length && isEquals; i++) {
			append(lhs[i], rhs[i]);
		}
		return this;
	}

	public EqualsBuilder append(short[] lhs, short[] rhs) {

		preCheckObject(lhs, rhs);
		if (!isEquals) {
			return this;
		}

		if (lhs.length != rhs.length) {
			this.setEquals(false);
			return this;
		}

		for (int i = 0; i < lhs.length && isEquals; i++) {
			append(lhs[i], rhs[i]);
		}
		return this;
	}

	public EqualsBuilder append(char[] lhs, char[] rhs) {

		preCheckObject(lhs, rhs);
		if (!isEquals) {
			return this;
		}

		if (lhs.length != rhs.length) {
			this.setEquals(false);
			return this;
		}

		for (int i = 0; i < lhs.length && isEquals; i++) {
			append(lhs[i], rhs[i]);
		}
		return this;
	}

	public EqualsBuilder append(byte[] lhs, byte[] rhs) {

		preCheckObject(lhs, rhs);
		if (!isEquals) {
			return this;
		}

		if (lhs.length != rhs.length) {
			this.setEquals(false);
			return this;
		}

		for (int i = 0; i < lhs.length && isEquals; i++) {
			append(lhs[i], rhs[i]);
		}
		return this;
	}

	public EqualsBuilder append(double[] lhs, double[] rhs) {

		preCheckObject(lhs, rhs);
		if (!isEquals) {
			return this;
		}

		if (lhs.length != rhs.length) {
			this.setEquals(false);
			return this;
		}

		for (int i = 0; i < lhs.length && isEquals; i++) {
			append(lhs[i], rhs[i]);
		}
		return this;
	}

	public EqualsBuilder append(float[] lhs, float[] rhs) {

		preCheckObject(lhs, rhs);
		if (!isEquals) {
			return this;
		}

		if (lhs.length != rhs.length) {
			this.setEquals(false);
			return this;
		}

		for (int i = 0; i < lhs.length && isEquals; i++) {
			append(lhs[i], rhs[i]);
		}
		return this;
	}

	public EqualsBuilder append(boolean[] lhs, boolean[] rhs) {

		preCheckObject(lhs, rhs);
		if (!isEquals) {
			return this;
		}

		if (lhs.length != rhs.length) {
			this.setEquals(false);
			return this;
		}

		for (int i = 0; i < lhs.length && isEquals; i++) {
			append(lhs[i], rhs[i]);
		}
		return this;
	}

	private void preCheckObject(Object lhs, Object rhs) {
		if (!isEquals) {
			return;
		}
		if (lhs == rhs) {
			return;
		}
		if (lhs == null || rhs == null) {
			this.setEquals(false);
			return;
		}
	}

	private void setEquals(boolean equals) {
		this.isEquals = equals;
	}

	public boolean isEquals() {
		return this.isEquals;
	}

	public void reset() {
		seenReferences = new IdentityHashMap<Tuple<Object, Object>, Tuple<Object, Object>>();
		this.isEquals = true;
	}
}
