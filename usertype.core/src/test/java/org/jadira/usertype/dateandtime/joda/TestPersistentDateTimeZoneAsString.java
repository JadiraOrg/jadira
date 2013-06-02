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

import org.jadira.usertype.dateandtime.joda.testmodel.JodaDateTimeZoneAsStringHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestPersistentDateTimeZoneAsString extends AbstractDatabaseTest<JodaDateTimeZoneAsStringHolder> {

    private static final DateTimeZone[] dateTimeZones = new DateTimeZone[]{DateTimeZone.forOffsetHours(4), DateTimeZone.UTC, null};

    public TestPersistentDateTimeZoneAsString() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < dateTimeZones.length; i++) {

            JodaDateTimeZoneAsStringHolder item = new JodaDateTimeZoneAsStringHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setDateTimeZone(dateTimeZones[i]);

            persist(item);
        }

        for (int i = 0; i < dateTimeZones.length; i++) {

            JodaDateTimeZoneAsStringHolder item = find(JodaDateTimeZoneAsStringHolder.class, i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            if (dateTimeZones[i] == null) {
                assertNull(item.getDateTimeZone());
            } else {
                assertEquals(dateTimeZones[i].toString(), item.getDateTimeZone().toString());
            }
        }

        verifyDatabaseTable();
    }
}
