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

import org.jadira.usertype.jsr310.spi.AbstractIntegerColumnMapper;


public class IntegerColumnYearMapper extends AbstractIntegerColumnMapper<Year> {

    private static final long serialVersionUID = 3803107030453775035L;

    @Override
    public Year fromNonNullString(String s) {
        return Year.of(Integer.parseInt(s));
    }

    @Override
    public Year fromNonNullValue(Integer value) {
        return Year.of(value);
    }

    @Override
    public String toNonNullString(Year value) {
        return "" + value.getValue();
    }

    @Override
    public Integer toNonNullValue(Year value) {
        return value.getValue();
    }
}
