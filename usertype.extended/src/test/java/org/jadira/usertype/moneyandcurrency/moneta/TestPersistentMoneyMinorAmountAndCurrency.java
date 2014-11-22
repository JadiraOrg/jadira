/*
 *  Copyright 2010, 2011 Christopher Pheby
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

import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.jadira.usertype.moneyandcurrency.moneta.testmodel.MoneyMinorAmountAndCurrencyHolder;
import org.javamoney.moneta.Money;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestPersistentMoneyMinorAmountAndCurrency extends AbstractDatabaseTest<MoneyMinorAmountAndCurrencyHolder> {

	private static final Money[] moneys = new Money[]{ Money.of(BigDecimal.valueOf(100), "USD"), Money.of(new BigDecimal("100.10"), "USD"), Money.of(new BigDecimal("0.99"), "EUR"), Money.of(new BigDecimal("-0.99"), "EUR"), null};

    public TestPersistentMoneyMinorAmountAndCurrency() {
    	super(TestMonetaMoneySuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < moneys.length; i++) {
            MoneyMinorAmountAndCurrencyHolder item = new MoneyMinorAmountAndCurrencyHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setMoney(moneys[i]);

            persist(item);
        }

        for (int i = 0; i < moneys.length; i++) {
            MoneyMinorAmountAndCurrencyHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            if (moneys[i] == null) {
                assertNull(item.getMoney());
            } else {
                assertTrue(moneys[i].isEqualTo(item.getMoney()));
            }
        }

        verifyDatabaseTable();
    }
}
