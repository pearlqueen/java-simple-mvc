/**
 * Created on Jul 13, 2010, 3:41:56 PM
 */
package com.dmurph.mvc.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Iterator;

import com.dmurph.mvc.ICloneable;
import com.dmurph.mvc.IDirtyable;
import com.dmurph.mvc.IModel;
import com.dmurph.mvc.IRevertible;
import com.dmurph.mvc.support.AbstractMVCSupport;
import com.dmurph.mvc.support.ISupportable;
import com.dmurph.mvc.support.RevertibleSupport;
import com.dmurph.mvc.support.RevertibleSupport.PropertyWrapper;

/**
 * Model that stores all properties in a HashMap, so all {@link IDirtyable}, {@link ICloneable}, and
 * {@link IRevertible} functionality is handled internally.<br/>
 * <br/>
 * This class also will forward all calls to it's members if implement the associated interface.  
 * For example, if {@link #revertChanges()} is called, then, after
 * reverting any changes to this model, it will call {@link IRevertible#revertChanges()} on any property
 * that is {@link IRevertible}.  This can get dangerous if your property tree goes in a loop (you'll 
 * get infinite calls).  In that case you can override {@link #isDeepMVCEnabled(String)) to return false for
 * properties that you don't want any calls forwarded to, or if you want more control, you can override
 * {@link #cloneImpl(Object)}, {@link #revertChangesImpl(IRevertible)}, {@link #isDirtyImpl(IDirtyable)},
 * or {@link #saveChangesImpl(IRevertible)} to prevent this as well.
 * 
 * @author Daniel Murphy
 *
 */
public class HashModel extends AbstractMVCSupport implements IDirtyable, ICloneable, IRevertible, IModel{
	private static final long serialVersionUID = 2L;
	
	private final HashMap<String, ModelProperty> propertyMap = new HashMap<String, ModelProperty>();
	private final RevertibleSupport revertibleSupport;
	
	public enum PropertyType{
		/**
		 * Property that can't be set by
		 * calling {@link HashModel#setProperty(String, Object)},
		 * but can be set by the extending class by calling
		 * {@link HashModel#registerProperty(String, PropertyType, Object)}
		 * again.
		 */
		READ_ONLY,
		/**
		 * Property that can be read and written to by the 
		 * {@link HashModel#getProperty(String)} and 
		 * {@link HashModel#setProperty(String, Object)}
		 * methods.
		 */
		READ_WRITE,
		/**
		 * Property that, after registration, cannot be set again
		 * by either accessing classes or the implementing class.
		 * This guarantees that the object returned from 
		 * {@link HashModel#getProperty(String)} will always be
		 * the correct reference.
		 */
		FINAL
	}
	
