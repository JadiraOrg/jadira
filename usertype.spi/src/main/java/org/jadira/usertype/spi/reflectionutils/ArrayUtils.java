/*
 *  Copyright 2010, 2011 Christopher Pheby
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
package org.jadira.usertype.spi.reflectionutils;

import java.lang.reflect.Array;

/**
 * Utility methods for manipulating arrays
 * @author Chris Pheby
 */
public class ArrayUtils {

    private ArrayUtils() {
    }

	/**
	 * This method does a similar job to JSE 6.0's Arrays.copyOf() method
	 * @param <U> The type of input array
	 * @param objectArray The array to copy
	 * @return A copy of the input, of component type <U>
	 */
    public static <U> U[] copyOf(U[] objectArray) {

    	Class<?> arrayClass = objectArray.getClass();
        @SuppressWarnings("unchecked") Class<U> componentClass = (Class<U>) arrayClass.getComponentType();

        @SuppressWarnings("unchecked") U[] copy = (U[]) Array.newInstance(componentClass, objectArray.length);
        System.arraycopy(objectArray, 0, copy, 0, objectArray.length);
        return copy;
    }

	/**
	 * This method does a similar job to JSE 6.0's Arrays.copyOf() method and overloads the method for use with arrays of ints
	 * @param <U> The type of input array
	 * @param intArray The array to copy
	 * @return A copy of the input, of component type <U>
	 */
    public static int[] copyOf(int[] intArray) {

        int[] copy = new int[intArray.length];
        System.arraycopy(intArray, 0, copy, 0, intArray.length);
        return copy;
    }

	/**
	 * This method does a similar job to JSE 6.0's Arrays.copyOf() method and overloads the method for use with arrays of short
	 * @param <U> The type of input array
	 * @param shortArray The array to copy
	 * @return A copy of the input, of component type <U>
	 */
    public static short[] copyOf(short[] shortArray) {

    	short[] copy = new short[shortArray.length];
        System.arraycopy(shortArray, 0, copy, 0, shortArray.length);
        return copy;
    }

	/**
	 * This method does a similar job to JSE 6.0's Arrays.copyOf() method and overloads the method for use with arrays of long
	 * @param <U> The type of input array
	 * @param longArray The array to copy
	 * @return A copy of the input, of component type <U>
	 */
    public static long[] copyOf(long[] longArray) {

    	long[] copy = new long[longArray.length];
        System.arraycopy(longArray, 0, copy, 0, longArray.length);
        return copy;
    }

	/**
	 * This method does a similar job to JSE 6.0's Arrays.copyOf() method and overloads the method for use with arrays of byte
	 * @param <U> The type of input array
	 * @param byteArray The array to copy
	 * @return A copy of the input, of component type <U>
	 */
    public static byte[] copyOf(byte[] byteArray) {

    	byte[] copy = new byte[byteArray.length];
        System.arraycopy(byteArray, 0, copy, 0, byteArray.length);
        return copy;
    }

	/**
	 * This method does a similar job to JSE 6.0's Arrays.copyOf() method and overloads the method for use with arrays of char
	 * @param <U> The type of input array
	 * @param charArray The array to copy
	 * @return A copy of the input, of component type <U>
	 */
    public static char[] copyOf(char[] charArray) {

    	char[] copy = new char[charArray.length];
        System.arraycopy(charArray, 0, copy, 0, charArray.length);
        return copy;
    }

	/**
	 * This method does a similar job to JSE 6.0's Arrays.copyOf() method and overloads the method for use with arrays of float
	 * @param <U> The type of input array
	 * @param floatArray The array to copy
	 * @return A copy of the input, of component type <U>
	 */
    public static float[] copyOf(float[] floatArray) {

    	float[] copy = new float[floatArray.length];
        System.arraycopy(floatArray, 0, copy, 0, floatArray.length);
        return copy;
    }

	/**
	 * This method does a similar job to JSE 6.0's Arrays.copyOf() method and overloads the method for use with arrays of double
	 * @param <U> The type of input array
	 * @param doubleArray The array to copy
	 * @return A copy of the input, of component type <U>
	 */
    public static double[] copyOf(double[] doubleArray) {

    	double[] copy = new double[doubleArray.length];
        System.arraycopy(doubleArray, 0, copy, 0, doubleArray.length);
        return copy;
    }

	/**
	 * This method does a similar job to JSE 6.0's Arrays.copyOf() method and overloads the method for use with arrays of boolean
	 * @param <U> The type of input array
	 * @param booleanArray The array to copy
	 * @return A copy of the input, of component type <U>
	 */
    public static boolean[] copyOf(boolean[] booleanArray) {

    	boolean[] copy = new boolean[booleanArray.length];
        System.arraycopy(booleanArray, 0, copy, 0, booleanArray.length);
        return copy;
    }
}
