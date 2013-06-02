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
import org.jadira.usertype.moneyandcurrency.joda.testmodel.CurrencyHolder;
import org.junit.Test;

import java.util.Currency;

import static org.junit.Assert.*;

public class TestPersistentCurrency extends AbstractDatabaseTest<CurrencyHolder> {

    private static final Currency[] currencies = new Currency[]{Currency.getInstance("EUR"), Currency.getInstance("USD"), Currency.getInstance("GBP"), Currency.getInstance("SAR"), null};

    public TestPersistentCurrency() {
    	super(TestJodaMoneySuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < currencies.length; i++) {
            CurrencyHolder item = new CurrencyHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setCurrency(currencies[i]);

            persist(item);
        }

        for (int i = 0; i < currencies.length; i++) {
            CurrencyHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            if (currencies[i] == null) {
                assertNull(item.getCurrency());
            } else {
                assertEquals(currencies[i].toString(), item.getCurrency().toString());
            }
        }

        verifyDatabaseTable();
    }
}
