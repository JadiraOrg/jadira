package org.jadira.scanner.core.concurrent;

import java.util.List;
import java.util.stream.Collectors;

import org.jadira.scanner.core.api.Allocator;

import jsr166y.RecursiveTask;

public class AllocatorTask<T,A> extends RecursiveTask<List<T>> {
	
	private static final long serialVersionUID = -5338937563634945167L;
	
	private final Allocator<T,A> allocator;
	private final List<A> inputs;

	public AllocatorTask(final Allocator<T,A> allocator, final List<A> inputs) {
		
		this.allocator = allocator;
		this.inputs = inputs;
	} 
	
	@Override
	public List<T> compute() {
		
		List<T> result = inputs.stream()
			.map( a -> allocator.allocate(a) )
			.collect( Collectors.toList() );
		
		return result;
	}
}
