package org.jadira.cloning.implementor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.IdentityHashMap;

import org.jadira.cloning.api.CloneDriver;
import org.jadira.cloning.api.CloneStrategy;
import org.jadira.cloning.model.PortableClassModel;
import org.jadira.cloning.model.PortableFieldModel;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

public class PortableCloneStrategy extends AbstractCloneStrategy<PortableClassModel, PortableFieldModel> implements CloneStrategy {

	private static final char CHAR_NULL = '\u0000';

	private static final Objenesis objenesis = new ObjenesisStd();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T newInstance(Class<T> c) {
		return (T) objenesis.newInstance(c);
	}

	private static PortableCloneStrategy instance = new PortableCloneStrategy();

	public static PortableCloneStrategy getInstance() {
		return instance;
	}

	protected <T> void handleTransientField(T copy, PortableFieldModel f) {

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

	protected <T> void handleCloneField(T obj, T copy, CloneDriver driver, PortableFieldModel f, IdentityHashMap<Object, Object> referencesToReuse) {

		Field field = f.getField();
		
		try {
			Class<?> clazz = field.getClass();

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
			} else if (!driver.isCloneSyntheticFields() && f.isSynthetic()) {
				field.set(copy, field.get(obj));
			} else {

				Object fieldObject;
				fieldObject = field.get(obj);
				final Object fieldObjectClone = clone(fieldObject, driver, referencesToReuse);
				field.set(copy, fieldObjectClone);
			}
		} catch (IllegalArgumentException e) {
			throw new IllegalStateException("Problem performing clone for field {" + field.getName() + "} of object {" + System.identityHashCode(obj) + "}: " + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException("Problem performing clone for field {" + field.getName() + "} of object {" + System.identityHashCode(obj) + "}: " + e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	protected <T> T handleArray(T origFieldValue, CloneDriver context, IdentityHashMap<Object, Object> visited) {

		if (visited.containsKey(origFieldValue)) {
			return (T) visited.get(origFieldValue);
		}

		final Class<?> componentType = origFieldValue.getClass().getComponentType();

		T result = null;

		if (componentType.getName().length() <= 7) {

			if (java.lang.Boolean.TYPE.isAssignableFrom(componentType)) {
				result = (T) Arrays.copyOf((boolean[]) origFieldValue, ((boolean[]) origFieldValue).length);
			} else if (java.lang.Byte.TYPE.isAssignableFrom(componentType)) {
				result = (T) Arrays.copyOf((byte[]) origFieldValue, ((byte[]) origFieldValue).length);
			} else if (java.lang.Character.TYPE.isAssignableFrom(componentType)) {
				result = (T) Arrays.copyOf((char[]) origFieldValue, ((char[]) origFieldValue).length);
			} else if (java.lang.Short.TYPE.isAssignableFrom(componentType)) {
				result = (T) Arrays.copyOf((short[]) origFieldValue, ((short[]) origFieldValue).length);
			} else if (java.lang.Integer.TYPE.isAssignableFrom(componentType)) {
				result = (T) Arrays.copyOf((int[]) origFieldValue, ((int[]) origFieldValue).length);
			} else if (java.lang.Long.TYPE.isAssignableFrom(componentType)) {
				result = (T) Arrays.copyOf((long[]) origFieldValue, ((long[]) origFieldValue).length);
			} else if (java.lang.Float.TYPE.isAssignableFrom(componentType)) {
				result = (T) Arrays.copyOf((float[]) origFieldValue, ((float[]) origFieldValue).length);
			} else if (java.lang.Double.TYPE.isAssignableFrom(componentType)) {
				result = (T) Arrays.copyOf((double[]) origFieldValue, ((double[]) origFieldValue).length);
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
			result = (T) array;
		}

		visited.put(origFieldValue, result);
		return result;
	}

	@Override
	protected PortableClassModel getClassModel(Class<?> clazz) {
		return PortableClassModel.get(clazz);
	}

	@Override
	public void initialiseFor(Class<?>... classes) {

		for (Class<?> clazz : classes) {
			PortableClassModel.get(clazz);
		}
	}
}
