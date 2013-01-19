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
import org.threeten.bp.DayOfWeek;

public class IntegerColumnDayOfWeekMapper extends AbstractIntegerColumnMapper<DayOfWeek> {

    private static final long serialVersionUID = 3803107030453775035L;

    @Override
    public DayOfWeek fromNonNullString(String s) {
        return DayOfWeek.of(Integer.parseInt(s));
    }

    @Override
    public DayOfWeek fromNonNullValue(Integer value) {
        return DayOfWeek.of(value);
    }

    @Override
    public String toNonNullString(DayOfWeek value) {
        return "" + value.getValue();
    }

    @Override
    public Integer toNonNullValue(DayOfWeek value) {
        return value.getValue();
    }
}
