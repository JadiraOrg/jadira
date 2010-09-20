/*
 *  Copyright 2010 Christopher Pheby
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
package org.jadira.usertype.dateandtime.shared.reflectionutils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.hibernate.type.Type;
import org.jadira.usertype.dateandtime.shared.spi.ColumnMapper;

public class Hibernate36Helper {

    protected static final Class<?> ABSTRACT_STANDARD_BASIC_TYPE_CLASS = getAbstractStandardBasicTypeClass();
    protected static final Class<?> ABSTRACT_SINGLE_COLUMN_STANDARD_BASIC_TYPE_CLASS = getAbstractSingleColumnStandardBasicTypeClass();
    protected static final Class<?> STANDARD_BASIC_TYPES_CLASS = getStandardBasicTypesClass();
    protected static final Class<?> WRAPPER_OPTIONS_CLASS = getWrapperOptionsClass();
    public static final boolean USE_STANDARD_BASIC_TYPE_API = ABSTRACT_STANDARD_BASIC_TYPE_CLASS == null ? false : true;    
    protected static final Object NO_WRAPPER_OPTIONS = getNoWrapperOptions(); 
    protected static final Method NULL_SAFE_GET_METHOD = getNullSafeGetMethod();
    protected static final Method NULL_SAFE_SET_METHOD = getNullSafeSetMethod();
    protected static final Method TOSTRING_METHOD = getToStringMethod();
    
    private Hibernate36Helper(){
    }
    
    public static Object nullSafeGet(ColumnMapper<?,?> mapper, ResultSet resultSet, String string) {
    	try {
			return NULL_SAFE_GET_METHOD.invoke(mapper.getHibernateType(), new Object[]{ resultSet, string, NO_WRAPPER_OPTIONS });
		} catch (IllegalArgumentException e) {
			throw new ReflectionException("Problem invoking nullSafeGet Method: " + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException("Problem invoking nullSafeGet Method: " + e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new ReflectionException("Problem invoking nullSafeGet Method: " + e.getMessage(), e);
		}
    }
    
	public static void nullSafeSet(ColumnMapper<?,?> mapper, PreparedStatement preparedStatement, Object object, int index) {
    	try {
			NULL_SAFE_SET_METHOD.invoke(mapper.getHibernateType(), new Object[]{ preparedStatement, object, index, NO_WRAPPER_OPTIONS });
		} catch (IllegalArgumentException e) {
			throw new ReflectionException("Problem invoking nullSafeGet Method: " + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException("Problem invoking nullSafeGet Method: " + e.getMessage(), e);
		} catch (InvocationTargetException e) {
			throw new ReflectionException("Problem invoking nullSafeGet Method: " + e.getMessage(), e);
		}
	}
	
	public static String nullSafeToString(ColumnMapper<?,?> mapper, Object object) {
    	
		if (object == null) {
			return null;
		} else {
			try {
				return (String) TOSTRING_METHOD.invoke(mapper.getHibernateType(), new Object[]{ object });
			} catch (IllegalArgumentException e) {
				throw new ReflectionException("Problem invoking nullSafeGet Method: " + e.getMessage(), e);
			} catch (IllegalAccessException e) {
				throw new ReflectionException("Problem invoking nullSafeGet Method: " + e.getMessage(), e);
			} catch (InvocationTargetException e) {
				throw new ReflectionException("Problem invoking nullSafeGet Method: " + e.getMessage(), e);
			}
		}
	}
    
    private static Class<?> getAbstractStandardBasicTypeClass() {
    	try {
    		return Class.forName("org.hibernate.type.AbstractStandardBasicType");
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
    
    private static Class<?> getAbstractSingleColumnStandardBasicTypeClass() {
    	try {
    		return Class.forName("org.hibernate.type.AbstractSingleColumnStandardBasicType");
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

    private static Class<?> getStandardBasicTypesClass() {
    	try {
    		return Class.forName("org.hibernate.type.StandardBasicTypes");
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
    
    private static Class<?> getWrapperOptionsClass() {
    	try {
    		return Class.forName("org.hibernate.type.descriptor.WrapperOptions");
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
    
    private static Method getNullSafeGetMethod() {
    	
    	if (ABSTRACT_STANDARD_BASIC_TYPE_CLASS == null) {
    		return null;
    	}
    	
    	try {
			Method method = ABSTRACT_STANDARD_BASIC_TYPE_CLASS.getDeclaredMethod("nullSafeGet", new Class[] { ResultSet.class, String.class, WRAPPER_OPTIONS_CLASS } );
			method.setAccessible(true);
			
			return method;
		} catch (SecurityException e) {
			throw new ReflectionException("Problem retrieving nullSafeGet Method: " + e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new ReflectionException("Problem retrieving nullSafeGet Method: " + e.getMessage(), e);
		}
	}

    private static Method getNullSafeSetMethod() {
    	
    	if (ABSTRACT_STANDARD_BASIC_TYPE_CLASS == null) {
    		return null;
    	}
    	
    	try {
			Method method = ABSTRACT_STANDARD_BASIC_TYPE_CLASS.getDeclaredMethod("nullSafeSet", new Class[] { PreparedStatement.class, Object.class, Integer.TYPE, WRAPPER_OPTIONS_CLASS } );
			method.setAccessible(true);
			
			return method;
		} catch (SecurityException e) {
			throw new ReflectionException("Problem retrieving nullSafeGet Method: " + e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new ReflectionException("Problem retrieving nullSafeGet Method: " + e.getMessage(), e);
		}
	}
    
    private static Method getToStringMethod() {
    	
    	if (ABSTRACT_STANDARD_BASIC_TYPE_CLASS == null) {
    		return null;
    	}
    	
    	try {
			return ABSTRACT_STANDARD_BASIC_TYPE_CLASS.getMethod("toString", new Class[] { Object.class } );
		} catch (SecurityException e) {
			throw new ReflectionException("Problem retrieving toString Method: " + e.getMessage(), e);
		} catch (NoSuchMethodException e) {
			throw new ReflectionException("Problem retrieving toString Method: " + e.getMessage(), e);
		}
	}

	public static Type getHibernateType(String name) {
		
		try {
			Field field = getStandardBasicTypesClass().getField(name);
			return (Type) field.get(null);
		} catch (SecurityException e) {
			throw new ReflectionException("Problem retrieving Hibernate type {" + name + "}: " + e.getMessage(), e);
		} catch (NoSuchFieldException e) {
			throw new ReflectionException("Problem retrieving Hibernate type {" + name + "}: " + e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new ReflectionException("Problem retrieving Hibernate type {" + name + "}: " + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException("Problem retrieving Hibernate type {" + name + "}: " + e.getMessage(), e);
		}
		
	}
    
	private static Object getNoWrapperOptions() {
    	
		if (ABSTRACT_SINGLE_COLUMN_STANDARD_BASIC_TYPE_CLASS == null) {
    		return null;
    	}
		
    	try {
			Field field = ABSTRACT_SINGLE_COLUMN_STANDARD_BASIC_TYPE_CLASS.getDeclaredField("NO_OPTIONS");
			field.setAccessible(true);
			
			return field.get(null);
		} catch (SecurityException e) {
			throw new ReflectionException("Problem retrieving NoWrapperOptions: " + e.getMessage(), e);
		} catch (NoSuchFieldException e) {
			throw new ReflectionException("Problem retrieving NoWrapperOptions: " + e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			throw new ReflectionException("Problem retrieving NoWrapperOptions: " + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException("Problem retrieving NoWrapperOptions: " + e.getMessage(), e);
		}
	}
}
