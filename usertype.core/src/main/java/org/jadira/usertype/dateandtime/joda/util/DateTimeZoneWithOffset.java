/*
 *  Copyright 2012 Christopher Pheby
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
package org.jadira.usertype.dateandtime.joda.util;

import java.io.Serializable;

import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;

public class DateTimeZoneWithOffset implements Serializable {

	private static final long serialVersionUID = 7258300189932937173L;

	private final DateTimeZone standardDateTimeZone;
	private final DateTimeZone offsetDateTimeZone;

	public DateTimeZoneWithOffset(ReadableInstant instant) {
		this(instant.getZone(), DateTimeZone.forOffsetMillis(instant.getZone().getOffset(instant)));
	}

	public DateTimeZoneWithOffset(DateTimeZone zone) {
		this.standardDateTimeZone = zone;
		this.offsetDateTimeZone = null;
	}
	
	public DateTimeZoneWithOffset(String zoneWithOffset) {
		
		int separatorIndex = zoneWithOffset.indexOf('{');
		if (separatorIndex == -1) {
			this.standardDateTimeZone = DateTimeZone.forID(zoneWithOffset);
			this.offsetDateTimeZone = null;
		} else {
			this.standardDateTimeZone = DateTimeZone.forID(zoneWithOffset.substring(0, separatorIndex));
			this.offsetDateTimeZone = DateTimeZone.forID(zoneWithOffset.substring(separatorIndex + 1, zoneWithOffset.length() - 1));
		}
		
	}
	
	public DateTimeZoneWithOffset(DateTimeZone standardDateTimeZone, DateTimeZone offsetDateTimeZone) {
		this.standardDateTimeZone = standardDateTimeZone;
		this.offsetDateTimeZone = offsetDateTimeZone;
		if (offsetDateTimeZone != null && standardDateTimeZone.isFixed()) {
			throw new IllegalArgumentException("offsetDateTimeZone must be null when standardDateTimeZone is fixed");
		}
		if (offsetDateTimeZone == null && !standardDateTimeZone.isFixed()) {
			throw new IllegalArgumentException("offsetDateTimeZone must be not null when standardDateTimeZone is not fixed");
		}
	}

	public DateTimeZone getStandardDateTimeZone() {
		return standardDateTimeZone;
	}
	
	public DateTimeZone getOffsetDateTimeZone() {
		return offsetDateTimeZone;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		if (offsetDateTimeZone == null) {
			return standardDateTimeZone.toString();
		} else {
			return standardDateTimeZone.toString() + "{" + offsetDateTimeZone.toString() + "}";
		}
	}
		
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
        if (obj == null) {
        	return false;
        }
        if (!this.getClass().equals(obj.getClass())) {
        	return false;
        }
       
        final DateTimeZoneWithOffset obj2 = (DateTimeZoneWithOffset)obj;
        if (this.standardDateTimeZone.equals(obj2.standardDateTimeZone)
        	&& offsetDateTimeZone.equals(obj2.offsetDateTimeZone)) {
            return true;
        }
        
        return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return standardDateTimeZone.hashCode() * 3 + offsetDateTimeZone.hashCode() * 5;
	}
}
