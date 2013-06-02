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

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Table;

import org.jadira.usertype.spi.utils.reflection.TypeHelper;
import org.junit.Before;

/**
 * This abstract parent test class bundles several method needed for database relevant tests:
 * <ul>
 * <li>Create/destroy the entity manager factory before/after class</li>
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

    protected EntityManagerFactory factory;

    private Class<T> tableType;

    @SuppressWarnings("unchecked")
	public AbstractDatabaseTest(EntityManagerFactory factory) {
    	this.factory = factory;
        this.tableType = (Class<T>) TypeHelper.getTypeArguments(AbstractDatabaseTest.class, this.getClass()).get(0);
    }

    @Before
    public void clearTableUnderTest() {
        EntityManager manager = factory.createEntityManager();
        manager.getTransaction().begin();

        manager.createQuery("delete from " + tableType.getSimpleName()).executeUpdate();
        manager.getTransaction().commit();
        manager.close();
    }

    protected <E extends Serializable> E persist(E item) {
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

    protected <E extends Serializable> E find(Class<E> entityType, long id) {
        EntityManager manager = factory.createEntityManager();
        manager.getTransaction().begin();
        E item = manager.find(entityType, id);
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
