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
import static org.junit.Assert.assertNull;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Table;

import org.jadira.usertype.dateandtime.joda.testmodel.IntervalJoda;
import org.jadira.usertype.dateandtime.joda.testmodel.JodaIntervalHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.DatabaseCapable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TestPersistentInterval extends DatabaseCapable {

    private static final Interval[] intervals     = new Interval[] { new Interval(new DateTime("1970-01-01T00:00:00.000", DateTimeZone.UTC).getMillis(), new DateTime("1970-01-12T17:46:40.000", DateTimeZone.UTC).getMillis(), DateTimeZone.forOffsetHours(4)), new Interval(1000000000, 2000000000, DateTimeZone.UTC), null };
    private static final Interval[] jodaIntervals = new Interval[] { new Interval(new DateTime("1970-01-01T00:00:00.000", DateTimeZone.UTC).getMillis(), new DateTime("1970-01-12T17:46:40.000", DateTimeZone.UTC).getMillis(), DateTimeZone.forOffsetHours(4)), new Interval(1000000000, 2000000000, DateTimeZone.UTC), null };

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

        for (int i = 0; i < intervals.length; i++) {

            JodaIntervalHolder item = new JodaIntervalHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setInterval(intervals[i]);

            manager.persist(item);
        }

        manager.flush();
        
        manager.getTransaction().commit();
        
        manager.close();

        manager = factory.createEntityManager();
        
        for (int i = 0; i < intervals.length; i++) {

            JodaIntervalHolder item = manager.find(JodaIntervalHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            if (intervals[i] == null) {
            	assertNull(item.getInterval());
            } else {
            	assertEquals(intervals[i].toString(), item.getInterval().toString());
            }
        }
        
        verifyDatabaseTable(manager, JodaIntervalHolder.class.getAnnotation(Table.class).name());
        
        manager.close();
    }
    
    @Test @Ignore // Joda Time Contrib does not handle timezones properly
    public void testRoundtripWithJodaTime() {
        
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < intervals.length; i++) {
            manager.remove(manager.find(JodaIntervalHolder.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();
        
        manager.getTransaction().begin();
        
        for (int i = 0; i < intervals.length; i++) {

            IntervalJoda item = new IntervalJoda();
            item.setId(i);
            item.setName("test_" + i);
            item.setInterval(jodaIntervals[i]);

            manager.persist(item);
        }

        manager.flush();
        
        manager.getTransaction().commit();
        
        manager.close();

        manager = factory.createEntityManager();
        
        for (int i = 0; i < intervals.length; i++) {

            JodaIntervalHolder item = manager.find(JodaIntervalHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(intervals[i].toString(), item.getInterval().toString());
        }
        manager.close();
    }
}
