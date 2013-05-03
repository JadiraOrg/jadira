package org.jadira.usertype.spi.shared;


/**
 * @author Chris
 * @param <T> Type representing the java zone
 */
public interface JavaZoneConfigured<T> {

	public void setJavaZone(T javaZone);
	
	public T parseZone(String zoneString);
}