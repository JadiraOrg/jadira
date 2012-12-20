package org.jadira.scanner.helper;

import java.io.InputStream;

public interface InputStreamOperation<T> {

	public T execute(InputStream fileInputStream);
}
