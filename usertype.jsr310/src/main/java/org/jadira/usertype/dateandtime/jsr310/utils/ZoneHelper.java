/*
 *  Copyright 2010 Christopher Pheby
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
package org.jadira.usertype.dateandtime.jsr310.utils;

import javax.time.calendar.TimeZone;
import javax.time.calendar.ZoneOffset;

public class ZoneHelper {

    private ZoneHelper() {}
    
    public static ZoneOffset getDefaultZoneOffset() {

        ZoneOffset zone = null;
        try {
            try {
                String id = System.getProperty("user.timezone");
                if (id != null) {
                    zone = ZoneOffset.of(id);
                }
            } catch (RuntimeException ex) { }
            if (zone == null) {
               zone = ZoneOffset.ofTotalSeconds(java.util.TimeZone.getDefault().getOffset(System.currentTimeMillis()) / 1000);
            }
        } catch (RuntimeException ex) { }
        if (zone == null) {
            zone = ZoneOffset.UTC;
        }
        return zone;
    }
        
    public static TimeZone getDefaultTimeZone() {

        TimeZone zone = null;
        try {
            try {
                String id = System.getProperty("user.timezone");
                if (id != null) {
                    zone = TimeZone.of(id);
                }
            } catch (RuntimeException ex) { }
            if (zone == null) {
                zone = TimeZone.of(java.util.TimeZone.getDefault().getID());
            }
        } catch (RuntimeException ex) { }
        if (zone == null) {
            zone = TimeZone.UTC;
        }
        return zone;
    }

}
