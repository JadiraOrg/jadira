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
import static org.junit.Assert.assertNull;

import org.jadira.usertype.dateandtime.joda.testmodel.JodaDateMidnightHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.DateMidnight;
import org.joda.time.DateTimeZone;
import org.junit.Test;

public class TestPersistentDateMidnight extends AbstractDatabaseTest<JodaDateMidnightHolder> {

    private static final DateMidnight[] dateMidnights = new DateMidnight[]{new DateMidnight(2004, 2, 25, DateTimeZone.forOffsetHours(4)), new DateMidnight(1980, 3, 11, DateTimeZone.UTC), null};

    public TestPersistentDateMidnight() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < dateMidnights.length; i++) {
            JodaDateMidnightHolder item = new JodaDateMidnightHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setDateMidnight(dateMidnights[i]);

            persist(item);
        }

        for (int i = 0; i < dateMidnights.length; i++) {

            JodaDateMidnightHolder item = find(JodaDateMidnightHolder.class, i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            if (dateMidnights[i] == null) {
                assertNull(item.getDateMidnight());
            } else {
                assertEquals(dateMidnights[i].toString(), item.getDateMidnight().toString());
            }
        }

        verifyDatabaseTable();
    }
}
