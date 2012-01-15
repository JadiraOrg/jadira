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

import org.jadira.usertype.moneyandcurrency.joda.columnmapper.IntegerColumnCurrencyUnitMapper;
import org.jadira.usertype.spi.shared.AbstractSingleColumnUserType;
import org.joda.money.CurrencyUnit;

/**
 * Maps a {@link CurrencyUnit} to and from an Integer for Hibernate.
 */
public class PersistentCurrencyUnitAsInteger extends AbstractSingleColumnUserType<CurrencyUnit, Integer, IntegerColumnCurrencyUnitMapper> {

	private static final long serialVersionUID = 39712131217254970L;
}
