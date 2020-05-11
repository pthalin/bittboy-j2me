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
import sdljava.x.swig.SDLPressedState;
import sdljava.x.swig.SDL_KeyboardEvent;
import sdljava.x.swig.SDL_keysym;

/**
 * A keyboard event occurs when a key is released (type=SDK_KEYUP or
 * state=SDL_RELEASED) and when a key is pressed (type=SDL_KEYDOWN or
 * state=SDL_PRESSED). The information on what key was pressed or
 * released is in the keysym member.
 * <p>
 * Note: Repeating SDL_KEYDOWN events will occur if key repeat is enabled (see
 * SDL_EnableKeyRepeat).
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLKeyboardEvent.java,v 1.13 2005/01/25 02:50:45 ivan_ganza Exp $
 */
public class SDLKeyboardEvent extends SDLEvent {

	/**
	 * Reference to the SWIG generated Keyboard Event
	 *
	 */
	SDL_KeyboardEvent swigKeyboardEvent;

	short cachedScancode;
	int cachedSym;
	SDLMod cachedMod;
	int cachedUnicode;

	/**
	 * Gets the value of swigKeyboardEvent
	 *
	 * @return the value of swigKeyboardEvent
	 */
	public SDL_KeyboardEvent getSwigKeyboardEvent() {
		return this.swigKeyboardEvent;
	}

	/**
	 * Sets the value of swigKeyboardEvent
	 *
	 * @param argSwigKeyboardEvent Value to assign to this.swigKeyboardEvent
	 */
	public void setSwigKeyboardEvent(SDL_KeyboardEvent argSwigKeyboardEvent) {
		this.swigKeyboardEvent = argSwigKeyboardEvent;

		// unwrap the keysym for quick access
		SDL_keysym keysym = swigKeyboardEvent.getKeysym();
		cachedScancode = keysym.getScancode();
		cachedSym = keysym.getSym();
		cachedMod = SDLMod.get(keysym.getMod());
		cachedUnicode = keysym.getUnicode();
	}

	/**
	 * Return the Hardware specific scancode
	 *
	 * @return Hardware specific scancode
	 */
	public short getScancode() {
		return cachedScancode;
	}

	/**
	 * SDL virtual keysym
	 *
	 * @return The SDL virtual keysym
	 */
	public int getSym() {
		return cachedSym;
	}

	/**
	 * Translated Unicode character
	 *
	 * @return The Translated Unicode character
	 */
	public int getUnicode() {
		return cachedUnicode;
	}

	/**
	 * Current key modifiers
	 *
	 * @return The Current key modifiers
	 */
	public SDLMod getMod() {
		return cachedMod;
	}

	/**
	 * Get the state of the button
	 *
	 * @return the state of the button
	 */
	public SDLPressedState getState() {
		return SDLPressedState.swigToEnum(swigKeyboardEvent.getState());
	}

	/**
	 * The type of the this event
	 *
	 * @return The type of event
	 */
	public int getType() {
		return swigKeyboardEvent.getType();
	}

	/**
	 * @return a <code>String</code> representation of myself
	 *
	 * 
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("SDLKeyboardEvent[type=").append(swigKeyboardEvent.getType()).append(", state=").append(getState())
				.append(", keysym=").append(getSym()).append(" (").append(SDLEvent.getKeyName(getSym())).append(")")
				.append(", mod=").append(getMod()).append("]");

		return buf.toString();
	}

} // class SDLKeyboardEvent
