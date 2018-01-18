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

import java.lang.reflect.Field;
import java.util.IdentityHashMap;

import org.jadira.reflection.access.model.ClassModel;
import org.jadira.reflection.access.model.FieldModel;
import org.jadira.reflection.access.portable.PortableClassAccess;
import org.jadira.reflection.cloning.api.CloneDriver;
import org.jadira.reflection.cloning.api.CloneStrategy;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

/**
 * A CloneStrategy that uses reflection
 */
public class PortableCloneStrategy extends AbstractCloneStrategy implements CloneStrategy {

	private static final char CHAR_NULL = '\u0000';

	private static final Objenesis objenesis = new ObjenesisStd();

	@Override
	public <T> T newInstance(Class<T> c) {
		return (T) objenesis.newInstance(c);
	}

	private static PortableCloneStrategy instance = new PortableCloneStrategy();

	/**
	 * Creates a new instance
	 */
	public PortableCloneStrategy() {
	}

    /**
     * Returns a shared instance of PortableCloneStrategy
     * @return The instance
     */
	public static PortableCloneStrategy getInstance() {
		return instance;
	}

	@Override
	protected <T> void handleTransientField(T copy, FieldModel<T> f) {

		Class<?> clazz = f.getField().getType();
		try {
			if (clazz.isPrimitive()) {
				if (java.lang.Boolean.TYPE == clazz) {
					f.getField().setBoolean(copy, false);
				} else if (java.lang.Byte.TYPE == clazz) {
					f.getField().setByte(copy, (byte) 0);
				} else if (java.lang.Character.TYPE == clazz) {
					f.getField().setChar(copy, CHAR_NULL);
				} else if (java.lang.Short.TYPE == clazz) {
					f.getField().setShort(copy, (short) 0);
				} else if (java.lang.Integer.TYPE == clazz) {
					f.getField().setInt(copy, 0);
				} else if (java.lang.Long.TYPE == clazz) {
					f.getField().setLong(copy, 0L);
				} else if (java.lang.Float.TYPE == clazz) {
					f.getField().setFloat(copy, 0.0F);
				} else if (java.lang.Double.TYPE == clazz) {
					f.getField().setDouble(copy, 0.0D);
				}
			} else {
				f.getField().set(copy, null);
			}
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("Problem performing clone for field {" + f.getField().getName() + "} of object {" + System.identityHashCode(copy) + "}: " + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Problem performing clone for field {" + f.getField().getName() + "} of object {" + System.identityHashCode(copy) + "}: " + e.getMessage(), e);
		}
	}

	@Override
	protected <W> ClassModel<W> getClassModel(Class<W> clazz) {
		return PortableClassAccess.get(clazz).getClassModel();
	}

	@Override
	protected <T> void handleClonePrimitiveField(T obj, T copy, CloneDriver driver, FieldModel<T> f, IdentityHashMap<Object, Object> referencesToReuse) {

		Field field = f.getField();

		try {
			Class<?> clazz = f.getFieldClass();

			if (java.lang.Boolean.TYPE == clazz) {
				field.setBoolean(copy, field.getBoolean(obj));
			} else if (java.lang.Byte.TYPE == clazz) {
				field.setByte(copy, field.getByte(obj));
			} else if (java.lang.Character.TYPE == clazz) {
				field.setChar(copy, field.getChar(obj));
			} else if (java.lang.Short.TYPE == clazz) {
				field.setShort(copy, field.getShort(obj));
			} else if (java.lang.Integer.TYPE == clazz) {
				field.setInt(copy, field.getInt(obj));
			} else if (java.lang.Long.TYPE == clazz) {
				field.setLong(copy, field.getLong(obj));
			} else if (java.lang.Float.TYPE == clazz) {
				field.setFloat(copy, field.getFloat(obj));
			} else if (java.lang.Double.TYPE == clazz) {
				field.setDouble(copy, field.getDouble(obj));
			} else {
				throw new IllegalStateException("Expected primitive but was :" + clazz.getName());
			}

		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("Problem performing clone for field {" + field.getName() + "} of object {" + System.identityHashCode(obj) + "}: " + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Problem performing clone for field {" + field.getName() + "} of object {" + System.identityHashCode(obj) + "}: " + e.getMessage(), e);
		}
	}

	@Override
	protected <T> Object getFieldValue(T obj, FieldModel<T> f) {

		final Field field = f.getField();

		final Object fieldObject;

		try {
			fieldObject = field.get(obj);

		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("Problem performing clone for field {" + field.getName() + "} of object {" + System.identityHashCode(obj) + "}: " + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Problem performing clone for field {" + field.getName() + "} of object {" + System.identityHashCode(obj) + "}: " + e.getMessage(), e);
		}

		return fieldObject;
	}

	@Override
	protected <T> void putFieldValue(T obj, FieldModel<T> f, Object value) {

		final Field field = f.getField();

		try {
			field.set(obj, value);

		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("Problem performing clone for field {" + field.getName() + "} of object {" + System.identityHashCode(obj) + "}: " + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Problem performing clone for field {" + field.getName() + "} of object {" + System.identityHashCode(obj) + "}: " + e.getMessage(), e);
		}
	}
}
