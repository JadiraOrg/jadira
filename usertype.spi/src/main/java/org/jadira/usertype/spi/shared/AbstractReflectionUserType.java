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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.EnhancedUserType;
import org.jadira.usertype.spi.utils.reflection.TypeHelper;

public abstract class AbstractReflectionUserType<T> extends AbstractHeuristicUserType implements EnhancedUserType, Serializable {

	private static final long serialVersionUID = 7943328235820102665L;

	private Class<?> mappedClass;

	private Method identifierMethod;
	private Method valueOfMethod;

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
    	if (!Modifier.isStatic(valueOfMethod.getModifiers())) {
    		throw new IllegalStateException("valueOfMethod must be static: "  + valueOfMethod.toString());
    	}
    	this.valueOfMethod = valueOfMethod;
    }
    
	public void setParameterValues(Properties parameters) {

		if (mappedClass == null) {
			
			throw new IllegalStateException("No mapped class was defined for " + this.getClass().getName());
		}

		if (identifierMethod == null) {
			
			throw new IllegalStateException("No identifier method was defined for " + this.getClass().getName());
		}
		
		setIdentifierType(identifierMethod.getReturnType());
		
		super.setParameterValues(parameters);
	}

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> returnedClass() {
        return (Class<T>) TypeHelper.getTypeArguments(AbstractReflectionUserType.class, getClass()).get(0);
    }
    
	@Override
	public Object doNullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner) throws HibernateException, SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

		Object identifier = getType().get(rs, names[0], session);
		
		if (rs.wasNull()) {
			return null;
		}

		return valueOfMethod.invoke(mappedClass, new Object[] { identifier });
	}

    @Override
    public void doNullSafeSet(PreparedStatement preparedStatement, Object value, int index, SessionImplementor session) throws SQLException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {

    	if (value == null) {
			preparedStatement.setNull(index, getType().sqlType());
		} else {
			Object identifier = identifierMethod.invoke(value, new Object[0]);
			getType().nullSafeSet(preparedStatement, identifier, index, session);
		}
    }
    
    @Override
    public String objectToSQLString(final Object object) {
    	
    	if (object == null) {
    		return null;
    	}
    	if (identifierMethod != null) {
    		try {
    			return String.valueOf(identifierMethod.invoke(object));
    		} catch (InvocationTargetException e) {
    			// Ignore
    		} catch (IllegalAccessException e) {
    			// Ignore
    		}
    	}
    	return super.objectToSQLString(object);
    }
    
    @Override
    public String toXMLString(Object object) {
        if (identifierMethod != null) {
            try {
                return String.valueOf(identifierMethod.invoke(object));

            } catch (InvocationTargetException e) {
                throw new HibernateException("Problem constructing XMLString: " + object + "'", e);

            } catch (IllegalAccessException e) {
            	throw new HibernateException("Problem constructing XMLString: " + object + "'", e);
            }
        }
        return super.toXMLString(object);
    }
}
