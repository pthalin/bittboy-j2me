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
package org.thenesis.midpath.sound.backend.alsa;

import java.io.IOException;

import com.sun.cldchi.jvm.JVM;

public class AlsaSink {
	
	static {
		JVM.loadLibrary("libmidpathalsa.so");
		//System.loadLibrary("midpathalsa");
	}
	
	public static final int BYTE_PER_FRAME = 4;
	
	protected boolean isOpen = false;
	
	public void open(String deviceName, int bufferSize, int sampleRate) throws IOException {

		int rval = open0(deviceName, bufferSize, sampleRate);

		if (rval < 0) {
			throw new IOException("ALSA can't open device. Reason: " + rval);
		}
	}
	
	public int available() {
		int available = available0();

		if (available > 0) {
			return available;
		}

		return 0;
	}

	public void close() {
		close0();
		isOpen = false;
	}
	
	public boolean isOpen() {
		return isOpen;
	}

	public int write(byte[] buf, int offset, int len) {
		int writtenBytes = write0(buf, offset, len / BYTE_PER_FRAME);
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
	private native int open0(String deviceName, int bufferSize, int sampleRate);

	/**
	 * Obtains the number of bytes of data that can be written to the buffer
	 * without blocking.
	 * 
	 * @return the amount of data available (in bytes) or a negative value if an error occurred
	 */
	private native int available0();

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
	
//	/**
	//	 * @param args
	//	 */
	//	public static void main(String[] args) {
	//		
	//		Random random = new Random();
	//		
	//		int bufferSize = 8192;
	//		byte[] buf = new byte[bufferSize];
	//		
	//		for (int i = 0; i < bufferSize; i++) {
	//			buf[i] = (byte)(random.nextInt(255));
	//		}
	//
	//		AlsaSink sink = new AlsaSink();
	//		sink.open(8192, 44100);
	//		
	//		int available = sink.available();
	//		System.out.println("available: "+ available);
	//		
	//		while(true) {
	//			if ((available = sink.available()) > 0) {
	//				System.out.println("available: "+ available);
	//				sink.write(buf, 0, available);
	//			}
	//			try {
	//				Thread.sleep(5);
	//			} catch (InterruptedException e) {
	//			}
	//		}
	//		
	//		//sink.close();
	//
	//
	//	}

}
