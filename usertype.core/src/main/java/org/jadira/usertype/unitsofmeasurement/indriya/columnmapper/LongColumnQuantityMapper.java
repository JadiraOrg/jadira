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
package org.jadira.usertype.unitsofmeasurement.indriya.columnmapper;

import javax.measure.Quantity;
import javax.measure.Unit;

import org.jadira.usertype.spi.shared.AbstractLongColumnMapper;
import org.jadira.usertype.unitsofmeasurement.indriya.util.UnitConfigured;

import tec.units.indriya.quantity.Quantities;

public class LongColumnQuantityMapper extends AbstractLongColumnMapper<Quantity<?>> implements UnitConfigured {

    private static final long serialVersionUID = 4205713919952452881L;
	
    private Unit<?> unit;
    
    @Override
    public Quantity<?> fromNonNullValue(Long val) {
    
    	return Quantities.getQuantity(val, unit);
    }

    @Override
    public Long toNonNullValue(Quantity<?> value) {
    	if (!unit.equals(value.getUnit())) {
    		throw new IllegalStateException("Expected unit " + unit.getSymbol() + " but was " + value.getUnit().getSymbol());
    	}
    	return Long.parseLong(value.getValue().toString());
    }

	@Override
	public Quantity<?> fromNonNullString(String s) {
		Quantity<?> quantity = Quantities.getQuantity(s);
    	if (!unit.equals(quantity.getUnit())) {
    		throw new IllegalStateException("Expected unit " + unit.getSymbol() + " but was " + quantity.getUnit().getSymbol());
    	}
    	return quantity;
	}

	@Override
	public String toNonNullString(Quantity<?> value) {
    	if (!unit.equals(value.getUnit())) {
    		throw new IllegalStateException("Expected unit " + unit.getSymbol() + " but was " + value.getUnit().getSymbol());
    	}
		return value.toString();
	}
	
	@Override
    public void setUnit(Unit<?> unit) {
        this.unit = unit;
    }
}
