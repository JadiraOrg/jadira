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

import org.jadira.usertype.spi.shared.DatabaseZoneConfigured;

import java.sql.Date;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

public class DateColumnLocalDateMapper extends AbstractDateThreeTenColumnMapper<LocalDate> implements DatabaseZoneConfigured<ZoneId> {

    private static final long serialVersionUID = 6734385103313158326L;

    public static final DateTimeFormatter LOCAL_DATE_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").toFormatter();

//	private static final int MILLIS_IN_SECOND = 1000;
    
	public DateColumnLocalDateMapper() {
	}

	public DateColumnLocalDateMapper(ZoneOffset databaseZone) {
		super(databaseZone);
	}
    
    @Override
    public LocalDate fromNonNullString(String s) {
        return LocalDate.parse(s);
    }

    @Override
    public LocalDate fromNonNullValue(Date value) {

		if (getDatabaseZone() == null) {
    		return LocalDate.parse(value.toString(), LOCAL_DATE_FORMATTER);
    	}

        ZoneId currentDatabaseZone = getDatabaseZone() == null ? getDefault() : getDatabaseZone();
        
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(value.getTime()), currentDatabaseZone);
        LocalDate localDate = dateTime.toLocalDate();
        
        return localDate;
    }

    @Override
    public String toNonNullString(LocalDate value) {
        return value.toString();
    }

    @Override
    public Date toNonNullValue(LocalDate value) {
    	
        if (getDatabaseZone() == null) {
        	return Date.valueOf(LOCAL_DATE_FORMATTER.format((LocalDate) value));
        }

        ZoneId currentDatabaseZone = getDatabaseZone() == null ? getDefault() : getDatabaseZone();
    	ZonedDateTime zonedValue = value.atStartOfDay(currentDatabaseZone);

//        Instant valueAsInstant = Instant.ofEpochSecond(zonedValue.toEpochSecond());
//        int defaultTimezoneOffset = TimeZone.getDefault().getOffset(zonedValue.toEpochSecond() * MILLIS_IN_SECOND);
//        int databaseTimezoneOffsetInSeconds = currentDatabaseZone.getRules().getOffset(valueAsInstant).getTotalSeconds();
//        int adjustment = defaultTimezoneOffset - databaseTimezoneOffsetInSeconds * MILLIS_IN_SECOND;
    	
    	final Date date = new Date(zonedValue.toInstant().toEpochMilli());
        return date;
    }

    private static ZoneId getDefault() {

        ZoneId zone = null;
        try {
            try {
                String id = System.getProperty("user.timezone");
                if (id != null) {
                    zone = ZoneId.of(id);
                }
            } catch (RuntimeException ex) {
                zone = null;
            }
            if (zone == null) {
                zone = ZoneId.of(java.util.TimeZone.getDefault().getID());
            }
        } catch (RuntimeException ex) {
            zone = null;
        }
        if (zone == null) {
            zone = ZoneId.of("Z");
        }
        return zone;
    }
    
	@Override
	public ZoneId parseZone(String zoneString) {
		return ZoneId.of(zoneString);
	}
}
