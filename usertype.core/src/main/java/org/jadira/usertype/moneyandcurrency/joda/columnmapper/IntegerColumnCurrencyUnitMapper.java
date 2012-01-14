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
package org.jadira.usertype.moneyandcurrency.joda.columnmapper;

import org.jadira.usertype.dateandtime.shared.spi.AbstractIntegerColumnMapper;
import org.joda.money.CurrencyUnit;
import org.joda.money.IllegalCurrencyException;

public class IntegerColumnCurrencyUnitMapper extends AbstractIntegerColumnMapper<CurrencyUnit> {

    private static final long serialVersionUID = 4205713919952452881L;

    @Override
    public CurrencyUnit fromNonNullValue(Integer i) {
        return CurrencyUnit.ofNumericCode(i);
    }

    @Override
    public Integer toNonNullValue(CurrencyUnit value) {
        return value.getNumericCode();
    }

	@Override
	public CurrencyUnit fromNonNullString(String s) {
		try {
			return CurrencyUnit.ofNumericCode(s);
		} catch (IllegalCurrencyException e) {
			return CurrencyUnit.of(s);
		}
	}

	@Override
	public String toNonNullString(CurrencyUnit value) {
		String str = value.getNumeric3Code();
		if ("".equals(str)) {
			return value.getCurrencyCode();
		} else {
			return str;
		}
	}
}
