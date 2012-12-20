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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.bytecode.ClassFile;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.exception.FileAccessException;
import org.jadira.scanner.filenamefilter.ClassFilenameFilter;
import org.jadira.scanner.helper.FileUtils;
import org.jadira.scanner.helper.InputStreamOperation;
import org.jadira.scanner.helper.JavassistAnnotationsHelper;
import org.jadira.scanner.helper.JavassistClassFileHelper;
import org.jadira.scanner.introspection.visitor.IntrospectionVisitor;
import org.jadira.scanner.resolver.ClasspathResolver;

public class InspPackage extends InspElement {

	private static final ClassFilenameFilter CLASSFILE_FILTER = new ClassFilenameFilter();
	
    private Package wrappedPackage;
    
    protected InspPackage(String name, ClasspathResolver resolver) throws ClasspathAccessException {
        super(name, resolver);
        wrappedPackage = findPackage(name, resolver);
    }

    protected InspPackage(Package wrappedPackage, ClasspathResolver resolver) {
        super(wrappedPackage.getName(), resolver);
        this.wrappedPackage = wrappedPackage;
    }

    public static InspPackage getInspPackage(String name, ClasspathResolver resolver) throws ClasspathAccessException {
        return new InspPackage(name, resolver);
    }

    public static InspPackage getInspPackage(Package pkg, ClasspathResolver resolver) throws ClasspathAccessException {
        return new InspPackage(pkg, resolver);
    }

    public Set<InspClass> getClasses() throws ClasspathAccessException {

        Set<InspClass> retVal = new HashSet<InspClass>();
        List<ClassFile> classes = findClassFilesForPackage(getName(), getResolver());
        for (ClassFile classFile : classes) {

            if (!classFile.isInterface() && (!classFile.getSuperclass().equals("java.lang.Enum")))
                retVal.add(InspClass.getInspClass(classFile, getResolver()));
        }
        return retVal;
    }

    public Set<InspInterface> getInterfaces() throws ClasspathAccessException {

        Set<InspInterface> retVal = new HashSet<InspInterface>();
        List<ClassFile> classes = findClassFilesForPackage(getName(), getResolver());
        for (ClassFile classFile : classes) {
            if (classFile.isInterface() && (!classFile.getSuperclass().equals("java.lang.annotation.Annotation")))
                retVal.add(InspInterface.getInspInterface(classFile, getResolver()));
        }
        return retVal;
    }

