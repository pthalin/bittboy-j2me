/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 */
package org.thenesis.midpath.ui.toolkit.sdl;

import sdljava.event.SDLEvent;
import sdljava.event.SDLExposeEvent;
import sdljava.event.SDLKey;
import sdljava.event.SDLKeyboardEvent;
import sdljava.event.SDLMouseButtonEvent;
import sdljava.event.SDLMouseMotionEvent;
import sdljava.event.SDLQuitEvent;
import sdljava.x.swig.SDLPressedState;

import com.sun.midp.events.EventQueue;
import com.sun.midp.events.EventTypes;
import com.sun.midp.events.NativeEvent;
import com.sun.midp.lcdui.EventConstants;
import com.sun.midp.log.Logging;

/**
 * Fetches events from SDL, translates them to MIDP events and pumps them up
 * into the MIDP UI event queue.
 *
 * @author Guillaume Legris
 * @author Mathieu Legris
 */
public class SDLEventPump implements Runnable {

	/**
	 * Indicates if we are currently inside a drag operation. This is
	 * set to the button ID when a button is pressed and to -1 (indicating
	 * that no drag is active) when the mouse is released.
	 */
	private int drag;
	private volatile Thread thread;
	
	//private String LETTERS = "abcdefghiklmnopqrstuvwxyz";

	/**
	 * Creates a new SDLEventThread for the specified X Display.
	 */
	public SDLEventPump() {
		drag = -1;
	}
	
	public void start() {
		thread = new Thread(this);
		thread.start();
	}
	
	public void stop() {
		thread = null;
	}

	/**
	 * The main event pump loop. Events are pumped into the system event queue.
	 */
	public void run() {

		try {
			
			SDLEvent.enableUNICODE(1);

			while (Thread.currentThread() == thread) {
				processEvent(SDLEvent.waitEvent(true));
			}

		} catch (Throwable t) {
			//Logging.report(Logging.ERROR, LogChannels.LC_EVENTS, "Exception during event dispatch: " + x.getMessage());
			Logging.trace(t, "Exception during event dispatch");
			//System.err.println("Exception during event dispatch: " + x.getMessage());
			//x.printStackTrace(System.err);
		}

	}

	public void processEvent(SDLEvent event) {

		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] SDLEventThread.processEvent()");

		if (event instanceof SDLMouseButtonEvent)
			processEvent((SDLMouseButtonEvent) event);
		else if (event instanceof SDLMouseMotionEvent)
			processEvent((SDLMouseMotionEvent) event);
		else if (event instanceof SDLQuitEvent)
			processEvent((SDLQuitEvent) event);
		else if (event instanceof SDLExposeEvent)
			processEvent((SDLExposeEvent) event);
		else if (event instanceof SDLKeyboardEvent)
			processEvent((SDLKeyboardEvent) event);
	}

	public void processEvent(SDLMouseButtonEvent event) {
		
		NativeEvent nativeEvent = new NativeEvent(EventTypes.PEN_EVENT);

		int sdlButton = event.getButton();
		drag = sdlButton;

		if (event.getState() == SDLPressedState.PRESSED) {
			if (Logging.TRACE_ENABLED)
				System.out.println("[DEBUG] SDLEventThread.processEvent(): MOUSE_PRESSED");
			nativeEvent.intParam1 = EventConstants.PRESSED; // Event type
		} else {
			if (Logging.TRACE_ENABLED)
				System.out.println("[DEBUG] SDLEventThread.processEvent(): MOUSE_RELEASED");
			drag = -1;
			nativeEvent.intParam1 = EventConstants.RELEASED; // Event type
		}
		
		nativeEvent.intParam2 = event.getX(); // x
		nativeEvent.intParam3 = event.getY(); // y
		// Set event source (intParam4). Fake display with id=1
		nativeEvent.intParam4 = 1;
		
		EventQueue.getEventQueue().post(nativeEvent);
		
	}

	public void processEvent(SDLMouseMotionEvent event) {
		
		NativeEvent nativeEvent = new NativeEvent(EventTypes.PEN_EVENT);
		
		if (drag != -1) {
			nativeEvent.intParam1 = EventConstants.DRAGGED; // Event type
			nativeEvent.intParam2 = event.getX(); // x
			nativeEvent.intParam3 = event.getY(); // y
			// Set event source (intParam4). Fake display with id=1
			nativeEvent.intParam4 = 1;
		}
		
		EventQueue.getEventQueue().post(nativeEvent);

	}

	public void processEvent(SDLKeyboardEvent event) {

		int unicode = event.getUnicode();
		int keyCode = event.getSym();
		
		NativeEvent nativeEvent = new NativeEvent(EventTypes.KEY_EVENT);
		// Set event type (intParam1)
		if (event.getState() == SDLPressedState.PRESSED) {
			if (Logging.TRACE_ENABLED)
				System.out.println("[DEBUG] SDLEventThread.processEvent(): key sym: " + event.getSym() + " unicode: " + unicode + " char: " + (char)unicode);
			nativeEvent.intParam1 = EventConstants.PRESSED;
		} else if (event.getState() == SDLPressedState.RELEASED) {
			nativeEvent.intParam1 = EventConstants.RELEASED;
		}
		// Set event key code (intParam2)
		char c = (char)unicode;
		int internalCode = SDLEventMapper.mapToInternalEvent(keyCode, c);
		if (internalCode != 0) {
			nativeEvent.intParam2 = internalCode;
		} else if ((unicode != 0) && (keyCode != SDLKey.SDLK_LSHIFT) && (keyCode != SDLKey.SDLK_RSHIFT)) {
			nativeEvent.intParam2 = c;
		} else {
			return;
		}
		
		// Set event source (intParam4). Fake display with id=1
		nativeEvent.intParam4 = 1;

		EventQueue.getEventQueue().post(nativeEvent);
	
	}

	public void processEvent(SDLQuitEvent event) {
		NativeEvent nativeEvent = new NativeEvent(EventTypes.SHUTDOWN_EVENT);
		EventQueue.getEventQueue().post(nativeEvent);
	}

	public void processEvent(SDLExposeEvent event) {
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] SDLEventThread.processEvent(SDLExposeEvent event): NOT IMPLEMENTED");
	}


	
}
