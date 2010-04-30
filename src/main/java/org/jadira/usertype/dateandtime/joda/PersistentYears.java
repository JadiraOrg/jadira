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

import javax.time.calendar.Year;

import org.jadira.usertype.dateandtime.shared.spi.AbstractUserType;
import org.joda.time.Years;

import org.jadira.usertype.dateandtime.joda.columnmapper.IntegerColumnYearsMapper;

/**
 * Persist {@link Year} via Hibernate using integer value.
 */
public class PersistentYears extends AbstractUserType<Years, Integer, IntegerColumnYearsMapper> {
}
