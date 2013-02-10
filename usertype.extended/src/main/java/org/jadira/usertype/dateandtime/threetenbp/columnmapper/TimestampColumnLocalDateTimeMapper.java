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

import org.jadira.usertype.spi.shared.AbstractTimestampColumnMapper;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;
import org.threeten.bp.temporal.ChronoField;


public class TimestampColumnLocalDateTimeMapper extends AbstractTimestampColumnMapper<LocalDateTime> {

    private static final long serialVersionUID = -7670411089210984705L;

    public static final DateTimeFormatter LOCAL_DATETIME_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss").appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, false).toFormatter();

    @Override
    public LocalDateTime fromNonNullString(String s) {
        return LocalDateTime.parse(s);
    }

    @Override
    public LocalDateTime fromNonNullValue(Timestamp value) {
        return LocalDateTime.parse(value.toString(), LOCAL_DATETIME_FORMATTER);
    }

    @Override
    public String toNonNullString(LocalDateTime value) {
        return value.toString();
    }

    @Override
    public Timestamp toNonNullValue(LocalDateTime value) {

        final String formattedTimestamp = LOCAL_DATETIME_FORMATTER.print((LocalDateTime) value);
        final Timestamp timestamp = Timestamp.valueOf(formattedTimestamp);

        return timestamp;
    }
}
