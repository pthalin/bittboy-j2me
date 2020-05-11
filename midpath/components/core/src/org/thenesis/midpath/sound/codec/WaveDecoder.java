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
package org.thenesis.midpath.sound.codec;

import java.io.IOException;
import java.io.InputStream;

import org.thenesis.midpath.sound.AudioFormat;

public class WaveDecoder implements AudioDecoder {
	
	public static final int BUFFER_SIZE_DEFAULT =  4 * 1024;
	
	private WaveFile waveFile;
	private InputStream is; 
	private AudioFormat waveFormat;
	private int bufferSize;
	private byte[] buffer;
	int bufferFrames;
	int bytesWritten;
	
	public WaveDecoder() {
	}
	
	public void initialize(InputStream is, int bufferSize) throws IOException {
		this.is = is;
		this.bufferSize = bufferSize;
		
		waveFile = new WaveFile(is);
		int bytesPerFrame = (waveFile.getBitsPerSample() * waveFile.getChannels()) / 8;
		int bytesPerSample = waveFile.getBitsPerSample() / 8;
		boolean signed = bytesPerSample > 1 ? true : false;
		waveFormat = new AudioFormat(waveFile.getRate(), bytesPerSample, waveFile.getChannels(), signed, false);
		buffer = new byte[bufferSize];
		bufferFrames = bufferSize / bytesPerFrame;
		bytesWritten = 0;
	}
	
	public void initialize(InputStream is) throws IOException {
		initialize(is, BUFFER_SIZE_DEFAULT);
	}
	
	public int decodeStep(DecoderCallback os) throws IOException {
		int framesRead = waveFile.readFrame(buffer, bufferFrames);
		
		if (framesRead <= 0) {
			return framesRead;
		}
		
		int samplesRead = framesRead * waveFormat.getBytesPerFrame();
		os.write(buffer, 0, samplesRead);
		bytesWritten += samplesRead;
		return samplesRead; 
	}
	
	public AudioFormat getOutputAudioFormat() {
		return waveFormat;
	}

}
