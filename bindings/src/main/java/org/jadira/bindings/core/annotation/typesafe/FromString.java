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
package org.jadira.bindings.core.annotation.typesafe;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation, when applied to a method indicates that it is suitable for
 * converting a String to a given representation as indicated by the method signature.
 * <p>
 * For any given {@link ConverterScope}, this annotation can be defined once for
 * a given output class.
 * <p>
 * The annotation may be applied to either a static method or a constructor. In
 * either case, the annotated method must have a single parameter. In the
 * case of a method, the return type must be of the bound target class. In the
 * case of a constructor, this must be for the bound target class.
 */
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR })
@Retention(RetentionPolicy.RUNTIME)
public @interface FromString {
}
