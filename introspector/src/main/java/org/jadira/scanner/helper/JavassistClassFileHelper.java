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
package org.jadira.scanner.helper;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.WeakHashMap;

import javassist.bytecode.ClassFile;

public class JavassistClassFileHelper {

	private static final WeakHashMap<String, ClassFile> CLASSFILES = new WeakHashMap<String, ClassFile>();
	
	private JavassistClassFileHelper() {
	}
	
    public static ClassFile constructClassFile(String className, InputStream bits) throws IOException {

		ClassFile cachedClassFile = CLASSFILES.get(className);
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
        
        CLASSFILES.put(className, cf);
        return cf;
    }
}
