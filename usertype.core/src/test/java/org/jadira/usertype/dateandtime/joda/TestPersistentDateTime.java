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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.hamcrest.core.IsEqual;
import org.jadira.usertype.dateandtime.joda.testmodel.DateTimeJoda;
import org.jadira.usertype.dateandtime.joda.testmodel.JodaDateTimeHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Ignore;
import org.junit.Test;

public class TestPersistentDateTime extends AbstractDatabaseTest<JodaDateTimeHolder> {

    private static final DateTime[] dateTimes = new DateTime[]{
            new DateTime(2004, 2, 25, 12, 11, 10, 0, DateTimeZone.forOffsetHours(4)),
            new DateTime(1980, 3, 11, 13, 12, 11, 500, DateTimeZone.UTC),
            null};
    private static final DateTime[] jodaDateTimes = new DateTime[]{
            new DateTime(2004, 2, 25, 12, 11, 10, 0, DateTimeZone.forOffsetHours(4)),
            new DateTime(1980, 3, 11, 13, 12, 11, 500, DateTimeZone.UTC),
            null};

    public TestPersistentDateTime() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
    @Test
    public void testPersist() {

        for (int i = 0; i < dateTimes.length; i++) {

            JodaDateTimeHolder item = new JodaDateTimeHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setDateTime(dateTimes[i]);

            persist(item);
        }

        for (int i = 0; i < dateTimes.length; i++) {

            JodaDateTimeHolder item = find(JodaDateTimeHolder.class, i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            if (dateTimes[i] == null) {
                assertNull(item.getDateTime());
            } else {
                assertEquals(dateTimes[i].withZone(DateTimeZone.UTC).toString(), item.getDateTime().toString());
            }
        }

        verifyDatabaseTable();
    }

    @Test
    @Ignore // Joda Time offsets the value on reading it back
    public void testRoundtripWithJodaTime() {

        for (int i = 0; i < dateTimes.length; i++) {
            DateTimeJoda item = new DateTimeJoda();
            item.setId(i);
            item.setName("test_" + i);
            item.setDateTime(jodaDateTimes[i]);

            persist(item);
        }

        for (int i = 0; i < dateTimes.length; i++) {
            JodaDateTimeHolder item = find(JodaDateTimeHolder.class, i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(dateTimes[i].withZone(DateTimeZone.UTC).toString(), item.getDateTime().toString());
        }
    }

    @Test // Added to investigate http://sourceforge.net/mailarchive/message.php?msg_id=29056453
    public void testDSTSummerToWinter() {

        DateTimeZone tz = DateTimeZone.forID("Europe/Berlin");
        assertFalse(tz.isFixed());
        DateTime dt = new DateTime(2010, 10, 31, 1, 0, 0, tz);

        for (int i = 0; i < 10; i++) {
            System.out.println("Saving: " + dt);

            JodaDateTimeHolder item = new JodaDateTimeHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setDateTime(dt);

            persist(item);

            JodaDateTimeHolder readItem = find(JodaDateTimeHolder.class, i);

            System.out.println("ReadItem: " + readItem.getDateTime());

            assertThat("For record {" + i + "}", dt.getMillis(), IsEqual.equalTo(readItem.getDateTime().getMillis()));

            dt = dt.plusHours(1);
        }
    }
    
    @Test
    public void testDSTWinterToSummer() {

        DateTimeZone tz = DateTimeZone.forID("Europe/Berlin");
        assertFalse(tz.isFixed());
        DateTime dt = new DateTime(2010, 3, 28, 1, 0, 0, tz);

        for (int i = 0; i < 5; i++) {

            System.out.println("Saving: " + dt + " [UTC: " + dt.withZone(DateTimeZone.UTC) + "]");

            JodaDateTimeHolder item = new JodaDateTimeHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setDateTime(dt);

            persist(item);

            JodaDateTimeHolder readItem = find(JodaDateTimeHolder.class, i);

            System.out.println("ReadItem: " + readItem.getDateTime());

            assertThat("For record {" + i + "}", readItem.getDateTime(), IsEqual.equalTo(dt.withZone(DateTimeZone.UTC)));

            dt = dt.plusHours(1);
        }
    }
}
