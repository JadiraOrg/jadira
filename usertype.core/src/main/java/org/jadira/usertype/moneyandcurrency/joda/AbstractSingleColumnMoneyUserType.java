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

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.moneyandcurrency.joda.util.CurrencyUnitConfiguredColumnMapper;
import org.jadira.usertype.spi.shared.AbstractSingleColumnUserType;
import org.jadira.usertype.spi.shared.ColumnMapper;
import org.jadira.usertype.spi.shared.ConfigurationHelper;
import org.jadira.usertype.spi.shared.IntegratorConfiguredType;
import org.joda.money.CurrencyUnit;

/**
 * Base class for money types that do not map a currency column using a configured currency instead.
 */
public abstract class AbstractSingleColumnMoneyUserType<T, J, C extends ColumnMapper<T, J>> extends AbstractSingleColumnUserType<T, J, C> implements ParameterizedType, IntegratorConfiguredType {

	private static final long serialVersionUID = 8244061728586173961L;

	private Properties parameterValues;

    private CurrencyUnit currencyUnit;
    
    @Override
    public void setParameterValues(Properties parameters) {
    	this.parameterValues = parameters;
    }
    
    protected Properties getParameterValues() {
    	return parameterValues;
    }
    
    @Override
	public void applyConfiguration(SessionFactory sessionFactory) {

    	CurrencyUnitConfiguredColumnMapper<T, J> columnMapper = (CurrencyUnitConfiguredColumnMapper<T, J>) getColumnMapper();
    	if (currencyUnit == null) {

    		String currencyString = null;
			if (parameterValues != null) {
				currencyString = parameterValues.getProperty("currencyCode");
			}
			if (currencyString == null) {
				currencyString = ConfigurationHelper.getProperty("currencyCode");
			}
			if (currencyString != null) {

				currencyUnit = CurrencyUnit.of(currencyString);
			} else {
				throw new IllegalStateException(getClass().getSimpleName() + " requires currencyCode to be defined as a parameter, or the jadira.usertype.currencyCode Hibernate property to be defined");
			}
    	}
    	columnMapper.setCurrencyUnit(currencyUnit);
    }
}
