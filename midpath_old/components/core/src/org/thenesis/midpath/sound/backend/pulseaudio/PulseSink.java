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
package org.thenesis.midpath.sound.backend.pulseaudio;

import java.io.IOException;

import com.sun.cldchi.jvm.JVM;

public class PulseSink {
	
	public static final int PULSE_BUF_SIZE = 8192;
	
	public static final int PA_SAMPLE_U8 = 0;           
	public static final int PA_SAMPLE_ALAW = 1;         
	public static final int PA_SAMPLE_ULAW = 2;            
	public static final int PA_SAMPLE_S16LE = 3;          
	public static final int PA_SAMPLE_S16BE = 4;           
	public static final int PA_SAMPLE_FLOAT32LE = 5; 
	public static final int PA_SAMPLE_FLOAT32B = 6;       
	
	static {
		JVM.loadLibrary("libmidpathpulse.so");
		//System.loadLibrary("midpathpulse");
	}
	
	protected boolean isOpen = false;
	
	public void open(String deviceName, int bufferSize, int sampleRate) throws IOException {

		boolean success = open0(PA_SAMPLE_S16LE, 2, sampleRate);

		if (!success) {
			throw new IOException("Can not create PulseAudio stream.");
		}
	}
	
	public int available() {
		// Pulseaudio simple API can't return available buffer size
		return PULSE_BUF_SIZE;
	}

	public void close() {
		close0();
		isOpen = false;
	}
	
	public boolean isOpen() {
		return isOpen;
	}

	public int write(byte[] buf, int offset, int len) {
		int writtenBytes = write0(buf, offset, len);
		if (writtenBytes > 0)
			return writtenBytes;
		return 0;
	}
	
	/* Native methods */

	/**
	 * Opens the line with the specified buffer size and sample rate,
	 * causing the line to acquire any required system resources.
	 * 
	 * @param deviceName the name of the device to open
	 * @param bufferSize the bufferSize (in frames)
	 * @param sampleRate the sample rate
	 * @return a value > 0 if success, otherwise a value < 0
	 */
	private native boolean open0(int format, int channels, int sampleRate);

	/**
	 * Writes data to the line
	 * 
	 * @param b the bytes to be written to the line
	 * @param off offset where to get data in the byte array
	 * @param len number of bytes to write to the line
	 * @return the number of bytes actually written or a negative value if an error occurred
	 */
	private native int write0(byte[] buf, int offset, int frames);

	/**
	 * Closes the line and released system resources it used.
	 */
	private native void close0();
	

}
