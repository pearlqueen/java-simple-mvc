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

import java.util.ArrayList;

import com.dmurph.mvc.ICloneable;
import com.dmurph.mvc.IDirtyable;

/**
 * {@link ICloneable} and {@link IDirtyable} Array List.  Will clone all values that
 * implement {@link ICloneable} in the {@link #clone()} and {@link #cloneFrom(ICloneable)} methods.
 * 
 * TODO reverting changes, saving, etc
 * @author Daniel Murphy
 */
public class MVCArrayList<E extends Object> extends ArrayList<E> implements ICloneable, IDirtyable {
	private static final long serialVersionUID = 4890270966369581329L;
	
	private boolean dirty = false;
	
	@Override
	public boolean add(E e) {
		dirty = true;
		return super.add(e);
	}
	
	@Override
	public void clear() {
		if(size() > 0){
			dirty = true;
		}
		super.clear();
	}
	
	@Override
	public E remove(int index) {
		dirty = true;
		return super.remove(index);
	}
	
	@Override
	public boolean remove(Object o) {
		dirty = true;
		return super.remove( o);
	}
	
	@Override
	public E set(int index, E element) {
		dirty = true;
		return super.set( index, element);
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
		ArrayList<E> other = (ArrayList<E>) argOther;
		clear();
		for(E e : other){
			if(e instanceof ICloneable){
				add((E) ((ICloneable) e).clone());
			}else{
				add(e);
			}
		}
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
		other.setDirty(false);
		return other;
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
