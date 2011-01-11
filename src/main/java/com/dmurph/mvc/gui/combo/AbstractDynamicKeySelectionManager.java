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
 * Created at Sep 21, 2010, 3:06:41 PM
 */
package com.dmurph.mvc.gui.combo;

import javax.swing.ComboBoxModel;
import javax.swing.JComboBox.KeySelectionManager;

/**
 * Sorting a combo box based on keystrokes can get hairy when custom
 * renderers are used and the objects are not strings.  This {@link KeySelectionManager}
 * adds that functionality back.
 * @author Daniel Murphy
 *
 */
public abstract class AbstractDynamicKeySelectionManager implements KeySelectionManager{
	public static final int DEFAULT_TIMEOUT = 1000;
	
	private int timeout;

	private String builtString = "";
	private long lastMillis = 0;
	
	public AbstractDynamicKeySelectionManager(){
		this(DEFAULT_TIMEOUT);
	}
	
	/**
	 * timout for autoselect word building, in milliseconds
	 * @param argTimeout
	 */
	public AbstractDynamicKeySelectionManager(int argTimeout){
		timeout = argTimeout;
	}
	
	/**
	 * @return the timeout
	 */
	public int getTimeout() {
		return timeout;
	}

	/**
	 * @param argTimeout the timeout to set
	 */
	public void setTimeout(int argTimeout) {
		timeout = argTimeout;
	}

	public int selectionForKey(char aKey, ComboBoxModel aModel) {
		int i,c;
        int currentSelection = -1;
        Object selectedItem = aModel.getSelectedItem();
        String v;
        String pattern;
        
        if ( selectedItem != null ) {
            for ( i=0,c=aModel.getSize();i<c;i++ ) {
                if ( selectedItem == aModel.getElementAt(i) ) {
                    currentSelection  =  i;
                    break;
                }
            }
        }
        pattern = ("" + aKey).toLowerCase();
        aKey = pattern.charAt(0);
        
        // build or clear the string
        long nowMillis = System.currentTimeMillis();
        if(nowMillis - lastMillis < timeout){
        	builtString += aKey;
        }else{
        	builtString = aKey+"";
        }
        lastMillis = nowMillis;
        
        for ( i = ++currentSelection, c = aModel.getSize() ; i < c ; i++ ) {
            Object elem = aModel.getElementAt(i);
			if (elem != null && elem.toString() != null) {
			    v = convertToString(elem).toLowerCase();
			    if ( v.length() > 0 && v.length() >= builtString.length() &&  v.substring(0, builtString.length()).equals(builtString)  ){
			    	return i;
			    }
			}
        }

        for ( i = 0 ; i < currentSelection ; i ++ ) {
            Object elem = aModel.getElementAt(i);
			if (elem != null && elem.toString() != null) {
			    v = convertToString(elem).toLowerCase();
			    if ( v.length() > 0 && v.length() >= builtString.length() && v.substring(0, builtString.length()).equals(builtString) ){
			    	return i;
			    }
			}
        }
        return -1;
	}

	public abstract String convertToString(Object o);
}
