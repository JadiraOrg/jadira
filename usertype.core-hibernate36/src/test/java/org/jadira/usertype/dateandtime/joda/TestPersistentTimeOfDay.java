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

import org.jadira.usertype.dateandtime.joda.testmodel.TimeOfDayAsTimeJoda;
import org.jadira.usertype.dateandtime.joda.testmodel.TimeOfDayHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.TimeOfDay;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings("deprecation")
public class TestPersistentTimeOfDay extends AbstractDatabaseTest<TimeOfDayHolder> {

    private static final TimeOfDay[] localTimes = new TimeOfDay[]{new TimeOfDay(14, 2, 25), new TimeOfDay(23, 59, 59), new TimeOfDay(0, 0, 0)};

    private static final TimeOfDay[] jodaTimeOfDays = new TimeOfDay[]{new TimeOfDay(14, 2, 25), new TimeOfDay(23, 59, 59), new TimeOfDay(0, 0, 0)};

    public TestPersistentTimeOfDay() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < localTimes.length; i++) {
            TimeOfDayHolder item = new TimeOfDayHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalTime(localTimes[i]);

            persist(item);
        }


        for (int i = 0; i < localTimes.length; i++) {
            TimeOfDayHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localTimes[i], item.getLocalTime());
        }

        verifyDatabaseTable();
    }

    @Test
    @Ignore
    public void testRoundtripWithJodaTime() {
        for (int i = 0; i < localTimes.length; i++) {
            TimeOfDayAsTimeJoda item = new TimeOfDayAsTimeJoda();
            item.setId(i);
            item.setName("test_" + i);
            item.setTimeOfDay(jodaTimeOfDays[i]);

            persist(item);
        }

        for (int i = 0; i < localTimes.length; i++) {

            TimeOfDayHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localTimes[i], item.getLocalTime());
        }
    }

    @Test
    @Ignore // This test will fail because Joda Time does not support nanosecond precision
    public void testNanosWithJodaTime() {
        TimeOfDayHolder item = new TimeOfDayHolder();
        item.setId(1);
        item.setName("test_nanos1");
        item.setLocalTime(new TimeOfDay(10, 10, 10, 111444444));

        persist(item);

        TimeOfDayAsTimeJoda jodaItem = find(TimeOfDayAsTimeJoda.class, (long) 1);

        assertNotNull(jodaItem);
        assertEquals(1, jodaItem.getId());
        assertEquals("test_nanos1", jodaItem.getName());
        assertEquals(new TimeOfDay(10, 10, 10, 0), jodaItem.getTimeOfDay());


        item = find(TimeOfDayHolder.class, Long.valueOf(1));

        assertNotNull(item);
        assertEquals(1, item.getId());
        assertEquals("test_nanos1", item.getName());
        assertEquals(new TimeOfDay(10, 10, 10, 111444444), item.getLocalTime());
    }
}
