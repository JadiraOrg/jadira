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
	
	@Override
	public String toString() {
		if (offsetDateTimeZone == null) {
			return standardDateTimeZone.toString();
		} else {
			return standardDateTimeZone.toString() + "{" + offsetDateTimeZone.toString() + "}";
		}
	}
	
	public DateTimeZone getStandardDateTimeZone() {
		return standardDateTimeZone;
	}
	
	public DateTimeZone getOffsetDateTimeZone() {
		return offsetDateTimeZone;
	}
}
