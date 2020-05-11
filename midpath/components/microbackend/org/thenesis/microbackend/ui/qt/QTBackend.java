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
package org.thenesis.microbackend.ui.qt;

import java.io.IOException;

import org.thenesis.microbackend.ui.BackendEventListener;
import org.thenesis.microbackend.ui.Configuration;
import org.thenesis.microbackend.ui.NullBackendEventListener;
import org.thenesis.microbackend.ui.UIBackend;

import com.sun.cldchi.jvm.JVM;

public class QTBackend implements UIBackend {

	public static final int PRESSED = 1;

	private int canvasWidth;
	private int canvasHeight;
	private BackendEventListener listener = new NullBackendEventListener();
	
	static {
		JVM.loadLibrary("libmicrobackendqt.so");
		//System.loadLibrary("microbackendqt");
	}

	public QTBackend(int width, int height) {
		this.canvasWidth = width;
		this.canvasHeight = height;
	}
	
	public QTBackend() {
	}

	/* UICanvas interface */

	public void configure(Configuration conf, int width, int height) {
		this.canvasWidth = width;
		this.canvasHeight = height;
	}

	public void setBackendEventListener(BackendEventListener listener) {
		this.listener = listener;

	}

	public void open() throws IOException {
		
		Runnable runnable = new Runnable() {
			public void run() {
				// Initialize QT.
				if (initialize(canvasWidth, canvasHeight)) {
					// Start main loop and block
					startMainLoop();
				} else {
					System.err.println("Can't initialize QTCanvas");
				}

			}
		};

		new Thread(runnable).start();

	}

	public void close() {
		quit();
	}

	public void updateARGBPixels(final int[] argbBuffer, final int x_src, final int y_src, final int width,
			final int height) {
		writeARGB(argbBuffer, x_src, y_src, width, height);
	}
	
	public int getWidth() {
        return canvasWidth;
    }

    public int getHeight() {
        return canvasHeight;
    }


	/* Event callback methods (called by native code) */

	public void onKeyEvent(int state, int keyCode, int unicode) {
		if (state == PRESSED) {
			// System.out.println("key pressed: " + keyCode + " " + ((char) unicode));
			listener.keyPressed(keyCode, (char) unicode, state);

		} else {
			// System.out.println("key released: " + keyCode + " " + ((char) unicode));
			listener.keyReleased(keyCode, (char) unicode, state);

		}
	}

	public void onMouseMoveEvent(int x, int y) {
		// System.out.println("motion event: " + x + " " + y);
		listener.pointerMoved(x, y, 0);
	}

	public void onMouseButtonEvent(int x, int y, int state) {
		if (state == PRESSED) {
			// System.out.println("button pressed: " + x + " " + y);
			listener.pointerPressed(x, y, 0);
		} else {
			// System.out.println("button released: " + x + " " + y);
			listener.pointerReleased(x, y, 0);
		}
	}

	public void onCloseEvent() {
		// System.out.println("Window delete event received: ");
		listener.windowClosed();
	}

	/* Native methods */

	native private boolean initialize(int width, int height);

	native private void writeARGB(int[] argbBuffer, int x_src, int y_src, int width, int height);

	native private int startMainLoop();

	native private int quit();

}
