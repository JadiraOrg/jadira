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
package org.jadira.scanner.classfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javassist.bytecode.ClassFile;
import jsr166y.ForkJoinPool;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jadira.scanner.core.api.Allocator;
import org.jadira.scanner.core.helper.JavassistClassFileHelper;
import org.jadira.scanner.core.spi.AbstractFileResolver;
import org.jadira.scanner.file.locator.JdkBaseClasspathUrlLocator;

import de.schlichtherle.truezip.file.TFileInputStream;

public class ClassFileResolver extends AbstractFileResolver<ClassFile> {

	public static ForkJoinPool FORKJOIN_TASK = new ForkJoinPool();

    private static final List<URL> JDK_BASE_CLASSPATH_JARS = new JdkBaseClasspathUrlLocator().locate();

	private final ClassFileAssigner assigner = new ClassFileAssigner();
	
    public ClassFileResolver() {    	
        super(JDK_BASE_CLASSPATH_JARS);
	}

	public ClassFileResolver(List<URL> classpaths) {
		super(JDK_BASE_CLASSPATH_JARS);
		getDriverData().addAll(classpaths);
	}

	@Override
	public String toString() {

		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append(getDriverData().toArray());

		return builder.toString();
	}

	@Override
	protected Allocator<ClassFile, File> getAssigner() {
		return assigner;
	}

	private final class ClassFileAssigner implements Allocator<ClassFile, File> {

		@Override
		public ClassFile allocate(File e) {
			
			TFileInputStream tis = null;
			
			try {
				tis = new TFileInputStream(e);
				return JavassistClassFileHelper.constructClassFileForPath(e.getPath(), tis);
			} catch (FileNotFoundException e1) {
				throw new IllegalArgumentException(e + " is not a valid File", e1);
			} catch (IOException e1) {
				throw new IllegalArgumentException("Could not load ClassFile for " + e, e1);
			} finally {
				if (tis != null) {
					try {
						tis.close();
					} catch (IOException e1) {
						// Ignore
					}
				}
			}
		}
	}	
}