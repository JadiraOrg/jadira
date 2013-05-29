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
package org.jadira.usertype.dateandtime.threetenbp.columnmapper;

import java.sql.Timestamp;
import java.util.TimeZone;

import org.jadira.usertype.spi.shared.AbstractTimestampColumnMapper;
import org.jadira.usertype.spi.shared.DatabaseZoneConfigured;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;
import org.threeten.bp.temporal.ChronoField;

public class TimestampColumnLocalTimeMapper extends AbstractTimestampColumnMapper<LocalTime> implements DatabaseZoneConfigured<ZoneOffset> {

    private static final long serialVersionUID = 1921591625617366103L;

    public static final DateTimeFormatter LOCAL_DATETIME_PRINTER = new DateTimeFormatterBuilder().appendPattern("0001-01-01 HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter();
    public static final DateTimeFormatter LOCAL_DATETIME_PARSER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).toFormatter();

	private static final int MILLIS_IN_SECOND = 1000;

    private ZoneOffset databaseZone = ZoneOffset.of("Z");

	public TimestampColumnLocalTimeMapper() {
	}

	public TimestampColumnLocalTimeMapper(ZoneOffset databaseZone) {
		this.databaseZone = databaseZone;
	}
    
    @Override
    public LocalTime fromNonNullString(String s) {
        return LocalTime.parse(s);
    }

    @Override
    public LocalTime fromNonNullValue(Timestamp value) {
    	
    	ZoneOffset currentDatabaseZone = databaseZone == null ? getDefault() : databaseZone;

        int adjustment = TimeZone.getDefault().getOffset(value.getTime()) - (currentDatabaseZone.getRules().getOffset(LocalDateTime.now()).getTotalSeconds() * MILLIS_IN_SECOND);
        
        Instant instant = Instant.ofEpochMilli(value.getTime() + adjustment);
        instant = instant.with(ChronoField.NANO_OF_SECOND, value.getNanos());
        
        LocalDateTime ldt = LocalDateTime.ofInstant(instant, currentDatabaseZone);
        LocalTime localTime = ldt.toLocalTime();
        
        return localTime;
    }

    @Override
    public String toNonNullString(LocalTime value) {
        return value.toString();
    }

    @Override
    public Timestamp toNonNullValue(LocalTime value) {

    	ZoneOffset currentDatabaseZone = databaseZone == null ? getDefault() : databaseZone;
    	
    	LocalDateTime ldt = value.atDate(LocalDate.of(1970, 1, 1));
    	ZonedDateTime zdt = ldt.atZone(currentDatabaseZone);
        int adjustment = TimeZone.getDefault().getOffset(zdt.toInstant().toEpochMilli()) - (currentDatabaseZone.getRules().getOffset(LocalDateTime.now()).getTotalSeconds() * MILLIS_IN_SECOND);
        
        final Timestamp timestamp = new Timestamp(zdt.toInstant().toEpochMilli() - adjustment);
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
    public void setDatabaseZone(ZoneOffset databaseZone) {
        this.databaseZone = databaseZone;
    }
    
	@Override
	public ZoneOffset parseZone(String zoneString) {
		return ZoneOffset.of(zoneString);
	}
}
