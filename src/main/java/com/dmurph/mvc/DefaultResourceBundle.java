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
package com.dmurph.mvc;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

public class DefaultResourceBundle extends ResourceBundle {
	@Override
	protected Object handleGetObject(String key) {
		return key;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Enumeration getKeys() {
		return EMPTY_ENUMERATION;
	}
	
	@SuppressWarnings("unchecked")
	private static final Enumeration EMPTY_ENUMERATION = new Enumeration() {
		public boolean hasMoreElements() {
			return false;
		}
		
		public Object nextElement() {
			throw new NoSuchElementException();
		}
	};
}