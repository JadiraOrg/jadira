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
package org.jadira.usertype.dateandtime.joda.columnmapper;

import org.jadira.usertype.spi.shared.AbstractIntegerColumnMapper;
import org.joda.time.Minutes;

public class IntegerColumnMinutesMapper extends AbstractIntegerColumnMapper<Minutes> {

    private static final long serialVersionUID = 3803107030453775035L;

    @Override
    public Minutes fromNonNullString(String s) {
        return Minutes.minutes(Integer.parseInt(s));
    }

    @Override
    public Minutes fromNonNullValue(Integer value) {
        return Minutes.minutes(value);
    }

    @Override
    public String toNonNullString(Minutes value) {
        return "" + value.getMinutes();
    }

    @Override
    public Integer toNonNullValue(Minutes value) {
        return value.getMinutes();
    }
}
