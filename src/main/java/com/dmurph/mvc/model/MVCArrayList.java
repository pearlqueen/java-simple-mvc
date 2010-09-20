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
package com.dmurph.mvc.model;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;

import com.dmurph.mvc.ICloneable;
import com.dmurph.mvc.IDirtyable;
import com.dmurph.mvc.IModel;
import com.dmurph.mvc.IRevertible;

/**
 * A full mvc implementation of an {@link ArrayList}.  Supports all operations in {@link ICloneable}, {@link IDirtyable},
 * and {@link IRevertible}.  Also fires property change events for the size of the array ({@link #SIZE}) and the dirty value
 * ({@link IModel#DIRTY} - which will also fire if any children do), and if an element in the array changed ({@link #CHANGED})
 * or added ({@link #ADDED}.<br/>
 * <br/>
 * This class also will forward all calls to it's members if implement the associated interface.  
 * For example, if {@link #revertChanges()} is called, then, after
 * reverting any changes to this model, it will call {@link IRevertible#revertChanges()} on any property
 * that is {@link IRevertible}.  This can get dangerous if your property tree goes in a loop (you'll 
 * get infinite calls).  In that case, or if you just don't want any calls to get forwarded to certain objects,
 *  you can you can override
 * {@link #cloneImpl(Object)}, {@link #revertChangesImpl(Object)}, {@link #isDirtyImpl(Object)},
 * {@link #setDirtyImpl(Object, boolean)}
 * or {@link #saveChangesImpl(Object)} to prevent this.<br/>
 * <br/>
 * All the operations are also synchronized, as most MVC implementations are multithreaded.
 * @author Daniel Murphy
 */
public class MVCArrayList<E> extends ArrayList<E> implements IModel, ICloneable, IDirtyable, IRevertible {
	private static final long serialVersionUID = 2L;
	
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
	 * is added or inserted into the array.  This fires an {@link IndexedPropertyChangeEvent}.
	 */
	public static final String ADDED = "ARRAY_LIST_ADDED";
	/**
	 * A value in the array was changed.  This fires an
	 * {@link IndexedPropertyChangeEvent}.
	 * @see #addPropertyChangeListener(PropertyChangeListener)
	 */
	public static final String CHANGED = "ARRAY_LIST_CHANGED";
	
	private boolean dirty = false;
	
	private final ArrayList<E> saved = new ArrayList<E>();
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
;
	
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
	 * @see java.util.ArrayList#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends E> argC) {
		int oldSize = size();
		boolean ret = super.addAll(argC);
		if(!ret){
			return false;
		}
		for(E e : argC){
			addListener(e);
			propertyChangeSupport.fireIndexedPropertyChange(ADDED, size()-1, null, e);
		}
		firePropertyChange(SIZE, oldSize, size());
		boolean old = dirty;
		dirty = true;
		firePropertyChange(DIRTY, old, dirty);
		return ret;
	}
    
	@Override
	public synchronized boolean add(E e) {
		boolean ret = super.add(e);
		addListener(e);
		propertyChangeSupport.fireIndexedPropertyChange(ADDED, size()-1, null, e);
		firePropertyChange(SIZE, size() - 1, size());
		boolean old = dirty;
		dirty = true;
		firePropertyChange(DIRTY, old, dirty);
		return ret;
	}
	
	public synchronized void add(int index, E element) {
		super.add(index, element);
		addListener(element);
		propertyChangeSupport.fireIndexedPropertyChange(ADDED, index, null, element);
		firePropertyChange(SIZE, size() - 1, size());
		boolean old = dirty;
		dirty = true;
		firePropertyChange(DIRTY, old, dirty);
	}
	
	private final ArrayList<E> temp = new ArrayList<E>();
	
	@Override
	public synchronized void clear() {
		if(size() > 0){
			int oldSize = size();
			temp.clear();
			temp.addAll(this);
			super.clear();
			for(int i=0; i<temp.size(); i++){
				E e = temp.get(i);
				removeListener(e);
				propertyChangeSupport.fireIndexedPropertyChange(REMOVED, i, e, null);
			}
			firePropertyChange(SIZE, oldSize, 0);
			boolean old = dirty;
			dirty = true;
			firePropertyChange(DIRTY, old, dirty);
		}
	}
	
	@Override
	public synchronized E remove(int index) {
		E ret = super.remove(index);
		removeListener(ret);
		propertyChangeSupport.fireIndexedPropertyChange(REMOVED, index, ret, null);
		firePropertyChange(SIZE, size() + 1, size());
		boolean old = dirty;
		dirty = true;
		firePropertyChange(DIRTY, old, dirty);
		return ret;
	}
	
	@Override
	public synchronized boolean remove(Object o) {
		boolean ret = super.remove(o);
		if(ret){
			removeListener(o);
			propertyChangeSupport.fireIndexedPropertyChange(REMOVED, -1, o, null);
			firePropertyChange(SIZE, size() + 1, size());
			boolean old = dirty;
			dirty = true;
			firePropertyChange(DIRTY, old, dirty);
		}
		return ret;
	}
	
	@Override
	public synchronized E set(int index, E element) {
		E ret = super.set(index, element);
		removeListener(ret);
		addListener(element);
		propertyChangeSupport.fireIndexedPropertyChange(CHANGED, index, ret, element);
		boolean old = dirty;
		dirty = true;
		firePropertyChange(DIRTY, old, dirty);
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
	 * Clones from another {@link ArrayList}, if the values are {@link ICloneable}, then
	 * they will be cloned to this one.  Otherwise it's a shallow copy (just sets the same values).
	 * @param argOther an {@link ArrayList}
	 * @see com.dmurph.mvc.ICloneable#cloneFrom(com.dmurph.mvc.ICloneable)
	 */
	@SuppressWarnings("unchecked")
	public synchronized void cloneFrom( ICloneable argOther) {
		MVCArrayList<E> other = (MVCArrayList<E>) argOther;
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
	 * @see java.util.ArrayList#addAll(int, java.util.Collection)
	 */
	@Override
	public boolean addAll(int argIndex, Collection<? extends E> argC) {
		int oldSize = size();
		boolean ret = super.addAll(argIndex, argC);
		if(!ret){
			return false;
		}
		for(E e : argC){
			addListener(e);
			propertyChangeSupport.fireIndexedPropertyChange(ADDED, size()-1, null, e);
		}
		firePropertyChange(SIZE, oldSize, size());
		boolean old = dirty;
		dirty = true;
		firePropertyChange(DIRTY, old, dirty);
		return ret;
	}
	/**
	 * Sets the dirty variable and, if argDirty is false,
	 * then will call {@link IDirtyable#setDirty(boolean)} on
	 * all {@link IDirtyable} objects in this array.
	 * @see com.dmurph.mvc.IDirtyable#setDirty(boolean)
	 */
	public synchronized void setDirty( boolean argDirty) {
		boolean oldDirty = dirty;
		dirty = argDirty;
		if(!dirty){
			for(E e: this){
				setDirtyImpl(e, false);
			}
		}
		firePropertyChange(DIRTY, oldDirty, dirty);
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
	public synchronized void revertChanges() {
		setFromSaved();
		for(E e: this){
			revertChangesImpl(e);
		}
		setDirty(false);
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
	 * Also calls {@link IRevertible#saveChanges()} on all
	 * objects in the reverted array that are {@link IRevertible}.
	 * @see com.dmurph.mvc.IRevertible#saveChanges()
	 */
	public synchronized void saveChanges() {
		setToSaved();
		for(E e: this){
			saveChangesImpl(e);
		}
		setDirty(false);
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
