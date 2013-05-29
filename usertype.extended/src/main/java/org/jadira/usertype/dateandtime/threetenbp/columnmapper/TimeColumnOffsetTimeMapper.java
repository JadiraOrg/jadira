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

import static org.jadira.usertype.dateandtime.threetenbp.utils.ZoneHelper.getDefaultZoneOffset;

import java.sql.Time;
import java.util.TimeZone;

import org.jadira.usertype.spi.shared.AbstractTimeColumnMapper;
import org.jadira.usertype.spi.shared.DatabaseZoneConfigured;
import org.jadira.usertype.spi.shared.JavaZoneConfigured;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.OffsetTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;

public class TimeColumnOffsetTimeMapper extends AbstractTimeColumnMapper<OffsetTime> implements DatabaseZoneConfigured<ZoneOffset>, JavaZoneConfigured<ZoneOffset> {

    private static final long serialVersionUID = 6734385103313158326L;

    public static final DateTimeFormatter LOCAL_TIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("HH:mm:ss").toFormatter();

	private static final int MILLIS_IN_SECOND = 1000;

    private ZoneOffset databaseZone = ZoneOffset.UTC;

    private ZoneOffset javaZone = null;

	public TimeColumnOffsetTimeMapper() {
	}

	public TimeColumnOffsetTimeMapper(ZoneOffset databaseZone, ZoneOffset javaZone) {
		this.databaseZone = databaseZone;
		this.javaZone = javaZone;
	}
    
    @Override
    public OffsetTime fromNonNullString(String s) {
        return OffsetTime.parse(s);
    }

    @Override
    public OffsetTime fromNonNullValue(Time value) {

    	ZoneOffset currentDatabaseZone = databaseZone == null ? getDefault() : databaseZone;
        
    	int adjustment = TimeZone.getDefault().getOffset(value.getTime()) - (currentDatabaseZone.getRules().getOffset(LocalDateTime.now()).getTotalSeconds() * MILLIS_IN_SECOND);
        
        OffsetDateTime dateTime = Instant.ofEpochMilli(value.getTime() + adjustment).atOffset(currentDatabaseZone);
        return dateTime.toOffsetTime().withOffsetSameInstant(javaZone);
    }

    @Override
    public String toNonNullString(OffsetTime value) {
        return value.toString();
    }

    @Override
    public Time toNonNullValue(OffsetTime value) {

        ZoneOffset currentDatabaseZone = databaseZone == null ? getDefaultZoneOffset() : databaseZone;

        final OffsetTime adjustedValue = value.withOffsetSameInstant(currentDatabaseZone);

        return Time.valueOf(LOCAL_TIME_FORMATTER.format(adjustedValue));
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
    public void setJavaZone(ZoneOffset javaZone) {
        this.javaZone = javaZone;
    }
        
	@Override
	public ZoneOffset parseZone(String zoneString) {
		return ZoneOffset.of(zoneString);
	}
}
