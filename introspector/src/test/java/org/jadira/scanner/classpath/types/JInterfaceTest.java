package org.jadira.scanner.classpath.types;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.jadira.scanner.classpath.ClasspathResolver;
import org.jadira.scanner.classpath.visitor.CollectingVisitor;
import org.jadira.scanner.core.exception.ClasspathAccessException;
import org.junit.Test;

public class JInterfaceTest {

	@Test
	public void getSuperInterfaces() throws ClasspathAccessException {

		ClasspathResolver helper = new ClasspathResolver();
		JInterface intf = JInterface.getJInterface("java.awt.event.AWTEventListener", helper);
		List<JInterface> superIntf = intf.getSuperInterfaces();
		assertEquals(1, superIntf.size());
		assertEquals(java.util.EventListener.class, superIntf.get(0).getActualClass());
	}

	@Test
	public void getMethods() {

		ClasspathResolver helper = new ClasspathResolver();
		JInterface intf = JInterface.getJInterface("java.awt.event.AWTEventListener", helper);
		List<JInterface> superIntf = intf.getSuperInterfaces();
		assertEquals(1, superIntf.size());
		assertEquals(0, superIntf.get(0).getMethods().size());

		assertEquals(1, intf.getMethods().size());
		assertEquals("eventDispatched", intf.getMethods().get(0).getName());
		assertEquals(1, intf.getMethods().get(0).getParameters().size());
		assertEquals("0", intf.getMethods().get(0).getParameters().get(0).getName());
		assertEquals(0, intf.getMethods().get(0).getParameters().get(0).getIndex());
		assertEquals("java.awt.AWTEvent", intf.getMethods().get(0).getParameters().get(0).getType().getName());
	}

	@Test
	public void getActualInterface() {

		ClasspathResolver helper = new ClasspathResolver();
		JInterface intf = JInterface.getJInterface("java.awt.event.AWTEventListener", helper);
		assertEquals(java.awt.event.AWTEventListener.class, intf.getActualClass());
		assertEquals(java.awt.event.AWTEventListener.class, intf.getActualInterface());
	}

	@SuppressWarnings("deprecation")
	@Test
	public void getAnnotations() {

		ClasspathResolver helper = new ClasspathResolver();
		JInterface intf = JInterface.getJInterface("java.rmi.registry.RegistryHandler", helper);
		assertEquals(java.rmi.registry.RegistryHandler.class, intf.getActualClass());
		assertEquals(java.rmi.registry.RegistryHandler.class, intf.getActualInterface());
		assertEquals(1, intf.getAnnotations().size());
		JAnnotation<?> ann = intf.getAnnotations().iterator().next();
		assertTrue(ann.getActualAnnotation() instanceof Deprecated);
	}

	@Test
	public void getPackage() {

		ClasspathResolver helper = new ClasspathResolver();
		JInterface intf = JInterface.getJInterface("java.awt.event.AWTEventListener", helper);
		assertEquals("java.awt.event", intf.getPackage().getName());
		assertEquals(Package.getPackage("java.awt.event"), intf.getPackage().getActualPackage());
	}

	@Test
	public void getActualClass() {

		ClasspathResolver helper = new ClasspathResolver();
		JInterface intf = JInterface.getJInterface("java.awt.event.AWTEventListener", helper);
		assertEquals(java.awt.event.AWTEventListener.class, intf.getActualClass());
	}

// public Set<JInterface> getSubInterfaces()
// public Set<JClass> getImplementingClasses()
// public Set<JEnum> getImplementingEnums()

	@Test
	public void acceptVisitor() {

		ClasspathResolver helper = new ClasspathResolver();
		JInterface intf = JInterface.getJInterface("java.awt.event.AWTEventListener", helper);

		CollectingVisitor visitor = new CollectingVisitor();
		intf.acceptVisitor(visitor);

		assertEquals(4, visitor.getVisitedElements().size());
	}

	@Test
	public void getEnclosingElement() {
		ClasspathResolver helper = new ClasspathResolver();
		JInterface intf = JInterface.getJInterface("java.awt.event.AWTEventListener", helper);
		assertEquals("java.awt.event", intf.getPackage().getName());
		assertEquals("java.awt.event", intf.getEnclosingElement().getName());
	}
}
