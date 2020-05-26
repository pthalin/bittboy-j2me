package sdljava.event;

/**
 *  sdljava - a java binding to the SDL API
 *
 *  Copyright (C) 2004  Ivan Z. Ganza
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *  USA
 *
 *  Ivan Z. Ganza (ivan_ganza@yahoo.com)
 */
import sdljava.SDLException;
import sdljava.SDLMain;
import sdljava.x.swig.SDL_Event;
import sdljava.x.swig.SWIG_SDLEvent;
import sdljava.x.swig.SWIG_SDLEventConstants;

/**
 * The SDL_Event is the core to all event handling in SDL, its
 * probably the most important structure after SDL_Surface. SDL_Event
 * is a union of all event structures used in SDL, using it is a
 * simple matter of knowing which union member relates to which event
 * type.
 * <P>
 * In the case of sdljava you can simply deal with the returned SDLEvent
 * instance.  For example if you have the code SDLEvent event = SDLEvent.waitEvent()
 * you can now do an instanceof on the returned event to determine its type, cast
 * it to the appropriate class, then deal with the data in any way you wish.
 *
 * NOTE: The following is not yet implemented:
 * <UL>
 *   <LI>SDL_SysWMEvent</I>
 *   <LI>SDL_UserEvent</I>
 *   <LI>SDL_PeekEvents</I>
 *   <LI>SDL_PushEvent</I>
 *   <LI>SDL_SetEventFilter</I>
 *   <LI>SDL_GetKeyState</I>
 * </UL>
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLEvent.java,v 1.25 2006/02/09 02:19:55 ivan_ganza Exp $ 
 */
public abstract class SDLEvent {

	////////////////////////////////////////////////////////////////////////////////
	// CONSTANTS

	public static final int SDL_ADDEVENT = 0;
	public static final int SDL_PEEKEVENT = 1;
	public static final int SDL_GETEVENT = 2;

	public static final int SDL_QUERY = -1;
	public static final int SDL_IGNORE = 0;
	public static final int SDL_DISABLE = 0;
	public static final int SDL_ENABLE = 1;

	public final static int SDL_DEFAULT_REPEAT_DELAY = SWIG_SDLEventConstants.SDL_DEFAULT_REPEAT_DELAY;
	public final static int SDL_DEFAULT_REPEAT_INTERVAL = SWIG_SDLEventConstants.SDL_DEFAULT_REPEAT_INTERVAL;

	public final static int SDL_PRESSED = 0x01;
	public final static int SDL_RELEASED = 0x00;

	public final static int SDL_NOEVENT = SWIG_SDLEventConstants.SDL_NOEVENT;
	public final static int SDL_ACTIVEEVENT = SWIG_SDLEventConstants.SDL_ACTIVEEVENT;
	public final static int SDL_KEYDOWN = SWIG_SDLEventConstants.SDL_KEYDOWN;
	public final static int SDL_KEYUP = SWIG_SDLEventConstants.SDL_KEYUP;
	public final static int SDL_MOUSEMOTION = SWIG_SDLEventConstants.SDL_MOUSEMOTION;
	public final static int SDL_MOUSEBUTTONDOWN = SWIG_SDLEventConstants.SDL_MOUSEBUTTONDOWN;
	public final static int SDL_MOUSEBUTTONUP = SWIG_SDLEventConstants.SDL_MOUSEBUTTONUP;
	public final static int SDL_JOYAXISMOTION = SWIG_SDLEventConstants.SDL_JOYAXISMOTION;
	public final static int SDL_JOYBALLMOTION = SWIG_SDLEventConstants.SDL_JOYBALLMOTION;
	public final static int SDL_JOYHATMOTION = SWIG_SDLEventConstants.SDL_JOYHATMOTION;
	public final static int SDL_JOYBUTTONDOWN = SWIG_SDLEventConstants.SDL_JOYBUTTONDOWN;
	public final static int SDL_JOYBUTTONUP = SWIG_SDLEventConstants.SDL_JOYBUTTONUP;
	public final static int SDL_QUIT = SWIG_SDLEventConstants.SDL_QUIT;
	public final static int SDL_SYSWMEVENT = SWIG_SDLEventConstants.SDL_SYSWMEVENT;
	public final static int SDL_VIDEORESIZE = SWIG_SDLEventConstants.SDL_VIDEORESIZE;
	public final static int SDL_VIDEOEXPOSE = SWIG_SDLEventConstants.SDL_VIDEOEXPOSE;
	public final static int SDL_USEREVENT = SWIG_SDLEventConstants.SDL_USEREVENT;
	public final static int SDL_NUMEVENTS = SWIG_SDLEventConstants.SDL_NUMEVENTS;

