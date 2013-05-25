package org.jadira.scanner.core.helper;

import java.io.InputStream;

public interface FileInputStreamOperation<T> {

	T execute(String path, InputStream fileInputStream);
}
