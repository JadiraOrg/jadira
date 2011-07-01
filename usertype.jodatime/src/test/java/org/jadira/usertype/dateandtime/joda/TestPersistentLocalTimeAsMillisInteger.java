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
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Table;

import org.jadira.usertype.dateandtime.joda.testmodel.JodaLocalTimeAsMillisIntegerHolder;
import org.jadira.usertype.dateandtime.joda.testmodel.LocalTimeExactJoda;
import org.jadira.usertype.dateandtime.joda.testmodel.TimeOfDayExactJoda;
import org.jadira.usertype.dateandtime.shared.dbunit.DatabaseCapable;
import org.joda.time.LocalTime;
import org.joda.time.TimeOfDay;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class TestPersistentLocalTimeAsMillisInteger extends DatabaseCapable {

    private static final LocalTime[] localTimes = new LocalTime[] { new LocalTime(14, 2, 25), new LocalTime(23, 59, 59, 999 / 1000000), new LocalTime(0, 0, 0) };
    
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

            JodaLocalTimeAsMillisIntegerHolder item = new JodaLocalTimeAsMillisIntegerHolder();
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

            JodaLocalTimeAsMillisIntegerHolder item = manager.find(JodaLocalTimeAsMillisIntegerHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localTimes[i], item.getLocalTime());
        }
        
        verifyDatabaseTable(manager, JodaLocalTimeAsMillisIntegerHolder.class.getAnnotation(Table.class).name());
        
        manager.close();
    }
    
    @Test
    public void testRoundtripWithJodaTime() {
        
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < localTimes.length; i++) {
            manager.remove(manager.find(JodaLocalTimeAsMillisIntegerHolder.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();
        
        manager.getTransaction().begin();
        
        for (int i = 0; i < localTimes.length; i++) {

            LocalTimeExactJoda item = new LocalTimeExactJoda();
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

            JodaLocalTimeAsMillisIntegerHolder item = manager.find(JodaLocalTimeAsMillisIntegerHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localTimes[i], item.getLocalTime());
        }
        manager.close();
    }
    
    @Test
    public void testNanosWithJodaTime() {
        
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < localTimes.length; i++) {
            manager.remove(manager.find(JodaLocalTimeAsMillisIntegerHolder.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();
        
        manager.getTransaction().begin();
        
        JodaLocalTimeAsMillisIntegerHolder item = new JodaLocalTimeAsMillisIntegerHolder();
        item.setId(1);
        item.setName("test_nanos1");
        item.setLocalTime(new LocalTime(10, 10, 10, 111));

        manager.persist(item);
        manager.flush();
        
        manager.getTransaction().commit();
        
        manager.close();

        manager = factory.createEntityManager();
        
        LocalTimeExactJoda jodaItem = manager.find(LocalTimeExactJoda.class, Long.valueOf(1));

        assertNotNull(jodaItem);
        assertEquals(1, jodaItem.getId());
        assertEquals("test_nanos1", jodaItem.getName());
        assertEquals(new LocalTime(10, 10, 10, 111), jodaItem.getLocalTime());

        manager.close();
        
        manager = factory.createEntityManager();

        item = manager.find(JodaLocalTimeAsMillisIntegerHolder.class, Long.valueOf(1));
 
        assertNotNull(item);
        assertEquals(1, item.getId());
        assertEquals("test_nanos1", item.getName());
        assertEquals(new LocalTime(10, 10, 10, 111), item.getLocalTime());

        manager.close();
    }
    
    @Test
    @Ignore // TimeOfDayExact is probably defective in Joda Time
    public void testRoundtripWithJodaTimeOfDay() {
        
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < localTimes.length; i++) {
            JodaLocalTimeAsMillisIntegerHolder entity = manager.find(JodaLocalTimeAsMillisIntegerHolder.class, Long.valueOf(i));
            if (entity != null) {
                manager.remove(entity);
            }
        }
        manager.flush();
        manager.getTransaction().commit();
        
        manager.getTransaction().begin();
        
        for (int i = 0; i < localTimes.length; i++) {

            TimeOfDayExactJoda item = new TimeOfDayExactJoda();
            item.setId(i);
            item.setName("test_" + i);
            TimeOfDay next = new TimeOfDay(localTimes[i]);
            item.setTimeOfDay(next);

            manager.persist(item);
        }

        manager.flush();
        
        manager.getTransaction().commit();
        
        manager.close();

        manager = factory.createEntityManager();
        
        for (int i = 0; i < localTimes.length; i++) {

            JodaLocalTimeAsMillisIntegerHolder item = manager.find(JodaLocalTimeAsMillisIntegerHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localTimes[i], item.getLocalTime());
        }
        manager.close();
    }
    
    @Test
    @Ignore
    public void testNanosWithJodaTimeOfDay() {
        
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < localTimes.length; i++) {
            JodaLocalTimeAsMillisIntegerHolder entity = manager.find(JodaLocalTimeAsMillisIntegerHolder.class, Long.valueOf(i));
            if (entity != null) {
                manager.remove(entity);
            }
        }
        manager.flush();
        manager.getTransaction().commit();
        
        manager.getTransaction().begin();
        
        JodaLocalTimeAsMillisIntegerHolder item = new JodaLocalTimeAsMillisIntegerHolder();
        item.setId(1);
        item.setName("test_nanos1");
        item.setLocalTime(new LocalTime(10, 10, 10, 111));

        manager.persist(item);
        manager.flush();
        
        manager.getTransaction().commit();
        
        manager.close();

        manager = factory.createEntityManager();
        
        TimeOfDayExactJoda jodaItem = manager.find(TimeOfDayExactJoda.class, Long.valueOf(1));

        assertNotNull(jodaItem);
        assertEquals(1, jodaItem.getId());
        assertEquals("test_nanos1", jodaItem.getName());
        assertEquals(new TimeOfDay(10, 10, 10, 111), jodaItem.getTimeOfDay());

        manager.close();
        
        manager = factory.createEntityManager();

        item = manager.find(JodaLocalTimeAsMillisIntegerHolder.class, Long.valueOf(1));
 
        assertNotNull(item);
        assertEquals(1, item.getId());
        assertEquals("test_nanos1", item.getName());
        assertEquals(new LocalTime(10, 10, 10, 111), item.getLocalTime());

        manager.close();
    }
}
