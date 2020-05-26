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
import sdljava.x.swig.SDL_JoyAxisEvent;

/**
 * A SDLJoyAxisEvent event occurs whenever a user moves an axis on
 * the joystick. The field which is the index of the joystick that
 * reported the event and axis is the index of the axis (for a more
 * detailed explaination see the Joystick section). value is the
 * current position of the axis.
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLJoyAxisEvent.java,v 1.5 2005/01/25 02:50:44 ivan_ganza Exp $
 */
public class SDLJoyAxisEvent extends SDLEvent {

	SDL_JoyAxisEvent swigEvent;

	/**
	 * Gets the value of swigEvent
	 *
	 * @return the value of swigEvent
	 */
	public SDL_JoyAxisEvent getSwigEvent() {
		return this.swigEvent;
	}

	/**
	 * Sets the value of swigEvent
	 *
	 * @param argSwigEvent Value to assign to this.swigEvent
	 */
	public void setSwigEvent(SDL_JoyAxisEvent argSwigEvent) {
		this.swigEvent = argSwigEvent;
	}

	public int getWhich() {
		return (int) swigEvent.getWhich();
	}

	public int getAxis() {
		return (int) swigEvent.getAxis();
	}

	public int getValue() {
		return (int) swigEvent.getValue();
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

		buf.append("SDLJoyAxisEvent[").append("which=").append(getWhich()).append(", axis=").append(getAxis()).append(
				", value=").append(getValue()).append("]");

		return buf.toString();
	}
}
