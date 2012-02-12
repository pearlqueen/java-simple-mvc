/**
 * Created at Feb 12, 2012, 5:05:39 PM
 */
package com.dmurph.tests;

import junit.framework.TestCase;

import com.dmurph.mvc.IEventListener;
import com.dmurph.mvc.IllegalThreadException;
import com.dmurph.mvc.IncorrectThreadException;
import com.dmurph.mvc.MVC;
import com.dmurph.mvc.MVCEvent;

/**
 * @author Daniel
 */
public class BasicEventTest extends TestCase implements IEventListener {
	
	public int[] eventCounts;
	public boolean splitOff;
	
	public void testEvents() {
		_testEvent(false);
	}
	
	public void testEventsWithSplitoff() {
		_testEvent(true);
	}
	
	public void _testEvent(boolean splitoff) {
		this.splitOff = splitoff;
		int numEventTypes = 5;
		int numToDispatch = 103;
		
		for (int i = 0; i < numEventTypes; i++) {
			MVC.addEventListener(i + "", this);
		}
		
		eventCounts = new int[numEventTypes];
		
		int[] correctCounts = new int[numEventTypes];
		for (int i = 0; i < numToDispatch; i++) {
			int type = (i % numEventTypes);
			(new MVCEvent("" + type)).dispatch();
			correctCounts[type]++;
		}
		MVC.completeRemainingEvents(2000);
		
		for (int i = 0; i < numEventTypes; i++) {
			assertEquals(correctCounts[i], eventCounts[i]);
		}
	}
	
	public boolean eventReceived(MVCEvent argEvent) {
		int type = Integer.parseInt(argEvent.key);
		eventCounts[type]++;
		
		if (splitOff) {
			if (type % 3 == 0) {
				try {
					MVC.splitOff();
				} catch (IllegalThreadException e) {
					fail(e.toString());
				} catch (IncorrectThreadException e) {
					fail(e.toString());
				}
			}
		}
		return true;
	}
}
