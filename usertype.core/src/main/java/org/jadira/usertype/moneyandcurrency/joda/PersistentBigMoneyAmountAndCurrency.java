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
package org.jadira.usertype.moneyandcurrency.joda;

import java.math.BigDecimal;

import org.jadira.usertype.moneyandcurrency.joda.columnmapper.BigDecimalBigDecimalColumnMapper;
import org.jadira.usertype.moneyandcurrency.joda.columnmapper.StringColumnCurrencyUnitMapper;
import org.jadira.usertype.spi.shared.AbstractMultiColumnUserType;
import org.jadira.usertype.spi.shared.ColumnMapper;
import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;

/**
 * Persists the decimal amount and currency from a BigMoney instance
 */
public class PersistentBigMoneyAmountAndCurrency extends AbstractMultiColumnUserType<BigMoney> {

	private static final long serialVersionUID = 3735995469031558183L;

	private static final ColumnMapper<?, ?>[] columnMappers = new ColumnMapper<?, ?>[] { new StringColumnCurrencyUnitMapper(), new BigDecimalBigDecimalColumnMapper() };

    private static final String[] propertyNames = new String[]{ "currencyUnit", "amount" };
	
	@Override
	protected ColumnMapper<?, ?>[] getColumnMappers() {
		return columnMappers;
	}

    @Override
    protected BigMoney fromConvertedColumns(Object[] convertedColumns) {

        CurrencyUnit currencyUnitPart = (CurrencyUnit) convertedColumns[0];
        BigDecimal amountPart = (BigDecimal) convertedColumns[1];
        BigMoney money = BigMoney.of(currencyUnitPart, amountPart);

        return money;
    }

    @Override
    protected Object[] toConvertedColumns(BigMoney value) {

        return new Object[] { value.getCurrencyUnit(), value.getAmount() };
    }
    
    @Override
	public String[] getPropertyNames() {
		return propertyNames;
	}
}
