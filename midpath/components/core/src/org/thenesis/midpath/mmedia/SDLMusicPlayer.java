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
import sdljava.SDLMain;
import sdljava.mixer.MixMusic;
import sdljava.mixer.SDLMixer;
import sdljava.audio.SDLAudio;

public class SDLMusicPlayer extends SDLPlayer {

    private MixMusic music;
    private EndOfMediaChecker eomChecker;
    public final static boolean DEBUG = false;
    
    public SDLMusicPlayer() {
    }
    
    protected void doClose() {
	if (DEBUG) {
	    System.out.println("doClose()");
	}
	try {
	    SDLMixer.resumeMusic();
	    SDLMixer.haltMusic();
	    SDLMixer.freeMusic(music);
	    SDLMixer.close();
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    if (eomChecker != null) eomChecker.stop();
	}
	if (DEBUG) {
	    System.out.println("coming out of doClose");
	}
    }

    protected void doDeallocate() {
	if (DEBUG) {
	    System.out.println("doDeallocate()");
	}
    }

    protected Control doGetControl(String type) {
	System.out.println("doGetControl():" + type);
	if (type.equals(pkgName + "VolumeControl")) {
	    return new SDLVolumeControl();
	}
	return null;
    }

    protected long doGetDuration() {
	if (DEBUG) {
	    System.out.println("doGetDuration()");
	}
	return TIME_UNKNOWN;
    }

    protected long doGetMediaTime() {
	if (DEBUG) {
	    System.out.println("doGetMediaTime()");
	}
	return TIME_UNKNOWN;
    }

    protected void doPrefetch() throws MediaException {
	if (DEBUG) {
	    System.out.println("doPrefetch()");
	}
    }

    protected void doRealize() throws MediaException {
	if (DEBUG) {
	    System.out.println("doRealize()");
	}
	try {
	    SDLMixer.openAudio(22050, SDLAudio.AUDIO_S16SYS, 1, 8192);
	    // FIXME Remove copy of audio data
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1000];
	    int len = 0;
	    while (((len = stream.read(buffer, 0, buffer.length)) != -1)) {
		baos.write(buffer, 0, len);
	    }
	    if (DEBUG) {
		System.out.println("doRealize len=" + len);
		System.out.println("doRealize buf size=" + buffer.length);
		System.out.println("doRealize baos size=" + baos.size());
	    }
	    music = SDLMixer.loadMUS(baos.toByteArray());

	} catch (Exception e) {
	    e.printStackTrace();
	    throw new MediaException(e.getMessage());
	}
	if (DEBUG) {
	    System.out.println("coming out of doRealize");
	}
    }
    
    protected long doSetMediaTime(long now) throws MediaException {
	if (DEBUG) {
	    System.out.println("doSetMediaTime - " + now);
	}
	if (now < 1) {
	    try {
		SDLMixer.rewindMusic(); 
		
	    } catch (Exception e) {
		e.printStackTrace();
		throw new MediaException("Can't rewind this media");
	    }
	} else {
		
	    try {
		if (!SDLMixer.setMusicPosition(now)) {
		    //throw new MediaException("Can't set position on this media - " + SDLMain.getError());
		    System.out.println("Can't set position on this media - " + SDLMain.getError());
		}
	    } catch (Exception e) {
		e.printStackTrace();
		throw new MediaException("Can't set position on this media");
	    }
	}
	if (DEBUG) {
	    System.out.println("coming out of doSetMediaTime");
	}
	return now;
    }
    
    protected boolean doStart() {
	if (DEBUG) {
	    System.out.println("doStart()");
	}
	if (SDLMixer.pausedMusic()) {
	    SDLMixer.resumeMusic();
	    if (DEBUG) {
		System.out.println("coming out of doStart():1");
	    }
	    return true;
	}

	try {
	    SDLMixer.playMusic(music, 0);
	    
	    eomChecker = new EndOfMediaChecker();
	    eomChecker.start();
	    if (DEBUG) {
		System.out.println("coming out of doStart():2");
	    }
	    return true;
	} catch (SDLException e) {
	    e.printStackTrace();
	    return false;
	}
    }

    protected void doStop() throws MediaException {
	if (DEBUG) {
	    System.out.println("doStop()");
	}
	SDLMixer.pauseMusic();
	if (DEBUG) {
	    System.out.println("coming out of doStop()");
	}
    }

    class SDLToneControl implements ToneControl {

	public void setSequence(byte[] sequence) {
	    
	    if (sequence == null)
		throw new IllegalArgumentException("null sequence");
	    
	    if ((state == Player.PREFETCHED) || (state == Player.STARTED))
		throw new IllegalStateException("Prefetched or Started");

	}	
    }

    class SDLVolumeControl implements VolumeControl {
	public int oldLevel = 0;
	boolean muted = false;
	
	public int getLevel() {
	    if (DEBUG) {
		System.out.println("getLevel()");
	    }
	    if (muted) {
		return oldLevel;
	    }
	    
	    try {
		return SDLMixer.volumeMusic(-1);
	    } catch (SDLException e) {
	    }
	    
	    return 0;
	}

	public boolean isMuted() {
	    if (DEBUG) {
		System.out.println("isMuted()");
	    }
	    return (getLevel() == 0) ? true : false;
	}

	public int setLevel(int level) {
	    if (DEBUG) {
		System.out.println("setLevel():" + level);
	    }
	    if (muted) {
		oldLevel = level;
		return level;
	    }

	    try {
		return SDLMixer.volumeMusic(level);
	    } catch (SDLException e) {
	    }
	    // Should not occur
	    return 0;
	}

	public void setMute(boolean mute) {
	    if (DEBUG) System.out.println("setMute():" + mute);
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

    protected boolean isEndOfMediaReached() {
	boolean end = !SDLMixer.playingMusic();
	if (DEBUG) System.out.println("isEndOfMediaReached():" + end);
	return end; 
    }
}
