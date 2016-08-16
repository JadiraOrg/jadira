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

import java.sql.Time;
import java.time.LocalTime;

import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.dateandtime.threeten.columnmapper.TimeColumnLocalTimeMapper;
import org.jadira.usertype.spi.shared.AbstractParameterizedUserType;
import org.jadira.usertype.spi.shared.IntegratorConfiguredType;

/**
 * Persist {@link LocalTime} via Hibernate as a SQL TIME datatype - note that sub-second values will not
 * be retained. This type is basically compatible with org.joda.time.contrib.hibernate.PersistentLocalTimeAsTime.
 * However, note that org.joda.time.contrib.hibernate.PersistentLocalTimeAsTime contains a bug where times written
 * down will be offset from GMT due to its use of {@link java.sql.Time#setTime(long)}. This class is not affected by this
 * issue, but this means you cannot rely on the interpretation of this type to be the same for both classes.
 */
public class PersistentLocalTime extends AbstractParameterizedUserType<LocalTime, Time, TimeColumnLocalTimeMapper> implements ParameterizedType, IntegratorConfiguredType {

    private static final long serialVersionUID = -6901872002988989156L;
}
