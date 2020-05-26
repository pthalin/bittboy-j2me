package sdljavax.gfx;
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
 *  Bart LEBOEUF  (bartleboeuf@yahoo.fr)
 */
import sdljava.SDLTimer;

/**
 * Full java version of the framerate manager from SDL_gfx library.
 * <P>
 * 
 * @author Bart LEBOEUF
 * @version $Id: FPSmanager.java,v 1.1 2005/02/18 02:54:32 doc_alton Exp $
 */
public final class FPSmanager {

	/* Some rates in Hz */
	public static final int FPS_UPPER_LIMIT = 200;
	public static final int FPS_LOWER_LIMIT = 1;
	public static final int FPS_DEFAULT = 30;

	int framecount;
	float rateticks;
	long lastticks;
	int rate;

	/**
	 * framerate manager
	 */
	public FPSmanager() {
		/*
		 * Store some sane values
		 */
		framecount = 0;
		rate = FPS_DEFAULT;
		rateticks = (1000.0f / (float) FPS_DEFAULT);
		lastticks = SDLTimer.getTicks();
	}

	/**
	 * Initialize the framerate manager
	 */
	public void initFramerate() {
		rate = 30;
	}

	/**
	 * Delay execution to maintain a constant framerate. Calculate fps.
	 */
	public void framerateDelay() {
		long current_ticks;
		long target_ticks;
		long the_delay;

		/*
		 * Next frame
		 */
		framecount++;

		/*
		 * Get/calc ticks
		 */
		current_ticks = SDLTimer.getTicks();
		target_ticks = lastticks + (framecount * (long) rateticks);

		if (current_ticks <= target_ticks) {
			the_delay = target_ticks - current_ticks;
			try {
				SDLTimer.delay(the_delay);
			} catch (InterruptedException e) {

			}
		} else {
			framecount = 0;
			lastticks = SDLTimer.getTicks();
		}
	}

	/**
	 * Set the framerate in Hz
	 */
	public int setFramerate(int new_rate) {
		if ((rate >= FPS_LOWER_LIMIT) && (rate <= FPS_UPPER_LIMIT)) {
			framecount = 0;
			rate = new_rate;
			rateticks = (1000.0f / (float) rate);
			return (0);
		} else {
			return (-1);
		}
	}

	/**
	 * Return the current target framerate in Hz
	 * 
	 * @return
	 */
	public int getFramerate() {
		return rate;
	}
}