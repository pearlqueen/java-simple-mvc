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
 * Created on May 31, 2010, 2:15:25 PM
 */
package com.dmurph.mvc.util;

import java.util.HashMap;

import com.dmurph.mvc.ICloneable;
import com.dmurph.mvc.IDirtyable;

/**
 * TODO implement revert, save, etc
 * @author Daniel Murphy
 */
public class MVCHashMap<K, V> extends HashMap<K, V> implements ICloneable, IDirtyable{
	private static final long serialVersionUID = 3144943839261154818L;
	
	private boolean dirty = false;
	
	/**
	 * @see java.util.HashMap#clear()
	 */
	@Override
	public void clear() {
		dirty = true;
		super.clear();
	}

	/**
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public V put(K key, V value) {
		dirty = true;
		return super.put(key, value);
	}

	/**
	 * @see java.util.HashMap#remove(java.lang.Object)
	 */
	@Override
	public V remove(Object key) {
		dirty = true;
		return super.remove(key);
	}

	/**
	 * Does a shallow copy unless the values are {@link ICloneable}.
	 * @param argOther 
	 * @see com.dmurph.mvc.ICloneable#cloneFrom(com.dmurph.mvc.ICloneable)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void cloneFrom(ICloneable argOther) {
		HashMap<K, V> map = (HashMap<K, V>) argOther;
		clear();
		for(K key : map.keySet()){
			V value = map.get(key);
			if(value instanceof ICloneable){
				put(key, (V) ((ICloneable) value).clone());
			}else{
				put(key, value);
			}
		}
	}
	
	/**
	 * does a shallow copy unless the values are {@link ICloneable}.
	 * @see ICloneable#clone()
	 */
	public ICloneable clone(){
		MVCHashMap<K, V> map = new MVCHashMap<K, V>();
		map.cloneFrom(this);
		return map;
	}

	/**
	 * Just sets dirty to false, doesn't revert changes
	 * @see IDirtyable#revert()
	 */
	@Override
	public boolean revert() {
		boolean oldDirty = dirty;
		dirty = false;
		return oldDirty;
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
