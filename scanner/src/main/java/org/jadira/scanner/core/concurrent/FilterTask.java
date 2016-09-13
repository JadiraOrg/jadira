package org.jadira.scanner.core.concurrent;

import java.util.List;
import java.util.stream.Collectors;

import org.jadira.scanner.core.api.Filter;

import jsr166y.RecursiveTask;

public class FilterTask<T> extends RecursiveTask<List<T>> {

	private static final long serialVersionUID = 7688297986024541356L;
	
	private final Filter<T> filter;
	private final List<T> inputs;

	public FilterTask(final Integer limit, final Filter<T> filter, final List<T> inputs) {
		
		this.filter = filter;
		this.inputs = inputs;
	} 
	
	@Override
	public List<T> compute() {
		
		List<T> result = inputs.stream()
			.filter( t -> filter.accept(t) )
			.collect( Collectors.toList() );
		
		return result;
    }
}
