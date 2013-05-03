package org.jadira.usertype.spi.shared;

/**
 * @author Chris
 * @param <T> Type representing the database zone
 */
public interface DatabaseZoneConfigured<T> {

	public void setDatabaseZone(T databaseZone);

	public T parseZone(String zoneString);
}