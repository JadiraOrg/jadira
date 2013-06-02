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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.hamcrest.core.IsEqual;
import org.jadira.usertype.dateandtime.joda.testmodel.JodaDateTimeAndZoneWithOffsetHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

public class TestPersistentDateTimeAndZoneWithOffset extends AbstractDatabaseTest<JodaDateTimeAndZoneWithOffsetHolder> {

    private static final DateTime[] dateTimes = new DateTime[] { 
        new DateTime(2004, 2, 25, 12, 11, 10, 0, DateTimeZone.forOffsetHours(4)).withZone(DateTimeZone.UTC), 
        new DateTime(1980, 3, 11, 13, 12, 11, 500, DateTimeZone.UTC), 
        null };

    public TestPersistentDateTimeAndZoneWithOffset() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
    @Test
    public void testPersist() {

        for (int i = 0; i < dateTimes.length; i++) {

            JodaDateTimeAndZoneWithOffsetHolder item = new JodaDateTimeAndZoneWithOffsetHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setDateTime(dateTimes[i]);

            persist(item);
        }

        for (int i = 0; i < dateTimes.length; i++) {

            JodaDateTimeAndZoneWithOffsetHolder item = find(JodaDateTimeAndZoneWithOffsetHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            if (dateTimes[i] == null) {
            	assertNull(item.getDateTime());
            } else {
            	assertEquals(dateTimes[i].toString(), item.getDateTime().toString());
            }
        }
        
        verifyDatabaseTable();
    }
    
	@Test // Added to investigate http://sourceforge.net/mailarchive/message.php?msg_id=29056453
	public void testDST() {

		DateTimeZone tz = DateTimeZone.forID("Europe/Berlin");
		assertFalse(tz.isFixed());
		DateTime dt = new DateTime(2010, 10, 31, 1, 0, 0, tz);

		for (int i = 0; i < 5; i++) {

			System.err.println("Saving: " + dt);

			JodaDateTimeAndZoneWithOffsetHolder item = new JodaDateTimeAndZoneWithOffsetHolder();
			item.setId(i + 10);
			item.setName("test_" + i);
			item.setDateTime(dt);

			persist(item);

			JodaDateTimeAndZoneWithOffsetHolder readItem = find(JodaDateTimeAndZoneWithOffsetHolder.class, Long.valueOf(i) + 10);

			System.err.println("ReadItem: " + readItem.getDateTime());

			assertThat("For record {" + i + "}", dt,
					IsEqual.equalTo(readItem.getDateTime()));

			dt = dt.plusHours(1);
		}
	}

}
