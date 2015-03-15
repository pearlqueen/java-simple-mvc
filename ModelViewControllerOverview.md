# Introduction #
The goal of MVC (which stands for Model-View-Controller) is to separate the data of the application and the logic of the application into loosely coupled sections of the application.

# The Model #
The model is the core of the pattern.  It's content dictates everything that the user sees.  The model:
  * Stores all the data.
  * Lets any listener know when something has changed.
The model has no idea or concept of anything else in the program.  All it knows it the data it holds.  Models can get tricky when you have models within models (or even items like `ArrayList`s), so JavaSimpleMVC (we'll call it JMVC) will propagate calls like `clone()`, `revertChanges()`, and `isDirty()` down the model for you.  See ModelSupport for more info.

# The View #
The view is everything the user sees, and is always associated with one or more models.  It:
  * Displays it's information based on one or more models
  * Listens for changes in the model to update it's view components
  * Dispatches events for any user interaction
  * Does not modify the model
Java does a great job with providing various view elements to extend (javax.swing, etc), so JMVC doesn't need to give you any help displaying visual components.  However, JMVC provides an entire event dispatch system for sending out `MVCEvent`s to the controllers.

# The Controller #
The controller is where all the application logic goes.
  * The controller listens for events from the view
  * Performs any logic or actions based on those events
  * Does not modify the view, can only modify the model.
The key concept of the controller is that it can't modify the view (other than to initialize/display it), it can only work with the model.  Then the view, listening for changes in the model, will update accordingly.  In JMVC, this part is separated into the controller and it's commands.  See ControllerSupport and CommandSupport.

# Conclusion #
Using the MVC methodology gives us many advantages:
  * All the data is in one spot (the model), so we can easily save it, load it, etc.
  * All the application logic is in one spot (the controller), so it's easy to find and understand.
  * Maintaining the codebase is much easier, as the structure of the application follows a methodology, allowing new developers to easily dive into the code or find bugs as they know where things should be layed out.

There are some disadvantages though:
  * At first, using this architechture requires a lot more initial coding, but it usually reduces the amount of code after the structure is in place.
  * It's often tricky to stick to the methodology, but once you get the hang of it gets easier.