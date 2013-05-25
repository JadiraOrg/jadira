package org.jadira.usertype.spi.shared;

/**
 * @author Chris
 * @param <T> Type representing the database zone
 */
public interface DatabaseZoneConfigured<T> {

	void setDatabaseZone(T databaseZone);

	T parseZone(String zoneString);
}