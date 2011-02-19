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
package org.jadira.usertype.dateandtime.shared.spi;

import static org.jadira.usertype.dateandtime.shared.reflectionutils.ArrayUtils.copyOf;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import org.jadira.usertype.dateandtime.shared.reflectionutils.Hibernate36Helper;
import org.jadira.usertype.dateandtime.shared.reflectionutils.TypeHelper;

public abstract class AbstractMultiColumnUserType<T> extends AbstractUserType implements CompositeUserType, Serializable {

    private static final long serialVersionUID = -8258683760413283329L;
    
    private final int[] sqlTypes;
    
    private final Type[] hibernateTypes;
    
//    private String[] defaultPropertyNames;
    
    public AbstractMultiColumnUserType() {
        
        sqlTypes = new int[getColumnMappers().length];
        for (int i = 0; i < sqlTypes.length; i++) {
            sqlTypes[i] = getColumnMappers()[i].getSqlType();
        }
        
        hibernateTypes = new Type[getColumnMappers().length];
        for (int i = 0; i < hibernateTypes.length; i++) {
            hibernateTypes[i] = getColumnMappers()[i].getHibernateType();
        }
        
//        Map<String, Integer> nameCount = new HashMap<String, Integer>();
//        
//        defaultPropertyNames = new String[getColumnMappers().length];
//        for (int i = 0; i < defaultPropertyNames.length; i++) {
//            String className = hibernateTypes[i].getClass().getSimpleName();
//            if (className.endsWith("Type")) {
//                className = className.substring(0, className.length() - 4);
//            }
//            
//            String name = className.toLowerCase();
//            final Integer count;
//            if (nameCount.containsKey(name)) {
//                Integer oldCount = nameCount.get(name);
//                count = oldCount.intValue() + 1;
//                defaultPropertyNames[i] = name + count;
//            } else {
//                count = 1;
//                defaultPropertyNames[i] = name;
//            }
//            nameCount.put(name, count);
//        }
    }
   
    public int[] sqlTypes() {
        return copyOf(sqlTypes);
    }
    
    @SuppressWarnings("unchecked")
    public Class<T> returnedClass() {
        return (Class<T>) TypeHelper.getTypeArguments(AbstractMultiColumnUserType.class, getClass()).get(0);
    }
    
    protected abstract ColumnMapper<?, ?>[] getColumnMappers();
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public T nullSafeGet(ResultSet resultSet, String[] strings, SessionImplementor session, Object object) throws SQLException {
        
        Object[] convertedColumns = new Object[getColumnMappers().length];
        
        for (int getIndex = 0; getIndex < getColumnMappers().length; getIndex++) {
            ColumnMapper nextMapper = getColumnMappers()[getIndex];
            
            final Object converted;
            if (Hibernate36Helper.isHibernate36ApiAvailable()) {
                converted = Hibernate36Helper.nullSafeGet(nextMapper, resultSet, strings[getIndex]);
            } else {
                converted = ((org.hibernate.type.NullableType) nextMapper.getHibernateType()).nullSafeGet(resultSet, strings[getIndex]);
            }
            
            if (converted != null) {
                convertedColumns[getIndex] = nextMapper.fromNonNullValue(converted);
            }
        }
        
        for (int i = 0; i < convertedColumns.length; i++) {
            if (convertedColumns[i] != null) {
                return fromConvertedColumns(convertedColumns);
            }
        }

        return null;
    }

    protected abstract T fromConvertedColumns(Object[] convertedColumns);
    
    protected abstract Object[] toConvertedColumns(T value);
        
    @SuppressWarnings("unchecked")
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index, SessionImplementor session) throws SQLException {

        final Object[] valuesToSet = new Object[getColumnMappers().length];

        if (value != null) {

            final T myValue = (T) value;
            Object[] convertedColumns = toConvertedColumns(myValue);

            for (int cIdx = 0; cIdx < valuesToSet.length; cIdx++) {

                @SuppressWarnings("rawtypes") ColumnMapper nextMapper = getColumnMappers()[cIdx];
                valuesToSet[cIdx] = nextMapper.toNonNullValue(convertedColumns[cIdx]);
            }
        }

        for (int setIndex = 0; setIndex < valuesToSet.length; setIndex++) {

            @SuppressWarnings("rawtypes") ColumnMapper nextMapper = getColumnMappers()[setIndex];
            if (Hibernate36Helper.isHibernate36ApiAvailable()) {
                Hibernate36Helper.nullSafeSet(nextMapper, preparedStatement, valuesToSet[setIndex], index + setIndex);
            } else {
                ((org.hibernate.type.NullableType) nextMapper.getHibernateType()).nullSafeSet(preparedStatement, valuesToSet[setIndex], index + setIndex);
            }
        }
    }

    public abstract String[] getPropertyNames();
//    public String[] getPropertyNames() {
//        return defaultPropertyNames;
//    }

    public Type[] getPropertyTypes() {
        return copyOf(hibernateTypes);
    }

    public Object getPropertyValue(Object component, int property) throws HibernateException {
        
        if (!returnedClass().isAssignableFrom(component.getClass())) {
            throw new HibernateException("getPropertyValue called with incorrect class: {" + component.getClass() + "}");
        } 
        @SuppressWarnings("unchecked") Object[] cols = toConvertedColumns((T) component);
        return cols[property];
    }

    public void setPropertyValue(Object component, int property, Object value) throws HibernateException {
        throw new HibernateException("Called setPropertyValue on an immutable type {" + component.getClass() + "}");
    }
    
    public Serializable disassemble(Object value, SessionImplementor session) throws HibernateException {
        return super.disassemble(value);
    }

    public Object assemble(Serializable cached, SessionImplementor session, Object owner) throws HibernateException {
        return super.assemble(cached, owner);
    }

    public Object replace(Object original, Object target, SessionImplementor session, Object owner) throws HibernateException {
        return super.replace(original, target, owner);
    }
}
