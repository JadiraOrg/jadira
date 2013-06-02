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

import org.jadira.usertype.dateandtime.joda.testmodel.YearsHolder;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.Years;
import org.junit.Test;

public class TestPersistentYears extends AbstractDatabaseTest<YearsHolder> {

    private static final Years[] years = new Years[]{Years.years(1), Years.years(2010), Years.years(1999)};

    public TestPersistentYears() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
    @Test
    public void testPersist() {

        for (int i = 0; i < years.length; i++) {

            YearsHolder item = new YearsHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setYear(years[i]);

            persist(item);
        }

        for (int i = 0; i < years.length; i++) {

            YearsHolder item = find(YearsHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(years[i], item.getYear());
        }

        verifyDatabaseTable();
    }
}
