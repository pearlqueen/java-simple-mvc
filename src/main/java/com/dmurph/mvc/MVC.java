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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dmurph.mvc.monitor.EventMonitor;
import com.dmurph.mvc.monitor.LoggingMonitor;
import com.dmurph.mvc.monitor.WarningMonitor;
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
	
	private static final Logger log = LoggerFactory.getLogger(MVC.class);
	
	private static final ThreadGroup mvcThreadGroup = new ThreadGroup("MVC Thread Group");
	private static final ArrayList<MVC> mvcThreads = new ArrayList<MVC>();
	private volatile static MVC mainThread = new MVC(0);
	private IGlobalEventMonitor monitor = null;
	
	private final HashMap<String, LinkedList<IEventListener>> listeners = new HashMap<String, LinkedList<IEventListener>>();
	private final Queue<MVCEvent> eventQueue = new LinkedList<MVCEvent>();
	private volatile boolean running = false;
	private volatile JGoogleAnalyticsTracker tracker = null;
	
	private final int threadCount;
	
	private MVC(int argNum) {
		super(mvcThreadGroup, "MVC Thread #"+argNum);
		threadCount = argNum;
		mvcThreads.add(this);
		monitor = new LoggingMonitor();
	}

	public synchronized static void setTracker(JGoogleAnalyticsTracker tracker) {
		mainThread.tracker = tracker;
	}

	public synchronized static JGoogleAnalyticsTracker getTracker() {
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
		mainThread._addEventListener(argKey, argListener);
	}
	
	/**
	 * Checks to see if the listener is listening to the given key.
	 * @param argKey
	 * @param argListener
	 * @return
	 */
	public synchronized static boolean isEventListener( String argKey, IEventListener argListener) {
		return mainThread._isEventListener(argKey, argListener);
	}

	/**
	 * Gets the listeners for the given event key.
	 * 
	 * @param argKey
	 * @return
	 */
	public synchronized static LinkedList<IEventListener> getListeners( String argKey) {
		return mainThread._getListeners(argKey);
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
		return mainThread._removeEventListener(argKey, argListener);
	}
	
	
	/**
	 * Adds an event to the dispatch queue for the MVC thread.
	 * Used by {@link MVCEvent#dispatch()}.
	 * @param argEvent
	 */
	protected synchronized static void dispatchEvent( MVCEvent argEvent) {
		mainThread._dispatchEvent(argEvent);
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
				log.debug("Splitting off...");
				MVC old = mainThread;
				mainThread = new MVC(old.threadCount+1);
				
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
				
				log.debug("Starting next MVC thread");
				mainThread.start();
			}else{
				log.error("Can't split off when this isn't the main thread");
				throw new IncorrectThreadException();
			}
		}else{
			log.error("Can't split off, we're not in the MVC thread.");
			throw new IllegalThreadException();
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
	
	private void _addEventListener(String argKey, IEventListener argListener){
		if (listeners.containsKey(argKey)) {
			// return if we're already listening
			if( _isEventListener( argKey, argListener)){
				return;
			}
			listeners.get(argKey).addFirst(argListener);
		}
		else {
			final LinkedList<IEventListener> stack = new LinkedList<IEventListener>();
			stack.addFirst(argListener);
			listeners.put(argKey, stack);
		}
	}
	
	private boolean _isEventListener( String argKey, IEventListener argListener) {
		if(!listeners.containsKey( argKey)){
			return false;
		}
		
		LinkedList<IEventListener> stack = listeners.get( argKey);
		return stack.contains( argListener);
	}
	
	private LinkedList<IEventListener> _getListeners( String argKey) {
		if (listeners.containsKey(argKey)) {
			return listeners.get(argKey);
		}
		else {
			LinkedList<IEventListener> stack = new LinkedList<IEventListener>();
			listeners.put(argKey, stack);
			return stack;
		}
	}

	private boolean _removeEventListener( String argKey, IEventListener argListener) {
		if (listeners.containsKey(argKey)) {
			LinkedList<IEventListener> stack = listeners.get(argKey);
			return stack.remove(argListener);
		}
		return false;
	}

	private void _dispatchEvent( MVCEvent argEvent) {
		if (listeners.containsKey(argEvent.key)) {
			eventQueue.add( argEvent);
			if(!running){
				if(getState() == State.NEW){
					start();
				}
			}
		}else{
			if(monitor != null){
				monitor.noListeners(argEvent);
			}
		}
	}
	
	@Override
	public void run(){
		running = true;
		log.info("MVC thread #"+threadCount+" starting up");
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
			try{
				monitor.beforeDispatch(argEvent);
			}
			catch(Exception e){
				// really? 
				log.error("Exception caught from monitor", e);
			}
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
				}else{
					log.warn("Event could not be tracked, as the tracker is null", event);
				}
			}
		}
		Iterator<IEventListener> it = stack.iterator();
		while(it.hasNext() && argEvent.isPropagating()){
			try{
				it.next().eventReceived( argEvent);				
			}catch(Exception e){
				if(monitor != null){
					try{// why do I have to do this? monitors shouldn't throw exceptions
						monitor.exceptionThrown(argEvent, e);
					}catch(Exception e2){
						log.error("Exception caught from event dispatch", e);
						log.error("Exception caught from monitor", e2);
						
					}
				}else{
					log.error("Exception caught from event dispatch", e);
				}
			}
		}
		
		if(monitor != null){
			try{ // do i really have to do this?
				monitor.afterDispatch(argEvent);
			}
			catch(Exception e){
				// really? 
				log.error("Exception caught from monitor", e);
			}
		}
	}
}
