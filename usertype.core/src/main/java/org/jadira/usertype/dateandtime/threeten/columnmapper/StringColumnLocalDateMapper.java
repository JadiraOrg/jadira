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

import java.time.LocalDate;

import org.jadira.usertype.spi.shared.AbstractStringColumnMapper;

public class StringColumnLocalDateMapper extends AbstractStringColumnMapper<LocalDate> {

    private static final long serialVersionUID = -6885561256539185520L;

    @Override
    public LocalDate fromNonNullValue(String s) {
        return LocalDate.parse(s);
    }

    @Override
    public String toNonNullValue(LocalDate value) {
        return value.toString();
    }
}
