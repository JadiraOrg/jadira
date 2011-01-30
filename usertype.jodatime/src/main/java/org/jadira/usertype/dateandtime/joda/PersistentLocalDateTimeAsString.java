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

import org.jadira.usertype.dateandtime.joda.columnmapper.StringColumnLocalDateTimeMapper;
import org.jadira.usertype.dateandtime.shared.spi.AbstractSingleColumnUserType;
import org.joda.time.LocalDateTime;

/**
 * Persist {@link LocalDateTime} via Hibernate.
 */
public class PersistentLocalDateTimeAsString extends AbstractSingleColumnUserType<LocalDateTime, String, StringColumnLocalDateTimeMapper> {

    private static final long serialVersionUID = 6089350124392309729L;
}
