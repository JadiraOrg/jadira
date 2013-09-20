package org.jadira.scanner.core.concurrent;

import java.util.ArrayList;
import java.util.List;

import jsr166y.RecursiveTask;

import org.jadira.scanner.core.api.Projector;

public class ProjectorTask<T> extends RecursiveTask<List<T>> {

    private static final long serialVersionUID = -5338937563634945167L;

    private static final int FORK_INTERVAL = 10;

    private final Projector<T> projector;
    private final List<T> inputs;
    private int currentIndex;

    public ProjectorTask(final Projector<T> projector, final List<T> inputs) {

        this(projector, inputs, inputs.size() - 1);
    }

    private ProjectorTask(final Projector<T> projector, final List<T> inputs, int currentIndex) {

        this.projector = projector;
        this.inputs = inputs;

        this.currentIndex = currentIndex;
    }

    @Override
    public List<T> compute() {

        int computeIndex = currentIndex;

        final ProjectorTask<T> pt;
        if ((getForkInterval() != -1) && (computeIndex > 0) && (computeIndex - getForkInterval() > getForkInterval())) {
            pt = new ProjectorTask<T>(projector, this.inputs, computeIndex - getForkInterval());
            pt.fork();
        } else {
            pt = null;
        }

        int floor = computeIndex - getForkInterval();
        if (floor <= getForkInterval()) {
            floor = -1;
        }

        final List<T> resultList = new ArrayList<T>();

        while (computeIndex > floor) {
            List<T> myResult = projector.project(inputs.get(computeIndex));
            resultList.addAll(myResult);
            computeIndex = computeIndex - 1;
        }

        if (pt != null) {
            resultList.addAll(pt.join());
        }
        return resultList;
    }

    public static int getForkInterval() {
        return FORK_INTERVAL;
    }
}
