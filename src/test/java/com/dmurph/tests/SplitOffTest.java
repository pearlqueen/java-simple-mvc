/**
 * Copyright (c) 2010 Daniel Murphy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/**
 * Created on Jun 8, 2010, 12:20:24 AM
 */
package com.dmurph.tests;

import java.util.HashSet;

import com.dmurph.mvc.IEventListener;
import com.dmurph.mvc.IllegalThreadException;
import com.dmurph.mvc.IncorrectThreadException;
import com.dmurph.mvc.MVC;
import com.dmurph.mvc.MVCEvent;

import junit.framework.TestCase;

/**
 * @author Daniel Murphy
 */
public class SplitOffTest extends TestCase implements IEventListener{
	
	public void testSplitOffAndIncorrectThread(){
		MVC.addEventListener("SplitOff", this);
		MVCEvent event = new MVCEvent("SplitOff");
		int dispatches = 10;
		for(int i=0; i<dispatches; i++) {
			event.dispatch();
		}
		MVC.completeRemainingEvents(1000);
	}
	
	public void testIllegalThread() {
		boolean caught = false;
		try {
			MVC.splitOff();
		} catch (IllegalThreadException e) {
			caught = true;
		} catch (IncorrectThreadException e) {
			fail("Wrong exception thrown");
		}
		assertTrue(caught);
	}
	
	
	public HashSet<Thread> threads = new HashSet<Thread>();
	
	public boolean eventReceived(MVCEvent argEvent) {
		
		System.out.println("Recieved thread: "+Thread.currentThread().getName()+", "+Thread.currentThread().getId());
		if(threads.contains(Thread.currentThread())){
			fail("Thread was already encountered");
		}
		
		threads.add(Thread.currentThread());
		try {
			MVC.splitOff();
		} catch (IllegalThreadException e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		catch (IncorrectThreadException e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
		
		boolean caught = false;
		try {
			MVC.splitOff();
		} catch (IllegalThreadException e) {
			fail("Dispatched from MVC thread but IllegalThreadException thrown");
		} catch (IncorrectThreadException e) {
			caught = true;
		}
		assertTrue(caught);
		return true;
	}
}
