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

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation can be applied to a method or constructor to indicate that it can be used to clone a particular type.
 * A Constructor with this annotation must take a single argument that must be the same type or a supertype of the type
 * to be cloned.
 * <br />
 * A Method with this annotation may be either a static method that returns an instance of the exact same type and takes 
 * an instance of the same type or one of its supertypes as a parameter, or it may be an instance method that returns an
 * instance of the same type and has no parameters. 
 */
@Retention(RUNTIME)
@Target({ CONSTRUCTOR, METHOD })
@Documented
public @interface Cloner {

}
