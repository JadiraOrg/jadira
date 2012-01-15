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

import java.math.BigDecimal;

import org.jadira.usertype.moneyandcurrency.joda.util.CurrencyUnitConfiguredColumnMapper;
import org.jadira.usertype.spi.shared.AbstractBigDecimalColumnMapper;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

public class BigDecimalColumnBigMoneyMapper extends AbstractBigDecimalColumnMapper<BigMoney> implements CurrencyUnitConfiguredColumnMapper<BigMoney, BigDecimal> {

    private static final long serialVersionUID = 4205713919952452881L;
	
    private CurrencyUnit currencyUnit;

    @Override
    public BigMoney fromNonNullValue(BigDecimal val) {
        return BigMoney.of(currencyUnit, val);
    }

    @Override
    public BigDecimal toNonNullValue(BigMoney value) {
    	if (!currencyUnit.equals(value.getCurrencyUnit())) {
    		throw new IllegalStateException("Expected currency " + currencyUnit.getCurrencyCode() + " but was " + value.getCurrencyUnit());
    	}
        return BigMoney.of(value).getAmount();
    }

	@Override
	public BigMoney fromNonNullString(String s) {
		return BigMoney.parse(s);
	}

	@Override
	public String toNonNullString(BigMoney value) {
		return value.toString();
	}
	
	@Override
    public void setCurrencyUnit(CurrencyUnit currencyUnit) {
        this.currencyUnit = currencyUnit;
    }
}
