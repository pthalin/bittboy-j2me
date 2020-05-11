package sdljava.event;
/**
 *  sdljava - a java binding to the SDL API
 *
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
 * Interface for handling SDLEvents.
 *
 * @author  Bart Lebeouf
 * @version $Id: SDLEventListener.java,v 1.2 2005/01/29 05:09:14 ivan_ganza Exp $
 */
public interface SDLEventListener  {
    
    /**
     * Called when an SDLEvent occurs
     *
     * @param event a <code>SDLEvent</code> value
     */
    public void handleEvent(SDLEvent event);
}
