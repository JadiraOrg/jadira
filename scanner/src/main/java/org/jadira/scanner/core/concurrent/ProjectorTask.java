package org.jadira.scanner.core.concurrent;

import java.util.List;
import java.util.stream.Collectors;

import org.jadira.scanner.core.api.Projector;

import jsr166y.RecursiveTask;

public class ProjectorTask<T> extends RecursiveTask<List<T>> {

    private static final long serialVersionUID = -5338937563634945167L;

    private final Projector<T> projector;
    private final List<T> inputs;

    public ProjectorTask(final Projector<T> projector, final List<T> inputs) {

        this.projector = projector;
        this.inputs = inputs;
    }

    @Override
    public List<T> compute() {

    	List<T> result = inputs.stream()
    		.flatMap( t -> projector.project(t).stream() )
    		.collect( Collectors.toList() );
    	
    	return result;
    }
}
