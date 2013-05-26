/*
 *  Copyright 2012 Christopher Pheby
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
package org.jadira.usertype.dateandtime.joda.util;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public final class Formatter {

    private Formatter() {
    }
    
    public static final DateTimeFormatter LOCAL_TIME_FORMATTER = new DateTimeFormatterBuilder().appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).appendLiteral(':').appendSecondOfMinute(2)
    .appendOptional(new DateTimeFormatterBuilder().appendLiteral('.').appendFractionOfSecond(3, 9).toParser()).toFormatter();

    public static final DateTimeFormatter LOCAL_TIME_PRINTER = new DateTimeFormatterBuilder().appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).appendLiteral(':').appendSecondOfMinute(2)
    .appendLiteral('.').appendFractionOfSecond(3, 9).toFormatter();
    public static final DateTimeFormatter LOCAL_TIME_NOSECONDS_PRINTER = new DateTimeFormatterBuilder().appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).toFormatter();
    public static final DateTimeFormatter LOCAL_TIME_NOMILLIS_PRINTER = new DateTimeFormatterBuilder().appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).appendLiteral(':').appendSecondOfMinute(2).toFormatter();
    
    public static final DateTimeFormatter LOCAL_TIME_TIMESTAMP_PRINTER = new DateTimeFormatterBuilder().appendPattern("0001-01-01 HH:mm:ss'.'").appendFractionOfSecond(0, 9).toFormatter();
    public static final DateTimeFormatter LOCAL_TIME_TIMESTAMP_FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss'.'").appendFractionOfSecond(0, 9).toFormatter();
}
