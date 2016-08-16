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

import javax.money.CurrencyUnit;
import javax.money.MonetaryAmount;

import org.jadira.usertype.moneyandcurrency.legacyjdk.columnmapper.LongLongColumnMapper;
import org.jadira.usertype.moneyandcurrency.moneta.columnmapper.StringColumnCurrencyUnitMapper;
import org.jadira.usertype.spi.shared.AbstractMultiColumnUserType;
import org.jadira.usertype.spi.shared.ColumnMapper;
import org.javamoney.moneta.Money;
import org.javamoney.moneta.function.MonetaryUtil;

/**
 * Persists the minor amount and currency from a Money instance
 */
public class PersistentMoneyMajorAmountAndCurrency extends AbstractMultiColumnUserType<MonetaryAmount> {

	private static final long serialVersionUID = -3990523657883978202L;

	private static final ColumnMapper<?, ?>[] COLUMN_MAPPERS = new ColumnMapper<?, ?>[] { new StringColumnCurrencyUnitMapper(), new LongLongColumnMapper<Long>() };

    private static final String[] PROPERTY_NAMES = new String[]{ "currencyUnit", "amountMajor" };
	
	@Override
	protected ColumnMapper<?, ?>[] getColumnMappers() {
		return COLUMN_MAPPERS;
	}

    @Override
    protected Money fromConvertedColumns(Object[] convertedColumns) {

        CurrencyUnit currencyUnitPart = (CurrencyUnit) convertedColumns[0];
        Long amountMajorPart = (Long) convertedColumns[1];
        Money money = Money.of(amountMajorPart, currencyUnitPart);

        return money;
    }

    @Override
    protected Object[] toConvertedColumns(MonetaryAmount value) {

        return new Object[] { value.getCurrency(), MonetaryUtil.majorPart().apply(value).getNumber().longValue() };
    }
    
    @Override
	public String[] getPropertyNames() {
		return PROPERTY_NAMES;
	}
}
