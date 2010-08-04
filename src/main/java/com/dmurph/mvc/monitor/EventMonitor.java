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
 * Created at Aug 3, 2010, 10:06:41 PM
 */
package com.dmurph.mvc.monitor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.dmurph.mvc.IGlobalEventMonitor;
import com.dmurph.mvc.MVCEvent;

/**
 * This is an event monitor gui, for displaying all {@link MVCEvent}s that
 * are dispatched.  This makes it easy to debug your program and find out what is
 * dispatched when.
 * @author Daniel Murphy
 */
public class EventMonitor extends JFrame implements IGlobalEventMonitor {
	private static final long serialVersionUID = 1L;
	
	public static final int DEFAULT_NUM_MESSAGES_LOGGED = 500;
	public static final int LOG_ALL_MESSAGES = Integer.MAX_VALUE;
	
	private JTable table = new JTable();
	private JButton enableDisable = new JButton();
	private JLabel info = new JLabel();
	private final EventTableModel model;
	private final IGlobalEventMonitor delegate;
	private final EventMonitorType type;
	
	private boolean enabled = true;
	
	private int numEvents = 0;
	private int numSilentEvents = 0;
	
	/**
	 * Creates a simple event monitor.
	 */
	public EventMonitor(){
		this(EventMonitorType.BEFORE_DISPATCH, null, DEFAULT_NUM_MESSAGES_LOGGED);
	}
	
	public EventMonitor(int argMaxLogEntries){
		this(EventMonitorType.BEFORE_DISPATCH, null, argMaxLogEntries);
	}
	
	public EventMonitor(IGlobalEventMonitor argDelegate){
		this(EventMonitorType.BEFORE_DISPATCH, argDelegate, DEFAULT_NUM_MESSAGES_LOGGED);
	}
	
	public EventMonitor(EventMonitorType argType){
		this(argType, null, DEFAULT_NUM_MESSAGES_LOGGED);
	}
	
	/**
	 * @param argType the type of event monitor this will be.
	 * @param argDelegate the delegate to forward calls to.  Can be null.
	 */
	public EventMonitor(EventMonitorType argType, IGlobalEventMonitor argDelegate){
		this(argType, argDelegate, DEFAULT_NUM_MESSAGES_LOGGED);
	}
	
	/**
	 * @param argType the type of event monitor this will be.
	 * @param argDelegate the delegate to forward calls to.  Can be null.
	 * @param argMaxMessages the maximum messages to keep.  Use {@link #LOG_ALL_MESSAGES}
	 * 						 to try to log all messages.
	 */
	public EventMonitor(EventMonitorType argType, IGlobalEventMonitor argDelegate, int argMaxMessages){
		super("JMVC Event Monitor");
		setSize(400, 400);
		delegate = argDelegate;
		type = argType;
		if(argMaxMessages < 0){
			throw new IllegalArgumentException("Number cannot be negative");
		}
		model = new EventTableModel(argMaxMessages);
		
		initializeComponents();
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent argE) {
				setEnabled(false);
			}
		});
	}
	
	/**
	 * @return the delegate
	 */
	public IGlobalEventMonitor getDelegate() {
		return delegate;
	}
	
	private void initializeComponents() {
		setLayout(new BorderLayout());
		
		add(new JScrollPane(table), "Center");
		table.setFillsViewportHeight(true);
		table.setModel(model);
		
		Box box = Box.createHorizontalBox();
		box.add(Box.createRigidArea(new Dimension(10, 10)));
		box.add(info);
		box.add(Box.createHorizontalGlue());
		enableDisable.setText("Disable");
		box.add(enableDisable);
		add(box, "South");
		
		enableDisable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent argE) {
				if(enabled){
					setEnabled(false);
				}else{
					setEnabled(true);
				}
			}
		});
	}
	

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param argEnabled the enabled to set
	 */
	public void setEnabled(boolean argEnabled) {
		enabled = argEnabled;
		if(enabled){
			enableDisable.setText("Disable");
		}else{
			enableDisable.setText("Enable");
		}
	}

	/**
	 * @see com.dmurph.mvc.IGlobalEventMonitor#afterDispatch(com.dmurph.mvc.MVCEvent)
	 */
	public void afterDispatch(MVCEvent argEvent) {
		if(delegate != null){
			delegate.afterDispatch(argEvent);
		}
		if(enabled && type == EventMonitorType.AFTER_DISPATCH){
			numEvents++;
			updateInfo();
			model.logEvent(argEvent, EventType.LISTENERS);
		}
	}
	
	/**
	 * @see com.dmurph.mvc.IGlobalEventMonitor#beforeDispatch(com.dmurph.mvc.MVCEvent)
	 */
	public void beforeDispatch(MVCEvent argEvent) {
		if(delegate != null){
			delegate.beforeDispatch(argEvent);
		}
		if(enabled && type == EventMonitorType.BEFORE_DISPATCH){
			numEvents++;
			updateInfo();
			model.logEvent(argEvent, EventType.LISTENERS);
		}
	}
	
	/**
	 * @see com.dmurph.mvc.IGlobalEventMonitor#noListeners(com.dmurph.mvc.MVCEvent)
	 */
	public void noListeners(MVCEvent argEvent) {
		if(delegate != null){
			delegate.noListeners(argEvent);
		}
		if(enabled){
			numEvents++;
			numSilentEvents++;
			updateInfo();
			model.logEvent(argEvent, EventType.NO_LISTENERS);
		}
	}
	
	private void updateInfo(){
		info.setText(numEvents+" total events, "+numSilentEvents+" never recieved.");
	}
	
	protected static enum EventType{
		LISTENERS, NO_LISTENERS
	}
	
//	public static void main(String[] args) {
//		EventMonitor monitor = new EventMonitor(EventMonitorType.BEFORE_DISPATCH, MVC.getGlobalEventMonitor());
//		MVC.setGlobalEventMonitor(monitor);
//		monitor.setVisible(true);
//		
//		MVCEvent event = new MVCEvent("HI");
//		event.dispatch();
//		event = new MVCEvent("HI");
//		event.dispatch();event = new MVCEvent("HI2");
//		event.dispatch();event = new MVCEvent("HI2");
//		event.dispatch();event = new MVCEvent("HI2");
//		event.dispatch();
//	}
}
