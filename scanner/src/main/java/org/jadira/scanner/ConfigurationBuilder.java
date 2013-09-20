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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jadira.scanner.classpath.filter.NameFilter;
import org.jadira.scanner.core.api.Filter;
import org.jadira.scanner.core.api.Locator;
import org.jadira.scanner.core.exception.ClasspathAccessException;
import org.jadira.scanner.core.utils.reflection.ClassLoaderUtils;

public class ConfigurationBuilder implements Configuration {
    
    private List<URL> urls = new ArrayList<URL>();
    
    private List<Locator<URL>> locators = new ArrayList<Locator<URL>>();

    private List<ClassLoader> classLoaders = new ArrayList<ClassLoader>();
    
    private List<Filter<?>> filters = new ArrayList<Filter<?>>();

    public ConfigurationBuilder() {
        ClassLoader[] cls = ClassLoaderUtils.getClassLoaders();
        for (ClassLoader next : cls) {
            classLoaders.add(next);
        }
    }

    public static ConfigurationBuilder build(final Object... params) {
        
        ConfigurationBuilder builder = new ConfigurationBuilder();
        List<Object> paramsList = flattenParams(params);

        List<ClassLoader> cLoaders = new ArrayList<ClassLoader>();
        
        for (Object param : paramsList) {
            if (param instanceof ClassLoader) {
                cLoaders.add((ClassLoader) param);
            } else if (param instanceof URL) {
                builder.addUrls((URL) param);
            } else if (param instanceof Locator) {
                @SuppressWarnings("unchecked") final Locator<URL>[] myParams = new Locator[] {(Locator<URL>)param};
                builder.addLocators(myParams);
            } else if (param instanceof Filter) {
                builder.addFilters((Filter<?>) param);
            } else if (param instanceof String) {
                builder.addFilters(new NameFilter((String)param));
            } else {
                throw new ClasspathAccessException("Could not handle builder parameter " + param.toString());
            }
            
        }

        builder.setClassLoaders(cLoaders);        
        return builder;
    }

    private static List<Object> flattenParams(final Object... params) {
        List<Object> paramsList = new ArrayList<Object>(params.length);
        if (params != null) {
            for (Object param : params) {
                if (param != null) {
                    if (param.getClass().isArray()) {
                        for (Object nextEntry : (Object[]) param) {
                            if (nextEntry != null) {
                                paramsList.add(nextEntry); 
                            }
                        }
                    }
                    else if (param instanceof Iterable) {
                        for (Object nextEntry : (Iterable<?>) param) {
                            if (nextEntry != null) {
                                paramsList.add(nextEntry); }
                            }
                        }
                    else {
                        paramsList.add(param);
                    }
                }
            }
        }
        return paramsList;
    }

    public Scanner build() {
        return new Scanner(this);
    }
    
    @Override
    public List<URL> getUrls() {
        return urls;
    }

    ConfigurationBuilder setUrls(final Collection<URL> urls) {
        this.urls = new ArrayList<URL>(urls);
        return this;
    }

    ConfigurationBuilder setUrls(final URL... urls) {
        this.urls = new ArrayList<URL>(urls.length);
        for(URL next : urls) {
            this.urls.add(next);
        }
        return this;
    }
    
    ConfigurationBuilder addUrls(final Collection<URL> urls) {
        this.urls.addAll(urls);
        return this;
    }
    
    ConfigurationBuilder addUrls(final URL... urls) {
        for(URL next : urls) {
            this.urls.add(next);
        }
        return this;
    }
    
    @Override
    public List<Locator<URL>> getLocators() {
        return locators;
    }

    ConfigurationBuilder setLocators(final Collection<Locator<URL>> locators) {
        this.locators = new ArrayList<Locator<URL>>(locators);
        return this;
    }

    ConfigurationBuilder setLocators(final Locator<URL>... locators) {
        this.locators = new ArrayList<Locator<URL>>(locators.length);
        for (Locator<URL> next : locators) {
            this.locators.add(next);
        }
        return this;
    }
        
    ConfigurationBuilder addLocators(final Collection<Locator<URL>> locators) {
        this.locators.addAll(locators);
        return this;
    }
    
    ConfigurationBuilder addLocators(final Locator<URL>... locators) {
        for (Locator<URL> next : locators) {
            this.locators.add(next);
        }
        return this;
    }
    
    @Override
    public List<ClassLoader> getClassLoaders() {
        return classLoaders;
    }

    ConfigurationBuilder setClassLoaders(final Collection<ClassLoader> classLoaders) {
        this.classLoaders = new ArrayList<ClassLoader>(classLoaders);
        return this;
    }

    ConfigurationBuilder setClassLoaders(final ClassLoader... classLoaders) {
        this.classLoaders = new ArrayList<ClassLoader>(classLoaders.length);
        for (ClassLoader next : classLoaders) {
            this.classLoaders.add(next);
        }
        return this;
    }
    
    ConfigurationBuilder addClassLoaders(final Collection<ClassLoader> classLoaders) {
        this.classLoaders.addAll(classLoaders);
        return this;
    }

    ConfigurationBuilder addClassLoaders(final ClassLoader... classLoaders) {
        for (ClassLoader next : classLoaders) {
            this.classLoaders.add(next);
        }
        return this;
    }
    
    @Override
    public List<Filter<?>> getFilters() {
        return filters;
    }

    ConfigurationBuilder setFilters(final List<Filter<?>> filters) {
        this.filters = new ArrayList<Filter<?>>(filters);
        return this;
    }

    ConfigurationBuilder setFilters(final Filter<?>... filters) {
        this.filters = new ArrayList<Filter<?>>(filters.length);
        for (Filter<?> next : filters) {
            this.filters.add(next);
        }
        return this;
    }
    
    ConfigurationBuilder addFilters(final List<Filter<?>> filters) {
        this.filters.addAll(filters);
        return this;
    }
    
    ConfigurationBuilder addFilters(final Filter<?>... filters) {
        for (Filter<?> next : filters) {
            this.filters.add(next);
        }
        return this;
    }
}
