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
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;
import org.threeten.bp.temporal.ChronoField;

public class TimestampColumnLocalDateTimeMapper extends AbstractTimestampColumnMapper<LocalDateTime> implements DatabaseZoneConfigured<ZoneOffset> {

    private static final long serialVersionUID = -7670411089210984705L;

    private ZoneOffset databaseZone = ZoneOffset.of("Z");
    
    public static final DateTimeFormatter LOCAL_DATETIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").appendOptional(new DateTimeFormatterBuilder().appendLiteral('.').appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, false).toFormatter()).toFormatter();

	private static final int MILLIS_IN_SECOND = 1000;

	public TimestampColumnLocalDateTimeMapper() {
	}

	public TimestampColumnLocalDateTimeMapper(ZoneOffset databaseZone) {
		this.databaseZone = databaseZone;
	}
    
    @Override
    public LocalDateTime fromNonNullString(String s) {
        return LocalDateTime.parse(s);
    }

    @Override
    public LocalDateTime fromNonNullValue(Timestamp value) {
    	
    	ZoneOffset currentDatabaseZone = databaseZone == null ? getDefault() : databaseZone;

        int adjustment = TimeZone.getDefault().getOffset(value.getTime()) - (currentDatabaseZone.getRules().getOffset(LocalDateTime.now()).getTotalSeconds() * MILLIS_IN_SECOND);
        
        Instant instant = Instant.ofEpochMilli(value.getTime() + adjustment);
        instant = instant.with(ChronoField.NANO_OF_SECOND, value.getNanos());
        
        return LocalDateTime.ofInstant(instant, currentDatabaseZone);
    }

    @Override
    public String toNonNullString(LocalDateTime value) {
        return value.toString();
    }

    @Override
    public Timestamp toNonNullValue(LocalDateTime value) {

    	ZoneOffset currentDatabaseZone = databaseZone == null ? getDefault() : databaseZone;
    	
    	ZonedDateTime zdt = value.atZone(currentDatabaseZone);
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
