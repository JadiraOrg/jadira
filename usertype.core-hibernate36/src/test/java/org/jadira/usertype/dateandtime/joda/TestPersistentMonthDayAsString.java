package org.jadira.usertype.dateandtime.joda;
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

import org.jadira.usertype.dateandtime.joda.testmodel.MonthDayAsStringHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.MonthDay;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestPersistentMonthDayAsString extends AbstractDatabaseTest<MonthDayAsStringHolder> {

    private static final MonthDay[] monthDays = new MonthDay[]{new MonthDay(1, 1), new MonthDay(2, 29), new MonthDay(12, 31)};

    public TestPersistentMonthDayAsString() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < monthDays.length; i++) {
            MonthDayAsStringHolder item = new MonthDayAsStringHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setMonthDay(monthDays[i]);

            persist(item);
        }


        for (int i = 0; i < monthDays.length; i++) {

            MonthDayAsStringHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(monthDays[i], item.getMonthDay());
        }

        verifyDatabaseTable();
    }
}
