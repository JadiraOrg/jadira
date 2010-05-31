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

import org.jadira.usertype.dateandtime.joda.testmodel.TimeOfDayAsTimeJoda;
import org.jadira.usertype.dateandtime.joda.testmodel.TimeOfDayHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.DatabaseCapable;
import org.joda.time.TimeOfDay;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class TestPersistentTimeOfDay extends DatabaseCapable {

    private static final TimeOfDay[] localTimes = new TimeOfDay[] { new TimeOfDay(14, 2, 25), new TimeOfDay(23, 59, 59), new TimeOfDay(0, 0, 0) };

    private static final TimeOfDay[] jodaTimeOfDays = new TimeOfDay[] { new TimeOfDay(14, 2, 25), new TimeOfDay(23, 59, 59), new TimeOfDay(0, 0, 0) };

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

            TimeOfDayHolder item = new TimeOfDayHolder();
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

            TimeOfDayHolder item = manager.find(TimeOfDayHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localTimes[i], item.getLocalTime());
        }
        
        verifyDatabaseTable(manager, TimeOfDayHolder.class.getAnnotation(Table.class).name());
        
        manager.close();
    }
    
    @Test @Ignore
    public void testRoundtripWithJodaTime() {
        
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < localTimes.length; i++) {
            manager.remove(manager.find(TimeOfDayHolder.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();
        
        manager.getTransaction().begin();
        
        for (int i = 0; i < localTimes.length; i++) {

            TimeOfDayAsTimeJoda item = new TimeOfDayAsTimeJoda();
            item.setId(i);
            item.setName("test_" + i);
            item.setTimeOfDay(jodaTimeOfDays[i]);

            manager.persist(item);
        }

        manager.flush();
        
        manager.getTransaction().commit();
        
        manager.close();

        manager = factory.createEntityManager();
        
        for (int i = 0; i < localTimes.length; i++) {

            TimeOfDayHolder item = manager.find(TimeOfDayHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localTimes[i], item.getLocalTime());
        }
        manager.close();
    }
    
    @Test
    @Ignore // This test will fail because Joda Time does not support nanosecond precision
    public void testNanosWithJodaTime() {
        
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < localTimes.length; i++) {
            manager.remove(manager.find(TimeOfDayHolder.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();
        
        manager.getTransaction().begin();
        
        TimeOfDayHolder item = new TimeOfDayHolder();
        item.setId(1);
        item.setName("test_nanos1");
        item.setLocalTime(new TimeOfDay(10, 10, 10, 111444444));

        manager.persist(item);
        manager.flush();
        
        manager.getTransaction().commit();
        
        manager.close();

        manager = factory.createEntityManager();
        
        TimeOfDayAsTimeJoda jodaItem = manager.find(TimeOfDayAsTimeJoda.class, Long.valueOf(1));

        assertNotNull(jodaItem);
        assertEquals(1, jodaItem.getId());
        assertEquals("test_nanos1", jodaItem.getName());
        assertEquals(new TimeOfDay(10, 10, 10, 0), jodaItem.getTimeOfDay());

        manager.close();
        
        manager = factory.createEntityManager();

        item = manager.find(TimeOfDayHolder.class, Long.valueOf(1));
 
        assertNotNull(item);
        assertEquals(1, item.getId());
        assertEquals("test_nanos1", item.getName());
        assertEquals(new TimeOfDay(10, 10, 10, 111444444), item.getLocalTime());

        manager.close();
    }
}
