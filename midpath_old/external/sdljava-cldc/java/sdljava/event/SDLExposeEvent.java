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
import sdljava.x.swig.SDL_ExposeEvent;

/**
 * SDL_ExposeEvent is a member of the SDL_Event union and is used whan an event of type SDL_VIDEOEXPOSE is reported.
 * <P>
 * A VIDEOEXPOSE event is triggered when the screen has been modified
 * outside of the application, usually by the window manager and needs to
 * be redrawn.
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLExposeEvent.java,v 1.4 2005/01/25 02:50:44 ivan_ganza Exp $
 */
public class SDLExposeEvent extends SDLEvent {
	SDL_ExposeEvent swigExposeEvent;

	/**
	 * Gets the value of swigExposeEvent
	 *
	 * @return the value of swigExposeEvent
	 */
	public SDL_ExposeEvent getSwigExposeEvent() {
		return this.swigExposeEvent;
	}

	/**
	 * Sets the value of swigExposeEvent
	 *
	 * @param argSwigExposeEvent Value to assign to this.swigExposeEvent
	 */
	public void setSwigExposeEvent(SDL_ExposeEvent argSwigExposeEvent) {
		this.swigExposeEvent = argSwigExposeEvent;
	}

	/**
	 * The type of the this event
	 *
	 * @return The type of event
	 */
	public int getType() {
		return swigExposeEvent.getType();
	}
}
