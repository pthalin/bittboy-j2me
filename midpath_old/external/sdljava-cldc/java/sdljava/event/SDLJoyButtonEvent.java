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
import sdljava.x.swig.SDL_JoyButtonEvent;

/**
 * A SDLJoyButtonEvent event occurs when ever a
 * user presses or releases a button on a joystick. The field which is
 * the index of the joystick that reported the event and button is the
 * index of the button (for a more detailed explanation see the
 * Joystick section). state is the current state or the button which
 * is either SDL_PRESSED or SDL_RELEASED.
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLJoyButtonEvent.java,v 1.5 2005/01/25 02:50:45 ivan_ganza Exp $
 */
public class SDLJoyButtonEvent extends SDLEvent {
	SDL_JoyButtonEvent swigEvent;

	/**
	 * Gets the value of swigEvent
	 *
	 * @return the value of swigEvent
	 */
	public SDL_JoyButtonEvent getSwigEvent() {
		return this.swigEvent;
	}

	/**
	 * Sets the value of swigEvent
	 *
	 * @param argSwigEvent Value to assign to this.swigEvent
	 */
	public void setSwigEvent(SDL_JoyButtonEvent argSwigEvent) {
		this.swigEvent = argSwigEvent;
	}

	/**
	 * Get Which joystick the event occured on
	 *
	 * @return Which joystick the event occured on
	 */
	public int getWhich() {
		return (int) swigEvent.getWhich();
	}

	/**
	 * Get the button that was pressed
	 *
	 * @return the button that was pressed
	 */
	public int getButton() {
		return (int) swigEvent.getButton();
	}

	/**
	 * Get the state of the button
	 *
	 * @return the state of the button
	 */
	public SDLPressedState getState() {
		return SDLPressedState.swigToEnum(swigEvent.getState());
	}

	/**
	 * The type of the this event
	 *
	 * @return The type of event
	 */
	public int getType() {
		return swigEvent.getType();
	}

	/**
	 * Return a string represenation of this object
	 *
	 * @return a String represenation of this object
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("SDLJoyButtonEvent[").append("which=").append(getWhich()).append(", button=").append(getButton())
				.append(", state=").append(getState()).append("]");

		return buf.toString();
	}
}
