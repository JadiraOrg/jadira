/*
 *  Copyright 2012 Chris Pheby
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
package org.jadira.scanner.core.helper.filenamefilter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Class that matches actual directory paths against an Ant style path
 * See http://ant.apache.org/manual/dirtasks.html
 */
public class AntPathFilter implements FilenameFilter {

    /**
     * Path separator: "/"
     */
    public static final String PATH_SEPARATOR = "/";
	
    private final String pattern;

    public AntPathFilter(String pattern) {
    	this.pattern = pattern;
    }
    
	@Override
	public boolean accept(File dir, String name) {
		
		if (dir == null || !dir.isDirectory()) {
			return false;
		}
		
		final String path = dir.getPath().replace('\\', '/') + '/' + name;
		
		return match(path);
	}
	
    /**
     * Returns true if the given path resolves a pattern as opposed to a literal path
     * @return True if pattern containing any * or ? character
     */
    public boolean isPatterned() {

        final boolean result;
        if (pattern.indexOf('*') != -1 || pattern.indexOf('?') != -1) {
            result = true;
        } else {
            result = false;
        }

        return result;
    }

    /**
     * Returns true if the given path resolves a literal path as opposed to a pattern
     * @return False in case of pattern containing any * or ? character, true otherwise.
     */
    public boolean isLiteral() {
    	return !isPatterned();
    }
    
    /**
     * Match the entire path against the given pattern
     * @param path The path to match
     * @return True if whole path matches
     */
    public boolean match(String path) {
        return doMatch(pattern, path, true);
    }

    /**
     * Match the start of the path against the given pattern
     * @param pattern The pattern
     * @param path The path to match
     * @return True if the path begins with the given pattern
     */
    public boolean matchStart(String path) {
        return doMatch(pattern, path, false);
    }

    private boolean doMatch(String pattern, String path, boolean fullMatch) {

        final boolean retVal;

        Pattern regex = convertAntPatternToRegex(pattern);
        Matcher matcher = regex.matcher(path);
        matcher.find();

        if (fullMatch) {
            if ((matcher.start() == 0) && (matcher.end() == path.length())) {
                retVal = true;
            } else {
                retVal = false;
            }

        } else {
            if (doMatch(pattern, path, true)) {
                retVal = true;
            } else {
                String nextPattern = StringUtils.substringBeforeLast(pattern, PATH_SEPARATOR);
                if (pattern.equals(nextPattern)) {
                    return false;
                } else {
                    return doMatch(nextPattern, path, false);
                }
            }
        }
        return retVal;
    }

    private Pattern convertAntPatternToRegex(String antPattern) {

    	String regex = antPattern;
        if (regex.endsWith(PATH_SEPARATOR)) {
        	regex = regex + "**";
        }

        regex = Pattern.quote(regex);

        regex = regex.replaceAll("[?]", "\\\\E[.^/]\\\\Q");
        regex = regex.replaceAll("([^*]|^)[*]([^*]|$){1}", "$1\\\\E(?:[^/]*)\\\\Q$2");
        regex = regex.replaceAll("[*]{2}", "\\\\E(?:.*)\\\\Q");
        return Pattern.compile(regex);
    }
}