	// for listening to dirty updates from children
	private final PropertyChangeListener childPropertyChangeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent argEvt) {
			if(argEvt.getPropertyName().equals(IModel.DIRTY)){
				if(argEvt.getNewValue() == Boolean.TRUE){
					propertyChangeSupport.firePropertyChange(argEvt);
				}
			}
		}
	};
	
	/**
	 * Constructs a hash model with an {@link IModel#DIRTY} property.
	 */
	public HashModel(){
		revertibleSupport = new RevertibleSupport(propertyChangeSupport, new ISupportable() {
			public void setProperty(String argPropertyName, Object argProperty) {
				HashModel.this.setProperty(argPropertyName, argProperty);
			}
		}, this);
		
		registerProperty(DIRTY, PropertyType.READ_WRITE, false);
	}
	
	/**
	 * Constructs a hash model with an {@link IModel#DIRTY} property, and
	 * the given properties all with property type of {@link PropertyType#READ_WRITE}.
	 * @param argProperties
	 */
	public HashModel(String[] argProperties){
		this();
		registerProperty(argProperties, PropertyType.READ_WRITE);
	}
	
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
	 * Register a property
	 * @param argKey
	 * @param argType
	 */
	protected void registerProperty(String argKey, PropertyType argType){
		registerProperty(argKey, argType, null);
	}
	
	/**
	 * Register a property with an initial value
	 * @param argKey
	 * @param argType
	 * @param argInitial
	 */
	protected synchronized void registerProperty(String argKey, PropertyType argType, Object argInitial){
		ModelProperty mp;
		if(propertyMap.containsKey(argKey)){
			mp = propertyMap.get(argKey);
			if(mp.type == PropertyType.FINAL){
				return;
			}
		}else{
			mp = new ModelProperty();
		}
		mp.type = argType;
		mp.prop = argInitial;
		addListener(mp.prop);
		propertyMap.put(argKey, mp);
	}
	
	/**
	 * Register an array of properties all of the same property type
	 * @param argKeys
	 * @param argType
	 */
	protected synchronized void registerProperty(String[] argKeys, PropertyType argType){
		for(String s: argKeys){
			registerProperty(s, argType, null);
		}
	}
	
	/**
	 * Sets a property, and will only set the property if it's {@link PropertyType} is
	 * {@link PropertyType#READ_WRITE}.  If the property isn't defined, it will be registered
	 * and set with the property type of {@link PropertyType#READ_WRITE}.
	 * @see #getPropertyType(String)
	 * @see com.dmurph.mvc.support.AbstractMVCSupport#setProperty(java.lang.String, java.lang.Object)
	 */
	public synchronized Object setProperty(String argKey, Object argProperty){
		if(propertyMap.containsKey(argKey)){
			ModelProperty mp = propertyMap.get(argKey);
			if(mp.type == PropertyType.READ_WRITE){
				if(mp.prop == argProperty){
					return argProperty;
				}
				Object old = mp.prop;
				removeListener(old);
				mp.prop = argProperty;
				addListener(mp.prop);
				firePropertyChange(argKey, old, argProperty);
				if(!argKey.equals(DIRTY)){
					setProperty(DIRTY, true);
				}
				return old;
			}else{
				return mp.prop;
			}
		}else{
			registerProperty(argKey, PropertyType.READ_WRITE, null);
			return setProperty(argKey, argProperty);
		}
	}
		
	/**
	 * Get a property
	 * @param argKey
	 * @return
	 */
	public synchronized Object getProperty(String argKey){
		ModelProperty mp = propertyMap.get(argKey);
		if(mp != null){
			return mp.prop;
		}else{
			return null;
		}
	}
	
	/**
	 * Get the {@link PropertyType} for a property.
	 * @param argKey
	 * @return
	 */
	public synchronized PropertyType getPropertyType(String argKey){
		ModelProperty mp = propertyMap.get(argKey);
		if(mp != null){
			return mp.type;
		}else{
			return null;
		}
	}
	
	/**
	 * Gets the names of all the properties.
	 * @return
	 */
	public synchronized String[] getPropertyNames(){
		return propertyMap.keySet().toArray(new String[0]);
	}
	
	/**
	 * Tells you if this hash model contains the given property.
	 * @param argProperty
	 * @return
	 */
	public synchronized boolean containsProperty(String argProperty){
		return propertyMap.containsKey(argProperty);
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ICloneable clone(){
		HashModel model = new HashModel();
		model.cloneFrom(this);
		return model;
	}
	
	// clears properties of the model, making sure the remove listeners
	// from any properties that are IModels
	private void cleanClear(){
		Iterator<ModelProperty> it = propertyMap.values().iterator();
		while(it.hasNext()){
			removeListener(it.next().prop);
			it.remove();
		}
	}
	
	/**
	 * Clones from another HashModel, and makes sure to copy any values
	 * in the model that are {@link ICloneable}. It watches for references
	 *  to <code>argOther</code> and sets them to <code>this</code>.
	 * @see com.dmurph.mvc.ICloneable#cloneFrom(com.dmurph.mvc.ICloneable)
	 */
	public synchronized void cloneFrom(ICloneable argOther) {
		if(argOther instanceof HashModel){
			cleanClear();
			HashModel other = (HashModel) argOther;
						
			for(String key: other.propertyMap.keySet()){
				ModelProperty mp = other.propertyMap.get(key);
				registerProperty(key, mp.type);
				
				// references itself
				if(mp.prop == argOther){
					setProperty(key, this);
					continue;
				}
				
				setProperty(key, cloneImpl(key, mp.prop));
			}
		}else{
			throw new RuntimeException("Not a HashModel");
		}
	}
	
	/**
	 * If false, this is the equivalent of {@link #saveChanges()}
	 * @see IDirtyable#setDirty(boolean)
	 */
	public synchronized void setDirty(boolean argDirty) {
		setProperty(DIRTY, argDirty);
		if(argDirty == false){
			for(String key: propertyMap.keySet()){
				ModelProperty mp = propertyMap.get(key);
				if(mp.prop instanceof IDirtyable){
					setDirtyImpl(key, (IDirtyable) mp.prop);
				}
			}
			saveChanges();
		}
	}
	
	/**
	 * @see com.dmurph.mvc.IDirtyable#isDirty()
	 */
	public synchronized boolean isDirty() {
		if((Boolean)getProperty(DIRTY)){
			return true;
		}
		for(PropertyWrapper prop : revertibleSupport.getRecordedProperties()){
			if(prop.isDirty()){
				return true;
			}
		}
		boolean ret = false;
		for(String key: propertyMap.keySet()){
			ModelProperty mp = propertyMap.get(key);
			if(mp.prop instanceof IDirtyable){
				ret = ret || isDirtyImpl(key, (IDirtyable) mp.prop);
				if(ret){
					return ret;
				}
			}
		}
		return ret;
	}

	/**
	 * @see com.dmurph.mvc.IRevertible#revertChanges()
	 */
	public synchronized void revertChanges() {
		revertibleSupport.revertChanges();
		for(String key: propertyMap.keySet()){
			ModelProperty mp = propertyMap.get(key);
			if(mp.prop instanceof IRevertible){
				revertChangesImpl(key, (IRevertible) mp.prop);
			}
		}
		setProperty(DIRTY, false);
	}
	
	/**
	 * @see com.dmurph.mvc.IRevertible#saveChanges()
	 */
	public synchronized void saveChanges() {
		setProperty(DIRTY, false);
		revertibleSupport.saveChanges();
		for(String key: propertyMap.keySet()){
			ModelProperty mp = propertyMap.get(key);
			if(mp.prop instanceof IRevertible){
				saveChangesImpl(key, (IRevertible) mp.prop);
			}
		}
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("HashModel[");
		for(String s: propertyMap.keySet()){
			sb.append(s);
			sb.append("=");
			sb.append(propertyMap.get(s).prop);
			sb.append(", ");
		}
		sb.delete(sb.length()-2, sb.length());
		sb.append("]");
		return sb.toString();
	}
	
	public void printModel(){
		System.out.println(toString());
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
		HashModel other = (HashModel) obj;
		if (propertyMap == null) {
			if (other.propertyMap != null)
				return false;
		}
		else if (!propertyMap.equals(other.propertyMap))
			return false;
		return true;
	}


	private static class ModelProperty{
		PropertyType type;
		Object prop;

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
			ModelProperty other = (ModelProperty) obj;
			if (prop == null) {
				if (other.prop != null)
					return false;
			}
			else if (!prop.equals(other.prop))
				return false;
			if (type != other.type)
				return false;
			return true;
		}
	}
}
