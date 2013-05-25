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
package org.jadira.usertype.spi.shared;

import static org.jadira.usertype.spi.utils.reflection.ArrayUtils.copyOf;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.TypeResolver;
import org.hibernate.type.descriptor.java.JavaTypeDescriptor;
import org.hibernate.usertype.EnhancedUserType;

public abstract class AbstractHeuristicUserType extends AbstractUserType implements EnhancedUserType, Serializable {

	private static final long serialVersionUID = 7099384329368123541L;

	private Class<?> identifierType;
	
	private AbstractSingleColumnStandardBasicType<?> type;
	
	private int[] sqlTypes;

    protected void setIdentifierType(Class<?> identifierType) {
    	this.identifierType = identifierType;
    }
    
    protected Class<?> getIdentifierType() {
    	return identifierType;
    }
    
    protected AbstractSingleColumnStandardBasicType<?> getType() {
    	return type;
    }
    
	public void setParameterValues(Properties parameters) {
		
		@SuppressWarnings("unchecked")
		final AbstractSingleColumnStandardBasicType<? extends Object> heuristicType = (AbstractSingleColumnStandardBasicType<? extends Object>) new TypeResolver().heuristicType(identifierType.getName(), parameters);
		if (heuristicType == null) {
			throw new HibernateException("Unsupported identifier type " + identifierType.getName());
		}
		
		type = heuristicType;
		sqlTypes = new int[]{ type.sqlType() };
	}

	public int[] sqlTypes() {
        return copyOf(sqlTypes);
    }

    @Override
    public abstract Class<?> returnedClass();
    
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {

		beforeNullSafeOperation(session);
		
		try {
	
			try {
				return doNullSafeGet(rs, names, session, owner);
			} catch (IllegalArgumentException e) {
				throw new HibernateException(
						"Exception during nullSafeGet of type '" 
							+ identifierType.getName() + "'", e);
			} catch (IllegalAccessException e) {
				throw new HibernateException(
						"Exception during nullSafeGet of type '" 
								+ identifierType.getName() + "'", e);
			} catch (InvocationTargetException e) {
				throw new HibernateException(
						"Exception during nullSafeGet of type '" 
								+ identifierType.getName() + "'", e);
			}    
    	} finally {
    		afterNullSafeOperation(session);
    	}
	}

    public abstract Object doNullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException;
    
    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index, SessionImplementor session) throws SQLException {

    	beforeNullSafeOperation(session);
    	
    	try {
    		doNullSafeSet(preparedStatement, value, index, session);
		} catch (IllegalArgumentException e) {
			throw new HibernateException(
					"Exception during nullSafeSet of type '" 
							+ identifierType.getName() + "'", e);
		} catch (IllegalAccessException e) {
			throw new HibernateException(
					"Exception during nullSafeSet of type '" 
							+ identifierType.getName() + "'", e);
			} catch (InvocationTargetException e) {
		} finally {
			afterNullSafeOperation(session);
		}
    }

    public abstract void doNullSafeSet(PreparedStatement preparedStatement, Object value, int index, SessionImplementor session) throws SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public String objectToSQLString(Object object) {
    	final JavaTypeDescriptor desc = type.getJavaTypeDescriptor();
    	return desc.toString(object);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
    public String toXMLString(Object object) {
    	final JavaTypeDescriptor desc = type.getJavaTypeDescriptor();
    	return desc.toString(object);
    }

    @Override
    public Object fromXMLString(String string) {
		return type.fromString(string);
    }
}
