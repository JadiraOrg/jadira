package org.jadira.scanner.classpath.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.jadira.scanner.classpath.types.JElement;
import org.jadira.scanner.core.filter.AbstractFilter;

public class JElementTypeFilter extends AbstractFilter<JElement> {

	private Set<Class<? extends JElement>> elementTypes = new HashSet<Class<? extends JElement>>();
	
	public JElementTypeFilter() {
	}
	
	public JElementTypeFilter(Class<? extends JElement> type) {
		if (type != null) {
			elementTypes.add(type);
		}
	}
	
	public JElementTypeFilter(Collection<Class<? extends JElement>> types) {
		for (Class<? extends JElement> next : types) {
			elementTypes.add(next);
		}
	}
	
	@Override
	public boolean accept(JElement element) {
		
		final boolean shouldAccept = elementTypes.contains(element.getClass());
		return shouldAccept;
	}
}
