/**
 * Created on Jun 22, 2010, 1:53:26 AM
 */
package com.dmurph.mvc;

/**
 * Default monitor for {@link MVC#getGlobalEventMonitor()}, warns the developer
 * through {@link System#out} that an event has no listeners.
 */
public class WarningMonitor implements IGlobalEventMonitor {
	
	/**
	 * @see com.dmurph.mvc.IGlobalEventMonitor#afterDispatch(com.dmurph.mvc.MVCEvent)
	 */
	@Override
	public void afterDispatch(MVCEvent argEvent) {}
	
	/**
	 * @see com.dmurph.mvc.IGlobalEventMonitor#beforeDispatch(com.dmurph.mvc.MVCEvent)
	 */
	@Override
	public void beforeDispatch(MVCEvent argEvent) {}
	
	/**
	 * Warns to the console that an event has no listeners
	 * @see com.dmurph.mvc.IGlobalEventMonitor#noListeners(com.dmurph.mvc.MVCEvent)
	 */
	@Override
	public void noListeners(MVCEvent argEvent) {
		System.out.println(I18n.getText("monitor.noListeners", argEvent.key));
	}
	
}
