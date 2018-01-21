package org.jadira.usertype.spi.shared;

import org.hibernate.SessionFactory;

public abstract class AbstractParameterizedTemporalMultiColumnUserType<T> extends AbstractParameterizedMultiColumnUserType<T> {

	private static final long serialVersionUID = -5378286101759906332L;

	@Override
	public void applyConfiguration(SessionFactory sessionFactory) {
		super.applyConfiguration(sessionFactory);
		doApplyConfiguration();
    }
    
	private <Z> void doApplyConfiguration() {
	    
		if (DatabaseZoneConfigured.class.isAssignableFrom(this.getClass())) {
			
			DatabaseZoneConfigured next = (DatabaseZoneConfigured)this;			
			performDatabaseZoneConfiguration(next);
		}
		if (JavaZoneConfigured.class.isAssignableFrom(this.getClass())) {
			
			@SuppressWarnings("unchecked")
			JavaZoneConfigured<Z> next = (JavaZoneConfigured<Z>)this;			
			performJavaZoneConfiguration(next);
		}

		
		for (int i = 0; i < getColumnMappers().length; i++) {
			
			if (DatabaseZoneConfigured.class.isAssignableFrom(getColumnMappers()[i].getClass())) {
	
				DatabaseZoneConfigured next = (DatabaseZoneConfigured)getColumnMappers()[i];
		        performDatabaseZoneConfiguration(next);
			}			
			if (JavaZoneConfigured.class.isAssignableFrom(getColumnMappers()[i].getClass())) {
				
				@SuppressWarnings("unchecked")
				JavaZoneConfigured<Z> next = (JavaZoneConfigured<Z>)getColumnMappers()[i];

				performJavaZoneConfiguration(next);				
			}
		}
    }
	
	private void performDatabaseZoneConfiguration(DatabaseZoneConfigured next) {
		
        String databaseZone = null;
        if (getParameterValues() != null) {
        	databaseZone = getParameterValues().getProperty("databaseZone");
        }
		if (databaseZone == null) {
			databaseZone = ConfigurationHelper.getProperty("databaseZone");
		}
		
        if (databaseZone != null) {
            if ("jvm".equals(databaseZone)) {
                next.setDatabaseZone(null);
            } else {
            	next.setDatabaseZone(next.parseZone(databaseZone));
            }
        }
	}
	
	private <Z> void performJavaZoneConfiguration(JavaZoneConfigured<Z> next) {
		
		String javaZone = null;
        if (getParameterValues() != null) {
        	javaZone = getParameterValues().getProperty("javaZone");
        }
		if (javaZone == null) {
			javaZone = ConfigurationHelper.getProperty("javaZone");
		}
		
        if (javaZone != null) {
            if ("jvm".equals(javaZone)) {
                next.setJavaZone(null);
            } else {
            	next.setJavaZone(next.parseJavaZone(javaZone));
            }
        }
	}	
}
