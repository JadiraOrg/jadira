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
package org.jadira.usertype.dateandtime.threetenbp;

import java.sql.Timestamp;

import org.hibernate.usertype.ParameterizedType;
import org.jadira.usertype.dateandtime.threetenbp.columnmapper.TimestampColumnLocalDateTimeMapper;
import org.jadira.usertype.spi.shared.AbstractParameterizedUserType;
import org.jadira.usertype.spi.shared.IntegratorConfiguredType;
import org.threeten.bp.LocalDateTime;


/**
 * Persist {@link LocalDateTime} via Hibernate. This type is
 * mostly compatible with org.joda.time.contrib.hibernate.PersistentLocalDateTime however
 * you should note that JodaTime's org.joda.time.LocalDateTime has only millisecond precision,
 * whilst JSR 310 offers nanosecond precision. When interpreting nanosecond values, Joda time will
 * round down to the nearest millisecond.
 * 
 * @deprecated Jadira now depends on Java 8 so you are recommended to switch to the threeten package types
 */
@Deprecated
public class PersistentLocalDateTime extends AbstractParameterizedUserType<LocalDateTime, Timestamp, TimestampColumnLocalDateTimeMapper> implements ParameterizedType, IntegratorConfiguredType {

    private static final long serialVersionUID = -521354449832270272L;
}
