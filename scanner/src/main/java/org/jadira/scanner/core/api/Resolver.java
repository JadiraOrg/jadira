package org.jadira.scanner.core.api;

import java.util.List;

public interface Resolver<T, E, A> {

	/**
	 * 
	 * @param locator
	 * @param projector
	 * @param filter
	 * @return
	 */
	T resolveFirst(Locator<A> locator, Projector<E> projector, Filter<?>... filter);
	
	/**
	 * 
	 * @param limit
	 * @param locator
	 * @param projector
	 * @param filter
	 * @return
	 */
	List<? extends T> resolve(Integer limit, Locator<A> locator, Projector<E> projector, Filter<?>... filter);
	
	/**
	 * 
	 * @param locator
	 * @param projector
	 * @param filter
	 * @return
	 */
	List<? extends T> resolveAll(Locator<A> locator, Projector<E> projector, Filter<?>... filter);
}
