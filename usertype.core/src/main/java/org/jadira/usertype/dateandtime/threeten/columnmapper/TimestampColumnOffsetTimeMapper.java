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

import static org.jadira.usertype.dateandtime.threeten.utils.ZoneHelper.getDefaultZoneOffset;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

import org.jadira.usertype.spi.shared.DatabaseZoneConfigured;
import org.jadira.usertype.spi.shared.JavaZoneConfigured;

/**
 * Maps a precise datetime column for storage. The UTC Zone will be used to store the value
 */
public class TimestampColumnOffsetTimeMapper extends AbstractTimestampThreeTenColumnMapper<OffsetTime> implements DatabaseZoneConfigured<ZoneId>, JavaZoneConfigured<ZoneId> {

    private static final long serialVersionUID = -7670411089210984705L;

    public static final DateTimeFormatter LOCAL_TIME_PRINTER = new DateTimeFormatterBuilder().appendPattern("0001-01-01 HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, false).toFormatter();
    public static final DateTimeFormatter LOCAL_TIME_PARSER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, false).toFormatter();

	private static final int MILLIS_IN_SECOND = 1000;

    private ZoneId javaZone = null;

	public TimestampColumnOffsetTimeMapper() {
		super();
	}

	public TimestampColumnOffsetTimeMapper(ZoneOffset databaseZone, ZoneOffset javaZone) {
		super(databaseZone);
		this.javaZone = javaZone;
	}
	
    @Override
    public OffsetTime fromNonNullString(String s) {
        return OffsetTime.parse(s);
    }

    @Override
    public OffsetTime fromNonNullValue(Timestamp value) {
    	
        ZoneId currentDatabaseZone = getDatabaseZone() == null ? getDefaultZoneOffset() : getDatabaseZone();
        ZoneId currentJavaZone = javaZone == null ? getDefaultZoneOffset() : javaZone;

        OffsetDateTime dateTime = OffsetDateTime.ofInstant(Instant.ofEpochMilli(value.getTime()), currentDatabaseZone);
        dateTime = dateTime.with(ChronoField.NANO_OF_SECOND, value.getNanos()).atZoneSameInstant(currentJavaZone).toOffsetDateTime();

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
        
        final Timestamp timestamp = new Timestamp((odt.toEpochSecond() * MILLIS_IN_SECOND));
        timestamp.setNanos(value.getNano());
        return timestamp;
    }

    @Override
    public void setJavaZone(ZoneId javaZone) {
        this.javaZone = javaZone;
    }
        
	@Override
	public ZoneOffset parseZone(String zoneString) {
		return ZoneOffset.of(zoneString);
	}		
}
