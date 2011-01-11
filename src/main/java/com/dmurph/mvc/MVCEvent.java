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
 * Created at 2:22:05 AM, Mar 12, 2010
 */
package com.dmurph.mvc;

import java.io.Serializable;
import java.util.Random;


/**
 * Simple event.  To dispatch call {@link #dispatch()}.
 * Note that events dispatch globally, so make sure that
 * the keys you choose are unique.
 * @author Daniel Murphy
 */
public class MVCEvent implements Serializable{
	private static final long serialVersionUID = 2L;
	private static final Random random = new Random();
	
	public final int id;
	public final String key;
	
	private volatile boolean propagate = true;
	
	public MVCEvent(final String argKey) {
		key = argKey;
		id = random.nextInt();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + id + "]" + "-"+ key;
	}
	
	/**
	 * Stops the event from propagating to the rest of the listeners.  Listeners are stored
	 * as a stack, so newer listeners receive events first.
	 */
	public void stopPropagation(){
		propagate = false;
	}
	
	protected boolean isPropagating(){
		return propagate;
	}
	
	/**
	 * Dispatches the event.  Events are dispatched globally, so make
	 * sure your key is unique!
	 */
	public void dispatch(){
		MVC.dispatchEvent( this);
	}
}
