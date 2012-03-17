package org.jadira.usertype.dateandtime.joda.util;

import org.joda.time.DateTimeZone;

public class ZoneHelper {

    private ZoneHelper() {
    }
    

    public static DateTimeZone getDefault() {
        return DateTimeZone.getDefault();
    }
}
