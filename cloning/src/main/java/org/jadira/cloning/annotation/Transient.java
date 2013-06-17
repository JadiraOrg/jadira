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

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.jadira.cloning.BasicCloner;

/**
 * Applied to a field, this annotation indicates that the field should be considered
 * to be transient when performing a clone. Transient fields when configured in {@link BasicCloner}
 * will be given the null or default values. If you simply want to retain the transient field without
 * copying, register it is {@link Immutable} or {@link NonCloneable} as appropriate.
 */
@Retention(RUNTIME) @Target({FIELD})
@Documented
public @interface Transient {
}
