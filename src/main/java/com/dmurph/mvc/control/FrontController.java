/**
 * Copyright (c) 2010 Daniel Murphy
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
/**
 * Created at 3:21:37 AM, Mar 17, 2010
 */
package com.dmurph.mvc.control;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import com.dmurph.mvc.I18n;
import com.dmurph.mvc.IEventListener;
import com.dmurph.mvc.MVC;
import com.dmurph.mvc.MVCEvent;


/**
 * Abstract controller.  Use {@link #registerCommand(String, IEventListener)} and {@link #registerCommand(String, String)}
 * to listen for events from the view objects.  The {@link #registerCommand(String, String)} method is
 * easier to use, as it will call a method for you when an event with the given key is dispatched.
 * @author Daniel Murphy
 */
public abstract class FrontController{
	
	// for getting the method
	private final Class<?> paramTypes[] = { MVCEvent.class };
	private final HashMap<String, HashSet<ICommand>> keyToCommands;
	
	public FrontController(){
		keyToCommands = new HashMap<String, HashSet<ICommand>>();
	}
	/**
	 * Registers the listener to the given key. {@link MVCEvent}'s are dispatched globally, so 
	 * careful with the actual values of your keys and make sure they are unique.
	 * @param argKey
	 * @param argCommand
	 */
	protected synchronized void registerCommand(String argKey, ICommand argCommand){
		if(argCommand == null){
			throw new NullPointerException(I18n.getText("frontController.commandNull"));
		}
		if(argKey == null){
			throw new NullPointerException(I18n.getText("frontController.keyNull"));
		}
		
		if(keyToCommands.containsKey(argKey)){
			keyToCommands.get(argKey).add(argCommand);
		}else{
			HashSet<ICommand> commands = new HashSet<ICommand>();
			commands.add(argCommand);
			FrontControllerEventListener listener = new FrontControllerEventListener(this);
			MVC.addEventListener(argKey, listener);
			keyToCommands.put(argKey, commands);
		}
	}
	
	/**
	 * Registers the given method with the event key.  The method must have only one argument
	 * of type {@link MVCEvent}.  It will be called with the event that was dispatched with this key.
	 * More than one method can be assigned to a key, and they will be called in the order of
	 * registration.  Duplicate registrations will be ignored.
	 * @param argKey
	 * @param argCommandMethod
	 * @throws NoSuchMethodException 
	 * @throws SecurityException 
	 */
	protected synchronized void registerCommand(String argKey, String argCommandMethod) throws SecurityException, NoSuchMethodException{
		if(argKey == null){
			throw new NullPointerException(I18n.getText("frontController.keyNull"));
		}
		if(argCommandMethod == null){
			throw new NullPointerException(I18n.getText("frontController.commandNull"));
		}
		
		Method m = getClass().getMethod( argCommandMethod, paramTypes);
		
		MethodCommand mCommand = new MethodCommand(this, m);
		registerCommand(argKey, mCommand);
	}
	
	private static class FrontControllerEventListener implements IEventListener{
		private final FrontController controller;
		
		public FrontControllerEventListener(FrontController argController) {
			controller = argController;
		}
		
		@Override
		public void eventReceived( MVCEvent argEvent) {
			HashSet<ICommand> commands = controller.keyToCommands.get(argEvent.key);
			
			for(ICommand command : commands){
				command.execute(argEvent);
			}
		}
	}
	
	private static class MethodCommand implements ICommand {
		private final FrontController controller;
		private final Method method;
		
		public MethodCommand(FrontController argController, Method argMethod){
			controller = argController;
			method = argMethod;
		}

		/**
		 * @see com.dmurph.mvc.control.ICommand#execute(com.dmurph.mvc.MVCEvent)
		 */
		@Override
		public void execute(MVCEvent argEvent) {
			try {
				method.invoke( controller, argEvent);
			} catch ( IllegalArgumentException e) {
				System.err.println(I18n.getText("frontController.invokingMethod", method.toString()));
				e.printStackTrace();
			} catch ( IllegalAccessException e) {
				System.err.println(I18n.getText("frontController.invokingMethod", method.toString()));
				e.printStackTrace();
			} catch ( InvocationTargetException e) {
				System.err.println(I18n.getText("frontController.invokingMethod", method.toString()));
				e.printStackTrace();
			}
		}
		
		/**
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((method == null) ? 0 : method.hashCode());
			return result;
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MethodCommand other = (MethodCommand) obj;
			if (controller == null) {
				if (other.controller != null)
					return false;
			}
			else if (!controller.equals(other.controller))
				return false;
			if (method == null) {
				if (other.method != null)
					return false;
			}
			else if (!method.equals(other.method))
				return false;
			return true;
		}
	}
}
