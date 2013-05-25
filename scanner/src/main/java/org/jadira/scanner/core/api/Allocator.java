package org.jadira.scanner.core.api;

public interface Allocator<T,E> {

	T allocate(E e);
}
