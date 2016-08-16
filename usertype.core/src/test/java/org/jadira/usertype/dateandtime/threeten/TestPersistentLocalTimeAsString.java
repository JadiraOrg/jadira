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

import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Table;

import org.jadira.usertype.dateandtime.joda.testmodel.LocalTimeAsStringJoda;
import org.jadira.usertype.dateandtime.shared.dbunit.DatabaseCapable;
import org.jadira.usertype.dateandtime.threeten.testmodel.LocalTimeAsStringJdk8;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TestPersistentLocalTimeAsString extends DatabaseCapable {

    private static final LocalTime[] localTimes = new LocalTime[] { LocalTime.of(14, 2, 25), LocalTime.of(23, 59, 59, 999000000), LocalTime.of(0, 0, 0) };

    private static final org.joda.time.LocalTime[] jodaLocalTimes = new org.joda.time.LocalTime[] {
        new org.joda.time.LocalTime(14, 2, 25),
        new org.joda.time.LocalTime(23, 59, 59, 999),
        new org.joda.time.LocalTime(0, 0, 0) };

    private static final TemporalAdjuster NORMALISE_NANOS = new NormaliseNanosAdjuster();

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

        for (int i = 0; i < localTimes.length; i++) {

            LocalTimeAsStringJdk8 item = new LocalTimeAsStringJdk8();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalTime(localTimes[i]);

            manager.persist(item);
        }

        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        for (int i = 0; i < localTimes.length; i++) {

            LocalTimeAsStringJdk8 item = manager.find(LocalTimeAsStringJdk8.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localTimes[i], item.getLocalTime());
        }

        verifyDatabaseTable(manager, LocalTimeAsStringJdk8.class.getAnnotation(Table.class).name());

        manager.close();
    }

    @Test @Ignore // Joda Time Contrib does not support Hibernate 4 yet
    public void testRoundtripWithJodaTime() {

        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < localTimes.length; i++) {
            manager.remove(manager.find(LocalTimeAsStringJdk8.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();

        manager.getTransaction().begin();

        for (int i = 0; i < localTimes.length; i++) {

            LocalTimeAsStringJoda item = new LocalTimeAsStringJoda();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalTime(jodaLocalTimes[i]);

            manager.persist(item);
        }

        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        for (int i = 0; i < localTimes.length; i++) {

            LocalTimeAsStringJdk8 item = manager.find(LocalTimeAsStringJdk8.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localTimes[i].with(NORMALISE_NANOS), item.getLocalTime());
        }
        manager.close();
    }

    @Test @Ignore // Joda Time Contrib does not support Hibernate 4 yet
    public void testNanosWithJodaTime() {

        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < localTimes.length; i++) {
            manager.remove(manager.find(LocalTimeAsStringJdk8.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();

        manager.getTransaction().begin();

        LocalTimeAsStringJdk8 item = new LocalTimeAsStringJdk8();
        item.setId(1);
        item.setName("test_nanos1");
        item.setLocalTime(LocalTime.of(10, 10, 10, 111444444));

        manager.persist(item);
        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        LocalTimeAsStringJoda jodaItem = manager.find(LocalTimeAsStringJoda.class, Long.valueOf(1));

        assertNotNull(jodaItem);
        assertEquals(1, jodaItem.getId());
        assertEquals("test_nanos1", jodaItem.getName());
        assertEquals(new org.joda.time.LocalTime(10, 10, 10, 111), jodaItem.getLocalTime());

        manager.close();

        manager = factory.createEntityManager();

        item = manager.find(LocalTimeAsStringJdk8.class, Long.valueOf(1));

        assertNotNull(item);
        assertEquals(1, item.getId());
        assertEquals("test_nanos1", item.getName());
        assertEquals(LocalTime.of(10, 10, 10, 111444444), item.getLocalTime());

        manager.close();
    }

    private static final class NormaliseNanosAdjuster implements TemporalAdjuster {

        public LocalTime adjustInto(Temporal time) {
            if (time == null) { return null; }

            int millis = (int) (time.get(ChronoField.NANO_OF_SECOND) / 1000000);

            return LocalTime.of(time.get(ChronoField.HOUR_OF_DAY), time.get(ChronoField.MINUTE_OF_HOUR), time.get(ChronoField.SECOND_OF_MINUTE), millis * 1000000);
        }
    }
}
