package org.jadira.usertype.spi.shared;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.usertype.ParameterizedType;

public abstract class AbstractParameterizedMultiColumnUserType<T> extends AbstractMultiColumnUserType<T> implements ParameterizedType, IntegratorConfiguredType {

	private static final long serialVersionUID = -5378286101759906332L;

	private Properties parameterValues;
    
    @Override
    public void setParameterValues(Properties parameters) {
    	this.parameterValues = parameters;
    }
    
    protected Properties getParameterValues() {
    	return parameterValues;
    }

    @Override
	public void applyConfiguration(SessionFactory sessionFactory) {
    }
}
