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
import sdljava.x.swig.SDL_QuitEvent;

/**
 * As can be seen, the SDL_QuitEvent structure serves no useful
 * purpose. The event itself, on the other hand, is very important. If
 * you filter out or ignore a quit event then it is impossible for the
 * user to close the window. On the other hand, if you do accept a quit
 * event then the application window will be closed, and screen updates
 * will still report success event though the application will no
 * longer be visible.
 * @author Ivan Z. Ganza
 * @version $Id: SDLQuitEvent.java,v 1.7 2005/01/25 02:50:45 ivan_ganza Exp $
 */
public class SDLQuitEvent extends SDLEvent {
	SDL_QuitEvent swigQuitEvent;

	/**
	 * Gets the value of swigQuitEvent
	 *
	 * @return the value of swigQuitEvent
	 */
	public SDL_QuitEvent getSwigQuitEvent() {
		return this.swigQuitEvent;
	}

	/**
	 * Sets the value of swigQuitEvent
	 *
	 * @param argSwigQuitEvent Value to assign to this.swigQuitEvent
	 */
	public void setSwigQuitEvent(SDL_QuitEvent argSwigQuitEvent) {
		this.swigQuitEvent = argSwigQuitEvent;
	}

	/**
	 * The type of the this event
	 *
	 * @return The type of event
	 */
	public int getType() {
		return swigQuitEvent.getType();
	}
}
