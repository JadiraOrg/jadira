package org.jadira.scanner.classpath.visitor;

import java.util.List;

import org.apache.commons.collections15.set.ListOrderedSet;
import org.jadira.scanner.classpath.types.JAnnotation;
import org.jadira.scanner.classpath.types.JClass;
import org.jadira.scanner.classpath.types.JConstructor;
import org.jadira.scanner.classpath.types.JElement;
import org.jadira.scanner.classpath.types.JField;
import org.jadira.scanner.classpath.types.JInnerClass;
import org.jadira.scanner.classpath.types.JInterface;
import org.jadira.scanner.classpath.types.JMethod;
import org.jadira.scanner.classpath.types.JPackage;
import org.jadira.scanner.classpath.types.JParameter;
import org.jadira.scanner.classpath.types.JStaticInitializer;

public class CollectingVisitor implements IntrospectionVisitor {

	private ListOrderedSet<JElement> visitedElements = new ListOrderedSet<JElement>();
	
	public List<JElement> getVisitedElements() {
		return visitedElements.asList();
	}
	
	@Override
	public void visit(JConstructor element) {
		visitedElements.add(element);
	}

	@Override
	public void visit(JMethod element) {
		visitedElements.add(element);
	}

	@Override
	public void visit(JStaticInitializer element) {
		visitedElements.add(element);
	}

	@Override
	public void visit(JPackage element) {
		visitedElements.add(element);
	}

	@Override
	public void visit(JAnnotation<?> element) {
		visitedElements.add(element);
	}

	@Override
	public void visit(JInnerClass element) {
		visitedElements.add(element);
	}

	@Override
	public void visit(JClass element) {
		visitedElements.add(element);
	}

	@Override
	public void visit(JInterface element) {
		visitedElements.add(element);
	}

	@Override
	public void visit(JField element) {
		visitedElements.add(element);
	}

	@Override
	public void visit(JParameter element) {
		visitedElements.add(element);
	}
}
