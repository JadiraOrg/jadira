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

import java.sql.Date;
import java.util.TimeZone;

import org.jadira.usertype.spi.shared.AbstractDateColumnMapper;
import org.jadira.usertype.spi.shared.DatabaseZoneConfigured;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.ZonedDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;

public class DateColumnLocalDateMapper extends AbstractDateColumnMapper<LocalDate> implements DatabaseZoneConfigured<ZoneOffset> {

    private static final long serialVersionUID = 6734385103313158326L;

    public static final DateTimeFormatter LOCAL_DATE_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").toFormatter();

	private static final int MILLIS_IN_SECOND = 1000;

    /* Explicitly set this to null for preferred default behaviour. See https://jadira.atlassian.net/browse/JDF-26 */
    private ZoneOffset databaseZone = null;
    
	public DateColumnLocalDateMapper() {
	}

	public DateColumnLocalDateMapper(ZoneOffset databaseZone) {
		this.databaseZone = databaseZone;
	}
    
    @Override
    public LocalDate fromNonNullString(String s) {
        return LocalDate.parse(s);
    }

    @Override
    public LocalDate fromNonNullValue(Date value) {

		if (databaseZone == null) {
    		return LocalDate.parse(value.toString(), LOCAL_DATE_FORMATTER);
    	}

		ZoneOffset currentDatabaseZone = databaseZone == null ? getDefault() : databaseZone;
        
		int adjustment = TimeZone.getDefault().getOffset(value.getTime()) - (currentDatabaseZone.getRules().getOffset(LocalDateTime.now()).getTotalSeconds() * MILLIS_IN_SECOND);
        
        ZonedDateTime dateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(value.getTime() + adjustment), currentDatabaseZone);
        LocalDate localDate = dateTime.toLocalDate();
        
        return localDate;
    }

    @Override
    public String toNonNullString(LocalDate value) {
        return value.toString();
    }

    @Override
    public Date toNonNullValue(LocalDate value) {
    	
        if (databaseZone==null) {
        	return Date.valueOf(LOCAL_DATE_FORMATTER.format((LocalDate) value));
        }
    	
		ZoneOffset currentDatabaseZone = databaseZone == null ? getDefault() : databaseZone;
    	ZonedDateTime zonedValue = value.atStartOfDay(currentDatabaseZone);
        
        int adjustment = TimeZone.getDefault().getOffset(zonedValue.toEpochSecond() * MILLIS_IN_SECOND) - (currentDatabaseZone.getRules().getOffset(LocalDateTime.now()).getTotalSeconds() * MILLIS_IN_SECOND);
    	
        final Date date = new Date(zonedValue.toEpochSecond() - adjustment);
        return date;
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
