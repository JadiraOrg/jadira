/*
 *  Copyright 2012 Christopher Pheby
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
package org.jadira.usertype.spi.repository;

import org.jadira.usertype.spi.utils.reflection.TypeHelper;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.Serializable;

/**
 * Base implementation of a read-only Repository / type-safe DAO using Hibernate JPA
 * 
 * @param <T> Entity type that this Repository handles
 * @param <ID> The type that identifies the ID column for the supported entity type
 */
public abstract class JpaSearchRepository<T extends Serializable, ID extends Serializable> implements SearchRepository<T, ID> {

	/**
	 * The EntityManager associated with this repository
	 */
	private EntityManager entityManager;

	protected JpaSearchRepository() {
	}

	/**
	 * Access the associated EntityManager
	 * 
	 * @return {@link EntityManager}
	 */
	protected EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * This method can be used to associate an EntityManager. When sub-classing this repository you
	 * may provide a public overriding method that can be annotated with
	 * <code>@PersistenceContext</code> and/or used with a dependency injection container such as
	 * Spring for injecting the manager. You are required to override this method so you can decide
	 * whether to use an annotation or other configuration to drive the injection.
	 * @param entityManager The entityManager to be used
	 */
	protected void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/**
	 * {@inheritDoc}
	 */
	public T findById(ID id) {

		T retVal = (T) getEntityManager().find(getEntityClass(), id);
		return retVal;
	}

	/**
	 * Returns the class for the entity type associated with this repository.
	 * @return Class
	 */
	protected final Class<T> getEntityClass() {

		@SuppressWarnings("unchecked")
		final Class<T> result = (Class<T>) TypeHelper.getTypeArguments(JpaSearchRepository.class, this.getClass()).get(0);
		return result;
	}

	/**
	 * Returns the class for the ID field for the entity type associated with this repository.
	 * @return Class for the ID
	 */
	protected final Class<ID> getIdClass() {

		@SuppressWarnings("unchecked")
		final Class<ID> result = (Class<ID>) TypeHelper.getTypeArguments(JpaSearchRepository.class, this.getClass()).get(1);
		return result;
	}

	/**
	 * Executes a query that returns a single record. In the case of no result, rather than throwing
	 * an exception, null is returned.
	 * @param q Query to Execute
	 * @return T The instance to return, or null if no result.
	 */
	protected T getSingleResult(Query q) {
		try {
			@SuppressWarnings("unchecked")
			T retVal = (T) q.getSingleResult();
			return retVal;
		} catch (NoResultException e) {
			return null;
		}
	}

	/**
	 * Clears the persistence context causing all managed entities to become detached
	 */
	public void clear() {

		getEntityManager().clear();
	}
}
