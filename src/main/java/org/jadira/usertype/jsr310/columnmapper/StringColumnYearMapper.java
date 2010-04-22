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
package org.jadira.usertype.jsr310.columnmapper;

import javax.time.calendar.Year;
import javax.time.calendar.format.DateTimeFormatter;
import javax.time.calendar.format.DateTimeFormatterBuilder;

import org.jadira.usertype.jsr310.spi.AbstractStringColumnMapper;


public class StringColumnYearMapper extends AbstractStringColumnMapper<Year> {

    private static final long serialVersionUID = -7158493703736747997L;

    public static final DateTimeFormatter YEAR_FORMATTER = new DateTimeFormatterBuilder().appendPattern("MMM").toFormatter();
    
    @Override
    public Year fromNonNullValue(String value) {
        return Year.of(YEAR_FORMATTER.parse(value).merge().get(Year.rule()));
    }

    @Override
    public String toNonNullValue(Year value) {
        return YEAR_FORMATTER.print((Year) value);
    }
}
