package org.jadira.usertype.spi.reflectionutils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.TimeZone;

@SuppressWarnings("restriction")
public class JavaTimeZoneWorkaroundHelper {

	private static final Logger log = LoggerFactory.getLogger(JavaTimeZoneWorkaroundHelper.class);
	
	private static Field JDK5_AND_PRESECURITYPATCH_DEFAULT_ZONE_TL = null;
	
	private static ThreadLocal<sun.awt.AppContext> APPCONTEXT = new ThreadLocal<sun.awt.AppContext>();
	
	private static Constructor<sun.awt.AppContext> APPCONTEXT_CONSTRUCTOR = null;
	private static Field JDK7_PATCHED_THREAD_APPCONTEXT = null;

	static {
		try {
			JDK5_AND_PRESECURITYPATCH_DEFAULT_ZONE_TL = ReflectionUtils.findField(TimeZone.class, "defaultZoneTL");
			JDK5_AND_PRESECURITYPATCH_DEFAULT_ZONE_TL.setAccessible(true);
		} catch (IllegalStateException e) {
				
			APPCONTEXT_CONSTRUCTOR = ReflectionUtils.findConstructor(sun.awt.AppContext.class, ThreadGroup.class);
			APPCONTEXT_CONSTRUCTOR.setAccessible(true);
				
			try {
				JDK7_PATCHED_THREAD_APPCONTEXT = ReflectionUtils.findField(sun.awt.AppContext.class, "threadAppContext");
				JDK7_PATCHED_THREAD_APPCONTEXT.setAccessible(true);
			} catch (IllegalStateException e2) {				
				log.warn("Under JDK 6 it may not be possible to handle DST transitions correctly");
				if (TimeZone.getDefault().useDaylightTime()) {
					log.error("Running under a Zone that uses daylight saving time. To avoid incorrect datetimes being stored during DST transition, either update to JDK 7 or use a Timezone for the JDK without Daylight Saving Time");
				}
			}
		}
	}
	
	private static ThreadLocal<TimeZone> OLD_ZONE = new ThreadLocal<TimeZone>();
	private static ThreadLocal<sun.awt.AppContext> OLD_THREAD_APPCONTEXT = new ThreadLocal<sun.awt.AppContext>();
	
	private JavaTimeZoneWorkaroundHelper() {}
	
	@SuppressWarnings("unchecked")
	public static final void setTimeZone(TimeZone zone) {

		if (!TimeZone.getDefault().useDaylightTime()) {
			if (JDK5_AND_PRESECURITYPATCH_DEFAULT_ZONE_TL != null) {
				InheritableThreadLocal<TimeZone> defaultZoneTl;
				try {			
					defaultZoneTl = (InheritableThreadLocal<TimeZone>) JDK5_AND_PRESECURITYPATCH_DEFAULT_ZONE_TL.get(null);
				} catch (IllegalArgumentException e) {
					throw new ReflectionException("Cannot reset timezone to avoid JDK JDBC Timezone issue", e);
				} catch (IllegalAccessException e) {
					throw new ReflectionException("Cannot reset timezone to avoid JDK JDBC Timezone issue", e);
				}
				
				TimeZone defaultZone = defaultZoneTl.get();
				OLD_ZONE.set(defaultZone);
				
				defaultZoneTl.set(zone);
			} else if (JDK7_PATCHED_THREAD_APPCONTEXT != null) {
				
				if (APPCONTEXT.get() == null) {
					try {
						ThreadGroup pseudoThreadGroup = new ThreadGroup("jadiraUsertype_stub");
						sun.awt.AppContext tmpAppContext = APPCONTEXT_CONSTRUCTOR.newInstance(pseudoThreadGroup);
	
						tmpAppContext.put(TimeZone.class, zone);
						if (APPCONTEXT.get() == null) {
							APPCONTEXT.set(tmpAppContext);
						}
						
						ThreadLocal<sun.awt.AppContext> threadAppCtx;
						try {			
							threadAppCtx = (ThreadLocal<sun.awt.AppContext>) JDK7_PATCHED_THREAD_APPCONTEXT.get(null);
						} catch (IllegalArgumentException e) {
							throw new ReflectionException("Cannot reset timezone to avoid JDK JDBC Timezone issue", e);
						} catch (IllegalAccessException e) {
							throw new ReflectionException("Cannot reset timezone to avoid JDK JDBC Timezone issue", e);
						}
						
						sun.awt.AppContext oldAppContext = threadAppCtx.get();
						OLD_THREAD_APPCONTEXT.set(oldAppContext);
						
						threadAppCtx.set(APPCONTEXT.get());
					} catch (IllegalArgumentException e1) {
						throw new IllegalStateException("Incorrectly configured attempt to create sun.awt.AppContext");
					} catch (InstantiationException e1) {
						throw new IllegalStateException("Could not instantiate sun.awt.AppContext");
					} catch (IllegalAccessException e1) {
						throw new IllegalStateException("Could not access sun.awt.AppContext");
					} catch (InvocationTargetException e1) {
						throw new IllegalStateException("Could not invoke sun.awt.AppContext");
					}
				}			
			} else { 
				// JDK6 Patched - do nothing
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public static final void unsetTimeZone() {
		
		if (JDK5_AND_PRESECURITYPATCH_DEFAULT_ZONE_TL != null) {
			InheritableThreadLocal<TimeZone> defaultZoneTl;
			try {			
				defaultZoneTl = (InheritableThreadLocal<TimeZone>) JDK5_AND_PRESECURITYPATCH_DEFAULT_ZONE_TL.get(null);
			} catch (IllegalArgumentException e) {
				throw new ReflectionException("Cannot reset timezone to avoid JDK JDBC Timezone issue", e);
			} catch (IllegalAccessException e) {
				throw new ReflectionException("Cannot reset timezone to avoid JDK JDBC Timezone issue", e);
			}
			
			defaultZoneTl.set(OLD_ZONE.get());
		} else if (JDK7_PATCHED_THREAD_APPCONTEXT != null) {
			
			ThreadLocal<sun.awt.AppContext> threadAppCtx;
			try {			
				threadAppCtx = (ThreadLocal<sun.awt.AppContext>) JDK7_PATCHED_THREAD_APPCONTEXT.get(null);
			} catch (IllegalArgumentException e) {
				throw new ReflectionException("Cannot reset timezone to avoid JDK JDBC Timezone issue", e);
			} catch (IllegalAccessException e) {
				throw new ReflectionException("Cannot reset timezone to avoid JDK JDBC Timezone issue", e);
			}
			
			threadAppCtx.set(OLD_THREAD_APPCONTEXT.get());			
		} else {
			// JDK 6 Patched - Do nothing
		}
	}
}
