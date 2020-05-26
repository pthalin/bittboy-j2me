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
import sdljava.x.swig.SDL_AudioSpec;

/**
 * The SDL_AudioSpec structure is used to describe the format of some audio data.
 * This structure is used by SDL_OpenAudio and SDL_LoadWAV. While all fields are
 * used by SDL_OpenAudio, only freq, format, samples and channels are used by SDL_LoadWAV.
 *
 * 
 * @version $Id: SDLAudioSpec.java,v 1.5 2004/12/29 19:11:52 ivan_ganza Exp $
 */
public class SDLAudioSpec {
	SDL_AudioSpec swigAudioSpec;

	public SDLAudioSpec() {
		swigAudioSpec = new SDL_AudioSpec();
	}

	/**
	 * Gets the value of swigAudioSpec
	 *
	 * @return the value of swigAudioSpec
	 */
	public SDL_AudioSpec getSwigAudioSpec() {
		return this.swigAudioSpec;
	}

	/**
	 * Sets the value of swigAudioSpec
	 *
	 * @param argSwigAudioSpec Value to assign to this.swigAudioSpec
	 */
	public void setSwigAudioSpec(SDL_AudioSpec argSwigAudioSpec) {
		this.swigAudioSpec = argSwigAudioSpec;
	}

	/**
	 * Set Audio frequency in samples per second
	 *
	 * @param freq an <code>int</code> value
	 */
	public void setFreq(int freq) {
		swigAudioSpec.setFreq(freq);
	}

	/**
	 * @return Audio frequency in samples per second
	 *
	 * 
	 */
	public int getFreq() {
		return swigAudioSpec.getFreq();
	}

	/**
	 * Set Audio data format
	 *
	 * @param format an <code>int</code> value
	 */
	public void setFormat(int format) {
		swigAudioSpec.setFormat(format);
	}

	/**
	 * @return The current Audio data format
	 *
	 * 
	 */
	public int getFormat() {
		return swigAudioSpec.getFormat();
	}

	/**
	 * Set number of channels (1-mono, 2-stereo)
	 *
	 * @param channels a <code>short</code> value
	 */
	public void setChannels(int channels) {
		swigAudioSpec.setChannels((short) channels);
	}

	/**
	 * @return Number of audio channels (1-mono, 2-stereo)
	 *
	 * 
	 */
	public short getChannels() {
		return swigAudioSpec.getChannels();
	}

	/**
	 * @return Audio buffer silence value (calculated)
	 *
	 * 
	 */
	public short getSilence() {
		return swigAudioSpec.getSilence();
	}

	/**
	 * Set Audio buffer size in samples
	 *
	 * @param samples an <code>int</code> value
	 */
	public void setSamples(int samples) {
		swigAudioSpec.setSamples(samples);
	}

	/**
	 * @return Audio buffer size in samples
	 *
	 * 
	 */
	public int getSamples() {
		return swigAudioSpec.getSamples();
	}

	/**
	 * @return Audio buffer size in bytes (calculated)
	 *
	 * 
	 */
	public long getSize() {
		return swigAudioSpec.getSize();
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("SDLAudioSpec[freq=").append(getFreq()).append(", format=").append(getFormat())
				.append(", channels=").append(getChannels()).append(", silence=").append(getSilence()).append(
						", samples=").append(getSamples()).append(", size=").append(getSize()).append("]");

		return buf.toString();
	}

} // class SDLAudioSpec