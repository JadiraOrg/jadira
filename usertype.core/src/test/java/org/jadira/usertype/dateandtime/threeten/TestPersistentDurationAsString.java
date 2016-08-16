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
package org.jadira.usertype.dateandtime.threeten;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Duration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Table;

import org.jadira.usertype.dateandtime.joda.testmodel.DurationJoda;
import org.jadira.usertype.dateandtime.shared.dbunit.DatabaseCapable;
import org.jadira.usertype.dateandtime.threeten.testmodel.DurationAsStringJdk8;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TestPersistentDurationAsString extends DatabaseCapable {

    private static final Duration[] durations = new Duration[] {
        Duration.ZERO,
        Duration.ofSeconds(30)
    };

    private static final org.joda.time.Duration[] jodaDurations = new org.joda.time.Duration[] {
        org.joda.time.Duration.ZERO,
        new org.joda.time.Duration(30 * 1000),
        new org.joda.time.Period(0, 0, 30, 0).toDurationFrom(new org.joda.time.LocalDateTime(2010, 8, 8, 10, 10, 10).toDateTime(org.joda.time.DateTimeZone.UTC)),
        new org.joda.time.Duration(new org.joda.time.DateTime(
                2010, 4, 1, 0, 0, 0, org.joda.time.DateTimeZone.UTC).minusMonths(3), new org.joda.time.DateTime(2010, 4, 1, 0, 0, 0, org.joda.time.DateTimeZone.UTC)) };

    private static EntityManagerFactory factory;

    @BeforeClass
    public static void setup() {
        factory = Persistence.createEntityManagerFactory("test1");
    }

    @AfterClass
    public static void tearDown() {
        factory.close();
    }

    @Test
    public void testPersist() {

        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();

        for (int i = 0; i < durations.length; i++) {

            DurationAsStringJdk8 item = new DurationAsStringJdk8();
            item.setId(i);
            item.setName("test_" + i);
            item.setDuration(durations[i]);

            manager.persist(item);
        }

        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        for (int i = 0; i < durations.length; i++) {

            DurationAsStringJdk8 item = manager.find(DurationAsStringJdk8.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(durations[i], item.getDuration());
        }

        verifyDatabaseTable(manager, DurationAsStringJdk8.class.getAnnotation(Table.class).name());

        manager.close();
    }

    @Test @Ignore // Joda Time Contrib does not support Hibernate 4 yet
    public void testRoundtripWithJodaTime() {

        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < durations.length; i++) {
            manager.remove(manager.find(DurationAsStringJdk8.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();

        manager.getTransaction().begin();

        for (int i = 0; i < durations.length; i++) {

            DurationJoda item = new DurationJoda();
            item.setId(i);
            item.setName("test_" + i);
            item.setDuration(jodaDurations[i]);

            manager.persist(item);
        }

        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        for (int i = 0; i < durations.length; i++) {

            DurationAsStringJdk8 item = manager.find(DurationAsStringJdk8.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(durations[i], item.getDuration());
        }
        manager.close();

        manager = factory.createEntityManager();

        for (int i = 0; i < durations.length; i++) {

            DurationJoda item = manager.find(DurationJoda.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(jodaDurations[i], item.getDuration());
        }
        manager.close();
    }

    @Test @Ignore // Joda Time Contrib does not support Hibernate 4 yet
    public void testNanosWithJodaTime() {

        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < durations.length; i++) {
            manager.remove(manager.find(DurationAsStringJdk8.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();

        manager.getTransaction().begin();

        DurationAsStringJdk8 item = new DurationAsStringJdk8();
        item.setId(1);
        item.setName("test_nanos1");
        item.setDuration(Duration.ofNanos(999));

        manager.persist(item);
        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        DurationJoda jodaItem = manager.find(DurationJoda.class, Long.valueOf(1));

        assertNotNull(jodaItem);
        assertEquals(1, jodaItem.getId());
        assertEquals("test_nanos1", jodaItem.getName());
        assertEquals(new org.joda.time.Duration(0), jodaItem.getDuration());

        manager.close();

        manager = factory.createEntityManager();

        item = manager.find(DurationAsStringJdk8.class, Long.valueOf(1));

        assertNotNull(item);
        assertEquals(1, item.getId());
        assertEquals("test_nanos1", item.getName());
        assertEquals(Duration.ofNanos(999), item.getDuration());

        manager.close();
    }
}
