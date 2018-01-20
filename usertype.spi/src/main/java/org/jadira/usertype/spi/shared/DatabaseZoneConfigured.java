package org.jadira.usertype.spi.shared;

import java.util.TimeZone;

import org.jadira.usertype.spi.timezone.proxy.WrapsSession;

/**
 * @author Chris
 */
public interface DatabaseZoneConfigured extends WrapsSession {

	void setDatabaseZone(TimeZone databaseZone);

	TimeZone parseZone(String zoneString);
	
	public TimeZone getDatabaseZone();
}