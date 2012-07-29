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

import org.hamcrest.core.IsEqual;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.jadira.usertype.dateandtime.joda.testmodel.JodaDateTimeWithZoneHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.junit.Ignore;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.util.List;

import static org.junit.Assert.*;

public class TestPersistentDateTimeWithZone extends AbstractDatabaseTest<JodaDateTimeWithZoneHolder> {

    private static final DateTime[] dateTimes = new DateTime[]{
            new DateTime(2004, 2, 25, 12, 11, 10, 0, DateTimeZone.forOffsetHours(4)).withZone(DateTimeZone.UTC),
            new DateTime(1980, 3, 11, 13, 12, 11, 500, DateTimeZone.UTC),
            null};

    public TestPersistentDateTimeWithZone() {
        super(JodaDateTimeWithZoneHolder.class);
    }

    @Test
    public void testPersist() {

        for (int i = 0; i < dateTimes.length; i++) {

            JodaDateTimeWithZoneHolder item = new JodaDateTimeWithZoneHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setDateTime(dateTimes[i]);

            persist(item);
        }


        for (int i = 0; i < dateTimes.length; i++) {

            JodaDateTimeWithZoneHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            if (dateTimes[i] == null) {
                assertNull(item.getDateTime());
            } else {
                assertEquals(dateTimes[i].toString(), item.getDateTime().toString());
            }
        }

        verifyDatabaseTable();
    }

    @Test
    @Ignore
    public void testReadWithCriteria() {

        for (int i = 0; i < dateTimes.length; i++) {

            JodaDateTimeWithZoneHolder item = new JodaDateTimeWithZoneHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setDateTime(dateTimes[i]);

            persist(item);
        }

        EntityManager manager = factory.createEntityManager();

        Criteria criteria = ((Session) (manager.getDelegate())).createCriteria(JodaDateTimeWithZoneHolder.class);
        criteria.setCacheable(true);
        criteria.add(Restrictions.le("dateTime.datetime", new DateTime()));
        @SuppressWarnings({"unused", "unchecked"})
        List<JodaDateTimeWithZoneHolder> result = (List<JodaDateTimeWithZoneHolder>) criteria.list();

        manager.close();

        manager = factory.createEntityManager();

        // Ensure use of criteria does not throw exception
        criteria = ((Session) (manager.getDelegate())).createCriteria(JodaDateTimeWithZoneHolder.class);
        criteria.setCacheable(true);
        criteria.add(Restrictions.le("dateTime.datetime", new LocalDateTime()));
        @SuppressWarnings({"unused", "unchecked"})
        List<JodaDateTimeWithZoneHolder> result2 = (List<JodaDateTimeWithZoneHolder>) criteria.list();

        manager.close();
    }

    @Test // Added to investigate http://sourceforge.net/mailarchive/message.php?msg_id=29056453
    public void testDSTSummerToWinter() {

        DateTimeZone tz = DateTimeZone.forID("Europe/Berlin");
        assertFalse(tz.isFixed());
        DateTime dt = new DateTime(2010, 10, 31, 1, 0, 0, tz);

        for (int i = 0; i < 10; i++) {
            System.out.println("Saving: " + dt);

            JodaDateTimeWithZoneHolder item = new JodaDateTimeWithZoneHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setDateTime(dt);

            persist(item);

            JodaDateTimeWithZoneHolder readItem = find((long) i);

            System.out.println("ReadItem: " + readItem.getDateTime());

            assertThat("For record {" + i + "}", dt, IsEqual.equalTo(readItem.getDateTime()));

            dt = dt.plusHours(1);
        }
    }

    @Test
    public void testDSTWinterToSummer() {

        DateTimeZone tz = DateTimeZone.forID("Europe/Berlin");
        assertFalse(tz.isFixed());
        DateTime dt = new DateTime(2010, 3, 28, 1, 0, 0, tz);

        for (int i = 0; i < 5; i++) {

            System.out.println("Saving: " + dt);

            JodaDateTimeWithZoneHolder item = new JodaDateTimeWithZoneHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setDateTime(dt);

            persist(item);

            JodaDateTimeWithZoneHolder readItem = find((long) i);

            System.out.println("ReadItem: " + readItem.getDateTime());

            assertThat("For record {" + i + "}", dt, IsEqual.equalTo(readItem.getDateTime()));

            dt = dt.plusHours(1);
        }
    }
}
