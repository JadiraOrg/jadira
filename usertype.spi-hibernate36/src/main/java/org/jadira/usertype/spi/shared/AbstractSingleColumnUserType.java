/*
 *  Copyright 2010, 2011 Christopher Pheby
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
package org.jadira.usertype.spi.shared;

import static org.jadira.usertype.spi.utils.reflection.ArrayUtils.copyOf;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.usertype.EnhancedUserType;
import org.jadira.usertype.spi.utils.reflection.TypeHelper;

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

    public T nullSafeGet(ResultSet resultSet, String[] strings, Object object) throws SQLException {
        
    	beforeNullSafeOperation(null);
    	
    	try {
	    	J converted = doNullSafeGet(resultSet, strings, null, object);
	
	        if (converted == null) {
	            return null;
	        }
	
	        return getColumnMapper().fromNonNullValue(converted);
	        
    	} finally {
    		afterNullSafeOperation(null);
    	}
    }

    protected J doNullSafeGet(ResultSet resultSet, String[] strings, SessionImplementor session, Object object) throws SQLException {
		@SuppressWarnings("unchecked")
		final J converted = (J) getColumnMapper().getHibernateType().nullSafeGet(resultSet, strings[0], session, object);
		return converted;
	}

    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index) throws SQLException {

    	beforeNullSafeOperation(null);
    	
    	try {
	        final J transformedValue;
	        if (value == null) {
	            transformedValue = null;
	        } else {
	            @SuppressWarnings("unchecked") T myValue = (T) value;
	            transformedValue = getColumnMapper().toNonNullValue(myValue);
	        }
	
	        doNullSafeSet(preparedStatement, transformedValue, index, null);
	        
    	} finally {
    		afterNullSafeOperation(null);
    	}
    }

    protected void doNullSafeSet(PreparedStatement preparedStatement, J transformedValue, int index, SessionImplementor session) throws SQLException {
    	getColumnMapper().getHibernateType().nullSafeSet(preparedStatement, transformedValue, index, session);
	}

    public String objectToSQLString(Object object) {
        @SuppressWarnings("unchecked") final T myObject = (T) object;
        J convertedObject = myObject == null ? null : getColumnMapper().toNonNullValue(myObject);
        
        return getColumnMapper().getHibernateType().toString(convertedObject);
    }

    public String toXMLString(Object object) {
        @SuppressWarnings("unchecked") final T myObject = (T) object;
        return getColumnMapper().toNonNullString(myObject);
    }

    public T fromXMLString(String string) {
        return getColumnMapper().fromNonNullString(string);
    }
}