	public final static int SDL_ACTIVEEVENTMASK = SWIG_SDLEventConstants.SDL_ACTIVEEVENTMASK;
	public final static int SDL_KEYDOWNMASK = SWIG_SDLEventConstants.SDL_KEYDOWNMASK;
	public final static int SDL_KEYUPMASK = SWIG_SDLEventConstants.SDL_KEYUPMASK;
	public final static int SDL_MOUSEMOTIONMASK = SWIG_SDLEventConstants.SDL_MOUSEMOTIONMASK;
	public final static int SDL_MOUSEBUTTONDOWNMASK = SWIG_SDLEventConstants.SDL_MOUSEBUTTONDOWNMASK;
	public final static int SDL_MOUSEBUTTONUPMASK = SWIG_SDLEventConstants.SDL_MOUSEBUTTONUPMASK;
	public final static int SDL_MOUSEEVENTMASK = SWIG_SDLEventConstants.SDL_MOUSEEVENTMASK;
	public final static int SDL_JOYAXISMOTIONMASK = SWIG_SDLEventConstants.SDL_JOYAXISMOTIONMASK;
	public final static int SDL_JOYBALLMOTIONMASK = SWIG_SDLEventConstants.SDL_JOYBALLMOTIONMASK;
	public final static int SDL_JOYHATMOTIONMASK = SWIG_SDLEventConstants.SDL_JOYHATMOTIONMASK;
	public final static int SDL_JOYBUTTONDOWNMASK = SWIG_SDLEventConstants.SDL_JOYBUTTONDOWNMASK;
	public final static int SDL_JOYBUTTONUPMASK = SWIG_SDLEventConstants.SDL_JOYBUTTONUPMASK;
	public final static int SDL_JOYEVENTMASK = SWIG_SDLEventConstants.SDL_JOYEVENTMASK;
	public final static int SDL_VIDEORESIZEMASK = SWIG_SDLEventConstants.SDL_VIDEORESIZEMASK;
	public final static int SDL_VIDEOEXPOSEMASK = SWIG_SDLEventConstants.SDL_VIDEOEXPOSEMASK;
	public final static int SDL_QUITMASK = SWIG_SDLEventConstants.SDL_QUITMASK;
	public final static int SDL_SYSWMEVENTMASK = SWIG_SDLEventConstants.SDL_SYSWMEVENTMASK;

	public final static int SDL_APPMOUSEFOCUS = SWIG_SDLEventConstants.SDL_APPMOUSEFOCUS;
	public final static int SDL_APPINPUTFOCUS = SWIG_SDLEventConstants.SDL_APPINPUTFOCUS;
	public final static int SDL_APPACTIVE = SWIG_SDLEventConstants.SDL_APPACTIVE;

	public final static int SDL_BUTTON_LEFT = 1;
	public final static int SDL_BUTTON_MIDDLE = 2;
	public final static int SDL_BUTTON_RIGHT = 3;
	public final static int SDL_BUTTON_WHEELUP = 4;
	public final static int SDL_BUTTON_WHEELDOWN = 5;

	////////////////////////////////////////////////////////////////////////////////
	// MEMBERS
	//   -- notice all the events are static, this is because we never create new ones

	static SDL_Event swigEvent = new SDL_Event();

	static SDLActiveEvent activeEvent = new SDLActiveEvent();
	static SDLKeyboardEvent sdlKeyboardEvent = new SDLKeyboardEvent();
	static SDLMouseMotionEvent mouseMotionEvent = new SDLMouseMotionEvent();
	static SDLMouseButtonEvent mouseButtonEvent = new SDLMouseButtonEvent();
	static SDLJoyAxisEvent joyAxisEvent = new SDLJoyAxisEvent();
	static SDLJoyBallEvent joyBallEvent = new SDLJoyBallEvent();
	static SDLJoyHatEvent joyHatEvent = new SDLJoyHatEvent();
	static SDLJoyButtonEvent joyButtonEvent = new SDLJoyButtonEvent();
	static SDLResizeEvent resizeEvent = new SDLResizeEvent();
	static SDLExposeEvent exposeEvent = new SDLExposeEvent();
	static SDLQuitEvent quitEvent = new SDLQuitEvent();
	static SDLUserEvent userEvent = new SDLUserEvent();
	static SDLSysWMEvent sysWMEvent = new SDLSysWMEvent();
	static DummyEvent dummyEvent = new DummyEvent();

