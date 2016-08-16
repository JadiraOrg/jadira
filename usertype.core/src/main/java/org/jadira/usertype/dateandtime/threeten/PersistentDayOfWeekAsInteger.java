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
package org.jadira.usertype.dateandtime.threeten;

import java.time.DayOfWeek;

import org.jadira.usertype.dateandtime.threeten.columnmapper.IntegerColumnDayOfWeekMapper;
import org.jadira.usertype.spi.shared.AbstractSingleColumnUserType;

/**
 * Persist {@link DayOfWeek} via Hibernate using integer value.
 */
public class PersistentDayOfWeekAsInteger extends AbstractSingleColumnUserType<DayOfWeek, Integer, IntegerColumnDayOfWeekMapper> {

    private static final long serialVersionUID = 4694981953643179773L;
}
