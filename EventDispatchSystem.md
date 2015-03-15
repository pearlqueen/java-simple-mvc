# Introduction #

The event dispatch system, which is run by the class `MVC`, is the core of the library.  Event listening is based on the event `key`, and all events are dispatched globally.  That means you have to be careful with your keys, as you don't want any duplicates.  While most implementations of this library will not be setting up event listening directly with the 'MVC' class (most events are meant for controllers, which automatically registers commands with events for you), you can still directly work with the dispatch thread to listen to event.  The system is single-threaded, but also has the ability to "split off" dispatching an event from the main dispatch thread (helpful if showing a modal dialog or if you need other MVC events dispatched during an operation that is running on the MVC thread).

This event dispatcher can easily be used independent of the mvc library if you just need a simple event dispatcher.

# Usage #
## Dispatching Events ##
The event dispatch system is the core of the library.  Events are dispatched through the extendable`MVCEvent` class, by calling `dispatch()`.  Ex:

```
MVCEvent event = new MVCEvent("mySpecialEventKey");
event.dispatch()
```
Since events are dispatched globally, it is a good idea to make the key unique to your event (to avoid any chance of duplication) and store the key in a static final variable so you can use it later when you want to listen for that event.
```
// defined usually in your controller or custom event
public static final String OK_BUTTON_PUSHED = "MAIN_WINDOW_OK_BUTTON_PUSHED";

// usually somewhere in your view
MVCEvent event = new MVCEvent(OK_BUTTON_PUSHED);
event.dispatch()
```

## Custom Events ##
Many events have to carry more information, and while there is a `ObjectEvent` that hold an object and a `StringEvent`, a lot of the time a custom event is necessary.  Fortunately, this is simple to do.  Here is an example:
```
// event to let the controller know the items selected
public class ItemsSelectedEvent extends MVCEvent {
	public final static String ITEMS_SELECTED = "ITEMS_SELECTED_EVENT";

	private final Object[] items;

	public ItemsSelectedEvent(Object[] argItems){
		super(ITEMS_SELECTED);
		items = argItems;
	}

	public Object[] getItems(){
		return items;
	}
}
```


## Listening for Events ##
To listen for events, you register your `IEventListener` with the `MVC` class like this:
```
IEventListener myEventListener = new MyEventListenerClass();
MVC.addEventListener(OK_BUTTON_PUSHED, myEventListener);
```
If there are multiple listeners for an event key, the listeners are called in the reverse order of how they are registered.  E.g., the last listener registered for an event key is called first.

## Monitoring Events ##
This library supports monitoring all dispatched events with and `IGlobalEventMonitor`.  Here is an example:
```
public class MyMonitor implements IGlobalEventMonitor {
	
	public void beforeDispatch(MVCEvent argEvent) {
		System.out.println("Event before dispatch: "+argEvent);
	}

	public void afterDispatch(MVCEvent argEvent) {
		System.out.println("Event after dispatch: "+argEvent);
	}
	
	public void noListeners(MVCEvent argEvent) {
		System.out.println("Event was not dispatched, no listeners: "+argEvent);
	}
	
}
```
And to set the monitor:
```
IGlobalEventMonitor monitor = new MyMonitor();
MVC.setGlobalEventMonitor(monitor);
```
The event dispatch system can only have one monitor.  See below for other ideas on how to handle events that are being monitored.

## Handling Events ##
JavaSimpleMVC gives you a couple of tools to make event handling as flexible as possible.

## Stop Listening ##
To stop listening for an event, just return false in the `eventReceived` method, like so
```
public boolean eventReceived(MVCEvent argEvent){
	return false;
}
```
This tells the dispatch thread to remove the current listener (you) from the listener list.

### Stop Propagation ###
You can stop event propagation (stop events from dispatching to the rest of the listeners) by calling 'stopPropagation()'.  Remember that the **last** listener registered will be the **first** to receive the event.
```
public boolean eventReceived(MVCEvent argEvent){
	if(argEvent.key.equals("mySpecialEventKey"){
		// don't let anyone else get this event!
		argEvent.stopPropagation();
	}
}
```
This call is handy for your `IGlobalEventMonitor`, as you can stop events before they are event dispatched.

### Split Off ###
For tasks that take a long time or dispatch other 'MVCEvent`s that need to be dispatched right away, you can "split off" your dispatch thread so other events can be dispatched.  This is done by making a new dispatch thread, giving it all of the pending events, and setting it as the main dispatch thread.  Then the old dispatch thread runs to completion.

Splitting off can only be done by the main dispatch thread.  E.g, it can't be done by a thread that was already "split off".

```
// uh oh, I'm about to do a whole lot of stuff, I better split off my thread so I don't stall the system
try {
	MVC.splitOff(); // so other mvc events can execute
} catch (IllegalThreadException e) {
	// this means that the thread that called splitOff() was not an MVC thread, and the next event's won't be blocked anyways.
	e.printStackTrace();
} catch (IncorrectThreadException e) {
	// this means that this MVC thread is not the main thread, it was already splitOff() previously
	e.printStackTrace();
}

// now these events will be dispatched now, before my process returns
MVCEvent event1 = new MVCEvent(COMPLICATED_STUFF_STARTING);
event1.dispatch();
MVCEvent event2 = new MVCEvent(ANOTHER_EVENT_KEY);
event2.dispatch();
```

---

For more information on what you can do with the event dispatch system, see the [javadocs](http://www.dmurph.com/javasimplemvc/index.html), as they are documented well and show all functionality.