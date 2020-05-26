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
package org.thenesis.microbackend.ui.fb;

import org.thenesis.microbackend.ui.BackendEventListener;
import org.thenesis.microbackend.ui.Configuration;
import org.thenesis.microbackend.ui.Logging;
import org.thenesis.microbackend.ui.NullBackendEventListener;
import org.thenesis.microbackend.ui.UIBackend;

import com.sun.cldchi.jvm.JVM;

public class FBBackend implements UIBackend {

	private BackendEventListener listener = new NullBackendEventListener();
	private PollEventThread eventThread;

	private int canvasWidth;
	private int canvasHeight;
	private String keyboardDeviceName;
	private String fbDeviceName;
	private String mouseDeviceName;
	private String touchscreenDeviceName;

	private int[] copiedARGBBuffer;

	private static final int MOUSE_WIDTH = 5;
	private static final int MOUSE_HEIGHT = 5;
	int[] mouseImage = { 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFFFF0000,
			0xFFFF0000, 0xFFFF0000, 0xFF000000, 0xFF000000, 0xFFFF0000, 0xFFFF0000, 0xFFFF0000, 0xFF000000, 0xFF000000,
			0xFFFF0000, 0xFFFF0000, 0xFFFF0000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000, 0xFF000000 };
	private int mouseX, mouseY, mouseButton;

	static {
		JVM.loadLibrary("libmicrobackendfb.so");
		//System.loadLibrary("microbackendfb");
	}

	public FBBackend(int width, int height, String keyboardDeviceName, String mouseDeviceName,
			String touchscreenDeviceName, String fbDeviceName) {
		this.keyboardDeviceName = keyboardDeviceName;
		this.fbDeviceName = fbDeviceName;
		this.mouseDeviceName = mouseDeviceName;
		this.touchscreenDeviceName = touchscreenDeviceName;
		this.canvasWidth = width;
		this.canvasHeight = height;
	}

	public FBBackend() {

	}

	/* UIBackend interface */

	public void configure(Configuration conf, int width, int height) {
		canvasWidth = width;
		canvasHeight = height;
		fbDeviceName = conf.getParameterDefault("org.thenesis.microbackend.ui.fb.framebufferDevice", null);
		keyboardDeviceName = conf.getParameterDefault("org.thenesis.microbackend.ui.fb.keyboardDevice", null);
		mouseDeviceName = conf.getParameterDefault("org.thenesis.microbackend.ui.fb.mouseDevice", null);
		touchscreenDeviceName = conf.getParameterDefault("org.thenesis.microbackend.ui.fb.touchscreenDevice", null);
	}

	public void setBackendEventListener(BackendEventListener listener) {
		this.listener = listener;
	}

	public void open() {

		copiedARGBBuffer = new int[canvasWidth * canvasHeight];

		// Initialize the framebuffer.
		if (!initialize(keyboardDeviceName, mouseDeviceName, touchscreenDeviceName, fbDeviceName, canvasWidth,
				canvasHeight)) {
			System.err.println("Can't open the framebuffer");
			return;
		}

		eventThread = new PollEventThread();
		eventThread.start();

	}

	public void close() {
		eventThread.stop();
	}

	public synchronized void updateARGBPixels(final int[] argbBuffer, final int x, final int y, final int w, final int h) {
		System.arraycopy(argbBuffer, 0, copiedARGBBuffer, 0, copiedARGBBuffer.length);
		drawARGB(argbBuffer, y * canvasWidth + x, canvasWidth, x, y, w, h);
		drawARGB(mouseImage, 0, MOUSE_WIDTH, mouseX, mouseY, MOUSE_WIDTH, MOUSE_HEIGHT);
	}
	
	public int getWidth() {
        return canvasWidth;
    }

    public int getHeight() {
        return canvasHeight;
    }

	/* Event callbacks */

	public void onKeyEvent(boolean pressed, int keyCode, char c) {
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] FBBackend.keyEvent(): key code: " + keyCode + " char: " + c + " (" + ((int) c)
					+ ")");

