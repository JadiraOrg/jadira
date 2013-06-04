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
package org.jadira.quant.exception;

/**
 * Indicates a problem arising during evaluation
 */
public class IntervalBisectionOutOfRangeException extends RuntimeException {

	private static final long serialVersionUID = 8651549079422635405L;

	private double lowerRange;
	private double upperRange;

	/**
     * Constructs an {@code IntervalBisectionOutOfRangeException} for the given input values.
     * @param message The detail message (which is saved for later retrieval by the
     *            {@link #getMessage()} method)
     */
    public IntervalBisectionOutOfRangeException(double lowerRange, double upperRange) {
        super("The BisectionInterval cannot be found for the inputs {" + lowerRange + ", " + upperRange + "}");
        this.lowerRange = lowerRange;
        this.upperRange = upperRange;
    }

	public double getLowerRange() {
		return lowerRange;
	}

	public double getUpperRange() {
		return upperRange;
	}
}