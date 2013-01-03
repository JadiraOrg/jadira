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
package org.jadira.scanner.introspection;

import java.io.FileNotFoundException;

import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.introspection.types.InspAnnotation;
import org.jadira.scanner.introspection.types.InspClass;
import org.jadira.scanner.introspection.types.InspConstructor;
import org.jadira.scanner.introspection.types.InspField;
import org.jadira.scanner.introspection.types.InspInnerClass;
import org.jadira.scanner.introspection.types.InspInterface;
import org.jadira.scanner.introspection.types.InspMethod;
import org.jadira.scanner.introspection.types.InspPackage;
import org.jadira.scanner.introspection.types.InspParameter;
import org.jadira.scanner.introspection.types.InspStaticInitializer;
import org.jadira.scanner.introspection.visitor.IntrospectionVisitor;
import org.jadira.scanner.resolver.ClasspathResolver;
import org.jadira.scanner.urllocator.JavaClasspathUrlLocator;
import org.junit.Test;

public class ClasspathResolverTest {

    @Test
    public void simpleExecution() throws ClasspathAccessException, FileNotFoundException {
    	
        ClasspathResolver helper = ClasspathResolver.getResolver(new JavaClasspathUrlLocator());
        
        InspPackage pkg = InspPackage.getInspPackage("java.lang", helper);
        
        pkg.acceptVisitor(new IntrospectionVisitor() {
			
			@Override
			public void visit(InspParameter element) {
				System.out.println(element.toString());
			}
			
			@Override
			public void visit(InspField element) {
				System.out.println(element.toString());
			}
			
			@Override
			public void visit(InspInterface element) {
				System.out.println(element.toString());
			}
			
			@Override
			public void visit(InspClass element) {
				System.out.println(element.toString());
			}
			
			@Override
			public void visit(InspInnerClass element) {
				System.out.println(element.toString());
			}
			
			@Override
			public void visit(InspPackage element) {
				System.out.println(element.toString());
			}
			
			@Override
			public void visit(InspStaticInitializer element) {
				System.out.println(element.toString());
			}
			
			@Override
			public void visit(InspMethod element) {
				System.out.println(element.toString());
			}
			
			@Override
			public void visit(InspConstructor element) {
				System.out.println(element.toString());
			}
			
			@Override
			public void visit(InspAnnotation<?> element) {
				System.out.println(element.toString());
			}

		});
        
//        InspPackage pkg = InspPackage.getInspPackage("org.jadira.scanner.introspection", helper);
//        
//        Set<InspClass> classes = pkg.getClasses();
//        List<InspClass> sortedClasses = new ArrayList<InspClass>();
//        sortedClasses.addAll(classes);
//        Collections.sort(sortedClasses, new Comparator<InspClass>() {
//
//			@Override
//			public int compare(InspClass o1, InspClass o2) {
//				return o1.getName().compareTo(o2.getName());
//			}
//		});
//        
//        assertEquals(2, classes.size());
//
//        Iterator<InspClass> it = sortedClasses.iterator();
//        InspClass firstClass = it.next();
//        
//        List<InspConstructor> constructors = firstClass.getConstructors();
//        assertEquals(1, constructors.size());
//        assertEquals("InspConstructor[name=<init>,enclosingType=InspClass[name=org.jadira.scanner.introspection.A],parameters=[InspParameter[name=0,type=InspClass[name=java.lang.String],enclosingType=InspClass[name=org.jadira.scanner.introspection.A]], InspParameter[name=1,type=InspClass[name=java.lang.Integer],enclosingType=InspClass[name=org.jadira.scanner.introspection.A]]]]", constructors.get(0).toString());
//        constructors.get(0).toString();
//
//        assertEquals(1, firstClass.getEnclosedClasses().size());
//        assertEquals("InspInnerClass[name=org.jadira.scanner.introspection.A$B,enclosingClass=InspClass[name=org.jadira.scanner.introspection.A]]", firstClass.getEnclosedClasses().get(0).toString());
//        firstClass.getEnclosedClasses().get(0).toString();
//
//        assertEquals("InspClass[name=org.jadira.scanner.introspection.A]", firstClass.toString());
//        firstClass.toString();
//        
//        InspClass secondClass = it.next();
//
//        secondClass.toString();
//        assertEquals("InspClass[name=org.jadira.scanner.introspection.ClasspathResolverTest]", secondClass.toString());
//        
//        List<InspConstructor> constructors2 = secondClass.getConstructors();
//        assertEquals(1, constructors2.size());
//        assertEquals("InspConstructor[name=<init>,enclosingType=InspClass[name=org.jadira.scanner.introspection.ClasspathResolverTest],parameters=[]]", constructors2.get(0).toString());
//        constructors2.get(0).toString();
//        
//        assertEquals(0, secondClass.getEnclosedClasses().size());
//        secondClass.getEnclosedClasses().size();        
    }
}
