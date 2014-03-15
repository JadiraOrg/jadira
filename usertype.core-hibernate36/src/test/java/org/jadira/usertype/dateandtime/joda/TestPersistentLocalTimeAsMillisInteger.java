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

import org.jadira.usertype.dateandtime.joda.testmodel.JodaLocalTimeAsMillisIntegerHolder;
import org.jadira.usertype.dateandtime.joda.testmodel.LocalTimeExactJoda;
import org.jadira.usertype.dateandtime.joda.testmodel.TimeOfDayExactJoda;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.LocalTime;
import org.joda.time.TimeOfDay;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings("deprecation")
public class TestPersistentLocalTimeAsMillisInteger extends AbstractDatabaseTest<JodaLocalTimeAsMillisIntegerHolder> {

    private static final LocalTime[] localTimes = new LocalTime[]{new LocalTime(14, 2, 25), new LocalTime(23, 59, 59, 999 / 1000000), new LocalTime(0, 0, 0)};

    public TestPersistentLocalTimeAsMillisInteger() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < localTimes.length; i++) {
            JodaLocalTimeAsMillisIntegerHolder item = new JodaLocalTimeAsMillisIntegerHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalTime(localTimes[i]);

            persist(item);
        }

        for (int i = 0; i < localTimes.length; i++) {
            JodaLocalTimeAsMillisIntegerHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localTimes[i], item.getLocalTime());
        }

        verifyDatabaseTable();
    }

    @Test
    @Ignore // Joda Time Contrib does not support Hibernate 4 yet
    public void testRoundtripWithJodaTime() {
        for (int i = 0; i < localTimes.length; i++) {

            LocalTimeExactJoda item = new LocalTimeExactJoda();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalTime(localTimes[i]);

            persist(item);
        }


        for (int i = 0; i < localTimes.length; i++) {

            JodaLocalTimeAsMillisIntegerHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localTimes[i], item.getLocalTime());
        }
    }

    @Test
    @Ignore // Joda Time Contrib does not support Hibernate 4 yet
    public void testNanosWithJodaTime() {
        JodaLocalTimeAsMillisIntegerHolder item = new JodaLocalTimeAsMillisIntegerHolder();
        item.setId(1);
        item.setName("test_nanos1");
        item.setLocalTime(new LocalTime(10, 10, 10, 111));

        persist(item);

        LocalTimeExactJoda jodaItem = find(LocalTimeExactJoda.class, (long) 1);

        assertNotNull(jodaItem);
        assertEquals(1, jodaItem.getId());
        assertEquals("test_nanos1", jodaItem.getName());
        assertEquals(new LocalTime(10, 10, 10, 111), jodaItem.getLocalTime());

        item = find((long) 1);

        assertNotNull(item);
        assertEquals(1, item.getId());
        assertEquals("test_nanos1", item.getName());
        assertEquals(new LocalTime(10, 10, 10, 111), item.getLocalTime());
    }

    @Test
    @Ignore // TimeOfDayExact is probably defective in Joda Time
    public void testRoundtripWithJodaTimeOfDay() {
        for (int i = 0; i < localTimes.length; i++) {
            TimeOfDayExactJoda item = new TimeOfDayExactJoda();
            item.setId(i);
            item.setName("test_" + i);
            TimeOfDay next = new TimeOfDay(localTimes[i]);
            item.setTimeOfDay(next);

            persist(item);
        }


        for (int i = 0; i < localTimes.length; i++) {
            JodaLocalTimeAsMillisIntegerHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localTimes[i], item.getLocalTime());
        }
    }

    @Test
    @Ignore
    public void testNanosWithJodaTimeOfDay() {
        JodaLocalTimeAsMillisIntegerHolder item = new JodaLocalTimeAsMillisIntegerHolder();
        item.setId(1);
        item.setName("test_nanos1");
        item.setLocalTime(new LocalTime(10, 10, 10, 111));

        persist(item);


        TimeOfDayExactJoda jodaItem = find(TimeOfDayExactJoda.class, (long) 1);

        assertNotNull(jodaItem);
        assertEquals(1, jodaItem.getId());
        assertEquals("test_nanos1", jodaItem.getName());
        assertEquals(new TimeOfDay(10, 10, 10, 111), jodaItem.getTimeOfDay());


        item = find((long) 1);

        assertNotNull(item);
        assertEquals(1, item.getId());
        assertEquals("test_nanos1", item.getName());
        assertEquals(new LocalTime(10, 10, 10, 111), item.getLocalTime());
    }
}
