/*
 *  Copyright 2012 Chris Pheby
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.jadira.scanner.core.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.WeakHashMap;

import org.jadira.scanner.core.exception.FileAccessException;

import de.schlichtherle.io.ArchiveDetector;
import de.schlichtherle.io.ArchiveException;

public final class FileUtils {

    private static final WeakHashMap<String, de.schlichtherle.io.File> FILE_CACHE = new WeakHashMap<String, de.schlichtherle.io.File>();
	
	private FileUtils() {
	}

	public static File getFileForPathName(String pathName, File parentFile) throws FileAccessException {

		final String directoryPathName;
		if (!pathName.contains("/")) {
			directoryPathName = pathName.replace('.', de.schlichtherle.io.File.separatorChar);
		} else {
			directoryPathName = pathName.replace('/', de.schlichtherle.io.File.separatorChar);
		}

		String filePath = parentFile.getPath() + de.schlichtherle.io.File.separatorChar + directoryPathName;
		de.schlichtherle.io.File cachedFile = FILE_CACHE.get(filePath);
		if (cachedFile != null) {
			return cachedFile;
		}

		de.schlichtherle.io.File resolvedFile = new de.schlichtherle.io.File(filePath, ArchiveDetector.ALL);
		if (resolvedFile.exists()) {
			FILE_CACHE.put(filePath, resolvedFile);
			return resolvedFile;
		} else {
			return null;
		}
	}
	
	public static File getFileForPathName(String pathName, URL url) throws FileAccessException {

		File parentFile = getFileFromURL(url);

		return getFileForPathName(pathName, parentFile);
	}

	public static File getFileFromURL(URL url) throws FileAccessException {

		String pathString = url.toString();
		if (pathString.endsWith("!/")) {
			pathString = pathString.substring(4);
			pathString = pathString.substring(0, pathString.length() - 2);
		}

		File retVal;

		try {
			if (pathString.endsWith("/")) {
				retVal = new File(url.toURI());
			} else {
				retVal = new de.schlichtherle.io.File(new File(url.toURI()), ArchiveDetector.ALL);
			}
		} catch (URISyntaxException e) {
			throw new FileAccessException("Could not derive file from URL: " + url, e);
		}

		return retVal;
	}

    public static <T> T doWithFile(File file, FileInputStreamOperation<T> callback) {
    	
    	InputStream fiStream = null;
		
		try {
			if (file instanceof de.schlichtherle.io.File) {
				fiStream = new de.schlichtherle.io.FileInputStream(file);
			} else {
				fiStream = new FileInputStream(file);
			}
			return callback.execute(file.getPath(), fiStream);
		} catch (FileNotFoundException e) {
			throw new FileAccessException("Could not find referenced file: " + file.getPath(), e);
		} finally {
			
			try {
				if (fiStream != null) {
					fiStream.close();
					
				}
			} catch (IOException e) {
			} finally {
			
				if (file instanceof de.schlichtherle.io.File) {
					try {
					    de.schlichtherle.io.File.umount(); //TVFS.umount((de.schlichtherle.io.File)file);
					} catch (ArchiveException e) { // FsSyncException e) {
					}
				}
			}
		}
    }
}
