/*
 *  Copyright 2010, 2011 Chris Pheby and Sousan Rassoul
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
package org.jadira.bindings.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation, when applied to a method indicates that it is suitable for
 * converting an object into a corresponding object representation as indicated by the signature.
 * <p>
 * For any given {@link ConverterScope}, this annotation can be defined once for
 * any related input / output class pair.
 * <p>
 * The annotation may be applied to either a static method or an instance
 * method. In either case, the annotated method return an object representation. In the case of
 * a static method, a single parameter of the bound type must be declared. In
 * the case of an instance method, this must declared for the bound target
 * class.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface To {
}
