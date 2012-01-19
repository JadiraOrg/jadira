/*
 *  Copyright 2010, 2011 Christopher Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.usertype.moneyandcurrency.joda;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.math.RoundingMode;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Table;

import org.jadira.usertype.dateandtime.shared.dbunit.DatabaseCapable;
import org.jadira.usertype.moneyandcurrency.joda.testmodel.BigMoneyMajorAmountHolder;
import org.joda.money.BigMoney;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestPersistentBigMoneyMajorAmount extends DatabaseCapable {

    private static final BigMoney[] bigMoneys     = new BigMoney[] { BigMoney.parse("USD 100.00"), BigMoney.parse("USD 100.10"), BigMoney.parse("USD 0.99"), null };

    private static EntityManagerFactory factory;

    @BeforeClass
    public static void setup() {
        factory = Persistence.createEntityManagerFactory("test1");
    }

    @AfterClass
    public static void tearDown() {
        factory.close();
    }

    @Test
    public void testPersist() {

        EntityManager manager = factory.createEntityManager();

        manager.getTransaction().begin();

        for (int i = 0; i < bigMoneys.length; i++) {

            BigMoneyMajorAmountHolder item = new BigMoneyMajorAmountHolder();
            item.setId(i);
            item.setName("test_" + i);
            item.setBigMoney(bigMoneys[i]);

            manager.persist(item);
        }

        manager.flush();
        
        manager.getTransaction().commit();
        
        manager.close();

        manager = factory.createEntityManager();
        
        for (int i = 0; i < bigMoneys.length; i++) {

            BigMoneyMajorAmountHolder item = manager.find(BigMoneyMajorAmountHolder.class, Long.valueOf(i));

            assertNotNull(item);
            assertEquals(i, item.getId());
            assertEquals("test_" + i, item.getName());
            if (bigMoneys[i] == null) {
            	assertNull(item.getBigMoney());
            } else {
            	assertEquals(bigMoneys[i].rounded(0, RoundingMode.DOWN).toString(), item.getBigMoney().toString());
            }
        }
        
        verifyDatabaseTable(manager, BigMoneyMajorAmountHolder.class.getAnnotation(Table.class).name());
        
        manager.close();
    }
}
