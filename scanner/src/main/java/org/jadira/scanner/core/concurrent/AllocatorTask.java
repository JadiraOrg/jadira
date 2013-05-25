package org.jadira.scanner.core.concurrent;

import java.util.ArrayList;
import java.util.List;

import jsr166y.RecursiveTask;

import org.jadira.scanner.core.api.Allocator;

public class AllocatorTask<T,A> extends RecursiveTask<List<T>> {
	
	private static final long serialVersionUID = -5338937563634945167L;
	
	private static final int FORK_INTERVAL = -1;
	
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
		if ((getForkInterval() != -1) && (computeIndex > 0) && (computeIndex % getForkInterval() == 0)) {
			at = new AllocatorTask<T, A>(allocator, this.inputs, computeIndex - getForkInterval());
			at.fork();
		} else {
			at = null;
		}
		
		final List<T> resultList = new ArrayList<T>();
		do {
			T myResult = allocator.allocate(inputs.get(computeIndex));
			resultList.add(myResult);
			computeIndex = computeIndex - 1;
		} while (indexMatchesThisThread(computeIndex));
		
		if (at != null) {
			resultList.addAll(at.join());
		} 
		return resultList;
	}
	
	private boolean indexMatchesThisThread(int computeIndex) {
		
		if (getForkInterval() == -1 && computeIndex >= 0) {
			return true;
		}
		if (computeIndex == 0) {
			return true;
		}
		if (computeIndex % getForkInterval() != 0 && computeIndex >= 0) {
			return true;
		}
		return false;
	}

	public static int getForkInterval() {
		return FORK_INTERVAL;
	}
}