	////////////////////////////////////////////////////////////////////////////////
	// PUBLIC INTERFACE

	/**
	 * Pumps the event loop, gathering events from the input devices.
	 * <P>
	 * SDL_PumpEvents gathers all the pending input information from devices and places it on the event queue.
	 * Without calls to SDL_PumpEvents no events would ever be placed on the queue. Often the need for calls to
	 * SDL_PumpEvents is hidden from the user since SDL_PollEvent and SDL_WaitEvent implicitly call SDL_PumpEvents.
	 * However, if you are not polling or waiting for events (e.g. you are filtering them), then you must call
	 * SDL_PumpEvents to force an event queue update.
	 * <P>
	 * Note: You can only call this function in the thread that set the video mode. 
	 *
	 * @throws SDLException if an error occurs
	 */
	public static void pumpEvents() throws SDLException {
		SWIG_SDLEvent.SDL_PumpEvents();
	}

	/**
	 * Checks the event queue for messages and optionally returns them.
	 * <p>
	 * If action is SDL_ADDEVENT, up to numevents events will be added to the back of the event queue.
	 * <p>
	 * If action is SDL_PEEKEVENT, up to numevents events at the front of the event queue, matching mask,
	 * will be returned and will not be removed from the queue.
	 * <p>
	 * If action is SDL_GETEVENT, up to numevents events at the front of the event queue, matching mask,
	 * will be returned and will be removed from the queue.
	 * <p>
	 * The mask parameter is a bitwise OR of SDL_EVENTMASK(event_type), for all event types you are interested in.
	 * This function is thread-safe. 
	 *
	 * @param events a <code>List</code> value
	 * @param numevents an <code>int</code> value
	 * @param action One of SDL_ADDEVENT, SDL_PEEKEVENT or SDL_GETEVENT
	 * @param mask an <code>int</code> value
	 *
	 * @return This method returns the number of events actually stored
	 * @throws SDLException if an error occurs
	 */
	//public static native int peepEvents(List events, int numevents, SDLEventAction action, int mask) throws SDLException;
	/**
	 * Polls for currently pending events.
	 * <p>
	 * If returnEvent is true the next event is removed from the queue and returned (if one exists)
	 *
	 * @param  returnEvent specifies if the event should be returned
	 * @return If returnEvent is true: The next event if one is waiting, otherwise null
	 *         <P>If returnEvent is false:  non-null SDLEvent instance if an event is available, otherwise null
	 *
	 * @throws SDLException if an error occurs
	 */
	public static SDLEvent pollEvent(boolean returnEvent) throws SDLException {
		int result;
		if (returnEvent) {
			result = SWIG_SDLEvent.SDL_PollEvent(swigEvent);
			return result == 1 ? processEvent(swigEvent) : null;

		} else {
			result = SWIG_SDLEvent.SDL_PollEvent(null);
			return result == 1 ? dummyEvent : null;
		}
	}

	/**
	 * Polls for currently pending events.
	 * <p>
	 *
	 * @return The next event if one is waiting, otherwise null
	 * @throws SDLException if an error occurs
	 */
	public static SDLEvent pollEvent() throws SDLException {
		return pollEvent(true);
	}

	/**
	 * Waits indefinitely for the next available event
	 * <p>
	 * If returnEvent is true the next event is removed from the queue and returned (if one exists)
	 * <P>Otherwise the method simply returns once the next event is available
	 *
	 * @param returnEvent specifies if the event should be returned
	 * @return The next event if one is waiting and returnEvent is true, otherwise null
	 * @throws SDLException if an error occurs
	 */
	public static SDLEvent waitEvent(boolean returnEvent) throws SDLException {
		if (returnEvent) {
			int result = SWIG_SDLEvent.SDL_WaitEvent(swigEvent);
			if (result == 0) {
				throw new SDLException(SDLMain.getError());
			}
			return processEvent(swigEvent);
		} else {
			int result = SWIG_SDLEvent.SDL_WaitEvent(null);
			if (result == 0) {
				throw new SDLException(SDLMain.getError());
			}
			return null;
		}
	}

