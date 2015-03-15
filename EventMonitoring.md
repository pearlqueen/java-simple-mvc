# Introduction #

As of version 1.3.2, JMVC includes an `EventMonitor` which will show the key, class, value, and thread of each event being dispatched, and also if the event was never received by a listener (if there were no listeners for that event key).

# Usage #

First, if you don't care about any configuration and just want to show the monitor, you can just call this

```
MVC.showEventMonitor();
```

Which creates and shows an event monitor.  You can then hide it by calling `MVC.hideEventMonitor()`.

Creating the monitor yourself is also pretty simple.  Here is an example:

```
IGlobalEventMonitor currMonitor = MVC.getGlobalEventMonitor();
EventMonitor monitor = new EventMonitor(EventMonitorType.BEFORE_DISPATCH, currMonitor, 600);
// this means we log the events before they are dispatched, our log has a maximum of 600 entries
// before it starts removing from the top, and we send it the current global monitor so it can forward
// calls to it (decorator pattern, google it if you don't understand).
MVC.setGlobalEventMonitor(monitor);
monitor.setVisible(true);
```

And that's it.  Here's an image of what it looks like:

![http://www.dmurph.com/javasimplemvc/MonitorExample.png](http://www.dmurph.com/javasimplemvc/MonitorExample.png)