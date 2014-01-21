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
package org.jadira.usertype.dateandtime.threeten.columnmapper;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.TimeZone;

import org.jadira.usertype.spi.shared.DatabaseZoneConfigured;

public class TimestampColumnLocalTimeMapper extends AbstractTimestampThreeTenColumnMapper<LocalTime> implements DatabaseZoneConfigured<ZoneOffset> {

    private static final long serialVersionUID = 1921591625617366103L;

    public static final DateTimeFormatter LOCAL_DATETIME_PRINTER = new DateTimeFormatterBuilder().appendPattern("0001-01-01 HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter();
    public static final DateTimeFormatter LOCAL_DATETIME_PARSER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter();

	private static final int MILLIS_IN_SECOND = 1000;
    
	public TimestampColumnLocalTimeMapper() {
		super(null);
	}

	public TimestampColumnLocalTimeMapper(ZoneOffset databaseZone) {
		super(databaseZone);
	}
    
    @Override
    public LocalTime fromNonNullString(String s) {
        return LocalTime.parse(s);
    }

    @Override
    public LocalTime fromNonNullValue(Timestamp value) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(value.getTime());
    	
        LocalTime time = LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND) * 1000000);
        return time;
    }
    	
    @Override
    public String toNonNullString(LocalTime value) {
        return value.toString();
    }

    @Override
    public Timestamp toNonNullValue(LocalTime value) {

    	ZoneOffset currentDatabaseZone = getDatabaseZone() == null ? getDefault() : getDatabaseZone();        
    	
    	LocalDateTime ldt = value.atDate(LocalDate.of(1970, 1, 1));
    	ZonedDateTime zdt = ldt.atZone(currentDatabaseZone);
        Instant ins = zdt.toInstant();

    	ZoneOffset off = getDefault();
        int adjustment = TimeZone.getDefault().getOffset(ins.toEpochMilli()) - (off.getRules().getOffset(LocalDateTime.now()).getTotalSeconds() * MILLIS_IN_SECOND);
        
        final Timestamp timestamp = new Timestamp(ins.toEpochMilli() - adjustment);
        timestamp.setNanos(value.getNano());
        return timestamp;
    }

    private static ZoneOffset getDefault() {

    	ZoneOffset zone = null;
        try {
            try {
                String id = System.getProperty("user.timezone");
                if (id != null) {
                    zone = ZoneOffset.of(id);
                }
            } catch (RuntimeException ex) {
                zone = null;
            }
            if (zone == null) {
                zone = ZoneOffset.of(java.util.TimeZone.getDefault().getID());
            }
        } catch (RuntimeException ex) {
            zone = null;
        }
        if (zone == null) {
            zone = ZoneOffset.of("Z");
        }
        return zone;
    }
    
	@Override
	public ZoneOffset parseZone(String zoneString) {
		return ZoneOffset.of(zoneString);
	}
}
