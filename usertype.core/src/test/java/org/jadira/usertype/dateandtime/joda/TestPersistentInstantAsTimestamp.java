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

import org.jadira.usertype.dateandtime.joda.testmodel.InstantJoda;
import org.jadira.usertype.dateandtime.joda.testmodel.JodaInstantAsTimestampHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestPersistentInstantAsTimestamp extends AbstractDatabaseTest<JodaInstantAsTimestampHolder> {

    private static final Instant[] instants =
            new org.joda.time.Instant[]{
                    new org.joda.time.DateTime(2004, 2, 25, 17, 3, 45, 760, DateTimeZone.UTC).toInstant(),
                    new org.joda.time.DateTime(1980, 3, 11, 2, 3, 45, 0, DateTimeZone.forID("+02:00")).toInstant()};

    private static final org.joda.time.Instant[] jodaInstants =
            new org.joda.time.Instant[]{
                    new org.joda.time.DateTime(2004, 2, 25, 17, 3, 45, 760, DateTimeZone.UTC).toInstant(),
                    new org.joda.time.DateTime(1980, 3, 11, 2, 3, 45, 0, DateTimeZone.forID("+02:00")).toInstant()};

    public TestPersistentInstantAsTimestamp() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
    @Test
    public void testPersist() {

        for (int i = 0; i < instants.length; i++) {

            JodaInstantAsTimestampHolder item = new JodaInstantAsTimestampHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setInstant(instants[i]);

            persist(item);
        }


        for (int i = 0; i < instants.length; i++) {

            JodaInstantAsTimestampHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(instants[i], item.getInstant());
        }

        verifyDatabaseTable();
    }

    @Test
    @Ignore // Joda Time Contrib doesnt compensate for system timezone
    public void testRoundtripWithJodaTime() {

        for (int i = 0; i < instants.length; i++) {

            InstantJoda item = new InstantJoda();
            item.setId(i);
            item.setName("test_" + i);
            item.setInstant(jodaInstants[i]);

            persist(item);
        }


        for (int i = 0; i < instants.length; i++) {

            JodaInstantAsTimestampHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(instants[i], item.getInstant());
        }
    }

    @Test
    @Ignore
    public void testNanosWithJodaTime() {
        JodaInstantAsTimestampHolder item = new JodaInstantAsTimestampHolder();
        item.setId(1);
        item.setName("test_nanos1");
        item.setInstant(new DateTime(2010, 8, 1, 10, 10, 10, 111, DateTimeZone.UTC).toInstant());

        persist(item);

        InstantJoda jodaItem = find(InstantJoda.class, (long) 1);

        assertNotNull(jodaItem);
        assertEquals(1, jodaItem.getId());
        assertEquals("test_nanos1", jodaItem.getName());
        assertEquals(new DateTime(2010, 8, 1, 10, 10, 10, 111, DateTimeZone.UTC).toInstant(), jodaItem.getInstant());

        item = find((long) 1);

        assertNotNull(item);
        assertEquals(1, item.getId());
        assertEquals("test_nanos1", item.getName());
        assertEquals(new DateTime(2010, 8, 1, 10, 10, 10, 111, DateTimeZone.UTC).toInstant(), item.getInstant());
    }
}
