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
import javax.measure.spi.SystemOfUnits;

import org.hibernate.SessionFactory;
import org.jadira.usertype.spi.shared.AbstractParameterizedMultiColumnUserType;
import org.jadira.usertype.spi.shared.ColumnMapper;
import org.jadira.usertype.spi.shared.ValidTypesConfigured;
import org.jadira.usertype.spi.utils.reflection.ArrayUtils;
import org.jadira.usertype.spi.utils.reflection.ClassLoaderUtils;
import org.jadira.usertype.unitsofmeasurement.indriya.columnmapper.StringColumnStringMapper;
import org.jadira.usertype.unitsofmeasurement.indriya.columnmapper.StringColumnUnitMapper;

import tec.units.indriya.quantity.Quantities;
import tec.units.indriya.unit.Units;

/**
 * Persists a Quantity using an arbitrary unit. Out of the box, the supported units are those defined in the generally used System of Units.
 * If you want to add additional units, you must either specify them using the parameter 'validTypes', where they can be specified as a comma separated
 * list of class names.
 */
public class PersistentQuantityAndUnit extends AbstractParameterizedMultiColumnUserType<Quantity<?>> implements ValidTypesConfigured<Unit<?>> {

	private static final long serialVersionUID = -2015829087239519037L;

    private static final SystemOfUnits UNITS = Units.getInstance();
    
    private final Map<String, Unit<?>> unitsMap = new HashMap<String, Unit<?>>();
    
	private List<Class<Unit<?>>> validTypes;
	
    private static final ColumnMapper<?, ?>[] COLUMN_MAPPERS = new ColumnMapper<?, ?>[] { new StringColumnStringMapper(), new StringColumnUnitMapper() };

    private static final String[] PROPERTY_NAMES = new String[]{ "quantity", "unit" };
    		
    public PersistentQuantityAndUnit() {
    	super();
    	
    	for (Unit<?> next : UNITS.getUnits()) {
    		unitsMap.put(next.getSymbol(), next);
        }
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
	
    @Override
    protected Quantity<?> fromConvertedColumns(Object[] convertedColumns) {

        String stringPart = (String) convertedColumns[0];
        Unit<?> unit = (Unit<?>) convertedColumns[1];

        return Quantities.getQuantity(stringPart + " " + unit.getSymbol());
    }

    @Override
    protected Object[] toConvertedColumns(Quantity<?> value) {
        return new Object[] { value.getValue().toString(), value.getUnit() };
    }
    
    @Override
    protected ColumnMapper<?, ?>[] getColumnMappers() {
        return COLUMN_MAPPERS;
    }

    @Override
    public String[] getPropertyNames() {
        return ArrayUtils.copyOf(PROPERTY_NAMES);
    }

}
