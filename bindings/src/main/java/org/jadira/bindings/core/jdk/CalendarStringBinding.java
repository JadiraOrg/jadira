/*
 *  Copyright 2010, 2011 Chris Pheby
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
package org.jadira.bindings.core.jdk;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.jadira.bindings.core.api.Binding;
import org.jadira.bindings.core.api.BindingException;

/**
 * Binds a Calendar to a String
 */
public class CalendarStringBinding extends AbstractStringBinding<Calendar> implements Binding<Calendar, String> {

    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        public SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public Calendar unmarshal(String object) {

        if (object.length() < 31 || object.charAt(26) != ':' || object.charAt(29) != '['
                || object.charAt(object.length() - 1) != ']') {
            throw new IllegalArgumentException("Unable to parse calendar: " + object);
        }
        TimeZone zone = TimeZone.getTimeZone(object.substring(30, object.length() - 1));

        String parseableDateString = object.substring(0, 26) + object.substring(27, 29);

        GregorianCalendar cal = new GregorianCalendar(zone);
        cal.setTimeInMillis(0);
        DATE_FORMAT.get().setCalendar(cal);

        try {
            DATE_FORMAT.get().parseObject(parseableDateString);
            return DATE_FORMAT.get().getCalendar();
        } catch (ParseException ex) {
            throw new BindingException(ex.getMessage(), ex);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String marshal(Calendar object) {

        if (object instanceof GregorianCalendar) {

            GregorianCalendar cal = (GregorianCalendar) object;

            DATE_FORMAT.get().setCalendar(cal);
            String str = DATE_FORMAT.get().format(cal.getTime());

            String printableDateString = str.substring(0, 26) + ":" + str.substring(26) + "["
                    + cal.getTimeZone().getID() + "]";
            return printableDateString;
        } else {
            throw new IllegalArgumentException("CalendarStringBinding can only support "
                    + GregorianCalendar.class.getSimpleName());
        }
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public Class<Calendar> getBoundClass() {
		return Calendar.class;
	}
}
