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
package org.jadira.usertype.bindings;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.EnhancedUserType;
import org.hibernate.usertype.ParameterizedType;
import org.jadira.bindings.core.api.Binding;
import org.jadira.bindings.core.binder.BasicBinder;
import org.jadira.usertype.spi.shared.AbstractHeuristicUserType;

/**
 * Allows dynamic invocation of any class by mapping it using a binding type.
 */
public class PersistentBoundClass extends AbstractHeuristicUserType implements ParameterizedType, EnhancedUserType, Serializable {

	private static final long serialVersionUID = 3094384329334123541L;
	
	private static final BasicBinder BINDING = new BasicBinder();
	
	private Class<?> javaClass;
	
	private Class<? extends Annotation> qualifier;

	@SuppressWarnings("rawtypes")
	private Binding binding;
	
	public void setParameterValues(Properties parameters) {

		if (parameters.containsKey("javaClass")) {
			String mappedClassName = parameters.getProperty("javaClass");
			try {
				this.javaClass = Class.forName(mappedClassName);
			} catch (ClassNotFoundException e) {
				throw new HibernateException("Specified Java class could not be found", e);
			}
		}
		
		if (parameters.containsKey("hibernateClass")) {
			String mappedClassName = parameters.getProperty("hibernateClass");
			try {
				super.setIdentifierType((Class<?>) Class.forName(mappedClassName));
			} catch (ClassNotFoundException e) {
				throw new HibernateException("Specified Hibernate class could not be found", e);
			}
		}
		
		if (parameters.containsKey("qualifier")) {
			String mappedClassName = parameters.getProperty("qualifier");
			try {
				@SuppressWarnings("unchecked")
				final Class<? extends Annotation> myQualifier = ((Class<? extends Annotation>) Class.forName(mappedClassName));
				qualifier = myQualifier;
			} catch (ClassNotFoundException e) {
				throw new HibernateException("Specified Qualifier class could not be found", e);
			}
		}

		if (javaClass == null) {
			throw new HibernateException("Java class was not defined");
		}
		if (super.getIdentifierType() == null) {
			throw new HibernateException("Hibernate class was not defined");
		}
		
		super.setParameterValues(parameters);
		
		binding = BINDING.findBinding(javaClass, getIdentifierType(), qualifier);
		
		if (binding == null) {
			throw new HibernateException("Could not resolve binding instance");
		}
	}

	@Override
	public Class<?> returnedClass() {
		return javaClass;
	}

	@Override
	public Object doNullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		Object identifier = getType().get(rs, names[0], session);
		
		if (rs.wasNull()) {
			return null;
		}

		@SuppressWarnings("unchecked")
		final Object result = binding.unmarshal(identifier);
		return result;
	}

    @Override
    public void doNullSafeSet(PreparedStatement preparedStatement, Object value, int index, SessionImplementor session) throws SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

    	if (value == null) {
			preparedStatement.setNull(index, getType().sqlType());
		} else {
			
			@SuppressWarnings("unchecked")
			final Object identifier = binding.marshal(value);
			getType().nullSafeSet(preparedStatement, identifier, index, session);
		}
    }
}
