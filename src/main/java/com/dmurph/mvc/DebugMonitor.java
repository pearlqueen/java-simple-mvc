/**
 * Created on Jul 13, 2010, 4:49:22 PM
 */
package com.dmurph.mvc;

/**
 * @author Daniel Murphy
 *
 */
public class DebugMonitor implements IGlobalEventMonitor {
	
	private IGlobalEventMonitor monitor;
	
	public DebugMonitor(){
		this(null);
	}
	public DebugMonitor(IGlobalEventMonitor argMonitor){
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
			System.out.println(I18n.getText("monitor.dispatchingValue", argEvent.key, ((ObjectEvent<?>) argEvent).getValue().toString()));
		}else{
			System.out.println(I18n.getText("monitor.dispatching", argEvent.key));
		}
	}
	
	/**
	 * @see com.dmurph.mvc.IGlobalEventMonitor#noListeners(com.dmurph.mvc.MVCEvent)
	 */
	public void noListeners(MVCEvent argEvent) {
		if(monitor != null){
			monitor.noListeners(argEvent);
		}
	}
	
}
