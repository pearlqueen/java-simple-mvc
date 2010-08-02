/**
 * Created at Aug 2, 2010, 2:45:23 PM
 */
package com.dmurph.mvc.tracking;

import com.dmurph.tracking.JGoogleAnalyticsTracker;

/**
 * If you want to use a custom tracker (say, if you want to have different profiles
 * within your application), implement this.
 * @author Daniel Murphy
 */
public interface ICustomTracker {
	public JGoogleAnalyticsTracker getCustomTracker();
}
