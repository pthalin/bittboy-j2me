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

public interface Line {

	public static final int STOPPED = 0;
	public static final int STARTED = 1;
	public static final int CLOSED = 2;

	public int available();

	public int write(byte[] b, int offset, int length);

	public void start();

	public void stop();
	
	public void drain();
	
	public void close();
	
	public boolean isRunning();

	/**
	 * Gets the format of the line audio data.
	 * 
	 * @return the format of the line audio data
	 */
	public AudioFormat getFormat();

}