/*
 *  Copyright 2013 Christopher Pheby
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

import org.jadira.usertype.dateandtime.joda.columnmapper.StringColumnDateTimeMapper;
import org.jadira.usertype.spi.shared.AbstractParameterizedTemporalUserType;
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
 * @author dwijnand
 */
public class PersistentDateTimeAsUtcString extends AbstractParameterizedTemporalUserType<DateTime, String, StringColumnDateTimeMapper> {

    private static final long serialVersionUID = 6477950463426162426L;
}
