package sdljava.cdrom;

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
 * Information about a particular frame position:  the minutes and seconds of
 * where the frame is on the track
 *
 * @author  Ivan Z. Ganza
 * @version $Id: FrameInfo.java,v 1.2 2004/12/29 02:42:47 ivan_ganza Exp $
 */
public class FrameInfo {

	int frame;
	int minutes;
	int seconds;
	int f;

	public FrameInfo(int frame, int minutes, int seconds, int f) {
		this.frame = frame;
		this.minutes = minutes;
		this.seconds = seconds;
		this.f = f;
	}

	/**
	 * Gets the value of frame
	 *
	 * @return the value of frame
	 */
	public int getFrame() {
		return this.frame;
	}

	/**
	 * Sets the value of frame
	 *
	 * @param argFrame Value to assign to this.frame
	 */
	public void setFrame(int argFrame) {
		this.frame = argFrame;
	}

	/**
	 * Gets the value of minutes
	 *
	 * @return the value of minutes
	 */
	public int getMinutes() {
		return this.minutes;
	}

	/**
	 * Sets the value of minutes
	 *
	 * @param argMinutes Value to assign to this.minutes
	 */
	public void setMinutes(int argMinutes) {
		this.minutes = argMinutes;
	}

	/**
	 * Gets the value of seconds
	 *
	 * @return the value of seconds
	 */
	public int getSeconds() {
		return this.seconds;
	}

	/**
	 * Sets the value of seconds
	 *
	 * @param argSeconds Value to assign to this.seconds
	 */
	public void setSeconds(int argSeconds) {
		this.seconds = argSeconds;
	}

	/**
	 * Gets the value of f
	 *
	 * @return the value of f
	 */
	public int getF() {
		return this.f;
	}

	/**
	 * Sets the value of f
	 *
	 * @param argF Value to assign to this.f
	 */
	public void setF(int argF) {
		this.f = argF;
	}

}