package sdljava.joystick;

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

import sdljava.x.swig.SWIG_SDLJoystickConstants;

/**
 * Describe the state of a joystick hat.
 *
 * @author Ivan Z. Ganza
 * @version $Id: HatState.java,v 1.4 2005/01/19 03:09:12 ivan_ganza Exp $
 */
public class HatState {

	public final static int SDL_HAT_CENTERED = SWIG_SDLJoystickConstants.SDL_HAT_CENTERED;
	public final static int SDL_HAT_UP = SWIG_SDLJoystickConstants.SDL_HAT_UP;
	public final static int SDL_HAT_RIGHT = SWIG_SDLJoystickConstants.SDL_HAT_RIGHT;
	public final static int SDL_HAT_DOWN = SWIG_SDLJoystickConstants.SDL_HAT_DOWN;
	public final static int SDL_HAT_LEFT = SWIG_SDLJoystickConstants.SDL_HAT_LEFT;
	public final static int SDL_HAT_RIGHTUP = (SDL_HAT_RIGHT | SDL_HAT_UP);
	public final static int SDL_HAT_RIGHTDOWN = (SDL_HAT_RIGHT | SDL_HAT_DOWN);
	public final static int SDL_HAT_LEFTUP = (SDL_HAT_LEFT | SDL_HAT_UP);
	public final static int SDL_HAT_LEFTDOWN = (SDL_HAT_LEFT | SDL_HAT_DOWN);

	/**
	 * cache of HatState instances, one for each possible
	 * mods values
	 *
	 */
	static Hashtable modCache = new Hashtable();

	int state;

	public HatState(int state) {
		this.state = state;
	}

	/**
	 * Get the HatState instance identified by state.  This method
	 * creates the HatState instance and caches it if it didn't
	 * already exist.  Once created we won't need to create new
	 * HatState object instances each time a keyboard event occurs.
	 *
	 * @param mods valid key mods (possibly OR'd together)
	 * @return The singleton HatState instance
	 */
	public static HatState get(int state) {
		HatState hat = (HatState) modCache.get(new Integer(state));
		if (hat != null)
			return hat;

		hat = new HatState(state);
		modCache.put(new Integer(state), hat);
		return hat;
	}

	public boolean hatCentered() {
		return ((state & SDL_HAT_CENTERED) != 0);
	}

	public boolean hatUp() {
		return ((state & SDL_HAT_UP) != 0);
	}

	public boolean hatRight() {
		return ((state & SDL_HAT_RIGHT) != 0);
	}

	public boolean hatDown() {
		return ((state & SDL_HAT_DOWN) != 0);
	}

	public boolean hatLeft() {
		return ((state & SDL_HAT_LEFT) != 0);
	}

	public boolean hatRightUp() {
		return ((state & SDL_HAT_RIGHTUP) != 0);
	}

	public boolean hatRightDown() {
		return ((state & SDL_HAT_RIGHTDOWN) != 0);
	}

	public boolean hatLeftUp() {
		return ((state & SDL_HAT_LEFTUP) != 0);
	}

	public boolean hatLeftDown() {
		return ((state & SDL_HAT_LEFTDOWN) != 0);
	}

	/**
	 * Return a string represenation of this object
	 *
	 * @return a String represenation of this object
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("HatState[");

		if (hatCentered())
			buf.append(", centered");
		if (hatUp())
			buf.append(", up");
		if (hatRight())
			buf.append(", right");
		if (hatDown())
			buf.append(", down");
		if (hatLeft())
			buf.append(", left");
		if (hatRightUp())
			buf.append(", right-up");
		if (hatRightDown())
			buf.append(", right-down");
		if (hatLeftUp())
			buf.append(", left-up");
		if (hatLeftDown())
			buf.append(", left-down");

		buf.append("]");
		return buf.toString();
	}
}