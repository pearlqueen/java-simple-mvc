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
 * Created at 2:47:16 PM, Apr 5, 2010
 */
package com.dmurph.mvc.util;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import com.dmurph.mvc.ICloneable;
import com.dmurph.mvc.IDirtyable;
import com.dmurph.mvc.IModel;
import com.dmurph.mvc.IRevertable;

/**
 * {@link ICloneable} and {@link IDirtyable} Array List.  Will clone all values that
 * implement {@link ICloneable} in the {@link #clone()} and {@link #cloneFrom(ICloneable)} methods.
 * 
 * @author Daniel Murphy
 */
public class MVCArrayList<E extends Object> extends ArrayList<E> implements IModel, ICloneable, IDirtyable, IRevertable {
	private static final long serialVersionUID = 4890270966369581329L;
	
	private static final String DIRTY = "ARRAY_LIST_DIRTY";
	private boolean dirty = false;
	
	private final ArrayList<E> orig = new ArrayList<E>();
	private final PropertyChangeSupport propertyChangeSupport;
	
    public MVCArrayList(){
        propertyChangeSupport = new PropertyChangeSupport(this);
    }
    
	@Override
	public boolean add(E e) {
		boolean ret = super.add(e);
		boolean old = dirty;
		dirty = true;
		firePropertyChange(DIRTY, old, dirty);
		return ret;
	}
	
	@Override
	public void clear() {
		if(size() > 0){
			boolean old = dirty;
			dirty = true;
			firePropertyChange(DIRTY, old, dirty);
			super.clear();
		}
	}
	
	@Override
	public E remove(int index) {
		E ret = super.remove(index);
		boolean old = dirty;
		dirty = true;
		firePropertyChange(DIRTY, old, dirty);
		return ret;
	}
	
	@Override
	public boolean remove(Object o) {
		boolean ret = super.remove(o);
		boolean old = dirty;
		dirty = true;
		firePropertyChange(DIRTY, old, dirty);
		return ret;
	}
	
	@Override
	public E set(int index, E element) {
		E ret = super.set(index, element);
		boolean old = dirty;
		dirty = true;
		firePropertyChange(DIRTY, old, dirty);
		return ret;
	}
	
	/**
	 * Clones from another {@link ArrayList}, if the values are {@link ICloneable}, then
	 * they will be cloned to this one.  Otherwise it's a shallow copy (just sets the same values).
	 * @param argOther an {@link ArrayList}
	 * @see com.dmurph.mvc.ICloneable#cloneFrom(com.dmurph.mvc.ICloneable)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void cloneFrom( ICloneable argOther) {
		MVCArrayList<E> other = (MVCArrayList<E>) argOther;
		clear();
		for(E e : other){
			if(e instanceof ICloneable){
				add((E) ((ICloneable) e).clone());
			}else{
				add(e);
			}
		}
		orig.clear();
		for(E e : other.orig){
			if(e instanceof ICloneable){
				orig.add((E) ((ICloneable) e).clone());
			}else{
				orig.add(e);
			}
		}
	}
	
	// do shallow clone, need to keep object references
	private void cloneFromOrig(){
		clear();
		for(E e: orig){
			add(e);
		}
	}
	
	// do shallow clone, need to keep object references
	private void cloneToOrig(){
		orig.clear();
		for(E e: this){
			orig.add(e);
		}
	}
	
	 /**
	 * @see com.dmurph.mvc.IModel#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
    public void addPropertyChangeListener(PropertyChangeListener argListener) {
        propertyChangeSupport.addPropertyChangeListener(argListener);
    }

    /**
	 * @see com.dmurph.mvc.IModel#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
    public void removePropertyChangeListener(PropertyChangeListener argListener) {
        propertyChangeSupport.removePropertyChangeListener(argListener);
    }

    /**
     * Fires a property change event.  If the argOldValue == argNewValue
     * or argOldValue.equals( argNewValue) then no event is thrown.
     * @param argPropertyName property name, should match the get and set methods for property name
     * @param argOldValue
     * @param argNewValue
     */
    private void firePropertyChange(String argPropertyName, Object argOldValue, Object argNewValue) {
        propertyChangeSupport.firePropertyChange(argPropertyName, argOldValue, argNewValue);
    }

	/**
	 * Clones this object to another {@link MVCArrayList}.  If the array values
	 * are also {@link ICloneable}, then they will be cloned as well.  If not, the values
	 * are just set (shallow copy).
	 * @see java.util.ArrayList#clone()
	 */
	@Override
	public ICloneable clone(){
		MVCArrayList<E> other = new MVCArrayList<E>();
		other.cloneFrom(this);
		return other;
	}

	/**
	 * @see com.dmurph.mvc.IDirtyable#isDirty()
	 */
	@Override
	public boolean isDirty() {
		if(dirty){
			return true;
		}
		for(E e : this){
			if(e instanceof IDirtyable){
				if(((IDirtyable) e).isDirty()){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Just sets the dirty variable
	 * @see com.dmurph.mvc.IDirtyable#setDirty(boolean)
	 */
	@Override
	public boolean setDirty( boolean argDirty) {
		boolean oldDirty = dirty;
		dirty = argDirty;
		firePropertyChange(DIRTY, oldDirty, dirty);
		return oldDirty;
	}

	/**
	 * @see com.dmurph.mvc.IRevertable#revertChanges()
	 */
	@Override
	public boolean revertChanges() {
		if(!isDirty()){
			return false;
		}
		cloneFromOrig();
		for(E e: this){
			if(e instanceof IRevertable){
				((IRevertable) e).revertChanges();
			}
		}
		return true;
	}

	/**
	 * @see com.dmurph.mvc.IRevertable#saveChanges()
	 */
	@Override
	public boolean saveChanges() {
		if(!isDirty()){
			return false;
		}
		cloneToOrig();
		for(E e: this){
			if(e instanceof IRevertable){
				((IRevertable) e).saveChanges();
			}
		}
		return true;
	}
}
