package org.jadira.usertype.dateandtime.joda;

import org.jadira.usertype.dateandtime.joda.columnmapper.StringColumnDateTimeMapper;
import org.jadira.usertype.spi.shared.AbstractParameterizedUserType;
import org.joda.time.DateTime;

/**
 * Persist {@link DateTime} as a string of three parts:
 * <ul>
 * <li>the {@code DateTime} transformed into UTC time, formatted as such: {@code yyyy-MM-dd'T'HH:mm:ss.SSS}</li>
 * <li>the underscore symbol (_)</li>
 * <li>the id of the {@code DateTime}'s original time zone (for example Europe/London or UTC)</li>
 * </ul>
 * This user-type was created to workaround Hibernate's <a href="https://hibernate.atlassian.net/browse/HHH-5574">HHH-5574</a>
 * bug by storing the complete {@code DateTime} data, including the specific time zone, not just the offset (ala ISO 8601), in
 * one single, sortable field.
 */
public class PersistentDateTimeAsUtcString extends AbstractParameterizedUserType<DateTime, String, StringColumnDateTimeMapper> {

    private static final long serialVersionUID = 6477950463426162426L;
}
