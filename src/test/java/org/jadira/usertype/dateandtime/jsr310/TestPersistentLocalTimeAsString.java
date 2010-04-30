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
package org.jadira.usertype.dateandtime.jsr310;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Table;
import javax.time.calendar.LocalTime;
import javax.time.calendar.TimeAdjuster;


import org.jadira.usertype.dateandtime.jsr310.testmodel.LocalTimeAsStringHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.DatabaseCapable;
import org.jadira.usertype.dateandtime.shared.testmodel.jodatime.LocalTimeAsStringJoda;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestPersistentLocalTimeAsString extends DatabaseCapable {

    private static final LocalTime[] localTimes = new LocalTime[] { LocalTime.of(14, 2, 25), LocalTime.of(23, 59, 59, 999000000), LocalTime.of(0, 0, 0) };

    private static final org.joda.time.LocalTime[] jodaLocalTimes = new org.joda.time.LocalTime[] { new org.joda.time.LocalTime(14, 2, 25), new org.joda.time.LocalTime(23, 59, 59, 999), new org.joda.time.LocalTime(0, 0, 0) };
    
    private static final TimeAdjuster NORMALISE_NANOS = new NormaliseNanosAdjuster();
    
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

            LocalTimeAsStringHolder item = new LocalTimeAsStringHolder();
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

            LocalTimeAsStringHolder item = manager.find(LocalTimeAsStringHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localTimes[i], item.getLocalTime());
        }
        
        verifyDatabaseTable(manager, LocalTimeAsStringHolder.class.getAnnotation(Table.class).name());
        
        manager.close();
    }
    
    @Test
    public void testRoundtripWithJodaTime() {
        
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < localTimes.length; i++) {
            manager.remove(manager.find(LocalTimeAsStringHolder.class, Long.valueOf(i)));
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

            LocalTimeAsStringHolder item = manager.find(LocalTimeAsStringHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localTimes[i].with(NORMALISE_NANOS), item.getLocalTime());
        }
        manager.close();
    }
    
    @Test
    public void testNanosWithJodaTime() {
        
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < localTimes.length; i++) {
            manager.remove(manager.find(LocalTimeAsStringHolder.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();
        
        manager.getTransaction().begin();
        
        LocalTimeAsStringHolder item = new LocalTimeAsStringHolder();
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

        item = manager.find(LocalTimeAsStringHolder.class, Long.valueOf(1));
 
        assertNotNull(item);
        assertEquals(1, item.getId());
        assertEquals("test_nanos1", item.getName());
        assertEquals(LocalTime.of(10, 10, 10, 111444444), item.getLocalTime());

        manager.close();
    }
    
    private static final class NormaliseNanosAdjuster implements TimeAdjuster {

        public LocalTime adjustTime(LocalTime time) {
            if(time == null) { return null; }
            
            int millis = (int)(time.getNanoOfSecond() / 1000000);
            
            return LocalTime.of(time.getHourOfDay(), time.getMinuteOfHour(), time.getSecondOfMinute(), millis * 1000000);
        }
    }
}
