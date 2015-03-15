# Introduction #
A command just has to implement the `ICommand` interface.  Commands are supposed to receive all their information from the events, and perform their task independent of previous events.

If the controller registered a command class with an event key, a new command is created every time that event is dispatched, so there must be an empty constructor available in all commands.

For convenience, methods in the controller can also be used as commands.

# Usage #
Here are the commands from the example in ControllerSupport.
```
public class StartupCommand implements ICommand{

	public void execute(MVCEvent argEvent){
		// startup stuff

                // initializations, if any

                // main window
                MainWindow window = new MainWindow();
                ModelLocator.getInstance().setMain(window);
                window.setVisible(true);
	}
}
```

```
public class QuitCommand implements ICommand{

	public void execute(MVCEvent argEvent){
		System.exit(1);
	}
}
```

```
public class AboutCommand implements ICommand{

	public void execute(MVCEvent argEvent){
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
		// since in my imaginary project I've made AboutDialog a blocking dialog window
		// (modal is true), this doesn't return until I close the window.
	}
}
```