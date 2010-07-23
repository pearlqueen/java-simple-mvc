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

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import com.dmurph.mvc.ICloneable;
import com.dmurph.mvc.IDirtyable;
import com.dmurph.mvc.IModel;
import com.dmurph.mvc.IRevertible;

/**
 * A full mvc implementation of an {@link ArrayList}.  Supports all operations in {@link ICloneable}, {@link IDirtyable},
 * and {@link IRevertible}.  Also fires property change events for the size of the array ({@link #SIZE}) and the dirty value
 * ({@link IModel#DIRTY}), and if an element in the array changed ({@link #ELEMENT}).<br/>
 * <br/>
 * This class also will forward all calls to it's
 * members if implement the associated interface.  For example, if {@link #revertChanges()} is called, then, after
 * reverting any changes to this model, it will call {@link IRevertible#revertChanges()} on any property
 * that is {@link IRevertible}.  This can get dangerous if your property tree goes in a loop (you'll 
 * get infinite calls).  In that case override {@link #cloneImpl(Object)}, {@link #revertChangesImpl(IRevertible)},
 * {@link #isDirtyImpl(IDirtyable)}, {@link #setDirtyImpl(IDirtyable, boolean)}, or {@link #saveChangesImpl(IRevertible)}
 * to prevent this.
 * 
 * @author Daniel Murphy
 */
public class MVCArrayList<E extends Object> extends ArrayList<E> implements IModel, ICloneable, IDirtyable, IRevertible {
	private static final long serialVersionUID = 4890270966369581329L;
	
	/**
	 * Array size property name for listening to property change events
	 * @see #addPropertyChangeListener(PropertyChangeListener)
	 */
	public static final String SIZE = "ARRAY_LIST_SIZE";
	
	/**
	 * Not exactly a property, but the name of the property when an element
	 * is removed from the array.  The {@link IndexedPropertyChangeEvent#getIndex()}
	 * cooresponds to the index that was removed, and is -1 if {@link #remove(Object)}
	 * is called (we don't know the index).
	 */
	public static final String REMOVED = "ARRAY_LIST_REMOVED";
	
	/**
	 * Not exactly a property, but the name of the property when an element
	 * is inserted into the array.  This fires an {@link IndexedPropertyChangeEvent}.
	 */
	public static final String INSERTED = "ARRAY_LIST_INSERT";
	/**
	 * A value in the array was changed.  This fires an
	 * {@link IndexedPropertyChangeEvent}.
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
	
	public void add(int index, E element) {
		super.add(index, element);
		propertyChangeSupport.fireIndexedPropertyChange(INSERTED, index, null, element);
		boolean old = dirty;
		dirty = true;
		firePropertyChange(DIRTY, old, dirty);
	}
	
	private final ArrayList<E> temp = new ArrayList<E>();
	
	@Override
	public void clear() {
		if(size() > 0){
			int oldSize = size();
			temp.clear();
			temp.addAll(this);
			super.clear();
			for(int i=0; i<temp.size(); i++){
				propertyChangeSupport.fireIndexedPropertyChange(REMOVED, i, temp.get(i), null);
			}
			firePropertyChange(SIZE, oldSize, 0);
			boolean old = dirty;
			dirty = true;
			firePropertyChange(DIRTY, old, dirty);
		}
	}
	
	@Override
	public E remove(int index) {
		E ret = super.remove(index);
		propertyChangeSupport.fireIndexedPropertyChange(REMOVED, index, ret, null);
		firePropertyChange(SIZE, size() - 1, size());
		boolean old = dirty;
		dirty = true;
		firePropertyChange(DIRTY, old, dirty);
		return ret;
	}
	
	@Override
	public boolean remove(Object o) {
		boolean ret = super.remove(o);
		if(ret){
			propertyChangeSupport.fireIndexedPropertyChange(REMOVED, -1, o, null);
			firePropertyChange(SIZE, size() - 1, size());
			boolean old = dirty;
			dirty = true;
			firePropertyChange(DIRTY, old, dirty);
		}
		return ret;
	}
	
	@Override
	public E set(int index, E element) {
		E ret = super.set(index, element);
		propertyChangeSupport.fireIndexedPropertyChange(ELEMENT, index, ret, element);
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
				if(isDirtyImpl((IDirtyable) e)){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Default just calls {@link IDirtyable#isDirty()}, but override
	 * to protect against loops (if the property tree goes in a loop).
	 * @param argRevertable
	 * @return
	 */
	public boolean isDirtyImpl(IDirtyable argDirtyable){
		return argDirtyable.isDirty();
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
					setDirtyImpl((IDirtyable) e, dirty);
				}
			}
		}
		firePropertyChange(DIRTY, oldDirty, dirty);
		return oldDirty;
	}
	
	/**
	 * Default just calls {@link IDirtyable#setDirty(boolean)}, but override
	 * to protect against loops (if the property tree goes in a loop).
	 * @param argRevertable
	 * @param argDirty
	 * @return
	 */
	public boolean setDirtyImpl(IDirtyable argDirtyable, boolean argDirty){
		return argDirtyable.setDirty(argDirty);
	}

	/**
	 * Also calls {@link IRevertible#revertChanges()} on all
	 * objects in the reverted array that are {@link IRevertible}.
	 * @see com.dmurph.mvc.IRevertible#revertChanges()
	 */
	@Override
	public boolean revertChanges() {
		if(!isDirty()){
			return false;
		}
		setFromSaved();
		for(E e: this){
			if(e instanceof IRevertible){
				revertChangesImpl((IRevertible) e);
			}
		}
		return true;
	}
	
	/**
	 * Default just calls {@link IRevertible#revertChanges()}, but override
	 * to protect against loops (if the property tree goes in a loop).
	 * @param argRevertable
	 * @return
	 */
	protected boolean revertChangesImpl(IRevertible argRevertible){
		return argRevertible.revertChanges();
	}

	/**
	 * Also calls {@link IRevertable#saveChanges()} on all
	 * objects in the reverted array that are {@link IRevertible}.
	 * @see com.dmurph.mvc.IRevertible#saveChanges()
	 */
	@Override
	public boolean saveChanges() {
		if(!isDirty()){
			return false;
		}
		setToSaved();
		for(E e: this){
			if(e instanceof IRevertible){
				saveChangesImpl((IRevertible) e);
			}
		}
		return true;
	}
	
	/**
	 * Default just calls {@link IRevertible#saveChanges()}, but override
	 * to protect against loops (if the property tree goes in a loop).
	 * @param argRevertible
	 * @return
	 */
	protected boolean saveChangesImpl(IRevertible argRevertible){
		return argRevertible.saveChanges();
	}
}
