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
import org.threeten.bp.DayOfWeek;

public class StringColumnDayOfWeekMapper extends AbstractStringColumnMapper<DayOfWeek> {

    private static final long serialVersionUID = 982411452349850753L;

    @Override
    public DayOfWeek fromNonNullValue(String s) {

        if ("SATURDAY".equals(s)) {
            return DayOfWeek.SATURDAY;
        } else if ("SUNDAY".equals(s)) {
            return DayOfWeek.SUNDAY;
        } else if ("MONDAY".equals(s)) {
            return DayOfWeek.MONDAY;
        } else if ("TUESDAY".equals(s)) {
            return DayOfWeek.TUESDAY;
        } else if ("WEDNESDAY".equals(s)) {
            return DayOfWeek.WEDNESDAY;
        } else if ("THURSDAY".equals(s)) {
            return DayOfWeek.THURSDAY;
        } else if ("FRIDAY".equals(s)) {
            return DayOfWeek.FRIDAY;
        } else {
            throw new IllegalArgumentException("Seen unexpected DayOfWeek: " + s);
        }
    }

    @Override
    public String toNonNullValue(DayOfWeek value) {
        return value.toString();
    }
}
