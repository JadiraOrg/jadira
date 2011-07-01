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

import java.math.BigInteger;

import org.jadira.usertype.dateandtime.shared.spi.AbstractVersionableBigIntegerColumnMapper;
import org.joda.time.Instant;

public class BigIntegerColumnInstantMapper extends AbstractVersionableBigIntegerColumnMapper<Instant> {

    private static final long serialVersionUID = 8408450977695192938L;

    @Override
    public Instant fromNonNullString(String s) {
        return new Instant(s);
    }

    @Override
    public Instant fromNonNullValue(BigInteger value) {
        return new Instant(value.divide(BigInteger.valueOf(1000000L)).longValue());
    }

    @Override
    public String toNonNullString(Instant value) {
        return value.toString();
    }

    @Override
    public BigInteger toNonNullValue(Instant value) {
        return BigInteger.valueOf(value.getMillis()).multiply(BigInteger.valueOf(1000000L));
    }
}
