/**
 * Created at Sep 6, 2010, 4:13:45 PM
 */
package com.dmurph.tests;

import junit.framework.TestCase;

import com.dmurph.mvc.model.HashModel;

/**
 * @author Daniel
 *
 */
public class HashModelTest extends TestCase{
	
	public void testDirty(){
		HashModel hm = new HashModel();
		hm.setProperty("Test1", "hi");
		
		assertEquals(true, hm.isDirty());
		
		hm.setDirty(false);
		assertEquals(false, hm.isDirty());
		
		hm.setProperty("Test1", "hii");
		assertEquals(true, hm.isDirty());

		HashModel imbeded = new HashModel();
		hm.setProperty("Test2", imbeded);
		
		assertEquals(false, imbeded.isDirty());
		assertEquals(true, hm.isDirty());
		
		hm.setDirty(false);
		
		// verify it propogated to imbedded model
		assertEquals(false, hm.isDirty());
		assertEquals(false, imbeded.isDirty());
		
		imbeded.setProperty("Dirtynow!", true);
		assertEquals(true, hm.isDirty());
		
		imbeded.setDirty(false);
		assertEquals(false, hm.isDirty());
		
		hm.setDirty(true);
		assertEquals(true, hm.isDirty());
	}
	
	public void testCloning(){
		HashModel hm = new HashModel();
		
		HashModel hm2 = (HashModel) hm.clone();
		assertEquals(true, hm.equals(hm2));
		
		hm.setProperty("Test1", true);
		hm.setProperty("Test2", "hii");
		assertEquals(false,hm.equals(hm2));
		hm2.cloneFrom(hm);
		assertEquals(true, hm.equals(hm2));
	}
	
	public void testReverting(){
		HashModel hm = new HashModel();
		hm.saveChanges();
		
		hm.setProperty("HIIII", true);
		hm.setProperty("what?", false);
		hm.saveChanges();
		hm.setProperty("HIIII", false);
		hm.setProperty("test", "hi");
		hm.revertChanges();
		assertEquals(true, hm.getProperty("HIIII"));
		assertEquals(false, hm.getProperty("what?"));
		assertEquals(null, hm.getProperty("test"));
		
		HashModel hm2 = new HashModel();
		hm2.setProperty("what", "persist");
		hm.setProperty("embedded", hm2);
		hm.saveChanges();
		assertEquals(false, hm2.isDirty());
		assertEquals(false, hm.isDirty());
		
		hm2.setProperty("what", "CHANGED!!!");
		hm.revertChanges();
		assertEquals("persist", hm2.getProperty("what"));
	}
}
