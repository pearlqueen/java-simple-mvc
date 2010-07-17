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
 * A full mvc implementation of an {@link ArrayList}.  Supports all operations in {@link ICloneable}, {@link IDirtyable},
 * and {@link IRevertable}.  Also fires property change events for the size of the array ({@link #SIZE}) and the dirty value
 * ({@link #DIRTY}), and if an element in the array changed ({@link #ELEMENT}).
 * 
 * @author Daniel Murphy
 */
public class MVCArrayList<E extends Object> extends ArrayList<E> implements IModel, ICloneable, IDirtyable, IRevertable {
	private static final long serialVersionUID = 4890270966369581329L;
	
	/**
	 * Dirty property name for listening to property change events
	 * @see #addPropertyChangeListener(PropertyChangeListener)
	 */
	public static final String DIRTY = "ARRAY_LIST_DIRTY";
	/**
	 * Array size property name for listening to property change events
	 * @see #addPropertyChangeListener(PropertyChangeListener)
	 */
	public static final String SIZE = "ARRAY_LIST_SIZE";
	/**
	 * A value in the array was changed.
	 * @see #addPropertyChangeListener(PropertyChangeListener)
	 */
	public static final String ELEMENT = "ARRAY_LIST_ELEMENT_CHANGED";
	
	private boolean dirty = false;
	
	private final ArrayList<E> saved = new ArrayList<E>();
	private final PropertyChangeSupport propertyChangeSupport;
	
    public MVCArrayList(){
        propertyChangeSupport = new PropertyChangeSupport(this);
    }
    
	@Override
	public boolean add(E e) {
		boolean ret = super.add(e);
		firePropertyChange(SIZE, size() - 1, size());
		boolean old = dirty;
		dirty = true;
		firePropertyChange(DIRTY, old, dirty);
		return ret;
	}
	
	@Override
	public void clear() {
		if(size() > 0){
			int oldSize = size();
			super.clear();
			firePropertyChange(SIZE, oldSize, 0);
			boolean old = dirty;
			dirty = true;
			firePropertyChange(DIRTY, old, dirty);
		}
	}
	
	@Override
	public E remove(int index) {
		E ret = super.remove(index);
		firePropertyChange(SIZE, size() - 1, size());
		boolean old = dirty;
		dirty = true;
		firePropertyChange(DIRTY, old, dirty);
		return ret;
	}
	
	@Override
	public boolean remove(Object o) {
		boolean ret = super.remove(o);
		firePropertyChange(SIZE, size() - 1, size());
		boolean old = dirty;
		dirty = true;
		firePropertyChange(DIRTY, old, dirty);
		return ret;
	}
	
	@Override
	public E set(int index, E element) {
		E ret = super.set(index, element);
		firePropertyChange(ELEMENT, ret, element);
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
		saved.clear();
		for(E e : other.saved){
			if(e instanceof ICloneable){
				saved.add((E) ((ICloneable) e).clone());
			}else{
				saved.add(e);
			}
		}
		this.dirty = other.dirty;
	}
	
	// do shallow clone, need to keep object references
	private void setFromSaved(){
		clear();
		for(E e: saved){
			add(e);
		}
	}
	
	// do shallow clone, need to keep object references
	private void setToSaved(){
		saved.clear();
		for(E e: this){
			saved.add(e);
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
	 * Also chacks to see if elements in this
	 * array are dirty, if any are {@link IDirtyable}.
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
	 * Sets the dirty variable and, if argDirty is false,
	 * then will call {@link IDirtyable#setDirty(boolean)} on
	 * all {@link IDirtyable} objects in this array.
	 * @see com.dmurph.mvc.IDirtyable#setDirty(boolean)
	 */
	@Override
	public boolean setDirty( boolean argDirty) {
		boolean oldDirty = dirty;
		dirty = argDirty;
		if(!dirty){
			for(E e: this){
				if(e instanceof IDirtyable){
					((IDirtyable) e).setDirty(dirty);
				}
			}
		}
		firePropertyChange(DIRTY, oldDirty, dirty);
		return oldDirty;
	}

	/**
	 * Also calls {@link IRevertable#revertChanges()} on all
	 * objects in the reverted array that are {@link IRevertable}.
	 * @see com.dmurph.mvc.IRevertable#revertChanges()
	 */
	@Override
	public boolean revertChanges() {
		if(!isDirty()){
			return false;
		}
		setFromSaved();
		for(E e: this){
			if(e instanceof IRevertable){
				((IRevertable) e).revertChanges();
			}
		}
		return true;
	}

	/**
	 * Also calls {@link IRevertable#saveChanges()()} on all
	 * objects in the reverted array that are {@link IRevertable}.
	 * @see com.dmurph.mvc.IRevertable#saveChanges()
	 */
	@Override
	public boolean saveChanges() {
		if(!isDirty()){
			return false;
		}
		setToSaved();
		for(E e: this){
			if(e instanceof IRevertable){
				((IRevertable) e).saveChanges();
			}
		}
		return true;
	}
}
