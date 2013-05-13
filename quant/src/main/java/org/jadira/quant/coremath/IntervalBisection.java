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
package org.jadira.quant.coremath;

import org.jadira.quant.api.CompositeFunction;
import org.jadira.quant.api.QuantitativeFunction;
import org.jadira.quant.exception.IntervalBisectionOutOfRangeException;

/**
 * This function solves the root of the equation by starting with two higher and lower bound values
 * (X0 and X1) that bracket the root. The function assumes that the initial range of these end
 * points contains the root. By evaluating the function at these points f(X0), f(X1) and checking
 * that the result of the function has a change of sign we confirm that the root is within the range
 * between the two values. The assumption is that the function is continuous at root and has at
 * least one zero.
 *
 * Inspired by the example that appears in Java Methods for Financial Engineering (Philip Barker), this
 * implementation includes error handling, a functional programming model and two precision strategies.
 */
public class IntervalBisection implements CompositeFunction<Double, QuantitativeFunction<Double, Double>> {

	private final double precision;
	private final int iterations;
	private final double lowerBound;
	private final double higherBound;
	private PrecisionStrategy precisionStrategy;
	
	public enum PrecisionStrategy {
		TO_INTERVAL, BETWEEN_RESULTS; 
	}
	
	public IntervalBisection(double lowerBound, double higherBound) {
		iterations = 20;
		precision = 0.001D; // Defaults to 1e-3
		precisionStrategy = PrecisionStrategy.TO_INTERVAL;
		
		this.lowerBound = lowerBound;
		this.higherBound = higherBound;
	}

	/**
	 * Create a new instance with the given number of iterations and precision
	 * @param lowerBound The minimum bound for the range to converge from
	 * @param higherBound The maximum bound for the range to converge from
	 * @param iterations The number of iterations to perform
	 * @param precision The requested precision
	 */
	public IntervalBisection(double lowerBound, double higherBound, int iterations, double precision, PrecisionStrategy precisionStrategy) {
		this.lowerBound = lowerBound;
		this.higherBound = higherBound;
		this.iterations = iterations;
		this.precision = precision;
		this.precisionStrategy = precisionStrategy;
	}

	public int getIterations() {
		return iterations;
	}

	public double getPrecision() {
		return precision;
	}

	@Override
	public Double apply(QuantitativeFunction<Double, Double> computeFunction) {

		// Check to see if we have the root within the range bounds
		final double lowerResult = computeFunction.apply(lowerBound);
		final double higherResult = computeFunction.apply(higherBound);
		if (lowerResult * higherResult > 0) {
			throw new IntervalBisectionOutOfRangeException(lowerBound, higherBound);
		}
		
		double currentLower = lowerBound;
		double currentHigher = higherBound;
		
		double currentMiddle = Double.NaN;
		double midValueResult = Double.NaN;
		double preceedingMidValueResult = Double.NaN;

		for (int i = 0; i < iterations; i++) {

			preceedingMidValueResult = midValueResult;
			
			currentMiddle = currentLower + 0.5 * (currentHigher - currentLower);
			midValueResult = computeFunction.apply(currentMiddle);

			if (lowerResult * midValueResult < 0) {
				currentHigher = currentMiddle;
			} else if (lowerResult * midValueResult > 0) {
				currentLower = currentMiddle;
			}

			if (PrecisionStrategy.TO_INTERVAL == precisionStrategy) {
				if (Math.abs(midValueResult) <= precision) {
					break;	
				}
			} else if (PrecisionStrategy.BETWEEN_RESULTS == precisionStrategy || null == precisionStrategy) {
				if (Math.abs(midValueResult - preceedingMidValueResult) <= precision) {
					break;
				}
			}
		}

		return currentMiddle;
	}
}