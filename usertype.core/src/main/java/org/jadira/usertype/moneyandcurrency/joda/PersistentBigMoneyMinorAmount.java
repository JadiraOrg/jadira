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

import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.moneyandcurrency.joda.columnmapper.LongColumnBigMoneyMinorMapper;
import org.jadira.usertype.spi.shared.IntegratorConfiguredType;
import org.joda.money.BigMoney;

/**
 * Maps {@link BigMoney} to and from a Long column using the minor amount for storage.
 * For example $100.34 will be stored as 10034.
 */
public class PersistentBigMoneyMinorAmount extends AbstractSingleColumnMoneyUserType<BigMoney, Long, LongColumnBigMoneyMinorMapper> implements ParameterizedType, IntegratorConfiguredType {

	private static final long serialVersionUID = -9177100044920987912L;
}
