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
package org.jadira.scanner.core.helper;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.WeakHashMap;

import javassist.bytecode.ClassFile;

public final class JavassistClassFileHelper {

	private static final WeakHashMap<String, ClassFile> CLASSFILES_BY_NAME = new WeakHashMap<String, ClassFile>(1024, 0.6f);
	private static final WeakHashMap<String, ClassFile> CLASSFILES_BY_FILE = new WeakHashMap<String, ClassFile>(1024, 0.6f);
	
	private static final int MAXIMUM_SIZE = 2048;
	
	private JavassistClassFileHelper() {
	}
	
    public static ClassFile constructClassFile(String className, InputStream bits) throws IOException {

		ClassFile cachedClassFile = CLASSFILES_BY_NAME.get(className);
		if (cachedClassFile != null) {
			return cachedClassFile;
		}
    	
    	DataInputStream dstream = new DataInputStream(new BufferedInputStream(bits));
        ClassFile cf = null;
        try {
            cf = new ClassFile(dstream);
        } finally {
            dstream.close();
            bits.close();
        }
        
        if (cf != null) {
        	CLASSFILES_BY_NAME.put(className, cf);
        }
        return cf;
    }
    
    public static ClassFile constructClassFileForPath(String path, InputStream bits) throws IOException {

		ClassFile cachedClassFile = CLASSFILES_BY_FILE.get(path);
		if (cachedClassFile != null) {
			return cachedClassFile;
		}
    	
    	DataInputStream dstream = new DataInputStream(new BufferedInputStream(bits));
        ClassFile cf = null;
        try {
            cf = new ClassFile(dstream);
        } finally {
            dstream.close();
            bits.close();
        }
        
        if (cf != null) {
        	if (CLASSFILES_BY_NAME.size() < MAXIMUM_SIZE) {
        		CLASSFILES_BY_NAME.put(cf.getName(), cf);
        	}
        	if (CLASSFILES_BY_FILE.size() < MAXIMUM_SIZE) {
        		CLASSFILES_BY_FILE.put(path, cf);
        	}
        }
        return cf;
    }}
