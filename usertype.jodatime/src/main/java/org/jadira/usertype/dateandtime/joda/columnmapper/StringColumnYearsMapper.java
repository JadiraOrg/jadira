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
import org.joda.time.Years;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class StringColumnYearsMapper extends AbstractStringColumnMapper<Years> {

    private static final long serialVersionUID = -7158493703736747997L;

    public static final DateTimeFormatter YEAR_FORMATTER = new DateTimeFormatterBuilder().appendPattern("MMM").toFormatter();
    
    @Override
    public Years fromNonNullValue(String value) {
        return Years.years(YEAR_FORMATTER.parseDateTime(value).getYear());
    }

    @Override
    public String toNonNullValue(Years value) {
        return YEAR_FORMATTER.print(value.getYears());
    }
}
