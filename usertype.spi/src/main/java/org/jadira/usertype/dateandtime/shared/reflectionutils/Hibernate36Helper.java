/*
 *  Copyright 2010 Christopher Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the Licensex.
 *  You may obtain a copy of the License at
 *
 *      http://www.apachex.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the Licensex.
 */
package org.jadira.usertype.dateandtime.shared.reflectionutils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.hibernate.type.Type;
import org.jadira.usertype.dateandtime.shared.spi.ColumnMapper;

/**
 * Classes for invoking Hibernate 3.6 type APIs using reflection in order
 * to attain runtime compatibility with all recent Hibernate versions including 3.5 and 3.6.
 */
public final class Hibernate36Helper {

    protected static final Class<?> ABSTRACT_STANDARD_BASIC_TYPE_CLASS = getAbstractStandardBasicTypeClass();
    protected static final boolean USE_STANDARD_BASIC_TYPE_API = ABSTRACT_STANDARD_BASIC_TYPE_CLASS == null ? false : true;
    
    protected static final Class<?> ABSTRACT_SINGLE_COLUMN_STANDARD_BASIC_TYPE_CLASS = getAbstractSingleColumnStandardBasicTypeClass();
    protected static final Class<?> STANDARD_BASIC_TYPES_CLASS = getStandardBasicTypesClass();
    protected static final Class<?> WRAPPER_OPTIONS_CLASS = getWrapperOptionsClass();
    protected static final Object NO_WRAPPER_OPTIONS = getNoWrapperOptions();
    protected static final Method NULL_SAFE_GET_METHOD = getNullSafeGetMethod();
    protected static final Method NULL_SAFE_SET_METHOD = getNullSafeSetMethod();
    protected static final Method TOSTRING_METHOD = getToStringMethod();

    private Hibernate36Helper() {
    }

    public static boolean isHibernate36ApiAvailable() {
        return USE_STANDARD_BASIC_TYPE_API;
    }
    
    public static Type getHibernateType(String name) {

        return (Type) readField(getStandardBasicTypesClass(), name, false);
    }

    public static Object nullSafeGet(ColumnMapper<?, ?> mapper,
            ResultSet resultSet, String string) {

        return invokeMethod(NULL_SAFE_GET_METHOD, mapper.getHibernateType(),
                    new Object[] { resultSet, string, NO_WRAPPER_OPTIONS });
    }

    public static void nullSafeSet(ColumnMapper<?, ?> mapper,
            PreparedStatement preparedStatement, Object object, int index) {

        invokeMethod(NULL_SAFE_SET_METHOD, mapper.getHibernateType(),
                    new Object[] { preparedStatement, object, index, NO_WRAPPER_OPTIONS });
    }

    public static String nullSafeToString(ColumnMapper<?, ?> mapper,
            Object object) {

        if (object == null) {
            return null;
        } else {
            return (String) invokeMethod(TOSTRING_METHOD, mapper.getHibernateType(), new Object[] { object });
        }
    }

    private static Class<?> getAbstractStandardBasicTypeClass() {

        return safeClassForName("org.hibernate.type.AbstractStandardBasicType");
    }

    private static Class<?> getAbstractSingleColumnStandardBasicTypeClass() {

        return safeClassForName("org.hibernate.type.AbstractSingleColumnStandardBasicType");
    }

    private static Class<?> getStandardBasicTypesClass() {

        return safeClassForName("org.hibernate.type.StandardBasicTypes");
    }

    private static Class<?> getWrapperOptionsClass() {

        return safeClassForName("org.hibernate.type.descriptor.WrapperOptions");
    }

    private static Method getNullSafeGetMethod() {

        if (ABSTRACT_STANDARD_BASIC_TYPE_CLASS == null) {
            return null;
        }

        return obtainDeclaredMethod(ABSTRACT_STANDARD_BASIC_TYPE_CLASS, "nullSafeGet", new Class[] { ResultSet.class, String.class, WRAPPER_OPTIONS_CLASS });
    }

    private static Method getNullSafeSetMethod() {

        if (ABSTRACT_STANDARD_BASIC_TYPE_CLASS == null) {
            return null;
        }

        return obtainDeclaredMethod(ABSTRACT_STANDARD_BASIC_TYPE_CLASS, "nullSafeSet", new Class[] { PreparedStatement.class, Object.class, Integer.TYPE, WRAPPER_OPTIONS_CLASS });
    }

    private static Method getToStringMethod() {

        if (ABSTRACT_STANDARD_BASIC_TYPE_CLASS == null) {
            return null;
        }

        return obtainDeclaredMethod(ABSTRACT_STANDARD_BASIC_TYPE_CLASS, "toString", new Class[] { Object.class });
    }

    private static Object getNoWrapperOptions() {

        if (ABSTRACT_SINGLE_COLUMN_STANDARD_BASIC_TYPE_CLASS == null) {
            return null;
        }
        
        return readField(ABSTRACT_SINGLE_COLUMN_STANDARD_BASIC_TYPE_CLASS, "NO_OPTIONS", true);
    }

    private static Class<?> safeClassForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }
    
    private static Method obtainDeclaredMethod(Class<?> clazz, String methodName, Class<?>... params) {

        try {
            final Method method = clazz.getDeclaredMethod(methodName, params);
            method.setAccessible(true);
            return method;
            
        } catch (SecurityException ex1) {
            throw new ReflectionException(
                    "Problem accessing " + methodName + " Method: " + ex1.getMessage(), ex1);
        } catch (NoSuchMethodException ex2) {
            throw new ReflectionException(
                    "Problem retrieving " + methodName + " Method: " + ex2.getMessage(), ex2);
        }

    }

    private static Object invokeMethod(Method method, Object target, Object... params) {
        try {
            return method.invoke(target, params);
        } catch (IllegalArgumentException ex1) {
            throw new ReflectionException(
                    "Problem with argument for " + method.getName() + " Method: "
                            + ex1.getMessage(), ex1);
        } catch (IllegalAccessException ex2) {
            throw new ReflectionException(
                    "Problem accessing " + method.getName() + " Method: "
                            + ex2.getMessage(), ex2);
        } catch (InvocationTargetException ex3) {
            throw new ReflectionException(
                    "Problem invoking " + method.getName() + " Method: "
                            + ex3.getMessage(), ex3);
        }
    }
    
    private static Object readField(Class<?> clazz, String fieldName, boolean isDeclared) {
        
        try {
            final Field field; 
            if (isDeclared) {
                field = clazz.getDeclaredField(fieldName);
            } else {
                field = clazz.getField(fieldName);
            }
            field.setAccessible(true);
            return field.get(null);
        } catch (SecurityException ex1) {
            throw new ReflectionException("Problem reading Field " + fieldName + ": " + ex1.getMessage(), ex1);
        } catch (IllegalArgumentException ex2) {
            throw new ReflectionException("Incorrect argument supplied when reading Field " + fieldName + ": " + ex2.getMessage(), ex2);
        } catch (IllegalAccessException ex3) {
            throw new ReflectionException("Problem accessing Field " + fieldName + ": " + ex3.getMessage(), ex3);
        } catch (NoSuchFieldException ex3) {
            throw new ReflectionException("Problem obtaining Field instance " + fieldName + ": " + ex3.getMessage(), ex3);
        } 
    }
}
