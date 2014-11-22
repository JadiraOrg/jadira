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
package org.jadira.usertype.moneyandcurrency.moneta.columnmapper;

import java.math.BigDecimal;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;
import javax.money.MonetaryCurrencies;

import org.jadira.usertype.moneyandcurrency.moneta.util.CurrencyUnitConfigured;
import org.jadira.usertype.spi.shared.AbstractLongColumnMapper;
import org.javamoney.moneta.Money;

public class LongColumnMoneyMinorMapper extends AbstractLongColumnMapper<MonetaryAmount> implements CurrencyUnitConfigured {

    private static final long serialVersionUID = 4205713919952452881L;

    private CurrencyUnit currencyUnit;

    @Override
    public Money fromNonNullValue(Long val) {
    	BigDecimal minorVal = BigDecimal.valueOf(val, currencyUnit.getDefaultFractionDigits());
    	return Money.of(currencyUnit, minorVal);
    }

    @Override
    public Long toNonNullValue(MonetaryAmount value) {
    	if (!currencyUnit.equals(value.getCurrency())) {
    		throw new IllegalStateException("Expected currency " + currencyUnit.getCurrencyCode() + " but was " + value.getCurrency());
    	}
        BigDecimal val = value.getNumber().numberValue(BigDecimal.class);

    	return val.movePointRight(currencyUnit.getDefaultFractionDigits()).longValue();
    }

	@Override
	public Money fromNonNullString(String s) {

		int separator = s.indexOf(' ');

		String currency = s.substring(0, separator);
		String value = s.substring(separator + 1);

		return Money.of(MonetaryCurrencies.getCurrency(currency), Long.parseLong(value));
	}

	@Override
	public String toNonNullString(MonetaryAmount value) {
		return value.toString();
	}

	@Override
    public void setCurrencyUnit(CurrencyUnit currencyUnit) {
        this.currencyUnit = currencyUnit;
    }
}
