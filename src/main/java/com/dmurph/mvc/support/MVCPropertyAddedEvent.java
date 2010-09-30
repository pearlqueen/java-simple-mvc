/**
 * Created at Sep 30, 2010, 12:29:08 PM
 */
package com.dmurph.mvc.support;

import java.beans.IndexedPropertyChangeEvent;

/**
 * Event for when properties are added to a model.  If it's indexed,
 * check by calling {@link #isIndexed()} and get it by calling
 * {@link #getIndex()}.  {@link #getOldValue()} will always return
 * null.
 * @author Daniel Murphy
 */
public class MVCPropertyAddedEvent extends IndexedPropertyChangeEvent {
	private static final long serialVersionUID = 1L;

	/**
	 * @param argSource
	 * @param argPropertyName
	 * @param argValue
	 */
	public MVCPropertyAddedEvent(Object argSource, String argPropertyName, Object argValue) {
		super(argSource, argPropertyName, null, argValue, -1);
	}
	
	/**
	 * @param argSource
	 * @param argPropertyName
	 * @param argOldValue
	 * @param argNewValue
	 * @param argIndex
	 */
	public MVCPropertyAddedEvent(Object argSource, String argPropertyName, Object argValue, int argIndex) {
		super(argSource, argPropertyName, null, argValue, argIndex);
	}
	
	public boolean isIndexed(){
		return getIndex() != -1;
	}
}
