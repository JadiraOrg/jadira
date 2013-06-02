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

import org.jadira.usertype.dateandtime.joda.testmodel.MinutesHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.Minutes;
import org.junit.Test;

public class TestPersistentMinutes extends AbstractDatabaseTest<MinutesHolder> {

    private static final Minutes[] minutes = new Minutes[]{Minutes.minutes(1), Minutes.minutes(2010), Minutes.minutes(1999)};

    public TestPersistentMinutes() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
    @Test
    public void testPersist() {

        for (int i = 0; i < minutes.length; i++) {

            MinutesHolder item = new MinutesHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setMinute(minutes[i]);

            persist(item);
        }

        for (int i = 0; i < minutes.length; i++) {

            MinutesHolder item = find(MinutesHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(minutes[i], item.getMinute());
        }

        verifyDatabaseTable();
    }
}
