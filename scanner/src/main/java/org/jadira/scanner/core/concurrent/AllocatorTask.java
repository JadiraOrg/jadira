package org.jadira.scanner.core.concurrent;

import java.util.ArrayList;
import java.util.List;

import jsr166y.RecursiveTask;

import org.jadira.scanner.core.api.Allocator;

public class AllocatorTask<T,A> extends RecursiveTask<List<T>> {
	
	private static final long serialVersionUID = -5338937563634945167L;
	
	private static final int FORK_INTERVAL = 50;
	
	private final Allocator<T,A> allocator;
	private final List<A> inputs;
	private final int currentIndex;

	public AllocatorTask(final Allocator<T,A> allocator, final List<A> inputs) {
		
		this(allocator, inputs, inputs.size() - 1);
	}
	
	private AllocatorTask(final Allocator<T,A> allocator, final List<A> inputs, int currentIndex) {
		
		this.allocator = allocator;
		this.inputs = inputs;
		
		this.currentIndex = currentIndex;
	} 
	
	@Override
	public List<T> compute() {
		
		int computeIndex = currentIndex;
		
		final AllocatorTask<T,A> at;
		if ((getForkInterval() != -1) && (computeIndex > 0) && (computeIndex - getForkInterval() > getForkInterval())) {
		    at = new AllocatorTask<T, A>(allocator, this.inputs, computeIndex - getForkInterval());
			at.fork();
		} else {
			at = null;
		}
		
		int floor = computeIndex - getForkInterval();
		if (floor <= getForkInterval()) {
		    floor = -1;
		}
		
		final List<T> resultList = new ArrayList<T>();
		
	    while (computeIndex > floor) {
			T myResult = allocator.allocate(inputs.get(computeIndex));
			resultList.add(myResult);
			computeIndex = computeIndex - 1;
	    }
		
		if (at != null) {
			resultList.addAll(at.join());
		} 
		return resultList;
	}
	
	public static int getForkInterval() {
		return FORK_INTERVAL;
	}
}
