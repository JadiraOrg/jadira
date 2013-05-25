package org.jadira.usertype.spi.shared;


/**
 * @author Chris
 * @param <T> Type representing the java zone
 */
public interface JavaZoneConfigured<T> {

	void setJavaZone(T javaZone);
	
	T parseZone(String zoneString);
}