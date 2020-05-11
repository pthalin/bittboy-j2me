package sdljava.audio;
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
import sdljava.x.swig.SWIG_SDLAudioConstants;

/**
 * Interface to the SDL Audio subsystem.  Please use SDLMixer instead of this
 * package.
 *
 * 
 * @version $Id: SDLAudio.java,v 1.10 2004/12/29 19:11:52 ivan_ganza Exp $
 */
public class SDLAudio {

    // constants from SDL_audio.h
    public final static int AUDIO_U8      = SWIG_SDLAudioConstants.AUDIO_U8;
    public final static int AUDIO_S8      = SWIG_SDLAudioConstants.AUDIO_S8;
    public final static int AUDIO_U16LSB  = SWIG_SDLAudioConstants.AUDIO_U16LSB;
    public final static int AUDIO_S16LSB  = SWIG_SDLAudioConstants.AUDIO_S16LSB;
    public final static int AUDIO_U16MSB  = SWIG_SDLAudioConstants.AUDIO_U16MSB;
    public final static int AUDIO_S16MSB  = SWIG_SDLAudioConstants.AUDIO_S16MSB;
    public final static int AUDIO_U16     = SWIG_SDLAudioConstants.AUDIO_U16;
    public final static int AUDIO_S16     = SWIG_SDLAudioConstants.AUDIO_S16;
    public final static int AUDIO_S16SYS  = SWIG_SDLAudioConstants.AUDIO_S16SYS;
    public final static int AUDIO_U16SYS  = SWIG_SDLAudioConstants.AUDIO_U16SYS;

//    public static void openAudio(SDLAudioSpec desired, SDLAudioSpec obtained) throws SDLException {
//	int result = SWIG_SDLAudio.SWIG_SDL_OpenAudio(desired.getSwigAudioSpec(), obtained.getSwigAudioSpec());
//	if (result != 0) {
//	    throw new SDLException(SDLError.SDL_GetError());
//	}
//    }

//    public static short[] loadWAV(String path, SDLAudioSpec spec) throws SDLException {
//	short[] wavBuffer = new short[1];
//	wavBuffer[0] = 1;
//
//	SWIG_SDL_AudioObject result = SWIG_SDLAudio.SWIG_SDL_LoadWAV(path, spec.getSwigAudioSpec());
//	if (result == null) {
//	    throw new SDLException(SDLError.SDL_GetError());
//	}
//
//	System.out.print("result.audioLength=" + result.getAudioLength());
//
//	return wavBuffer;
//    }

//    public static void pauseAudio(boolean b) throws SDLException {
//	SWIG_SDLAudio.SDL_PauseAudio(b == true ? 1 : 0);
//    }
} // class SDLAudio