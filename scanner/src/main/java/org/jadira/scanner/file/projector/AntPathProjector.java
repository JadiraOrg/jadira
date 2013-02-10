package org.jadira.scanner.file.projector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jadira.scanner.core.api.Projector;
import org.jadira.scanner.core.exception.FileAccessException;
import org.jadira.scanner.core.helper.FileUtils;
import org.jadira.scanner.core.helper.filenamefilter.AntPathFilter;

public class AntPathProjector implements Projector<File> {

	private String path;
	
	public AntPathProjector(String path) {
		this.path = path;
	}
	
	@Override
	public List<File> project(File segment) {

		final List<File> files;
		
		AntPathFilter antPathMatcher = new AntPathFilter(path);
		if (antPathMatcher.isPatterned()) {
			files = findFilesForPatternPath(segment, path);
		} else {
			files = findFilesForActualPath(segment, path);
		}
		return files;
	}

	private List<File> findFilesForPatternPath(File parentFile, String pattern) throws FileAccessException {

		final List<File> files = new ArrayList<File>();

		AntPathFilter antPathMatcher = new AntPathFilter(pattern);
		if (antPathMatcher.match(AntPathFilter.PATH_SEPARATOR) || antPathMatcher.match("")) {
			files.add(parentFile);
		} else {
			findFilesForPatternRecursively(pattern, files, parentFile, parentFile);
		}
		return files;
	}

	private List<File> findFilesForActualPath(File parentFile, String path) {

		final List<File> files = new ArrayList<File>();
		File nextFile = FileUtils.getFileForPathName(path, parentFile);
		if ((nextFile != null) && nextFile.isFile()) {
			files.add(nextFile);
		}
		return files;
	}

	private void findFilesForPatternRecursively(String pattern, final List<File> resultsHolder, File root, File currentParent) {

		if (currentParent.isDirectory()) {
			File[] childFiles = currentParent.listFiles();
			for (File next : childFiles) {
				String path = next.getPath().substring(root.getPath().length());
				if (next.isDirectory() && (!path.endsWith(AntPathFilter.PATH_SEPARATOR))) {
					path = path + AntPathFilter.PATH_SEPARATOR;
				}
				AntPathFilter antPathMatcher = new AntPathFilter(pattern);
				if (antPathMatcher.match(path)) {
					resultsHolder.add(next);
				} else if (antPathMatcher.matchStart(path)) {
					findFilesForPatternRecursively(pattern, resultsHolder, root, currentParent);
				}
			}
		}
	}
}
