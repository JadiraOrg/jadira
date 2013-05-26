package org.jadira.scanner.classpath.projector;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jadira.scanner.core.api.Projector;

public class ClasspathProjector implements Projector<File> {

	@Override
	public List<File> project(File segment) {

		File[] dirs = segment.listFiles(new FileFilter() {
			
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		});
		
		List<File> files = new ArrayList<File>();
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
		return files;
	}
}
