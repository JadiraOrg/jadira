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
package org.jadira.usertype.dateandtime.jsr310.columnmapper;

import static org.jadira.usertype.dateandtime.jsr310.utils.ZoneHelper.getDefaultZoneOffset;

import java.sql.Time;

import javax.time.calendar.LocalTime;
import javax.time.calendar.OffsetTime;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatterBuilder;

import org.jadira.usertype.spi.shared.AbstractTimeColumnMapper;

public class TimeColumnOffsetTimeMapper extends AbstractTimeColumnMapper<OffsetTime> {

    private static final long serialVersionUID = 6734385103313158326L;

    public static final DateTimeFormatter LOCAL_TIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("HH:mm:ss").toFormatter();

    private ZoneOffset databaseZone = ZoneOffset.UTC;

    private ZoneOffset javaZone = null;

    @Override
    public OffsetTime fromNonNullString(String s) {
        return OffsetTime.parse(s);
    }

    @Override
    public OffsetTime fromNonNullValue(Time value) {

        ZoneOffset currentDatabaseZone = databaseZone == null ? getDefaultZoneOffset() : databaseZone;
        ZoneOffset currentJavaZone = javaZone == null ? getDefaultZoneOffset() : javaZone;

        LocalTime localTime = LOCAL_TIME_FORMATTER.parse(value.toString()).merge().get(LocalTime.rule());

        OffsetTime time = localTime.atOffset(currentDatabaseZone);
        return time.withOffsetSameInstant(currentJavaZone);
    }

    @Override
    public String toNonNullString(OffsetTime value) {
        return value.toString();
    }

    @Override
    public Time toNonNullValue(OffsetTime value) {

        ZoneOffset currentDatabaseZone = databaseZone == null ? getDefaultZoneOffset() : databaseZone;

        value = value.withOffsetSameInstant(currentDatabaseZone);

        return Time.valueOf(LOCAL_TIME_FORMATTER.print(value));
    }

    public void setDatabaseZone(ZoneOffset databaseZone) {
        this.databaseZone = databaseZone;
    }

    public void setJavaZone(ZoneOffset javaZone) {
        this.javaZone = javaZone;
    }
}
