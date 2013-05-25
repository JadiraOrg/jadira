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
package org.jadira.quant.api;

/**
 * This interface defines a function. It is intended to be adaptable to /compatible with 
 * {@code java.util.function.Function} being introduced into Java 8.
 * @param <T> The input type of the function.
 * @param <R> The result type of the function.
 */
//@FunctionalInterface for Java 8
public interface QuantitativeFunction<R extends Number, T> {

	/**
	 * Apply a function to the input argument T, yielding an appropriate result R.
	 * @param value the input value
	 * @return the result of the function
	 */
	R apply(T value);
}