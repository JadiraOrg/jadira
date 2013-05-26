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

import org.jadira.usertype.spi.shared.AbstractStringColumnMapper;
import org.threeten.bp.Instant;
import org.threeten.bp.format.DateTimeFormatter;
import org.threeten.bp.format.DateTimeFormatterBuilder;
import org.threeten.bp.temporal.ChronoField;

public class StringColumnInstantMapper extends AbstractStringColumnMapper<Instant> {

    private static final long serialVersionUID = -6885561256539185520L;

    public static final DateTimeFormatter INSTANT_FORMATTER = new DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd'T'HH:mm:ss").appendOptional(new DateTimeFormatterBuilder().appendLiteral('.').appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, false).toFormatter()).appendLiteral('Z').toFormatter();

    @Override
    public Instant fromNonNullValue(String s) {
    	return Instant.parse(s);
    }

    @Override
    public String toNonNullValue(Instant value) {
        return value.toString();
    }
}
