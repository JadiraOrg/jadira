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

import org.jadira.usertype.dateandtime.joda.testmodel.JodaLocalDateHolder;
import org.jadira.usertype.dateandtime.joda.testmodel.LocalDateJoda;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestPersistentLocalDate extends AbstractDatabaseTest<JodaLocalDateHolder> {

    private static final LocalDate[] localDates = new LocalDate[]{new LocalDate(2004, 2, 25), new LocalDate(1980, 3, 11)};

    private static final org.joda.time.LocalDate[] jodaLocalDates = new org.joda.time.LocalDate[]{new org.joda.time.LocalDate(2004, 2, 25), new org.joda.time.LocalDate(1980, 3, 11)};

    @Test
    public void testPersist() {
        for (int i = 0; i < localDates.length; i++) {
            JodaLocalDateHolder item = new JodaLocalDateHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalDate(localDates[i]);

            persist(item);
        }

        for (int i = 0; i < localDates.length; i++) {

            JodaLocalDateHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localDates[i], item.getLocalDate());
        }

        verifyDatabaseTable();
    }

    @Test
    @Ignore // Joda Time Contrib does not support Hibernate 4 yet
    public void testRoundtripWithJodaTime() {
        for (int i = 0; i < localDates.length; i++) {
            LocalDateJoda item = new LocalDateJoda();
            item.setId(i);
            item.setName("test_" + i);
            item.setLocalDate(jodaLocalDates[i]);

            persist(item);
        }

        for (int i = 0; i < localDates.length; i++) {

            JodaLocalDateHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(localDates[i], item.getLocalDate());
        }
    }
}
