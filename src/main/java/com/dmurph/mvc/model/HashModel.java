/**
 * Created on Jul 13, 2010, 3:41:56 PM
 */
package com.dmurph.mvc.model;

import java.util.HashMap;

import com.dmurph.mvc.ICloneable;
import com.dmurph.mvc.IDirtyable;
import com.dmurph.mvc.IRevertable;

/**
 * Model that stores all properties in a HashMap, so all {@link IDirtyable}, {@link ICloneable}, and
 * {@link IRevertable} functionality is handled internally.
 * @author Daniel Murphy
 *
 */
public class HashModel extends AbstractRevertableModel implements IDirtyable, ICloneable, IRevertable{
	public static final String DIRTY = "HASH_MODEL_DIRTY";
	
	private final HashMap<String, ModelProperty> propertyMap = new HashMap<String, ModelProperty>();
	
	public enum PropertyType{
		READ_ONLY, READ_WRITE
	}
	
	public HashModel(){
		registerProperty(DIRTY, PropertyType.READ_WRITE, false);
	}
	
	/**
	 * 
	 * @param argKey
	 * @param argType
	 */
	protected void registerProperty(String argKey, PropertyType argType){
		registerProperty(argKey, argType, null);
	}
	
	/**
	 * @param argKey
	 * @param argType
	 * @param argInitial
	 */
	protected synchronized void registerProperty(String argKey, PropertyType argType, Object argInitial){
		ModelProperty mp = new ModelProperty();
		mp.type = argType;
		mp.prop = argInitial;
		propertyMap.put(argKey, mp);
	}
	
	/**
	 * @see com.dmurph.mvc.model.AbstractRevertableModel#setProperty(java.lang.String, java.lang.Object)
	 */
	public synchronized Object setProperty(String argKey, Object argProperty){
		if(propertyMap.containsKey(argKey)){
			ModelProperty mp = propertyMap.get(argKey);
			if(mp.type == PropertyType.READ_WRITE){
				if(mp.prop == argProperty){
					return argProperty;
				}
				Object old = mp.prop;
				mp.prop = argProperty;
				firePropertyChange(argKey, old, argProperty);
				if(!argProperty.equals(DIRTY)){
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
	 * 
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
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ICloneable clone(){
		HashModel model = new HashModel();
		model.cloneFrom(this);
		return model;
	}
	
	/**
	 * Clones from another HashModel, and makes sure to copy any values
	 * in the model that are {@link ICloneable}. It watches for references
	 *  to <code>argOther</code> and sets them to <code>this</code>.
	 * @see com.dmurph.mvc.ICloneable#cloneFrom(com.dmurph.mvc.ICloneable)
	 */
	@Override
	public void cloneFrom(ICloneable argOther) {
		if(argOther instanceof HashModel){
			propertyMap.clear();
			HashModel other = (HashModel) argOther;
						
			for(String key: other.propertyMap.keySet()){
				ModelProperty mp = other.propertyMap.get(key);
				registerProperty(key, mp.type);
				
				if(mp.prop == argOther){
					setProperty(key, this);
					continue;
				}
				
				setProperty(key, cloneObject(mp.prop));
			}
		}else{
			throw new RuntimeException("Not a HashModel");
		}
	}
	
	/**
	 * override to handle cloning your own way
	 * @param o
	 * @return
	 */
	protected Object cloneObject(Object o){
		if(o instanceof ICloneable){
			return ((ICloneable) o).clone();
		}else{
			return o;
		}
	}

	/**
	 * @see com.dmurph.mvc.model.AbstractRevertableModel#revertChanges()
	 */
	@Override
	public boolean revertChanges() {
		boolean ret = super.revertChanges();
		for(String key: propertyMap.keySet()){
			ModelProperty mp = propertyMap.get(key);
			if(mp.prop instanceof IRevertable){
				((IRevertable) mp.prop).revertChanges();
			}
		}
		setProperty(DIRTY, false);
		return ret;
	}
	
	/**
	 * @see com.dmurph.mvc.model.AbstractRevertableModel#saveChanges()
	 */
	@Override
	public boolean saveChanges() {
		boolean ret = super.saveChanges();
		setProperty(DIRTY, false);
		for(String key: propertyMap.keySet()){
			ModelProperty mp = propertyMap.get(key);
			if(mp.prop instanceof IRevertable){
				((IRevertable) mp.prop).saveChanges();
			}
		}
		return ret;
	}
	
	private class ModelProperty{
		PropertyType type;
		Object prop;
	}
}
