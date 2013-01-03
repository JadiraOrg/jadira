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

import java.lang.reflect.Constructor;

import javassist.bytecode.MethodInfo;

import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.helper.JavassistMethodInfoHelper;
import org.jadira.scanner.introspection.visitor.IntrospectionVisitor;
import org.jadira.scanner.resolver.ClasspathResolver;

public class InspConstructor extends InspOperation {

    protected InspConstructor(MethodInfo methodInfo, InspClass inspClass, ClasspathResolver resolver) {
        super(methodInfo, inspClass, resolver);
    }

    public static InspConstructor getInspConstructor(MethodInfo methodInfo, InspClass inspClass, ClasspathResolver resolver) {
        return new InspConstructor(methodInfo, inspClass, resolver);
    }

    public Constructor<?> getActualConstructor() throws ClasspathAccessException {

        Class<?>[] methodParams = JavassistMethodInfoHelper.getMethodParamClasses(getMethodInfo());

        try {
            Class<?> clazz = ((InspClass) getEnclosingType()).getActualClass();
            return clazz.getDeclaredConstructor(methodParams);
        } catch (SecurityException e) {
            throw new ClasspathAccessException("Could not access constructor: " + e, e);
        } catch (NoSuchMethodException e) {
            throw new ClasspathAccessException("Could not find constructor: " + e, e);
        }
    }

    @Override
    public void acceptVisitor(IntrospectionVisitor visitor) throws ClasspathAccessException {
        visitor.visit(this);

        for (InspParameter next : getParameters()) {
            next.acceptVisitor(visitor);
        }
        for (InspAnnotation<?> next : getAnnotations()) {
            next.acceptVisitor(visitor);
        }
    }

    @Override
    public InspClass getEnclosingElement() {
        return (InspClass)super.getEnclosingElement();
    }
}