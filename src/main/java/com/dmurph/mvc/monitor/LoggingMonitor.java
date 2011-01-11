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
 * Created at Jan 11, 2011, 4:42:32 AM
 */
package com.dmurph.mvc.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dmurph.mvc.I18n;
import com.dmurph.mvc.IGlobalEventMonitor;
import com.dmurph.mvc.MVCEvent;
import com.dmurph.mvc.ObjectEvent;

/**
 * Default monitor, logs everything.  All events are 
 * logged at the debug level, events with no listeners are logged at the warning level, and events
 * that throw exceptions log at the error level.
 * @author Daniel
 *
 */
public class LoggingMonitor implements IGlobalEventMonitor {
	
	private static final Logger log = LoggerFactory.getLogger(LoggingMonitor.class);
	private IGlobalEventMonitor monitor;
	
	public LoggingMonitor(){
		this(null);
	}
	public LoggingMonitor(IGlobalEventMonitor argMonitor){
		monitor = argMonitor;
	}
	/**
	 * @see com.dmurph.mvc.IGlobalEventMonitor#afterDispatch(com.dmurph.mvc.MVCEvent)
	 */
	public void afterDispatch(MVCEvent argEvent) {
		if(monitor != null){
			monitor.afterDispatch(argEvent);
		}
	}
	
	/**
	 * @see com.dmurph.mvc.IGlobalEventMonitor#beforeDispatch(com.dmurph.mvc.MVCEvent)
	 */
	public void beforeDispatch(MVCEvent argEvent) {
		if(monitor != null){
			monitor.beforeDispatch(argEvent);
		}
		if(argEvent instanceof ObjectEvent<?>){
			log.debug(I18n.getText("monitor.dispatchingValue", argEvent.key, ((ObjectEvent<?>) argEvent).getValue().toString()), argEvent);
		}else{
			log.debug(I18n.getText("monitor.dispatching", argEvent.key), argEvent);
		}
	}
	
	/**
	 * @see com.dmurph.mvc.IGlobalEventMonitor#noListeners(com.dmurph.mvc.MVCEvent)
	 */
	public void noListeners(MVCEvent argEvent) {
		if(monitor != null){
			monitor.noListeners(argEvent);
		}
		log.warn(I18n.getText("monitor.noListeners", argEvent.key), argEvent);

	}
	/**
	 * @see com.dmurph.mvc.IGlobalEventMonitor#exceptionThrown(com.dmurph.mvc.MVCEvent, java.lang.Exception)
	 */
	public void exceptionThrown(MVCEvent argEvent, Exception argException) {
		if(monitor != null){
			monitor.exceptionThrown(argEvent, argException);
		}
		log.error(I18n.getText("monitor.exception", argEvent.key), argEvent);
	}
}
