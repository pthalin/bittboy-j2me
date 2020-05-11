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
import sdljava.x.swig.SDL_ActiveEvent;

/**
 * When the mouse leaves or enters the window area a SDL_APPMOUSEFOCUS type
 * activation event occurs, if the mouse entered the window then gain will be 1,
 * otherwise gain will be 0. A SDL_APPINPUTFOCUS type activation event occurs when
 * the application loses or gains keyboard focus. This usually occurs when another
 * application is made active. Finally, a SDL_APPACTIVE type event occurs when the
 * application is either minimised/iconified (gain=0) or restored.
 * <p>
 * Note: This event does not occur when an application window is first created. 
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLActiveEvent.java,v 1.8 2005/01/25 02:50:43 ivan_ganza Exp $
 */
public class SDLActiveEvent extends SDLEvent {

    /**
     * Reference to the SWIG generated event
     *
     */
    SDL_ActiveEvent swigActiveEvent;
    
    /**
     * Gets the value of swigActiveEvent
     *
     * @return the value of swigActiveEvent
     */
    public SDL_ActiveEvent getSwigActiveEvent()  {
	return this.swigActiveEvent;
    }

    /**
     * Sets the value of swigActiveEvent
     *
     * @param argSwigActiveEvent Value to assign to this.swigActiveEvent
     */
    public void setSwigActiveEvent(SDL_ActiveEvent argSwigActiveEvent) {
	this.swigActiveEvent = argSwigActiveEvent;
    }

    /**
     * The type of the this event
     *
     * @return The type of event
     */
    public  int getType() {
	return swigActiveEvent.getType();
    }

    /**
     * @return a <code>String</code> representation of myself
     *
     * 
     */
    public String toString() {
	StringBuffer buf = new StringBuffer();

	buf.append("SDLActiveEvent[type=").append(swigActiveEvent.getType()).
	    append(", gain=").append(swigActiveEvent.getGain() == 0 ? "LOSS" : "GAIN").
	    append(", state=").append(translateState(swigActiveEvent.getState())).append("]");

	return buf.toString();
    }

    String translateState(int state) {
	switch (state) {
	    case SDL_APPMOUSEFOCUS: return "Focus";
	    case SDL_APPINPUTFOCUS: return "Input Focus";
	    case SDL_APPACTIVE:     return "Application Iconified";
	    default: return "Unknown state[" + state + "]";
	}
    }

    /**
     * The app has mouse coverage
     *
     */
    public static final int SDL_APPMOUSEFOCUS = 0x01;
    
    /**
     * The app has input focus
     *
     */
    public static final int SDL_APPINPUTFOCUS = 0x02;

    /**
     * The application is active
     *
     */
    public static final int SDL_APPACTIVE     = 0x04;
}
