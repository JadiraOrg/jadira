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
package org.jadira.usertype.dateandtime.joda;

import org.jadira.usertype.dateandtime.joda.columnmapper.StringColumnTimeOfDayMapper;
import org.jadira.usertype.dateandtime.shared.spi.AbstractSingleColumnUserType;
import org.joda.time.TimeOfDay;

/**
 * Persist {@link TimeOfDay} via Hibernate. This type is
 * mostly compatible with {@link org.joda.time.contrib.hibernate.PersistentTimeOfDayAsString} however
 * you should note that JodaTime's {@link org.joda.time.TimeOfDay} has only millisecond precision,
 * whilst JSR 310 offers nanosecond precision. When interpreting nanosecond values, Joda Time will
 * round down to the nearest millisecond.
 * @deprecated Recommend replacing use of {@link TimeOfDay} with {@link org.joda.time.LocalTime} and {@link PersistentLocalTimeAsString}
 */
public class PersistentTimeOfDayAsString extends AbstractSingleColumnUserType<TimeOfDay, String, StringColumnTimeOfDayMapper> {

    private static final long serialVersionUID = -6895579990780796242L;
}