    @Override
    public Set<InspAnnotation<?>> getAnnotations() throws ClasspathAccessException {

        Set<InspAnnotation<?>> retVal = new HashSet<InspAnnotation<?>>();
        List<ClassFile> classes = findClassFilesForPackage(getName(), getResolver());
        for (ClassFile classFile : classes) {
            if (classFile.isInterface() && (classFile.getSuperclass().equals("java.lang.annotation.Annotation")))
                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) (Class.forName(classFile.getName()));
                    Annotation theAnnotation = annotationClass.newInstance();
                    retVal.add(InspAnnotation.getInspAnnotation(theAnnotation, this, getResolver()));
                } catch (ClassNotFoundException e) {
                    throw new ClasspathAccessException("Cannot find class for annotation: " + e.getMessage(), e);
                } catch (InstantiationException e) {
                    throw new ClasspathAccessException("Cannot instantiate annotation: " + e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    throw new ClasspathAccessException("Cannot access annotation: " + e.getMessage(), e);
                }
        }
        return retVal;
    }

    public Set<InspEnum> getEnums() throws ClasspathAccessException {

        Set<InspEnum> retVal = new HashSet<InspEnum>();
        List<ClassFile> classes = findClassFilesForPackage(getName(), getResolver());
        for (ClassFile classFile : classes) {

            if (!classFile.isInterface() && (classFile.getSuperclass().equals("java.lang.Enum"))) {

                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends Enum<?>> enumeration = (Class<? extends Enum<?>>) Class.forName(classFile.getName());
                    retVal.add(InspEnum.getInspEnum(enumeration, getResolver()));
                } catch (ClassNotFoundException e) {
                    throw new ClasspathAccessException("Cannot find class for enumeration: " + e.getMessage(), e);
                }
            }
        }
        return retVal;
    }

    public Set<InspPackage> getChildPackages() throws ClasspathAccessException {

        Set<InspPackage> retVal = new HashSet<InspPackage>();
        List<Package> packages = findChildPackagesForPackage(getName(), getResolver());
        for (Package pkg : packages) {

            retVal.add(getInspPackage(pkg, getResolver()));
        }
        return retVal;
    }

    public Package getActualPackage() {
        return wrappedPackage;
    }

    @Override
    public <A extends java.lang.annotation.Annotation> InspAnnotation<A> getAnnotation(Class<A> annotation)  throws ClasspathAccessException {

        Set<InspAnnotation<?>> inspAnnotations = getAnnotations();
        for (InspAnnotation<?> next : inspAnnotations) {
            if (next.getName().equals(annotation.getName())
                    && (next.getActualAnnotation().getClass().equals(annotation.getClass()))) {
                @SuppressWarnings("unchecked") InspAnnotation<A> retVal = (InspAnnotation<A>) next;
                return retVal;
            }
        }
        return null;
    }
    
    public InspPackage getParentPackage() throws ClasspathAccessException {

    	String name = getName();
    	
        Package retVal = null;
        while (retVal == null && name.lastIndexOf(".") != -1) {
            name = name.substring(0, name.lastIndexOf("."));
            retVal = findPackage(name, getResolver());
        }
        if (retVal == null) {
            retVal = findPackage("", getResolver());
        }
        return InspPackage.getInspPackage(retVal, getResolver());
    }
    
	public Annotation[] getAnnotationsForPackage() {

		ClassFile cf = findClassFile(getName() + ".package-info.class", getResolver());

		if (cf == null) {
			return new Annotation[] {};
		}

		return JavassistAnnotationsHelper.getAnnotationsForClass(cf);
	}
    
    @Override
    public void acceptVisitor(IntrospectionVisitor visitor) throws ClasspathAccessException {
        visitor.visit(this);

        for (InspInterface next : getInterfaces()) {
            next.acceptVisitor(visitor);
        }
        for (InspClass next : getClasses()) {
            next.acceptVisitor(visitor);
        }
        for (InspEnum next : getEnums()) {
            next.acceptVisitor(visitor);
        }
        for (InspAnnotation<?> next : getAnnotations()) {
            next.acceptVisitor(visitor);
        }
        for (InspPackage next : getChildPackages()) {
            next.acceptVisitor(visitor);
        }
    }

    @Override
    public InspElement getEnclosingElement() {
        return null;
    }
    
	private static Package findPackage(final String packageName, ClasspathResolver resolver) throws ClasspathAccessException {

		Package retVal = null;
		try {
			retVal = Class.forName(packageName + ".package-info").getPackage();
		} catch (ClassNotFoundException e) {
			// Ignore
		}
		if (retVal == null) {

			for (URL url : resolver.getClasspaths()) {
				
				File nextFile = FileUtils.getFileForPathName(packageName, url);
				if (nextFile != null) {

					final File[] classes = nextFile.listFiles(CLASSFILE_FILTER);
					for (int i = 0; i < classes.length; i++) {
						
						final int idx = i;
						ClassFile classFile = FileUtils.doWithFile(classes[i], new InputStreamOperation<ClassFile>(){

								@Override
								public ClassFile execute(InputStream fileInputStream) {
									
									try {
										return JavassistClassFileHelper.constructClassFile(packageName + "." + classes[idx].getName().substring(0, classes[idx].getName().length() - ".class".length()), fileInputStream);
									} catch (IOException e) {
										throw new FileAccessException("Cannot access class file: " + e.getMessage(), e);
									}
								}
						});
						
						try {
							retVal = Class.forName(classFile.getName()).getPackage();
						} catch (ClassNotFoundException e) {
							throw new FileAccessException("Cannot access class file: " + e.getMessage(), e);
						}
						
						if (retVal != null) {
							return retVal;
						}
					}
				}
			}
		}
		if (retVal == null) {
			throw new ClasspathAccessException("Cannot find package: " + packageName);
		}
		return retVal;
	}

	private static List<Package> findChildPackagesForPackage(String name, ClasspathResolver resolver) throws ClasspathAccessException {

		List<Package> retVal = new ArrayList<Package>();

		for (URL url : resolver.getClasspaths()) {

			File nextFile = FileUtils.getFileForPathName(name, url);

			if (nextFile != null) {

				File[] files = nextFile.listFiles();

				for (int i = 0; i < files.length; i++) {
					if (files[i].isDirectory()) {
						retVal.add(findPackage(name + "." + files[i].getName(), resolver));
					}
				}
			}
		}
		return retVal;
	}

	public static List<ClassFile> findClassFilesForPackage(final String packageName, ClasspathResolver resolver) throws ClasspathAccessException {

		List<ClassFile> retVal = new ArrayList<ClassFile>();

		for (URL url : resolver.getClasspaths()) {

			File nextFile = FileUtils.getFileForPathName(packageName, url);
			
			if (nextFile != null) {
				
				final File[] classes = nextFile.listFiles(CLASSFILE_FILTER);
				for (int i = 0; i < classes.length; i++) {

					final int idx = i;
					
					retVal.add(FileUtils.doWithFile(classes[i], new InputStreamOperation<ClassFile>(){

							@Override
							public ClassFile execute(InputStream fileInputStream) {
								try {
									
									return JavassistClassFileHelper.constructClassFile(packageName + "." + classes[idx].getName().substring(0, classes[idx].getName().length() - ".class".length()), fileInputStream);
								} catch (IOException e) {
									throw new FileAccessException("Could not read referenced file: " + classes[idx].getName(), e);
								}
							}
					}));
				}
				break;
			}			
		}
		return retVal;
	}
	
    @Override
    public String toString() {
    	
    	ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    	builder.append("name", this.getName());
    	
    	return builder.toString();
    }
}