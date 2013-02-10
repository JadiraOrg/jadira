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
package org.jadira.scanner.classpath.visitor;

import org.jadira.scanner.classpath.types.JAnnotation;
import org.jadira.scanner.classpath.types.JClass;
import org.jadira.scanner.classpath.types.JConstructor;
import org.jadira.scanner.classpath.types.JField;
import org.jadira.scanner.classpath.types.JInnerClass;
import org.jadira.scanner.classpath.types.JInterface;
import org.jadira.scanner.classpath.types.JMethod;
import org.jadira.scanner.classpath.types.JPackage;
import org.jadira.scanner.classpath.types.JParameter;
import org.jadira.scanner.classpath.types.JStaticInitializer;

public interface IntrospectionVisitor {

    void visit(JConstructor element);
    void visit(JMethod element);
    void visit(JStaticInitializer element);
    void visit(JPackage element);
    void visit(JAnnotation<?> element);
    void visit(JInnerClass element);
    void visit(JClass element);
    void visit(JInterface element);
    void visit(JField element);
    void visit(JParameter element);
}
