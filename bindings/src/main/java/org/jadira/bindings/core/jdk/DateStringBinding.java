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
import java.util.Date;
import java.util.TimeZone;

import org.jadira.bindings.core.api.Binding;

/**
 * Binds a Date to a String. Date String binding always uses the GMT zone to render.... if you 
 * want control over the zone, use CalendarStringBinding instead or better still use JodaTime or JSR310.
 */
public class DateStringBinding extends AbstractStringBinding<Date> implements Binding<Date, String> {

    private static final ThreadLocal<SimpleDateFormat> DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        public SimpleDateFormat initialValue() {
            final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
            return formatter;
        }
    };

    /**
     * {@inheritDoc}
     */
    @Override
    public Date unmarshal(String object) {

        if (object.length() != 29) {
            throw new IllegalArgumentException("Invalid date: " + object);
        }
        final String result = object.substring(0, 26) + object.substring(27);

        try {
            return DATE_FORMAT.get().parse(result);
        } catch (ParseException ex) {
            throw new IllegalArgumentException("Invalid date: " + object);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String marshal(Date object) {

        String output = DATE_FORMAT.get().format(object);
        return output.substring(0, 26) + ":" + output.substring(26);
    }

    /**
     * {@inheritDoc}
     */
    /* @Override */
	public Class<Date> getBoundClass() {
		return Date.class;
	}
}
