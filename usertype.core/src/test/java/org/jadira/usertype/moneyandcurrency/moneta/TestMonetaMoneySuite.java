package org.jadira.usertype.moneyandcurrency.moneta;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses(value = { TestPersistentCurrencyUnit.class, TestPersistentMoneyAmount.class,
        TestPersistentMoneyAmountAndCurrency.class, TestPersistentMoneyMajorAmount.class,
        TestPersistentMoneyMajorAmountAndCurrency.class, TestPersistentMoneyMinorAmount.class,
        TestPersistentMoneyMinorAmountAndCurrency.class })
public class TestMonetaMoneySuite {

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
