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

import java.io.Serializable;

import javax.persistence.LockModeType;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.ejb.HibernateEntityManagerFactory;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.jadira.usertype.spi.utils.reflection.TypeHelper;

/**
 * Base implementation of a Repository / type-safe DAO using Hibernate JPA
 * 
 * @param <T> Entity type that this Repository handles
 * @param <ID> The type that identifies the ID column for the supported entity type
 */
public abstract class JpaBaseRepository<T extends Serializable, ID extends Serializable> extends JpaSearchRepository<T, ID> implements BaseRepository<T, ID> {

	protected JpaBaseRepository() {
	}

	/**
	 * Determines the ID for the entity
	 * 
	 * @param entity The entity to retrieve the ID for
	 * @return The ID
	 */
	protected ID extractId(T entity) {

		final Class<?> entityClass = TypeHelper.getTypeArguments(JpaBaseRepository.class, this.getClass()).get(0);
		final SessionFactory sf = ((HibernateEntityManagerFactory) getEntityManager().getEntityManagerFactory()).getSessionFactory();
		final ClassMetadata cmd = sf.getClassMetadata(entityClass);

		final SessionImplementor si = (SessionImplementor)(getEntityManager().getDelegate());

		@SuppressWarnings("unchecked")
		final ID result = (ID) cmd.getIdentifier(entity, si);
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T persist(T entity) {

		// Case of new, non-persisted entity
		if (extractId(entity) == null) {
			getEntityManager().persist(entity);
		}
	
		else if (!getEntityManager().contains(entity)) {
			// In the case of an attached entity, we do nothing (it
			// will be persisted automatically on synchronisation)
			// But... in the case of an unattached, but persisted entity
			// we perform a merge to re-attach and persist it
			entity = getEntityManager().merge(entity);
		}

		return entity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(ID entityId) {

		T entity = getEntityManager().find(getEntityClass(), entityId);

		// Case of attached entity - simply remove it
		if (getEntityManager().contains(entity)) {
			getEntityManager().remove(entity);
		}
		// Case of unattached entity, first it is necessary to perform
		// a merge, before doing the remove
		else {
			entity = getEntityManager().merge(entity);
			getEntityManager().remove(entity);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T refresh(T entity) {

		// Attempting to refresh a non-persisted entity
		// will result in an exception
		if (extractId(entity) == null) {
			// causes exception
			getEntityManager().refresh(entity);
		}
		// Case of attached empty - this gets refreshed
		else if (getEntityManager().contains(entity)) {
			getEntityManager().refresh(entity);
		}
		// Case of unattached entity, first it is necessary to perform
		// a merge, before doing the refresh
		else {
			entity = getEntityManager().merge(entity);
			getEntityManager().refresh(entity);
		}

		return entity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T evict(T entity) {

		Session session = (Session) getEntityManager().getDelegate();
		session.evict(entity);

		return entity;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void flush() {

		getEntityManager().flush();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void lock(T entity, LockModeType lockMode) {

		getEntityManager().lock(entity, lockMode);
	}
}