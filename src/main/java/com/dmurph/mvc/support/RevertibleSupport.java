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
 * Created at Jul 30, 2010, 9:27:20 PM
 */
package com.dmurph.mvc.support;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashMap;

import com.dmurph.mvc.IRevertible;

/**
 * @author daniel
 *
 */
public class RevertibleSupport implements IRevertible{
	
	private final HashMap<String, PropertyWrapper> propertyMap = new HashMap<String, PropertyWrapper>();
	private final ISupportable supportable;
	
	public RevertibleSupport(PropertyChangeSupport argPropertyChangeSupport, ISupportable argSupportable){
		supportable = argSupportable;
		argPropertyChangeSupport.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent argEvt) {
				if(propertyMap.containsKey(argEvt.getPropertyName())){
					PropertyWrapper wrapper = propertyMap.get(argEvt.getPropertyName());
					wrapper.dirtyObject = argEvt.getNewValue();
				}else{
					PropertyWrapper wrapper = new PropertyWrapper();
					wrapper.name = argEvt.getPropertyName();
					wrapper.cleanObject = argEvt.getOldValue();
					wrapper.dirtyObject = argEvt.getNewValue();
					propertyMap.put(argEvt.getPropertyName(), wrapper);
				}
			}
		});
	}
	
	public Collection<PropertyWrapper> getRecordedProperties(){
		return propertyMap.values();
	}
	
	/**
	 * @see com.dmurph.mvc.IRevertible#saveChanges()
	 */
	public boolean saveChanges() {
		boolean saved = false;
		for(String key : propertyMap.keySet()){
			PropertyWrapper wrapper = propertyMap.get(key);
			if(wrapper.isDirty()){
				saved = true;
				wrapper.cleanObject = wrapper.dirtyObject;
			}
		}
		return saved;
	}
	
	/**
	 * @see com.dmurph.mvc.IRevertible#revertChanges()
	 */
	public boolean revertChanges() {
		boolean reverted = false;
		for(String key : propertyMap.keySet()){
			PropertyWrapper wrapper = propertyMap.get(key);
			if(wrapper.isDirty()){
				reverted = true;
				wrapper.dirtyObject = wrapper.cleanObject;
				supportable.setProperty(wrapper.name, wrapper.cleanObject);
			}
		}
		return reverted;
	}
	
	public static class PropertyWrapper{
		String name = null;
		Object cleanObject = null;
		Object dirtyObject = null;
		
		public boolean isDirty(){
			if(cleanObject == dirtyObject){
				return false;
			}
			
			if(cleanObject == null){
				return true;
			}
			
			if(cleanObject.equals(dirtyObject)){
				return false;
			}
			
			return true;
		}
	}
}
