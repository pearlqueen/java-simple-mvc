# Overview #
JavaSimpleMVC (which we'll call JMVC for short) is a compact but powerful mvc framework for java gui developers.  It is driven by a flexible event dispatch system that can split off dispatch threads when requested, has property change support for all models, revertible model support for easily reverting changes, and provides custom clonable java util classes for use with the models.  See the [changelog](Changelog.md) for a full feature list.  To learn more about the library take a look at the wiki or read more below.

If there are any features that aren't here or any problems that you see, please contact me, as I'd love to know how to improve this framework.

**UPDATE** - A new version was just released, 1.4.1, which fixes some concurrency issues with the 1.3.1 release.  Please update to this version.

# Event Dispatch System #
The event dispatch system (see EventDispatchSystem), which is run by the class `MVC`, is the core of the library. Event listening is based on the event key, and all events are dispatched globally. That means you have to be careful with your keys, as you don't want any duplicates. While most implementations of this library will not be setting up event listening directly with the `MVC` class (most events are meant for controllers, which send them to commands), you can still directly work with the dispatch thread to listen to event. The system is single-threaded, but also has the ability to "split off" dispatching an event from the main dispatch thread.

# Model Support #
There are four different types of models you can use depending on your need.
  * `AbstractModel` is the simplest, which provides methods for firing `PropertyChangeEvent`s.
  * `AbstractDirtyableModel` keeps track of a `dirty` property that is used to tell if a model has changed.
  * `AbstractRevertibleModel` keeps track of all property changes and will revert/save changes for you.
  * `HashModel` uses a `HashMap` to store properties internally, and thus can also provide all the functionality for `ICloneable`, `IDirtyable`, and `IRevertible` internally.  This is the model that takes the least code to implement, but the drawback is that all properties need to be casted when they are retrieved.

See ModelSupport for more information.

# Controller Support #
The controller support in JMVC (see ControllerSupport) is pretty straight forward. In your controller you register commands for message keys that you are listening for.

# Command Support #
A command just has to implement the `ICommand` interface.  Commands are supposed to receive all their information from the events, and perform their task independent of previous events (see CommandSupport).

If the controller registered a command class with an event key, a new command is created every time that event is dispatched, so there must be an empty constructor available in all commands.

For convenience, methods in the controller can also be used as commands.

# Google Analytics #
Version 1.3.0 adds support for google analytics tracking.  All you need to do is implement `ITrackable` and JMVC will automatically send tracking requests with my java google tracking library [JGoogleAnalyticsTracker](https://code.google.com/p/jgoogleanalyticstracker/).  See GoogleAnalyticsSupport.

# Debugging #
Version 1.3.2 adds an event visualizer to help debug your application.  It shows you the key, class, value, and thread of each event being dispatched, and also if the event was never received by a listener (if there were no listeners for that event key).  See EventMonitoring.

# Utility Classes #
In the `com.dmurph.mvc.model` package there is `MVCArrayList` and `MVCHashSet`, which extend their `java.util` originals and add full MVC support (Cloneable, Dirtyable, Revertable, and have property change support).

# Future Features #
  * ~~Create a gui event monitor so developers can easily see all events in their application for debugging.~~ done!
  * ~~Tie in Google Analytics for event monitoring - update: finished the [tracking](http://github.com/dmurph/JGoogleAnalyticsTracker) utility, incorporating into the project soon.~~ done!
  * see [the changelog](Changelog.md)




---

I maintain this project on my own time, so any donations are appreciated.<br>
Dwolla: <a href='https://www.dwolla.com/u/812-638-9391'>812-638-9391</a><br>
Bitcoin: 1Fd564w4SK5FKtCprVFCgkD2iimSCzUego<br>
Paypal:  Donate button is <a href='http://jbox2d.org'>here</a>