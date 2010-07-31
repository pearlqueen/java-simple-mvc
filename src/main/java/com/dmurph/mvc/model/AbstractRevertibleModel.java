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
 * Created at Jun 21, 2010, 3:54:44 AM
 */
package com.dmurph.mvc.model;

import com.dmurph.mvc.IDirtyable;
import com.dmurph.mvc.IRevertible;
import com.dmurph.mvc.support.ISupportable;
import com.dmurph.mvc.support.RevertibleSupport;
import com.dmurph.mvc.support.RevertibleSupport.PropertyWrapper;

/**
 * This model keeps track of original and changed property values through the 
 * {@link #firePropertyChange(String, Object, Object)} method.  Assuming an
 * extending class correctly implements {@link #firePropertyChange(String, Object, Object)}
 * and {@link #setProperty(String, Object)} correctly, this class will keep track of any
 * changes to the model so the model can save or revert changes.  This also makes the
 * {@link #isDirty()} method more accurate, as it can check all the properties to see if
 * they have changed.
 * @author Daniel Murphy
 */
public abstract class AbstractRevertibleModel extends AbstractModel implements IDirtyable, IRevertible{
	private static final long serialVersionUID = 1L;
	
	private boolean overridingDirty = false;
	private final RevertibleSupport revertibleSupport;
	
	public AbstractRevertibleModel(){
		super();
		revertibleSupport = new RevertibleSupport(propertyChangeSupport, new ISupportable() {
			@Override
			public void setProperty(String argPropertyName, Object argProperty) {
				AbstractRevertibleModel.this.setProperty(argPropertyName, argProperty);
			}
		});
	}
    /**
     * If the model is "dirty", or changed since last save.  This
     * method can be expensive, as it checks all changed properties to
     * see if they are dirty.  Will also return true if {@link #setDirty(boolean)}
     * was recently called with true.
     * @see com.dmurph.mvc.IDirtyable#isDirty()
     */
	@Override
	public boolean isDirty(){
		if(overridingDirty){
			return overridingDirty;
		}
		for(PropertyWrapper prop : revertibleSupport.getRecordedProperties()){
			if(prop.isDirty()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * If called with false, this is the equivalent
	 * of {@link #saveChanges()}.  If called with true,
	 * the model is dirty until {@link #saveChanges()},
	 * {@link #revertChanges()}, or this method is called
	 * with false.
	 * @see com.dmurph.mvc.IDirtyable#setDirty(boolean)
	 */
	@Override
	public boolean setDirty(boolean argDirty) {
		boolean dirty = isDirty();
		if(argDirty){
			overridingDirty = true;
			return dirty;
		}else{
			overridingDirty = false;
			saveChanges();
			return dirty;
		}
	}
	
	/**
	 * Used to reset variables when {@link #revertChanges()} is called.
	 * @param argPropertyName the property name corresponding to the name
	 * 						  given to the {@link #firePropertyChange(String, Object, Object)}
	 * 						  method
	 * @param argValue the value to set the property
	 * @return the old value
	 */
	protected abstract Object setProperty(String argPropertyName, Object argValue);
	
	
	/**
	 * @see com.dmurph.mvc.IRevertible#saveChanges()
	 */
	@Override
	public boolean saveChanges() {
		return revertibleSupport.saveChanges();
	}
	
	/**
	 * @see com.dmurph.mvc.IRevertible#revertChanges()
	 */
	@Override
	public boolean revertChanges() {
		return revertibleSupport.revertChanges();
	}
}
