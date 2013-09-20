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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.classpath.filter.JClassImplementsFilter;
import org.jadira.scanner.classpath.filter.JElementTypeFilter;
import org.jadira.scanner.classpath.filter.JTypeSubTypeOfFilter;
import org.jadira.scanner.classpath.projector.ClasspathProjector;
import org.jadira.scanner.classpath.visitor.IntrospectionVisitor;
import org.jadira.scanner.core.api.Projector;
import org.jadira.scanner.core.exception.ClasspathAccessException;

public class JInterface extends JType {

    private static final Projector<File> CLASSPATH_PROJECTOR = ClasspathProjector.SINGLETON;
    
    protected JInterface(String name, ClasspathResolver resolver) throws ClasspathAccessException {
        this(findClassFile(name, resolver), resolver);
    }

    protected JInterface(ClassFile classFile, ClasspathResolver resolver) {
        super(classFile, resolver);
        if (!classFile.isInterface() || (classFile.getSuperclass().equals("java.lang.annotation.Annotation"))) {
            throw new IllegalArgumentException("Argument was not interface: " + classFile.getName());
        }
    }
    

    public static JInterface getJInterface(String name, ClasspathResolver resolver) throws ClasspathAccessException {
        return new JInterface(name, resolver);
    }

    public static JInterface getJInterface(ClassFile classFile, ClasspathResolver resolver) {
        return new JInterface(classFile, resolver);
    }

    public List<JInterface> getSuperInterfaces() throws ClasspathAccessException {

        final List<JInterface> retVal = new ArrayList<JInterface>();
        String[] interfaces = getClassFile().getInterfaces();

        for (String next : interfaces) {
            retVal.add(JInterface.getJInterface(next, getResolver()));
        }
        return retVal;
    }
    
    public List<Class<?>> getActualSuperInterfaces() throws ClasspathAccessException {

        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (JInterface next : getSuperInterfaces()) {
            classes.add(next.getActualClass());
        }
        return classes;
    }

    public List<JMethod> getMethods() {

        final List<JMethod> retVal = new ArrayList<JMethod>();
        @SuppressWarnings("unchecked")
        final List<MethodInfo> methods = (List<MethodInfo>) getClassFile().getMethods();

        for (MethodInfo next : methods) {
            if (next.isMethod()) {
                retVal.add(JMethod.getJMethod(next, this, getResolver()));
            }
        }
        return retVal;
    }

    public Class<?> getActualInterface() throws ClasspathAccessException {

        return getResolver().loadClass(getClassFile().getName());
    }

    @Override
    public Set<JAnnotation<?>> getAnnotations() throws ClasspathAccessException {

        AnnotationsAttribute visible = (AnnotationsAttribute) getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
        AnnotationsAttribute invisible = (AnnotationsAttribute) getClassFile().getAttribute(AnnotationsAttribute.invisibleTag);

        Set<JAnnotation<?>> annotations = new HashSet<JAnnotation<?>>();

        List<Annotation> annotationsList = new ArrayList<Annotation>();
        if (visible != null) {
            annotationsList.addAll(Arrays.asList(visible.getAnnotations()));
        }
        if (invisible != null) {
            annotationsList.addAll(Arrays.asList(invisible.getAnnotations()));
        }

        for (Annotation nextAnnotation : annotationsList) {
            annotations.add(JAnnotation.getJAnnotation(nextAnnotation, this, getResolver()));
        }

        return annotations;
    }

    @Override
    public JPackage getPackage() throws ClasspathAccessException {

        String fqClassName = getClassFile().getName();

        String packageName;
        if (fqClassName.contains(".")) {
            packageName = fqClassName.substring(0, fqClassName.lastIndexOf('.'));
        } else {
            packageName = "";
        }

        return JPackage.getJPackage(packageName, getResolver());
    }

    @Override
    public Class<?> getActualClass() throws ClasspathAccessException {
        return getActualInterface();
    }

    public Set<JInterface> getSubInterfaces() {
        
        Set<JInterface> retVal = new HashSet<JInterface>();
        List<? extends ClassFile> classes = getResolver().getClassFileResolver().resolveAll(null, CLASSPATH_PROJECTOR, new JTypeSubTypeOfFilter(this.getActualClass()), new JElementTypeFilter(JInterface.class)); 
        for (ClassFile classFile : classes) {
            retVal.add(JInterface.getJInterface(classFile, getResolver()));
        }
        return retVal;
    }

    public Set<JClass> getImplementingClasses() {
        
        Set<JClass> retVal = new HashSet<JClass>();
        List<? extends ClassFile> classes = getResolver().getClassFileResolver().resolveAll(null, CLASSPATH_PROJECTOR, new JClassImplementsFilter(this.getActualClass()), new JElementTypeFilter(JClass.class)); 
        for (ClassFile classFile : classes) {
            retVal.add(JClass.getJClass(classFile, getResolver()));
        }
        return retVal;
    }

    @Override
    public void acceptVisitor(IntrospectionVisitor visitor) throws ClasspathAccessException {
        visitor.visit(this);

        for (JInterface next : getSuperInterfaces()) {
            next.acceptVisitor(visitor);
        }
        for (JMethod next : getMethods()) {
            next.acceptVisitor(visitor);
        }
        for (JAnnotation<?> next : getAnnotations()) {
            next.acceptVisitor(visitor);
        }
    }

    @Override
    public JPackage getEnclosingElement() {
        return getPackage();
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
		// JInterface rhs = (JInterface) obj;
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