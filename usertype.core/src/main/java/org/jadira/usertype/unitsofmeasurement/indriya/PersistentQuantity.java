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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.spi.ServiceProvider;
import javax.measure.spi.SystemOfUnitsService;

import org.hibernate.SessionFactory;
import org.jadira.usertype.spi.shared.AbstractParameterizedUserType;
import org.jadira.usertype.spi.shared.ValidTypesConfigured;
import org.jadira.usertype.spi.utils.reflection.ClassLoaderUtils;
import org.jadira.usertype.unitsofmeasurement.indriya.columnmapper.StringColumnQuantityMapper;
import org.jadira.usertype.unitsofmeasurement.indriya.util.UnitConfigured;

/**
 * Persists a Quantity using the specified unit. Out of the box, the supported units are those defined in the generally used System of Units.
 * If you want to add additional units, you must either specify them using the parameter 'validTypes', where they can be specified as a comma separated
 * list of class names. Define the unit using its symbol via the parameter 'unit' - or, if the type has not been specified (or is built in), you can 
 * also use its fully qualified class name.
 */
public class  PersistentQuantity<Q extends Quantity<Q>> extends AbstractParameterizedUserType<Q, String, StringColumnQuantityMapper<Q>> implements ValidTypesConfigured<Unit<?>> {

	private static final long serialVersionUID = -2015829087239519037L;
    
    private static final Map<String, Unit<?>> BASE_UNITS_MAP = new HashMap<String, Unit<?>>();

	private static SystemOfUnitsService SYSTEM_OF_UNITS_SERVICE = ServiceProvider.current().getSystemOfUnitsService();
    
    static {
        for (Unit<?> next : SYSTEM_OF_UNITS_SERVICE.getSystemOfUnits().getUnits()) {
        	BASE_UNITS_MAP.put(next.getSymbol(), next);
        }
    }
    
    private static final Map<String, Unit<?>> unitsMap = new HashMap<String, Unit<?>>();
    
	private List<Class<Unit<?>>> validTypes;
    		
    public PersistentQuantity() {
    	super();
	}
	
	@Override
	public void applyConfiguration(SessionFactory sessionFactory) {
		super.applyConfiguration(sessionFactory);
		doApplyConfiguration();
    }
    
	private <Z> void doApplyConfiguration() {

//		if (ValidTypesConfigured.class.isAssignableFrom(this.getClass())) {
		ValidTypesConfigured<Unit<?>> next = (ValidTypesConfigured<Unit<?>>)this;			
		performValidTypesConfiguration(next);
//		}
//		if (ValidTypesConfigured.class.isAssignableFrom(getColumnMapper().getClass())) {			
//		ValidTypesConfigured<Unit<?>> next = (ValidTypesConfigured<Unit<?>>)getColumnMapper();
//		performValidTypesConfiguration(next);
//		}
//		if (UnitConfigured.class.isAssignableFrom(this.getClass())) {
//		UnitConfigured unitConfigured = (UnitConfigured)this;			
//		performUnitConfiguration(unitConfigured);
//		}
		if (UnitConfigured.class.isAssignableFrom(getColumnMapper().getClass())) {
			UnitConfigured<?> unitConfigured = (UnitConfigured<?>)getColumnMapper();
			performUnitConfiguration(unitConfigured);
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void performUnitConfiguration(UnitConfigured<?> unitConfigured) {
        String unitString = null;
        if (getParameterValues() != null) {
        	unitString = getParameterValues().getProperty("unit");
        }
		if (unitString != null) {				
			try {
				Unit<?> unit = BASE_UNITS_MAP.get(unitString);
				
				if (unit == null) {
					unit = unitsMap.get(unitString);
				}
				if (unit == null) {
					final Class<Unit<?>> unitClass = (Class<Unit<?>>)(ClassLoaderUtils.classForName(unitString));
					final Unit<?> myUnit = unitClass.newInstance();
					unit = myUnit;
				}
				((StringColumnQuantityMapper) unitConfigured).setUnit(unit);
			} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
				throw new IllegalStateException("Cannot find specified class or unit " + unitString, e);
			}
		}
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

	@Override
	public void setValidTypes(List<Class<Unit<?>>> types) {
		for (Class<Unit<?>> next : types) {
			Unit<?> unit;
			try {
				unit = next.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalStateException("Cannot instantiate " + next.getName() + ": " + e.getMessage(), e);
			}
			unitsMap.put(unit.getSymbol(), unit);
		}
		this.validTypes = Collections.unmodifiableList(types);
	}

	@Override
	public List<Class<Unit<?>>> getValidTypes() {
		return this.validTypes;
	}
}
