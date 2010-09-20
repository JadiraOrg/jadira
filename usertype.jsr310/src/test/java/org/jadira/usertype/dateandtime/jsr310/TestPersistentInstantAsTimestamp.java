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
import javax.time.Instant;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.OffsetDateTime;
import javax.time.calendar.TimeZone;
import javax.time.calendar.ZoneOffset;

import org.jadira.usertype.dateandtime.jsr310.testmodel.InstantAsTimestampHolder;
import org.jadira.usertype.dateandtime.jsr310.testmodel.InstantJoda;
import org.jadira.usertype.dateandtime.shared.dbunit.DatabaseCapable;
import org.joda.time.DateTimeZone;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TestPersistentInstantAsTimestamp extends DatabaseCapable {

    private static final Instant[] instants = new Instant[] { Instant.from(OffsetDateTime.of(2004, 2, 25, 17, 3, 45, 760000000, ZoneOffset.of("Z"))),
            Instant.from(OffsetDateTime.of(1980, 3, 11, 2, 3, 45, 0, ZoneOffset.of("+02:00"))) };

    private static final org.joda.time.Instant[] jodaInstants = 
        new org.joda.time.Instant[] { 
            new org.joda.time.DateTime(2004, 2, 25, 17, 3, 45, 760, DateTimeZone.UTC).toInstant(),
            new org.joda.time.DateTime(1980, 3, 11, 2, 3, 45, 0, DateTimeZone.forID("+02:00")).toInstant() };

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

        for (int i = 0; i < instants.length; i++) {

            InstantAsTimestampHolder item = new InstantAsTimestampHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setInstant(instants[i]);

            manager.persist(item);
        }

        manager.flush();

        manager.getTransaction().commit();

        manager.close();

        manager = factory.createEntityManager();

        for (int i = 0; i < instants.length; i++) {

            InstantAsTimestampHolder item = manager.find(InstantAsTimestampHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(instants[i], item.getInstant());
        }
        
        verifyDatabaseTable(manager, InstantAsTimestampHolder.class.getAnnotation(Table.class).name());
        
        manager.close();
    }
        
    @Test
    @Ignore // Joda Time Contrib is not compatible with Hibernate 3.6
    public void testRoundtripWithJodaTime() {
        
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < instants.length; i++) {
            manager.remove(manager.find(InstantAsTimestampHolder.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();
        
        manager.getTransaction().begin();

        for (int i = 0; i < instants.length; i++) {

            InstantJoda item = new InstantJoda();
            item.setId(i);
            item.setName("test_" + i);
            item.setInstant(jodaInstants[i]);

            manager.persist(item);
        }

        manager.flush();
        
        manager.getTransaction().commit();
        
        manager.close();

        manager = factory.createEntityManager();
        
        for (int i = 0; i < instants.length; i++) {

            InstantAsTimestampHolder item = manager.find(InstantAsTimestampHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(instants[i], item.getInstant());
        }
        
        manager.close();
    }
        
    @Test @Ignore
    public void testNanosWithJodaTime() {
        
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < instants.length; i++) {
            manager.remove(manager.find(InstantAsTimestampHolder.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();
        
        manager.getTransaction().begin();
        
        InstantAsTimestampHolder item = new InstantAsTimestampHolder();
        item.setId(1);
        item.setName("test_nanos1");
        item.setInstant(LocalDateTime.of(2010, 8, 1, 10, 10, 10, 111444444).atZone(TimeZone.UTC).toInstant());

        manager.persist(item);
        manager.flush();
        
        manager.getTransaction().commit();
        
        manager.close();

        manager = factory.createEntityManager();
        
        InstantJoda jodaItem = manager.find(InstantJoda.class, Long.valueOf(1));

        assertNotNull(jodaItem);
        assertEquals(1, jodaItem.getId());
        assertEquals("test_nanos1", jodaItem.getName());
        assertEquals(new org.joda.time.DateTime(2010, 8, 1, 10, 10, 10, 111, DateTimeZone.UTC).toInstant(), jodaItem.getInstant());

        manager.close();
        
        manager = factory.createEntityManager();

        item = manager.find(InstantAsTimestampHolder.class, Long.valueOf(1));
 
        assertNotNull(item);
        assertEquals(1, item.getId());
        assertEquals("test_nanos1", item.getName());
        assertEquals(LocalDateTime.of(2010, 8, 1, 10, 10, 10, 111444444).atZone(TimeZone.UTC).toInstant(), item.getInstant());

        manager.close();
    }
}
