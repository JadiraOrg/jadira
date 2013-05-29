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

import java.sql.Timestamp;
import java.util.TimeZone;

import org.jadira.usertype.spi.shared.AbstractTimestampColumnMapper;
import org.jadira.usertype.spi.shared.DatabaseZoneConfigured;
import org.jadira.usertype.spi.shared.JavaZoneConfigured;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.OffsetTime;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;
import org.threeten.bp.temporal.ChronoField;

/**
 * Maps a precise datetime column for storage. The UTC Zone will be used to store the value
 */
public class TimestampColumnOffsetTimeMapper extends AbstractTimestampColumnMapper<OffsetTime> implements DatabaseZoneConfigured<ZoneOffset>, JavaZoneConfigured<ZoneOffset> {

    private static final long serialVersionUID = -7670411089210984705L;

    public static final DateTimeFormatter LOCAL_TIME_PRINTER = new DateTimeFormatterBuilder().appendPattern("0001-01-01 HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, false).toFormatter();
    public static final DateTimeFormatter LOCAL_TIME_PARSER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, false).toFormatter();

	private static final int MILLIS_IN_SECOND = 1000;

    private ZoneOffset databaseZone = ZoneOffset.UTC;

    private ZoneOffset javaZone = null;

	public TimestampColumnOffsetTimeMapper() {
	}

	public TimestampColumnOffsetTimeMapper(ZoneOffset databaseZone, ZoneOffset javaZone) {
		this.databaseZone = databaseZone;
		this.javaZone = javaZone;
	}
	
    @Override
    public OffsetTime fromNonNullString(String s) {
        return OffsetTime.parse(s);
    }

    @Override
    public OffsetTime fromNonNullValue(Timestamp value) {
    	

        ZoneOffset currentDatabaseZone = databaseZone == null ? getDefaultZoneOffset() : databaseZone;
        ZoneOffset currentJavaZone = javaZone == null ? getDefaultZoneOffset() : javaZone;

        int adjustment = TimeZone.getDefault().getOffset(value.getTime()) - (currentDatabaseZone.getTotalSeconds() * MILLIS_IN_SECOND);
        
        OffsetDateTime dateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(value.getTime() + adjustment), currentDatabaseZone);
        dateTime = dateTime.with(ChronoField.NANO_OF_SECOND, value.getNanos()).withOffsetSameInstant(currentJavaZone);

        OffsetTime time = dateTime.toOffsetTime();
        return time;
    }

    @Override
    public String toNonNullString(OffsetTime value) {
        return value.toString();
    }

    @Override
    public Timestamp toNonNullValue(OffsetTime value) {

    	OffsetDateTime odt = value.atDate(LocalDate.of(1970, 1, 1));
        
    	ZoneOffset currentDatabaseZone = databaseZone == null ? getDefaultZoneOffset() : databaseZone;        
        int adjustment = TimeZone.getDefault().getOffset(odt.toEpochSecond()) - (currentDatabaseZone.getTotalSeconds() * MILLIS_IN_SECOND);
        
        final Timestamp timestamp = new Timestamp((odt.toEpochSecond() * MILLIS_IN_SECOND) - adjustment);
        timestamp.setNanos(value.getNano());
        return timestamp;
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
