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
package org.jadira.scanner.introspection.types;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Set;

import javassist.bytecode.ClassFile;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.exception.FileAccessException;
import org.jadira.scanner.helper.InputStreamOperation;
import org.jadira.scanner.helper.FileUtils;
import org.jadira.scanner.helper.JavassistClassFileHelper;
import org.jadira.scanner.introspection.visitor.IntrospectionVisitor;
import org.jadira.scanner.resolver.ClasspathResolver;

public abstract class InspElement {

	private static final String CLASS_SUFFIX = ".class";
	
    private final String name;
	private final ClasspathResolver resolver;

    protected InspElement(String name, ClasspathResolver resolver) {
        this.name = name;
        this.resolver = resolver;
    }

    public String getName() {
        return name;
    }

    public abstract Set<InspAnnotation<?>> getAnnotations() throws ClasspathAccessException;

    public abstract <A extends Annotation> InspAnnotation<A> getAnnotation(Class<A> annotation) throws ClasspathAccessException;

    public abstract void acceptVisitor(IntrospectionVisitor visitor) throws ClasspathAccessException;

    public abstract InspElement getEnclosingElement();
    
    protected ClasspathResolver getResolver() {
    		return resolver;
	}
    
	protected static ClassFile findClassFile(final String name, ClasspathResolver resolver) throws ClasspathAccessException {

		ClassFile retVal = null;

		for (URL url : resolver.getClasspaths()) {

			final File nextFile = FileUtils.getFileForPathName(name.replace('.', '/') + CLASS_SUFFIX, url);
			
			if (nextFile != null) {
				retVal = FileUtils.doWithFile(nextFile, new InputStreamOperation<ClassFile>(){

					@Override
					public ClassFile execute(InputStream fileInputStream) {
						try {
							return JavassistClassFileHelper.constructClassFile(name, fileInputStream);
						} catch (IOException e) {
							throw new FileAccessException("Could not read referenced file: " + nextFile.getName(), e);
						}
					}
				});
				
				break;
			}			
		}
		return retVal;
	}
	
    @Override
    public String toString() {
    	
    	ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    	builder.append(this.name);
    	
    	return builder.toString();
    }
}