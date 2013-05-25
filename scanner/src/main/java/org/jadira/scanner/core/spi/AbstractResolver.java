package org.jadira.scanner.core.spi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jsr166y.ForkJoinPool;

import org.jadira.scanner.core.api.Allocator;
import org.jadira.scanner.core.api.Filter;
import org.jadira.scanner.core.api.Locator;
import org.jadira.scanner.core.api.Projector;
import org.jadira.scanner.core.api.Resolver;
import org.jadira.scanner.core.concurrent.AllocatorTask;
import org.jadira.scanner.core.concurrent.FilterTask;
import org.jadira.scanner.core.concurrent.ProjectorTask;

public abstract class AbstractResolver<T, E, A> implements Resolver<T, E, A> {

	private static final Integer ZERO = Integer.valueOf(0);

	private static final int SEGMENT_SIZE = 500;
	private static final ForkJoinPool FORKJOIN_POOL = new ForkJoinPool();
	
	private final List<A> driverData;
	
	protected AbstractResolver() {
		this.driverData = new ArrayList<A>();
	}
	
	protected AbstractResolver(List<A> driverData) {
		this.driverData = driverData;
	}
	
	protected List<A> getDriverData() {
		return driverData;
	}
	
	protected List<A> locate(Locator<A> locator) {
		
		final List<A> result = new ArrayList<A>();
		if (driverData != null) {
			result.addAll(driverData);
		}
		final List<A> located = locator == null ? null : locator.locate();
		if (located != null) {
			result.addAll(located);
		}
		return result;
	}
	
	protected List<E> allocate(List<A> driverData) {
		
		AllocatorTask<E, A> task = new AllocatorTask<E, A>(getAllocator(), driverData);
		
		final List<E> result;
		if (AllocatorTask.getForkInterval() != -1) {
			result = FORKJOIN_POOL.invoke(task);
		} else {
			result = task.compute();
		}
		
		return result;
	}
	
	protected List<E> project(Projector<E> projector, List<E> sourceList) {
		
		ProjectorTask<E> task = new ProjectorTask<E>(projector, sourceList);
		
		List<E> result;
		if (ProjectorTask.getForkInterval() != -1) {
			result = FORKJOIN_POOL.invoke(task);
		} else {
			result = task.compute();
		}
		
		return result;
	}
	
	protected List<T> assign(List<E> sourceList) {
		
		AllocatorTask<T, E> task = new AllocatorTask<T, E>(getAssigner(), sourceList);
		List<T> result;
		if (AllocatorTask.getForkInterval() != -1) {
			result = FORKJOIN_POOL.invoke(task);
		} else {
			result = task.compute();
		}
		
		return result;
	}
	
	private <S> List<S> filter(Class<?> sourceType, Integer limit, List<Filter<?>> myFilters, List<S> sourceList) {

		if (ZERO.equals(limit)) {
			return Collections.emptyList();
		}
		
		List<S> result = sourceList;
		for (Filter<?> nextFilter : myFilters) {
			if (nextFilter.targetType().isAssignableFrom(sourceType)) {
				
				@SuppressWarnings("unchecked")
				Filter<S> theFilter = (Filter<S>) nextFilter;
				
				FilterTask<S> task = new FilterTask<S>(limit, theFilter, result);
				if (FilterTask.getForkInterval() != -1) {
					result = FORKJOIN_POOL.invoke(task);
				} else {
					result = task.compute();
				}
			}
		}
		return result;
	}
	
	protected abstract Allocator<E,A> getAllocator();
	
	protected abstract Allocator<T,E> getAssigner();

	@Override
	public T resolveFirst(Locator<A> locator, Projector<E> projector, Filter<?>... filters) {
		List<? extends T> result = resolve(Integer.valueOf(1), locator, projector, filters);
		return result.isEmpty() ? null : result.get(0);
	}

	@Override
	public List<? extends T> resolve(Integer limit, Locator<A> locator, Projector<E> projector, Filter<?>... filters) {

		final List<Filter<?>> myFilters = Arrays.asList(filters);
		
		final List<A> locatedList = locate(locator);
		List<E> sourceList = allocate(locatedList);
		
		// Chunk the source list to avoid resource starvation
		final List<T> output = new ArrayList<T>();

		sourceList = project(projector, sourceList);
		
		while (!sourceList.isEmpty() 
				&& (limit == null || (output.size() < limit))) {
			
			List<E> nextSegmentList;
			if (sourceList.size() <= SEGMENT_SIZE) {
				nextSegmentList = sourceList;
				sourceList = Collections.emptyList();
			} else {
				nextSegmentList = sourceList.subList(0, SEGMENT_SIZE);
				sourceList = sourceList.subList(SEGMENT_SIZE, sourceList.size());
			}
			
			nextSegmentList = filter(getSourceType(), null, myFilters, nextSegmentList);
		
			if (!nextSegmentList.isEmpty()) {
				List<T> targetList = assign(nextSegmentList);
				targetList = filter(getTargetType(), limit, myFilters, targetList);
				output.addAll(targetList);
			}
		}
		if (limit == null) {
			return output;
		} else {
			return output.subList(0, output.size() < limit ? output.size() : limit);
		}
	}

	@Override
	public List<? extends T> resolveAll(Locator<A> locator, Projector<E> projector, Filter<?>... filters) {
		return resolve(null, locator, projector, filters);
	}
	
	protected Class<?> getSourceType() {
		return TypeHelper.getTypeArguments(AbstractResolver.class, this.getClass()).get(1);
	}

	protected Class<?> getTargetType() {
		return TypeHelper.getTypeArguments(AbstractResolver.class, this.getClass()).get(0);
	}
}
