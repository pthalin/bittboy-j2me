package sdljava.joystick;

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
import sdljava.SDLException;
import sdljava.SDLMain;
import sdljava.x.swig.SWIGTYPE_p__SDL_Joystick;
import sdljava.x.swig.SWIG_SDLJoystick;

/**
 * Binding to the SDL Joystick routines.
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLJoystick.java,v 1.3 2005/01/19 03:09:12 ivan_ganza Exp $
 */
public class SDLJoystick {

	SWIGTYPE_p__SDL_Joystick swigJoystick;

	private SDLJoystick(SWIGTYPE_p__SDL_Joystick swigJoystick) {
		this.swigJoystick = swigJoystick;
	}

	/**
	 * Count available joysticks.
	 * <P>
	 * Counts the number of joysticks attached to the system.
	 *
	 * @return Returns the number of attached joysticks
	 */
	public static int numJoysticks() {
		return SWIG_SDLJoystick.SDL_NumJoysticks();
	}

	/**
	 * Get joystick name
	 * <P>
	 * Get the implementation dependent name of joystick. The index
	 * parameter refers to the N'th joystick on the system.
	 *
	 * @return The joystick name
	 */
	public static String joystickName(int index) {
		return SWIG_SDLJoystick.SDL_JoystickName(index);
	}

	/**
	 * Opens a joystick for use.  A joystick must be opened before it can be used
	 *
	 * @param index index refers to the N'th joystick in the system
	 * @return The joystick
	 * @exception SDLException if an error occured
	 */
	public static SDLJoystick joystickOpen(int index) throws SDLException {
		SWIGTYPE_p__SDL_Joystick joystick = SWIG_SDLJoystick.SDL_JoystickOpen(index);
		if (joystick == null) {
			throw new SDLException(SDLMain.getError());
		}
		return new SDLJoystick(joystick);
	}

	/**
	 * Determine if a joystick has been opened
	 * <P>
	 * Determines whether a joystick has already been opened within the application. 
	 *
	 * @param index index refers to the N'th joystick on the system.
	 * @return if the joystick at index has been opened
	 */
	public static boolean joystickOpened(int index) {
		return SWIG_SDLJoystick.SDL_JoystickOpened(index) == 1;
	}

	/**
	 * Updates the state of all joysticks <P> Updates the
	 * state(position, buttons, etc.) of all open joysticks. If
	 * joystick events have been enabled with SDL_JoystickEventState
	 * then this is called automatically in the event loop.
	 *
	 */
	public static void joystickUpdate() {
		SWIG_SDLJoystick.SDL_JoystickUpdate();
	}

	/**
	 * Get the index of an SDL_Joystick.
	 *
	 * @return Index number of the joystick.
	 */
	public int joystickIndex() {
		return SWIG_SDLJoystick.SDL_JoystickIndex(swigJoystick);
	}

	/**
	 * Get the number of joystick axes
	 * 
	 *
	 * @return The number of axes
	 */
	public int joystickNumAxes() {
		return SWIG_SDLJoystick.SDL_JoystickNumAxes(swigJoystick);
	}

	/**
	 * Get the number of joystick trackballs
	 * 
	 *
	 * @return The number of trackballs
	 */
	public int joystickNumBalls() {
		return SWIG_SDLJoystick.SDL_JoystickNumBalls(swigJoystick);
	}

	/**
	 * Get the number of joystick hats
	 * 
	 *
	 * @return The number of hats
	 */
	public int joystickNumHats() {
		return SWIG_SDLJoystick.SDL_JoystickNumHats(swigJoystick);
	}

	/**
	 * Get the number of joystick Buttons
	 * 
	 *
	 * @return The number of Buttons
	 */
	public int joystickNumButtons() {
		return SWIG_SDLJoystick.SDL_JoystickNumButtons(swigJoystick);
	}

	/**
	 * Get the current state of an axis
	 * <P>
	 * SDL_JoystickGetAxis returns the current state of the given axis on the given joystick.
	 * <P>
	 * On most modern joysticks the X axis is usually represented by axis 0
	 * and the Y axis by axis 1. The value returned by SDL_JoystickGetAxis is
	 * a signed integer (-32768 to 32767) representing the current position
	 * of the axis, it may be necessary to impose certain tolerances on these
	 * values to account for jitter. It is worth noting that some joysticks
	 * use axes 2 and 3 for extra buttons.
	 *
	 * @param axis an <code>int</code> value
	 * @return the current position of the axis.
	 */
	public int joystickGetAxis(int axis) {
		return SWIG_SDLJoystick.SDL_JoystickGetAxis(swigJoystick, axis);
	}

	/**
	 * Get the current state of a joystick hat
	 * 
	 * @param hat The hat to get the state for
	 * @return the current state of the given hat on the given joystick.
	 */
	public HatState joystickGetHat(int hat) {
		return HatState.get(SWIG_SDLJoystick.SDL_JoystickGetHat(swigJoystick, hat));
	}

	/**
	 * Get the current state of a given button on a given joystick
	 *
	 * @param button an <code>int</code> value
	 * @return if the given button on the given joystick is pressed
	 */
	public boolean joystickGetButton(int button) {
		return SWIG_SDLJoystick.SDL_JoystickGetButton(swigJoystick, button) == 1;
	}

	/**
	 * Get relative trackball motion
	 *
	 * @param ball The ball
	 * @param dx The value of the x change will be placed here
	 * @param dy The value of the y change will be placed here
	 * @exception SDLException if an error occurs
	 */
	public void joystickGetBall(int ball, Integer dx, Integer dy) throws SDLException {
		int x[] = { 0 };
		int y[] = { 0 };
		int result = SWIG_SDLJoystick.SDL_JoystickGetBall(swigJoystick, ball, x, y);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
		dx = new Integer(x[0]);
		dy = new Integer(y[0]);
	}

	/**
	 * Closes a previously opened joystick
	 *
	 */
	public void joystickClose() {
		SWIG_SDLJoystick.SDL_JoystickClose(swigJoystick);
	}

	protected void finalize() {
		joystickClose();
	}
}