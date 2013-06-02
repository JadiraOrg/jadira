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

import org.jadira.usertype.dateandtime.joda.testmodel.JodaLocalDateTimeHolder;
import org.jadira.usertype.dateandtime.joda.testmodel.LocalDateTimeJoda;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.LocalDateTime;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestPersistentLocalDateTime extends AbstractDatabaseTest<JodaLocalDateTimeHolder> {

    private static final LocalDateTime[] localDateTimes = new LocalDateTime[]{new LocalDateTime(2004, 2, 25, 12, 11, 10), new LocalDateTime(1980, 3, 11, 13, 12, 11)};
    private static final LocalDateTime[] jodaLocalDateTimes = new LocalDateTime[]{new LocalDateTime(2004, 2, 25, 12, 11, 10), new LocalDateTime(1980, 3, 11, 13, 12, 11)};

    public TestPersistentLocalDateTime() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < localDateTimes.length; i++) {
            JodaLocalDateTimeHolder item = new JodaLocalDateTimeHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalDateTime(localDateTimes[i]);

            persist(item);
        }

        for (int i = 0; i < localDateTimes.length; i++) {
            JodaLocalDateTimeHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localDateTimes[i], item.getLocalDateTime());
        }

        verifyDatabaseTable();
    }

    @Test
    @Ignore // Joda Time Contrib does not support Hibernate 4 yet
    public void testRoundtripWithJodaTime() {
        for (int i = 0; i < localDateTimes.length; i++) {

            LocalDateTimeJoda item = new LocalDateTimeJoda();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalDateTime(jodaLocalDateTimes[i]);

            persist(item);
        }

        for (int i = 0; i < localDateTimes.length; i++) {

            JodaLocalDateTimeHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localDateTimes[i], item.getLocalDateTime());
        }
    }

    @Test
    @Ignore // Nanos are not properly supported by JodaTime type
    public void testNanosWithJodaTime() {
        JodaLocalDateTimeHolder item = new JodaLocalDateTimeHolder();
        item.setId(1);
        item.setName("test_nanos1");
        item.setLocalDateTime(new LocalDateTime(2010, 8, 1, 10, 10, 10, 111444444));

        persist(item);


        JodaLocalDateTimeHolder jodaItem = find((long) 1);

        assertNotNull(jodaItem);
        assertEquals(1, jodaItem.getId());
        assertEquals("test_nanos1", jodaItem.getName());
        assertEquals(new org.joda.time.LocalDateTime(2010, 8, 1, 10, 10, 10, 111), jodaItem.getLocalDateTime());


        item = find((long) 1);

        assertNotNull(item);
        assertEquals(1, item.getId());
        assertEquals("test_nanos1", item.getName());
    }
}
