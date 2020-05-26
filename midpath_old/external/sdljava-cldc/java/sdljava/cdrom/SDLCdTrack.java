package sdljava.cdrom;
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
import sdljava.x.swig.SDL_CDtrack;
import sdljava.x.swig.SWIG_SDLCdrom;
import sdljava.x.swig.TrackType;

/**
 * CD Track Information
 *
 * 
 * @version $Id: SDLCdTrack.java,v 1.4 2005/02/21 17:32:43 doc_alton Exp $
 */
public class SDLCdTrack {

    public static final int CD_FPS = 75;
    public static final int SDL_AUDIO_TRACK = 0x00; 
    public static final int SDL_DATA_TRACK = 0x04;

    SDL_CDtrack swigTrack;

    protected SDLCdTrack(SDL_CDtrack swigTrack) {
	this.swigTrack = swigTrack;
    }
    
    /**
     * Return if current track is a Data Track.
     * @return boolean return <code>true</code> if is an data track, else <code>false</code>. 
     */
    public boolean isDataTrack() {
    	return getType().swigValue()==SDLCdTrack.SDL_DATA_TRACK;
    }

    /**
     * Return if current track is an Audio Track.
     * @return boolean return <code>true</code> if is an audio track, else <code>false</code>. 
     */
    public boolean isAudioTrack() {
    	return getType().swigValue()==SDLCdTrack.SDL_AUDIO_TRACK;
    }
    
    /**
     * Get the track number
     *
     * @return the track number
     */
    public int getId() {
	return swigTrack.getId();
    }
    
    /**
     * Get the track type
     *
     * @return the track type
     */
    public TrackType getType() {
	return TrackType.swigToEnum(swigTrack.getType());
    }
    
    /**
     * Get the length (in frames) of this track
     *
     * @return the length (in frames) of this track
     */
    public long getLength() {
	return swigTrack.getLength();
    }
    
    /**
     * Get the frame offset of the beginning of this track
     *
     * @return the frame offset of the beginning of this track
     */
    public long getOffset() {
	return swigTrack.getOffset();
    }

    public FrameInfo getFrameInfo() {
	int[]  m = {0};
	int[]  s = {0};
	int[]  f = {0};

	int frame = (int)getOffset();
	
	SWIG_SDLCdrom.SWIG_framesToMSF(frame, m, s, f);

	return new FrameInfo(frame, m[0], s[0], f[0]);
    }
    
//    /**
//     * Describe <code>framesToMSF</code> method here.
//     *
//     * @return a <code>long</code> value
//     */
//    public long framesToMSF() {
//    }
//    
//    /**
//     * Describe <code>msfToFrames</code> method here.
//     *
//     * @return a <code>long</code> value
//     */
//    public long msfToFrames() {
//    }

        /**
     * Return a string represenation of this object
     *
     * @return a String represenation of this object
     */
    public String toString() {
	StringBuffer buf = new StringBuffer();

	buf.append("SDLCDtrack[").
	    append("id=").append(getId()).
	    append(", type=").append(getType()).
	    append(", length=").append(getLength()).
	    append(", offset=").append(getOffset()).
	    append("]");
	
	return buf.toString();
    }
}