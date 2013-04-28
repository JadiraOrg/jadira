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

import java.io.IOException;
import java.sql.SQLException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Table;

import org.jadira.usertype.dateandtime.shared.dbunit.DatabaseCapable;
import org.jadira.usertype.dateandtime.threetenbp.testmodel.PeriodAsStringHolder;
import org.jadira.usertype.dateandtime.threetenbp.testmodel.PeriodJoda;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.threeten.bp.Period;

public class TestPersistentPeriodAsString extends DatabaseCapable {

    private static final Period[] periods = new Period[] {
        Period.of(0, 3, 0),
        Period.of(4, 35, 40),
        Period.of(28, 10, 2),
        Period.of(28, 10, 0)
    };

    private static final org.joda.time.Period[] jodaPeriods = new org.joda.time.Period[] {
        org.joda.time.Period.months(3),
        new org.joda.time.Period(4, 35, 0, 0, 0, 0, 0, 0),
        new org.joda.time.Period(28, 10, 2, 0, 0, 0, 0, 0),
        new org.joda.time.Period(28, 10, 0, 0, 0, 0, 0, 0)
    };

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
    public void testPersist() throws SQLException, IOException {

        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();

        for (int i = 0; i < periods.length; i++) {
            PeriodAsStringHolder item = new PeriodAsStringHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setPeriod(periods[i]);
            manager.persist(item);
        }

        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        for (int i = 0; i < periods.length; i++) {

            PeriodAsStringHolder item = manager.find(PeriodAsStringHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(periods[i], item.getPeriod());
        }

        verifyDatabaseTable(manager, PeriodAsStringHolder.class.getAnnotation(Table.class).name());

        manager.close();
    }

    @Test @Ignore // Joda Time Contrib does not support Hibernate 4 yet
    public void testRoundtripWithJodaTime() {

        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < periods.length; i++) {
            manager.remove(manager.find(PeriodAsStringHolder.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();

        manager.getTransaction().begin();

        for (int i = 0; i < periods.length; i++) {

            PeriodJoda item = new PeriodJoda();
            item.setId(i);
            item.setName("test_" + i);
            item.setPeriod(jodaPeriods[i]);

            manager.persist(item);
        }

        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        for (int i = 0; i < periods.length; i++) {

            PeriodAsStringHolder item = manager.find(PeriodAsStringHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());

            Period expected = periods[i];

            assertEquals(expected, item.getPeriod());
        }
        manager.close();
    }
}
