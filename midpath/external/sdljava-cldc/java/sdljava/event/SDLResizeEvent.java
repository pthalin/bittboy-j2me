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
import sdljava.x.swig.SDL_ResizeEvent;

/**
 * SDL_ResizeEvent is a member of the SDL_Event union and is used when an event of type SDL_VIDEORESIZE is reported.
 * <P>
 * When SDL_RESIZABLE is passed as a flag to SDL_SetVideoMode the user is
 * allowed to resize the applications window. When the window is resized
 * an SDL_VIDEORESIZE is reported, with the new window width and height
 * values stored in w and h respectively. When an SDL_VIDEORESIZE is
 * recieved the window should be resized to the new dimensions using
 * SDL_SetVideoMode.
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLResizeEvent.java,v 1.4 2005/01/25 02:50:45 ivan_ganza Exp $
 */
public class SDLResizeEvent extends SDLEvent {
	SDL_ResizeEvent swigResizeEvent;

	/**
	 * Gets the value of swigResizeEvent
	 *
	 * @return the value of swigResizeEvent
	 */
	public SDL_ResizeEvent getSwigResizeEvent() {
		return this.swigResizeEvent;
	}

	/**
	 * Sets the value of swigResizeEvent
	 *
	 * @param argSwigResizeEvent Value to assign to this.swigResizeEvent
	 */
	public void setSwigResizeEvent(SDL_ResizeEvent argSwigResizeEvent) {
		this.swigResizeEvent = argSwigResizeEvent;
	}

	/**
	 * Get the new width
	 *
	 * @return the new width
	 */
	public int getWidth() {
		return swigResizeEvent.getW();
	}

	/**
	 * Get the new height
	 *
	 * @return the new height
	 */
	public int getHeight() {
		return swigResizeEvent.getH();
	}

	/**
	 * The type of the this event
	 *
	 * @return The type of event
	 */
	public int getType() {
		return swigResizeEvent.getType();
	}
}
