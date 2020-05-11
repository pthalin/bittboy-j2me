package sdljava.event;

/**
 *  sdljava - a java binding to the SDL API
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
import java.util.Hashtable;

import sdljava.x.swig.SWIG_SDLEventConstants;

/**
 * The current state of the mouse buttons.
 *
 * 
 * @version $Id: MouseButtonState.java,v 1.4 2005/01/19 03:09:12 ivan_ganza Exp $
 */
public class MouseButtonState {

	public final static int SDL_BUTTON_LEFT = SWIG_SDLEventConstants.SDL_BUTTON_LEFT;
	public final static int SDL_BUTTON_MIDDLE = SWIG_SDLEventConstants.SDL_BUTTON_MIDDLE;
	public final static int SDL_BUTTON_RIGHT = SWIG_SDLEventConstants.SDL_BUTTON_RIGHT;
	public final static int SDL_BUTTON_WHEELUP = SWIG_SDLEventConstants.SDL_BUTTON_WHEELUP;
	public final static int SDL_BUTTON_WHEELDOWN = SWIG_SDLEventConstants.SDL_BUTTON_WHEELDOWN;

	/**
	 * cache of MouseButtonState instances, one for each possible
	 * mods values
	 *
	 */
	static Hashtable buttonStateCache = new Hashtable();

	/**
	 * Current button state values (possibly ORed together)
	 *
	 */
	int buttons;

	/**
	 * Creates a new <code>MouseButtonState</code> instance.
	 *
	 * @param buttons an <code>int</code> value
	 */
	protected MouseButtonState(int buttons) {
		this.buttons = buttons;
	}

	/**
	 * Get the MouseButtonState instance identified by mods.  This method
	 * creates the MouseButtonState instance and caches it if it didn't
	 * already exist.  Once created we won't need to create new
	 * MouseButtonState object instances each time a keyboard event occurs.
	 *
	 * @param mods valid button state (possibly OR'd together)
	 * @return The singleton MouseButtonState instance
	 */
	public static MouseButtonState get(int buttons) {
		MouseButtonState mod = (MouseButtonState) buttonStateCache.get(new Integer(buttons));
		if (mod != null)
			return mod;

		mod = new MouseButtonState(buttons);
		buttonStateCache.put(new Integer(buttons), mod);
		return mod;
	}

	/**
	 * Get if the left mouse button is pressed
	 *
	 * @return if the left mouse button is pressed
	 */
	public boolean buttonLeft() {
		return ((buttons & button(SDL_BUTTON_LEFT)) != 0);
	}

	/**
	 * Get if the middle mouse button is pressed
	 *
	 * @return if the middle mouse button is pressed
	 */
	public boolean buttonMiddle() {
		return ((buttons & button(SDL_BUTTON_MIDDLE)) != 0);
	}

	/**
	 * Get if the right mouse button is pressed
	 *
	 * @return if the middle mouse button is pressed
	 */
	public boolean buttonRight() {
		return ((buttons & button(SDL_BUTTON_RIGHT)) != 0);
	}

	/**
	 * Get if the wheel is up
	 *
	 * @return if the wheel is in the up
	 */
	public boolean wheelUp() {
		return ((buttons & button(SDL_BUTTON_WHEELUP)) != 0);
	}

	/**
	 * Get if the wheel is down
	 *
	 * @return if the wheel is down
	 */
	public boolean wheelDown() {
		return ((buttons & button(SDL_BUTTON_WHEELDOWN)) != 0);
	}

	public int getState() {
		return this.buttons;
	}

	final int button(int x) {
		return (SDLEvent.SDL_PRESSED << (x - 1));
	}

	/**
	 * Return a string represenation of this object
	 *
	 * @return a String represenation of this object
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("MouseButtonState[");

		if (buttonLeft())
			buf.append(", buttonLeft");
		if (buttonMiddle())
			buf.append(", buttonMiddle");
		if (buttonRight())
			buf.append(", buttonRight");
		if (wheelUp())
			buf.append(", wheelUP");
		if (wheelDown())
			buf.append(", wheelDown");
		buf.append("]");

		return buf.toString();
	}
}