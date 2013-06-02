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
import org.jadira.usertype.moneyandcurrency.joda.testmodel.CurrencyUnitHolder;
import org.joda.money.CurrencyUnit;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestPersistentCurrencyUnit extends AbstractDatabaseTest<CurrencyUnitHolder> {

    private static final CurrencyUnit[] currencies = new CurrencyUnit[]{CurrencyUnit.EUR, CurrencyUnit.USD, CurrencyUnit.GBP, CurrencyUnit.getInstance("SAR"), null};

    public TestPersistentCurrencyUnit() {
    	super(TestJodaMoneySuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < currencies.length; i++) {
            CurrencyUnitHolder item = new CurrencyUnitHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setCurrencyUnit(currencies[i]);

            persist(item);
        }

        for (int i = 0; i < currencies.length; i++) {
            CurrencyUnitHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            if (currencies[i] == null) {
                assertNull(item.getCurrencyUnit());
            } else {
                assertEquals(currencies[i].toString(), item.getCurrencyUnit().toString());
            }
        }

        verifyDatabaseTable();
    }
}
