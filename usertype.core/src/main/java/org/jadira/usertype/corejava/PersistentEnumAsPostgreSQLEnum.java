/*
 *  Copyright 2014 Christopher Pheby
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
package org.jadira.usertype.corejava;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.postgresql.util.PGobject;

public class PersistentEnumAsPostgreSQLEnum extends PersistentEnum {

    private static final long serialVersionUID = 6811103311933769966L;

    private static final int POSTGRES_ENUM_TYPE = 1111;
    
    @Override
    public Object doNullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        Object identifier = rs.getObject(names[0]);
        
        if (rs.wasNull()) {
            return null;
        }

        if (identifier instanceof PGobject) {
            PGobject pg = (PGobject) identifier;
            return getValueOfMethod().invoke(getMappedClass(), new Object[] { pg.getValue() });
        } else if (identifier instanceof String) { // some PostgreSQL Dialects / Versions return String not PGObject
            return getValueOfMethod().invoke(getMappedClass(), new Object[] { (String) identifier });
        } else {
            throw new IllegalArgumentException("PersistentEnum type expected PGobject, received " + identifier.getClass().getName() + " with value of '" + identifier + "'");
        }
    }

    @Override
    public void doNullSafeSet(PreparedStatement preparedStatement, Object value, int index, SessionImplementor session) throws SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        if (value == null) {
            preparedStatement.setNull(index, POSTGRES_ENUM_TYPE);
        } else {
            Object identifier = getIdentifierMethod().invoke(value, new Object[0]);
            preparedStatement.setObject(index, identifier, POSTGRES_ENUM_TYPE);
        }
    }
    
    @Override
    public void setParameterValues(Properties parameters) {

        super.setParameterValues(parameters);
        
        if (!getIdentifierType().equals(String.class)) {
            throw new HibernateException("PostgreSQL Enum must be mapped using String form");
        }
    }
}
