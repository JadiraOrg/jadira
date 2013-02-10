package org.jadira.scanner.core.api;

public interface Allocator<T,E> {

	public T allocate(E e);
}
