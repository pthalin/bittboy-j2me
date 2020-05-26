package sdljava.mixer;
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
import sdljava.x.swig.SWIGTYPE_p__Mix_Music;
import sdljava.x.swig.SWIG_SDLMixer;

/**
 * The internal format for a music chunk
 *
 * @author Ivan Z. Ganza
 * @version $Id: MixMusic.java,v 1.6 2004/12/24 17:32:16 ivan_ganza Exp $
 */
public class MixMusic {

    SWIGTYPE_p__Mix_Music swigMixMusic;

    public MixMusic(SWIGTYPE_p__Mix_Music swigMixMusic) {
	this.swigMixMusic = swigMixMusic;
    }
    
    /**
     * Gets the value of swigMixMusic
     *
     * @return the value of swigMixMusic
     */
    public SWIGTYPE_p__Mix_Music getSwigMixMusic()  {
	return this.swigMixMusic;
    }

    /**
     * Sets the value of swigMixMusic
     *
     * @param argSwigMixMusic Value to assign to this.swigMixMusic
     */
    public void setSwigMixMusic(SWIGTYPE_p__Mix_Music argSwigMixMusic) {
	this.swigMixMusic = argSwigMixMusic;
    }

    protected void finalize() {
	SWIG_SDLMixer.Mix_FreeMusic(swigMixMusic);
    }
}