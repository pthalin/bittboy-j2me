/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 */
package org.thenesis.midpath.sound;

/**
 * AudioFormat is the class that specifies arrangement of data
 * in a sound stream.
 * 
 * @author Guillaume Legris
 *
 */
public class AudioFormat {

	public static final int MONO = 1;
	public static final int STEREO = 2;
	public static final int BITS_8 = 1;
	public static final int BITS_16 = 2;

	public int sampleRate;
	public int bytePerSample;
	public int channels;
	public boolean signed;
	public boolean bigEndian;

	/**
	 * 
	 * @param sampleRate the sample rate in Hz
	 * @param bytePerSample the number of byte per sample.
	 * @param channels the number of channels.
	 * @param signed if true, data are signed; otherwise, they are unsigned.
	 * @param bigEndian if true, data are in the big endian format; 
	 *        otherwise, data are in the little endian format.
	 */
	public AudioFormat(int sampleRate, int bytePerSample, int channels, boolean signed, boolean bigEndian) {
		this.sampleRate = sampleRate;
		this.bytePerSample = bytePerSample;
		this.channels = channels;
		this.signed = signed;
		this.bigEndian = bigEndian;
	}

	public int getBytesPerFrame() {
		return bytePerSample * channels;
	}

	/**
	 * Tests if the given audio format matches with this one.
	 * @param format the audio format to test.
	 * @return true if the given format matches this one, otherwise false
	 */
	public boolean matches(AudioFormat format) {

		if (format.sampleRate != sampleRate) {
			return false;
		}

		if (format.bytePerSample != bytePerSample) {
			return false;
		}

		if (format.channels != channels) {
			return false;
		}

		if (format.signed != signed) {
			return false;
		}

		if (format.bigEndian != bigEndian) {
			return false;
		}

		return true;
	}

	public boolean is16bitsStereoSignedLittleEndian() {
		if (bytePerSample != BITS_16) {
			return false;
		}

		if (channels != STEREO) {
			return false;
		}

		if (signed != true) {
			return false;
		}

		if (bigEndian != false) {
			return false;
		}

		return true;
	}

	public String toString() {
		
		return (sampleRate + " Hz") + ", " + ((bytePerSample * 8) + " bits") + ", "
				+ (channels + " channels") + ", " + (signed ? "signed" : "not signed") + ", "
				+ (bigEndian ? "bigEndian" : "little endian");
	}

}