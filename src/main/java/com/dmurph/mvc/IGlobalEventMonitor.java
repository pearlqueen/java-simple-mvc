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
 * Created on Jun 22, 2010, 1:30:26 AM
 */
package com.dmurph.mvc;

/**
 * Interface to monitor all messages dispatched from {@link MVC}.
 * @author Daniel Murphy
 */
public interface IGlobalEventMonitor {
	
	/**
	 * Called when an event has no listeners.
	 * {@link #afterDispatch(MVCEvent)} and {@link #beforeDispatch(MVCEvent)}
	 * are never called with this event.
	 * @param argEvent event whose key is not being listened to.
	 */
	public void noListeners(MVCEvent argEvent);
	
	/**
	 * Called with each event dispatched before dispatching event to
	 * listening objects.
	 * @param argEvent event about to be dispatched to listeners
	 * @see MVCEvent#stopPropagation()
	 */
	public void beforeDispatch(MVCEvent argEvent);
	
	/**
	 * Called with each event dispatched after dispatching event to
	 * listening objects.
	 * @param argEvent event already dispatched to listeners
	 */
	public void afterDispatch(MVCEvent argEvent);
	
	/**
	 * Called when an exception is thrown when dispatching an event.  This
	 * can be called multiple times with the same event, as there can be multiple
	 * listeners.
	 * @param argEvent event that caused the exception
	 * @param argException the exception from the listener
	 */
	public void exceptionThrown(MVCEvent argEvent, Exception argException);
}
