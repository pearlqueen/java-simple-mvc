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
@SuppressWarnings("rawtypes") 
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
