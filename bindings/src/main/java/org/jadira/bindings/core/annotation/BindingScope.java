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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that is optionally applied when defining a ToString or FromString
 * scope. A ToString scope is used to qualify a ToString method so that it is
 * only invoked for the given scope.
 * 
 * Anyone can define a new scope. A scope annotation is annotated with
 * 
 * @BindingScope, @Retention(RUNTIME), and typically @Documented. For example:
 * 
 * <pre>
 * &#064;java.lang.annotation.Documented
 * &#064;java.lang.annotation.Retention(RUNTIME)
 * &#064;BindingScope
 * public @interface JdbcDateTime {
 * }
 * @author Sousan Rassoul
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface BindingScope {
}
