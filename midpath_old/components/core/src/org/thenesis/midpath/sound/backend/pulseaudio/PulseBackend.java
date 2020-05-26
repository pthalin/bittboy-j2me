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

import org.thenesis.midpath.sound.AudioFormat;
import org.thenesis.midpath.sound.Mixer;
import org.thenesis.midpath.sound.SoftMixer;
import org.thenesis.midpath.sound.SoundBackend;
import org.thenesis.midpath.sound.SoundToolkit;

public class PulseBackend extends PulseSink implements SoundBackend {
	
	public static final String DEFAULT_DEVICE_NAME = "default"; //"plughw:0,0";
	public static final int DEFAULT_BUFFER_SIZE = 8192;
	private SoftMixer mixer;
	private AudioFormat audioFormat;
	private int bufferSize;
	
	public void open() throws IOException {
		
		if (!isOpen) {
			
			audioFormat = SoundToolkit.getAudioFormat();
			bufferSize = SoundToolkit.getBufferSize();
			mixer = new SoftMixer(this);
			
			//System.out.println("audioFormat: " + audioFormat);
			open(SoundToolkit.getDeviceName(), SoundToolkit.getBufferSize(), SoundToolkit.getAudioFormat().sampleRate);
			isOpen = true;
		}
		
	}

	public Mixer getMixer() {
		return mixer;
	}
	
	public AudioFormat getAudioFormat() {
		return audioFormat;
	}
	
	public int getBufferSize() {
		return bufferSize;
	}


}
