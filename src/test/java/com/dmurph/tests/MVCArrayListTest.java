/**
 * Created at Sep 6, 2010, 7:41:12 PM
 */
package com.dmurph.tests;

import junit.framework.TestCase;

import com.dmurph.mvc.model.MVCArrayList;

/**
 * @author Daniel
 *
 */
public class MVCArrayListTest extends TestCase{

	public void testDirty(){
		MVCArrayList<Object> hm = new MVCArrayList<Object>();
		hm.add("Hello");
		
		assertEquals(true, hm.isDirty());
		
		hm.setDirty(false);
		assertEquals(false, hm.isDirty());
		
		hm.add("Test1");
		assertEquals(true, hm.isDirty());

		MVCArrayList<String> imbeded = new MVCArrayList<String>();
		hm.add(imbeded);
		
		assertEquals(false, imbeded.isDirty());
		assertEquals(true, hm.isDirty());
		
		hm.setDirty(false);
		
		// verify it propogated to imbedded model
		assertEquals(false, hm.isDirty());
		assertEquals(false, imbeded.isDirty());
		
		imbeded.add("Dirtynow!");
		assertEquals(true, hm.isDirty());
		
		imbeded.setDirty(false);
		assertEquals(false, hm.isDirty());
		
		hm.setDirty(true);
		assertEquals(true, hm.isDirty());
		
		hm.setDirty(false);
		hm.addAll(imbeded);
		assertEquals(true, hm.isDirty());
	}
	
	@SuppressWarnings("unchecked")
	public void testCloning(){
		MVCArrayList<Object> hm = new MVCArrayList<Object>();
		
		MVCArrayList<Object> hm2 = (MVCArrayList<Object>) hm.clone();
		assertEquals(true, hm.equals(hm2));
		
		hm.add("hello");
		hm.add(true);
		assertEquals(false, hm.equals(hm2));
		hm2.cloneFrom(hm);
		assertEquals(true, hm.equals(hm2));
		
		MVCArrayList<String> emb = new MVCArrayList<String>();
		emb.add("HI");
		hm.add(emb);
		hm2.cloneFrom(hm);
		assertEquals(false, hm2.get(2) == hm.get(2));
		assertEquals(true, hm2.get(2).equals(hm.get(2)));
	}
	
	public void testReverting(){
		MVCArrayList<Object> hm = new MVCArrayList<Object>();
		hm.saveChanges();
		
		hm.add("HIIII");
		hm.add("what?");
		hm.saveChanges();
		hm.remove("HIIII");
		hm.add("test");
		hm.revertChanges();
		assertEquals(true, hm.contains("HIIII"));
		assertEquals(true, hm.get(1).equals("what?"));
		assertEquals(false, hm.contains("test"));
		
		MVCArrayList<String> hm2 = new MVCArrayList<String>();
		hm2.add("what");
		hm.add(hm2);
		hm.saveChanges();
		assertEquals(false, hm2.isDirty());
		assertEquals(false, hm.isDirty());
		
		hm2.add("CHANGED!!!");
		hm.revertChanges();
		assertEquals(true, hm2.get(0).equals("what"));
		assertEquals(1, hm2.size());
	}
}
