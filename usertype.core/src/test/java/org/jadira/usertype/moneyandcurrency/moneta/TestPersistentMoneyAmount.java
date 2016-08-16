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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;

import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.jadira.usertype.moneyandcurrency.moneta.testmodel.MoneyAmountHolder;
import org.javamoney.moneta.Money;
import org.junit.Test;

public class TestPersistentMoneyAmount extends AbstractDatabaseTest<MoneyAmountHolder> {

    private static final Money[] moneys = new Money[] { Money.of(BigDecimal.valueOf(100), "USD"),
            Money.of(new BigDecimal("100.10"), "USD"), Money.of(new BigDecimal("0.99"), "USD"), null };

    public TestPersistentMoneyAmount() {
        super(TestMonetaMoneySuite.getFactory());
    }

    @Test
    public void testPersist() {
        for (int i = 0; i < moneys.length; i++) {
            MoneyAmountHolder item = new MoneyAmountHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setMoney(moneys[i]);

            persist(item);
        }

        for (int i = 0; i < moneys.length; i++) {
            MoneyAmountHolder item = find((long) i);

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