	/**
	 * Waits indefinitely for the next available event
	 * <p>
	 * 
	 *
	 * 
	 * @return The next event if one is waiting, otherwise null
	 * @throws SDLException if an error occurs
	 */
	public static SDLEvent waitEvent() throws SDLException {
		return waitEvent(true);
	}

	/**
	 * This function allows you to set the state of processing certain event types.
	 * <P>
	 * If state is set to SDL_IGNORE, that event type will be automatically dropped from the event queue and will not be filtered.
	 * <P>
	 * If state is set to SDL_ENABLE, that event type will be processed normally.
	 * <P>
	 * If state is set to SDL_QUERY, SDL_EventState will return the current processing state of the specified event
	 * type.
	 * <P>
	 * A list of event types can be found in the SDL_Event section. 
	 *
	 * @param type a <code>SDLEventType</code> value
	 * @param state a <code>SDLEventState</code> value
	 * @return a <code>SDLEventState</code> value
	 * @exception SDLException if an error occurs
	 */
	public static SDLEventState eventState(int type, SDLEventState state) throws SDLException {
		short s = SWIG_SDLEvent.SDL_EventState((short) type, (short) state.swigValue());
		return SDLEventState.swigToEnum(s);
	}

	/**
	 * Get the state of modifier keys.
	 *
	 * @return Returns the current state of the modifier keys (CTRL, ALT, etc.)
	 */
	public static SDLMod getModState() {
		int modstate = SWIG_SDLEvent.SDL_GetModState();
		return SDLMod.get(modstate);
	}

	/**
	 * Describe <code>setModState</code> method here.
	 *
	 */
	public static void setModState(SDLMod mod) {
		SWIG_SDLEvent.SDL_SetModState(mod.getState());
	}

	/**
	 * Get the name of an SDL virtual keysym
	 *
	 * @param key a <code>SDLKey</code> value
	 * @return the SDL-defined name of the SDLKey key.
	 */
	public static String getKeyName(int key) {
		return SWIG_SDLEvent.SDL_GetKeyName(key);
	}

