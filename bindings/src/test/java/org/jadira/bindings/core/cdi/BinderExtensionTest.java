/*
 *  Copyright 2010 Chris Pheby
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
package org.jadira.bindings.core.cdi;

import static org.junit.Assert.assertEquals;

import javax.inject.Inject;

import org.jadira.bindings.core.binder.Binder;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@Ignore // Due to https://issues.jboss.org/browse/ARQ-817
public class BinderExtensionTest {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class, "test.jar")
                .addPackage(HelloWorld.class.getPackage())
                .addPackage(BinderExtension.class.getPackage())
                .addAsManifestResource(
                        "META-INF/beans.xml",
                        ArchivePaths.create("beans.xml"));
    }
    
    @Inject
    public HelloWorld helloWorld;

    @Inject
    public Binder basicBinder;

    @Test
    public void testSayHello() {
//        System.out.println("Starting testSayHello");
//        System.out.println("Applied binder: " + basicBinder.convertTo(helloWorld));
        assertEquals("Hello World!", basicBinder.convertTo(HelloWorld.class, String.class, helloWorld));
    }
}