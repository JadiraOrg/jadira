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

import org.jadira.usertype.dateandtime.joda.testmodel.JodaLocalDateTimeAsStringHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.LocalDateTime;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestPersistentLocalDateTimeAsString extends AbstractDatabaseTest<JodaLocalDateTimeAsStringHolder> {

    private static final LocalDateTime[] localDateTimes = new LocalDateTime[]{new LocalDateTime(2004, 2, 25, 12, 11, 10), new LocalDateTime(1980, 3, 11, 13, 12, 11)};

    public TestPersistentLocalDateTimeAsString() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < localDateTimes.length; i++) {
            JodaLocalDateTimeAsStringHolder item = new JodaLocalDateTimeAsStringHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalDateTime(localDateTimes[i]);

            persist(item);
        }

        for (int i = 0; i < localDateTimes.length; i++) {

            JodaLocalDateTimeAsStringHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localDateTimes[i], item.getLocalDateTime());
        }

        verifyDatabaseTable();
    }
}
