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

import org.jadira.usertype.dateandtime.joda.testmodel.JodaInstantAsStringHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestPersistentInstantAsString extends AbstractDatabaseTest<JodaInstantAsStringHolder> {

    private static final Instant[] instants =
            new org.joda.time.Instant[]{
                    new org.joda.time.DateTime(2004, 2, 25, 17, 3, 45, 760, DateTimeZone.UTC).toInstant(),
                    new org.joda.time.DateTime(1980, 3, 11, 2, 3, 45, 0, DateTimeZone.forID("+02:00")).toInstant()};

    public TestPersistentInstantAsString() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
    @Test
    public void testPersist() {
        for (int i = 0; i < instants.length; i++) {
            JodaInstantAsStringHolder item = new JodaInstantAsStringHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setInstant(instants[i]);

            persist(item);
        }

        for (int i = 0; i < instants.length; i++) {
            JodaInstantAsStringHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(instants[i], item.getInstant());
        }

        verifyDatabaseTable();
    }
}
