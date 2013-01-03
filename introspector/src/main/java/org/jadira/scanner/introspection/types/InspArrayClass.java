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

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javassist.bytecode.ClassFile;

import org.jadira.scanner.exception.ClasspathAccessException;
import org.jadira.scanner.resolver.ClasspathResolver;

public class InspArrayClass extends InspClass {

    private Class<?> actualClass;

	protected InspArrayClass(Class<?> clazz, ClasspathResolver resolver) throws ClasspathAccessException {
        super(InspClass.getInspClass(Array.class, resolver).getClassFile(), resolver);
        this.actualClass = clazz;
    }

    public static InspArrayClass getInspClass(Class<?> clazz, ClasspathResolver resolver) throws ClasspathAccessException {
        return new InspArrayClass(clazz, resolver);
    }
	
    public InspClass getSuperClass() throws ClasspathAccessException {

        return null;
    }

    public List<InspInterface> getImplementedInterfaces() throws ClasspathAccessException {

        return Collections.emptyList();
    }

    @Override
    public Class<?> getActualClass() throws ClasspathAccessException {

        return this.actualClass;
    }

    // public Set<InspClass> getSubClasses()

    public List<InspInnerClass> getEnclosedClasses() throws ClasspathAccessException {

        return Collections.emptyList();
    }

    public List<InspField> getFields() {

        return Collections.emptyList();
    }

    public List<InspConstructor> getConstructors() throws ClasspathAccessException {

        return Collections.emptyList();
    }

    @Override
    public Set<InspAnnotation<?>> getAnnotations() {

        return Collections.emptySet();
    }

    @Override
    public InspPackage getPackage() throws ClasspathAccessException {

        return null;
    }

    public List<InspMethod> getMethods() {

        return Collections.emptyList();
    }

    public List<InspStaticInitializer> getStaticInitializers() {

        return Collections.emptyList();
    }
	
	@Override
	public boolean isArray() {
		return true;
	}
	
    public String getName() {
        return actualClass.getName();
    }
    
    public ClassFile getClassFile() {
    	return null;
    }

}