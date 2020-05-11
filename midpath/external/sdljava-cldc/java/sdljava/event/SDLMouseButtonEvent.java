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
import sdljava.x.swig.SDL_MouseButtonEvent;

/**
 * When a mouse button press or release is detected then number of the button pressed
 * (from 1 to 255, with 1 usually being the left button and 2 the right) is placed into
 * button, the position of the mouse when this event occured is stored in the x and the y fields.
 * Like SDL_KeyboardEvent, information on whether the event was a press or a release event is stored
 * in both the type and state fields, but this should be obvious.
 * <p>
 * Mouse wheel events are reported as buttons 4 (up) and 5 (down).
 * Two events are generated i.e. you get a SDL_MOUSEBUTTONDOWN followed by a SDL_MOUSEBUTTONUP event. 
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLMouseButtonEvent.java,v 1.12 2005/01/30 05:15:16 ivan_ganza Exp $
 */
public class SDLMouseButtonEvent extends SDLEvent {

	/**
	 * Reference to the SWIG generated
	 *
	 */
	SDL_MouseButtonEvent swigMouseButtonEvent;

	/**
	 * Gets the value of swigMouseButtonEvent
	 *
	 * @return the value of swigMouseButtonEvent
	 */
	public SDL_MouseButtonEvent getSwigMouseButtonEvent() {
		return this.swigMouseButtonEvent;
	}

	/**
	 * Sets the value of swigMouseButtonEvent
	 *
	 * @param argSwigMouseButtonEvent Value to assign to this.swigMouseButtonEvent
	 */
	public void setSwigMouseButtonEvent(SDL_MouseButtonEvent argSwigMouseButtonEvent) {
		this.swigMouseButtonEvent = argSwigMouseButtonEvent;
	}

	/**
	 * Describe <code>getWhich</code> method here.
	 *
	 * @return a <code>short</code> value
	 */
	public int getWhich() {
		return (int) swigMouseButtonEvent.getWhich();
	}

	/**
	 * The mouse button index (SDL_BUTTON_LEFT, SDL_BUTTON_MIDDLE,
	 * SDL_BUTTON_RIGHT, SDL_BUTTON_WHEELUP, SDL_BUTTON_WHEELDOWN)
	 *
	 * @return a <code>short</code> value
	 */
	public int getButton() {
		return (int) swigMouseButtonEvent.getButton();
	}

	/**
	 * Get the state of the button
	 *
	 * @return the state of the button
	 */
	public SDLPressedState getState() {
		return SDLPressedState.swigToEnum(swigMouseButtonEvent.getState());
	}

	/**
	 * The X co-oridnate
	 *
	 * @return an <code>int</code> value
	 */
	public int getX() {
		return swigMouseButtonEvent.getX();
	}

	/**
	 * The Y co-ordinate
	 *
	 * @return an <code>int</code> value
	 */
	public int getY() {
		return swigMouseButtonEvent.getY();
	}

	/**
	 * The type of the this event
	 *
	 * @return The type of event
	 */
	public int getType() {
		return swigMouseButtonEvent.getType();
	}

	/**
	 * Get string represenation of button value
	 *
	 * @param button an <code>int</code> value
	 * @return the string represenation of button value
	 */
	public String translateButton(int button) {
		switch (button) {
		case SDL_BUTTON_LEFT:
			return "Left";
		case SDL_BUTTON_MIDDLE:
			return "Middle";
		case SDL_BUTTON_RIGHT:
			return "Right";
		case SDL_BUTTON_WHEELUP:
			return "WheelUP";
		case SDL_BUTTON_WHEELDOWN:
			return "WheelDOWN";
		default:
			return "unknown button";
		}
	}

	/**
	 * @return a <code>String</code> representation of myself
	 *
	 * 
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("SDLMouseButtonEvent[").append(", witch=").append(swigMouseButtonEvent.getWhich())
				.append(", state=").append(getState()).append(", button=").append(
						translateButton(swigMouseButtonEvent.getButton())).append(", x=").append(
						swigMouseButtonEvent.getX()).append(", y=").append(swigMouseButtonEvent.getY()).append(", ")
				.append(getButton()).append("]");

		return buf.toString();
	}
}
