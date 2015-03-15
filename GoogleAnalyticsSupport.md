# Introduction #

This library uses another project of mine, [JGoogleAnalyticsTracker](http://github.com/dmurph/JGoogleAnalyticsTracker).  While !JGoogleAnalyticsTracker (we'll call it JGAT) is very straightforward and easy to use in it's own right, JavaSimpleMVC (JMVC) provides an easy way to tie your `MVCEvent`s into Google Analytics.

# Usage #

To enable JGAT, you would do something like this:

```
JGoogleAnalyticsTracker tracker = new JGoogleAnalyticsTracker(new AnalyticsConfigData(), GoogleAnalyticsVersion.V_4_7_2);
MVC.setTracker(tracker);
```

Then, all you have to do is have your event implement `ITrackable`, and then every time it is dispatched JMVC (through JGAT) will dispatch an event tracking request.  Unless configured otherwise through the JGAT tracker, all requests are asynchronous so it won't slow the dispatch thread down at all.

```
public class ShowColumnEvent extends StringEvent implements ITrackable{	

	public ShowColumnEvent(String argColumnName) {
		super(ColumnController.SHOW_COLUMN, argColumnName);
	}

	public String getTrackingCategory() {
		return "Main Window";
	}

	public String getTrackingAction() {
		return "Show Column";
	}

	public String getTrackingLabel() {
		return getValue();  // this will be the column name
	}

	public Integer getTrackingValue() {
		return null; // we don't have a value
	}
}
```

When you implements `ITrackable`, `getTrackingCategory()` and `getTrackingAction()` **cannot** be null.  If they are, then JMVC will not track this event, as Google requires both a category and an action (JGAT would throw an exception if you tried).