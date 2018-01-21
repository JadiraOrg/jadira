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

import org.hibernate.SessionFactory;

public abstract class AbstractParameterizedTemporalUserType<T, J, C extends ColumnMapper<T, J>> extends AbstractParameterizedUserType<T, J, C> {

	private static final long serialVersionUID = -8038898451426631564L;

	@Override
	public void applyConfiguration(SessionFactory sessionFactory) {
		super.applyConfiguration(sessionFactory);
		doApplyConfiguration();
    }
    
	private <Z> void doApplyConfiguration() {

		if (DatabaseZoneConfigured.class.isAssignableFrom(this.getClass())) {
				
			DatabaseZoneConfigured next = (DatabaseZoneConfigured)this;			
			performDatabaseZoneConfiguration(next);
		}
		if (JavaZoneConfigured.class.isAssignableFrom(this.getClass())) {
			
			@SuppressWarnings("unchecked")
			JavaZoneConfigured<Z> next = (JavaZoneConfigured<Z>)this;			
			performJavaZoneConfiguration(next);
		}
		
		if (DatabaseZoneConfigured.class.isAssignableFrom(getColumnMapper().getClass())) {

			DatabaseZoneConfigured next = (DatabaseZoneConfigured)getColumnMapper();
			performDatabaseZoneConfiguration(next);
		}		
		if (JavaZoneConfigured.class.isAssignableFrom(getColumnMapper().getClass())) {
			
			@SuppressWarnings("unchecked")
			JavaZoneConfigured<Z> next = (JavaZoneConfigured<Z>)getColumnMapper();

			performJavaZoneConfiguration(next);
		}
	}
	
	private <Z> void performDatabaseZoneConfiguration(DatabaseZoneConfigured next) {
		
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
            	next.setJavaZone(next.parseJavaZone(javaZone));
            }
        }
	}	
}