		if (pressed) {
			listener.keyPressed(keyCode, c, 0);
		} else {
			listener.keyReleased(keyCode, c, 0);
		}
	}

	public void onMouseButtonEvent(int x, int y, int button) {

		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] FBBackend.buttonEvent(): " + ((button != 0) ? "PRESSED" : "RELEASED"));

		//		if (((button & BUTTON_MASK) == BUTTON_MASK) || ((button & LEFT_BUTTON_MASK) == LEFT_BUTTON_MASK)) {
		//			System.out.println("Left button pressed: " + x + " " + y);
		//		}
		//		if ((button & RIGHT_BUTTON_MASK) == RIGHT_BUTTON_MASK) {
		//			System.out.println("Right button pressed: " + x + " " + y);
		//		}
		if (button != 0) {
			listener.mousePressed(x, y, 0);
		} else {
			listener.mouseReleased(x, y, 0);
		}

	}

	public void onMouseMoveEvent(int x, int y) {
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] FBBackend.motionEvent()");

		listener.mouseMoved(x, y, 0);
	}

	public synchronized void onRawMouseEvent(int dx, int dy, int button) {

		//System.out.println("raw mouse event: dx=" + dx + " dy=" + dy + " button=" + button);

		// Redraw the part of screen which was hidden by the mouse
		drawARGB(copiedARGBBuffer, mouseY * canvasWidth + mouseX, canvasWidth, mouseX, mouseY, MOUSE_WIDTH,
				MOUSE_HEIGHT);

		// Set mouse absolute position
		boolean moved = false;
		if (dx != 0) {
			moved = true;
			mouseX += dx;
			if (mouseX < 0)
				mouseX = 0;
			if (mouseX > canvasWidth)
				mouseX = canvasWidth;
		}
		if (dy != 0) {
			moved = true;
			mouseY += dy;
			if (mouseY < 0)
				mouseY = 0;
			if (mouseY > canvasHeight)
				mouseY = canvasHeight;
		}

		if (button != mouseButton) {
			onMouseButtonEvent(mouseX, mouseY, button);
		}
		if (moved) {
			onMouseMoveEvent(mouseX, mouseY);
		}

		// Draw the mouse
		drawARGB(mouseImage, 0, MOUSE_WIDTH, mouseX, mouseY, MOUSE_WIDTH, MOUSE_HEIGHT);

	}

	public synchronized void onRawTouchscreenEvent(int rawX, int rawY, int button) {

		//System.out.println("raw touchscreen event: rawX=" + x + " rawY=" + y + " button=" + button);

		// Redraw the part of screen which was hidden by the mouse
		drawARGB(copiedARGBBuffer, mouseY * canvasWidth + mouseX, canvasWidth, mouseX, mouseY, MOUSE_WIDTH,
				MOUSE_HEIGHT);

		mouseX = rawX;
		mouseY = rawY;
		onMouseButtonEvent(mouseX, mouseY, button);

		// Draw the mouse
		drawARGB(mouseImage, 0, MOUSE_WIDTH, mouseX, mouseY, MOUSE_WIDTH, MOUSE_HEIGHT);

	}

	/* Native methods */

	native private boolean initialize(String keyboardDeviceName, String mouseDeviceName, String touchscreenDeviceName,
			String fbDeviceName, int width, int height);

	/** 
	 * Renders a series of device-independent ARGB values in a specified region
	 * @param rgbData an array of ARGB values in the format
	 * <code>0xAARRGGBB</code>
	 * @param offset the array index of the first ARGB value
	 * @param scanlength the relative array offset between the
	 * corresponding pixels in consecutive rows in the
	 * <code>rgbData</code> array
	 * @param x the horizontal location of the region to be rendered
	 * @param y the vertical location of the region to be rendered
	 * @param width the width of the region to be rendered
	 * @param height the height of the region to be rendered
	 * @param processAlpha <code>true</code> if <code>rgbData</code>
	 * has an alpha channel, false if all pixels are fully opaque
	 */
	native private void drawARGB(int[] argbBuffer, int offset, int scanlength, int x, int y, int width, int height);

	native private void eventLoop();

	native private int quit();

	/**
	 * An event thread which polls events from the native layer 
	 */
	private class PollEventThread implements Runnable {

		private Thread thread;

		public void start() {
			thread = new Thread(PollEventThread.this);
			thread.start();
		}

		public void stop() {
			// Quit the event loop
			quit();

			// Wait a bit that the event thread is stopped
			try {
				while (thread.isAlive()) {
					Thread.sleep(1);
				}
			} catch (InterruptedException e) {
			}
		}

		public void run() {
			eventLoop();
		}
	}

}
