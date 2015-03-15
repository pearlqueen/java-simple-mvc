# Introduction #
Many people are confused on how to actually implement the MVC architecture.  This guide will show you an example of implementing JavaSimpleMVC (which we'll just call JMVC).  See ModelViewControllerOverview for a brief overview of the MVC methodology if you're feeling rusty.

Keep in mind, in a normal application, these tasks are way too simple for using MVC.  But it's a good example of how to use it.

# Concept #
First we decide what our application is going to do.  Our example application (titled Foo) will:
  * display the text "MVC is GREAT!"
  * have some buttons to change the color of the text
  * have a button that brings up dialog to change the text
Lets get crackin!

# Implementation #

## Model Locator ##
The first thing we do is create our **ModelLocator**.  The model locator is a singleton (only one instance), and is meant to be the center of the application, where we store anything that is universally available.  This component usually contains:
  * A central application model (hence "ModelLocator") that might have application preferences or user data.
  * A reference to the main application window
  * Methods to retrieve localization strings
  * Anything else that needs to be universally available
And always contains all instantiated controllers (they have to be created somewhere!)

We start with a very basic FooModelLocator:
```
package foo.model;

public class FooModelLocator {
	private static FooModelLocator instance = null;

	private FooModelLocator(){

	}

	public static FooModelLocator getInstance(){
		if(instance == null){
			instance = new FooModelLocator();
		}
		return instance;
	}
}
```
As you can see, this is literally just an empty singleton.  But we'll expand it later.

## Model Creation ##
Next, we create our models.  In this example, we'll need two models, but for now lets just focus on our main window, where we need one model to store:
  * The text to show
  * The color of the text

JMVC provides different types of models based on what you need.  The simplest is `AbstractModel`, which just provides property change support, but others support further operations like dirtyable (knowing if an object has changed) and revertible (reverting changes).  For more information on the provided models see `ModelSupport`.

Our model doesn't need to know if it's dirty, and certainly doesn't need to revert or save changes, so we'll just use the `AbstractModel` class:
```
package foo.model.main;

import java.awt.Color;

import com.dmurph.mvc.model.AbstractModel;

public class MainModel extends AbstractModel {
	private String text = "MVC is GREAT!";
	private Color color = Color.black;
	
	public void setText(String argText) {
		String old = text;
		text = argText;
		firePropertyChange("text", old, text);
	}
	public void setColor(Color argColor) {
		Color old = color;
		color = argColor;
		firePropertyChange("color", old, color);
	}
	
	public Color getColor() {
		return color;
	}
	public String getText() {
		return text;
	}
}
```
You can see how the object, being a model, is entirely contained.  It doesn't know about the application at all, it just holds it's data and lets people know when it changes.  In this model when we set any of the properties we fire a property change event with the old and new value.  This event will only dispatch if the old value doesn't == or `.equal(Object)` the new value.  Further change event dispatching operations can be accessed from the `propertyChangeSupport` field.

This is the only model that we need.

## Main View ##
Now we can create our main view.  The view is the gui elements that the user sees, and will:
  * display itself based on the data from the model (and listen for changes to the model so it can update itself)
  * dispatch events for any user action

Here is our main view:
```
package foo.view.main;

import java.awt.*;
import java.beans.*;
import javax.swing.*;
import foo.model.main.MainModel;

public class MainWindow extends JFrame{
	
	private JLabel text;
	private JButton redButton;
	private JButton greenButton;
	private JButton blueButton;
	private JButton editTextButton;
	
	private MainModel model;
	
	public MainWindow(MainModel argModel){
		model = argModel;
		initComponents();
		linkModel();
		addListeners();
		populateLocale();
	}
	
	private void initComponents(){
		text = new JLabel("", JLabel.CENTER);
		redButton = new JButton();
		greenButton = new JButton();
		blueButton = new JButton();
		editTextButton = new JButton();
		
		setLayout(new GridLayout(2, 1));
		
		add(text);
		
		Box box = Box.createHorizontalBox();
		box.add(redButton);
		box.add(greenButton);
		box.add(blueButton);
		box.add(Box.createRigidArea(new Dimension(30, 1	)));
		box.add(Box.createHorizontalGlue());
		box.add(editTextButton);
		
		add(box);
	}
	
	private void linkModel(){
		text.setText(model.getText());
		text.setForeground(model.getColor());
		
		model.addPropertyChangeListener(new PropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent evt) {
				String name = evt.getPropertyName();
				
				if(name.equals("text")){
					String value = (String) evt.getNewValue();
					text.setText(value);
				}
				else if(name.equals("color")){
					Color value = (Color) evt.getNewValue();
					text.setForeground(value);
				}
			}
		});
	}
	
	private void addListeners(){
		redButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Dispatch event
			}
		});
		
		greenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Dispatch event
			}
		});
		
		blueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Dispatch event
			}
		});
		
		editTextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// TODO Dispatch event
			}
		});
	}
	
	private void populateLocale(){
		// this is where you would normally get the
		// locale strings from your model locator, but
		// that's out of the scope of this example
		redButton.setText("Red");
		greenButton.setText("Green");
		blueButton.setText("Blue");
		
		editTextButton.setText("Edit Text");
	}
}
```
This object will set itself from the model it's given and then change's it's text and text color whenever the corresponding properties in the model changes.  However, the object is incomplete.  We don't dispatch any events from the button clicks!  Let's make the other half of the application first.

## Controller/Events/Commands ##
Now we make the other half of the application.

### Controller ###
Let's start by making our controller with the event keys:
```
package foo.control.main;

import com.dmurph.mvc.control.FrontController;

public class MainController extends FrontController {
	// event keys.  The all capital letters is just my preference for the actual keys
	public static final String COLOR_CHANGE = "MAIN_COLOR_CHANGE";
	public static final String DISPLAY_TEXT_CHANGE = "MAIN_DISPLAY_TEXT_CHANGE";
}
```
Notice that the keys have a prefix of the controller name.  This is because all `MVCEvent`s are dispatched globally, so you wouldn't want to get event keys mixed up between controllers (in case another controller had a color change event).

### Events ###
We have two type events needed here, a `ColorChangeEvent` and a `DisplayTextChangeEvent`.  The root class for all events is the `MVCEvent` class, which stores the event key.  JMVC provides a couple extensions of the class for convenience (`ObjectEvent<E>` and `StringEvent`) so you don't have to code as much, but we'll make custom ones so you can see how to do that (it's very simple).

Our ColorChangeEvent:
```
package foo.control.main;

import java.awt.Color;
import com.dmurph.mvc.MVCEvent;

public class ColorChangeEvent extends MVCEvent {

	public final Color color;
	public final MainModel model;

	public ColorChangeEvent(Color argColor, MainModel argModel) {
		super(MainController.COLOR_CHANGE);
		color = argColor;
	}
}
```

Our DisplayTextChangeEvent:
```
package foo.control.main;

import com.dmurph.mvc.MVCEvent;

public class DisplayTextChangeEvent extends MVCEvent {

	public DisplayTextChangeEvent() {
		super(MainController.DISPLAY_TEXT_CHANGE);
	}
}
```
One stores some properties, while the other is just an event.  The events seem pretty simple themselves, but the dispatch system that JMVC provides is very powerful,  take a look at the EventDispatchSystem page for more info.

### Commands ###
Now lets make our commands that we're going to associate with our events

Here's our ColorChangeModel:
```
package foo.command.main;

import com.dmurph.mvc.MVCEvent;
import com.dmurph.mvc.control.ICommand;
import foo.control.main.ColorChangeEvent;

public class ChangeColorCommand implements ICommand {

	public void execute(MVCEvent argEvent) {
		ColorChangeEvent event = (ColorChangeEvent) argEvent;
		event.model.setColor(event.color);
	}
}
```

and our DisplayTextChangeCommand:
```
package foo.command.main;

import com.dmurph.mvc.MVCEvent;
import com.dmurph.mvc.control.ICommand;

public class DisplayTextChangeCommand implements ICommand {

	@Override
	public void execute(MVCEvent argEvent) {
		// TODO implement this
	}
}
```
We haven't created our text editor for the label, so we're not going to worry about the `DisplayTextChangeCommand` just yet.

## Wire it up! ##
Now let's wire it up!

First we connect the events to the commands in our controller:
```
package foo.control.main;

import com.dmurph.mvc.control.FrontController;
import foo.command.main.ChangeColorCommand;
import foo.command.main.DisplayTextChangeCommand;

public class MainController extends FrontController {
	// event keys.  The all capital letters is just my preference for the actual keys
	public static final String COLOR_CHANGE = "MAIN_COLOR_CHANGE";
	public static final String DISPLAY_TEXT_CHANGE = "MAIN_DISPLAY_TEXT_CHANGE";
	
	public MainController(){
		registerCommand(COLOR_CHANGE, ChangeColorCommand.class);
		registerCommand(DISPLAY_TEXT_CHANGE, DisplayTextChangeCommand.class);
	}
}
```

Next let's put our events in our view:
```
	// we're only updating this method
	private void addListeners(){
		redButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ColorChangeEvent event = new ColorChangeEvent(Color.red, model);
				event.dispatch();
			}
		});
		
		greenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ColorChangeEvent event = new ColorChangeEvent(Color.green, model);
				event.dispatch();
			}
		});
		
		blueButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ColorChangeEvent event = new ColorChangeEvent(Color.blue, model);
				event.dispatch();
			}
		});
		
		editTextButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DisplayTextChangeEvent event = new DisplayTextChangeEvent();
				event.dispatch();
			}
		});
	}
```

## Launch! ##

Finally lets create our application entry point and create our window.
```
package foo;

import javax.swing.JFrame;
import foo.model.main.MainModel;
import foo.view.main.MainWindow;

public class App {
	
	public static void main(String[] args) {
		MainModel model = new MainModel();
		MainWindow window = new MainWindow(model);
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
}
```
Launch the app!

## Something's not right... ##

Your app should run and you should see your label and buttons.  However, what happens when you try to click on any of the buttons?  The text doesn't change colors, and you should see something like this in the output console:

```
There are no listeners for event key 'MAIN_COLOR_CHANGE'
There are no listeners for event key 'MAIN_COLOR_CHANGE'
There are no listeners for event key 'MAIN_COLOR_CHANGE'
There are no listeners for event key 'MAIN_DISPLAY_TEXT_CHANGE'
```

What happened?  We forgot to make our controller, that's what happened.  Remember the `FooModelLocator`?  That's where we make our controllers.  Here's an updated `FooModelLocator`:

```
package foo.model;

import foo.control.main.MainController;
import foo.view.main.MainWindow;

public class FooModelLocator {
	private static FooModelLocator instance = null;
	
	// controllers
	private MainController mainController = new MainController();
	
	private MainWindow mainWindow = null;
	
	private FooModelLocator(){}

	public void setMainWindow(MainWindow argWindow){
		mainWindow = argWindow;
	}
	
	public MainWindow getMainWindow(){
		return mainWindow;
	}
	
	public static FooModelLocator getInstance(){
		if(instance == null){
			instance = new FooModelLocator();
		}
		return instance;
	}
}
```

and here's our updated `App`:
```
package foo;

import javax.swing.JFrame;

import foo.model.FooModelLocator;
import foo.model.main.MainModel;
import foo.view.main.MainWindow;

public class App {
	
	public static void main(String[] args) {
		FooModelLocator locator = FooModelLocator.getInstance();
		MainModel model = new MainModel();
		MainWindow window = new MainWindow(model);
		locator.setMainWindow(window);
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
}
```

Now we didn't really need to add the whole `mainWindow` stuff, but I put it there as an excuse to instantiate the ModelLocator, and because it's good to store your main application window in the ModelLocator, as you'll probably need it later.

Let's run it again with our changes...

## Colors Change!!! ##
You should be able to change the color of the text by pressing the buttons!

![http://www.dmurph.com/javasimplemvc/ExampleWindow1.png](http://www.dmurph.com/javasimplemvc/ExampleWindow1.png)

### Let's take a look at these events ###
Before we make our change text window, lets take a moment to visualize our events.  JMVC provides a great tool for seeing all the events that are dispatched (see EventMonitoring).  To show the monitor, we can simply call `MVC.showEventMonitor()`.  Let's put it in our `App` class:

```
package foo;

import javax.swing.JFrame;

import foo.model.FooModelLocator;
import foo.model.main.MainModel;
import foo.view.main.MainWindow;

public class App {
	
	public static void main(String[] args) {
		MVC.showEventMonitor();  // here is where we add it
		FooModelLocator locator = FooModelLocator.getInstance();
		MainModel model = new MainModel();
		MainWindow window = new MainWindow(model);
		locator.setMainWindow(window);
		window.pack();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
	}
}
```

Now when we launch the application, we see the event monitor window pop up, and when we click on the button we can see our events as they dispatch!

![http://www.dmurph.com/javasimplemvc/ExampleMonitor1.png](http://www.dmurph.com/javasimplemvc/ExampleMonitor1.png)

This tool is invaluable when trying to debug your code, and making sure that all MVC events are fired correctly.  It will also show you when an event doesn't have a listeners (there's a star in the `Never Received` column).

Notice, however, that there is a column we're not using, the `Event Value` column.  This is because the monitor populates this only if the event is an `ObjectEvent<E>`.  Then it can call `getValue().toString()` on the event and display the value of the event.  Lets do this for our ColorChangeEvent:

```
public class ColorChangeEvent extends ObjectEvent<Color> {

	public final MainModel model;
	
	public ColorChangeEvent(Color argColor, MainModel argModel) {
		super(MainController.COLOR_CHANGE, argColor);
		model = argModel;
	}
}
```

When we lauch the app again, now we see the color values!

![http://www.dmurph.com/javasimplemvc/ExampleMonitor2.png](http://www.dmurph.com/javasimplemvc/ExampleMonitor2.png)

Now that we've learned how to monitor our events, lets create our text change dialog.

## Text Change Dialog ##

Next part of the tutorial coming soon!