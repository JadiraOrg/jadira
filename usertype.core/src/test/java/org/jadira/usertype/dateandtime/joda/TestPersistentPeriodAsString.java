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
package org.jadira.usertype.dateandtime.joda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.sql.SQLException;

import org.jadira.usertype.dateandtime.joda.testmodel.JodaPeriodAsStringHolder;
import org.jadira.usertype.dateandtime.joda.testmodel.PeriodJoda;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.junit.Ignore;
import org.junit.Test;

public class TestPersistentPeriodAsString extends AbstractDatabaseTest<JodaPeriodAsStringHolder> {

    private static final Period[] periods = new Period[]{
            Period.days(2),
            Period.seconds(30),
            Period.months(3),
            Period.seconds(30),
            new Period(4, 35, 0, 40, 141, 0, 0, 0),
            new Period(28, 10, 0, 2, 2, 4, 35, 40),
            new Period(28, 10, 0, 0, 16, 4, 35, 40),
            new Period(0, 0, 0, 0, 16, 0, 0, 0, PeriodType.hours())
    };

    private static final org.joda.time.Period[] jodaPeriods = new org.joda.time.Period[]{
            org.joda.time.Period.days(2),
            org.joda.time.Period.seconds(30),
            org.joda.time.Period.months(3),
            org.joda.time.Period.seconds(30),
            new org.joda.time.Period(4, 35, 0, 40, 141, 0, 0, 0),
            new org.joda.time.Period(28, 10, 0, 2, 2, 4, 35, 40),
            new org.joda.time.Period(28, 10, 0, 0, 16, 4, 35, 40),
            new org.joda.time.Period(0, 0, 0, 0, 16, 0, 0, 0, PeriodType.hours())
    };

    public TestPersistentPeriodAsString() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
    @Test
    public void testPersist() throws SQLException, IOException {
        for (int i = 0; i < periods.length; i++) {
            JodaPeriodAsStringHolder item = new JodaPeriodAsStringHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setPeriod(periods[i]);
            persist(item);
        }

        for (int i = 0; i < periods.length; i++) {

            JodaPeriodAsStringHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            assertEquals(periods[i], item.getPeriod());
        }

        verifyDatabaseTable();
    }

    @Test
    @Ignore // Joda Time Contrib does not support Hibernate 4 yet
    public void testRoundtripWithJodaTime() {
        for (int i = 0; i < periods.length; i++) {
            PeriodJoda item = new PeriodJoda();
            item.setId(i);
            item.setName("test_" + i);
            item.setPeriod(jodaPeriods[i]);

            persist(item);
        }

        for (int i = 0; i < periods.length; i++) {
            JodaPeriodAsStringHolder item = find((long) i);

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());

            assertEquals(periods[i], item.getPeriod());
        }
    }

    @Test
    @Ignore // Joda Time Contrib does not support Hibernate 4 yet
    public void testMillisWithJodaTime() {
        JodaPeriodAsStringHolder item = new JodaPeriodAsStringHolder();
        item.setId(1);
        item.setName("test_millis");
        item.setPeriod(Period.millis(111));

        persist(item);

        PeriodJoda jodaItem = find(PeriodJoda.class, (long) 1);

        assertNotNull(jodaItem);
        assertEquals(1, jodaItem.getId());
        assertEquals("test_millis", jodaItem.getName());
        assertEquals(new org.joda.time.Period(0, 0, 0, 111), jodaItem.getPeriod());

        item = find((long) 1);

        assertNotNull(item);
        assertEquals(1, item.getId());
        assertEquals("test_millis", item.getName());
        assertEquals(Period.millis(111), item.getPeriod());
    }
}
