package org.jadira.usertype.spi.shared;

/**
 * @author Chris
 */
public interface JavaZoneConfigured<T> {

	void setJavaZone(T javaZone);
	
	T parseJavaZone(String zoneString);
}