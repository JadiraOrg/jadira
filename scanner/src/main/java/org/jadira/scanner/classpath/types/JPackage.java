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
package org.jadira.scanner.classpath.types;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.bytecode.ClassFile;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jadira.scanner.classfile.filter.PackageFileFilter;
import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.classpath.filter.JElementTypeFilter;
import org.jadira.scanner.classpath.filter.PackageFilter;
import org.jadira.scanner.classpath.filter.PackagePrefixFilter;
import org.jadira.scanner.classpath.projector.ClasspathProjector;
import org.jadira.scanner.classpath.visitor.IntrospectionVisitor;
import org.jadira.scanner.core.api.Projector;
import org.jadira.scanner.core.exception.ClasspathAccessException;
import org.jadira.scanner.core.helper.JavassistAnnotationsHelper;

public class JPackage extends JElement {

	private static final Projector<File> CLASSPATH_PROJECTOR = ClasspathProjector.SINGLETON;
	
    private Package wrappedPackage;
    
    protected JPackage(String name, ClasspathResolver resolver) throws ClasspathAccessException {
        super(name, resolver);
        wrappedPackage = Package.getPackage(name);
    }

    protected JPackage(Package wrappedPackage, ClasspathResolver resolver) {
        super(wrappedPackage.getName(), resolver);
        this.wrappedPackage = wrappedPackage;
    }

    public static JPackage getJPackage(String name, ClasspathResolver resolver) throws ClasspathAccessException {
        return new JPackage(name, resolver);
    }

    public static JPackage getJPackage(Package pkg, ClasspathResolver resolver) throws ClasspathAccessException {
        return new JPackage(pkg, resolver);
    }

    public Set<JClass> getClasses() throws ClasspathAccessException {

        Set<JClass> retVal = new HashSet<JClass>();
        List<? extends ClassFile> classes = getResolver().getClassFileResolver().resolveAll(null, CLASSPATH_PROJECTOR, new PackageFileFilter(getName(), false), new PackagePrefixFilter(this), new JElementTypeFilter(JClass.class)); 
        for (ClassFile classFile : classes) {

        	if ((classFile.getSuperclass() != null) && 
        			(!classFile.isInterface() && (!classFile.getSuperclass().equals("java.lang.Enum"))
	            		&& (classFile.getInnerAccessFlags() == -1))) {
        		retVal.add(JClass.getJClass(classFile, getResolver()));
        	}
        }
        return retVal;
    }

    public Set<JInterface> getInterfaces() throws ClasspathAccessException {

        Set<JInterface> retVal = new HashSet<JInterface>();
        List<? extends ClassFile> classes = getResolver().getClassFileResolver().resolveAll(null, CLASSPATH_PROJECTOR, new PackageFileFilter(getName(), false), new PackageFilter(this), new JElementTypeFilter(JInterface.class));
        for (ClassFile classFile : classes) {
            if (classFile.isInterface() && (!classFile.getSuperclass().equals("java.lang.annotation.Annotation"))) {
                retVal.add(JInterface.getJInterface(classFile, getResolver()));
            }
        }
        return retVal;
    }

    @Override
    public Set<JAnnotation<?>> getAnnotations() throws ClasspathAccessException {

        Set<JAnnotation<?>> retVal = new HashSet<JAnnotation<?>>();
        List<? extends ClassFile> classes = getResolver().getClassFileResolver().resolveAll(null, CLASSPATH_PROJECTOR, new PackageFileFilter(getName(), false), new PackageFilter(this), new JElementTypeFilter(JAnnotation.class));
        for (ClassFile classFile : classes) {
            if (classFile.isInterface() && (classFile.getSuperclass().equals("java.lang.annotation.Annotation"))) {
                try {
                    @SuppressWarnings("unchecked")
                    Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) (getResolver().loadClass(classFile.getName()));
                    Annotation theAnnotation = annotationClass.newInstance();
                    retVal.add(JAnnotation.getJAnnotation(theAnnotation, this, getResolver()));
                } catch (InstantiationException e) {
                    throw new ClasspathAccessException("Cannot instantiate annotation: " + e.getMessage(), e);
                } catch (IllegalAccessException e) {
                    throw new ClasspathAccessException("Cannot access annotation: " + e.getMessage(), e);
                }
            }
        }
        return retVal;
    }

//    public Set<JPackage> getChildPackages() throws ClasspathAccessException {
//
//        Set<JPackage> retVal = new HashSet<JPackage>();
//        List<ClassFile> classes = getResolver().getClassFileResolver().resolveAll(null, PACKAGE_PROJECTOR);
//        for (Package pkg : packages) {
//
//            retVal.add(getJPackage(pkg, getResolver()));
//        }
//        return retVal;
//    }

    public Package getActualPackage() {
        return wrappedPackage;
    }

    @Override
    public <A extends java.lang.annotation.Annotation> JAnnotation<A> getAnnotation(Class<A> annotation)  throws ClasspathAccessException {

        Set<JAnnotation<?>> inspAnnotations = getAnnotations();
        for (JAnnotation<?> next : inspAnnotations) {
            if (next.getName().equals(annotation.getName())
                    && (next.getActualAnnotation().getClass().equals(annotation.getClass()))) {
                @SuppressWarnings("unchecked") JAnnotation<A> retVal = (JAnnotation<A>) next;
                return retVal;
            }
        }
        return null;
    }
    
    public JPackage getParentPackage() throws ClasspathAccessException {

    	String name = getName();
    	
        Package retVal = null;
        while (retVal == null && name.lastIndexOf('.') != -1) {
            name = name.substring(0, name.lastIndexOf('.'));
            retVal = Package.getPackage(name);
        }
        if (retVal == null) {
            retVal = Package.getPackage("");
        }
        return JPackage.getJPackage(retVal, getResolver());
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

        for (JInterface next : getInterfaces()) {
            next.acceptVisitor(visitor);
        }
        for (JClass next : getClasses()) {
            next.acceptVisitor(visitor);
        }
        for (JAnnotation<?> next : getAnnotations()) {
            next.acceptVisitor(visitor);
        }
//        for (JPackage next : getChildPackages()) {
//            next.acceptVisitor(visitor);
//        }
    }

    @Override
    public JElement getEnclosingElement() {
        return null;
    }

//	private static List<Package> findChildPackagesForPackage(String name, ClasspathResolver resolver) throws ClasspathAccessException {
//
//		List<Package> retVal = new ArrayList<Package>();
//
//		for (URL url : resolver.getClasspaths()) {
//
//			File nextFile = FileUtils.getFileForPathName(name, url);
//
//			if (nextFile != null) {
//
//				File[] files = nextFile.listFiles();
//
//				for (int i = 0; i < files.length; i++) {
//					if (files[i].isDirectory()) {
//						retVal.add(findPackage(name + "." + files[i].getName(), resolver));
//					}
//				}
//			}
//		}
//		return retVal;
//	}

    @Override
    public String toString() {
    	
    	ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
    	builder.append("name", this.getName());
    	
    	return builder.toString();
    }
    
    @Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		// JPackage rhs = (JPackage) obj;
		return new EqualsBuilder()
			 	.appendSuper(super.equals(obj))
				.isEquals();
	}

    @Override
	public int hashCode() {
		return new HashCodeBuilder(11, 47).append(super.hashCode())
				.toHashCode();
	}    
}