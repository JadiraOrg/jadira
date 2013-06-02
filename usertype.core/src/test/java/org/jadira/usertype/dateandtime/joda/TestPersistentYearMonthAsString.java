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
package org.jadira.usertype.dateandtime.joda;

import org.jadira.usertype.dateandtime.joda.testmodel.YearMonthAsStringHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.YearMonth;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestPersistentYearMonthAsString extends AbstractDatabaseTest<YearMonthAsStringHolder> {

    private static final YearMonth[] yearMonths = new YearMonth[]{new YearMonth(2011, 1), new YearMonth(2000, 01), new YearMonth(1999, 12)};

    public TestPersistentYearMonthAsString() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < yearMonths.length; i++) {
            YearMonthAsStringHolder item = new YearMonthAsStringHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setYearMonth(yearMonths[i]);

            persist(item);
        }

        for (int i = 0; i < yearMonths.length; i++) {

            YearMonthAsStringHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(yearMonths[i], item.getYearMonth());
        }

        verifyDatabaseTable();
    }
}
