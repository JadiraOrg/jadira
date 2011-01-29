/*
 *  Copyright 2010 Christopher Pheby
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

import org.jadira.usertype.dateandtime.shared.spi.AbstractStringColumnMapper;
import javax.time.Instant;
import javax.time.calendar.LocalDateTime;
import javax.time.calendar.ZoneOffset;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatterBuilder;

public class StringColumnInstantMapper extends AbstractStringColumnMapper<Instant> {

    private static final long serialVersionUID = -6885561256539185520L;

    public static final DateTimeFormatter INSTANT_FORMATTER = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-ddTHH:mm:ss").appendOptional(new DateTimeFormatterBuilder().appendPattern("ffn").toFormatter()).appendLiteral('Z').toFormatter();
    
    @Override
    public Instant fromNonNullValue(String s) {
        // Instant.parse(s) is currently not yet implemented;
        return INSTANT_FORMATTER.parse(s).merge().get(LocalDateTime.rule()).atOffset(ZoneOffset.UTC).toInstant();
    }

    @Override
    public String toNonNullValue(Instant value) {
        return value.toString();
    }
}
