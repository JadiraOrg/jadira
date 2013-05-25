package org.jadira.scanner.core.concurrent;

import java.util.ArrayList;
import java.util.List;

import jsr166y.RecursiveTask;

import org.jadira.scanner.core.api.Filter;

public class FilterTask<T> extends RecursiveTask<List<T>> {

	private static final long serialVersionUID = 7688297986024541356L;

	private static final int FORK_INTERVAL = 500;
		
	private final Integer limit;
	private final Filter<T> filter;
	private final List<T> inputs;
	private final int currentIndex;
	

	public FilterTask(final Integer limit, final Filter<T> filter, final List<T> inputs) {
		
		this(limit, filter, inputs, inputs.size()-1);
	}
	
	private FilterTask(final Integer limit, final Filter<T> filter, final List<T> inputs, int currentIndex) {
		
		this.limit = limit;
		
		this.filter = filter;
		this.inputs = inputs;
		
		this.currentIndex = currentIndex;
	} 
	
	@Override
	public List<T> compute() {
		
		int computeIndex = currentIndex;
		
		final FilterTask<T> ft;
		if ((getForkInterval() != -1) && (computeIndex > 0) && (computeIndex % getForkInterval() == 0)) {
			ft = new FilterTask<T>(limit, filter, this.inputs, computeIndex - getForkInterval());
			ft.fork();
		} else {
			ft = null;
		}
		
		int myLimit = limit == null ? -1 : limit.intValue();
		
		final List<T> resultList = new ArrayList<T>();
		do {
			boolean canAdd = filter.accept(inputs.get(computeIndex));
			if (canAdd && (myLimit == -1 || (resultList.size() < myLimit))) {
				resultList.add(inputs.get(computeIndex));
			}
			computeIndex = computeIndex - 1;
		} while (indexMatchesThisThread(computeIndex));
		
		if (ft != null) {
			resultList.addAll(ft.join());
		}
		
		if (myLimit != -1 && resultList.size() > myLimit) {
			return resultList.subList(0, myLimit);
		} else {
			return resultList;
		}
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
