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
