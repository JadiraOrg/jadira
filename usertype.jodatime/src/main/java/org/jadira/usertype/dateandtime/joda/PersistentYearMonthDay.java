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

import java.sql.Date;

import org.jadira.usertype.dateandtime.joda.columnmapper.DateColumnYearMonthDayMapper;
import org.jadira.usertype.dateandtime.shared.spi.AbstractUserType;
import org.joda.time.YearMonthDay;

/**
 * Persist {@link YearMonthDay} via Hibernate. This type shares database
 * representation with {@link org.joda.time.contrib.hibernate.PersistentYearMonthDay}
 * @deprecated Recommend replacing use of {@link YearMonthDay} with {@link org.joda.time.LocalDate} and {@link PersistentLocalDate}
 */
public class PersistentYearMonthDay extends AbstractUserType<YearMonthDay, Date, DateColumnYearMonthDayMapper> {
}
