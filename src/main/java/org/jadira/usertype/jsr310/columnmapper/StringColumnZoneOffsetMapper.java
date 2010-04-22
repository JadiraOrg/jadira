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

import javax.time.calendar.ZoneOffset;

import org.jadira.usertype.jsr310.spi.AbstractStringColumnMapper;


public class StringColumnZoneOffsetMapper extends AbstractStringColumnMapper<ZoneOffset> {

    private static final long serialVersionUID = 982411452349850753L;

    @Override
    public ZoneOffset fromNonNullValue(String s) {
        return ZoneOffset.of(s);
    }

    @Override
    public String toNonNullValue(ZoneOffset value) {
        return value.toString();
    }
}
