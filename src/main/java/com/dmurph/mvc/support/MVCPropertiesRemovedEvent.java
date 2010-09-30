/**
 * Created at Sep 30, 2010, 1:08:04 PM
 */
package com.dmurph.mvc.support;

import java.beans.PropertyChangeEvent;
import java.util.Collection;

/**
 * Event for when multiple properties are removed from models, for performance issues
 * instead of firing many {@link MVCPropertyRemovedEvent}s, we fire this event, which
 * contains a collection of all the elements removed.  If the properties are indexed, 
 * you can check by calling {@link #isIndexed()}, and the start and end indexes
 * are {@link #getStartIndex()} and {@link #getEndIndex()}.  {@link #getNewValue()}
 * will always return null.
 * @author Daniel Murphy
 */
public class MVCPropertiesRemovedEvent extends PropertyChangeEvent {
	private static final long serialVersionUID = 1L;
	
	private final int startIndex;
	private final int endIndex;
	
	/**
	 * Constructs the event with no indexes
	 * @param argSource
	 * @param argPropertyName
	 * @param argNewValues
	 */
	public MVCPropertiesRemovedEvent(Object argSource, String argPropertyName, Collection argOldValues) {
		super(argSource, argPropertyName, argOldValues, null);
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
	public MVCPropertiesRemovedEvent(Object argSource, String argPropertyName, Collection argOldValues, int argStartIndex,
								   int argEndIndex) {
		super(argSource, argPropertyName, argOldValues, null);
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
