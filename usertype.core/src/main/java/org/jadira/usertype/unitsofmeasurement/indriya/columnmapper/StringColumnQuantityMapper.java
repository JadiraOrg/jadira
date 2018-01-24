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

import org.jadira.usertype.spi.shared.AbstractStringColumnMapper;
import org.jadira.usertype.unitsofmeasurement.indriya.util.UnitConfigured;

import tec.units.indriya.quantity.Quantities;

public class StringColumnQuantityMapper<Q extends Quantity<Q>> extends AbstractStringColumnMapper<Q> implements UnitConfigured<Q> {

    private static final long serialVersionUID = 4205713919952452881L;
	
    private Unit<Q> unit;
    
    @Override
    public Q fromNonNullValue(String val) {
    
    	@SuppressWarnings("unchecked")
		final Q quantity = (Q) Quantities.getQuantity(val + " " + unit.getSymbol());
    	return quantity;
    }

    @Override
    public String toNonNullValue(Q value) {
    	if (!unit.getClass().isAssignableFrom(value.getUnit().getClass())) {
    		throw new IllegalStateException("Expected unit " + unit.getSymbol() + " but was " + value.getUnit().getSymbol());
    	}
    	return value.getValue().toString();
    }
	
	@Override
    public void setUnit(Unit<Q> unit) {
        this.unit = unit;
    }
}
