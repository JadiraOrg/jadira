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

import java.time.LocalTime;

import org.jadira.usertype.spi.shared.AbstractIntegerColumnMapper;

public class IntegerColumnLocalTimeMapper extends AbstractIntegerColumnMapper<LocalTime> {

    private static final long serialVersionUID = -3448788221055335510L;

    @Override
    public LocalTime fromNonNullString(String s) {
        return LocalTime.parse(s);
    }

    @Override
    public LocalTime fromNonNullValue(Integer value) {
        final long nanos = value * 1000000L;
        return LocalTime.ofNanoOfDay(nanos);
    }

    @Override
    public String toNonNullString(LocalTime value) {
        return value.toString();
    }

    @Override
    public Integer toNonNullValue(LocalTime value) {
        final Integer integer = (int) (value.toNanoOfDay() / 1000000);
        return integer;
    }
}
