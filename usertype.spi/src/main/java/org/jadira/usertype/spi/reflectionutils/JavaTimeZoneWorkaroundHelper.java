package org.jadira.usertype.spi.reflectionutils;

import java.lang.reflect.Field;
import java.util.TimeZone;

public class JavaTimeZoneWorkaroundHelper {

	private static Field DEFAULT_ZONE_TL = ReflectionUtils.findField(TimeZone.class, "defaultZoneTL");
	static {
		DEFAULT_ZONE_TL.setAccessible(true);
	}
	
	private static ThreadLocal<TimeZone> OLD_ZONE = new ThreadLocal<TimeZone>();
	
	private JavaTimeZoneWorkaroundHelper() {}
	
	@SuppressWarnings("unchecked")
	public static final void setTimeZone(TimeZone zone) {
		
		InheritableThreadLocal<TimeZone> defaultZoneTl;
		try {			
			defaultZoneTl = (InheritableThreadLocal<TimeZone>) DEFAULT_ZONE_TL.get(null);
		} catch (IllegalArgumentException e) {
			throw new ReflectionException("Cannot reset timezone to avoid JDK JDBC Timezone issue", e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException("Cannot reset timezone to avoid JDK JDBC Timezone issue", e);
		}
		
		TimeZone defaultZone = defaultZoneTl.get();
		OLD_ZONE.set(defaultZone);
		
		defaultZoneTl.set(zone);
	}
	
	@SuppressWarnings("unchecked")
	public static final void unsetTimeZone() {
		
		InheritableThreadLocal<TimeZone> defaultZoneTl;
		try {			
			defaultZoneTl = (InheritableThreadLocal<TimeZone>) DEFAULT_ZONE_TL.get(null);
		} catch (IllegalArgumentException e) {
			throw new ReflectionException("Cannot reset timezone to avoid JDK JDBC Timezone issue", e);
		} catch (IllegalAccessException e) {
			throw new ReflectionException("Cannot reset timezone to avoid JDK JDBC Timezone issue", e);
		}
		
		defaultZoneTl.set(OLD_ZONE.get());
	}
}
