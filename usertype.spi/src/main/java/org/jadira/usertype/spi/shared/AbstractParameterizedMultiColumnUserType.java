package org.jadira.usertype.spi.shared;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.spi.timezone.proxy.WrapsSession;
import org.jadira.usertype.spi.utils.lang.ConcurrentHashMapBackedProperties;
import org.jadira.usertype.spi.utils.runtime.JavaVersion;

public abstract class AbstractParameterizedMultiColumnUserType<T> extends AbstractMultiColumnUserType<T> implements ParameterizedType, IntegratorConfiguredType {

	private static final long serialVersionUID = -5378286101759906332L;

	private Properties parameterValues;
    
    @Override
    public void setParameterValues(Properties parameters) {
    	this.parameterValues = new ConcurrentHashMapBackedProperties(parameters);
    }
    
    protected Properties getParameterValues() {
    	return parameterValues;
    }

	@Override
	public void applyConfiguration(SessionFactory sessionFactory) {
		doApplyConfiguration();
    }
    
	private <Z> void doApplyConfiguration() {

	    if (JavaVersion.isJava8OrLater() &&
	            Jdbc42Configured.class.isAssignableFrom(this.getClass())) {
	        Jdbc42Configured next = (Jdbc42Configured)this;
	        performJdbc42Configuration(next);
	    }
		
		for (int i = 0; i < getColumnMappers().length; i++) {
			
		    if (JavaVersion.isJava8OrLater() &&
		            Jdbc42Configured.class.isAssignableFrom(getColumnMappers()[i].getClass())) {
		        Jdbc42Configured next = (Jdbc42Configured)this;
		        performJdbc42Configuration(next);
		    }
		}
    }
		
	@SuppressWarnings("unused")
	private void performJdbc42Configuration(Jdbc42Configured next) {
        
        Boolean jdbc42Apis = null;
        if (getParameterValues() != null) {
            String apisString = getParameterValues().getProperty("jdbc42Apis");
            if (apisString != null) {
                jdbc42Apis = Boolean.parseBoolean(apisString);
            }
        }
        if (jdbc42Apis == null) {
            jdbc42Apis = ConfigurationHelper.getUse42Api();
        }
        if (jdbc42Apis == null) {
            jdbc42Apis = Boolean.FALSE;
        }
        
        next.setUseJdbc42Apis(jdbc42Apis);
    }
	
	@Override
	protected SharedSessionContractImplementor doWrapSession(SharedSessionContractImplementor session) {
		SharedSessionContractImplementor mySession = session;
		for (ColumnMapper<?, ?> next : getColumnMappers()) {
			if (WrapsSession.class.isAssignableFrom(next.getClass())) {
				mySession = ((WrapsSession)next).wrapSession(mySession);
			}
		}
		if (WrapsSession.class.isAssignableFrom(this.getClass())) {
			mySession = ((WrapsSession)this).wrapSession(mySession);
		}
		return mySession;
	}
}
