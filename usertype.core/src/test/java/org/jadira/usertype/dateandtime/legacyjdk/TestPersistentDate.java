/*
 *  Copyright 2010, 2011, 2012 Christopher Pheby
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
package org.jadira.usertype.dateandtime.legacyjdk;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.jadira.usertype.dateandtime.legacyjdk.testmodel.LegacyDateHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.junit.Test;

public class TestPersistentDate extends AbstractDatabaseTest<LegacyDateHolder> {

	private static final Calendar GMT_CAL = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
	
	private static final Date[] dateTimes = new Date[2];
    
    static {
    	GMT_CAL.clear();
    	GMT_CAL.set(2004, 2 - 1, 25, 12, 11, 10);
    	dateTimes[0] = GMT_CAL.getTime();
    	
    	GMT_CAL.clear();
    	GMT_CAL.set(1980, 3 - 1, 11, 13, 12, 11);
    	dateTimes[1] = GMT_CAL.getTime();
    }

    public TestPersistentDate() {
    	super(TestLegacyJdkSuite.getFactory());
    }
    
    @Test
    public void testPersist() {

        for (int i = 0; i < dateTimes.length; i++) {

            LegacyDateHolder item = new LegacyDateHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setDate(dateTimes[i]);

            persist(item);
        }

        for (int i = 0; i < dateTimes.length; i++) {

            LegacyDateHolder item = find(LegacyDateHolder.class, i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            if (dateTimes[i] == null) {
                assertNull(item.getDate());
            } else {
                assertEquals(dateTimes[i].toString(), item.getDate().toString());
            }
        }

        verifyDatabaseTable();
    }
}
