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

import static org.jadira.usertype.spi.reflectionutils.ArrayUtils.copyOf;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import org.jadira.usertype.spi.reflectionutils.TypeHelper;

public abstract class AbstractHeuristicUserType<T> extends AbstractUserType implements EnhancedUserType, Serializable {

	private static final long serialVersionUID = 7099384329368123541L;

	private Class<?> mappedClass;

	private Method identifierMethod;
	private Method valueOfMethod;

	private Class<?> identifierType;
	private AbstractSingleColumnStandardBasicType<?> type;
	
	private int[] sqlTypes;

    protected void setMappedClass(Class<?> mappedClass) {
    	this.mappedClass = mappedClass;
    }
    
    protected Class<?> getMappedClass() {
    	return mappedClass;
    }
    
    protected void setIdentifierMethod(Method identifierMethod) {
    	this.identifierMethod = identifierMethod;
    }
    
    protected void setValueOfMethod(Method valueOfMethod) {
    	this.valueOfMethod = valueOfMethod;
    }
    
	public void setParameterValues(Properties parameters) {

		if (mappedClass == null) {
			
			throw new IllegalStateException("No mapped class was defined for " + this.getClass().getName());
		}

		if (identifierMethod == null) {
			
			throw new IllegalStateException("No identifier method was defined for " + this.getClass().getName());
		}
		
		identifierType = identifierMethod.getReturnType();
		
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

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> returnedClass() {
        return (Class<T>) TypeHelper.getTypeArguments(AbstractHeuristicUserType.class, getClass()).get(0);
    }
    
	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException {

		beforeNullSafeOperation(session);
		
		try {
		
			Object identifier = type.get(rs, names[0], session);
			if (rs.wasNull()) {
				return null;
			}
	
			try {
				return valueOfMethod.invoke(mappedClass, new Object[] { identifier });
			} catch (IllegalArgumentException e) {
				throw new HibernateException(
						"Exception while invoking valueOf method '"
								+ valueOfMethod.getName() + "' of class '" 
								+ mappedClass + "'", e);
			} catch (IllegalAccessException e) {
				throw new HibernateException(
						"Exception while invoking valueOf method '"
								+ valueOfMethod.getName() + "' of class '" 
								+ mappedClass + "'", e);
			} catch (InvocationTargetException e) {
				throw new HibernateException(
						"Exception while invoking valueOf method '"
								+ valueOfMethod.getName() + "' of class '" 
								+ mappedClass + "'", e);
			}    
    	} finally {
    		afterNullSafeOperation(session);
    	}
	}

    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index, SessionImplementor session) throws SQLException {

    	beforeNullSafeOperation(session);
    	
    	try {
    		if (value == null) {
    			preparedStatement.setNull(index, type.sqlType());
    		} else {
    			Object identifier = identifierMethod.invoke(value, new Object[0]);
    			type.nullSafeSet(preparedStatement, identifier, index, session);
    		}
		} catch (IllegalArgumentException e) {
			throw new HibernateException(
					"Exception while invoking identifierMethod '"
							+ identifierMethod.getName() + "' of class '" 
							+ mappedClass + "'", e);
		} catch (IllegalAccessException e) {
			throw new HibernateException(
					"Exception while invoking identifierMethod '"
							+ identifierMethod.getName() + "' of class '" 
							+ mappedClass + "'", e);		} catch (InvocationTargetException e) {
		} finally {
			afterNullSafeOperation(session);
		}
    }

    @Override
    public Serializable disassemble(Object value) throws HibernateException {
        return super.disassemble(value);
    }

    @Override
    public Object assemble(Serializable cached, Object owner) throws HibernateException {
        return super.assemble(cached, owner);
    }

    @Override
    public Object replace(Object original, Object target, Object owner) throws HibernateException {
        return super.replace(original, target, owner);
    }
    
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
    public T fromXMLString(String string) {
        @SuppressWarnings("unchecked")
		T fromString = (T) type.fromString(string);
		return fromString;
    }
}
