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

/**
 * The most fundamental repository, being a repository that only offers read and write capabilities.
 * 
 * @param <T> Entity type that this Repository handles
 * @param <ID> The type that identifies the ID column for the supported entity type
 */
public interface BaseRepository<T extends Serializable, ID extends Serializable> extends SearchRepository<T, ID> {

	/**
	 * Persists the given entity. Normally, you will assign the result of the persist operation back
	 * to the original object reference.
	 * 
	 * The implementation of persist in this object model will perform a no-op or merge if required.
	 * This means you do not need to consider whether an entity has been already persisted or is
	 * detached when calling this method. This is especially useful with multi-tier applications.
	 * 
	 * @param entity The entity to be persisted
	 * @return The entity, persisted
	 */
	T persist(T entity);

	/**
	 * Removes the given entity from the database, cascading as appropriate.
	 * 
	 * The implementation of persist in this object model will perform a merge in the case of an
	 * unattached entity. This means you do not need to consider whether an entity is detached when
	 * calling this method. This is especially useful with multi-tier applications.
	 * 
	 * @param entity The entity to be removed
	 */
	void remove(ID entityId);

	/**
	 * Refresh the supplied entity from the database, overriding its state where changed.
	 * 
	 * In the case of a detached entity a merge will be first performed. Therefore you must assign
	 * the result of this method always back to the original object reference.
	 * 
	 * @param entity The entity to be refreshed.
	 * @return The entity, refreshed
	 */
	T refresh(T entity);

	/**
	 * Evicts the supplied entity from the session. Therefore, during flush, the entity will not be
	 * synchronised. you must assign the result of this method always back to the original object
	 * reference, as in the case of a detached entity, a merge will be first performed, meaning that
	 * the evicted entity returned will be a copy.
	 * 
	 * @param entity The entity to be evicted.
	 * @return The entity, evicted
	 */
	T evict(T entity);

	/**
	 * Synchronises the state of EntityManager for this repository with the database
	 */
	void flush();

	/**
	 * Lock an entity instance that is contained in the persistence context with the specified lock
	 * mode type.
	 * 
	 * @param entity The entity to be locked.
	 * @param lockMode The {@link LockModeType}
	 * @see javax.persistence.EntityManager#lock(Object, LockModeType)
	 */
	void lock(T entity, LockModeType lockMode);
}