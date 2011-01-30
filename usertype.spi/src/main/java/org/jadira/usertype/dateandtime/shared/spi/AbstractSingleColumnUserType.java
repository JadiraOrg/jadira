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
import org.hibernate.usertype.EnhancedUserType;
import org.jadira.usertype.dateandtime.shared.reflectionutils.Hibernate36Helper;
import org.jadira.usertype.dateandtime.shared.reflectionutils.TypeHelper;

public abstract class AbstractSingleColumnUserType<T, J, C extends ColumnMapper<T, J>> extends AbstractUserType implements EnhancedUserType, Serializable {

    private static final long serialVersionUID = -8258683760413283329L;
    
    private final C columnMapper;
    private final int[] sqlTypes;

    @SuppressWarnings("unchecked")
    public AbstractSingleColumnUserType() {
        
        try {
            columnMapper = (C) TypeHelper.getTypeArguments(AbstractSingleColumnUserType.class, getClass()).get(2).newInstance();
        } catch (InstantiationException ex) {
            throw new HibernateException("Could not initialise column mapper for " + getClass(), ex);
        } catch (IllegalAccessException ex) {
            throw new HibernateException("Could not access column mapper for " + getClass(), ex);
        }
        sqlTypes = new int[] { getColumnMapper().getSqlType() };
    }
    
    public final C getColumnMapper() {
        return columnMapper;
    }
    
    public Class<T> returnedClass() {
        return getColumnMapper().returnedClass();
    }
    
    public final int[] sqlTypes() {
        return copyOf(sqlTypes);
    }
    
    @SuppressWarnings({ "unchecked" }) 
    public T nullSafeGet(ResultSet resultSet, String[] strings, Object object) throws HibernateException, SQLException {
        J converted;
        if (Hibernate36Helper.isHibernate36ApiAvailable()) {
            converted = (J) Hibernate36Helper.nullSafeGet(getColumnMapper(), resultSet, strings[0]);
        } else {
            converted = (J) ((org.hibernate.type.NullableType) getColumnMapper().getHibernateType()).nullSafeGet(resultSet, strings[0]);
        }
        
        if (converted == null) {
            return null;
        }

        return getColumnMapper().fromNonNullValue(converted);
    }
    
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index) throws HibernateException, SQLException {
        if (value == null) {
            if (Hibernate36Helper.isHibernate36ApiAvailable()) {
                Hibernate36Helper.nullSafeSet(getColumnMapper(), preparedStatement, null, index);
            } else {
                ((org.hibernate.type.NullableType) getColumnMapper().getHibernateType()).nullSafeSet(preparedStatement, null, index);
            }
        } else {
            @SuppressWarnings("unchecked") final T myValue = (T) value;
            if (Hibernate36Helper.isHibernate36ApiAvailable()) {
                Hibernate36Helper.nullSafeSet(getColumnMapper(), preparedStatement, getColumnMapper().toNonNullValue(myValue), index);
            } else {
                ((org.hibernate.type.NullableType) getColumnMapper().getHibernateType()).nullSafeSet(preparedStatement, getColumnMapper().toNonNullValue(myValue), index);
            }
            
        }
    }
    
    public String objectToSQLString(Object object) {
        @SuppressWarnings("unchecked") final T myObject = (T) object;
        J convertedObject = myObject == null ? null : getColumnMapper().toNonNullValue(myObject);
        if (Hibernate36Helper.isHibernate36ApiAvailable()) {
            return Hibernate36Helper.nullSafeToString(getColumnMapper(), convertedObject);
        } else {
            return ((org.hibernate.type.NullableType) getColumnMapper().getHibernateType()).nullSafeToString(convertedObject);
        }
    }
       
    public String toXMLString(Object object) {
        @SuppressWarnings("unchecked") final T myObject = (T) object;
        return getColumnMapper().toNonNullString(myObject);
    }

    public T fromXMLString(String string) {
        return getColumnMapper().fromNonNullString(string);
    }
}
