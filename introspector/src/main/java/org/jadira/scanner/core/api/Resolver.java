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
	public T resolveFirst(Locator<A> locator, Projector<E> projector, Filter<?>... filter);
	
	/**
	 * 
	 * @param limit
	 * @param locator
	 * @param projector
	 * @param filter
	 * @return
	 */
	public List<T> resolve(Integer limit, Locator<A> locator, Projector<E> projector, Filter<?>... filter);
	
	/**
	 * 
	 * @param locator
	 * @param projector
	 * @param filter
	 * @return
	 */
	public List<T> resolveAll(Locator<A> locator, Projector<E> projector, Filter<?>... filter);
}
