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

import org.jadira.usertype.dateandtime.shared.spi.AbstractBigDecimalColumnMapper;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public class BigDecimalColumnMoneyMapper extends AbstractBigDecimalColumnMapper<Money> {

    private static final long serialVersionUID = 4205713919952452881L;
	
    private CurrencyUnit currency;

    @Override
    public Money fromNonNullValue(BigDecimal val) {
        return Money.of(currency, val);
    }

    @Override
    public BigDecimal toNonNullValue(Money value) {
        return value.getAmount();
    }

	@Override
	public Money fromNonNullString(String s) {
		return Money.parse(s);
	}

	@Override
	public String toNonNullString(Money value) {
		return value.toString();
	}
	
    public void setCurrency(CurrencyUnit currency) {
        this.currency = currency;
    }
}
