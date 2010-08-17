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
 * Created on May 22, 2010, 3:50:08 AM
 */
package com.dmurph.mvc.model;

import com.dmurph.mvc.IDirtyable;

/**
 * Keeps track of if a model is dirty through calling the methods {@link #setDirty(boolean)}
 * and {@link #firePropertyChange(String, Object, Object)}.  This class also fires a property
 * change event when the dirty property changes, with the key <code>"dirty"</code>
 * @author Daniel Murphy
 */
public abstract class AbstractDirtyableModel extends AbstractModel implements IDirtyable {
	private static final long serialVersionUID = 1L;
	
	private boolean dirty = false;
	
    /**
     * If the model is "dirty", or changed since last save.
     * @see com.dmurph.mvc.IDirtyable#isDirty()
     */
	public boolean isDirty(){
		return dirty;
	}
	
	/**
	 * @see com.dmurph.mvc.IDirtyable#setDirty(boolean)
	 */
	public void setDirty(boolean argDirty){
		boolean oldDirty = dirty;
		dirty = argDirty;
		super.firePropertyChange("dirty", oldDirty, dirty);
	}
	
	/**
	 * @see com.dmurph.mvc.model.AbstractModel#firePropertyChange(java.lang.String, java.lang.Object, java.lang.Object)
	 */
	@Override
	protected void firePropertyChange(String argPropertyName, Object argOldValue, Object argNewValue) {
		if(argOldValue != null && !argOldValue.equals(argNewValue)){
			setDirty(true);
		}else if(argNewValue == null && argNewValue != null){
			setDirty(true);
		}
		super.firePropertyChange(argPropertyName, argOldValue, argNewValue);
	}
}
