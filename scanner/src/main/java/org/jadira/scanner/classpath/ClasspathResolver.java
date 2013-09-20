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
package org.jadira.scanner.classpath;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javassist.bytecode.ClassFile;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jadira.scanner.classfile.ClassFileResolver;
import org.jadira.scanner.classpath.types.JClass;
import org.jadira.scanner.classpath.types.JElement;
import org.jadira.scanner.classpath.types.JInterface;
import org.jadira.scanner.classpath.types.JPackage;
import org.jadira.scanner.core.api.Allocator;
import org.jadira.scanner.core.exception.ClasspathAccessException;
import org.jadira.scanner.core.exception.FileAccessException;
import org.jadira.scanner.core.helper.FileInputStreamOperation;
import org.jadira.scanner.core.helper.FileUtils;
import org.jadira.scanner.core.helper.JavassistClassFileHelper;
import org.jadira.scanner.core.helper.filenamefilter.ClassFilenameFilter;
import org.jadira.scanner.core.spi.AbstractFileResolver;
import org.jadira.scanner.core.utils.reflection.ClassLoaderUtils;
import org.jadira.scanner.file.locator.JdkBaseClasspathUrlLocator;

import de.schlichtherle.io.FileInputStream;

public class ClasspathResolver extends AbstractFileResolver<JElement> {

    private static final List<URL> JDK_BASE_CLASSPATH_JARS = new JdkBaseClasspathUrlLocator().locate();
    
	private static final ClassFilenameFilter CLASSFILE_FILTER = new ClassFilenameFilter();
	
	private final ClasspathAssigner assigner = new ClasspathAssigner();
	
	private final ClassFileResolver classFileResolver;

    private final ClassLoader[] classLoaders;
	
    public ClasspathResolver() {    	
        super(JDK_BASE_CLASSPATH_JARS);
        this.classLoaders  = ClassLoaderUtils.getClassLoaders();
        classFileResolver = new ClassFileResolver(classLoaders);
	}

	public ClasspathResolver(List<URL> classpaths) {
		super();
		this.classLoaders = ClassLoaderUtils.getClassLoaders();
		classFileResolver = new ClassFileResolver(classpaths, this.classLoaders);
		getDriverData().addAll(classpaths);
	}
	
    public ClasspathResolver(ClassLoader... classLoaders) {
        super(JDK_BASE_CLASSPATH_JARS);
        this.classLoaders  = ClassLoaderUtils.getClassLoaders(classLoaders);
        classFileResolver = new ClassFileResolver(this.classLoaders);
	}

    public ClasspathResolver(List<URL> classpaths, List<ClassLoader> classLoaders) {
        super();
        this.classLoaders  = ClassLoaderUtils.getClassLoaders((ClassLoader[])classLoaders.toArray());
        classFileResolver = new ClassFileResolver(classpaths, this.classLoaders);
        getDriverData().addAll(classpaths);
    }
    
	public ClasspathResolver(List<URL> classpaths, ClassLoader... classLoaders) {
		super();
		this.classLoaders  = ClassLoaderUtils.getClassLoaders(classLoaders);
		classFileResolver = new ClassFileResolver(classpaths, this.classLoaders);
		getDriverData().addAll(classpaths);
	}

	@Override
	public String toString() {

		ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
		builder.append(getDriverData().toArray());

		return builder.toString();
	}

	@Override
	protected Allocator<JElement, File> getAssigner() {
		return assigner;
	}

	private final class ClasspathAssigner implements Allocator<JElement, File> {

		@Override
		public JElement allocate(File e) {

			JElement element;
			if (e.isDirectory()) {
				// Attempt to construct package
				element = assignPackage(e);
			} else {
				try {
					ClassFile f = JavassistClassFileHelper.constructClassFileForPath(e.getPath(), new FileInputStream(e));
					if (f.isInterface()) {
						return JInterface.getJInterface(f, ClasspathResolver.this);
					} else {
						return JClass.getJClass(f, ClasspathResolver.this);
					}
				} catch (FileNotFoundException e1) {
					throw new ClasspathAccessException("Couldnt find file", e1);
				} catch (IOException e1) {
					throw new ClasspathAccessException("Couldnt access file", e1);
				}
			}
			return element;
		}

		private JPackage assignPackage(File e) {

			JPackage retVal;

			final File[] classes = e.listFiles(CLASSFILE_FILTER);
			for (int i = 0; i < classes.length; i++) {

				ClassFile classFile = FileUtils.doWithFile(classes[i], new FileInputStreamOperation<ClassFile>() {

					@Override
					public ClassFile execute(String path, InputStream fileInputStream) {

						try {
							return JavassistClassFileHelper.constructClassFile(path, fileInputStream);
						} catch (IOException e) {
							throw new FileAccessException("Cannot access class file: " + e.getMessage(), e);
						}
					}
				});

				Package pkg = loadClass(classFile.getName()).getPackage();
				retVal = JPackage.getJPackage(pkg, ClasspathResolver.this);

				if (retVal != null) {
					return retVal;
				}
			}
			return null;
		}
	}	
	
	public ClassFileResolver getClassFileResolver() {
		return classFileResolver;
	}
	
	public Class<?> loadClass(String className) {
	    
	    for (int i = 0; i < classLoaders.length; i++) {
            try {
                return Class.forName(className, true, classLoaders[i]);
            } catch (ClassNotFoundException e) {
                // Ignore
            }
	    }
	    throw new ClasspathAccessException("Could not find class: " + className);
	}
}