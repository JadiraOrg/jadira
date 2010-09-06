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

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.type.SerializationException;
import org.hibernate.usertype.EnhancedUserType;
import org.jadira.usertype.dateandtime.shared.reflectionutils.TypeHelper;

import static org.jadira.usertype.dateandtime.shared.reflectionutils.ArrayUtils.copyOf;

public abstract class AbstractUserType<T, J, C extends ColumnMapper<T, J>> implements EnhancedUserType {

    private static final long serialVersionUID = -8258683760413283329L;
    private final ColumnMapper<T, J> columnMapper;
    private final int[] sqlTypes;

    @SuppressWarnings("unchecked")
    public AbstractUserType() {
        
        try {
            columnMapper = (ColumnMapper<T, J>) TypeHelper.getTypeArguments(AbstractUserType.class, getClass()).get(2).newInstance();
        } catch (InstantiationException ex) {
            throw new HibernateException("Could not initialise column mapper for " + getClass(), ex);
        } catch (IllegalAccessException ex) {
            throw new HibernateException("Could not access column mapper for " + getClass(), ex);
        }
        sqlTypes = new int[] { getColumnMapper().getSqlType() };
    }
    
    public final ColumnMapper<T, J> getColumnMapper() {
        return columnMapper;
    }
    
    public Class<T> returnedClass() {
        return getColumnMapper().returnedClass();
    }
    
    public final int[] sqlTypes() {
        return copyOf(sqlTypes);
    }
    
    public final boolean isMutable() {
        return false;
    }
    
    public boolean equals(Object x, Object y) throws HibernateException {
        if (x == y) {
            return true;
        }
        if ((x == null) || (y == null)) {
            return false;
        }
        return x.equals(y);
    }

    public int hashCode(Object x) throws HibernateException {
        assert (x != null);
        return x.hashCode();
    }

    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    public Serializable disassemble(Object value) throws HibernateException {
        Object deepCopy = deepCopy(value);

        if (!(deepCopy instanceof Serializable)) {
            throw new SerializationException(String.format("deepCopy of %s is not serializable", value), null);
        }

        return (Serializable) deepCopy;
    }

    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }

    public Object deepCopy(Object value) throws HibernateException {
        return value;
    }
    
    public T nullSafeGet(ResultSet resultSet, String[] strings, Object object) throws HibernateException, SQLException {
        @SuppressWarnings("unchecked") J converted = (J) getColumnMapper().getHibernateType().nullSafeGet(resultSet, strings[0]);
        if (converted == null) {
            return null;
        }

        return getColumnMapper().fromNonNullValue(converted);
    }
    
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index) throws HibernateException, SQLException {
        if (value == null) {
            getColumnMapper().getHibernateType().nullSafeSet(preparedStatement, null, index);
        } else {
            @SuppressWarnings("unchecked") final T myValue = (T) value;
            getColumnMapper().getHibernateType().nullSafeSet(preparedStatement, getColumnMapper().toNonNullValue(myValue), index);
        }
    }
    
    public String objectToSQLString(Object object) {
        @SuppressWarnings("unchecked") final T myObject = (T) object;
        return getColumnMapper().getHibernateType().nullSafeToString(myObject == null ? null : getColumnMapper().toNonNullValue(myObject));
    }
       
    public String toXMLString(Object object) {
        @SuppressWarnings("unchecked") final T myObject = (T) object;
        return getColumnMapper().toNonNullString(myObject);
    }

    public T fromXMLString(String string) {
        return getColumnMapper().fromNonNullString(string);
    }
}
