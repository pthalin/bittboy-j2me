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
 * THIS CLASS NOT CURRENTLY USED - SOON
 *
 * This event is unique, it is never created by SDL but only by the
 * user. The event can be pushed onto the event queue using
 * SDL_PushEvent. The contents of the structure members or completely
 * up to the programmer, the only requirement is that type is a value
 * from SDL_USEREVENT to SDL_NUMEVENTS-1 (inclusive).
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLUserEvent.java,v 1.7 2005/09/25 17:55:46 ivan_ganza Exp $
 */
public class SDLUserEvent extends SDLEvent {

	short type;
	Object data1;
	Object data2;

	public SDLUserEvent() {
	}

	public SDLUserEvent(short type) {
		this.type = type;
	}

	public SDLUserEvent(short type, Object data1) {
		this(type);
		this.data1 = data1;

	}

	public SDLUserEvent(short type, Object data1, Object data2) {
		this(type, data1);
		this.data2 = data2;
	}

	/**
	 * Set the type of event.  Must be between SDL_USEREVENT and SDL_NUMEVENTS-1 (inclusive).
	 *
	 * @param t an <code>int</code> value
	 */
	public void setType(short t) {
		//if (type < SDLEvent.SDL_USEREVENT || type > SDLEvent.SDL_NUMEVENTS-1) throw new IllegalArgumentException("type must be between SDL_USEREVENT and SDL_NUMEVENTS-1 (inclusive).  Type was: [" + type + "]");
		this.type = t;
	}

	/**
	 * The type of the this event
	 *
	 * @return The type of event
	 */
	public int getType() {
		return this.type;
	}

	/**
	 * Gets the value of data1
	 *
	 * @return the value of data1
	 */
	public Object getData1() {
		return this.data1;
	}

	/**
	 * Sets the value of data1
	 *
	 * @param argData1 Value to assign to this.data1
	 */
	public void setData1(Object argData1) {
		this.data1 = argData1;
	}

	/**
	 * Gets the value of data2
	 *
	 * @return the value of data2
	 */
	public Object getData2() {
		return this.data2;
	}

	/**
	 * Sets the value of data2
	 *
	 * @param argData2 Value to assign to this.data2
	 */
	public void setData2(Object argData2) {
		this.data2 = argData2;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("SDLUserEvent[type=").append(type).append(",data1=").append(data1.getClass().getName()).append(
				", data2=").append(data2.getClass().getName()).append("]");

		return buf.toString();
	}
}
