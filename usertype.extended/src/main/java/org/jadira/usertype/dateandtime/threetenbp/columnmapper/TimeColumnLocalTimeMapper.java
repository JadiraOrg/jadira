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

import java.sql.Time;

import org.jadira.usertype.spi.shared.DatabaseZoneConfigured;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;

public class TimeColumnLocalTimeMapper extends AbstractTimeThreeTenBPColumnMapper<LocalTime> implements DatabaseZoneConfigured<ZoneOffset> {

    private static final long serialVersionUID = 6734385103313158326L;

    private ZoneOffset databaseZone = null;
    
    public static final DateTimeFormatter LOCAL_TIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("HH:mm:ss").toFormatter();

	public TimeColumnLocalTimeMapper() {
	}

	public TimeColumnLocalTimeMapper(ZoneOffset databaseZone) {
		this.databaseZone = databaseZone;
	}
    
    @Override
    public LocalTime fromNonNullString(String s) {
        return LocalTime.parse(s);
    }

    @Override
    public LocalTime fromNonNullValue(Time value) {
    	
    	ZoneOffset currentDatabaseZone = databaseZone == null ? getDefault() : databaseZone;
        
        ZonedDateTime dateTime = Instant.ofEpochMilli(value.getTime()).atZone(currentDatabaseZone);
        LocalTime localTime = dateTime.toLocalTime();
        
        return localTime;
    }

    @Override
    public String toNonNullString(LocalTime value) {
        return value.toString();
    }

    @Override
    public Time toNonNullValue(LocalTime value) {

    	ZoneOffset currentDatabaseZone = databaseZone == null ? getDefault() : databaseZone;
    	
    	OffsetDateTime zonedValue = LocalDateTime.of(
    				1970, 1, 1, value.getHour(), value.getMinute(), value.getSecond(), value.getNano()
    			).atOffset(currentDatabaseZone);
    	
        final Time time = new Time(zonedValue.toInstant().toEpochMilli());
        return time;
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
