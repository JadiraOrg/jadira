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
package org.jadira.usertype.moneyandcurrency.joda;

import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.jadira.usertype.moneyandcurrency.joda.testmodel.MoneyMajorAmountAndCurrencyHolder;
import org.joda.money.Money;
import org.junit.Test;

import java.math.RoundingMode;

import static org.junit.Assert.*;

public class TestPersistentMoneyMajorAmountAndCurrency extends AbstractDatabaseTest<MoneyMajorAmountAndCurrencyHolder> {

    private static final Money[] moneys = new Money[]{Money.parse("USD 100.00"), Money.parse("USD 100.10"), Money.parse("EUR 0.99"), Money.parse("EUR -0.99"), null};

    public TestPersistentMoneyMajorAmountAndCurrency() {
    	super(TestJodaMoneySuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < moneys.length; i++) {
            MoneyMajorAmountAndCurrencyHolder item = new MoneyMajorAmountAndCurrencyHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setMoney(moneys[i]);

            persist(item);
        }


        for (int i = 0; i < moneys.length; i++) {

            MoneyMajorAmountAndCurrencyHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            if (moneys[i] == null) {
                assertNull(item.getMoney());
            } else {
                assertEquals(moneys[i].rounded(0, RoundingMode.DOWN).toString(), item.getMoney().toString());
            }
        }

        verifyDatabaseTable();
    }
}
