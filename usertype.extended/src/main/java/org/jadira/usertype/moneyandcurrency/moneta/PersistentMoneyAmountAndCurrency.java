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
package org.jadira.usertype.moneyandcurrency.moneta;

import java.math.BigDecimal;

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

import org.jadira.usertype.moneyandcurrency.legacyjdk.columnmapper.BigDecimalBigDecimalColumnMapper;
import org.jadira.usertype.moneyandcurrency.moneta.columnmapper.StringColumnCurrencyUnitMapper;
import org.jadira.usertype.spi.shared.AbstractMultiColumnUserType;
import org.jadira.usertype.spi.shared.ColumnMapper;
import org.javamoney.moneta.Money;

/**
 * Persists the decimal amount and currency from a Money instance
 */
public class PersistentMoneyAmountAndCurrency extends AbstractMultiColumnUserType<MonetaryAmount> {

	private static final long serialVersionUID = 3735995469031558183L;

	private static final ColumnMapper<?, ?>[] COLUMN_MAPPERS = new ColumnMapper<?, ?>[] { new StringColumnCurrencyUnitMapper(), new BigDecimalBigDecimalColumnMapper() };

    private static final String[] PROPERTY_NAMES = new String[]{ "currencyUnit", "amount" };
	
	@Override
	protected ColumnMapper<?, ?>[] getColumnMappers() {
		return COLUMN_MAPPERS;
	}

    @Override
    protected Money fromConvertedColumns(Object[] convertedColumns) {

        CurrencyUnit currencyUnitPart = (CurrencyUnit) convertedColumns[0];
        BigDecimal amountPart = (BigDecimal) convertedColumns[1];

        return Money.of(amountPart, currencyUnitPart);
    }

    @Override
    protected Object[] toConvertedColumns(MonetaryAmount value) {

        return new Object[] { value.getCurrency(), value.getNumber().numberValue(BigDecimal.class) };
    }
    
    @Override
	public String[] getPropertyNames() {
		return PROPERTY_NAMES;
	}
}
