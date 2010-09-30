/**
 * Created at Sep 30, 2010, 12:51:22 PM
 */
package com.dmurph.mvc.support;

import java.beans.PropertyChangeSupport;
import java.util.Collection;

/**
 * Adds custom mvc property change events.
 * @author Daniel Murphy
 *
 */
public class MVCPropertyChangeSupport extends PropertyChangeSupport {
	private static final long serialVersionUID = 1L;

	private final Object source;
	/**
	 * @param argSourceBean
	 */
	public MVCPropertyChangeSupport(Object argSourceBean) {
		super(argSourceBean);
		source = argSourceBean;
	}

	
	public void firePropertyAddedEvent(String argPropertyName, Object argProperty){
		MVCPropertyAddedEvent event = new MVCPropertyAddedEvent(source, argPropertyName, argProperty);
		firePropertyChange(event);
	}
	
	public void firePropertyAddedEvent(String argPropertyName, Object argProperty, int argIndex){
		MVCPropertyAddedEvent event = new MVCPropertyAddedEvent(source, argPropertyName, argProperty, argIndex);
		firePropertyChange(event);
	}
	
	public void firePropertiesAddedEvent(String argPropertyName, Collection argProperties){
		MVCPropertiesAddedEvent event = new MVCPropertiesAddedEvent(source, argPropertyName, argProperties);
		firePropertyChange(event);
	}
	
	public void firePropertiesAddedEvent(String argPropertyName, Collection argProperties, int argStartIndex, int argEndIndex){
		MVCPropertiesAddedEvent event = new MVCPropertiesAddedEvent(source, argPropertyName, argProperties, argStartIndex, argEndIndex);
		firePropertyChange(event);
	}
	
	public void firePropertyRemovedEvent(String argPropertyName, Object argProperty){
		MVCPropertyRemovedEvent event = new MVCPropertyRemovedEvent(source, argPropertyName, argProperty);
		firePropertyChange(event);
	}
	
	public void firePropertyRemovedEvent(String argPropertyName, Object argProperty, int argIndex){
		MVCPropertyRemovedEvent event = new MVCPropertyRemovedEvent(source, argPropertyName, argProperty, argIndex);
		firePropertyChange(event);
	}
	
	public void firePropertiesRemovedEvent(String argPropertyName, Collection argProperties){
		MVCPropertiesRemovedEvent event = new MVCPropertiesRemovedEvent(source, argPropertyName, argProperties);
		firePropertyChange(event);
	}
	
	public void firePropertiesRemovedEvent(String argPropertyName, Collection argProperties, int argStartIndex, int argEndIndex){
		MVCPropertiesRemovedEvent event = new MVCPropertiesRemovedEvent(source, argPropertyName, argProperties, argStartIndex, argEndIndex);
		firePropertyChange(event);
	}
}
