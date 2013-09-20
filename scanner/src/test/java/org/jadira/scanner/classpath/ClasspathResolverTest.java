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
package org.jadira.scanner.classpath;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import org.jadira.scanner.classpath.visitor.IntrospectionVisitor;
import org.jadira.scanner.core.exception.ClasspathAccessException;
import org.jadira.scanner.file.locator.JavaClasspathUrlLocator;
import org.junit.Test;

public class ClasspathResolverTest {

    @Test
    public void simpleExecution() throws ClasspathAccessException, FileNotFoundException {

        ClasspathResolver helper = new ClasspathResolver(new JavaClasspathUrlLocator().locate());

        JPackage pkg = JPackage.getJPackage("org.jadira.scanner.classpath", helper);

        Set<JClass> classes = pkg.getClasses();
        List<JClass> sortedClasses = new ArrayList<JClass>();
        sortedClasses.addAll(classes);
        Collections.sort(sortedClasses, new Comparator<JClass>() {

			@Override
			public int compare(JClass o1, JClass o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

        assertEquals(3, classes.size());

        Iterator<JClass> it = sortedClasses.iterator();
        JClass firstClass = it.next();

        List<JConstructor> constructors = firstClass.getConstructors();
        assertEquals(1, constructors.size());
        assertEquals("JConstructor[name=<init>,enclosingType=JClass[name=org.jadira.scanner.classpath.A],parameters=[JParameter[name=first,type=JClass[name=java.lang.String],enclosingType=JClass[name=org.jadira.scanner.classpath.A]], JParameter[name=second,type=JClass[name=java.lang.Integer],enclosingType=JClass[name=org.jadira.scanner.classpath.A]]]]", constructors.get(0).toString());

        assertEquals(2, firstClass.getEnclosedClasses().size());
        assertEquals("JInnerClass[name=org.jadira.scanner.classpath.A$1,enclosingClass=JClass[name=org.jadira.scanner.classpath.A]]", firstClass.getEnclosedClasses().get(0).toString());
        assertEquals("JInnerClass[name=org.jadira.scanner.classpath.A$B,enclosingClass=JClass[name=org.jadira.scanner.classpath.A]]", firstClass.getEnclosedClasses().get(1).toString());

        assertEquals("JClass[name=org.jadira.scanner.classpath.A]", firstClass.toString());

        it.next(); // JClass secondClass = it.next();
        JClass thirdClass = it.next();

        assertEquals("JClass[name=org.jadira.scanner.classpath.ClasspathResolverTest]", thirdClass.toString());

        List<JConstructor> constructors3 = thirdClass.getConstructors();
        assertEquals(1, constructors3.size());
        assertEquals("JConstructor[name=<init>,enclosingType=JClass[name=org.jadira.scanner.classpath.ClasspathResolverTest],parameters=[]]", constructors3.get(0).toString());

        assertEquals(4, thirdClass.getEnclosedClasses().size());
    }

    @Test
    public void simpleExecution2() throws ClasspathAccessException, FileNotFoundException {

        ClasspathResolver helper = new ClasspathResolver(new JavaClasspathUrlLocator().locate());

        JPackage pkg = JPackage.getJPackage("org.jadira.scanner.classpath", helper);

        Set<JClass> classes = pkg.getClasses();
        List<JClass> sortedClasses = new ArrayList<JClass>();
        sortedClasses.addAll(classes);
        Collections.sort(sortedClasses, new Comparator<JClass>() {

            @Override
            public int compare(JClass o1, JClass o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        assertEquals(3, classes.size());

        Iterator<JClass> it = sortedClasses.iterator();
        JClass firstClass = it.next();

        List<JConstructor> constructors = firstClass.getConstructors();
        assertEquals(1, constructors.size());
        assertEquals("JConstructor[name=<init>,enclosingType=JClass[name=org.jadira.scanner.classpath.A],parameters=[JParameter[name=first,type=JClass[name=java.lang.String],enclosingType=JClass[name=org.jadira.scanner.classpath.A]], JParameter[name=second,type=JClass[name=java.lang.Integer],enclosingType=JClass[name=org.jadira.scanner.classpath.A]]]]", constructors.get(0).toString());

        assertEquals(2, firstClass.getEnclosedClasses().size());
        assertEquals("JInnerClass[name=org.jadira.scanner.classpath.A$1,enclosingClass=JClass[name=org.jadira.scanner.classpath.A]]", firstClass.getEnclosedClasses().get(0).toString());
        assertEquals("JInnerClass[name=org.jadira.scanner.classpath.A$B,enclosingClass=JClass[name=org.jadira.scanner.classpath.A]]", firstClass.getEnclosedClasses().get(1).toString());

        assertEquals("JClass[name=org.jadira.scanner.classpath.A]", firstClass.toString());

        it.next(); // JClass secondClass = it.next();
        JClass thirdClass = it.next();

        assertEquals("JClass[name=org.jadira.scanner.classpath.ClasspathResolverTest]", thirdClass.toString());

        List<JConstructor> constructors3 = thirdClass.getConstructors();
        assertEquals(1, constructors3.size());
        assertEquals("JConstructor[name=<init>,enclosingType=JClass[name=org.jadira.scanner.classpath.ClasspathResolverTest],parameters=[]]", constructors3.get(0).toString());

        assertEquals(4, thirdClass.getEnclosedClasses().size());
    }
    
    @Test
    public void simpleVisit() throws ClasspathAccessException, FileNotFoundException {

        ClasspathResolver helper = new ClasspathResolver();

        // @SuppressWarnings("unchecked")
		// FIXME List<JClass> classes = (List<JClass>)helper.resolveAll(new JavaClasspathUrlLocator(), new ClasspathProjector(), new JClassFilter());

        // JClass pkg = JClass.getJClass("java.lang.CharacterData00", helper);

        JPackage pkg = JPackage.getJPackage("java.lang", helper);
        Set<JClass> classes = pkg.getClasses();

        for (JClass next : classes) {
	        next.acceptVisitor(new IntrospectionVisitor() {

				@Override
				public void visit(JParameter element) {
					System.out.println(element.toString());
				    // element.toString();
				}

				@Override
				public void visit(JField element) {
					System.out.println(element.toString());
				    // element.toString();
				}

				@Override
				public void visit(JInterface element) {
					System.out.println(element.toString());
				    // element.toString();
				}

				@Override
				public void visit(JClass element) {
					System.out.println(element.toString());
				    // element.toString();
				}

				@Override
				public void visit(JInnerClass element) {
					System.out.println(element.toString());
				    // element.toString();
				}

				@Override
				public void visit(JPackage element) {
					System.out.println(element.toString());
				    // element.toString();
				}

				@Override
				public void visit(JStaticInitializer element) {
					System.out.println(element.toString());
				    // element.toString();
				}

				@Override
				public void visit(JMethod element) {
					System.out.println(element.toString());
				    // element.toString();
				}

				@Override
				public void visit(JConstructor element) {
					System.out.println(element.toString());
				    // element.toString();
				}

				@Override
				public void visit(JAnnotation<?> element) {
					System.out.println(element.toString());
				    // element.toString();
				}
			});
        }
    }

    @Test
    public void simpleVisit2() throws ClasspathAccessException, FileNotFoundException {

        ClasspathResolver helper = new ClasspathResolver();

        // @SuppressWarnings("unchecked")
        // FIXME List<JClass> classes = (List<JClass>)helper.resolveAll(new JavaClasspathUrlLocator(), new ClasspathProjector(), new JClassFilter());

        // JClass pkg = JClass.getJClass("java.lang.CharacterData00", helper);

        JPackage pkg = JPackage.getJPackage("java.util", helper);
        Set<JClass> classes = pkg.getClasses();

        for (JClass next : classes) {
            next.acceptVisitor(new IntrospectionVisitor() {

                @Override
                public void visit(JParameter element) {
                    System.out.println(element.toString());
                    // element.toString();
                }

                @Override
                public void visit(JField element) {
                    System.out.println(element.toString());
                    // element.toString();
                }

                @Override
                public void visit(JInterface element) {
                    System.out.println(element.toString());
                    // element.toString();
                }

                @Override
                public void visit(JClass element) {
                    System.out.println(element.toString());
                    // element.toString();
                }

                @Override
                public void visit(JInnerClass element) {
                    System.out.println(element.toString());
                    // element.toString();
                }

                @Override
                public void visit(JPackage element) {
                    System.out.println(element.toString());
                    // element.toString();
                }

                @Override
                public void visit(JStaticInitializer element) {
                    System.out.println(element.toString());
                    // element.toString();
                }

                @Override
                public void visit(JMethod element) {
                    System.out.println(element.toString());
                    // element.toString();
                }

                @Override
                public void visit(JConstructor element) {
                    System.out.println(element.toString());
                    // element.toString();
                }

                @Override
                public void visit(JAnnotation<?> element) {
                    System.out.println(element.toString());
                    // element.toString();
                }
            });
        }
    }

}
