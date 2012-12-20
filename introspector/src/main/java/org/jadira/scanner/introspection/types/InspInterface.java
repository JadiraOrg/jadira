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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;

import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.introspection.visitor.IntrospectionVisitor;
import org.jadira.scanner.resolver.ClasspathResolver;

public class InspInterface extends InspType {

    protected InspInterface(String name, ClasspathResolver resolver) throws ClasspathAccessException {
        this(findClassFile(name, resolver), resolver);
    }

    protected InspInterface(ClassFile classFile, ClasspathResolver resolver) {
        super(classFile, resolver);
        if (!classFile.isInterface() || (classFile.getSuperclass().equals("java.lang.annotation.Annotation"))) {
            throw new IllegalArgumentException("Argument was not interface: " + classFile.getName());
        }
    }

    public static InspInterface getInspInterface(String name, ClasspathResolver resolver) throws ClasspathAccessException {
        return new InspInterface(name, resolver);
    }

    public static InspInterface getInspInterface(ClassFile classFile, ClasspathResolver resolver) {
        return new InspInterface(classFile, resolver);
    }

    public List<InspInterface> getSuperInterfaces() throws ClasspathAccessException {

        final List<InspInterface> retVal = new ArrayList<InspInterface>();
        String[] interfaces = getClassFile().getInterfaces();

        for (String next : interfaces) {
            retVal.add(InspInterface.getInspInterface(next, getResolver()));
        }
        return retVal;
    }

    public List<InspMethod> getMethods() {

        final List<InspMethod> retVal = new ArrayList<InspMethod>();
        @SuppressWarnings("unchecked")
        final List<MethodInfo> methods = (List<MethodInfo>) getClassFile().getMethods();

        for (MethodInfo next : methods) {
            if (next.isMethod()) {
                retVal.add(InspMethod.getInspMethod(next, this, getResolver()));
            }
        }
        return retVal;
    }

    public Class<?> getActualInterface() throws ClasspathAccessException {

        try {
            return Class.forName(getClassFile().getName());
        } catch (ClassNotFoundException e) {
            throw new ClasspathAccessException("Could not find class: " + getClassFile().getName(), e);
        }
    }

    @Override
    public Set<InspAnnotation<?>> getAnnotations() throws ClasspathAccessException {

        AnnotationsAttribute visible = (AnnotationsAttribute) getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
        AnnotationsAttribute invisible = (AnnotationsAttribute) getClassFile().getAttribute(AnnotationsAttribute.invisibleTag);

        Set<InspAnnotation<?>> annotations = new HashSet<InspAnnotation<?>>();

        List<Annotation> annotationsList = new ArrayList<Annotation>();
        if (visible != null) {
            annotationsList.addAll(Arrays.asList(visible.getAnnotations()));
        }
        if (invisible != null) {
            annotationsList.addAll(Arrays.asList(invisible.getAnnotations()));
        }

        for (Annotation nextAnnotation : annotationsList) {
            annotations.add(InspAnnotation.getInspAnnotation(nextAnnotation, this, getResolver()));
        }

        return annotations;
    }

    @Override
    public InspPackage getPackage() throws ClasspathAccessException {

        String fqClassName = getClassFile().getName();

        String packageName;
        if (fqClassName.contains(".")) {
            packageName = fqClassName.substring(0, fqClassName.lastIndexOf('.'));
        } else {
            packageName = "";
        }

        return InspPackage.getInspPackage(packageName, getResolver());
    }

    @Override
    public Class<?> getActualClass() throws ClasspathAccessException {
        return getActualInterface();
    }

    // public Set<InspInterface> getSubInterfaces()

    // public Set<InspClass> getImplementingClasses()

    // public Set<InspEnum> getImplementingEnums()

    @Override
    public void acceptVisitor(IntrospectionVisitor visitor) throws ClasspathAccessException {
        visitor.visit(this);

        for (InspInterface next : getSuperInterfaces()) {
            next.acceptVisitor(visitor);
        }
        for (InspMethod next : getMethods()) {
            next.acceptVisitor(visitor);
        }
        for (InspAnnotation<?> next : getAnnotations()) {
            next.acceptVisitor(visitor);
        }
    }

    @Override
    public InspPackage getEnclosingElement() {
        return getPackage();
    }
}