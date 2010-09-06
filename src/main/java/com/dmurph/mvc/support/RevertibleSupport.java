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
	
	private final HashMap<String, PropertyWrapper> revertibleProperties = new HashMap<String, PropertyWrapper>();
	private final ISupportable supportable;
	
	/**
	 * 
	 * @param argPropertyChangeSupport
	 * @param argSupportable
	 * @param argSource the source that the events have to originate from.  The changes are not recorded if the source doesn't match the event
	 * source.
	 */
	public RevertibleSupport(PropertyChangeSupport argPropertyChangeSupport, ISupportable argSupportable, final Object argSource){
		supportable = argSupportable;
		argPropertyChangeSupport.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent argEvt) {
				if(argEvt.getSource() != argSource){
					return; // don't record if the source isn't correct
				}
				if(revertibleProperties.containsKey(argEvt.getPropertyName())){
					PropertyWrapper wrapper = revertibleProperties.get(argEvt.getPropertyName());
					wrapper.dirtyObject = argEvt.getNewValue();
				}else{
					PropertyWrapper wrapper = new PropertyWrapper();
					wrapper.name = argEvt.getPropertyName();
					wrapper.cleanObject = argEvt.getOldValue();
					wrapper.dirtyObject = argEvt.getNewValue();
					revertibleProperties.put(argEvt.getPropertyName(), wrapper);
				}
			}
		});
	}
	
	public Collection<PropertyWrapper> getRecordedProperties(){
		return revertibleProperties.values();
	}
	
	/**
	 * @see com.dmurph.mvc.IRevertible#saveChanges()
	 */
	public void saveChanges() {
		for(String key : revertibleProperties.keySet()){
			PropertyWrapper wrapper = revertibleProperties.get(key);
			if(wrapper.isDirty()){
				wrapper.cleanObject = wrapper.dirtyObject;
			}
		}
	}
	
	/**
	 * @see com.dmurph.mvc.IRevertible#revertChanges()
	 */
	public void revertChanges() {
		for(String key : revertibleProperties.keySet()){
			PropertyWrapper wrapper = revertibleProperties.get(key);
			if(wrapper.isDirty()){
				wrapper.dirtyObject = wrapper.cleanObject;
				supportable.setProperty(wrapper.name, wrapper.cleanObject);
			}
		}
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((revertibleProperties == null) ? 0 : revertibleProperties.hashCode());
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RevertibleSupport other = (RevertibleSupport) obj;
		if (revertibleProperties == null) {
			if (other.revertibleProperties != null)
				return false;
		}
		else if (!revertibleProperties.equals(other.revertibleProperties))
			return false;
		
		return true;
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
