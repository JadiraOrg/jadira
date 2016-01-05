/*
 *  Copyright 2010, 2011, 2012 Christopher Pheby
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
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.spi.shared.AbstractKnownClassHeuristicUserType;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

public class PersistentJsonObjectAsString<T> extends AbstractKnownClassHeuristicUserType<T> implements ParameterizedType {

	private static final long serialVersionUID = 3094384329334123541L;
	
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	private ObjectReader objectReader;

	private ObjectWriter objectWriter;
	
	protected ObjectReader getObjectReader() {
		return objectReader;
	}
	
	protected ObjectWriter getObjectWriter() {
		return objectWriter;
	}
	
	protected void setMappedClass(Class<T> mappedClass) {
    	super.setMappedClass(mappedClass);
    	
    	objectReader = OBJECT_MAPPER.readerFor(mappedClass);
    	objectWriter = OBJECT_MAPPER.writerFor(mappedClass);
    }
	
	@SuppressWarnings({ "unchecked" })
	public void setParameterValues(Properties parameters) {

		if (parameters.containsKey("jsonClass")) {
			String jsonClassName = parameters.getProperty("jsonClass");
			try {
				setMappedClass((Class<T>) Class.forName(jsonClassName));
			} catch (ClassNotFoundException e) {
				throw new HibernateException("Specified class could not be found", e);
			}
		}
		
		super.setParameterValues(parameters);
	}
	
	 @Override
	 public Class<T> returnedClass() {
	    Class<T> mappedClass = getMappedClass();
	    if (mappedClass == null) {
	        throw new IllegalStateException("class was not defined for " + this.getClass().getName());
	    }
	    return mappedClass;
	 }
	 
		@Override
		public Object doNullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

			String jsonText = (String) getType().get(rs, names[0], session);
			
			if (rs.wasNull()) {
				return null;
			}

			Object obj;
			try {
				obj = getObjectReader().readValue(jsonText);
			} catch (JsonParseException e) {
				throw new HibernateException("Problem parsing retrieved JSON String: " + jsonText, e);
			} catch (JsonMappingException e) {
				throw new HibernateException("Problem mapping retrieved JSON String: " + jsonText, e);
			} catch (IOException e) {
				throw new HibernateException("Problem reading JSON String: " + jsonText, e);
			}
			
			return obj;
		}

	    @Override
	    public void doNullSafeSet(PreparedStatement preparedStatement, Object value, int index, SessionImplementor session) throws SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

	    	if (value == null) {
				preparedStatement.setNull(index, getType().sqlType());
			} else {				
				
				try {
					String identifier = objectWriter.writeValueAsString(value);
					getType().nullSafeSet(preparedStatement, identifier, index, session);
				} catch (JsonProcessingException e) {
					throw new HibernateException("Problem writing JSON String: " + e.getMessage(), e);
				}
			}
	    }
	    
	    @Override
	    public String objectToSQLString(final Object object) {
	    	
	    	if (object == null) {
	    		return null;
	    	}

	    	try {
				return objectWriter.writeValueAsString(object);
			} catch (JsonProcessingException e) {
				throw new HibernateException("Cannot serialize JSON object: " + e.getMessage(), e);
			}
	    }
	    
	    @Override
	    public String toXMLString(Object object) {
	    	
	    	if (object == null) {
	    		return null;
	    	}

	    	try {
				return objectWriter.writeValueAsString(object);
			} catch (JsonProcessingException e) {
				throw new HibernateException("Cannot serialize JSON object: " + e.getMessage(), e);
			}
	    }
}
