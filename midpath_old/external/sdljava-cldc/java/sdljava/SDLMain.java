package sdljava;

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
import sdljava.x.swig.SDL_version;
import sdljava.x.swig.SWIG_SDLMain;
import sdljava.x.swig.SWIG_SDLMainConstants;

/**
 * Wrapping of the SDL_main functions.
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLMain.java,v 1.8 2005/02/19 02:26:01 ivan_ganza Exp $
 */
public class SDLMain {

	/**
	 * Initializes the timer subsystem
	 *
	 */
	public final static int SDL_INIT_TIMER = SWIG_SDLMainConstants.SDL_INIT_TIMER;

	/**
	 * Initializes the audio subsystem
	 *
	 */
	public final static int SDL_INIT_AUDIO = SWIG_SDLMainConstants.SDL_INIT_AUDIO;

	/**
	 * Initializes the video subsystem
	 *
	 */
	public final static int SDL_INIT_VIDEO = SWIG_SDLMainConstants.SDL_INIT_VIDEO;

	/**
	 * Initializes the cdrom subsystem
	 *
	 */
	public final static int SDL_INIT_CDROM = SWIG_SDLMainConstants.SDL_INIT_CDROM;

	/**
	 * Initializes the joystick subsystem 
	 *
	 */
	public final static int SDL_INIT_JOYSTICK = SWIG_SDLMainConstants.SDL_INIT_JOYSTICK;

	/**
	 * Prevents SDL from catching fatal signals
	 *
	 */
	public final static int SDL_INIT_NOPARACHUTE = SWIG_SDLMainConstants.SDL_INIT_NOPARACHUTE;

	/**
	 * 
	 *
	 */
	public final static int SDL_INIT_EVENTTHREAD = SWIG_SDLMainConstants.SDL_INIT_EVENTTHREAD;

	/**
	 * Initialize all
	 *
	 */
	public final static int SDL_INIT_EVERYTHING = SWIG_SDLMainConstants.SDL_INIT_EVERYTHING;

	/**
	 * Time in milliseconds of the SDL library initialization
	 */
	private static long start_time = System.currentTimeMillis();

	/**
	 * Initializes SDL. This should be called before all other SDL
	 * functions. The flags parameter specifies what part(s) of SDL to
	 * initialize.
	 * <P>
	 *      SDL_INIT_TIMER 	Initializes the timer subsystem.<br>
	 *      SDL_INIT_AUDIO 	Initializes the audio subsystem.<br>
	 *      SDL_INIT_VIDEO 	Initializes the video subsystem.<br>
	 *      SDL_INIT_CDROM 	Initializes the cdrom subsystem.<br>
	 *      SDL_INIT_JOYSTICK 	Initializes the joystick subsystem.<br>
	 *      SDL_INIT_EVERYTHING 	Initialize all of the above.<br>
	 *      SDL_INIT_NOPARACHUTE 	Prevents SDL from catching fatal signals.<br>
	 *      SDL_INIT_EVENTTHREAD<br>
	 *
	 * @param flags an <code>int</code> value
	 * @exception SDLException if an error occurs
	 */
	public static void init(long flags) throws SDLException {
		int result = SWIG_SDLMain.SDL_Init(flags);
		if (result == -1) {
			throw new SDLException(getError());
		}
		start_time = System.currentTimeMillis();
	}

	/**
	 * Initialize subsystems
	 * <P>
	 * After SDL has been initialized with SDL_Init you may initialize
	 * uninitialized subsystems with SDL_InitSubSystem. The flags parameter
	 * is the same as that used in SDL_Init.
	 *
	 * @param flags an <code>int</code> value
	 * @exception SDLException if an error occurs
	 */
	public static void initSubSystem(int flags) throws SDLException {
		int result = SWIG_SDLMain.SDL_InitSubSystem(flags);
		if (result == -1) {
			throw new SDLException(getError());
		}
	}

	/**
	 * Shut down a subsystem
	 * <P>
	 * SDL_QuitSubSystem allows you to shut
	 * down a subsystem that has been previously initialized by
	 * SDL_Init or SDL_InitSubSystem. The flags tells
	 * SDL_QuitSubSystem which subsystems to shut down, it uses the
	 * same values that are passed to SDL_Init.
	 *
	 * @param flags an <code>int</code> value
	 */
	public static void quitSubSystem(int flags) {
		SWIG_SDLMain.SDL_QuitSubSystem(flags);
	}

	/**
	 * SDL_Quit shuts down all SDL subsystems and frees the resources allocated to them.
	 *
	 */
	public static void quit() {
		SWIG_SDLMain.SDL_Quit();
	}

	/**
	 * Checks which subsystems are initialized
	 *
	 * @param flags  bitwise OR'd combination of the subsystems you wish to check
	 * @return Returns a bitwised OR'd combination of the initialized subsystems.
	 * @exception SDLException if an error occurs
	 */
	public static long wasInit(long flags) throws SDLException {
		return SWIG_SDLMain.SDL_WasInit(flags);
	}

	/**
	 * Gets SDL error string about the last internal SDL error
	 *
	 * @return returns string containing information about the last internal SDL error.
	 */
	public static String getError() {
		return SWIG_SDLMain.SDL_GetError();
	}

	/**
	 * Get time of the SDL library initialization.
	 * 
	 * @return returns long represent time of the SDL library initialization.
	 */
	public static long getStartedTime() {
		return start_time;
	}

	/**
	 * get a version structure with the compile-time version of the SDL library.
	 *
	 *
	 * @return the compile-time version of the SDL library.
	 */
	public static SDLVersion getSDLVersion() {
		SDL_version v = SWIG_SDLMain.SWIG_SDL_VERSION();
		return new SDLVersion(v);
		//return null;
	}

} // class SDLMain