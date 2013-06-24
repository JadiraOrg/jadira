package org.jadira.cloning;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jadira.cloning.api.Cloner;
import org.jadira.cloning.data.DeepCopyHolder;
import org.jadira.cloning.data.ExampleEnum;
import org.jadira.cloning.data.IdHolder;
import org.jadira.cloning.data.ReferencesHolder;
import org.jadira.cloning.implementor.PortableCloneStrategy;
import org.jadira.cloning.unsafe.UnsafeOperations;
import org.junit.Assert;
import org.junit.Test;

public class TestCloner {

	private static final Cloner[] CLONERS = new Cloner[] { new BasicCloner(), new BasicCloner(new PortableCloneStrategy()), new MinimalCloner() };

	/**
	 * Test that verifies that JDK types are handled correctly
	 */
	@Test
	public void testImmutableType() {

		for (int i = 0; i < CLONERS.length; i++) {
			doTestImmutableType(CLONERS[i]);
		}
	}

	public void doTestImmutableType(Cloner cloner) {

		final Double double1 = 1203.23D;
		final Double clonedDouble1 = cloner.clone(double1);
		assertSame(double1, clonedDouble1);
		assertEquals(double1, clonedDouble1);

		final String string1 = "TESTDATA_STRING";
		final String clonedString = cloner.clone(string1);
		assertSame(string1, clonedString);
		
		assertEquals(string1, clonedString);
	}

	/**
	 * Test that ensures references are properly preserved during a clone
	 */
	@Test
	public void testReferencesWiring() {

		for (int i = 0; i < CLONERS.length; i++) {
			doTestReferencesWiring(CLONERS[i]);
		}
	}

	public void doTestReferencesWiring(Cloner cloner) {

		final Object ref1 = new Object();
		final Object ref2 = new Object();

		final ReferencesHolder refHolder = new ReferencesHolder();

		refHolder.ref1 = ref1;
		refHolder.ref2 = ref2;

		refHolder.ref3 = ref1;
		refHolder.ref4 = ref2;

		ReferencesHolder refHolderClone = cloner.clone(refHolder);

		assertSame(refHolder.ref1, refHolder.ref3);
		assertSame(refHolder.ref2, refHolder.ref4);
		assertNotSame(refHolder.ref1, refHolder.ref2);
		assertNotSame(refHolder.ref2, refHolder.ref3);

		assertSame(refHolderClone.ref1, refHolderClone.ref3);
		assertSame(refHolderClone.ref2, refHolderClone.ref4);
		assertNotSame(refHolderClone.ref1, refHolderClone.ref2);
		assertNotSame(refHolderClone.ref2, refHolderClone.ref3);
	}
	
	@Test
	public void testDate() {

		for (int i = 0; i < CLONERS.length; i++) {
			if (CLONERS[i] instanceof BasicCloner) {
				doTestDate((BasicCloner)CLONERS[i]);
			} else {
				doTestDate((MinimalCloner)CLONERS[i]);
			}
		}
	}

	public void doTestDate(BasicCloner cloner) {
		
		Date original = new Date();
		cloner.setCloneTransientFields(false);
		Date clone = cloner.clone(original);

		assertEquals(0, clone.getTime());
		
		cloner.setCloneTransientFields(true);
		clone = cloner.clone(original);

		assertEquals(original.getTime(), clone.getTime());
	}
	
	public void doTestDate(MinimalCloner cloner) {

		// MinimalCloner doesn't offer the capability to ignore transients
		// so we don't test for that
		
		Date original = new Date();
		Date clone = cloner.clone(original);

		assertEquals(original.getTime(), clone.getTime());
	}

	@Test
	public void testEnum() {

		for (int i = 0; i < CLONERS.length; i++) {
			doTestEnum(CLONERS[i]);
		}
	}

	public void doTestEnum(Cloner cloner) {
		final ExampleEnum original = ExampleEnum.A;
		assertTrue(original.getClass().isEnum());
		final ExampleEnum clone = cloner.clone(original);
		assertSame(clone, original);
	}
	
	@Test
	public void testArray() {

		for (int i = 0; i < CLONERS.length; i++) {
			doTestArray(CLONERS[i]);
		}
	}

	public void doTestArray(Cloner cloner) {

		final String[] stringArray = { "TEST", "CLONING", "ARRAY" };
		final String[] stringArrayClone = cloner.clone(stringArray);

		assertEquals(stringArray.length, stringArrayClone.length);
		for (int i = 0; i < stringArray.length; i++) {
			assertEquals(stringArray[i], stringArrayClone[i]);
		}

		final double[] doubleArray = { 1.01, 2.3234, 3.234321 };
		final double[] doubleArrayClone = cloner.clone(doubleArray);

		assertEquals(doubleArray.length, doubleArrayClone.length);

		for (int i = 0; i < doubleArray.length; i++) {
			assertEquals(doubleArray[i], doubleArrayClone[i], 0.00000000001);
		}
	}

	@Test
	public void testLargeLinkedList() {

		for (int i = 0; i < CLONERS.length; i++) {
			doTestLargeLinkedList(CLONERS[i]);
		}
	}

	public void doTestLargeLinkedList(Cloner cloner) {
		final List<Integer> lst = new LinkedList<Integer>();
		for (int i = 0; i < 100000; i++) {
			lst.add(i);
		}
		final List<Integer> clone = cloner.clone(lst);
		assertEquals(lst.size(), clone.size());
	}

	@Test
	public void testLinkedList() {

		for (int i = 0; i < CLONERS.length - 1; i++) {
			doTestLinkedList(CLONERS[i]);
		}
	}
	
