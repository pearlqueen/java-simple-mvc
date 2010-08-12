/**
 * Created on Jun 22, 2010, 1:53:26 AM
 */
package com.dmurph.mvc;

/**
 * Default monitor for {@link MVC#getGlobalEventMonitor()}, warns the developer
 * through {@link System#err} that an event has no listeners.
 */
public class WarningMonitor implements IGlobalEventMonitor {
	
	private IGlobalEventMonitor monitor;
	
	public WarningMonitor(){
		this(null);
	}
	public WarningMonitor(IGlobalEventMonitor argMonitor){
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
	}
	
	/**
	 * Warns to the console that an event has no listeners
	 * @see com.dmurph.mvc.IGlobalEventMonitor#noListeners(com.dmurph.mvc.MVCEvent)
	 */
	public void noListeners(MVCEvent argEvent) {
		if(monitor != null){
			monitor.noListeners(argEvent);
		}
		System.err.println(I18n.getText("monitor.noListeners", argEvent.key));
	}
	
	/**
	 * @see com.dmurph.mvc.IGlobalEventMonitor#exceptionThrown(com.dmurph.mvc.MVCEvent, java.lang.Exception)
	 */
	public void exceptionThrown(MVCEvent argEvent, Exception argException) {
		if(monitor != null){
			monitor.exceptionThrown(argEvent, argException);
		}
		// TODO locale
		System.err.println("Exception thrown when dispatching event "+argEvent+":"+argException.toString());
		argException.printStackTrace(System.err);
	}
}
