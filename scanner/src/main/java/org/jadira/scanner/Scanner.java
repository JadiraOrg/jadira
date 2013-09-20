/*
 *  Copyright 2013 Chris Pheby
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
package org.jadira.scanner;

import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.classpath.filter.JElementTypeFilter;
import org.jadira.scanner.classpath.filter.JTypeAnnotatedWithFilter;
import org.jadira.scanner.classpath.filter.JTypeSubTypeOfFilter;
import org.jadira.scanner.classpath.projector.ClasspathProjector;
import org.jadira.scanner.classpath.types.JElement;
import org.jadira.scanner.classpath.types.JType;
import org.jadira.scanner.core.api.Filter;
import org.jadira.scanner.core.api.Locator;

public class Scanner {

    protected final transient Configuration configuration;
    private ClasspathResolver classpathResolver;

    public Scanner(final Configuration configuration) {
        this.configuration = configuration;
        
        List<URL> urls = new ArrayList<URL>();
        if (configuration.getUrls() != null) {
            urls.addAll(configuration.getUrls());
        }
        if (configuration.getLocators() != null) {
            for (Locator<URL> next : configuration.getLocators()) {
                urls.addAll(next.locate());
            }
        }
        if (configuration.getClassLoaders() == null) {
            classpathResolver = new ClasspathResolver(urls);
        } else {
            classpathResolver = new ClasspathResolver(urls, configuration.getClassLoaders());
        }
    }

    public Scanner(final String prefix, final Filter<?>...s) {
        this((Object) prefix,s);
    }

    public Scanner(final Object... params) {
        this(ConfigurationBuilder.build(params));
    }

    public Scanner() {
        this(new ConfigurationBuilder());         
    }

    public <T> Class<? extends T>[] findSubTypesOf(final Class<T> type) {
        
        @SuppressWarnings("unchecked")
        Class<? extends T>[] result = (Class<? extends T>[]) jtypeToClass(classpathResolver.resolveAll(null, ClasspathProjector.SINGLETON, assembleFilters(new JElementTypeFilter(JType.class), new JTypeSubTypeOfFilter(type))));
        return result;
    }

    public Class<?>[] findTypesAnnotatedWith(final Class<? extends Annotation> annotation) {
        
        return jtypeToClass(classpathResolver.resolveAll(null, ClasspathProjector.SINGLETON, assembleFilters(new JElementTypeFilter(JType.class), new JTypeAnnotatedWithFilter(annotation))));
    }
    
    public Configuration getConfiguration() {
        return configuration;
    }
    
    private static Class<?>[] jtypeToClass(Collection<? extends JElement> jclasses) {
        
        List<JType> elements = new ArrayList<JType>(jclasses.size());
        for (JElement next : jclasses) {
            if (!(next instanceof JType)) {
                throw new IllegalStateException("Only JType can be converted to classes");
            } else {
                elements.add((JType)next);
            }
        }
        return jtypeToClass(elements.toArray(new JType[]{}));
    }
    
    private static Class<?>[] jtypeToClass(JType... jclasses) {
        
        Class<?>[] classes = new Class[jclasses.length];
        for (int i=0; i<jclasses.length; i++) {
            Class<?> actualClass = (Class<?>) jclasses[i].getActualClass();
            classes[i] = actualClass;
        }
        return classes;
    }
    
    private Filter<?>[] assembleFilters(Filter<?>... searchFilters) {

        Filter<?>[] filters = new Filter<?>[configuration.getFilters().size() + searchFilters.length];
        for (int i = 0; i < configuration.getFilters().size(); i++) {
            filters[i] = configuration.getFilters().get(0);
        }
        
        for (int i = 0; i < searchFilters.length; i++) {
            filters[configuration.getFilters().size() + i] = searchFilters[i];
        }
        return filters;
    }
}