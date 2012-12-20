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

import javassist.bytecode.MethodInfo;

import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.introspection.visitor.IntrospectionVisitor;
import org.jadira.scanner.resolver.ClasspathResolver;

public class InspStaticInitializer extends InspOperation {

    protected InspStaticInitializer(MethodInfo methodInfo, InspType enclosingType, ClasspathResolver resolver) {
        super(methodInfo, enclosingType, resolver);
    }

    public static InspStaticInitializer getInspStaticInitializer(MethodInfo methodInfo, InspType enclosingType, ClasspathResolver resolver) {
        return new InspStaticInitializer(methodInfo, enclosingType, resolver);
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