/**
 * Created at Sep 30, 2010, 1:04:14 PM
 */
package com.dmurph.mvc.support;

import java.beans.IndexedPropertyChangeEvent;

/**
 * Event for when properties are added to a model.  If it's indexed,
 * check by calling {@link #isIndexed()} and get it by calling
 * {@link #getIndex()}.  {@link #getNewValue()} will always return
 * null.
 * @author Daniel
 *
 */
public class MVCPropertyRemovedEvent extends IndexedPropertyChangeEvent{
	private static final long serialVersionUID = 1L;

	/**
	 * @param argSource
	 * @param argPropertyName
	 * @param argValue
	 */
	public MVCPropertyRemovedEvent(Object argSource, String argPropertyName, Object argValue) {
		super(argSource, argPropertyName, argValue, null, -1);
	}
	
	/**
	 * @param argSource
	 * @param argPropertyName
	 * @param argOldValue
	 * @param argNewValue
	 * @param argIndex
	 */
	public MVCPropertyRemovedEvent(Object argSource, String argPropertyName, Object argValue, int argIndex) {
		super(argSource, argPropertyName, argValue, null, argIndex);
	}
	
	public boolean isIndexed(){
		return getIndex() != -1;
	}
}
