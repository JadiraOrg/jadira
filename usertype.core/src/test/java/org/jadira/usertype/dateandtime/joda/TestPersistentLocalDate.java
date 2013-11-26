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

import java.sql.Date;

import org.jadira.usertype.dateandtime.joda.columnmapper.DateColumnLocalDateMapper;
import org.jadira.usertype.dateandtime.joda.testmodel.JodaLocalDateHolder;
import org.jadira.usertype.dateandtime.joda.testmodel.LocalDateJoda;
import org.jadira.usertype.dateandtime.shared.dbunit.AbstractDatabaseTest;
import org.joda.time.DateTimeUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.Ignore;
import org.junit.Test;

public class TestPersistentLocalDate extends AbstractDatabaseTest<JodaLocalDateHolder> {

    private static final LocalDate[] localDates = new LocalDate[]{new LocalDate(2004, 2, 25), new LocalDate(1980, 3, 11)};

    private static final org.joda.time.LocalDate[] jodaLocalDates = new org.joda.time.LocalDate[]{new org.joda.time.LocalDate(2004, 2, 25), new org.joda.time.LocalDate(1980, 3, 11)};

    public TestPersistentLocalDate() {
    	super(TestJodaTimeSuite.getFactory());
    }
    
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
    
    @Test
    public void testMapper() {
    	final DateColumnLocalDateMapper mapper = new DateColumnLocalDateMapper();
    
    	final LocalDate in = new LocalDate("2012-10-15");
    	final Date db = mapper.toNonNullValue(in);
    	final LocalDate out = mapper.fromNonNullValue(db);
    	
        assertEquals(in, out);
    }

    @Test
    public void testMapperWithDatabaseZoneAndDstTransitionForward() {
        final DateColumnLocalDateMapper mapper = new DateColumnLocalDateMapper();
        DateTimeZone zoneWithDaylightSavings = DateTimeZone.forID("America/Denver");

        mapper.setDatabaseZone(zoneWithDaylightSavings);

        final LocalDate in = new LocalDate("2012-10-15");
        long nextTransition = zoneWithDaylightSavings.nextTransition(in.toDateTimeAtStartOfDay().getMillis());

        try
        {
            DateTimeUtils.setCurrentMillisFixed(nextTransition);
            final Date db = mapper.toNonNullValue(in);

            assertEquals(db.toString(), "2012-10-15");
        }
        finally
        {
            DateTimeUtils.setCurrentMillisSystem();
        }
    }

    @Test
    public void testMapperWithDatabaseZoneAndDstTransitionBackward() {
        final DateColumnLocalDateMapper mapper = new DateColumnLocalDateMapper();
        DateTimeZone zoneWithDaylightSavings = DateTimeZone.forID("America/Denver");

        mapper.setDatabaseZone(zoneWithDaylightSavings);

        final Date in = Date.valueOf("2013-01-15");
        long nextTransition = zoneWithDaylightSavings.nextTransition(in.getTime());

        try
        {
            DateTimeUtils.setCurrentMillisFixed(nextTransition);
            final LocalDate out = mapper.fromNonNullValue(in);

            assertEquals(out.toString(), "2013-01-15");
        }
        finally
        {
            DateTimeUtils.setCurrentMillisSystem();
        }
    }
}
