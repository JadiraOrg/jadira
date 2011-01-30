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

        try {
            Field field = getStandardBasicTypesClass().getField(name);
            return (Type) field.get(null);
        } catch (SecurityException ex) {
            throw new ReflectionException("Problem retrieving Hibernate type {"
                    + name + "}: " + ex.getMessage(), ex);
        } catch (NoSuchFieldException ex) {
            throw new ReflectionException("Problem retrieving Hibernate type {"
                    + name + "}: " + ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ReflectionException("Problem retrieving Hibernate type {"
                    + name + "}: " + ex.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            throw new ReflectionException("Problem retrieving Hibernate type {"
                    + name + "}: " + ex.getMessage(), ex);
        }
    }

    public static Object nullSafeGet(ColumnMapper<?, ?> mapper,
            ResultSet resultSet, String string) {

        try {
            return NULL_SAFE_GET_METHOD.invoke(mapper.getHibernateType(),
                    new Object[] { resultSet, string, NO_WRAPPER_OPTIONS });
        } catch (IllegalArgumentException ex) {
            throw new ReflectionException(
                    "Problem invoking nullSafeGet Method: " + ex.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            throw new ReflectionException(
                    "Problem invoking nullSafeGet Method: " + ex.getMessage(), ex);
        } catch (InvocationTargetException ex) {
            throw new ReflectionException(
                    "Problem invoking nullSafeGet Method: " + ex.getMessage(), ex);
        }
    }

    public static void nullSafeSet(ColumnMapper<?, ?> mapper,
            PreparedStatement preparedStatement, Object object, int index) {

        try {
            NULL_SAFE_SET_METHOD.invoke(mapper.getHibernateType(),
                    new Object[] { preparedStatement, object, index, NO_WRAPPER_OPTIONS });
        } catch (IllegalArgumentException ex) {
            throw new ReflectionException(
                    "Problem invoking nullSafeSet Method: " + ex.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            throw new ReflectionException(
                    "Problem invoking nullSafeSet Method: " + ex.getMessage(), ex);
        } catch (InvocationTargetException ex) {
            throw new ReflectionException(
                    "Problem invoking nullSafeSet Method: " + ex.getMessage(), ex);
        }
    }

    public static String nullSafeToString(ColumnMapper<?, ?> mapper,
            Object object) {

        if (object == null) {
            return null;
        } else {
            try {
                return (String) TOSTRING_METHOD.invoke(
                        mapper.getHibernateType(), new Object[] { object });
            } catch (IllegalArgumentException ex) {
                throw new ReflectionException(
                        "Problem invoking nullSafeToString Method: "
                                + ex.getMessage(), ex);
            } catch (IllegalAccessException ex) {
                throw new ReflectionException(
                        "Problem invoking nullSafeToString Method: "
                                + ex.getMessage(), ex);
            } catch (InvocationTargetException ex) {
                throw new ReflectionException(
                        "Problem invoking nullSafeToString Method: "
                                + ex.getMessage(), ex);
            }
        }
    }

    private static Class<?> getAbstractStandardBasicTypeClass() {

        try {
            return Class
                    .forName("org.hibernatex.typex.AbstractStandardBasicType");
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    private static Class<?> getAbstractSingleColumnStandardBasicTypeClass() {

        try {
            return Class
                    .forName("org.hibernatex.typex.AbstractSingleColumnStandardBasicType");
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    private static Class<?> getStandardBasicTypesClass() {

        try {
            return Class.forName("org.hibernatex.typex.StandardBasicTypes");
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    private static Class<?> getWrapperOptionsClass() {

        try {
            return Class
                    .forName("org.hibernatex.typex.descriptor.WrapperOptions");
        } catch (ClassNotFoundException ex) {
            return null;
        }
    }

    private static Method getNullSafeGetMethod() {

        if (ABSTRACT_STANDARD_BASIC_TYPE_CLASS == null) {
            return null;
        }

        try {
            Method method = ABSTRACT_STANDARD_BASIC_TYPE_CLASS
                    .getDeclaredMethod("nullSafeGet", new Class[] { ResultSet.class, String.class, WRAPPER_OPTIONS_CLASS });
            method.setAccessible(true);

            return method;
        } catch (SecurityException ex) {
            throw new ReflectionException(
                    "Problem retrieving nullSafeGet Method: " + ex.getMessage(),
                    ex);
        } catch (NoSuchMethodException ex) {
            throw new ReflectionException(
                    "Problem retrieving nullSafeGet Method: " + ex.getMessage(),
                    ex);
        }
    }

    private static Method getNullSafeSetMethod() {

        if (ABSTRACT_STANDARD_BASIC_TYPE_CLASS == null) {
            return null;
        }

        try {
            Method method = ABSTRACT_STANDARD_BASIC_TYPE_CLASS
                    .getDeclaredMethod("nullSafeSet", new Class[] { PreparedStatement.class, Object.class, Integer.TYPE, WRAPPER_OPTIONS_CLASS });
            method.setAccessible(true);

            return method;
        } catch (SecurityException ex) {
            throw new ReflectionException(
                    "Problem retrieving nullSafeSet Method: " + ex.getMessage(),
                    ex);
        } catch (NoSuchMethodException ex) {
            throw new ReflectionException(
                    "Problem retrieving nullSafeSet Method: " + ex.getMessage(),
                    ex);
        }
    }

    private static Method getToStringMethod() {

        if (ABSTRACT_STANDARD_BASIC_TYPE_CLASS == null) {
            return null;
        }

        try {
            return ABSTRACT_STANDARD_BASIC_TYPE_CLASS.getMethod("toString",
                    new Class[] { Object.class });
        } catch (SecurityException ex) {
            throw new ReflectionException(
                    "Problem retrieving toString Method: " + ex.getMessage(), ex);
        } catch (NoSuchMethodException ex) {
            throw new ReflectionException(
                    "Problem retrieving toString Method: " + ex.getMessage(), ex);
        }
    }

    private static Object getNoWrapperOptions() {

        if (ABSTRACT_SINGLE_COLUMN_STANDARD_BASIC_TYPE_CLASS == null) {
            return null;
        }

        try {
            Field field = ABSTRACT_SINGLE_COLUMN_STANDARD_BASIC_TYPE_CLASS
                    .getDeclaredField("NO_OPTIONS");
            field.setAccessible(true);

            return field.get(null);
        } catch (SecurityException ex) {
            throw new ReflectionException(
                    "Problem retrieving NoWrapperOptions: " + ex.getMessage(), ex);
        } catch (NoSuchFieldException ex) {
            throw new ReflectionException(
                    "Problem retrieving NoWrapperOptions: " + ex.getMessage(), ex);
        } catch (IllegalArgumentException ex) {
            throw new ReflectionException(
                    "Problem retrieving NoWrapperOptions: " + ex.getMessage(), ex);
        } catch (IllegalAccessException ex) {
            throw new ReflectionException(
                    "Problem retrieving NoWrapperOptions: " + ex.getMessage(), ex);
        }
    }
}
