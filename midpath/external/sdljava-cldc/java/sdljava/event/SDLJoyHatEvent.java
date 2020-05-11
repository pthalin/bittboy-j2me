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
import sdljava.joystick.HatState;
import sdljava.x.swig.SDL_JoyHatEvent;

/**
 * A SDLJoyHatEvent event occurs when ever a user moves a hat on the
 * joystick. The field which is the index of the joystick that
 * reported the event and hat is the index of the hat (for a more
 * detailed explanation see the Joystick section). value is the
 * current position of the hat. It is a bitwise OR'd combination of
 * the following values (whose meanings should be pretty obvious):
 * <P>
 *  SDL_HAT_CENTERED<br>
 *  SDL_HAT_UP<br>
 *  SDL_HAT_RIGHT<br>
 *  SDL_HAT_DOWN<br>
 *  SDL_HAT_LEFT<br>
 * <br><P>
 *  The following defines are also provided:<br>
 *  SDL_HAT_RIGHTUP<br>
 *  SDL_HAT_RIGHTDOWN<br>
 *  SDL_HAT_LEFTUP<br>
 *  SDL_HAT_LEFTDOWN<br>
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLJoyHatEvent.java,v 1.5 2005/01/25 02:50:45 ivan_ganza Exp $
 */
public class SDLJoyHatEvent extends SDLEvent {
	SDL_JoyHatEvent swigEvent;

	/**
	 * Gets the value of swigEvent
	 *
	 * @return the value of swigEvent
	 */
	public SDL_JoyHatEvent getSwigEvent() {
		return this.swigEvent;
	}

	/**
	 * Sets the value of swigEvent
	 *
	 * @param argSwigEvent Value to assign to this.swigEvent
	 */
	public void setSwigEvent(SDL_JoyHatEvent argSwigEvent) {
		this.swigEvent = argSwigEvent;
	}

	public int getWhich() {
		return (int) swigEvent.getWhich();
	}

	public int getHat() {
		return (int) swigEvent.getHat();
	}

	public HatState getValue() {
		return HatState.get((int) swigEvent.getValue());
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

		buf.append("SDLJoyHatEvent[").append("which=").append(getWhich()).append(", hat=").append(getHat()).append(
				", value=").append(getValue()).append("]");

		return buf.toString();
	}
}
