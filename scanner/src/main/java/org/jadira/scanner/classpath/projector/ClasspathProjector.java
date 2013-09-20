package org.jadira.scanner.classpath.projector;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.jadira.scanner.core.api.Projector;
import org.jadira.scanner.core.exception.ClasspathAccessException;

import de.schlichtherle.io.archive.zip.ZipEntry;
import de.schlichtherle.util.zip.ZipFile;

public class ClasspathProjector implements Projector<File> {

    public static final ClasspathProjector SINGLETON = new ClasspathProjector();
    
    private static final Map<File, List<File>> PROJECTION_CACHE = new HashMap<File, List<File>>();

    public ClasspathProjector() {
        // TODO Preload the projection cache if possible
    }
    
    @Override
	public List<File> project(File segment) {

	    List<File> files = PROJECTION_CACHE.get(segment);
	    if (files != null) {
	        return files;
	    }
	    
        files = new ArrayList<File>();
        boolean isArchive = false;
        
        if (segment instanceof de.schlichtherle.io.File) {
            if (((de.schlichtherle.io.File)segment).isArchive()) {
                isArchive = true;
            }
        }
	    
//	    if (segment.toString().startsWith(System.getProperty("java.home"))) {	        
//	        String cacheFileKey = buildFileKey(segment);
//	        ClassLoader[] classLoaders = ClassLoaderUtils.getClassLoaders();
//	        
//	        for (ClassLoader cl : classLoaders) {
//	            
//	            InputStream is = null;
//	            InputStreamReader isr = null;
//	            BufferedReader br = null;
//	            try {
//	                is = cl.getResourceAsStream(cacheFileKey);
//                    if (is != null) {
//                        isr = new InputStreamReader(is);
//	                    br = new BufferedReader(isr, 16384);
//	                    String nextLine;
//	                    try {
//                            while ((nextLine = br.readLine()) != null) {
//                                files.add(new de.schlichtherle.io.File(segment.getPath() + System.getProperty("file.separator") + nextLine));
//                            }
//                        } catch (IOException e) {
//                            throw new ClasspathAccessException("Could not open Cached File List: " + e.getMessage(), e);
//                        }
//                        PROJECTION_CACHE.put(segment, files);
//                        return files;
//	                }
//	            } finally {
//	                if (is != null) {
//	                    try {
//                            is.close();
//                        } catch (IOException e) {
//                        }
//	                }
//	                if (isr != null) {
//                        try {
//                            isr.close();
//                        } catch (IOException e) {
//                        }
//                    }
//	                if (br != null) {
//                        try {
//                            br.close();
//                        } catch (IOException e) {
//                        }
//                    }
//	            }
//	        }
//            projectCachedJavaHome(segment);
//	    }
	    
	    if (!isArchive) {
	        
    		File[] dirs = segment.listFiles(new FileFilter() {
    			
    			@Override
    			public boolean accept(File file) {
    				return file.isDirectory();
    			}
    		});
    		
    		for (File dir : dirs) {
    			files.addAll(project(dir));
    		}
    		
    		File[] classes = segment.listFiles(new FileFilter() {
    			
    			@Override
    			public boolean accept(File file) {
    				return !file.isDirectory() && file.getName().endsWith(".class");
    			}
    		});
    		files.addAll(Arrays.asList(classes));
    		
	    } else if (segment.getPath().endsWith("jar")) { // else if ("jar".equals(((de.schlichtherle.io.File)segment).getArchiveDetector().getScheme(segment.getPath()).toString())) {
	        
	        JarFile jarFile = null;
            try {
                jarFile = new JarFile(segment.getPath());
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()) {
                    JarEntry next = entries.nextElement();
                    if (next.getName().endsWith(".class")) {
                        files.add(new de.schlichtherle.io.File(segment.getPath() + System.getProperty("file.separator") + next.getName()));
                    }
                }
            } catch (IOException e) {
                throw new ClasspathAccessException("Could not open JarFile: " + e.getMessage(), e);
            } finally {
                if (jarFile != null) {
                    try {
                        jarFile.close();
                    } catch (IOException e) {
                        // Ignore this
                    }
                }
            }
        } else if (segment.getPath().endsWith("zip")) { // else if ("zip".equals(((TFile)segment).getArchiveDetector().getScheme(segment.getPath()).toString())) {
            ZipFile zipFile = null;
            try {
                zipFile = new ZipFile(segment.getPath());
                @SuppressWarnings("unchecked")
                Enumeration<ZipEntry> entries = zipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry next = entries.nextElement();
                    if (next.getName().endsWith(".class")) {
                        files.add(new de.schlichtherle.io.File(segment.getPath()  + System.getProperty("file.separator") + next.getName()));
                    }
                }
            } catch (IOException e) {
                throw new ClasspathAccessException("Could not open ZipFile: " + e.getMessage(), e);
            } finally {
                if (zipFile != null) {
                    try {
                        zipFile.close();
                    } catch (IOException e) {
                        // Ignore this
                    }
                }
            }
	    } else {
	        
            File[] classes = ((de.schlichtherle.io.File)segment).listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".class");
                }
            });
            files.addAll(Arrays.asList(classes));
	    }
	    PROJECTION_CACHE.put(segment, files);
		return files;
	}

//    private String buildFileKey(File segment) {
//        return ((("java-" 
//           + System.getProperty("java.vendor") 
//           + "-" 
//           + System.getProperty("java.version") 
//           + (segment.toString().substring(System.getProperty("java.home").length())))
//               .replace('/', '_')
//               .replace('\\', '_')
//               .replace(' ', '_')
//               .replace('.', '_')) + ".classes");
//    }
//
//    private void projectCachedJavaHome(File segment) {
//
//        StringBuilder sb = new StringBuilder(buildFileKey(segment));
//        sb.append(System.getProperty("line.separator"));
//        sb.append(System.getProperty("line.separator"));
//        JarFile jarFile = null;
//        try {
//            jarFile = new JarFile(segment.getPath());
//            Enumeration<JarEntry> entries = jarFile.entries();
//            while (entries.hasMoreElements()) {
//                JarEntry next = entries.nextElement();
//                if (next.getName().endsWith(".class")) {
//                    sb.append(next.getName());
//                    sb.append(System.getProperty("line.separator"));
//                }
//            }
//        } catch (IOException e) {
//            throw new ClasspathAccessException("Could not open JarFile: " + e.getMessage(), e);
//        } finally {
//            if (jarFile != null) {
//                try {
//                    jarFile.close();
//                } catch (IOException e) {
//                    // Ignore this
//                }
//            }
//        }
//        sb.append(System.getProperty("line.separator"));
//        sb.append(System.getProperty("line.separator"));
//        System.err.println(sb);
//    }
}
