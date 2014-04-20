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

import org.jadira.usertype.spi.shared.AbstractIntegerColumnMapper;
import org.threeten.bp.Duration;

public class IntegerColumnDurationMapper extends AbstractIntegerColumnMapper<Duration> {

    private static final long serialVersionUID = 8408450977695192938L;

    @Override
    public Duration fromNonNullString(String s) {
        return Duration.parse(s);
    }

    @Override
    public Duration fromNonNullValue(Integer value) {
        return Duration.ofSeconds(value.intValue());
    }

    @Override
    public String toNonNullString(Duration value) {
        return value.toString();
    }

    @Override
    public Integer toNonNullValue(Duration value) {
        long longValue = value.getSeconds();
        if (longValue < Integer.MIN_VALUE || longValue > Integer.MAX_VALUE) {
            throw new IllegalStateException(longValue + " cannot be cast to int without changing its value.");
        }
        return (int)longValue;
    }
}
