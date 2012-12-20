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

import java.lang.reflect.Method;
import java.util.List;

import javassist.bytecode.MethodInfo;

import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.introspection.visitor.IntrospectionVisitor;
import org.jadira.scanner.resolver.ClasspathResolver;

public class InspMethod extends InspOperation {

    protected InspMethod(MethodInfo methodInfo, InspType enclosingType, ClasspathResolver resolver) {
        super(methodInfo, enclosingType, resolver);
    }

    public static InspMethod getInspMethod(MethodInfo methodInfo, InspType enclosingType, ClasspathResolver resolver) {
        return new InspMethod(methodInfo, enclosingType, resolver);
    }

    @Override
    public Method getActualMethod() throws ClasspathAccessException {

        List<InspParameter> params = getParameters();
        Class<?>[] paramClasses = new Class<?>[params.size()];
        for (int i = 0; i < params.size(); i++) {
            paramClasses[i] = params.get(i).getType().getActualClass();
        }

        try {
            return getEnclosingType().getActualClass().getDeclaredMethod(getName(), paramClasses);
        } catch (SecurityException e) {
            throw new ClasspathAccessException("Could not access class: " + e.getMessage(), e);
        } catch (NoSuchMethodException e) {
            throw new ClasspathAccessException("Could not find method: " + e.getMessage(), e);
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
}