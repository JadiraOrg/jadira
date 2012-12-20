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
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.ClassFile;
import javassist.bytecode.FieldInfo;
import javassist.bytecode.InnerClassesAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;

import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.introspection.visitor.IntrospectionVisitor;
import org.jadira.scanner.resolver.ClasspathResolver;

public class InspClass extends InspType {

    protected InspClass(String name, ClasspathResolver resolver) throws ClasspathAccessException {
    	this(findClassFile(name, resolver), resolver);
    }

    protected InspClass(Class<?> clazz, ClasspathResolver resolver) throws ClasspathAccessException {
        this(findClassFile(clazz.getName(), resolver), resolver);
    }

    protected InspClass(ClassFile classFile, ClasspathResolver resolver) {
        super(classFile, resolver);
        if (classFile.isInterface()) {
            throw new IllegalArgumentException("Argument was interface: " + classFile.getName());
        }
        if (classFile.getSuperclass().equals("java.lang.Enum")) {
            throw new IllegalArgumentException("Argument was enum: " + classFile.getName());
        }
    }

    public static InspClass getInspClass(String name, ClasspathResolver resolver) throws ClasspathAccessException {
        return new InspClass(name, resolver);
    }

    public static InspClass getInspClass(ClassFile classFile, ClasspathResolver resolver) {
        return new InspClass(classFile, resolver);
    }

    public static InspClass getInspClass(Class<?> clazz, ClasspathResolver resolver) throws ClasspathAccessException {
        return new InspClass(clazz, resolver);
    }

    public InspClass getSuperClass() throws ClasspathAccessException {

        final String superClassFile = getClassFile().getSuperclass();
        return InspClass.getInspClass(superClassFile, getResolver());
    }

    public List<InspInterface> getImplementedInterfaces() throws ClasspathAccessException {

        final List<InspInterface> retVal = new ArrayList<InspInterface>();
        final String[] interfaces = getClassFile().getInterfaces();

        for (String next : interfaces) {
            retVal.add(InspInterface.getInspInterface(next, getResolver()));
        }
        return retVal;
    }

    @Override
    public Class<?> getActualClass() throws ClasspathAccessException {

        try {
            return Class.forName(getClassFile().getName());
        } catch (ClassNotFoundException e) {
            throw new ClasspathAccessException("Could not find class: " + getClassFile().getName(), e);
        }
    }

    // public Set<InspClass> getSubClasses()

    public List<InspInnerClass> getEnclosedClasses() throws ClasspathAccessException {

        final List<InspInnerClass> retVal = new ArrayList<InspInnerClass>();
        @SuppressWarnings("unchecked")
        List<AttributeInfo> attrs = (List<AttributeInfo>) getClassFile().getAttributes();
        for (AttributeInfo next : attrs) {
            if (next instanceof InnerClassesAttribute) {
                int innerClassCount = ((InnerClassesAttribute) next).tableLength();
                for (int i = 0; i < innerClassCount; i++) {
                    String innerName = ((InnerClassesAttribute) next).innerName(i);
                    // Skip anonymous classes - these are returned via method introspection instead
                    if (innerName != null) {
                        retVal.add(InspInnerClass.getInspInnerClass(this.getClassFile(), findClassFile(getName() + "$" + innerName, getResolver()), getResolver()));
                    }
                }
            }
        }
        return retVal;
    }

    public List<InspField> getFields() {

        final List<InspField> retVal = new ArrayList<InspField>();
        @SuppressWarnings("unchecked")
        final List<FieldInfo> fields = (List<FieldInfo>) getClassFile().getFields();

        for (FieldInfo next : fields) {
            retVal.add(InspField.getInspField(next, this, getResolver()));
        }
        return retVal;
    }

    public List<InspConstructor> getConstructors() throws ClasspathAccessException {

        final List<InspConstructor> retVal = new ArrayList<InspConstructor>();
        @SuppressWarnings("unchecked")
        final List<MethodInfo> methods = (List<MethodInfo>) getClassFile().getMethods();

        for (MethodInfo next : methods) {
            if (next.isConstructor()) {
                retVal.add(InspConstructor.getInspConstructor(next, this, getResolver()));
            }
        }
        
        if (retVal.isEmpty()) {
        	retVal.add(InspDefaultConstructor.getInspConstructor(null, this, getResolver()));
        }
        	
        return retVal;
    }

    @Override
    public Set<InspAnnotation<?>> getAnnotations() {

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

    public List<InspStaticInitializer> getStaticInitializers() {

        final List<InspStaticInitializer> retVal = new ArrayList<InspStaticInitializer>();
        @SuppressWarnings("unchecked")
        final List<MethodInfo> methods = (List<MethodInfo>) getClassFile().getMethods();

        for (MethodInfo next : methods) {
            if (next.isStaticInitializer()) {
                retVal.add(InspStaticInitializer.getInspStaticInitializer(next, this, getResolver()));
            }
        }
        return retVal;
    }

    @Override
    public void acceptVisitor(IntrospectionVisitor visitor) throws ClasspathAccessException {
        visitor.visit(this);

        for (InspInterface next : getImplementedInterfaces()) {
            next.acceptVisitor(visitor);
        }
        for (InspInnerClass next : getEnclosedClasses()) {
            next.acceptVisitor(visitor);
        }
        for (InspField next : getFields()) {
            next.acceptVisitor(visitor);
        }
        for (InspConstructor next : getConstructors()) {
            next.acceptVisitor(visitor);
        }
        for (InspMethod next : getMethods()) {
            next.acceptVisitor(visitor);
        }
        for (InspStaticInitializer next : getStaticInitializers()) {
            next.acceptVisitor(visitor);
        }
        for (InspAnnotation<?> next : getAnnotations()) {
            next.acceptVisitor(visitor);
        }
    }

    @Override
    public InspElement getEnclosingElement() {
        return getPackage();
    }
}