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
package org.jadira.usertype.dateandtime.threetenbp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Table;

import org.jadira.usertype.dateandtime.shared.dbunit.DatabaseCapable;
import org.jadira.usertype.dateandtime.threetenbp.testmodel.OffsetTimeAsTimeAndStringOffsetHolder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.threeten.bp.LocalTime;
import org.threeten.bp.OffsetTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.temporal.ChronoField;
import org.threeten.bp.temporal.Temporal;
import org.threeten.bp.temporal.TemporalAdjuster;

public class TestPersistentOffsetTimeAsTimeAndStringOffset extends DatabaseCapable {

    private static final OffsetTime[] offsetTimes = new OffsetTime[] { OffsetTime.of(LocalTime.of(12, 10, 31), ZoneOffset.UTC), OffsetTime.of(LocalTime.of(23, 7, 43, 120), ZoneOffset.ofHours(2)) };

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

        for (int i = 0; i < offsetTimes.length; i++) {

            OffsetTimeAsTimeAndStringOffsetHolder item = new OffsetTimeAsTimeAndStringOffsetHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setOffsetTime(offsetTimes[i]);

            manager.persist(item);
        }

        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        for (int i = 0; i < offsetTimes.length; i++) {

            OffsetTimeAsTimeAndStringOffsetHolder item = manager.find(OffsetTimeAsTimeAndStringOffsetHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(offsetTimes[i].with(NORMALISE_NANOS), item.getOffsetTime());
        }

        verifyDatabaseTable(manager, OffsetTimeAsTimeAndStringOffsetHolder.class.getAnnotation(Table.class).name());

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
