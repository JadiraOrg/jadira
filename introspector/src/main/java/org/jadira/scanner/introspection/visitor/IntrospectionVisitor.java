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
package org.jadira.scanner.introspection.visitor;

import org.jadira.scanner.introspection.types.InspAnnotation;
import org.jadira.scanner.introspection.types.InspClass;
import org.jadira.scanner.introspection.types.InspConstructor;
import org.jadira.scanner.introspection.types.InspEnum;
import org.jadira.scanner.introspection.types.InspField;
import org.jadira.scanner.introspection.types.InspInnerClass;
import org.jadira.scanner.introspection.types.InspInterface;
import org.jadira.scanner.introspection.types.InspMethod;
import org.jadira.scanner.introspection.types.InspPackage;
import org.jadira.scanner.introspection.types.InspParameter;
import org.jadira.scanner.introspection.types.InspStaticInitializer;

public interface IntrospectionVisitor {

    void visit(InspConstructor element);
    void visit(InspMethod element);
    void visit(InspStaticInitializer element);
    void visit(InspPackage element);
    void visit(InspAnnotation<?> element);
    void visit(InspInnerClass element);
    void visit(InspClass element);
    void visit(InspEnum element);
    void visit(InspInterface element);
    void visit(InspField element);
    void visit(InspParameter element);
}
