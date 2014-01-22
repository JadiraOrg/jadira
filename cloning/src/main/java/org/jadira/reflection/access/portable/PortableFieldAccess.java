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
 *  WITHOUT WARRANTIES OR CONDITIONS OObject ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.reflection.access.portable;

import java.lang.reflect.Field;

import org.jadira.reflection.access.api.FieldAccess;

/**
 * FieldAccess implementation which should be portable across most JVMs. 
 * @param <C> The Class to be accessed
 */
public class PortableFieldAccess<C> implements FieldAccess<C> {

	private Field field;
	private Class<C> declaringClass;	
	private Class<?> type;	

	@SuppressWarnings("unchecked")
	private PortableFieldAccess(Field f) {
		this.field = f;
		if (!field.isAccessible()) {
			field.setAccessible(true);
		}
		this.declaringClass = (Class<C>) f.getDeclaringClass();
		this.type = (Class<?>) f.getType();
	}
	
	@Override
	public Class<C> declaringClass() {
		return declaringClass;
	}

	@Override
	public Class<?> fieldClass() {
		return type;
	}

	@Override
	public Field field() {
		return field;
	}
	
	@Override
	public Object getValue(C parent) {
		try {
			return (Object) field.get(parent);
		} catch (IllegalArgumentException e) {
            throw new IllegalStateException("Problem target object {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Problem accessing {" + field.getName() + "} of object {"
                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
        }
	}

	@Override
	public boolean getBooleanValue(C parent) {
		throw new UnsupportedOperationException("Not supported for this field type");
	}
	
	@Override
	public byte getByteValue(C parent) {
		throw new UnsupportedOperationException("Not supported for this field type");
	}
	
	@Override
	public char getCharValue(C parent) {
		throw new UnsupportedOperationException("Not supported for this field type");
	}
	
	@Override
	public short getShortValue(C parent) {
		throw new UnsupportedOperationException("Not supported for this field type");
	}
	
	@Override
	public int getIntValue(C parent) {
		throw new UnsupportedOperationException("Not supported for this field type");
	}
	
	@Override
	public long getLongValue(C parent) {
		throw new UnsupportedOperationException("Not supported for this field type");
	}
	
	@Override
	public float getFloatValue(C parent) {
		throw new UnsupportedOperationException("Not supported for this field type");
	}
	
	@Override
	public double getDoubleValue(C parent) {
		throw new UnsupportedOperationException("Not supported for this field type");
	}
	
	@Override
	public void putValue(C parent, Object newFieldValue) {
        try {
            field.set(parent, newFieldValue);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Problem argument for field {" + field.getName() + "} of object {"
                    + System.identityHashCode(newFieldValue) + "}: " + e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Problem accessing for field {" + field.getName() + "} of object {"
                    + System.identityHashCode(newFieldValue) + "}: " + e.getMessage(), e);
        }
	}
	
	@Override
	public void putBooleanValue(C parent, boolean newFieldValue) {
		throw new UnsupportedOperationException("Not supported for this field type");
	}
	
	@Override
	public void putByteValue(C parent, byte newFieldValue) {
		throw new UnsupportedOperationException("Not supported for this field type");
	}
	
	@Override
	public void putCharValue(C parent, char newFieldValue) {
		throw new UnsupportedOperationException("Not supported for this field type");
	}
	
	@Override
	public void putShortValue(C parent, short newFieldValue) {
		throw new UnsupportedOperationException("Not supported for this field type");
	}
	
	@Override
	public void putIntValue(C parent, int newFieldValue) {
		throw new UnsupportedOperationException("Not supported for this field type");
	}
	
	@Override
	public void putLongValue(C parent, long newFieldValue) {
		throw new UnsupportedOperationException("Not supported for this field type");
	}
	
	@Override
	public void putFloatValue(C parent, float newFieldValue) {
		throw new UnsupportedOperationException("Not supported for this field type");
	}
	
	@Override
	public void putDoubleValue(C parent, double newFieldValue) {
		throw new UnsupportedOperationException("Not supported for this field type");
	}
	
	/**
	 * Get a new instance that can access the given Field
	 * @param f Field to be accessed
	 * @return New PortableFieldAccess instance
	 */
	public static <C> PortableFieldAccess<C> get(Field f) {
		
		Class<?> type = (Class<?>) f.getType();
		
		if (type.isPrimitive()) {
			if (java.lang.Boolean.TYPE == type) {
				return (PortableFieldAccess<C>) new PortableBooleanFieldAccess<C>(f);
			} else if (java.lang.Byte.TYPE == type) {
				return (PortableFieldAccess<C>) new PortableByteFieldAccess<C>(f);
			} else if (java.lang.Character.TYPE == type) {
				return (PortableFieldAccess<C>) new PortableCharFieldAccess<C>(f);
			} else if (java.lang.Short.TYPE == type) {
				return (PortableFieldAccess<C>) new PortableShortFieldAccess<C>(f);
			} else if (java.lang.Integer.TYPE == type) {
				return (PortableFieldAccess<C>) new PortableIntFieldAccess<C>(f);
			} else if (java.lang.Long.TYPE == type) {
				return (PortableFieldAccess<C>) new PortableLongFieldAccess<C>(f);
			} else if (java.lang.Float.TYPE == type) {
				return (PortableFieldAccess<C>) new PortableFloatFieldAccess<C>(f);
			} else if (java.lang.Double.TYPE == type) {
				return (PortableFieldAccess<C>) new PortableDoubleFieldAccess<C>(f);
			}
		}
		return new PortableFieldAccess<C>(f);
	}
	
	/**
	 * PortableFieldAccess implementation suitable for accessing boolean fields
	 * @param <C> The Class containing the Field to be accessed
	 */
	public static class PortableBooleanFieldAccess<C> extends PortableFieldAccess<C> {

		/**
		 * Construct a new instance for the given Field
		 * @param f The Field to be accessed
		 */
		public PortableBooleanFieldAccess(Field f) {
			super(f);
		}

		@Override
		public boolean getBooleanValue(C parent) {
			try {
				return super.field.getBoolean(parent);
			} catch (IllegalArgumentException e) {
	            throw new IllegalStateException("Problem target object {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
	        } catch (IllegalAccessException e) {
	            throw new IllegalStateException("Problem accessing {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
	        }
		}
		
		@Override
		public void putBooleanValue(C parent, boolean newFieldValue) {
	        try {
	            super.field.setBoolean(parent, newFieldValue);
	        } catch (IllegalArgumentException e) {
	            throw new IllegalStateException("Problem argument for field {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(newFieldValue) + "}: " + e.getMessage(), e);
	        } catch (IllegalAccessException e) {
	            throw new IllegalStateException("Problem accessing for field {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(newFieldValue) + "}: " + e.getMessage(), e);
	        }
		}
	}

	/**
	 * PortableFieldAccess implementation suitable for accessing byte fields
	 * @param <C> The Class containing the Field to be accessed
	 */
	public static class PortableByteFieldAccess<C> extends PortableFieldAccess<C> {

		/**
		 * Construct a new instance for the given Field
		 * @param f The Field to be accessed
		 */
		public PortableByteFieldAccess(Field f) {
			super(f);
		}

		@Override
		public byte getByteValue(C parent) {
			try {
				return super.field.getByte(parent);
			} catch (IllegalArgumentException e) {
	            throw new IllegalStateException("Problem target object {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
	        } catch (IllegalAccessException e) {
	            throw new IllegalStateException("Problem accessing {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
	        }
		}
		
		@Override
		public void putByteValue(C parent, byte newFieldValue) {
	        try {
	            super.field.setByte(parent, newFieldValue);
	        } catch (IllegalArgumentException e) {
	            throw new IllegalStateException("Problem argument for field {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(newFieldValue) + "}: " + e.getMessage(), e);
	        } catch (IllegalAccessException e) {
	            throw new IllegalStateException("Problem accessing for field {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(newFieldValue) + "}: " + e.getMessage(), e);
	        }
		}
	}
	
	/**
	 * PortableFieldAccess implementation suitable for accessing char fields
	 * @param <C> The Class containing the Field to be accessed
	 */
	public static class PortableCharFieldAccess<C> extends PortableFieldAccess<C> {

		/**
		 * Construct a new instance for the given Field
		 * @param f The Field to be accessed
		 */
		public PortableCharFieldAccess(Field f) {
			super(f);
		}

		@Override
		public char getCharValue(C parent) {
			try {
				return super.field.getChar(parent);
			} catch (IllegalArgumentException e) {
	            throw new IllegalStateException("Problem target object {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
	        } catch (IllegalAccessException e) {
	            throw new IllegalStateException("Problem accessing {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
	        }

		}
		
		@Override
		public void putCharValue(C parent, char newFieldValue) {
	        try {
	            super.field.setChar(parent, newFieldValue);
	        } catch (IllegalArgumentException e) {
	            throw new IllegalStateException("Problem argument for field {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(newFieldValue) + "}: " + e.getMessage(), e);
	        } catch (IllegalAccessException e) {
	            throw new IllegalStateException("Problem accessing for field {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(newFieldValue) + "}: " + e.getMessage(), e);
	        }
		}
	}
	
	/**
	 * PortableFieldAccess implementation suitable for accessing short fields
	 * @param <C> The Class containing the Field to be accessed
	 */
	public static class PortableShortFieldAccess<C> extends PortableFieldAccess<C> {

		/**
		 * Construct a new instance for the given Field
		 * @param f The Field to be accessed
		 */
		public PortableShortFieldAccess(Field f) {
			super(f);
		}

		@Override
		public short getShortValue(C parent) {
			try {
				return super.field.getShort(parent);
			} catch (IllegalArgumentException e) {
	            throw new IllegalStateException("Problem target object {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
	        } catch (IllegalAccessException e) {
	            throw new IllegalStateException("Problem accessing {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
	        }

		}
		
		@Override
		public void putShortValue(C parent, short newFieldValue) {
	        try {
	            super.field.setShort(parent, newFieldValue);
	        } catch (IllegalArgumentException e) {
	            throw new IllegalStateException("Problem argument for field {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(newFieldValue) + "}: " + e.getMessage(), e);
	        } catch (IllegalAccessException e) {
	            throw new IllegalStateException("Problem accessing for field {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(newFieldValue) + "}: " + e.getMessage(), e);
	        }
		}
	}
	
	/**
	 * PortableFieldAccess implementation suitable for accessing int fields
	 * @param <C> The Class containing the Field to be accessed
	 */
	public static class PortableIntFieldAccess<C> extends PortableFieldAccess<C> {

		/**
		 * Construct a new instance for the given Field
		 * @param f The Field to be accessed
		 */
		public PortableIntFieldAccess(Field f) {
			super(f);
		}

		@Override
		public int getIntValue(C parent) {
			try {
				return super.field.getInt(parent);
			} catch (IllegalArgumentException e) {
	            throw new IllegalStateException("Problem target object {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
	        } catch (IllegalAccessException e) {
	            throw new IllegalStateException("Problem accessing {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
	        }

		}
		
		@Override
		public void putIntValue(C parent, int newFieldValue) {
	        try {
	            super.field.setInt(parent, newFieldValue);
	        } catch (IllegalArgumentException e) {
	            throw new IllegalStateException("Problem argument for field {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(newFieldValue) + "}: " + e.getMessage(), e);
	        } catch (IllegalAccessException e) {
	            throw new IllegalStateException("Problem accessing for field {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(newFieldValue) + "}: " + e.getMessage(), e);
	        }
		}
	}
	
	/**
	 * PortableFieldAccess implementation suitable for accessing long fields
	 * @param <C> The Class containing the Field to be accessed
	 */
	public static class PortableLongFieldAccess<C> extends PortableFieldAccess<C> {

		/**
		 * Construct a new instance for the given Field
		 * @param f The Field to be accessed
		 */
		public PortableLongFieldAccess(Field f) {
			super(f);
		}

		@Override
		public long getLongValue(C parent) {
			try {
				return super.field.getLong(parent);
			} catch (IllegalArgumentException e) {
	            throw new IllegalStateException("Problem target object {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
	        } catch (IllegalAccessException e) {
	            throw new IllegalStateException("Problem accessing {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
	        }

		}
		
		@Override
		public void putLongValue(C parent, long newFieldValue) {
	        try {
	            super.field.setLong(parent, newFieldValue);
	        } catch (IllegalArgumentException e) {
	            throw new IllegalStateException("Problem argument for field {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(newFieldValue) + "}: " + e.getMessage(), e);
	        } catch (IllegalAccessException e) {
	            throw new IllegalStateException("Problem accessing for field {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(newFieldValue) + "}: " + e.getMessage(), e);
	        }
		}
	}
	
	/**
	 * PortableFieldAccess implementation suitable for accessing float fields
	 * @param <C> The Class containing the Field to be accessed
	 */
	public static class PortableFloatFieldAccess<C> extends PortableFieldAccess<C> {

		/**
		 * Construct a new instance for the given Field
		 * @param f The Field to be accessed
		 */
		public PortableFloatFieldAccess(Field f) {
			super(f);
		}

		@Override
		public float getFloatValue(C parent) {
			try {
				return super.field.getFloat(parent);
			} catch (IllegalArgumentException e) {
	            throw new IllegalStateException("Problem target object {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
	        } catch (IllegalAccessException e) {
	            throw new IllegalStateException("Problem accessing {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
	        }

		}
		
		@Override
		public void putFloatValue(C parent, float newFieldValue) {
	        try {
	            super.field.setFloat(parent, newFieldValue);
	        } catch (IllegalArgumentException e) {
	            throw new IllegalStateException("Problem argument for field {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(newFieldValue) + "}: " + e.getMessage(), e);
	        } catch (IllegalAccessException e) {
	            throw new IllegalStateException("Problem accessing for field {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(newFieldValue) + "}: " + e.getMessage(), e);
	        }
		}
	}
	
	/**
	 * PortableFieldAccess implementation suitable for accessing double fields
	 * @param <C> The Class containing the Field to be accessed
	 */
	public static class PortableDoubleFieldAccess<C> extends PortableFieldAccess<C> {

		/**
		 * Construct a new instance for the given Field
		 * @param f The Field to be accessed
		 */
		public PortableDoubleFieldAccess(Field f) {
			super(f);
		}

		@Override
		public double getDoubleValue(C parent) {
			try {
				return super.field.getDouble(parent);
			} catch (IllegalArgumentException e) {
	            throw new IllegalStateException("Problem target object {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
	        } catch (IllegalAccessException e) {
	            throw new IllegalStateException("Problem accessing {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(parent) + "}: " + e.getMessage(), e);
	        }

		}
		
		@Override
		public void putDoubleValue(C parent, double newFieldValue) {
	        try {
	            super.field.setDouble(parent, newFieldValue);
	        } catch (IllegalArgumentException e) {
	            throw new IllegalStateException("Problem argument for field {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(newFieldValue) + "}: " + e.getMessage(), e);
	        } catch (IllegalAccessException e) {
	            throw new IllegalStateException("Problem accessing for field {" + super.field.getName() + "} of object {"
	                    + System.identityHashCode(newFieldValue) + "}: " + e.getMessage(), e);
	        }
		}
	}
}
