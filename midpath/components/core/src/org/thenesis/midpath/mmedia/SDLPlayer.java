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

import javax.microedition.media.PlayerListener;

import sdljava.SDLException;
import sdljava.SDLMain;
import sdljava.audio.SDLAudio;
import sdljava.mixer.SDLMixer;

public abstract class SDLPlayer extends BasicPlayer{
	
	static {
		try {
			SDLMain.init(SDLMain.SDL_INIT_AUDIO);
			SDLMixer.openAudio(22050, SDLAudio.AUDIO_S16SYS, 1, 8192);
		} catch (SDLException e) {
			e.printStackTrace();
		}
	}
	
	
	protected abstract boolean isEndOfMediaReached();
    
    class EndOfMediaChecker implements Runnable {
		
		private volatile boolean running = true;
		
		public void run() {
			
			try {
				while(running) {
					if(isEndOfMediaReached()) {
						sendEvent(PlayerListener.END_OF_MEDIA, new Long(-1));
						state = PREFETCHED;
						running = false;
						break;
					}
					Thread.sleep(50);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		public synchronized void start() {
			new Thread(this).start();
		}
		
		public synchronized void stop() {
			running = false;
		}
		
	}
	

}
