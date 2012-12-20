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
package org.jadira.scanner.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.WeakHashMap;

import org.jadira.scanner.exception.FileAccessException;

import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;
import de.schlichtherle.truezip.file.TVFS;
import de.schlichtherle.truezip.fs.FsSyncException;

public class FileUtils {

    private static final WeakHashMap<String, TFile> FILE_CACHE = new WeakHashMap<String, TFile>();
	
	private FileUtils() {
	}

	public static File getFileForPathName(String pathName, URL url) throws FileAccessException {

		File parentFile = getFileFromURL(url);

		final String directoryPathName;
		if (!pathName.contains("/")) {
			directoryPathName = pathName.replace('.', TFile.separatorChar);
		} else {
			directoryPathName = pathName.replace('/', TFile.separatorChar);
		}

		String filePath = parentFile.getPath() + TFile.separatorChar + directoryPathName;
		TFile cachedFile = FILE_CACHE.get(filePath);
		if (cachedFile != null) {
			return cachedFile;
		}

		TFile resolvedFile = new TFile(filePath, TArchiveDetector.ALL);
		if (resolvedFile.exists()) {
			FILE_CACHE.put(filePath, resolvedFile);
			return resolvedFile;
		} else {
			return null;
		}
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
				retVal = new TFile(url.toURI());
			}
		} catch (URISyntaxException e) {
			throw new FileAccessException("Could not derive file from URL: " + url, e);
		}

		return retVal;
	}

    public static <T> T doWithFile(File file, InputStreamOperation<T> callback) {
    	
    	InputStream fiStream = null;
		
		try {
			if (file instanceof TFile) {
				fiStream = new TFileInputStream(file);
			} else {
				fiStream = new FileInputStream(file);
			}
			return callback.execute(fiStream);
		} catch (FileNotFoundException e) {
			throw new FileAccessException("Could not find referenced file: " + file.getPath(), e);
		} finally {
			
			try {
				if (fiStream != null) {
					fiStream.close();
					
				}
			} catch (IOException e) {
			} finally {
			
				if (file instanceof TFile)
				try {
					TVFS.umount((TFile)file);
				} catch (FsSyncException e) {
				}
			}
		}
    }
}
