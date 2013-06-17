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
package org.jadira.cloning.unsafe;

/**
 * This class safely detects whether optional feature libraries - sun.misc.Unsafe,
 * MutabilityDetector and Objenesis are available.
 */
public class FeatureDetection {

	private static final boolean HAS_UNSAFE;

	private static final boolean HAS_MUTABILITY_DETECTOR;

	private static final boolean HAS_OBJENESIS;

	static {
		boolean hasUnsafe = true;
		try {
			Class.forName("sun.misc.Unsafe");
		} catch (ClassNotFoundException e) {
			hasUnsafe = false;
		}
		HAS_UNSAFE = hasUnsafe;
	}

	static {
		boolean hasMutabilityDetector = true;
		try {
			Class.forName("org.mutabilitydetector.ThreadUnsafeAnalysisSession");
		} catch (ClassNotFoundException e) {
			hasMutabilityDetector = false;
		}
		HAS_MUTABILITY_DETECTOR = hasMutabilityDetector;
	}

	static {
		boolean hasObjenesis = true;
		try {
			Class.forName("org.objenesis.Objenesis");
		} catch (ClassNotFoundException e) {
			hasObjenesis = false;
		}
		HAS_OBJENESIS = hasObjenesis;
	}

	public static final boolean hasUnsafe() {
		return HAS_UNSAFE;
	}

	public static final boolean hasMutabilityDetector() {
		return HAS_MUTABILITY_DETECTOR;
	}

	public static final boolean hasObjenesis() {
		return HAS_OBJENESIS;
	}
}
