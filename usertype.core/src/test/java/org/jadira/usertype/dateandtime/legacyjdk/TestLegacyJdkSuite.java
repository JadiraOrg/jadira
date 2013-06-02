package org.jadira.usertype.dateandtime.legacyjdk;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { 
		TestPersistentDate.class,
		TestPersistentTimestamp.class
})
public class TestLegacyJdkSuite {

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