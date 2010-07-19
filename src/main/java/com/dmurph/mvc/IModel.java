/**
 * Created on Jul 16, 2010, 6:17:37 PM
 */
package com.dmurph.mvc;

import java.beans.PropertyChangeListener;

/**
 * @author Daniel Murphy
 *
 */
public interface IModel {
	
	public static final String DIRTY = "MODEL_DIRTY";
	/**
	 * Adds a property change listener to this model
	 * @param argListener
	 */
	public abstract void addPropertyChangeListener(PropertyChangeListener argListener);
	
	/**
	 * Removes a property change listener to this model
	 * @param argListener
	 */
	public abstract void removePropertyChangeListener(PropertyChangeListener argListener);
	
}