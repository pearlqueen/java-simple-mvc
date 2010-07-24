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
package com.dmurph.mvc.util;

import java.util.HashSet;

import com.dmurph.mvc.ICloneable;
import com.dmurph.mvc.IDirtyable;

/**
 * A {@link HashSet} that implements {@link ICloneable} and {@link IDirtyable} for use with the models.
 * TODO implement like {@link MVCArrayList}, with revertible support and property events
 * @author Daniel Murphy
 */
public class MVCHashSet<E> extends HashSet<E> implements ICloneable, IDirtyable{
	private static final long serialVersionUID = 1L;
	
	private boolean dirty = false;
	/**
	 * @see java.util.HashSet#add(java.lang.Object)
	 */
	@Override
	public boolean add(E e) {
		dirty = true;
		return super.add(e);
	}

	/**
	 * @see java.util.HashSet#clear()
	 */
	@Override
	public void clear() {
		dirty = true;
		super.clear();
	}
	
	/**
	 * @see java.util.HashSet#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
		dirty = true;
		return super.remove(o);
	}
	
	/**
	 * Clones this object, if it can it will clone the objects in the set as well,
	 * as long as they implement {@link ICloneable}.  Otherwise it can't copy them, and does
	 * a shallow copy.
	 * @see com.dmurph.mvc.model.ICloneable#clone()
	 */
	@Override
	public ICloneable clone(){
		MVCHashSet<E> set = new MVCHashSet<E>();
		set.cloneFrom(this);
		return set;
	}

	/**
	 * Clones from the other object, if it can it will clone the objects in the set as well,
	 * as long as they implement {@link ICloneable}.  Otherwise it can't copy them, and does
	 * a shallow copy.
	 * @param argOther a {@link HashSet}
	 * @see com.dmurph.mvc.ICloneable#cloneFrom(com.dmurph.mvc.ICloneable)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void cloneFrom(ICloneable argOther) {
		// TODO Auto-generated method stub
		HashSet<E> other = (HashSet<E>) argOther;
		clear();
		
		for(E thing : other){
			if(thing instanceof ICloneable){
				add((E) ((ICloneable) thing).clone());
			}else{
				add(thing);
			}
		}
	}

	/**
	 * @see com.dmurph.mvc.IDirtyable#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Just sets the dirty variable
	 * @see com.dmurph.mvc.IDirtyable#setDirty(boolean)
	 */
	@Override
	public boolean setDirty( boolean argDirty) {
		boolean oldDirty = dirty;
		dirty = argDirty;
		return oldDirty;
	}
}
