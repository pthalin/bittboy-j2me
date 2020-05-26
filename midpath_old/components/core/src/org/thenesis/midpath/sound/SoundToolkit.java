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

import org.thenesis.midpath.sound.backend.alsa.AlsaBackend;
import org.thenesis.midpath.sound.backend.esd.EsdBackend;
import org.thenesis.midpath.sound.backend.pulseaudio.PulseBackend;

import com.sun.midp.main.Configuration;

public class SoundToolkit {

	private static int bufferSize;
	private static String deviceName;
	public static SoundBackend soundBackend;
	private static AudioFormat audioFormat;

	static {
		int sampleRate = com.sun.midp.main.Configuration.getIntProperty("org.thenesis.midpath.sound.sampleRate", 44100);
		bufferSize = com.sun.midp.main.Configuration.getIntProperty("org.thenesis.midpath.sound.bufferSize", 8192);
		deviceName = com.sun.midp.main.Configuration.getPropertyDefault("org.thenesis.midpath.sound.device", "default");
		audioFormat = new AudioFormat(sampleRate, AudioFormat.BITS_16, AudioFormat.STEREO, true, false);

		String backendName = Configuration.getPropertyDefault("org.thenesis.midpath.sound.backend", "NULL");
		if (backendName.equalsIgnoreCase("ALSA")) {
			soundBackend = new AlsaBackend();
		} else if (backendName.equalsIgnoreCase("ESD")) {
			soundBackend = new EsdBackend();
		} else if (backendName.equalsIgnoreCase("PulseAudio")) {
			soundBackend = new PulseBackend();
		} else {
			soundBackend = new NullSoundBackend();
		}
	}

	public static SoundBackend getBackend() {
		return soundBackend;
	}

	public static AudioFormat getAudioFormat() {
		return audioFormat;
	}

	public static int getBufferSize() {
		return bufferSize;
	}

	public static String getDeviceName() {
		return deviceName;
	}

}