	/**
	 * The event queue can actually be used as a two way communication channel.
	 * <p>
	 * Not only can events be read from the queue, but the user can also push their own events onto it.
	 * event is a pointer to the event structure you wish to push onto the queue.
	 * <p>
	 * Note: Pushing device input events onto the queue doesn't modify the state of the device within SDL
	 *
	 * @param event The event to place in the queue
	 * @return a <code>boolean</code> value
	 * @exception SDLException if an error occurs
	 */
	//    public static int pushEvent(SDLUserEvent event) throws SDLException {
	//	SDL_UserEvent userEvent = new SDL_UserEvent();
	//	userEvent.setType((short)event.getType());
	//	userEvent.setData1(event.getData1());
	//	userEvent.setData2(event.getData2());
	//	
	//	SDL_Event e = new SDL_Event();
	//	e.setUser(userEvent);
	//	
	//	return SWIG_SDLEvent.SDL_PushEvent(e);
	//    }
	// not yet implemented
	//public static native void setEventFilter(SDLEventFilter filter) throws SDLException;
	/**
	 * This function allows you to set the state of processing certain event types.
	 * <p>
	 * If state is set to SDL_IGNORE, that event type will be automatically dropped from the event queue
	 * and will not be filtered.
	 * <p>
	 * If state is set to SDL_ENABLE, that event type will be processed normally.
	 * <p>
	 * If state is set to SDL_QUERY, SDL_EventState will return the current processing state of the specified
	 * event type.
	 * <p>
	 * A list of event types can be found in the SDL_Event section.
	 *
	 * @param type The class of the event (eg SDLKeyboardEvent.class)
	 * @param state one of SDL_QUERY, SDL_IGNORE, SDL_DISABLE, SDL_ENABLE
	 * @return an <code>int</code> value
	 * @exception SDLException if an error occurs
	 */
	//public static native int eventState(Class type, int state) throws SDLException;
	/**
	 * Get a snapshot of the current keyboard state.  The current state is returned as a Set.  Within the
	 * set an value which exists (eg.  SDLKeyboardEvent.SDLK_RETURN ) means the key is pressed. If it is
	 * not in the set the key is not in the state of "pressed".
	 *
	 * @return The Set of pressed keys
	 * @exception SDLException if an error occurs
	 */
	//public static native Set getKeyState() throws SDLException;
	/**
	 *  Get the state of modifier keys.
	 *
	 * @return a <code>SDLMod</code> value
	 * @exception SDLException if an error occurs
	 */
	//public static native SDLMod getModState() throws SDLException;
	/**
	 * The inverse of SDL_GetModState, SDL_SetModState allows you to impose modifier key states on your application.
	 *
	 * @param mod a <code>SDLMod</code> value
	 * @exception SDLException if an error occurs
	 */
	//public static native void setModState(SDLMod mod) throws SDLException;
	/**
	 * Returns the SDL-defined name of the SDLKey key.
	 *
	 * @param key a <code>SDLKey</code> value
	 * @return The SDL-defined name of key
	 * @exception SDLException if an error occurs
	 */
	//public static native String getKeyName(SDLKey key) throws SDLException;
	/**
	 * Enables/Disables Unicode keyboard translation.
	 * <p>
	 * To obtain the character codes corresponding to received keyboard events, Unicode translation
	 * must first be turned on using this function. The translation incurs a slight overhead for each
	 * keyboard event and is therefore disabled by default. For each subsequently received key down event,
	 * the unicode member of the SDL_keysym structure will then contain the corresponding character code,
	 * or zero for keysyms that do not correspond to any character code.
	 * <p>
	 * A value of 1 for enable enables Unicode translation; 0 disables it, and -1 leaves it unchanged
	 * (useful for querying the current translation mode).
	 * <p>
	 * Note that only key press events will be translated, not release events. 
	 *
	 * @param enable a <code>boolean</code> value
	 * @return The previous translation mode
	 * @exception SDLException if an error occurs
	 */
	public static int enableUNICODE(int mode) throws SDLException {
		return SWIG_SDLEvent.SDL_EnableUNICODE(mode);
	}

