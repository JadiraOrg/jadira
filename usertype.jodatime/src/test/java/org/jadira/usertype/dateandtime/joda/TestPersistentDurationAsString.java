/*
 *  Copyright 2010 Christopher Pheby
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
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Table;

import org.jadira.usertype.dateandtime.joda.testmodel.DurationJoda;
import org.jadira.usertype.dateandtime.joda.testmodel.JodaDurationAsStringHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.DatabaseCapable;
import org.joda.time.DateMidnight;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestPersistentDurationAsString extends DatabaseCapable {

    private static final Duration[] durations = new Duration[] { 
        Duration.ZERO, 
        new Duration(30 * 1000), 
        new Period(0, 0, 30, 0).toDurationFrom(new LocalDateTime(2010, 8, 8, 10, 10, 10).toDateTime(DateTimeZone.UTC)), 
        new Duration(new DateMidnight(2010, 4, 1, DateTimeZone.UTC).minusMonths(3), new DateMidnight(2010, 4, 1, DateTimeZone.UTC)) };

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

            JodaDurationAsStringHolder item = new JodaDurationAsStringHolder();
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
            
            JodaDurationAsStringHolder item = manager.find(JodaDurationAsStringHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(durations[i], item.getDuration());
        }

        verifyDatabaseTable(manager, JodaDurationAsStringHolder.class.getAnnotation(Table.class).name());
        
        manager.close();
    }
    
    @Test
    public void testRoundtripWithJodaTime() {
        
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < durations.length; i++) {
            manager.remove(manager.find(JodaDurationAsStringHolder.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();
        
        manager.getTransaction().begin();
        
        for (int i = 0; i < durations.length; i++) {

            DurationJoda item = new DurationJoda();
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

            JodaDurationAsStringHolder item = manager.find(JodaDurationAsStringHolder.class, Long.valueOf(i));

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
            assertEquals(durations[i], item.getDuration());
        }
        manager.close();
    }    
}
