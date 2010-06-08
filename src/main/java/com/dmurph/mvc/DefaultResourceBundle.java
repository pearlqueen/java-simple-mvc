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