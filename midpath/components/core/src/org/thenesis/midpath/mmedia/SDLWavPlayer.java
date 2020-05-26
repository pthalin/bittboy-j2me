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
package org.thenesis.midpath.mmedia;

import java.io.ByteArrayOutputStream;

import javax.microedition.media.Control;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.control.ToneControl;
import javax.microedition.media.control.VolumeControl;

import sdljava.SDLException;
import sdljava.mixer.MixChunk;
import sdljava.mixer.SDLMixer;

public class SDLWavPlayer extends SDLPlayer {

	
	private MixChunk mixChunk;
	private int channel;
	private EndOfMediaChecker eomChecker;
	
	static {
		try {
			/*int allocatedChannels =*/ SDLMixer.allocateChannels(4);
		} catch (SDLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public SDLWavPlayer() {
	}

	protected void doClose() {
		try {
			SDLMixer.freeChunk(mixChunk);
			
		} catch (SDLException e) {
			//e.printStackTrace();
		} finally {
			eomChecker.stop();
		}

	}

	protected void doDeallocate() {

	}

	protected Control doGetControl(String type) {
		if (type.equals(pkgName + "VolumeControl")) {
			return new SDLVolumeControl();
		}
		return null;
	}

	protected long doGetDuration() {
		return TIME_UNKNOWN;
	}

	protected long doGetMediaTime() {
		return TIME_UNKNOWN;
	}

	protected void doPrefetch() throws MediaException {

	}

	protected void doRealize() throws MediaException {
		try {

			// FIXME Remove copy of audio data
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			byte[] buffer = new byte[1000];
			int len = 0;
			while ((len = stream.read(buffer, 0, buffer.length)) != -1) {
				baos.write(buffer, 0, len);
			}

			mixChunk = SDLMixer.loadWAV(baos.toByteArray());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new MediaException(e.getMessage());
			//e.printStackTrace();
		}

	}

	protected long doSetMediaTime(long now) throws MediaException {
		throw new MediaException("Can't set position on this media");	
	}

	protected boolean doStart() {

		try {
			
			if (SDLMixer.paused(channel) == 1) {
				SDLMixer.resume(channel);
				return true;
			}
			channel = SDLMixer.playChannel(-1, mixChunk, 0);
			
			eomChecker = new EndOfMediaChecker();
			eomChecker.start();
			return true;
		} catch (SDLException e) {
			//e.printStackTrace();
			return false;
		}

	}

	protected void doStop() throws MediaException {
		try {
			SDLMixer.pause(channel);
		} catch (SDLException e) {
			throw new MediaException(e.getMessage());
		}
	}
	
	protected boolean isEndOfMediaReached() {
		try {
			return SDLMixer.playing(channel) == 0;
		} catch (SDLException e) {
		}
		return true;
	}

	class SDLToneControl implements ToneControl {

		public void setSequence(byte[] sequence) {

			if (sequence == null)
				throw new IllegalArgumentException("null sequence");

			if ((state == Player.PREFETCHED) || (state == Player.STARTED))
				throw new IllegalStateException("Prefetched or Started");

			// TODO Auto-generated method stub
		}

	}

	class SDLVolumeControl implements VolumeControl {

		public int oldLevel = 0;
		boolean muted = false;

		public int getLevel() {

			if (muted) {
				return oldLevel;
			}

			try {
				return SDLMixer.volumeChunk(mixChunk, -1);
			} catch (SDLException e) {
			}

			return 0;
		}

		public boolean isMuted() {
			return (getLevel() == 0) ? true : false;
		}

		public int setLevel(int level) {

			if (muted) {
				oldLevel = level;
				return level;
			}

			try {
				return SDLMixer.volumeChunk(mixChunk, level);
			} catch (SDLException e) {
			}
			// Should not occur
			return 0;
		}

		public void setMute(boolean mute) {

			if (mute) {
				oldLevel = getLevel();
				setLevel(0);
				muted = true;
			} else {
				setLevel(oldLevel);
				muted = false;
			}

		}

	}

}
