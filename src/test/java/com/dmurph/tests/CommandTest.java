/**
 * Created at Jun 24, 2010, 4:44:10 AM
 */
package com.dmurph.tests;

import junit.framework.TestCase;

import com.dmurph.mvc.MVCEvent;
import com.dmurph.mvc.control.FrontController;
import com.dmurph.mvc.control.ICommand;

/**
 * @author daniel
 *
 */
public class CommandTest extends TestCase{

	public void testCommand(){
		@SuppressWarnings("unused")
		Controller controller = new Controller();
		MVCEvent event = new MVCEvent("test");
		event.dispatch();
		event.dispatch();
		event.dispatch();
		event = new MVCEvent("test2");
		event.dispatch();
		event.dispatch();
		event.dispatch();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static class Controller extends FrontController{
		
		public Controller(){
			registerCommand("test", Command.class);
			registerCommand("test2", "test2");
		}
		
		public void test2(MVCEvent argEvent){
			System.out.println(this.toString());
		}
	}
	
	public static class Command implements ICommand{
		/**
		 * @see com.dmurph.mvc.control.ICommand#execute(com.dmurph.mvc.MVCEvent)
		 */
		@Override
		public void execute(MVCEvent argEvent) {
			System.out.println(this.toString());
		}
		
	}
}


