/*
 *  Copyright 2012
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

package org.jadira.usertype.dateandtime.shared.dbunit;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * This abstract parent test class bundles several method needed for database relevant tests:
 * <ul>
 * <li>Create/detroy the entity manager factory before/after class</li>
 * <li>Clear the database table before each test</li>
 * <li>Provides methods to</li>
 * <ul>
 * <li>Find entities by primary key</li>
 * <li>Persist entities</li>
 * </ul>
 * </ul>
 * <p/>
 * Implementing test classes has to set the generic parameter T of the used entity
 * and pass T's class to the parent constructor.
 */
public abstract class AbstractDatabaseTest<T extends Serializable> extends DatabaseCapable {

    protected static EntityManagerFactory factory;

    private Class<T> tableType;

    public AbstractDatabaseTest(Class<T> tableType) {
        this.tableType = tableType;
    }

    @BeforeClass
    public static void setup() {
        factory = Persistence.createEntityManagerFactory("test1");
    }

    @Before
    public void clearTable() {
        EntityManager manager = factory.createEntityManager();
        manager.getTransaction().begin();

        manager.createQuery("delete from " + tableType.getSimpleName()).executeUpdate();
        manager.getTransaction().commit();
        manager.close();
    }

    @AfterClass
    public static void tearDown() {
        factory.close();
    }

    protected <ENTITY extends Serializable> ENTITY persist(ENTITY item) {
        EntityManager manager = factory.createEntityManager();
        manager.getTransaction().begin();
        manager.persist(item);
        manager.getTransaction().commit();
        manager.close();

        return item;
    }

    protected T find(long id) {
        return find(tableType, id);
    }

    protected <ENTITY extends Serializable> ENTITY find(Class<ENTITY> entityType, long id) {
        EntityManager manager = factory.createEntityManager();
        manager.getTransaction().begin();
        ENTITY item = manager.find(entityType, id);
        manager.getTransaction().commit();
        manager.close();

        return item;
    }

    protected void verifyDatabaseTable() {
        EntityManager manager = factory.createEntityManager();
        verifyDatabaseTable(manager, tableType.getAnnotation(Table.class).name());
        manager.close();
    }
}
