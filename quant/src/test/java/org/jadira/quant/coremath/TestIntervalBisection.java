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

import static org.junit.Assert.assertEquals;

import org.jadira.quant.api.QuantitativeFunction;
import org.jadira.quant.exception.IntervalBisectionOutOfRangeException;
import org.junit.Test;

public class TestIntervalBisection {

	@Test
	public void testIntervalBisection() {
		
		IntervalBisection bisection = new IntervalBisection(0D, 2D);
		Double result = bisection.apply(new QuantitativeFunction<Double, Double>() {

			@Override
			public Double apply(Double value) {
				return 2 - Math.pow(Math.E, value);
			}
		});
		assertEquals(0.693359375D, result, 0.0000000001D); // The approximate value for which 2 - e^value gives zero
	}
	
	@Test(expected=IntervalBisectionOutOfRangeException.class)
	public void testOutOfRange() {
		
		IntervalBisection bisection = new IntervalBisection(-3D, -1D);
		Double result = bisection.apply(new QuantitativeFunction<Double, Double>() {

			@Override
			public Double apply(Double value) {
				return 2 - Math.pow(Math.E, value);
			}
		});
		assertEquals(0.693359375D, result, 0.0000000001D);
	}
}
