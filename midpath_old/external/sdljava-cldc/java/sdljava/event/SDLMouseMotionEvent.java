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
import sdljava.x.swig.SDL_MouseMotionEvent;

/**
 * Simply put, a SDL_MOUSEMOTION type event occurs when a user
 * moves the mouse within the application window or when SDL_WarpMouse
 * is called. Both the absolute (x and y) and relative (xrel and yrel) coordinates
 * are reported along with the current button states (state). The button state can be
 * interpreted using the SDL_BUTTON macro (see SDL_GetMouseState).
 * <p>
 * If the cursor is hidden (SDL_ShowCursor(0)) and the input is grabbed
 * (SDL_WM_GrabInput(SDL_GRAB_ON)), then the mouse will give relative motion
 * events even when the cursor reaches the edge of the screen. This is currently only
 * implemented on Windows and Linux/Unix-alikes. 
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLMouseMotionEvent.java,v 1.10 2005/02/10 04:19:45 ivan_ganza Exp $ 
 */
public class SDLMouseMotionEvent extends SDLEvent {

	/**
	 * Reference to the SWIG generated
	 *
	 */
	SDL_MouseMotionEvent swigMouseMotionEvent;

	int x, y;

	/**
	 * Gets the value of swigMouseMotionEvent
	 *
	 * @return the value of swigMouseMotionEvent
	 */
	public SDL_MouseMotionEvent getSwigMouseMotionEvent() {
		return this.swigMouseMotionEvent;
	}

	/**
	 * Sets the value of swigMouseMotionEvent
	 *
	 * @param argSwigMouseMotionEvent Value to assign to this.swigMouseMotionEvent
	 */
	public void setSwigMouseMotionEvent(SDL_MouseMotionEvent argSwigMouseMotionEvent) {
		this.swigMouseMotionEvent = argSwigMouseMotionEvent;
		x = swigMouseMotionEvent.getX();
		y = swigMouseMotionEvent.getY();
	}

	/**
	 * Describe <code>getWhich</code> method here.
	 *
	 * @return a <code>short</code> value
	 */
	public int getWhich() {
		return (int) swigMouseMotionEvent.getWhich();
	}

	/**
	 * The current button state
	 *
	 * @return a <code>short</code> value
	 */
	public MouseButtonState getState() {
		return MouseButtonState.get((int) swigMouseMotionEvent.getState());
	}

	/**
	 * The X co-ordinate
	 *
	 * @return an <code>int</code> value
	 */
	public int getX() {
		return x;
	}

	public void setX(int newX) {
		this.x = newX;
	}

	/**
	 * The Y co-oridante
	 *
	 * @return an <code>int</code> value
	 */
	public int getY() {
		return y;
	}

	public void setY(int newY) {
		this.y = newY;
	}

	/**
	 * The relative X motion
	 *
	 * @return a <code>short</code> value
	 */
	public int getXrel() {
		return (int) swigMouseMotionEvent.getXrel();
	}

	/**
	 * THe relative Y motion
	 *
	 * @return a <code>short</code> value
	 */
	public int getYrel() {
		return (int) swigMouseMotionEvent.getYrel();
	}

	/**
	 * The type of the this event
	 *
	 * @return The type of event
	 */
	public int getType() {
		return swigMouseMotionEvent.getType();
	}

	/**
	 * @return a <code>String</code> representation of myself
	 *
	 * 
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("SDLMouseMotionEvent[").append("witch=").append(swigMouseMotionEvent.getWhich()).append(", x=")
				.append(swigMouseMotionEvent.getX()).append(", y=").append(swigMouseMotionEvent.getY()).append(
						", xrel=").append(swigMouseMotionEvent.getXrel()).append(", yref=").append(
						swigMouseMotionEvent.getYrel()).append(", statev=").append(swigMouseMotionEvent.getState())
				.append(", state=").append(getState()).append("]");

		return buf.toString();
	}
}
