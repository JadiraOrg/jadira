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

import java.math.BigInteger;

import org.jadira.usertype.spi.shared.AbstractVersionableBigIntegerColumnMapper;
import org.threeten.bp.Instant;

public class BigIntegerColumnInstantMapper extends AbstractVersionableBigIntegerColumnMapper<Instant> {

    private static final long serialVersionUID = 2647692721818989190L;
	
    private static final BigInteger NANOS_IN_MILLI = BigInteger.valueOf(1000000L);

	private static final BigInteger MILLIS_IN_SECOND = BigInteger.valueOf(1000L);

    @Override
    public Instant fromNonNullString(String s) {
        return Instant.parse(s);
    }

    @Override
    public Instant fromNonNullValue(BigInteger value) {
    	
    	BigInteger[] milliAndNanoPart = value.divideAndRemainder(NANOS_IN_MILLI);
        Instant result = Instant.ofEpochMilli(milliAndNanoPart[0].longValue());
        return result.plusNanos(milliAndNanoPart[1].longValue());
    }

    @Override
    public String toNonNullString(Instant value) {
        return value.toString();
    }

    @Override
    public BigInteger toNonNullValue(Instant value) {
        BigInteger result = BigInteger.valueOf(value.getEpochSecond()).multiply(NANOS_IN_MILLI).multiply(MILLIS_IN_SECOND);
        return result.add(BigInteger.valueOf(value.getNano()));
    }
}
