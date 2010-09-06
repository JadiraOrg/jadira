/*
 *  Copyright 2010 Christopher Pheby
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
package org.jadira.usertype.dateandtime.shared.reflectionutils;

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
	 * @param original The array to copy
	 * @return A copy of the input, of component type <U>
	 */
    public static <U> U[] copyOf(U[] original) {
        
    	Class<?> arrayClass = original.getClass();
        @SuppressWarnings("unchecked") Class<U> componentClass = (Class<U>) arrayClass.getComponentType();
        
        @SuppressWarnings("unchecked") U[] copy = (U[]) Array.newInstance(componentClass, original.length);
        System.arraycopy(original, 0, copy, 0, original.length);
        return copy;
    }
    
	/**
	 * This method does a similar job to JSE 6.0's Arrays.copyOf() method and overloads the method for use with arrays of ints
	 * @param <U> The type of input array
	 * @param original The array to copy
	 * @return A copy of the input, of component type <U>
	 */
    public static int[] copyOf(int[] original) {
        
        int[] copy = new int[original.length];
        System.arraycopy(original, 0, copy, 0, original.length);
        return copy;
    }

	/**
	 * This method does a similar job to JSE 6.0's Arrays.copyOf() method and overloads the method for use with arrays of short
	 * @param <U> The type of input array
	 * @param original The array to copy
	 * @return A copy of the input, of component type <U>
	 */
    public static short[] copyOf(short[] original) {
        
    	short[] copy = new short[original.length];
        System.arraycopy(original, 0, copy, 0, original.length);
        return copy;
    }
    
	/**
	 * This method does a similar job to JSE 6.0's Arrays.copyOf() method and overloads the method for use with arrays of long
	 * @param <U> The type of input array
	 * @param original The array to copy
	 * @return A copy of the input, of component type <U>
	 */
    public static long[] copyOf(long[] original) {
        
    	long[] copy = new long[original.length];
        System.arraycopy(original, 0, copy, 0, original.length);
        return copy;
    }

	/**
	 * This method does a similar job to JSE 6.0's Arrays.copyOf() method and overloads the method for use with arrays of byte
	 * @param <U> The type of input array
	 * @param original The array to copy
	 * @return A copy of the input, of component type <U>
	 */
    public static byte[] copyOf(byte[] original) {
        
    	byte[] copy = new byte[original.length];
        System.arraycopy(original, 0, copy, 0, original.length);
        return copy;
    }
    
	/**
	 * This method does a similar job to JSE 6.0's Arrays.copyOf() method and overloads the method for use with arrays of char
	 * @param <U> The type of input array
	 * @param original The array to copy
	 * @return A copy of the input, of component type <U>
	 */
    public static char[] copyOf(char[] original) {
        
    	char[] copy = new char[original.length];
        System.arraycopy(original, 0, copy, 0, original.length);
        return copy;
    }
    
	/**
	 * This method does a similar job to JSE 6.0's Arrays.copyOf() method and overloads the method for use with arrays of float
	 * @param <U> The type of input array
	 * @param original The array to copy
	 * @return A copy of the input, of component type <U>
	 */
    public static float[] copyOf(float[] original) {
        
    	float[] copy = new float[original.length];
        System.arraycopy(original, 0, copy, 0, original.length);
        return copy;
    }

	/**
	 * This method does a similar job to JSE 6.0's Arrays.copyOf() method and overloads the method for use with arrays of double
	 * @param <U> The type of input array
	 * @param original The array to copy
	 * @return A copy of the input, of component type <U>
	 */
    public static double[] copyOf(double[] original) {
        
    	double[] copy = new double[original.length];
        System.arraycopy(original, 0, copy, 0, original.length);
        return copy;
    }

	/**
	 * This method does a similar job to JSE 6.0's Arrays.copyOf() method and overloads the method for use with arrays of boolean
	 * @param <U> The type of input array
	 * @param original The array to copy
	 * @return A copy of the input, of component type <U>
	 */
    public static boolean[] copyOf(boolean[] original) {
        
    	boolean[] copy = new boolean[original.length];
        System.arraycopy(original, 0, copy, 0, original.length);
        return copy;
    }
}
