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
 * Created at Jul 31, 2010, 1:49:27 AM
 */
package com.dmurph.mvc.tracking;

import com.dmurph.mvc.MVC;

/**
 * Have your event implement this so it gets tracked by google code.  To start google analytics tracking,
 * call {@link MVC#setTracker(com.dmurph.tracking.JGoogleAnalyticsTracker)} or implement {@link ICustomTracker}
 * along with this interface.<br/>
 * If {@link #getTrackingCategory()} or {@link #getTrackingAction()} returns null,
 * then the tracking will be ignored.  But the label and value can be null.
 * @author daniel
 */
public interface ITrackable {

	/**
	 * Gets the tracking category.  This will be the major label for the 
	 * event in Google Analytics.  This is required to not be null.
	 * @return
	 */
	public String getTrackingCategory();
	
	/**
	 * Get the tracking action.  This is the label for the event in 
	 * google analytics.  This is required not to be null.
	 * @return
	 */
	public String getTrackingAction();
	
	/**
	 * Get the label for the action.  These show up when you click on the 
	 * event action label.  This can be null.
	 * @return
	 */
	public String getTrackingLabel();
	
	/**
	 * Get the value for the action.  These show up when you click on the label
	 * for an event action.  This can be null.
	 * @return
	 */
	public Integer getTrackingValue();
}
