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
package org.jadira.usertype.dateandtime.jsr310.columnmapper;

import javax.time.calendar.Period;

import org.jadira.usertype.spi.shared.AbstractStringColumnMapper;

public class StringColumnPeriodMapper extends AbstractStringColumnMapper<Period> {

    private static final long serialVersionUID = -5741261927204773374L;

    @Override
    public Period fromNonNullValue(String s) {
        return Period.parse(s);
    }

    @Override
    public String toNonNullValue(Period value) {
        return toString((Period) value);
    }

    // Workaround defect in EA revision of JSR 310
    // See https://jsr-310.dev.java.net/servlets/ReadMsg?list=dev&msgNo=2117

    /**
     * Returns a string representation of the amount of time.
     * @return the amount of time in ISO8601 string format
     */
    public String toString(Period value) {

        final String str;
        if (value.isZero()) {
            str = "PT0S";
        } else {
            StringBuilder buf = new StringBuilder();
            buf.append('P');
            if (value.getYears() != 0) {
                buf.append(value.getYears()).append('Y');
            }
            if (value.getMonths() != 0) {
                buf.append(value.getMonths()).append('M');
            }
            if (value.getDays() != 0) {
                buf.append(value.getDays()).append('D');
            }
            if ((value.getHours() | value.getMinutes() | value.getSeconds()) != 0 || value.getNanos() != 0) {
                buf.append('T');
                if (value.getHours() != 0) {
                    buf.append(value.getHours()).append('H');
                }
                if (value.getMinutes() != 0) {
                    buf.append(value.getMinutes()).append('M');
                }
                if (value.getSeconds() != 0 || value.getNanos() != 0) {
                    if (value.getNanos() == 0) {
                        buf.append(value.getSeconds()).append('S');
                    } else {
                        long s = value.getSeconds() + (value.getNanos() / 1000000000);
                        long n = value.getNanos() % 1000000000;
                        if (s < 0 && n > 0) {
                            n -= 1000000000;
                            s++;
                        } else if (s > 0 && n < 0) {
                            n += 1000000000;
                            s--;
                        }
                        if (n < 0) {
                            n = -n;
                            if (s == 0) {
                                buf.append('-');
                            }
                        }
                        buf.append(s).append('.').append(String.format("%09d", n));
                        while (buf.charAt(buf.length() - 1) == '0') {
                            buf.setLength(buf.length() - 1);
                        }
                        buf.append('S');
                    }
                }
            }
            str = buf.toString();
        }
        return str;
    }
}
