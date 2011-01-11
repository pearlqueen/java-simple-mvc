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
import java.util.HashMap;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dmurph.mvc.I18n;
import com.dmurph.mvc.IEventListener;
import com.dmurph.mvc.MVC;
import com.dmurph.mvc.MVCEvent;


/**
 * Abstract controller.  Use {@link #registerCommand(String, ICommand)} and {@link #registerCommand(String, String)}
 * to listen for events from the view objects.  The {@link #registerCommand(String, String)} method is
 * easier to use, as it will call a method for you when an event with the given key is dispatched.
 * @author Daniel Murphy
 */
public abstract class FrontController{
	
	private final static Logger log = LoggerFactory.getLogger(FrontController.class);
	
	// for getting the method
	private final Class<?> paramTypes[] = { MVCEvent.class };
	private final HashSet<String> keys = new HashSet<String>();
	private final HashMap<String, HashSet<Class<? extends ICommand>>> keyToCommands;
	private final HashMap<String, HashSet<Method>> keyToMethods;
	
	public FrontController(){
		keyToCommands = new HashMap<String, HashSet<Class<? extends ICommand>>>();
		keyToMethods = new HashMap<String, HashSet<Method>>();
	}
	/**
	 * Registers the listener to the given key. {@link MVCEvent}'s are dispatched globally, so 
	 * careful with the actual values of your keys and make sure they are unique.
	 * @param argKey
	 * @param argCommand
	 */
	protected synchronized void registerCommand(String argKey, Class<? extends ICommand> argCommand){
		if(argCommand == null){
			throw new NullPointerException(I18n.getText("frontController.commandNull"));
		}
		if(argKey == null){
			throw new NullPointerException(I18n.getText("frontController.keyNull"));
		}
		try {
			argCommand.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(I18n.getText("frontController.makingCommand"), e);
		}
		
		if(keyToCommands.containsKey(argKey)){
			keyToCommands.get(argKey).add(argCommand);
		}else{
			HashSet<Class<? extends ICommand>> commands = new HashSet<Class<? extends ICommand>>();
			commands.add(argCommand);
			keyToCommands.put(argKey, commands);
			if(!keys.contains(argKey)){
				FrontControllerEventListener listener = new FrontControllerEventListener(this);
				MVC.addEventListener(argKey, listener);
				keys.add(argKey);
			}
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
	protected synchronized void registerCommand(String argKey, String argCommandMethod){
		if(argKey == null){
			throw new NullPointerException(I18n.getText("frontController.keyNull"));
		}
		if(argCommandMethod == null){
			throw new NullPointerException(I18n.getText("frontController.commandNull"));
		}
		HashSet<Method> set;
		if(keyToMethods.containsKey(argKey)){
			set = keyToMethods.get(argKey);
		}else{
			set = new HashSet<Method>();
			keyToMethods.put(argKey, set);
		}
		
		try{
			set.add(getClass().getMethod( argCommandMethod, paramTypes));
		}catch(Exception e){
			log.error(I18n.getText("frontController.findingMethod",argCommandMethod), e);
			throw new RuntimeException(I18n.getText("frontController.findingMethod",argCommandMethod), e);
		}
		
		if(!keys.contains(argKey)){
			FrontControllerEventListener listener = new FrontControllerEventListener(this);
			MVC.addEventListener(argKey, listener);
			keys.add(argKey);
		}
	}
	
	private static class FrontControllerEventListener implements IEventListener{
		FrontController controller;
		
		public FrontControllerEventListener(FrontController argController){
			controller = argController;
		}
		
		public void eventReceived( MVCEvent argEvent) {
			HashSet<Class<? extends ICommand>> commands = controller.keyToCommands.get(argEvent.key);
			
			if(commands != null){
				for(Class<? extends ICommand> commandClass : commands){
					ICommand command;
					try {
						command = commandClass.newInstance();
					} catch (Exception e){
						// shouldn't happen
						log.error("Exception when creating new command instance", e);
						continue;
					}
					
					command.execute(argEvent);
				}
			}
			
			HashSet<Method> methods = controller.keyToMethods.get(argEvent.key);
			
			if(methods != null){
				for(Method m : methods){
					try {
						m.invoke(controller, argEvent);
					} catch ( IllegalArgumentException e) {
						log.error(I18n.getText("frontController.invokingMethod", m.toString()), e);
					} catch ( IllegalAccessException e) {
						log.error(I18n.getText("frontController.invokingMethod", m.toString()), e);
					} catch ( InvocationTargetException e) {
						log.error(I18n.getText("frontController.invokingMethod", m.toString()), e);
					}
				}
			}
		}
	}
}
