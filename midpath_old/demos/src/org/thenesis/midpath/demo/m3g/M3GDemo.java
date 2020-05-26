/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of MIDPath nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.thenesis.midpath.demo.m3g;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

public class M3GDemo extends MIDlet {
	private static M3GDemo instance;
	M3GCanvas displayable = new M3GCanvas();
	Timer iTimer = new Timer();

	/* Construct the midlet. */
	public M3GDemo() {
		this.instance = this;
	}

	/** * Main method. */
	public void startApp() {
		Display.getDisplay(this).setCurrent(displayable);
		iTimer.schedule(new MyTimerTask(), 0, 20);
		//System.out.println("Midlet started");
	}

	/** * Handle pausing the MIDlet. */
	public void pauseApp() {

	}

	/** * Handle destroying the MIDlet. */
	public void destroyApp(boolean unconditional) {

	}

	/** * Quit the MIDlet. */

	public static void quitApp() {
		instance.destroyApp(true);
		instance.notifyDestroyed();
		instance = null;
		//System.out.println("Midlet exited");
	}

	/** * Our timer task for providing animation. */
	class MyTimerTask extends TimerTask {
		public void run() {
			if (displayable != null) {
				displayable.repaint();
			}
		}
	}
}
