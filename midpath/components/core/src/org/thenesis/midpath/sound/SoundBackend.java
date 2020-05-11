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

import java.io.IOException;

public interface SoundBackend {
	
	
	/**
	 * Opens the line with default value,
	 * causing the line to acquire any required system resources.
	 */
	public void open() throws IOException;
	
	public boolean isOpen();
	
	public AudioFormat getAudioFormat();
	
	public int getBufferSize();
	
//	/**
//	 * Opens the line with the specified buffer size and sample rate,
//	 * causing the line to acquire any required system resources.
//	 * 
//	 * @param bufferSize
//	 * @param sampleRate
//	 */
//	public void open(String deviceName, int bufferSize, int sampleRate) throws IOException;

	/**
	 * Obtains the number of bytes of data that can be written to the buffer
	 * without blocking.
	 * 
	 * @return the amount of data available (in bytes)
	 */
	public int available();

	/**
	 * Writes data to the line
	 * 
	 * @param b the bytes to be written to the line
	 * @param off offset where to get data in the byte array
	 * @param len number of bytes to write to the line
	 * @return the number of bytes actually written
	 */
	public int write(byte[] buf, int offset, int len);


	/**
	 * Closes the line and released system resources it used.
	 */
	public void close();
	
	public Mixer getMixer();

}