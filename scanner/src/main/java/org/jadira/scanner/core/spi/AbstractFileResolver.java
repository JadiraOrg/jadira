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
package org.jadira.scanner.core.spi;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.jadira.scanner.core.api.Allocator;
import org.jadira.scanner.core.helper.FileUtils;

public abstract class AbstractFileResolver<F> extends AbstractResolver<F, File, URL> {
	
	private static final Allocator<File, URL> FILE_ALLOCATOR = new Allocator<File, URL>() {
		@Override
		public File allocate(URL e) {
			return FileUtils.getFileFromURL(e);
	}};

    protected AbstractFileResolver() {
        super();
    }

    protected AbstractFileResolver(List<URL> classpaths) {
        super();
        getDriverData().addAll(classpaths);
    }

	@Override
	protected Allocator<File, URL> getAllocator() {
		return FILE_ALLOCATOR;
	}
}
