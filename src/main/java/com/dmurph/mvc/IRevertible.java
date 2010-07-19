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
 * Created at Jun 21, 2010, 3:57:50 AM
 */
package com.dmurph.mvc;

/**
 * An object that is revertable, where changes
 * can be "canceled".
 * @author Daniel Murphy
 */
public interface IRevertible {

	/**
	 * Saves the current changes to the model so
	 * further calls to {@link #revertChanges()}
	 * will revert changes to the model's current
	 * state.
	 * @return if there were changes to save
	 */
	public boolean saveChanges();
	
	/**
	 * Reverts the model to the last time {@link #saveChanges()}
	 * was called, or to it's original state if {@link #saveChanges()}
	 * was never called.
	 * @return if changed were reverted
	 */
	public boolean revertChanges();
}
