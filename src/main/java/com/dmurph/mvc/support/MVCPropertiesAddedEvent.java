/**
 * Created at Sep 30, 2010, 3:45:39 AM
 */
package com.dmurph.mvc.support;

import java.beans.PropertyChangeEvent;
import java.util.Collection;

/**
 * Event for when multiple properties are added to models, for performance issues
 * instead of firing many {@link MVCPropertyAddedEvent}s, we fire this event, which
 * contains a collection of all the elements added.  If the properties are indexed, 
 * you can check by calling {@link #isIndexed()}, and the start and end indexes
 * are {@link #getStartIndex()} and {@link #getEndIndex()}.  {@link #getOldValue()}
 * will always return null.
 * @author Daniel Murphy
 *
 */
public class MVCPropertiesAddedEvent extends PropertyChangeEvent {
	private static final long serialVersionUID = 1L;
	
	private final int startIndex;
	private final int endIndex;
	
	/**
	 * Constructs the event with no indexes
	 * @param argSource
	 * @param argPropertyName
	 * @param argNewValues
	 */
	public MVCPropertiesAddedEvent(Object argSource, String argPropertyName, Collection argNewValues) {
		super(argSource, argPropertyName, null, argNewValues);
		startIndex = -1;
		endIndex = -1;
	}
	
	/**
	 * 
	 * @param argSource
	 * @param argPropertyName
	 * @param argNewValues
	 * @param argStartIndex
	 * @param argEndIndex
	 */
	public MVCPropertiesAddedEvent(Object argSource, String argPropertyName, Collection argNewValues, int argStartIndex,
								   int argEndIndex) {
		super(argSource, argPropertyName, null, argNewValues);
		startIndex = argStartIndex;
		endIndex = argEndIndex;
	}
	
	/**
	 * If the property added was indexed.
	 * @return
	 */
	public boolean isIndexed(){
		return startIndex != -1;
	}
	
	/**
	 * Gets the first index of the properties added, 
	 * if the properties are indexed.
	 * @return
	 */
	public int getStartIndex(){
		return startIndex;
	}
	
	/**
	 * Gets the index of the last property added,
	 * if the properties are indexed.
	 * @return
	 */
	public int getEndIndex(){
		return endIndex;
	}
}
