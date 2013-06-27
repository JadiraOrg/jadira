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
 * This class is used to make a best effort determination as to whether this is a 64-bit or 32-bit
 * JVM
 */
public class JreAddressSize {

	private static final String SUN_ARCH_DATA_MODEL = System.getProperty("sun.arch.data.model");
	private static final String OS_ARCH = System.getProperty("os.arch");

	private static final boolean IS_64_BIT;

	static {
		if (UnsafeOperations.isUnsafeAvailable() && (UnsafeOperations.getUnsafeOperations().getAddressSize() >= 8)) {
			IS_64_BIT = true;
		}

		else if (SUN_ARCH_DATA_MODEL != null && SUN_ARCH_DATA_MODEL.contains("64")) {
			IS_64_BIT = true;
		}

		else if (OS_ARCH != null && OS_ARCH.contains("64")) {
			IS_64_BIT = true;
		} else {
			IS_64_BIT = false;
		}
	}

	private JreAddressSize() {
	}

	public static final boolean is64Bit() {
		return IS_64_BIT;
	}
}
