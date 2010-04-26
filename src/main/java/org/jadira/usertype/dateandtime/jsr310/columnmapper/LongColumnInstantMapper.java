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
package org.jadira.usertype.dateandtime.jsr310.columnmapper;

import org.jadira.usertype.dateandtime.shared.spi.AbstractLongColumnMapper;
import javax.time.Instant;

public class LongColumnInstantMapper extends AbstractLongColumnMapper<Instant> {

    private static final long serialVersionUID = 8408450977695192938L;

    @Override
    public Instant fromNonNullString(String s) {
        return Instant.parse(s);
    }

    @Override
    public Instant fromNonNullValue(Long value) {
        return Instant.millis(value);
    }

    @Override
    public String toNonNullString(Instant value) {
        return value.toString();
    }

    @Override
    public Long toNonNullValue(Instant value) {
        return value.toEpochMillisLong();
    }
}
