package org.jadira.usertype.dateandtime.joda.util;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class Formatter {

    private Formatter() {
    }
    
    public static final DateTimeFormatter LOCAL_TIME_FORMATTER = new DateTimeFormatterBuilder().appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).appendLiteral(':').appendSecondOfMinute(2)
    .appendOptional(new DateTimeFormatterBuilder().appendLiteral('.').appendFractionOfSecond(3, 9).toParser()).toFormatter();

    public static final DateTimeFormatter LOCAL_TIME_NOSECONDS_FORMATTER = new DateTimeFormatterBuilder().appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).toFormatter();
    
    public static final DateTimeFormatter LOCAL_DATETIME_PRINTER = new DateTimeFormatterBuilder().appendPattern("0001-01-01 HH:mm:ss'.'").appendFractionOfSecond(0, 9).toFormatter();
    public static final DateTimeFormatter LOCAL_DATETIME_PARSER = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss'.'").appendFractionOfSecond(0, 9).toFormatter();
    
    public static final DateTimeFormatter LOCAL_DATETIME_FORMATTER = LOCAL_DATETIME_PARSER;
}
