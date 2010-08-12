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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.dmurph.mvc.monitor.EventMonitor;
import com.dmurph.mvc.tracking.ICustomTracker;
import com.dmurph.mvc.tracking.ITrackable;
import com.dmurph.tracking.JGoogleAnalyticsTracker;


/**
 * This stores all the listener information, dispatches events
 * to the corresponding listeners.  To dispatch events use
 * {@link MVCEvent#dispatch()}.</br>
 * </br>  
 * Also, look at {@link #splitOff()}.  To set up Google analytics, call {@link #setTracker(JGoogleAnalyticsTracker)},
 * or implement {@link ICustomTracker} in your events to be tracked, and then any event that implements {@link ITrackable}
 * will be tracked.  If {@link ITrackable#getTrackingCategory()} or {@link ITrackable#getTrackingAction()}
 * returns <code>null</code>, then it will be ignored.
 * @author Daniel Murphy
 */
public class MVC extends Thread{
	
	private static final ThreadGroup mvcThreadGroup = new ThreadGroup("MVC Thread Group");
	private static final ArrayList<MVC> mvcThreads = new ArrayList<MVC>();
	private volatile static MVC mainThread = new MVC();
	private IGlobalEventMonitor monitor;
	
	private final HashMap<String, LinkedList<IEventListener>> listeners = new HashMap<String, LinkedList<IEventListener>>();
	private final Queue<MVCEvent> eventQueue = new LinkedList<MVCEvent>();
	private volatile boolean running = false;
	private volatile JGoogleAnalyticsTracker tracker = null;
	
	private volatile static int threadCounter = 0;
	
	private MVC() {
		super(mvcThreadGroup, "MVC Thread #"+(threadCounter++));
		mvcThreads.add(this);
		monitor = new WarningMonitor();
	}

	public static void setTracker(JGoogleAnalyticsTracker tracker) {
		mainThread.tracker = tracker;
	}

	public static JGoogleAnalyticsTracker getTracker() {
		return mainThread.tracker;
	}

	/**
	 * Adds a listener for the given event key.  If the listener is already listening
	 * to that key, then nothing is done.
	 * 
	 * @param argKey
	 * @param argListener
	 */
	public synchronized static void addEventListener( String argKey, IEventListener argListener) {

		if (mainThread.listeners.containsKey(argKey)) {
			// return if we're already listening
			if( isEventListener( argKey, argListener)){
				return;
			}
			mainThread.listeners.get(argKey).addFirst(argListener);
		}
		else {
			final LinkedList<IEventListener> stack = new LinkedList<IEventListener>();
			stack.addFirst(argListener);
			mainThread.listeners.put(argKey, stack);
		}
	}
	
	/**
	 * Checks to see if the listener is listening to the given key.
	 * @param argKey
	 * @param argListener
	 * @return
	 */
	public synchronized static boolean isEventListener( String argKey, IEventListener argListener) {
		if(!mainThread.listeners.containsKey( argKey)){
			return false;
		}
		
		LinkedList<IEventListener> stack = mainThread.listeners.get( argKey);
		return stack.contains( argListener);
	}

	/**
	 * gets the listeners for the given event key.
	 * 
	 * @param argKey
	 * @return
	 */
	public synchronized static LinkedList<IEventListener> getListeners( String argKey) {
		if (mainThread.listeners.containsKey(argKey)) {
			return mainThread.listeners.get(argKey);
		}
		else {
			LinkedList<IEventListener> stack = new LinkedList<IEventListener>();
			mainThread.listeners.put(argKey, stack);
			return stack;
		}
	}

	/**
	 * removes a listener from the given key.
	 * 
	 * @param argKey
	 * @param argListener
	 * @return true if the listener was removed, and false if it wasn't there to
	 *         begin with
	 */
	public static synchronized boolean removeEventListener( String argKey, IEventListener argListener) {
		if (mainThread.listeners.containsKey(argKey)) {
			LinkedList<IEventListener> stack = mainThread.listeners.get(argKey);
			return stack.remove(argListener);
		}
		return false;
	}
	
	/**
	 * Adds an event to the dispatch queue for the MVC thread.
	 * Used by {@link MVCEvent#dispatch()}.
	 * @param argEvent
	 */
	protected synchronized static void dispatchEvent( MVCEvent argEvent) {
		if (mainThread.listeners.containsKey(argEvent.key)) {
			mainThread.eventQueue.add( argEvent	);
			if(!mainThread.running){
				if(mainThread.getState() == State.NEW){
					mainThread.start();
				}
			}
		}else{
			if(mainThread.monitor != null){
				mainThread.monitor.noListeners(argEvent);
			}
		}
	}
	
