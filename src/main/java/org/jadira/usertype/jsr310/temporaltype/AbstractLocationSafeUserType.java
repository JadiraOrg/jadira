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
package org.jadira.usertype.jsr310.temporaltype;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimeZone;

import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.type.MutableType;
import org.hibernate.usertype.UserType;

public abstract class AbstractLocationSafeUserType extends MutableType implements UserType, Serializable {

    private static final long serialVersionUID = -1670945705327210610L;

    public final int[] sqlTypes() {
        return new int[] { sqlType() };
    }
    
    protected final Calendar getUtcCalendar() {
        
        final Calendar utcCalendar = Calendar.getInstance();
        utcCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        return utcCalendar;
    }

    public final Object deepCopy(Object value) throws HibernateException {
        return (value==null) ? null : deepCopyNotNull(value);
    }

    public boolean equals(Object x, Object y) {
        return (x == null) ? (y == null) : x.equals(y);
    }
    
    @Override
    public int getHashCode(Object x, EntityMode entityMode) {
        return x.hashCode();
    }
    
    public int hashCode(Object x)  throws HibernateException {
        return x.hashCode();
    }

    @Override
    public abstract int sqlType();
    
    public abstract Class<?> returnedClass();

    public @SuppressWarnings("unchecked") Class getReturnedClass() {
        return returnedClass();
    }
    
    @Override
    protected abstract Object deepCopyNotNull(Object value) throws HibernateException;
    
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return deepCopy(original);
    }

    public Serializable disassemble(Object value) throws HibernateException {
        return (Serializable) deepCopy(value);
    }
 
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return deepCopy(cached);
    }

    public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner) throws HibernateException, SQLException {
        return nullSafeGet(resultSet, names[0]);
    }
    

    @Override
    public void set(PreparedStatement st, Object value, int index) throws HibernateException, SQLException {
        if (!(value instanceof java.sql.Date))                
            value = deepCopy(value);
        st.setDate(index, (java.sql.Date) value, getUtcCalendar());
    }
}
