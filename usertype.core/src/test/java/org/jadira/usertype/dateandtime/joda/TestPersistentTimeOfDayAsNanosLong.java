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

import org.jadira.usertype.dateandtime.joda.testmodel.TimeOfDayAsNanosLongHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.TimeOfDay;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@SuppressWarnings("deprecation")
public class TestPersistentTimeOfDayAsNanosLong extends AbstractDatabaseTest<TimeOfDayAsNanosLongHolder> {

    private static final TimeOfDay[] localTimes = new TimeOfDay[]{new TimeOfDay(14, 2, 25), new TimeOfDay(23, 59, 59, 999), new TimeOfDay(0, 0, 0)};

    public TestPersistentTimeOfDayAsNanosLong() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < localTimes.length; i++) {
            TimeOfDayAsNanosLongHolder item = new TimeOfDayAsNanosLongHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalTime(localTimes[i]);

            persist(item);
        }

        for (int i = 0; i < localTimes.length; i++) {

            TimeOfDayAsNanosLongHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localTimes[i], item.getLocalTime());
        }

        verifyDatabaseTable();
    }
}
