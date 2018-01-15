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
import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.spi.utils.runtime.JavaVersion;

public abstract class AbstractParameterizedUserType<T, J, C extends ColumnMapper<T, J>> extends AbstractSingleColumnUserType<T, J, C> implements ParameterizedType, IntegratorConfiguredType {

	private static final long serialVersionUID = -8038898451426631564L;

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
		doApplyConfiguration();
    }
    
	private <Z> void doApplyConfiguration() {

	    if (JavaVersion.isJava8OrLater() &&
	            Jdbc42Configured.class.isAssignableFrom(this.getClass())) {
	        Jdbc42Configured next = (Jdbc42Configured)this;
	        performJdbc42Configuration(next);
	    }
		if (DatabaseZoneConfigured.class.isAssignableFrom(this.getClass())) {
				
			@SuppressWarnings("unchecked")
			DatabaseZoneConfigured<Z> next = (DatabaseZoneConfigured<Z>)this;			
			performDatabaseZoneConfiguration(next);
		}
		if (JavaZoneConfigured.class.isAssignableFrom(this.getClass())) {
			
			@SuppressWarnings("unchecked")
			JavaZoneConfigured<Z> next = (JavaZoneConfigured<Z>)this;			
			performJavaZoneConfiguration(next);
		}
		
		if (DatabaseZoneConfigured.class.isAssignableFrom(getColumnMapper().getClass())) {

			@SuppressWarnings("unchecked")
			DatabaseZoneConfigured<Z> next = (DatabaseZoneConfigured<Z>)getColumnMapper();
			
			performDatabaseZoneConfiguration(next);
		}
		
		if (JavaZoneConfigured.class.isAssignableFrom(getColumnMapper().getClass())) {
			
			@SuppressWarnings("unchecked")
			JavaZoneConfigured<Z> next = (JavaZoneConfigured<Z>)getColumnMapper();

			performJavaZoneConfiguration(next);
		}
	}
	
	private <Z> void performDatabaseZoneConfiguration(DatabaseZoneConfigured<Z> next) {
		
        String databaseZone = null;
        if (getParameterValues() != null) {
        	databaseZone = getParameterValues().getProperty("databaseZone");
        }
		if (databaseZone == null) {
			databaseZone = ConfigurationHelper.getProperty("databaseZone");
		}
		
        if (databaseZone != null) {
            if ("jvm".equals(databaseZone)) {
                next.setDatabaseZone(null);
            } else {
            	next.setDatabaseZone(next.parseZone(databaseZone));
            }
        }
	}
	
	private <Z> void performJavaZoneConfiguration(JavaZoneConfigured<Z> next) {
		
		String javaZone = null;
        if (getParameterValues() != null) {
        	javaZone = getParameterValues().getProperty("javaZone");
        }
		if (javaZone == null) {
			javaZone = ConfigurationHelper.getProperty("javaZone");
		}
		
        if (javaZone != null) {
            if ("jvm".equals(javaZone)) {
                next.setJavaZone(null);
            } else {
            	next.setJavaZone(next.parseZone(javaZone));
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
}
