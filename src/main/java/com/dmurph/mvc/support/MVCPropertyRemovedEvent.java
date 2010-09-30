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
 * Created at Sep 30, 2010, 1:04:14 PM
 */
package com.dmurph.mvc.support;

import java.beans.IndexedPropertyChangeEvent;

/**
 * Event for when properties are added to a model.  If it's indexed,
 * check by calling {@link #isIndexed()} and get it by calling
 * {@link #getIndex()}.  {@link #getNewValue()} will always return
 * null.
 * @author Daniel Murphy
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