	/**
	 * Enables or disables the keyboard repeat rate. delay specifies how long the key must
	 * be pressed before it begins repeating, it then repeats at the speed specified by interval.
	 * Both delay and interval are expressed in milliseconds.
	 * <p>
	 * Setting delay to 0 disables key repeating completely. Good default values are
	 * SDL_DEFAULT_REPEAT_DELAY and SDL_DEFAULT_REPEAT_INTERVAL
	 *
	 * @param delay an <code>int</code> value
	 * @param interval an <code>int</code> value
	 * @return a <code>boolean</code> value
	 * @exception SDLException if an error occurs
	 */
	public static void enableKeyRepeat(int delay, int interval) throws SDLException {
		int result = SWIG_SDLEvent.SDL_EnableKeyRepeat(delay, interval);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * Retrieve the current state of the mouse
	 *
	 * @return a <code>SDLMouseState</code> value
	 * @exception SDLException if an error occurs
	 */
	public static MouseState getMouseState() throws SDLException {
		int x[] = { 0 };
		int y[] = { 0 };

		int buttons = SWIG_SDLEvent.SDL_GetMouseState(x, y);
		return new MouseState(x[0], y[0], buttons);
	}

	/**
	 * Retrieve the <I><B>relative</B></I> current state of the mouse since the last call to getRelativeMouseState()
	 *
	 * @return    a <code>MouseState</code> value
	 * @exception SDLException if an error occurs
	 */
	public static MouseState getRelativeMouseState() throws SDLException {
		int x[] = { 0 };
		int y[] = { 0 };

		int buttons = SWIG_SDLEvent.SDL_GetRelativeMouseState(x, y);
		return new MouseState(x[0], y[0], buttons);
	}

	/**
	 *
	 * Toggle whether or not the cursor is shown on the screen.
	 * The cursor starts off displayed, but can be turned off.
	 * SDL_ShowCursor() returns true if the cursor was being displayed
	 * before the call, or false if it was not.  You can query the current
	 * state by passing a 'toggle' value of -1.
	 *
	 * @return 1 if the cursor was being displayed before the call, or 0 if it was not.
	 */
	public static boolean showCursor(int toggle) {
		return SWIG_SDLEvent.SDL_ShowCursor(toggle) == 1 ? true : false;
	}

	/**
	 * This function returns the current state of the application.
	 * <p>
	 * The value returned is a bitwise combination of:
	 * <p>
	 * SDL_APPMOUSEFOCUS 	The application has mouse focus.
	 * <p>
	 * SDL_APPINPUTFOCUS 	The application has keyboard focus
	 * <p>
	 * SDL_APPACTIVE 	The application is visible
	 *
	 *
	 * @return an <code>int</code> value
	 * @exception SDLException if an error occurs
	 */
	public static SDLAppState getAppState() throws SDLException {
		return SDLAppState.swigToEnum(SWIG_SDLEvent.SDL_GetAppState());
	}

	/**
	 * This function is used to enable or disable joystick event processing.
	 * With joystick event processing disabled you will have to update joystick states with
	 * SDL_JoystickUpdate and read the joystick information manually.
	 * state is either SDL_QUERY, SDL_ENABLE or SDL_IGNORE.
	 * <p>
	 * Note: Joystick event handling is preferred 
	 *
	 * @param state a <code>SDLEventState</code> value
	 * @return If state is SDL_QUERY then the current state is
	 * returned, otherwise the new processing state is returned.
	 * @exception SDLException if an error occurs
	 */
	public static int joystickEventState(SDLEventState state) throws SDLException {
		return SWIG_SDLEvent.SDL_JoystickEventState(state.swigValue());
	}

	/**
	 * The type of the this event
	 *
	 * @return The type of event
	 */
	public abstract int getType();

	static final SDLEvent processEvent(SDL_Event swigEvent) {
		int type = (int) swigEvent.getType();

		switch (type) {
		// ACTIVE
		case SDL_ACTIVEEVENT:
			activeEvent.setSwigActiveEvent(swigEvent.getActive());
			return activeEvent;

			// KEYBOARD
		case SDL_KEYUP:
		case SDL_KEYDOWN:
			sdlKeyboardEvent.setSwigKeyboardEvent(swigEvent.getKey());
			return sdlKeyboardEvent;

			// MOUSE
		case SDL_MOUSEMOTION:
			mouseMotionEvent.setSwigMouseMotionEvent(swigEvent.getMotion());
			return mouseMotionEvent;

		case SDL_MOUSEBUTTONUP:
		case SDL_MOUSEBUTTONDOWN:
			mouseButtonEvent.setSwigMouseButtonEvent(swigEvent.getButton());
			return mouseButtonEvent;

			// JOYSTICK
		case SDL_JOYAXISMOTION:
			joyAxisEvent.setSwigEvent(swigEvent.getJaxis());
			return joyAxisEvent;

		case SDL_JOYBUTTONUP:
		case SDL_JOYBUTTONDOWN:
			joyButtonEvent.setSwigEvent(swigEvent.getJbutton());
			return joyButtonEvent;

		case SDL_JOYHATMOTION:
			joyHatEvent.setSwigEvent(swigEvent.getJhat());
			return joyHatEvent;

		case SDL_JOYBALLMOTION:
			joyBallEvent.setSwigEvent(swigEvent.getJball());
			return joyBallEvent;

			// RESIZE
		case SDL_VIDEORESIZE:
			resizeEvent.setSwigResizeEvent(swigEvent.getResize());
			return resizeEvent;

			// EXPOSE
		case SDL_VIDEOEXPOSE:
			exposeEvent.setSwigExposeEvent(swigEvent.getExpose());
			return exposeEvent;

			// WM
		case SDL_SYSWMEVENT:
			// NOT YET IMPLEMENTED
			break;

		// USER
		//	    case SDL_USEREVENT:
		//		SDL_UserEvent swigUserEvent = swigEvent.getUser();
		//		userEvent.setType(swigUserEvent.getType());
		//		userEvent.setData1(swigUserEvent.getData1());
		//		userEvent.setData2(swigUserEvent.getData2());
		//		return userEvent;

		// QUIT
		case SDL_QUIT:
			quitEvent.setSwigQuitEvent(swigEvent.getQuit());
			return quitEvent;

		default:
			System.err.println("UNKNOWN event type: " + type);
		}

		return null;
	}

} // class SDLEvent