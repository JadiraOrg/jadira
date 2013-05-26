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
package org.jadira.bindings.core.utils.string;

/**
 * Utility methods for working with Strings
 */
public final class StringUtils {

    private StringUtils() {
    }
    
    /**
     * Removes any whitespace from a String, correctly handling surrogate characters
     * @param string String to process
     * @return String with any whitespace removed
     */
    public static String removeWhitespace(String string) {

        if (string == null || string.length() == 0) {
            return string;
        } else {
            int codePoints = string.codePointCount(0, string.length());
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < codePoints; i++) {
                int offset = string.offsetByCodePoints(0, i);

                int nextCodePoint = string.codePointAt(offset);
                if (!Character.isWhitespace(nextCodePoint)) {
                    sb.appendCodePoint(nextCodePoint);
                }
            }

            if (string.length() == sb.length()) {
                return string;
            } else {
                return sb.toString();
            }
        }
    }
}
