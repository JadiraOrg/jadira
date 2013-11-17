package org.jadira.usertype.dateandtime.joda.columnmapper;

import org.jadira.usertype.spi.shared.AbstractStringColumnMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

/**
 * Maps {@link DateTime} as one single, sorted string. See
 * {@link org.jadira.usertype.dateandtime.joda.PersistentDateTimeAsUtcString PersistentDateTimeAsUtcString} for more details.
 */
public class StringColumnDateTimeMapper extends AbstractStringColumnMapper<DateTime> {

    private static final long serialVersionUID = -2548824513686423324L;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
        .append(ISODateTimeFormat.date())
        .appendLiteral('T')
        .append(ISODateTimeFormat.hourMinuteSecondFraction())
        .appendLiteral('_')
        .appendTimeZoneId()
        .toFormatter();

    @Override
    public DateTime fromNonNullValue(String s) {

        DateTime parsedDateTime = DATE_TIME_FORMATTER.parseDateTime(s);
        DateTimeZone correctTimeZone = parsedDateTime.getZone();
        DateTime utcDateTime = parsedDateTime.withZoneRetainFields(DateTimeZone.UTC);
        DateTime correctedDateTime = utcDateTime.withZone(correctTimeZone);
        return correctedDateTime;
    }

    @Override
    public String toNonNullValue(DateTime value) {

        DateTimeZone correctTimeZone = value.getZone();
        DateTime utcDateTime = value.withZone(DateTimeZone.UTC);
        DateTime utcDateTimeWithCorrectTimeZone = utcDateTime.withZoneRetainFields(correctTimeZone);
        String dateTimeAsString = DATE_TIME_FORMATTER.print(utcDateTimeWithCorrectTimeZone);
        return dateTimeAsString;
    }
}
