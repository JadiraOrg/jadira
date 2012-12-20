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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.bytecode.ClassFile;

import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.introspection.visitor.IntrospectionVisitor;
import org.jadira.scanner.resolver.ClasspathResolver;

public class InspEnum extends InspType {

    private Class<? extends Enum<?>> enumeration;

    @SuppressWarnings("unchecked")
    protected InspEnum(ClassFile classFile, ClasspathResolver resolver) throws ClasspathAccessException {

        super(classFile, resolver);
        Class<?> enumeration;
        try {
            enumeration = (Class<?>) Class.forName(classFile.getName());
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Argument could not be found: " + classFile.getName());
        }
        if (enumeration.isEnum()) {
            this.enumeration = (Class<? extends Enum<?>>) enumeration;
        } else {
            throw new IllegalArgumentException("Argument was not enum: " + classFile.getName());
        }
    }

    @SuppressWarnings("unchecked")
    protected InspEnum(String name, ClasspathResolver resolver) {

        super(findClassFile(name, resolver), resolver);
        Class<?> enumeration;
        try {
            enumeration = (Class<?>) Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Argument could not be found: " + name);
        }
        if (enumeration.isEnum()) {
            this.enumeration = (Class<? extends Enum<?>>) enumeration;
        } else {
            throw new IllegalArgumentException("Argument was not enum: " + name);
        }

    }

    protected InspEnum(Class<? extends Enum<?>> enumeration, ClasspathResolver resolver) {
        super(findClassFile(enumeration.getName(), resolver), resolver);
        this.enumeration = enumeration;
    }

    public static InspEnum getInspEnum(String name, ClasspathResolver resolver) throws ClasspathAccessException {
        return new InspEnum(name, resolver);
    }

    public static InspEnum getInspEnum(ClassFile classFile, ClasspathResolver resolver) throws ClasspathAccessException {
        return new InspEnum(classFile, resolver);
    }

    public static InspEnum getInspEnum(Class<? extends Enum<?>> enumeration, ClasspathResolver resolver) {
        return new InspEnum(enumeration, resolver);
    }

    public List<InspInterface> getSuperInterfaces() throws ClasspathAccessException {

        final List<InspInterface> retVal = new ArrayList<InspInterface>();
        final Class<?>[] interfaces = enumeration.getInterfaces();

        for (Class<?> next : interfaces) {
            retVal.add(InspInterface.getInspInterface(next.getName(), getResolver()));
        }
        return retVal;
    }

    public Class<? extends Enum<?>> getActualEnum() {
        return enumeration;
    }

    @Override
    public Set<InspAnnotation<?>> getAnnotations() throws ClasspathAccessException {

        Annotation[] annotations = enumeration.getAnnotations();
        Set<InspAnnotation<?>> retVal = new HashSet<InspAnnotation<?>>();

        for (int i = 0; i < annotations.length; i++) {
            retVal.add(InspAnnotation.getInspAnnotation(annotations[i], this, getResolver()));
        }

        return retVal;
    }

    @Override
    public InspPackage getPackage() throws ClasspathAccessException {
        return new InspPackage(enumeration.getPackage(), getResolver());
    }

    @Override
    public Class<?> getActualClass() throws ClasspathAccessException {
        return getActualEnum().getClass();
    }

    // public List<InspType> getTypeParams()

    @Override
    public void acceptVisitor(IntrospectionVisitor visitor) throws ClasspathAccessException {
        visitor.visit(this);

        for (InspInterface next : getSuperInterfaces()) {
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