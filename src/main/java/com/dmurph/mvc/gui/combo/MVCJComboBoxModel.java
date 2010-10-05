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
 * Created at Sep 9, 2010, 4:35:08 PM
 */
package com.dmurph.mvc.gui.combo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.MutableComboBoxModel;


/**
 * Copied from {@link DefaultComboBoxModel}, with methods added for more control
 * over the model data.
 * @author Daniel Murphy
 *
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class MVCJComboBoxModel<E> extends AbstractListModel implements MutableComboBoxModel, Serializable{
	private static final long serialVersionUID = 1L;
	
	private final Vector<E> objects;
    private E selectedObject;

    /**
     * Constructs an empty DefaultComboBoxModel.
     */
    public MVCJComboBoxModel() {
        objects = new Vector();
    }

    /**
     * Constructs a DefaultComboBoxModel object initialized with
     * an array of objects.
     *
     * @param items  an array of Object objects
     */
	public MVCJComboBoxModel(final E items[]) {
        objects = new Vector();
        objects.ensureCapacity( items.length );

        int i,c;
        for ( i=0,c=items.length;i<c;i++ )
            objects.addElement(items[i]);

        if ( getSize() > 0 ) {
            selectedObject = getElementAt( 0 );
        }
    }

    /**
     * Constructs a DefaultComboBoxModel object initialized with
     * a vector.
     *
     * @param v  a Vector object ...
     */
    public MVCJComboBoxModel(Vector<E> v) {
        objects = v;

        if ( getSize() > 0 ) {
            selectedObject = getElementAt( 0 );
        }
    }

    // implements javax.swing.ComboBoxModel
    /**
     * Set the value of the selected item. The selected item may be null.
     * <p>
     * @param anObject The combo box value or null for no selection.
     */
    public void setSelectedItem(Object anObject) {
        if ((selectedObject != null && !selectedObject.equals( anObject )) ||
	    selectedObject == null && anObject != null) {
	    selectedObject = (E) anObject;
	    fireContentsChanged(this, -1, -1);
        }
    }

    // implements javax.swing.ComboBoxModel
    public E getSelectedItem() {
        return selectedObject;
    }

    // implements javax.swing.ListModel
    public int getSize() {
        return objects.size();
    }

    // implements javax.swing.ListModel
    public E getElementAt(int index) {
        if ( index >= 0 && index < objects.size() )
            return objects.elementAt(index);
        else
            return null;
    }

    /**
     * Returns the index-position of the specified object in the list.
     *
     * @param anObject  
     * @return an int representing the index position, where 0 is 
     *         the first position
     */
    public int getIndexOf(Object anObject) {
        return objects.indexOf(anObject);
    }

    // implements javax.swing.MutableComboBoxModel
    public void addElement(Object anObject) {
        objects.addElement((E)anObject);
        fireIntervalAdded(this,objects.size()-1, objects.size()-1);
        if ( objects.size() == 1 && selectedObject == null && anObject != null ) {
            setSelectedItem( anObject );
        }
    }
    
    public void addElements(int argIndex, Collection<E> argElements){
    	if(argElements == null){
    		return;
    	}
    	
    	objects.addAll(argIndex, argElements);
        fireIntervalAdded(this, argIndex, argIndex + argElements.size() - 1);  
        
        if ( objects.size() == 1 && selectedObject == null) {
        	setSelectedItem(objects.get(0));
        }
    }
    
    public void addElements(Collection<E> argElements){
    	if(argElements == null){
    		return;
    	}
    	
    	int oldSize = argElements.size();
    	objects.addAll(argElements);
        fireIntervalAdded(this,oldSize, objects.size()-1);  
        
        if ( objects.size() == 1 && selectedObject == null) {
        	setSelectedItem(objects.get(0));
        }
    }

    // implements javax.swing.MutableComboBoxModel
    public void insertElementAt(Object anObject,int index) {
        objects.insertElementAt((E)anObject,index);
        fireIntervalAdded(this, index, index);
    }
    
    public void setElementAt(E anObject, int index){
    	objects.setElementAt(anObject, index);
    	fireContentsChanged(this, index, index);
    }
    
    // implements javax.swing.MutableComboBoxModel
    public void removeElementAt(int index) {
        if ( getElementAt( index ) == selectedObject ) {
            if ( index == 0 ) {
                setSelectedItem( getSize() == 1 ? null : getElementAt( index + 1 ) );
            }
            else {
                setSelectedItem( getElementAt( index - 1 ) );
            }
        }

        objects.removeElementAt(index);

        fireIntervalRemoved(this, index, index);
    }
    
    public void sort(Comparator<E> argComparator){
    	Collections.sort(objects, argComparator);
    }

    // implements javax.swing.MutableComboBoxModel
    public void removeElement(Object anObject) {
        int index = objects.indexOf(anObject);
        if ( index != -1 ) {
            removeElementAt(index);
        }
    }

    /**
     * Empties the list.
     */
    public void removeAllElements() {
	    if ( objects.size() > 0 ) {
	            int firstIndex = 0;
	            int lastIndex = objects.size() - 1;
	            objects.removeAllElements();
		    selectedObject = null;
	            fireIntervalRemoved(this, firstIndex, lastIndex);
	        } else {
		    selectedObject = null;
		}
    }
}
