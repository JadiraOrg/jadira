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

public class LongColumnQuantityMapper<Q extends Quantity<Q>> extends AbstractLongColumnMapper<Q> implements UnitConfigured<Q> {

    private static final long serialVersionUID = 4205713919952452881L;
	
    private Unit<Q> unit;
    
	@Override
    public Q fromNonNullValue(Long val) {
    
		@SuppressWarnings("unchecked")
    	final Q result = (Q) Quantities.getQuantity(val, unit);
		return result;
    }

    @Override
    public Long toNonNullValue(Q value) {
    	if (!unit.getClass().isAssignableFrom(value.getUnit().getClass())) {
    		throw new IllegalStateException("Expected unit " + unit + " but was " + value.getUnit());
    	}
    	return Long.parseLong(value.getValue().toString());
    }

	@Override
	public Q fromNonNullString(String s) {
		@SuppressWarnings("unchecked")
		final Q quantity = (Q) Quantities.getQuantity(s);
    	if (!unit.getClass().isAssignableFrom(quantity.getUnit().getClass())) {
    		throw new IllegalStateException("Expected unit " + unit + " but was " + quantity.getUnit());
    	}
    	return quantity;
	}

	@Override
	public String toNonNullString(Q value) {
    	if (!unit.getClass().isAssignableFrom(value.getUnit().getClass())) {
    		throw new IllegalStateException("Expected unit " + unit + " but was " + value.getUnit());
    	}
		return value.toString();
	}
	
	@Override
    public void setUnit(Unit<Q> unit) {
        this.unit = unit;
    }
}
