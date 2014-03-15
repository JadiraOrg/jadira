package org.jadira.usertype.dateandtime.joda;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { 
		TestPersistentDateMidnight.class,
		TestPersistentDateMidnightAsString.class,
		TestPersistentDateTime.class,
		TestPersistentDateTimeAndZoneWithOffset.class,
		TestPersistentDateTimeAsString.class,
		TestPersistentDateTimeZoneAsString.class,
		TestPersistentDurationAsString.class,
		TestPersistentInstantAsMillisLong.class,
		TestPersistentInstantAsNanosBigInteger.class,
		TestPersistentInstantAsString.class,
		TestPersistentInstantAsTimestamp.class,
		TestPersistentInterval.class,
		TestPersistentLocalDate.class,
		TestPersistentLocalDateAsString.class,
		TestPersistentLocalDateTime.class,
		TestPersistentLocalDateTimeAsString.class,
		TestPersistentLocalTime.class,
		TestPersistentLocalTimeAsMillisInteger.class,
		TestPersistentLocalTimeAsNanosLong.class,
		TestPersistentLocalTimeAsString.class,
		TestPersistentLocalTimeAsTimestamp.class,
		TestPersistentMinutes.class,
		TestPersistentMonthDayAsString.class,
		TestPersistentPeriodAsString.class,
		TestPersistentTimeOfDay.class,
		TestPersistentTimeOfDayAsMillisInteger.class,
		TestPersistentTimeOfDayAsNanosLong.class,
		TestPersistentTimeOfDayAsString.class,
		TestPersistentTimeOfDayAsTimestamp.class,
		TestPersistentYearMonthAsString.class,
		TestPersistentYearMonthDay.class,
		TestPersistentYearMonthDayAsString.class,
		TestPersistentYears.class
})
public class TestJodaTimeSuite {

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