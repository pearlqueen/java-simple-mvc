/**
 * Created on Jul 13, 2010, 3:41:56 PM
 */
package com.dmurph.mvc.model;

import java.util.HashMap;

import com.dmurph.mvc.ICloneable;
import com.dmurph.mvc.IDirtyable;
import com.dmurph.mvc.IModel;
import com.dmurph.mvc.IRevertible;

/**
 * Model that stores all properties in a HashMap, so all {@link IDirtyable}, {@link ICloneable}, and
 * {@link IRevertible} functionality is handled internally.<br/>
 * <br/>
 * This class also will forward all calls to it's
 * members if implement the associated interface.  For example, if {@link #revertChanges()} is called, then, after
 * reverting any changes to this model, it will call {@link IRevertible#revertChanges()} on any property
 * that is {@link IRevertible}.  This can get dangerous if your property tree goes in a loop (you'll 
 * get infinite calls).  In that case override {@link #cloneImpl(Object)}, {@link #revertChangesImpl(IRevertible)},
 * {@link #isDirtyImpl(IDirtyable)}, or {@link #saveChangesImpl(IRevertible)} to prevent this.
 * 
 * @author Daniel Murphy
 *
 */
public class HashModel extends AbstractRevertibleModel implements IDirtyable, ICloneable, IRevertible{
	private static final long serialVersionUID = 2L;
	
	private final HashMap<String, ModelProperty> propertyMap = new HashMap<String, ModelProperty>();
	
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
	
	/**
	 * Constructs a hash model with an {@link IModel#DIRTY} property.
	 */
	public HashModel(){
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
	 * @see com.dmurph.mvc.model.AbstractRevertibleModel#setProperty(java.lang.String, java.lang.Object)
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
	public synchronized void cloneFrom(ICloneable argOther) {
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
				
				setProperty(key, cloneImpl(mp.prop));
			}
		}else{
			throw new RuntimeException("Not a HashModel");
		}
	}
	
	/**
	 * Default just calls {@link ICloneable#clone()} (if the object is
	 * {@link ICloneable}), but override to implement your own cloning and
	 * to protect against loops (if the property tree goes in a loop).
	 * @param argRevertable
	 * @return
	 */
	protected Object cloneImpl(Object o){
		if(o instanceof ICloneable){
			return ((ICloneable) o).clone();
		}else{
			return o;
		}
	}
	
	/**
	 * @see com.dmurph.mvc.model.AbstractRevertibleModel#isDirty()
	 */
	@Override
	public synchronized boolean isDirty() {
		boolean ret = super.isDirty();
		if(ret){
			return ret;
		}
		for(String key: propertyMap.keySet()){
			ModelProperty mp = propertyMap.get(key);
			if(mp.prop instanceof IDirtyable){
				ret = ret || isDirtyImpl((IDirtyable) mp.prop);
				if(ret){
					setProperty(DIRTY, ret);
					return ret;
				}
			}
		}
		setProperty(DIRTY, ret);
		return ret;
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
	 * @see com.dmurph.mvc.model.AbstractRevertibleModel#revertChanges()
	 */
	@Override
	public synchronized boolean revertChanges() {
		boolean ret = super.revertChanges();
		for(String key: propertyMap.keySet()){
			ModelProperty mp = propertyMap.get(key);
			if(mp.prop instanceof IRevertible){
				ret = ret || revertChangesImpl((IRevertible) mp.prop);
			}
		}
		setProperty(DIRTY, false);
		return ret;
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
	 * @see com.dmurph.mvc.model.AbstractRevertibleModel#saveChanges()
	 */
	@Override
	public synchronized boolean saveChanges() {
		boolean ret = super.saveChanges();
		setProperty(DIRTY, false);
		for(String key: propertyMap.keySet()){
			ModelProperty mp = propertyMap.get(key);
			if(mp.prop instanceof IRevertible){
				ret = ret || saveChangesImpl((IRevertible) mp.prop);
			}
		}
		return ret;
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
	
	private static class ModelProperty{
		PropertyType type;
		Object prop;
	}
}
