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
package org.jadira.usertype.dateandtime.joda.columnmapper;

import org.jadira.usertype.dateandtime.shared.spi.AbstractStringColumnMapper;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class StringColumnLocalDateTimeMapper extends AbstractStringColumnMapper<LocalDateTime> {
    
    private static final long serialVersionUID = -6885561256539185520L;

    public static final DateTimeFormatter LOCAL_DATETIME_FORMATTER = ISODateTimeFormat.localDateOptionalTimeParser();
    
    @Override
    public LocalDateTime fromNonNullValue(String s) { 
        return LOCAL_DATETIME_FORMATTER.parseDateTime(s).toLocalDateTime();
    }

    @Override
    public String toNonNullValue(LocalDateTime value) {

        String formatted = LOCAL_DATETIME_FORMATTER.print(value);
        return formatted;
    }
}
