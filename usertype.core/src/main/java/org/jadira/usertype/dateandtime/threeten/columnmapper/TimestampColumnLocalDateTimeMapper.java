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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.jadira.usertype.spi.shared.DatabaseZoneConfigured;

public class TimestampColumnLocalDateTimeMapper extends AbstractTimestampThreeTenColumnMapper<LocalDateTime> implements DatabaseZoneConfigured<ZoneId> {

    private static final long serialVersionUID = -7670411089210984705L;
    
    public static final DateTimeFormatter LOCAL_DATETIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").appendOptional(new DateTimeFormatterBuilder().appendLiteral('.').appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, false).toFormatter()).toFormatter();

	public TimestampColumnLocalDateTimeMapper() {
		super();
	}

	public TimestampColumnLocalDateTimeMapper(ZoneOffset databaseZone) {
		super(databaseZone);
	}
    
    @Override
    public LocalDateTime fromNonNullString(String s) {
        return LocalDateTime.parse(s);
    }

    @Override
    public LocalDateTime fromNonNullValue(Timestamp value) {
    	
    	ZoneId currentDatabaseZone = getDatabaseZone() == null ? getDefault() : getDatabaseZone();
        
        Instant instant = Instant.ofEpochMilli(value.getTime());
        instant = instant.with(ChronoField.NANO_OF_SECOND, value.getNanos());
        
        return LocalDateTime.ofInstant(instant, currentDatabaseZone);
    }

    @Override
    public String toNonNullString(LocalDateTime value) {
        return value.toString();
    }

    @Override
    public Timestamp toNonNullValue(LocalDateTime value) {

    	ZoneId currentDatabaseZone = getDatabaseZone() == null ? getDefault() : getDatabaseZone();
    	
    	ZonedDateTime zdt = value.atZone(currentDatabaseZone);
        
        final Timestamp timestamp = new Timestamp(zdt.toInstant().toEpochMilli());
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
