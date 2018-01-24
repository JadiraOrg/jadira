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

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.spi.timezone.proxy.WrapsSession;
import org.jadira.usertype.spi.utils.runtime.JavaVersion;
import org.jadira.usertype.corejava.ConcurrentHashMapBackedProperties;

public abstract class AbstractParameterizedUserType<T, J, C extends ColumnMapper<T, J>> extends AbstractSingleColumnUserType<T, J, C> implements ParameterizedType, IntegratorConfiguredType {

	private static final long serialVersionUID = -8038898451426631564L;

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
		
	    if (JavaVersion.isJava8OrLater() &&
	            Jdbc42Configured.class.isAssignableFrom(getColumnMapper().getClass())) {
	        Jdbc42Configured next = (Jdbc42Configured)this;
	        performJdbc42Configuration(next);
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
		if (WrapsSession.class.isAssignableFrom(getColumnMapper().getClass())) {
			mySession = ((WrapsSession)getColumnMapper()).wrapSession(mySession);
		}
		if (WrapsSession.class.isAssignableFrom(this.getClass())) {
			mySession = ((WrapsSession)this).wrapSession(mySession);
		}
		return mySession;
	}
}
