package org.jadira.scanner.core.api;

import java.util.List;

public interface Resolver<T, E, A> {

	/**
	 * Resolve the first match
	 * @param locator Locator to be used
	 * @param projector Projector to be used
	 * @param filter Zero or more filters to be used
	 * @return The first match
	 */
	T resolveFirst(Locator<A> locator, Projector<E> projector, Filter<?>... filter);
	
	/**
	 * Resolve all matches up to limit
	 * @param limit The maximum number of results to match
	 * @param locator Locator to be used
	 * @param projector Projector to be used
	 * @param filter Zero or more filters to be used
	 * @return The first match
	 */
	List<? extends T> resolve(Integer limit, Locator<A> locator, Projector<E> projector, Filter<?>... filter);
	
	/**
	 * Resolve all matches
	 * @param locator Locator to be used
	 * @param projector Projector to be used
	 * @param filter Zero or more filters to be used
	 * @return The first match
	 */
	List<? extends T> resolveAll(Locator<A> locator, Projector<E> projector, Filter<?>... filter);
}
