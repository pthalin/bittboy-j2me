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

/**
 * Platform-dependent window manager event.  <P> The system window
 * manager event contains a pointer to system-specific information
 * about unknown window manager events. If you enable this event using
 * SDL_EventState, it will be generated whenever unhandled events are
 * received from the window manager. This can be used, for example, to
 * implement cut-and-paste in your application.
 * <P>
 * If you want to obtain system-specific information about the window
 * manager, you can fill in the version member of a SDL_SysWMinfo
 * structure (details can be found in SDL_syswm.h, which must be
 * included) using the SDL_VERSION() macro found in SDL_version.h, and
 * pass it to the function:
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLSysWMEvent.java,v 1.4 2005/01/25 02:50:45 ivan_ganza Exp $
 */
public class SDLSysWMEvent extends SDLEvent {
	/**
	 * The type of the this event
	 *
	 * @return The type of event
	 */
	public int getType() {
		return -1;
	}
}
