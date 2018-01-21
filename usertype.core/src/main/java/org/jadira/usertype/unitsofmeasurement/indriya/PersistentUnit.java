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
package org.jadira.usertype.unitsofmeasurement.indriya;

import java.util.ArrayList;
import java.util.List;

import javax.measure.Unit;

import org.hibernate.SessionFactory;
import org.jadira.usertype.spi.shared.AbstractParameterizedUserType;
import org.jadira.usertype.spi.shared.ValidTypesConfigured;
import org.jadira.usertype.spi.utils.reflection.ClassLoaderUtils;
import org.jadira.usertype.unitsofmeasurement.indriya.columnmapper.StringColumnUnitMapper;

/**
 * Persists a Unit using its assigned Symbol. Out of the box, the supported units are those defined in the generally used System of Units.
 * If you want to add additional units, you must specify them using the parameter 'validTypes', where they can be specified as a comma separated
 * list of class names.
 */
public class PersistentUnit extends AbstractParameterizedUserType<Unit<?>, String, StringColumnUnitMapper> {

	private static final long serialVersionUID = -2015829087239519037L;
	
	@Override
	public void applyConfiguration(SessionFactory sessionFactory) {
		super.applyConfiguration(sessionFactory);
		doApplyConfiguration();
    }
    
	private <Z> void doApplyConfiguration() {

//		if (ValidTypesConfigured.class.isAssignableFrom(this.getClass())) {
//				
//			ValidTypesConfigured<Unit<?>> next = (ValidTypesConfigured<Unit<?>>)this;			
//			performValidTypesConfiguration(next);
//		}
//		if (ValidTypesConfigured.class.isAssignableFrom(getColumnMapper().getClass())) {			
		ValidTypesConfigured<Unit<?>> next = (ValidTypesConfigured<Unit<?>>)getColumnMapper();
		performValidTypesConfiguration(next);
//		}
	}
	
	private void performValidTypesConfiguration(ValidTypesConfigured<Unit<?>> next) {
		
        String validTypesString = null;
        if (getParameterValues() != null) {
        	validTypesString = getParameterValues().getProperty("validTypes");
        }
		if (validTypesString != null) {
			String[] validTypes = validTypesString.split(",");
			List<Class<Unit<?>>> units = new ArrayList<>();
			for (String nextType : validTypes) {
				
				
				try {
					@SuppressWarnings("unchecked")
					Class<Unit<?>> nextClass = (Class<Unit<?>>)(ClassLoaderUtils.classForName(nextType));
					units.add(nextClass);
				} catch (ClassNotFoundException e) {
					throw new IllegalStateException("Cannot find specified class " + nextType, e);
				}
				
			}
			next.setValidTypes(units);
		}
	}
}
