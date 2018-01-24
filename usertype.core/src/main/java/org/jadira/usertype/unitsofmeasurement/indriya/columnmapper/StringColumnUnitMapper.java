package org.jadira.usertype.unitsofmeasurement.indriya.columnmapper;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.measure.Unit;
import javax.measure.spi.ServiceProvider;
import javax.measure.spi.SystemOfUnitsService;

import org.jadira.usertype.spi.shared.AbstractStringColumnMapper;
import org.jadira.usertype.spi.shared.ValidTypesConfigured;

public class StringColumnUnitMapper extends AbstractStringColumnMapper<Unit<?>> implements ValidTypesConfigured<Unit<?>> {

    private static final long serialVersionUID = 4205713919952452881L;

	private static SystemOfUnitsService SYSTEM_OF_UNITS_SERVICE = ServiceProvider.current().getSystemOfUnitsService();
    
    private final Map<String, Unit<?>> unitsMap = new HashMap<String, Unit<?>>();

	private List<Class<Unit<?>>> validTypes;
    		
    public StringColumnUnitMapper() {
    	for (Unit<?> next : SYSTEM_OF_UNITS_SERVICE.getSystemOfUnits().getUnits()) {
    		unitsMap.put(next.getSymbol(), next);
        }
	}	
    
    @Override
    public Unit<?> fromNonNullValue(String s) {
    	return unitsMap.get(s);
    }

    @Override
    public String toNonNullValue(Unit<?> value) {
        return value.getSymbol();
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