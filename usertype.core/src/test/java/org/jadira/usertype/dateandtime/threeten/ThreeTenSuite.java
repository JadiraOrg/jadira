package org.jadira.usertype.dateandtime.threeten;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { 
		TestPersistentDayOfWeekAsInteger.class,
		TestPersistentDayOfWeekAsString.class,
		TestPersistentDurationAsString.class,
		TestPersistentInstantAsMillisLong.class,
		TestPersistentInstantAsNanosBigInteger.class,
		TestPersistentInstantAsString.class,
		TestPersistentInstantAsTimestamp.class,
		TestPersistentLocalDate.class,
		TestPersistentLocalDateAsString.class,
		TestPersistentLocalDateTime.class,
		TestPersistentLocalDateTimeAsString.class,
		TestPersistentLocalTime.class,
		TestPersistentLocalTimeAsMillisInteger.class,
		TestPersistentLocalTimeAsNanosLong.class,
		TestPersistentLocalTimeAsString.class,
		TestPersistentLocalTimeAsTimestamp.class,
		TestPersistentMonthAsInteger.class,
		TestPersistentMonthAsString.class,
		TestPersistentMonthDayAsString.class,
		TestPersistentOffsetDateTimeAsStringAndStringOffset.class,
		TestPersistentOffsetTimeAsLongAndStringOffset.class,
		TestPersistentOffsetTimeAsStringAndStringOffset.class,
		TestPersistentOffsetTimeAsTimeAndStringOffset.class,
		TestPersistentPeriodAsString.class,
		TestPersistentYearAsInteger.class,
		TestPersistentYearMonthAsString.class,
		TestPersistentZonedDateTimeAsStringAndStringZone.class,
		TestPersistentZoneIdAsString.class,
		TestPersistentZoneOffsetAsString.class
})
public class ThreeTenSuite {

    private static EntityManagerFactory factory;
	
    @BeforeClass
    public static void setup() {
        factory = Persistence.createEntityManagerFactory("test1");
    }

    @AfterClass
    public static void tearDown() {
        factory.close();
    }
    
    public static EntityManagerFactory getFactory() {
    	if (factory == null) {
    		setup();
    	}
    	return factory;
    }
}