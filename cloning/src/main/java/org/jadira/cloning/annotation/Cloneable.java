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
package org.jadira.cloning.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation can be applied to classes to indicate that they may be cloned. In practice
 * the use of this annotation is not mandatory as all classes can effectively be cloned by the cloning library. <br />
 * There are benefits to using this annotation however, as it allows the cloning behaviour for the annotated class to be
 * customised.
 */
@Retention(RUNTIME)
@Target({ TYPE })
@Documented
public @interface Cloneable {

    Class<?> implementor() default void.class;
}
