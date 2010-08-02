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
 * Created on May 31, 2010, 1:17:24 PM
 */
package com.dmurph.mvc.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import com.dmurph.mvc.ICloneable;
import com.dmurph.mvc.IDirtyable;
import com.dmurph.mvc.IModel;
import com.dmurph.mvc.IRevertible;

/**
 * A fully implemented {@link HashSet}, pretty much exactly like {@link MVCArrayList}.
 * @author Daniel Murphy
 */
public class MVCHashSet<E> extends HashSet<E> implements IModel, ICloneable, IDirtyable, IRevertible{
	private static final long serialVersionUID = 1L;
	
	/**
	 * Array size property name for listening to property change events
	 * @see #addPropertyChangeListener(PropertyChangeListener)
	 */
	public static final String SIZE = "HASH_SET_SIZE";
	
	/**
	 * Not exactly a property, but the name of the property when an element
	 * is removed from the set.
	 */
	public static final String REMOVED = "HASH_SET_REMOVED";
	
	/**
	 * Not exactly a property, but the name of the property when an element
	 * is added or inserted into the set.
	 */
	public static final String ADDED = "HASH_SET_ADDED";
	
	private boolean dirty = false;
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private final HashSet<E> saved = new HashSet<E>();
	
	private final PropertyChangeListener childPropertyChangeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent argEvt) {
			if(argEvt.getPropertyName().equals(IModel.DIRTY)){
				if(argEvt.getNewValue() == Boolean.TRUE){
					propertyChangeSupport.firePropertyChange(argEvt);
				}
			}
		}
	};
    
    private void addListener(Object argObject){
		if(argObject instanceof IModel){
			((IModel) argObject).addPropertyChangeListener(childPropertyChangeListener);
		}
	}
	
	private void removeListener(Object argObject){
		if(argObject instanceof IModel){
			((IModel) argObject).removePropertyChangeListener(childPropertyChangeListener);
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

    private void firePropertyChange(String argPropertyName, Object argOldValue, Object argNewValue) {
        propertyChangeSupport.firePropertyChange(argPropertyName, argOldValue, argNewValue);
    }

	@Override
	public synchronized boolean add(E e) {
		boolean ret = super.add(e);
		addListener(e);
		firePropertyChange(SIZE, size() - 1, size());
		propertyChangeSupport.fireIndexedPropertyChange(ADDED, size()-1, null, e);
		boolean old = dirty;
		dirty = true;
		firePropertyChange(DIRTY, old, dirty);
		return ret;
	}
	
	private final HashSet<E> temp = new HashSet<E>();
	
	@Override
	public synchronized void clear() {
		if(size() > 0){
			int oldSize = size();
			temp.clear();
			temp.addAll(this);
			super.clear();
			Iterator<E> it = temp.iterator();
			while(it.hasNext()){
				E e = it.next();
				removeListener(e);
				firePropertyChange(REMOVED, e, null);
			}
			firePropertyChange(SIZE, oldSize, 0);
			boolean old = dirty;
			dirty = true;
			firePropertyChange(DIRTY, old, dirty);
		}
	}
	
	@Override
	public synchronized boolean remove(Object o) {
		boolean ret = super.remove(o);
		if(ret){
			removeListener(o);
			firePropertyChange(REMOVED, o, null);
			firePropertyChange(SIZE, size() + 1, size());
			boolean old = dirty;
			dirty = true;
			firePropertyChange(DIRTY, old, dirty);
		}
		return ret;
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
	 * Clones from another {@link ArrayList}, if the values are {@link ICloneable}, then
	 * they will be cloned to this one.  Otherwise it's a shallow copy (just sets the same values).
	 * @param argOther an {@link ArrayList}
	 * @see com.dmurph.mvc.ICloneable#cloneFrom(com.dmurph.mvc.ICloneable)
	 */
	@SuppressWarnings("unchecked")
	public synchronized void cloneFrom( ICloneable argOther) {
		MVCHashSet<E> other = (MVCHashSet<E>) argOther;
		clear();
		for(E e : other){
			add(cloneImpl(e));
		}
		saved.clear();
		for(E e : other.saved){
			saved.add(cloneImpl(e));
		}
		this.dirty = other.dirty;
	}
	
	/**
	 * Default just calls {@link ICloneable#clone}, but override
	 * to protect against loops (if the property tree goes in a loop).
	 * @param argObject
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected E cloneImpl(E argObject){
		if(argObject instanceof ICloneable){
			return (E) ((ICloneable) argObject).clone();
		}else{
			return argObject;
		}
	}

	/**
	 * Clones this object to another {@link MVCArrayList}.  If the array values
	 * are also {@link ICloneable}, then they will be cloned as well.  If not, the values
	 * are just set (shallow copy).
	 * @see java.util.ArrayList#clone()
	 */
	@Override
	public synchronized ICloneable clone(){
		MVCArrayList<E> other = new MVCArrayList<E>();
		other.cloneFrom(this);
		return other;
	}

	/**
	 * Also checks to see if elements in this
	 * array are dirty, if any are {@link IDirtyable}.
	 * @see com.dmurph.mvc.IDirtyable#isDirty()
	 */
	public synchronized boolean isDirty() {
		if(dirty){
			return true;
		}
		for(E e : this){
			if(e instanceof IDirtyable){
				if(isDirtyImpl(e)){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Default just calls {@link IDirtyable#isDirty()}, but override
	 * to protect against loops (if the property tree goes in a loop).
	 * @return
	 */
	protected boolean isDirtyImpl(E argE){
		if(argE instanceof IDirtyable){
			if(((IDirtyable) argE).isDirty()){
				return true;
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
	public synchronized boolean setDirty( boolean argDirty) {
		boolean oldDirty = dirty;
		dirty = argDirty;
		if(!dirty){
			for(E e: this){
				setDirtyImpl(e, false);
			}
		}
		firePropertyChange(DIRTY, oldDirty, dirty);
		return oldDirty;
	}
	
	/**
	 * Default just calls {@link IDirtyable#setDirty(boolean)}, but override
	 * to protect against loops (if the property tree goes in a loop).
	 * @param argE
	 * @param argDirty
	 */
	protected void setDirtyImpl(E argE, boolean argDirty){
		if(argE instanceof IDirtyable){
			((IDirtyable) argE).setDirty(argDirty);
		}
	}

	/**
	 * Also calls {@link IRevertible#revertChanges()} on all
	 * objects in the reverted array that are {@link IRevertible}.
	 * @see com.dmurph.mvc.IRevertible#revertChanges()
	 */
	public synchronized boolean revertChanges() {
		if(!isDirty()){
			return false;
		}
		setFromSaved();
		for(E e: this){
			revertChangesImpl(e);
		}
		return true;
	}
	
	/**
	 * Default just calls {@link IRevertible#revertChanges()}, but override
	 * to protect against loops (if the property tree goes in a loop).
	 */
	protected void revertChangesImpl(E argE){
		if(argE instanceof IRevertible){
			((IRevertible) argE).revertChanges();
		}
	}

	/**
	 * Also calls {@link IRevertable#saveChanges()} on all
	 * objects in the reverted array that are {@link IRevertible}.
	 * @see com.dmurph.mvc.IRevertible#saveChanges()
	 */
	public synchronized boolean saveChanges() {
		if(!isDirty()){
			return false;
		}
		setToSaved();
		for(E e: this){
			saveChangesImpl(e);
		}
		return true;
	}
	
	/**
	 * Default just calls {@link IRevertible#saveChanges()}, but override
	 * to protect against loops (if the property tree goes in a loop).
	 */
	protected void saveChangesImpl(E argE){
		if(argE instanceof IRevertible){
			((IRevertible) argE).saveChanges();
		}
	}
}
