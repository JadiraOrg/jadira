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

import org.jadira.usertype.dateandtime.joda.testmodel.YearMonthDayHolder;
import org.jadira.usertype.dateandtime.joda.testmodel.YearMonthDayJoda;
import org.jadira.usertype.dateandtime.shared.dbunit.DatabaseCapable;
import org.joda.time.YearMonthDay;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings("deprecation")
public class TestPersistentYearMonthDay extends DatabaseCapable {

    private static final YearMonthDay[] localDates = new YearMonthDay[] { new YearMonthDay(2004, 2, 25), new YearMonthDay(1980, 3, 11) };
    
    private static final YearMonthDay[] jodaYearMonthDays = new YearMonthDay[] { new YearMonthDay(2004, 2, 25), new YearMonthDay(1980, 3, 11) };

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
        
        for (int i = 0; i < localDates.length; i++) {

            YearMonthDayHolder item = new YearMonthDayHolder();
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

            YearMonthDayHolder item = manager.find(YearMonthDayHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localDates[i], item.getLocalDate());
        }
        
        verifyDatabaseTable(manager, YearMonthDayHolder.class.getAnnotation(Table.class).name());
        
        manager.close();
    }
    
    @Test
    public void testRoundtripWithJodaTime() {
        
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < localDates.length; i++) {
            manager.remove(manager.find(YearMonthDayHolder.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();
        
        manager.getTransaction().begin();

        for (int i = 0; i < localDates.length; i++) {

            YearMonthDayJoda item = new YearMonthDayJoda();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalDate(jodaYearMonthDays[i]);

            manager.persist(item);
        }

        manager.flush();
        
        manager.getTransaction().commit();
        
        manager.close();

        manager = factory.createEntityManager();
        
        for (int i = 0; i < localDates.length; i++) {

            YearMonthDayHolder item = manager.find(YearMonthDayHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localDates[i], item.getLocalDate());
        }
        
        manager.close();
    }
}
