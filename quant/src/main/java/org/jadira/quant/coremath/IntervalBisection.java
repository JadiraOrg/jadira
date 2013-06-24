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
 * This class can be used to solve the equation f(x) = 0. It will determine the double value of x,
 * where f is a continuous function defined on an interval between [a, b] and f(a) and f(b) have opposite
 * signs. [a, b] are said to bracket the root since there must be at least one root between a and b that
 * yields f(x) = 0.
 * 
 * For each iteration, the approach divides the interval in two by determining the midpoint of the interval, c,
 * such that c = (a+b)/2. The method then determines the interval that brackets the root, this being either
 * [a, c] or [c, b]. This is used as the interval for the next iteration. The process is terminated either 
 * if the maximum number of iterations is reached, the gap between this result and the previous iteration 
 * result is sufficiently small (if configured with {@link PrecisionStrategy#BETWEEN_RESULTS}) or the value is
 * within the desired distance of root (if configured with {@link PrecisionStrategy#TO_INTERVAL}. 
 * 
 * PrecisionStrategy defaults to {@link PrecisionStrategy#TO_INTERVAL}, the method defaults to 20 iterations, 
 * and an accuracy distance of 1e-3 (0.001). 
 * 
 * Inspired by the example that appears in Java Methods for Financial Engineering (Philip Barker),
 * this implementation includes error handling, a functional programming model and two precision
 * strategies.
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
		this(lowerBound, higherBound, 20);
	}

	/**
	 * Create a new instance with the given number of iterations and precision
	 * @param lowerBound The minimum bound for the range to converge from
	 * @param higherBound The maximum bound for the range to converge from
	 * @param iterations The number of iterations to perform
	 */
	public IntervalBisection(double lowerBound, double higherBound, int iterations) {
		// Defaults precision strategy to 1e-3
		this(lowerBound, higherBound, iterations, 0.001D, PrecisionStrategy.TO_INTERVAL);
	}
	
	/**
	 * Create a new instance with the given number of iterations and precision
	 * @param lowerBound The minimum bound for the range to converge from
	 * @param higherBound The maximum bound for the range to converge from
	 * @param iterations The number of iterations to perform
	 * @param precision The requested precision
	 */
	public IntervalBisection(double lowerBound, double higherBound, int iterations, double precision, PrecisionStrategy precisionStrategy) {

		if (iterations < 1) {
			throw new IllegalArgumentException("iterations must be greater than 1");
		}
		if (Double.compare(precision, 0.0D)  < 0) {
			throw new IllegalArgumentException("precision must be a positive number");
		}
		if (precisionStrategy == null) {
			throw new IllegalArgumentException("precisionStrategy may not be null");
		}
		
		this.lowerBound = lowerBound;
		this.higherBound = higherBound;
		this.iterations = iterations;
		this.precision = precision;
		this.precisionStrategy = precisionStrategy;
	}

	public double getLowerBound() {
		return lowerBound;
	}
	
	public double getHigherBound() {
		return higherBound;
	}
	
	public int getIterations() {
		return iterations;
	}

	public double getPrecision() {
		return precision;
	}

	@Override
	public Double apply(QuantitativeFunction<Double, Double> computeFunction) {

		final double resultA = computeFunction.apply(lowerBound);
		final double resultB = computeFunction.apply(higherBound);
		
		if (resultA != 0.0D && resultB != 0.0D  
				&& (Double.compare(resultA, 0.0D) ^ Double.compare(0.0D, resultB)) != 0) {
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

			if (resultA * midValueResult < 0) {
				currentHigher = currentMiddle;
			} else if (resultA * midValueResult > 0) {
				currentLower = currentMiddle;
			}

			if (PrecisionStrategy.TO_INTERVAL == precisionStrategy) {
				if (Math.abs(midValueResult) <= precision) {
					break;	
				}
			} else if ((PrecisionStrategy.BETWEEN_RESULTS == precisionStrategy || null == precisionStrategy)
					&& (Math.abs(midValueResult - preceedingMidValueResult) <= precision)) {
					break;
			}
		}
		return currentMiddle;
	}
}