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
package org.jadira.usertype.json.jackson;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.postgresql.util.PGobject;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class PersistentJsonObjectAsPostgreSQLJson<T> extends PersistentJsonObjectAsString<T> {

	private static final long serialVersionUID = 228945479215593795L;

    @Override
    public Object doNullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

        Object identifier = rs.getObject(names[0]);
        
        if (rs.wasNull()) {
            return null;
        }

		final String jsonText;
		if (identifier instanceof PGobject) {
            PGobject pg = (PGobject) identifier;
            jsonText = pg.getValue();
        } else if (identifier instanceof String) { // some PostgreSQL Dialects / Versions return String not PGObject
            jsonText = (String)identifier;
        } else {
            throw new IllegalArgumentException("PersistentJsonObjectAsPostgreSQLJson type expected PGobject, received " + identifier.getClass().getName() + " with value of '" + identifier + "'");
        }
		
		try {
			Object obj = getObjectReader().readValue(jsonText);
			return obj;			
		} catch (JsonParseException e) {
			throw new HibernateException("Problem parsing retrieved JSON String: " + jsonText, e);
		} catch (JsonMappingException e) {
			throw new HibernateException("Problem mapping retrieved JSON String: " + jsonText, e);
		} catch (IOException e) {
			throw new HibernateException("Problem reading JSON String: " + jsonText, e);
		}
    }

    @Override
    public void doNullSafeSet(PreparedStatement preparedStatement, Object value, int index, SessionImplementor session) throws SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

    	if (value == null) {
			preparedStatement.setNull(index, Types.NULL);
		} else {				
			
			try {
				String identifier = getObjectWriter().writeValueAsString(value);
				
	        	PGobject jsonObject = new PGobject();
	        	jsonObject.setType("json");
	        	jsonObject.setValue(identifier);
				
	            preparedStatement.setObject(index, jsonObject);
			} catch (JsonProcessingException e) {
				throw new HibernateException("Problem writing JSON String: " + e.getMessage(), e);
			}
		}
    }
}
