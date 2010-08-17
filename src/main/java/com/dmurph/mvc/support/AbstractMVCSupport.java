/**
 * Created on Aug 17, 2010, 12:44:33 PM
 */
package com.dmurph.mvc.support;

import com.dmurph.mvc.ICloneable;
import com.dmurph.mvc.IDirtyable;
import com.dmurph.mvc.IRevertible;
import com.dmurph.mvc.model.AbstractRevertibleModel;

/**
 * For for internal use, this includes methods that the user can override to control the mvc support of the class.
 * @author Daniel Murphy
 *
 */
public abstract class AbstractMVCSupport extends AbstractRevertibleModel implements ICloneable, IDirtyable, IRevertible {
	private static final long serialVersionUID = 1L;

	/**
	 * @see ICloneable#clone()
	 */
	@Override
	public abstract ICloneable clone();
	
	/**
	 * Default just calls {@link ICloneable#clone()} (if the object is
	 * {@link ICloneable}), but override to implement your own cloning and
	 * to protect against loops (if the property tree goes in a loop).
	 * @see #isDeepMVCEnabled(String)
	 */
	protected Object cloneImpl(String argProperty, Object o){
		if(o instanceof ICloneable && isDeepMVCEnabled(argProperty)){
			return ((ICloneable) o).clone();
		}else{
			return o;
		}
	}
	
	/** 
	 * Default just calls {@link IDirtyable#setDirty(boolean)}, but override
	 * to protect against loops (if the property tree goes in a loop).
	 * @see #isDeepMVCEnabled(String)
	 */
	protected void setDirtyImpl(String argProperty, IDirtyable argDirtyable){
		if(isDeepMVCEnabled(argProperty)){
			argDirtyable.setDirty(false);
		}
	}
	
	/**
	 * Default just calls {@link IDirtyable#isDirty()}, but override
	 * to protect against loops (if the property tree goes in a loop).
	 * @see #isDeepMVCEnabled(String)
	 */
	protected boolean isDirtyImpl(String argProperty, IDirtyable argDirtyable){
		if(isDeepMVCEnabled(argProperty)){
			return argDirtyable.isDirty();
		}else{
			return false;
		}
	}
	
	/**
	 * Override to disable deep MVC support for a property.  This prevents forwarding
	 * MVC calls (like {@link IDirtyable#setDirty(boolean)} or {@link IRevertible#revertChanges()})
	 * to stored properties.  The default is just to return true;
	 * @param argProperty name of the property in question
	 * @return if deeper
	 */
	protected boolean isDeepMVCEnabled(String argProperty){
		return true;
	}
	
	/**
	 * Default just calls {@link IRevertible#revertChanges()}, but override
	 * to protect against loops (if the property tree goes in a loop).
	 * @see #isDeepMVCEnabled(String)
	 */
	protected void revertChangesImpl(String argProperty, IRevertible argRevertible){
		if(isDeepMVCEnabled(argProperty)){
			argRevertible.revertChanges();
		}
	}
	
	/**
	 * Default just calls {@link IRevertible#saveChanges()}, but override
	 * to protect against loops (if the property tree goes in a loop).
	 * @see #isDeepMVCEnabled(String)
	 */
	protected void saveChangesImpl(String argProperty, IRevertible argRevertible){
		if(isDeepMVCEnabled(argProperty)){
			argRevertible.saveChanges();
		}
	}
	
}
