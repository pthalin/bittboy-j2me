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
import sdljava.x.swig.Mix_Chunk;

/**
 * The internal format for an audio chunk
 *
 * @author Ivan Z. Ganza
 * @version $Id: MixChunk.java,v 1.6 2004/12/24 17:32:16 ivan_ganza Exp $
 */
public class MixChunk {
	Mix_Chunk swigMixChunk;

	public MixChunk(Mix_Chunk swigMixChunk) {
		this.swigMixChunk = swigMixChunk;
	}

	/**
	 * Gets the value of swigMixChunk
	 *
	 * @return the value of swigMixChunk
	 */
	public Mix_Chunk getSwigMixChunk() {
		return this.swigMixChunk;
	}

	/**
	 * Sets the value of swigMixChunk
	 *
	 * @param argSwigMixChunk Value to assign to this.swigMixChunk
	 */
	public void setSwigMixChunk(Mix_Chunk argSwigMixChunk) {
		this.swigMixChunk = argSwigMixChunk;
	}

	public int getAllocated() {
		return swigMixChunk.getAllocated();
	}

	public long getAlen() {
		return swigMixChunk.getAlen();
	}

	public void setVolume(int volume) {
		swigMixChunk.setVolume((short) volume);
	}

	public int getVolume() {
		return (short) swigMixChunk.getVolume();
	}

	protected void finalize() {
		try {
			SDLMixer.freeChunk(this);
		} catch (sdljava.SDLException e) {
			e.printStackTrace();
		} // try-catch
	}
}