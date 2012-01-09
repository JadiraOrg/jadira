package org.jadira.usertype.dateandtime.joda.util;

import org.joda.time.DateTimeZone;

public class ZoneHelper {

    private ZoneHelper() {
    }
    
    public static DateTimeZone getDefault() {

        DateTimeZone zone = null;
        try {
            try {
                String id = System.getProperty("user.timezone");
                if (id != null) {
                    zone = DateTimeZone.forID(id);
                }
            } catch (RuntimeException ex) {
                zone = null;
            }
            if (zone == null) {
                zone = DateTimeZone.forID(java.util.TimeZone.getDefault().getID());
            }
        } catch (RuntimeException ex) {
            zone = null;
        }
        if (zone == null) {
            zone = DateTimeZone.UTC;
        }
        return zone;
    }
}
