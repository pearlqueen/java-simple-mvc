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
 * Created at 2:19:39 AM, Mar 12, 2010
 */
package com.dmurph.mvc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;


/**
 * This stores all the listener information, dispatches events
 * to the corresponding listeners.  To dispatch events use
 * {@link MVCEvent#dispatch()}.
 * @author Daniel Murphy
 */
public class MVC implements Runnable{
	private static final MVC mvc = new MVC();
	private static final Thread thread = new Thread(mvc, "MVC Thread");
	
	private final HashMap<String, LinkedList<IEventListener>> listeners = new HashMap<String, LinkedList<IEventListener>>();
	private final Queue<MVCEvent> eventQueue = new LinkedList<MVCEvent>();
	private volatile boolean running = false;
	
	private MVC() {
		
	}

	/**
	 * Adds a listener for the given event key.  If the listener is already listening
	 * to that key, then nothing is done.
	 * 
	 * @param argKey
	 * @param argReciever
	 */
	public synchronized static void addEventListener( String argKey, IEventListener argListener) {

		if (mvc.listeners.containsKey(argKey)) {
			// return if we're already listening
			if( isEventListener( argKey, argListener)){
				return;
			}
			mvc.listeners.get(argKey).addFirst(argListener);
		}
		else {
			final LinkedList<IEventListener> stack = new LinkedList<IEventListener>();
			stack.addFirst(argListener);
			mvc.listeners.put(argKey, stack);
		}
	}
	
	/**
	 * Checks to see if the listener is listening to the given key.
	 * @param argKey
	 * @param argListener
	 * @return
	 */
	public synchronized static boolean isEventListener( String argKey, IEventListener argListener) {
		if(!mvc.listeners.containsKey( argKey)){
			return false;
		}
		
		LinkedList<IEventListener> stack = mvc.listeners.get( argKey);
		return stack.contains( argListener);
	}

	/**
	 * gets the listeners for the given event key.
	 * 
	 * @param argKey
	 * @return
	 */
	public synchronized static LinkedList<IEventListener> getListeners( String argKey) {
		if (mvc.listeners.containsKey(argKey)) {
			return mvc.listeners.get(argKey);
		}
		else {
			LinkedList<IEventListener> stack = new LinkedList<IEventListener>();
			mvc.listeners.put(argKey, stack);
			return stack;
		}
	}

	/**
	 * removes a listener from the given key.
	 * 
	 * @param argKey
	 * @param argReciever
	 * @return true if the listener was removed, and false if it wasn't there to
	 *         begin with
	 */
	public static synchronized boolean removeEventListener( String argKey, IEventListener argListener) {
		if (mvc.listeners.containsKey(argKey)) {
			LinkedList<IEventListener> stack = mvc.listeners.get(argKey);
			return stack.remove(argListener);
		}
		return false;
	}
	
	/**
	 * Dispatch an event
	 * @param argEvent
	 */
	protected synchronized static void dispatchEvent( MVCEvent argEvent) {
		if (mvc.listeners.containsKey(argEvent.key)) {
			mvc.eventQueue.add( argEvent	);
			if(!mvc.running){
				thread.start();
			}
		}
	}
	
	public synchronized static void stopDispatchThread(){
		System.out.println("Stopping MVC EventDispatch thread.");
		mvc.running = false;
	}
	
	public synchronized static void restartDispatchThread(){
		if(mvc.running){
			return;
		}
		thread.start();
	}
	
	@Override
	public void run(){
		running = true;
		System.out.println("Starting MVC EventDispatch thread.");
		while(running){
			if(eventQueue.isEmpty()){
				try {
					Thread.sleep(100);
				} catch ( InterruptedException e) {}
			}else {
				MVCEvent event = eventQueue.poll();
				internalDispatchEvent( event);
			}
		}
		System.out.println("MVC EventDispatch thread stopped");
	}
	
	private synchronized void internalDispatchEvent(MVCEvent argEvent){
		LinkedList<IEventListener> stack = listeners.get(argEvent.key);
		
		Iterator<IEventListener> it = stack.iterator();
		while(it.hasNext() && argEvent.isPropagating()){
			it.next().eventReceived( argEvent);
		}
	}
}