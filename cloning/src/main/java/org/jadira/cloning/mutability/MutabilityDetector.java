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
package org.jadira.cloning.mutability;

import static org.mutabilitydetector.Configurations.OUT_OF_THE_BOX_CONFIGURATION;

import java.util.HashMap;
import java.util.Map;

import org.mutabilitydetector.AnalysisSession;
import org.mutabilitydetector.IsImmutable;
import org.mutabilitydetector.ThreadUnsafeAnalysisSession;
import org.mutabilitydetector.locations.Dotted;

/**
 * This class provides a thread safe interface to an {@link org.mutabilitydetector.AnalysisSession}
 * for runtime mutability determination. 
 */
public final class MutabilityDetector {
    
    private static final Map<Class<?>,IsImmutable> DETECTED_IMMUTABLE_CLASSES = new HashMap<Class<?>,IsImmutable>();
    
    private static final ThreadLocal<AnalysisSession> ANALYSIS_SESSION = new ThreadLocal<AnalysisSession>() {
        public AnalysisSession initialValue() {
            return ThreadUnsafeAnalysisSession.createWithCurrentClassPath(OUT_OF_THE_BOX_CONFIGURATION);
        }
    };
    
    private static final MutabilityDetector MUTABILITY_DETECTOR = new MutabilityDetector();    
    
    private MutabilityDetector() {
    }
    
    public static final MutabilityDetector getMutabilityDetector() {
        return MUTABILITY_DETECTOR;
    }
    
    public boolean isImmutable(Class<?> clazz) {
     
        if (clazz.isPrimitive() || clazz.isEnum()) {
            return false;
        }
        if (clazz.isArray()) {
            return false;
        }
         
        final IsImmutable isImmutable;
        if (DETECTED_IMMUTABLE_CLASSES.containsKey(clazz)) {
            isImmutable = DETECTED_IMMUTABLE_CLASSES.get(clazz);
        } else {
            Dotted dottedClassName = Dotted.fromClass(clazz);
            isImmutable = ANALYSIS_SESSION.get().resultFor(dottedClassName).isImmutable;
            DETECTED_IMMUTABLE_CLASSES.put(clazz, isImmutable);
        }

        if (isImmutable.equals(IsImmutable.IMMUTABLE) || isImmutable.equals(IsImmutable.EFFECTIVELY_IMMUTABLE)) {
            return true;
        } else {
            return false;
        }
    }
 }
