package org.jadira.usertype.moneyandcurrency.joda;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { 
		TestPersistentBigMoneyAmount.class,
		TestPersistentBigMoneyAmountAndCurrency.class,
		TestPersistentBigMoneyAmountAndCurrencyAsInteger.class,
		TestPersistentBigMoneyMajorAmount.class,
		TestPersistentBigMoneyMajorAmountAndCurrency.class,
		TestPersistentBigMoneyMajorAmountAndCurrencyAsInteger.class,
		TestPersistentBigMoneyMinorAmount.class,
		TestPersistentBigMoneyMinorAmountAndCurrency.class,
		TestPersistentBigMoneyMinorAmountAndCurrencyAsInteger.class,
		TestPersistentCurrency.class,
		TestPersistentCurrencyUnit.class,
		TestPersistentCurrencyUnitAsInteger.class,
		TestPersistentMoneyAmount.class,
		TestPersistentMoneyAmountAndCurrency.class,
		TestPersistentMoneyAmountAndCurrencyAsInteger.class,
		TestPersistentMoneyMajorAmount.class,
		TestPersistentMoneyMajorAmountAndCurrency.class,
		TestPersistentMoneyMajorAmountAndCurrencyAsInteger.class,
		TestPersistentMoneyMinorAmount.class,
		TestPersistentMoneyMinorAmountAndCurrency.class,
		TestPersistentMoneyMinorAmountAndCurrencyAsInteger.class
})
public class TestJodaMoneySuite {

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