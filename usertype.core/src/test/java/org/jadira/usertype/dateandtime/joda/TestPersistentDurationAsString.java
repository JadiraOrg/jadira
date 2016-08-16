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

import org.jadira.usertype.dateandtime.joda.testmodel.DurationJoda;
import org.jadira.usertype.dateandtime.joda.testmodel.JodaDurationAsStringHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.junit.Ignore;
import org.junit.Test;

public class TestPersistentDurationAsString extends AbstractDatabaseTest<JodaDurationAsStringHolder> {

    private static final Duration[] durations = new Duration[] { 
        Duration.ZERO, 
        new Duration(30l * 1000l), 
        new Period(0, 0, 30, 0).toDurationFrom(new LocalDateTime(2010, 8, 8, 10, 10, 10).toDateTime(DateTimeZone.UTC)), 
        new Duration(new DateTime(2010, 4, 1, 0, 0, 0, DateTimeZone.UTC).minusMonths(3), new DateTime(2010, 4, 1, 0, 0, 0, DateTimeZone.UTC)) };
    
    public TestPersistentDurationAsString() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        
        for (int i = 0; i < durations.length; i++) {

            JodaDurationAsStringHolder item = new JodaDurationAsStringHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setDuration(durations[i]);

            persist(item);
        }

        for (int i = 0; i < durations.length; i++) {
            
            JodaDurationAsStringHolder item = find(JodaDurationAsStringHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(durations[i], item.getDuration());
        }

        verifyDatabaseTable();
    }
    
    @Test @Ignore // Joda Time Contrib does not support Hibernate 4 yet
    public void testRoundtripWithJodaTime() {
        
        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();
        for (int i = 0; i < durations.length; i++) {
            manager.remove(manager.find(JodaDurationAsStringHolder.class, Long.valueOf(i)));
        }
        manager.flush();
        manager.getTransaction().commit();

        manager.clear(); // Clear the persistence context to ensure that the entity is not loaded from session cache.
                
        for (int i = 0; i < durations.length; i++) {

            DurationJoda item = new DurationJoda();
            item.setId(i);
            item.setName("test_" + i);
            item.setDuration(durations[i]);

            persist(item);
        }


        for (int i = 0; i < durations.length; i++) {

            JodaDurationAsStringHolder item = find(JodaDurationAsStringHolder.class, i);

            assertNotNull(item);
            assertEquals(i, item.getId());
        }

        verifyDatabaseTable();
    }
}
