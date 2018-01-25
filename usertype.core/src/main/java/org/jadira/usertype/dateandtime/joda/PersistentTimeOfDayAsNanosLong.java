/*
 *  Copyright 2010, 2011 Christopher Pheby
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

import org.jadira.usertype.dateandtime.joda.columnmapper.LongColumnTimeOfDayMapper;
import org.jadira.usertype.spi.shared.AbstractSingleColumnUserType;
import org.joda.time.TimeOfDay;

/**
 * Persist {@link TimeOfDay} via Hibernate using nanoseconds of the day. This class is INCOMPATIBLE with
 * Joda Time's org.joda.time.contrib.hibernate.PersistentLocalTimeExact because that class uses a millisecond rather than nanosecond
 * absolute value. For compatibility use {@link PersistentLocalTimeAsMillisInteger}
 * @see PersistentLocalTimeAsMillisInteger
 * @deprecated Recommend replacing use of {@link TimeOfDay} with {@link org.joda.time.LocalTime} and {@link PersistentLocalTimeAsNanosLong}
 */
@Deprecated
public class PersistentTimeOfDayAsNanosLong extends AbstractSingleColumnUserType<TimeOfDay, Long, LongColumnTimeOfDayMapper> {

    private static final long serialVersionUID = 8364584846390296447L;
}
