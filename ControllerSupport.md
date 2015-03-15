# Introduction #
The controller support in JavaSimpleMVC is pretty straight forward.  In your controller you register commands for message keys that you are listening for.

# Usage #
Here is an example of a simple controller:

```
public class MainWindowController extends FrontController{
	public static final String STARTUP = "APPLICATION_STARTUP";
	public static final String QUIT = "APPLICATION_QUIT";
	public static final String ABOUT = "APPLICATION_ABOUT";

	public MainWindowController(){
		registerCommand(STARTUP, StartupCommand.class);
		registerCommand(QUIT, QuitCommand.class);
		registerCommand(ABOUT, AboutCommand.class);
	}
}
```

This controller will execute each command when an event with the registered event key is dispatched.  See CommandSupport for these command examples.

Here is an example of the same controller but with a lazier programmer (using methods in the controller instead of separate command objects).

```
public class MainWindowController extends FrontController{
	public static final String STARTUP = "APPLICATION_STARTUP";
	public static final String QUIT = "APPLICATION_QUIT";
	public static final String ABOUT = "APPLICATION_ABOUT";

	public MainWindowController(){
		registerCommand(STARTUP, "startup");
		registerCommand(QUIT, "quit");
		registerCommand(ABOUT, "about");
	}

	public void startup(MVCEvent argEvent){
		// startup stuff

		// initializations, if any

		// main window
		MainWindow window = new MainWindow();
		ModelLocator.getInstance().setMain(window);
		window.setVisible(true);
	}

	public void quit(MVCEvent argEvent){
		System.exit(1);
	}

	public void about(MVCEvent argEvent){
		// example of custom event that holds a boolean value
		AboutEvent event = (AboutEvent) argEvent;
		boolean showLogo = event.isShowLogo();

		AboutDialog about = new AboutDialog(this, showLogo);
		try {
			MVC.splitOff();
			// so other mvc events can execute, this is explained in the EventDispatchSystem wiki page
		} catch (IllegalThreadException e) {
			e.printStackTrace();
		} catch (IncorrectThreadException e) {
			e.printStackTrace();
		}
		about.setVisible(true);
		// since in my imaginary project I've made AboutDialog a blocking dialog window, this doesn't return until I close the window.
	}
}
```

This isn't the best usage if you're looking to stick to MVC standards, as this allows interaction between command methods (they can see the same object fields and such).  In general, commands need to be self contained and unique to each event.  The first method  enforces this methodology much better.

So, these commands listen for events like these:
```
MVCEvent event = new MVCEvent(MainWindowController.STARTUP);
event.dispatch();
```
or
```
MVCEvent event = new MVCEvent(MainWindowController.QUIT);
event.dispatch();
```
or
```
MVCEvent event = new AboutEvent(true);
event.dispatch();
```

But how does the controller get created?  See ImplementationGuide.