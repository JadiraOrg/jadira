package org.jadira.usertype.spi.shared;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.UserType;

public abstract class AbstractMapUserType<K,V> extends AbstractUserType implements UserType, Serializable {

	private static final long serialVersionUID = 4071411441689437245L;

	@Override
	public int[] sqlTypes() {
        return new int[] {Types.LONGVARCHAR};
    }
	
	@Override
	public boolean isMutable() {
		return true;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Class<Map> returnedClass() {
		return Map.class;
	}

	@Override
    public int hashCode(Object x) throws HibernateException {
        assert (x != null);
        return x.hashCode();
    }
	
    @Override
    public Map<K, V> nullSafeGet(ResultSet resultSet, String[] strings, SharedSessionContractImplementor session, Object object) throws SQLException {
        
    	beforeNullSafeOperation(session);
    	
    	try {
	    	Map<K, V> converted = doNullSafeGet(resultSet, strings, session, object);
	        if (converted == null) {
	            return null;
	        }
	
	        return converted;
	        
    	} finally {
    		afterNullSafeOperation(session);
    	}
    }

    protected Map<K,V> doNullSafeGet(ResultSet resultSet, String[] strings, SharedSessionContractImplementor session, Object object) throws SQLException {
		
		String value = resultSet.getString(strings[0]);
		if (value == null) {
			return null;
		}
		return toMap(value);
	}

	@SuppressWarnings("unchecked")
	@Override
    public void nullSafeSet(PreparedStatement preparedStatement, Object value, int index, SharedSessionContractImplementor session) throws SQLException {

    	beforeNullSafeOperation(session);
    	
    	try {	
	        doNullSafeSet(preparedStatement, (Map<K, V>) value, index, session);
	        
    	} finally {
    		afterNullSafeOperation(session);
    	}
    }

    protected void doNullSafeSet(PreparedStatement preparedStatement, Map<K, V> value, int index, SharedSessionContractImplementor session) throws SQLException {
		
    	if (value == null) {
			preparedStatement.setNull(index, sqlTypes()[0]);
		} else {
			String strValue = toString((Map<K, V>) value);
			preparedStatement.setString(index, strValue);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Map<String,?> deepCopy(Object value) throws HibernateException {
		
		if (value == null) {
			return null;
		}

		if (!(value instanceof HashMap)) {
			throw new UnsupportedOperationException("can't convert " + value.getClass());
		}

		return new HashMap((HashMap) value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		if (!(value instanceof HashMap)) {
			throw new UnsupportedOperationException("can't convert " + value.getClass());
		}
		return toString((Map<K, V>) value);
	}

	@Override
	public Object assemble(Serializable value, Object o) throws HibernateException {
		return toMap((String) value);
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return deepCopy(original);
	}

	protected abstract String toString(Map<K, V> map);
	
	protected abstract Map<K, V> toMap(String value);
}
