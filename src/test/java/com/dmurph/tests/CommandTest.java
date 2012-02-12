/**
 * Created at Jun 24, 2010, 4:44:10 AM
 */
package com.dmurph.tests;

import junit.framework.TestCase;

import com.dmurph.mvc.MVC;
import com.dmurph.mvc.MVCEvent;
import com.dmurph.mvc.control.FrontController;
import com.dmurph.mvc.control.ICommand;

/**
 * @author daniel
 *
 */
public class CommandTest extends TestCase{

	static int commandEventCount;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		commandEventCount = 0;
	}
	
	
	public void testCommand(){
		Controller controller = new Controller();
		MVCEvent commandEvent = new MVCEvent("command");
		MVCEvent controllerEvent = new MVCEvent("controller");
		int eventsEach = 10;
		for(int i=0; i<eventsEach; i++) {
			if(i % 3 == 0 ){
				controllerEvent.dispatch();
				commandEvent.dispatch();
			}else {
				commandEvent.dispatch();
				controllerEvent.dispatch();
			}
		}
		MVC.completeRemainingEvents(1000);
		
		assertEquals(eventsEach, controller.controllerEventCount);
		assertEquals(eventsEach, commandEventCount);
	}
	
	public static class Controller extends FrontController {
		int controllerEventCount = 0;
		
		public Controller(){
			registerCommand("command", Command.class);
			registerCommand("controller", "test2");
		}
		
		public void test2(MVCEvent argEvent){
			controllerEventCount++;
		}
	}
	
	public static class Command implements ICommand{
		public void execute(MVCEvent argEvent) {
			CommandTest.commandEventCount++;
		}
	}
}


