/**
 * Created at Jul 31, 2010, 1:49:27 AM
 */
package com.dmurph.mvc.tracking;
/**
 * Have your event implement this so it gets tracked by google code.  To start google code tracking,
 * initialize the tracker by doing something like this:</br>
 * <code>JGoogleAnalyticsTracker tracker = JGoogleAnalyticsTracker.getInstance();<br/>
 * tracker.initialize(new AnalyticsConfigData());</code><br/>
 * If {@link #getTrackingCategory()} or {@link #getTrackingAction()} returns null,
 * then the tracking will be ignored.
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
	 * for an event action.
	 * @return
	 */
	public String getTrackingValue();
}
