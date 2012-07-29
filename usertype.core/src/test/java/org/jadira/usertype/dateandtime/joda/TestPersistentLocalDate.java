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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Table;

import org.hamcrest.core.IsEqual;
import org.jadira.usertype.dateandtime.joda.testmodel.JodaDateTimeHolder;
import org.jadira.usertype.dateandtime.joda.testmodel.JodaLocalDateHolder;
import org.jadira.usertype.dateandtime.joda.testmodel.LocalDateJoda;
import org.jadira.usertype.dateandtime.shared.dbunit.DatabaseCapable;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TestPersistentLocalDate extends DatabaseCapable {

    private static final LocalDate[] localDates = new LocalDate[] { new LocalDate(2004, 2, 25), new LocalDate(1980, 3, 11) };
    
    private static final org.joda.time.LocalDate[] jodaLocalDates = new org.joda.time.LocalDate[] { new org.joda.time.LocalDate(2004, 2, 25), new org.joda.time.LocalDate(1980, 3, 11) };

    private static EntityManagerFactory factory;

    @BeforeClass
    public static void setup() {
    	DateTimeZone tz = DateTimeZone.forID("Brazil/East");
    	DateTimeZone.setDefault(tz);
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
        
        for (int i = 0; i < localDates.length; i++) {

            JodaLocalDateHolder item = new JodaLocalDateHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalDate(localDates[i]);

            manager.persist(item);
        }

        manager.flush();
        
        manager.getTransaction().commit();
        
        manager.close();

        manager = factory.createEntityManager();
        
        for (int i = 0; i < localDates.length; i++) {

            JodaLocalDateHolder item = manager.find(JodaLocalDateHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localDates[i], item.getLocalDate());
        }
        
        verifyDatabaseTable(manager, JodaLocalDateHolder.class.getAnnotation(Table.class).name());
        
        manager.close();
    }
    
    @Test @Ignore // Joda Time Contrib does not support Hibernate 4 yet
    public void testRoundtripWithJodaTime() {
        
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < localDates.length; i++) {
            manager.remove(manager.find(JodaLocalDateHolder.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();
        
        manager.getTransaction().begin();

        for (int i = 0; i < localDates.length; i++) {

            LocalDateJoda item = new LocalDateJoda();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalDate(jodaLocalDates[i]);

            manager.persist(item);
        }

        manager.flush();
        
        manager.getTransaction().commit();
        
        manager.close();

        manager = factory.createEntityManager();
        
        for (int i = 0; i < localDates.length; i++) {

            JodaLocalDateHolder item = manager.find(JodaLocalDateHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localDates[i], item.getLocalDate());
        }
        
        manager.close();
    }
    
//	@Test // Added to investigate http://sourceforge.net/mailarchive/message.php?msg_id=29056453
//	public void testDST() {
//		
//		DateTime dt = new DateTime(2010, 10, 31, 1, 0, 0, tz);
//
//		EntityManager manager = factory.createEntityManager();
//
//		for (int i = 0; i < 5; i++) {
//
//			System.out.println("Saving: " + dt);
//
//			manager.getTransaction().begin();
//
//			JodaDateTimeHolder item = new JodaDateTimeHolder();
//			item.setId(i + 10);
//			item.setName("test_" + i);
//			item.setDateTime(dt);
//
//			manager.persist(item);
//			manager.flush();
//			manager.getTransaction().commit();
//
//			JodaDateTimeHolder readItem = manager.find(
//					JodaDateTimeHolder.class, Long.valueOf(i) + 10);
//
//			System.out.println("ReadItem: " + readItem.getDateTime());
//
//			assertThat("For record {" + i + "}", dt,
//					IsEqual.equalTo(readItem.getDateTime()));
//
//			dt = dt.plusHours(1);
//		}
//		manager.close();
//	}

}
