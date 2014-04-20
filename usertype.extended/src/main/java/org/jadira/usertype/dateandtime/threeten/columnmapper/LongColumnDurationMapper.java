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

import java.math.BigInteger;
import java.time.Duration;

import org.jadira.usertype.spi.shared.AbstractLongColumnMapper;

public class LongColumnDurationMapper extends AbstractLongColumnMapper<Duration> {

    private static final long serialVersionUID = 8408450977695192938L;

    private static final BigInteger NANOS_IN_MILLI = BigInteger.valueOf(1_000_000L);
    
    private static final BigInteger MILLIS_IN_SECOND = BigInteger.valueOf(1_000L);
    
    @Override
    public Duration fromNonNullString(String s) {
        return Duration.parse(s);
    }

    @Override
    public Duration fromNonNullValue(Long value) {
        return Duration.ofMillis(value);
    }

    @Override
    public String toNonNullString(Duration value) {
        return value.toString();
    }

    @Override
    public Long toNonNullValue(Duration value) {
        BigInteger millisValue = BigInteger.valueOf(value.getNano()).divide(NANOS_IN_MILLI).add(BigInteger.valueOf(value.getSeconds()).multiply(MILLIS_IN_SECOND));
        return Long.valueOf(millisValue.longValueExact());
    }
}
