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
package org.thenesis.microbackend.ui.gtk;

import java.util.Vector;

import org.thenesis.microbackend.ui.BackendEventListener;
import org.thenesis.microbackend.ui.Configuration;
import org.thenesis.microbackend.ui.Logging;
import org.thenesis.microbackend.ui.NullBackendEventListener;
import org.thenesis.microbackend.ui.UIBackend;

import com.sun.cldchi.jvm.JVM;

public class GTKBackend implements UIBackend {

	static {
		JVM.loadLibrary("libmicrobackendgtk.so");
		//System.loadLibrary("microbackendgtk");
	}

	private int canvasWidth;
	private int canvasHeight;
	private BackendEventListener listener = new NullBackendEventListener();
	private GTKThread gtkThread;

	public GTKBackend(int w, int h) {
		canvasWidth = w;
		canvasHeight = h;
	}

	public GTKBackend() {
	}

	/* UIBackend interface */

	public void configure(Configuration conf, int width, int height) {
		canvasWidth = width;
		canvasHeight = height;
	}

	public void setBackendEventListener(BackendEventListener listener) {
		this.listener = listener;
	}

	public void updateARGBPixels(final int[] argbBuffer, final int x_src, final int y_src, final int width,
			final int height) {

		//writeARGB(argbBuffer, x_src, y_src, width, height);

		gtkThread.addToQueue(new Runnable() {
			public void run() {
				writeARGB(argbBuffer, x_src, y_src, width, height);
			}
		});
	}
	
	public int getWidth() {
        return canvasWidth;
    }

    public int getHeight() {
        return canvasHeight;
    }

	public void open() {

		// Create a dedicated thread for the GTK main loop
		gtkThread = new GTKThread();
		gtkThread.start();

		// Initialize GTK.
		gtkThread.addToQueue(new Runnable() {
			public void run() {
				initialize(canvasWidth, canvasHeight);
			}
		});

	}

	public void close() {
		gtkThread.stop();
	}

	/* (non-Javadoc)
	 * @see org.thenesis.microbackend.ui.gtk.GTKCanvas#onButtonEvent(int, int, int, int)
	 */
	public void onButtonEvent(boolean pressed, int x, int y, int state) {
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] GTKBackend.buttonEvent(): " + pressed);

		if (pressed) {
			listener.mousePressed(x, y, 0);
		} else {
			listener.mouseReleased(x, y, 0);
		}
	}

	/* (non-Javadoc)
	 * @see org.thenesis.microbackend.ui.gtk.GTKCanvas#onKeyEvent(int, int, int)
	 */
	public void onKeyEvent(boolean pressed, int keyCode, int unicode) {

		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] GTKBackend.keyEvent(): key code: " + keyCode + " char: " + (char) unicode);

		if (pressed) {
			listener.keyPressed(keyCode, (char) unicode, 0);
		} else {
			listener.keyReleased(keyCode, (char) unicode, 0);
		}

	}

	/* (non-Javadoc)
	 * @see org.thenesis.microbackend.ui.gtk.GTKCanvas#onMotionEvent(int, int, int)
	 */
	public void onMotionEvent(int x, int y, int state) {
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] GTKBackend.motionEvent()");
		listener.mouseMoved(x, y, 0);
	}

	/* (non-Javadoc)
	 * @see org.thenesis.microbackend.ui.gtk.GTKCanvas#onWindowDeleteEvent()
	 */
	public void onWindowDeleteEvent() {
		if (Logging.TRACE_ENABLED)
			System.out.println("Window delete event received: ");
		listener.windowClosed();
	}

	/* Native methods */

	native public int initialize(int width, int height);

	//native public int isMainLoopStarted(); 

	native private void writeARGB(int[] argbBuffer, int x_src, int y_src, int width, int height);

	native public int gtkMainIterationDo();

	native public int destroy();

	/**
	 * The GTK thread
	 */
	private class GTKThread implements Runnable {

		private volatile Thread thread;
		private Vector repaintQueue = new Vector();

		public void start() {
			thread = new Thread(GTKThread.this);
			thread.start();
		}

		public void stop() {
			thread = null;
		}

		public void run() {

			// Start GTK loop
			while (Thread.currentThread() == thread) {
				synchronized (repaintQueue) {
					processQueue();
					gtkMainIterationDo();
				}

				Thread.yield();
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// Do nothing
				}
			}

			// Destroy GTK context.
			destroy();

		} // run()

		private void processQueue() {
			if (!repaintQueue.isEmpty()) {
				int size = repaintQueue.size();
				for (int i = 0; i < size; i++) {
					Runnable r = (Runnable) repaintQueue.elementAt(size - 1);
					r.run();
				}
				repaintQueue.removeAllElements();
				repaintQueue.notify();
			}
		}

		public void addToQueue(Runnable r) {
			// Block until queue is empty
			synchronized (repaintQueue) {
				if (repaintQueue.size() > 0) {
					try {
						repaintQueue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				repaintQueue.addElement(r);
			}
		}
	}

}