	public void doTestLinkedList(Cloner cloner) {
		final LinkedList<Object> linkedList = new LinkedList<Object>();

		linkedList.add(100D); 
		linkedList.add("This is a String");
		linkedList.add(35);
		linkedList.add(42);

		final LinkedList<Object> clonedLinkedList = cloner.clone(linkedList);
		assertEquals(linkedList.size(), clonedLinkedList.size());

		for (int i = 0; i < linkedList.size(); i++) {
			assertEquals(linkedList.get(i), clonedLinkedList.get(i));
		}

		assertNotSame(linkedList, clonedLinkedList);
		assertSame(linkedList.get(0), clonedLinkedList.get(0));
		assertEquals(linkedList.get(0), clonedLinkedList.get(0));

		assertEquals(4, linkedList.size());
		linkedList.add(77);
		assertEquals(5, linkedList.size());
		assertEquals(4, clonedLinkedList.size());
		clonedLinkedList.add(8);
		assertEquals(5, clonedLinkedList.size());
	}
	
	@Test
    public void testBasicWithPortable() throws DatatypeConfigurationException {

        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.YEAR, 10);
        XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) cal);
        cal.add(Calendar.MONTH, 3);

        DeepCopyHolder source = new DeepCopyHolder();
        source.value = new IdHolder();
        source.value.setId("A Sample Value to Copy");
        source.timestamp = new Timestamp(System.currentTimeMillis() + 10000000);
        source.calendar = cal;
        source.xmlCalendar = xmlCal;

        BasicCloner cloner = new BasicCloner(new PortableCloneStrategy());
        cloner.initialiseFor(IdHolder.class);
        DeepCopyHolder dest = cloner.clone(source);
        
        Assert.assertEquals(source.value, dest.value);
        Assert.assertNotSame(source.value, dest.value);
        Assert.assertEquals(source.timestamp, dest.timestamp);
        Assert.assertNotSame(source.timestamp, dest.timestamp);
        Assert.assertEquals(source.calendar, dest.calendar);
        Assert.assertNotSame(source.calendar, dest.calendar);
        Assert.assertEquals(source.xmlCalendar, dest.xmlCalendar);
        Assert.assertNotSame(source.xmlCalendar, dest.xmlCalendar);
    }    
	
	@Test
    public void testUnsafeOperations() throws DatatypeConfigurationException {

        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.YEAR, 10);
        XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) cal);
        cal.add(Calendar.MONTH, 3);

        DeepCopyHolder source = new DeepCopyHolder();
        source.value = new IdHolder();
        source.value.setId("A Sample Value to Copy");
        source.timestamp = new Timestamp(System.currentTimeMillis() + 10000000);
        source.calendar = cal;
        source.xmlCalendar = xmlCal;

        DeepCopyHolder dest = UnsafeOperations.getUnsafeOperations().deepCopy(source);
        
        Assert.assertEquals(source.value, dest.value);
        Assert.assertNotSame(source.value, dest.value);
        Assert.assertEquals(source.timestamp, dest.timestamp);
        Assert.assertNotSame(source.timestamp, dest.timestamp);
        Assert.assertEquals(source.calendar, dest.calendar);
        Assert.assertNotSame(source.calendar, dest.calendar);
        Assert.assertEquals(source.xmlCalendar, dest.xmlCalendar);
        Assert.assertNotSame(source.xmlCalendar, dest.xmlCalendar);
    }
	
	@Test
    public void testMinimal() throws DatatypeConfigurationException {

        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.YEAR, 10);
        XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) cal);
        cal.add(Calendar.MONTH, 3);

        DeepCopyHolder source = new DeepCopyHolder();
        source.value = new IdHolder();
        source.value.setId("A Sample Value to Copy");
        source.timestamp = new Timestamp(System.currentTimeMillis() + 10000000);
        source.calendar = cal;
        source.xmlCalendar = xmlCal;

        MinimalCloner mcloner = new MinimalCloner();
        DeepCopyHolder dest = mcloner.clone(source);

        Assert.assertEquals(source.value, dest.value);
        Assert.assertNotSame(source.value, dest.value);
        Assert.assertEquals(source.timestamp, dest.timestamp);
        Assert.assertNotSame(source.timestamp, dest.timestamp);
        Assert.assertEquals(source.calendar, dest.calendar);
        Assert.assertNotSame(source.calendar, dest.calendar);
        Assert.assertEquals(source.xmlCalendar, dest.xmlCalendar);
        Assert.assertNotSame(source.xmlCalendar, dest.xmlCalendar);
    }
	
    public void testBasic() throws DatatypeConfigurationException {

        GregorianCalendar cal = new GregorianCalendar();
        cal.add(Calendar.YEAR, 10);
        XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar((GregorianCalendar) cal);
        cal.add(Calendar.MONTH, 3);

        DeepCopyHolder source = new DeepCopyHolder();
        source.value = new IdHolder();
        source.value.setId("A Sample Value to Copy");
        source.timestamp = new Timestamp(System.currentTimeMillis() + 10000000);
        source.calendar = cal;
        source.xmlCalendar = xmlCal;

        BasicCloner cloner = new BasicCloner();
        cloner.initialiseFor(IdHolder.class);
        DeepCopyHolder dest = cloner.clone(source);
        
        Assert.assertEquals(source.value, dest.value);
        Assert.assertNotSame(source.value, dest.value);
        Assert.assertEquals(source.timestamp, dest.timestamp);
        Assert.assertNotSame(source.timestamp, dest.timestamp);
        Assert.assertEquals(source.calendar, dest.calendar);
        Assert.assertNotSame(source.calendar, dest.calendar);
        Assert.assertEquals(source.xmlCalendar, dest.xmlCalendar);
        Assert.assertNotSame(source.xmlCalendar, dest.xmlCalendar);
    }   
}
