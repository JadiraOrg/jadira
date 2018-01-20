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
import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.Type;
import org.hibernate.usertype.CompositeUserType;
import org.jadira.usertype.spi.utils.reflection.TypeHelper;

public abstract class AbstractMultiColumnUserType<T> extends AbstractUserType implements CompositeUserType, Serializable {

    private static final long serialVersionUID = -8258683760413283329L;

    private int[] sqlTypes;

    private Type[] hibernateTypes;

    /* DefaultPropertyNames is currently not being used */
    private String[] defaultPropertyNames;

	public AbstractMultiColumnUserType() {

    	initialise();
    }

    protected final void initialise() {
    	
        initialiseSqlTypes();
        initialiseHibernateTypes();
        initialiseDefaultPropertyNames();
	}

	private void initialiseDefaultPropertyNames() {
		Map<String, Integer> nameCount = new HashMap<String, Integer>();

        defaultPropertyNames = new String[getColumnMappers().length];
        for (int i = 0; i < defaultPropertyNames.length; i++) {
            String className = hibernateTypes[i].getClass().getSimpleName();
            if (className.endsWith("Type")) {
                className = className.substring(0, className.length() - 4);
            }

            String name = className.toLowerCase();
            final Integer count;
            if (nameCount.containsKey(name)) {
                Integer oldCount = nameCount.get(name);
                count = oldCount.intValue() + 1;
                defaultPropertyNames[i] = name + count;
            } else {
                count = 1;
                defaultPropertyNames[i] = name;
            }
            nameCount.put(name, count);
        }
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialiseHibernateTypes() {
		hibernateTypes = new Type[getColumnMappers().length];
        for (int i = 0; i < hibernateTypes.length; i++) {
            hibernateTypes[i] = new ColumnMapperSingleColumnTypeAdapter(getColumnMappers()[i]);
        }
	}

	private void initialiseSqlTypes() {
		sqlTypes = new int[getColumnMappers().length];
        for (int i = 0; i < sqlTypes.length; i++) {
            sqlTypes[i] = getColumnMappers()[i].getSqlType();
        }
	}

	public int[] sqlTypes() {
        return copyOf(sqlTypes);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<T> returnedClass() {
        return (Class<T>) TypeHelper.getTypeArguments(AbstractMultiColumnUserType.class, getClass()).get(0);
    }

    protected abstract ColumnMapper<?, ?>[] getColumnMappers();

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public T nullSafeGet(ResultSet resultSet, String[] strings, SharedSessionContractImplementor session, Object object) throws SQLException {

    	beforeNullSafeOperation(session);
    	
    	final SharedSessionContractImplementor mySession = doWrapSession(session);
    	
    	try {
	        Object[] convertedColumns = new Object[getColumnMappers().length];
	
	        for (int getIndex = 0; getIndex < getColumnMappers().length; getIndex++) {
	            ColumnMapper nextMapper = getColumnMappers()[getIndex];
	
	            final Object converted = nextMapper.getHibernateType().nullSafeGet(resultSet, strings[getIndex], mySession, object);
	
	            if (converted != null) {
	                convertedColumns[getIndex] = nextMapper.fromNonNullValue(converted);
	            }
	        }
	
	        for (int i = 0; i < convertedColumns.length; i++) {
	            if (convertedColumns[i] != null) {
	                return fromConvertedColumns(convertedColumns);
	            }
	        }
	
	        return null;
	        
    	} finally {
    		afterNullSafeOperation(session);
    	}
    }

    protected abstract T fromConvertedColumns(Object[] convertedColumns);

    protected abstract Object[] toConvertedColumns(T value);

    @SuppressWarnings("unchecked")
    @Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index, SharedSessionContractImplementor session) throws SQLException {

    	beforeNullSafeOperation(session);
    	
    	final SharedSessionContractImplementor mySession = doWrapSession(session);
    	
    	try {
	        final Object[] valuesToSet = new Object[getColumnMappers().length];
	
	        if (value != null) {
	
	            final T myValue = (T) value;
	            Object[] convertedColumns = toConvertedColumns(myValue);
	
	            for (int cIdx = 0; cIdx < valuesToSet.length; cIdx++) {
	
	                @SuppressWarnings("rawtypes") ColumnMapper nextMapper = getColumnMappers()[cIdx];
	                valuesToSet[cIdx] = nextMapper.toNonNullValue(convertedColumns[cIdx]);
	            }
	        }
	
	        for (int setIndex = 0; setIndex < valuesToSet.length; setIndex++) {
	
	            @SuppressWarnings("rawtypes") ColumnMapper nextMapper = getColumnMappers()[setIndex];

	            // TODO Still need to work out where an adjuster will be injected
	            nextMapper.getHibernateType().nullSafeSet(preparedStatement, valuesToSet[setIndex], index + setIndex, mySession);
	        }
	        
		} finally {
			afterNullSafeOperation(session);
		}
    }

	protected SharedSessionContractImplementor doWrapSession(SharedSessionContractImplementor session) {
		return session;
	}
    
    @Override
    public String[] getPropertyNames() {
        return defaultPropertyNames;
    }

    @Override
    public Type[] getPropertyTypes() {
        return copyOf(hibernateTypes);
    }

    @Override
    public Object getPropertyValue(Object component, int property) throws HibernateException {

        if (!returnedClass().isAssignableFrom(component.getClass())) {
            throw new HibernateException("getPropertyValue called with incorrect class: {" + component.getClass() + "}");
        }
        @SuppressWarnings("unchecked") Object[] cols = toConvertedColumns((T) component);
        return cols[property];
    }

    @Override
    public void setPropertyValue(Object component, int property, Object value) throws HibernateException {
        throw new HibernateException("Called setPropertyValue on an immutable type {" + component.getClass() + "}");
    }

    @Override
    public Serializable disassemble(Object value, SharedSessionContractImplementor session) throws HibernateException {
        return super.disassemble(value);
    }

    @Override
    public Object assemble(Serializable cached, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return super.assemble(cached, owner);
    }

    @Override
    public Object replace(Object original, Object target, SharedSessionContractImplementor session, Object owner) throws HibernateException {
        return super.replace(original, target, owner);
    }
}
