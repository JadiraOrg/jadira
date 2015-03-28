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

import java.math.BigDecimal;

import org.jadira.usertype.spi.shared.AbstractBigDecimalColumnMapper;
import org.threeten.bp.Duration;

public class BigDecimalColumnDurationMapper extends AbstractBigDecimalColumnMapper<Duration> {

    private static final long serialVersionUID = 8408450977695192938L;

    private static final BigDecimal NANOS_IN_SECOND = BigDecimal.valueOf(1000000000L);
    
    @Override
    public Duration fromNonNullString(String s) {
        return Duration.parse(s);
    }

    @Override
    public Duration fromNonNullValue(BigDecimal value) {
        
        StringBuilder durationPattern = new StringBuilder();
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            durationPattern.append('-');
        }
        durationPattern.append("PT");
        durationPattern.append(value.abs().toPlainString());
        durationPattern.append("S");
        
        return Duration.parse(durationPattern.toString());
    }

    @Override
    public String toNonNullString(Duration value) {
        return value.toString();
    }

    @Override
    public BigDecimal toNonNullValue(Duration value) {
        return BigDecimal.valueOf(value.getSeconds()).add(BigDecimal.valueOf(value.getNano()).divide(NANOS_IN_SECOND));
    }
}
