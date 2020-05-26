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
import sdljava.x.swig.SDL_JoyBallEvent;

/**
 * A SDLJoyBallEvent event occurs when a user moves a trackball on
 * the joystick. The field which is the index of the joystick that
 * reported the event and ball is the index of the trackball (for a
 * more detailed explanation see the Joystick section). Trackballs
 * only return relative motion, this is the change in position on the
 * ball since it was last polled (last cycle of the event loop) and it
 * is stored in xrel and yrel.
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLJoyBallEvent.java,v 1.5 2005/01/25 02:50:44 ivan_ganza Exp $
 */
public class SDLJoyBallEvent extends SDLEvent {
	SDL_JoyBallEvent swigEvent;

	/**
	 * Gets the value of swigEvent
	 *
	 * @return the value of swigEvent
	 */
	public SDL_JoyBallEvent getSwigEvent() {
		return this.swigEvent;
	}

	/**
	 * Sets the value of swigEvent
	 *
	 * @param argSwigEvent Value to assign to this.swigEvent
	 */
	public void setSwigEvent(SDL_JoyBallEvent argSwigEvent) {
		this.swigEvent = argSwigEvent;
	}

	public int getWhich() {
		return (int) swigEvent.getWhich();
	}

	public int getBall() {
		return (int) swigEvent.getBall();
	}

	public int getXRelative() {
		return (int) swigEvent.getXrel();
	}

	public int getYRelative() {
		return (int) swigEvent.getYrel();
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

		buf.append("SDLJoyBallEvent[").append("which=").append(getWhich()).append(", ball=").append(getBall()).append(
				", xrel=").append(getXRelative()).append(", yrel=").append(getYRelative()).append("]");

		return buf.toString();
	}
}