	/**
	 * Split off the current MVC thread, all queued events and future
	 * event dispatches are handled by a new MVC thread, while this one
	 * runs to completion.  If the thread calling this is not the current
	 * core MVC thread, then an exception is thrown
	 * @throws IllegalThreadException if the thread calling this is not an MVC thread
	 * @throws IncorrectThreadException if the MVC thread calling this is not the main thread, e.g.
	 *									it has already split off.
	 */
	public synchronized static void splitOff() throws IllegalThreadException, IncorrectThreadException{
		if( Thread.currentThread() instanceof MVC){
			MVC thread = (MVC) Thread.currentThread();
			if(thread == mainThread){
				MVC old = mainThread;
				mainThread = new MVC();
				
				for(MVCEvent event : old.eventQueue){
					mainThread.eventQueue.add(event);
				}
				old.eventQueue.clear();
				
				for(String key : old.listeners.keySet()){
					mainThread.listeners.put(key, old.listeners.get(key));
				}
				old.listeners.clear();
				old.running = false;
				mainThread.tracker = old.tracker;
				mainThread.monitor = old.monitor;
				old.tracker = null;
				
				mainThread.start();
			}else{
				throw new IncorrectThreadException();
			}
		}else{
			throw new IllegalThreadException();
		}
	}
	
	/**
	 * Sets the global event monitor, which is called before and after each event is
	 * dispatched.
	 * @param argMonitor
	 * @see IGlobalEventMonitor
	 */
	public synchronized static void setGlobalEventMonitor(IGlobalEventMonitor argMonitor){
		mainThread.monitor = argMonitor;
	}
	
	/**
	 * Gets the global event monitor.  Default is {@link WarningMonitor}.
	 * @return
	 * @see IGlobalEventMonitor
	 */
	public synchronized static IGlobalEventMonitor getGlobalEventMonitor(){
		return mainThread.monitor;
	}
	
	private volatile static EventMonitor guiMonitor = null;
	
	/**
	 * Convenience method to construct and show an {@link EventMonitor}.  To
	 * have more control on how the {@link EventMonitor} is configured,
	 * you can just create it yourself and use {@link #setGlobalEventMonitor(IGlobalEventMonitor)}
	 * to have it be the global event monitor.
	 * @return the {@link EventMonitor}.
	 */
	public synchronized static EventMonitor showEventMonitor(){
		if(guiMonitor == null){
			guiMonitor = new EventMonitor(mainThread.monitor);
			setGlobalEventMonitor(guiMonitor);
		}
		guiMonitor.setVisible(true);
		return guiMonitor;
	}
	
	/**
	 * Hides the event monitor, if you had used {@link #showEventMonitor()}.
	 */
	public synchronized static void hideEventMonitor(){
		if(guiMonitor != null){
			guiMonitor.setVisible(false);
		}
	}
	
	/**
	 * Stops the dispatch thread, dispatching any remaining events
	 * before cleanly returning.  Thread automatically gets started
	 * when new events are dispatched
	 */
	public synchronized static void stopDispatchThread(){
		mainThread.running = false;
	}
	
	/**
	 * Manually starts the dispatch thread.
	 */
	public synchronized static void startDispatchThread(){
		if(mainThread.running){
			return;
		}
		mainThread.start();
	}
	
	public void stopGracefully(){
		running = false;
	}
	
	@Override
	public void run(){
		running = true;
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
		mvcThreads.remove(this);
	}
	
	private synchronized void internalDispatchEvent(MVCEvent argEvent){
		LinkedList<IEventListener> stack = listeners.get(argEvent.key);
		
		if(monitor != null){
			monitor.beforeDispatch(argEvent);
		}
		if(argEvent instanceof ITrackable){
			ITrackable event = (ITrackable) argEvent;
			if(event.getTrackingCategory() != null && event.getTrackingAction() != null){
				if(event instanceof ICustomTracker){
					((ICustomTracker) event).getCustomTracker().trackEvent(event.getTrackingCategory(),
																		   event.getTrackingAction(),
																		   event.getTrackingLabel(),
																		   event.getTrackingValue());
				}
				else if(tracker != null){
					tracker.trackEvent(event.getTrackingCategory(),
									   event.getTrackingAction(),
									   event.getTrackingLabel(),
									   event.getTrackingValue());
				}
			}
		}
		Iterator<IEventListener> it = stack.iterator();
		while(it.hasNext() && argEvent.isPropagating()){
			try{
				it.next().eventReceived( argEvent);				
			}catch(Exception e){
				monitor.exceptionThrown(argEvent, e);
			}
		}
		if(monitor != null){
			monitor.afterDispatch(argEvent);
		}
	}
}
