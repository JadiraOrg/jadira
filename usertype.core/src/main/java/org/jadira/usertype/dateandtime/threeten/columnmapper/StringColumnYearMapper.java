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

import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import org.jadira.usertype.spi.shared.AbstractStringColumnMapper;

public class StringColumnYearMapper extends AbstractStringColumnMapper<Year> {

    private static final long serialVersionUID = -7158493703736747997L;

    public static final DateTimeFormatter YEAR_FORMATTER = new DateTimeFormatterBuilder().appendPattern("MMM").toFormatter();

    @Override
    public Year fromNonNullValue(String value) {
        return Year.parse(value, YEAR_FORMATTER);
    }

    @Override
    public String toNonNullValue(Year value) {
        return YEAR_FORMATTER.format((Year) value);
    }
}
