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

/**
 * The most fundamental repository, being a repository that only offers search (read) capabilities.
 * 
 * @param <T> Entity type that this Repository handles
 * @param <ID> The type that identifies the ID column for the supported entity type
 */
public interface SearchRepository<T extends Serializable, ID extends Serializable> {

	/**
	 * Returns the instance with the given ID or null
	 * 
	 * @param id The ID to be used as a search criteria
	 * @return The matched entity of type T or null
	 */
	T findById(ID id);
}