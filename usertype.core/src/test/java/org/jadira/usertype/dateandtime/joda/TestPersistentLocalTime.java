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

import org.jadira.usertype.dateandtime.joda.testmodel.JodaLocalTimeHolder;
import org.jadira.usertype.dateandtime.joda.testmodel.TimeOfDayAsTimeJoda;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.LocalTime;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestPersistentLocalTime extends AbstractDatabaseTest<JodaLocalTimeHolder> {

    private static final LocalTime[] localTimes = new LocalTime[]{new LocalTime(14, 2, 25), new LocalTime(23, 59, 59), new LocalTime(0, 0, 0)};

    public TestPersistentLocalTime() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < localTimes.length; i++) {
            JodaLocalTimeHolder item = new JodaLocalTimeHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalTime(localTimes[i]);

            persist(item);
        }

        for (int i = 0; i < localTimes.length; i++) {

            JodaLocalTimeHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localTimes[i], item.getLocalTime());
        }

        verifyDatabaseTable();
    }

    @Test
    @Ignore
    @SuppressWarnings("deprecation")
    public void testRoundtripWithJodaTimeOfDay() {
        for (int i = 0; i < localTimes.length; i++) {
            TimeOfDayAsTimeJoda item = new TimeOfDayAsTimeJoda();
            item.setId(i);
            item.setName("test_" + i);
            org.joda.time.TimeOfDay next = new org.joda.time.TimeOfDay(localTimes[i]);
            item.setTimeOfDay(next);

            persist(item);
        }


        for (int i = 0; i < localTimes.length; i++) {

            JodaLocalTimeHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localTimes[i], item.getLocalTime());
        }
    }

    @Test
    @Ignore // Joda Time Contrib does not support Hibernate 4 yet
    @SuppressWarnings("deprecation")
    public void testNanosWithJodaTimeOfDay() {
        JodaLocalTimeHolder item = new JodaLocalTimeHolder();
        item.setId(1);
        item.setName("test_nanos1");
        item.setLocalTime(new LocalTime(10, 10, 10, 111));

        persist(item);

        TimeOfDayAsTimeJoda jodaItem = find(TimeOfDayAsTimeJoda.class, (long) 1);

        assertNotNull(jodaItem);
        assertEquals(1, jodaItem.getId());
        assertEquals("test_nanos1", jodaItem.getName());
        assertEquals(new org.joda.time.TimeOfDay(10, 10, 10), jodaItem.getTimeOfDay());


        item = find((long) 1);

        assertNotNull(item);
        assertEquals(1, item.getId());
        assertEquals("test_nanos1", item.getName());
        assertEquals(new LocalTime(10, 10, 10), item.getLocalTime());
    }
}
