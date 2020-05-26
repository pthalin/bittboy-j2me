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
package org.thenesis.midpath.ui.toolkit.virtual;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.FontPeer;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.UIToolkit;

import org.thenesis.microbackend.ui.BackendEventListener;
import org.thenesis.microbackend.ui.KeyConstants;
import org.thenesis.microbackend.ui.NullBackend;
import org.thenesis.microbackend.ui.UIBackend;
import org.thenesis.microbackend.ui.UIBackendFactory;

import com.sun.midp.events.EventMapper;
import com.sun.midp.events.EventQueue;
import com.sun.midp.events.EventTypes;
import com.sun.midp.events.NativeEvent;
import com.sun.midp.lcdui.EventConstants;
import com.sun.midp.log.LogChannels;
import com.sun.midp.log.Logging;
import com.sun.midp.main.Configuration;
import com.sun.midp.main.ConfigurationProperties;

public class VirtualToolkit extends UIToolkit {

	private VirtualSurface rootSurface;
	private VirtualGraphics rootPeer;
	private VirtualBackend backend;

	public VirtualToolkit() {
	}

	public void initialize(int w, int h) {
		
		String backendName = Configuration.getPropertyDefault("org.thenesis.microbackend.ui.backend", "null");
		if (Logging.TRACE_ENABLED) {
			System.out.println("[DEBUG] VirtualToolkit.initialize(): backendName: " + backendName);
		}
		
		// Create the backend
		UIBackend b = UIBackendFactory.createBackend(backendName);
		if (b == null) {
			System.out.println("[WARNING] Backend '" + backendName + "' was not found. Switching to NULL backend...");
			b = new NullBackend();
		}
		
		// Wrap it
		backend = new VirtualBackendImpl(b, w, h);
		try {
			backend.open();
		} catch (IOException e) {
			if (Logging.REPORT_LEVEL <= Logging.ERROR)
				Logging.report(Logging.ERROR, LogChannels.LC_HIGHUI, "Can't open '" + backendName + "' backend");
			e.printStackTrace();
		}
		
		rootSurface = backend.getRootSurface();
		rootPeer = new VirtualGraphics(rootSurface);
	}

	public Graphics getRootGraphics() {
		return rootPeer;
	}

	public Graphics createGraphics(Image image) {
		if (image instanceof VirtualImage) {
			return new VirtualGraphics(((VirtualImage) image).surface);
		} else {
			// FIXME ??
			return null;
		}
	}

	public void refresh(int displayId, int x, int y, long widht, long heigth) {

		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] Toolkit.refresh(): x=" + x + " y=" + y + " widht=" + widht + " heigth="
					+ heigth);

		backend.updateSurfacePixels(x, y, widht, heigth);
	}

	public Image createImage(int w, int h) {
		return new VirtualImage(w, h);
	}

	public Image createImage(Image source) {
		if (!source.isMutable()) {
			return source;
		}
		return new VirtualImage((VirtualImage) source);
	}

	public Image createImage(byte[] imageData, int imageOffset, int imageLength) throws IOException {
		return new VirtualImage(imageData, imageOffset, imageLength);
	}

	public Image createImage(InputStream stream) throws IOException {
		return new VirtualImage(stream);
	}

	public Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha) throws IOException {
		return new VirtualImage(rgb, width, height, processAlpha);
	}

	public Image createImage(Image image, int x, int y, int width, int height, int transform) throws IOException {
		return new VirtualImage(image, x, y, width, height, transform);
	}

	public EventMapper getEventMapper() {
		return backend.getEventMapper();
	}

	/**
	 * Construct a new FontPeer object
	 *
	 * @param face The face to use to construct the Font
	 * @param style The style to use to construct the Font
	 * @param size The point size to use to construct the Font
	 */
	public FontPeer createFontPeer(int face, int style, int size) {

		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG]VirtualToolkit.createFontPeer(): size=" + size);

		String fontRendererName = Configuration.getPropertyDefault("org.thenesis.midpath.ui.fontRenderer", "raw");
		if (fontRendererName.equalsIgnoreCase("BDF")) {
			return new BDFFontPeer(face, style, size);
		} else {
			return new RawFontPeer(face, style, size);
		}
		
		//return new RawFontPeer(face, style, size);
		//return new BDFFontPeer(face, style, size);
	}

	public Image createImage(int[] rgb, int width, int height, boolean processAlpha) {
		return new VirtualImage(rgb, width, height, processAlpha);
	}
	
	VirtualBackend getBackend() {
		return backend;
	}

	public void close() {
		backend.close();
	}
	
}

/** Wrapper around UIBackend */
class VirtualBackendImpl implements VirtualBackend {

	private VirtualSurface rootVirtualSurface;
	private GenericEventMapper eventMapper = new GenericEventMapper();
	private VirtualBackendEventListener listener = new VirtualBackendEventListener(eventMapper);
	private UIBackend backend;
	

	public VirtualBackendImpl(UIBackend backend, int w, int h) {
		this.backend = backend;
		rootVirtualSurface = new VirtualSurfaceImpl(w, h);
		
		//Copy MIDPath config in the backend config
		ConfigurationProperties properties = Configuration.getAllProperties();
		org.thenesis.microbackend.ui.Configuration backendConfig = new org.thenesis.microbackend.ui.Configuration();
		for (int i = 0; i < properties.size(); i++) {
			backendConfig.addParameter(properties.getKeyAt(i), properties.getValueAt(i));
		}
		backend.setBackendEventListener(listener);
		backend.configure(backendConfig, w, h);
	}

	public EventMapper getEventMapper() {
		return eventMapper;
	}

	public VirtualSurface createSurface(int w, int h) {
		return new VirtualSurfaceImpl(w, h);
	}

	public VirtualSurface getRootSurface() {
		return rootVirtualSurface;
	}

	public void updateSurfacePixels(int x, int y, long width, long height) {
		backend.updateARGBPixels(rootVirtualSurface.data, x, y, (int) width, (int) height);
	}
	
	public void open() throws IOException {
		backend.open();
	}

	public void close() {
		backend.close();
	}

	private class VirtualSurfaceImpl extends VirtualSurface {
		public VirtualSurfaceImpl(int w, int h) {
			data = new int[w * h];
			this.width = w;
			this.height = h;
		}
	}
	
}

/**
 * Listens events coming from the UIBackend and send it to the event queue
 */
class VirtualBackendEventListener implements BackendEventListener {

	private boolean dragEnabled = false;
	private GenericEventMapper eventMapper;

	public VirtualBackendEventListener(GenericEventMapper eventMapper) {
		this.eventMapper = eventMapper;
	}

	private void fireKeyEvent(boolean pressed, int keycode, char c, int modifiers) {

		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] VirtualBackendEventListener.fireKeyEvent: key code: " + keycode + " char: " + c);

		NativeEvent nativeEvent = new NativeEvent(EventTypes.KEY_EVENT);
		// Set event type (intParam1)
		if (pressed) {
			nativeEvent.intParam1 = EventConstants.PRESSED;
		} else {
			nativeEvent.intParam1 = EventConstants.RELEASED;
		}
		
		// Filter unwanted events 
		if ((keycode == KeyConstants.VK_SHIFT) || (keycode == KeyConstants.VK_CONTROL) || (keycode == KeyConstants.VK_ALT)) {
			return;
		}
		
		// Set event key code (intParam2)
		int internalCode = eventMapper.mapToInternalEvent(keycode, c);
		nativeEvent.intParam2 = internalCode;
		//System.out.println("[DEBUG] VirtualBackendEventListener.fireKeyEvent: key code: " + keycode + " char: " + c + " internalCode: " + internalCode);
		
		// Set event source (intParam4). Fake display with id=1
		nativeEvent.intParam4 = 1;

		EventQueue.getEventQueue().post(nativeEvent);

	}

	public void keyPressed(int keycode, char c, int modifiers) {
		fireKeyEvent(true, keycode, c, modifiers);
	}

	public void keyReleased(int keycode, char c, int modifiers) {
		fireKeyEvent(false, keycode, c, modifiers);
	}

	public void mouseMoved(int x, int y, int modifiers) {
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] VirtualBackendEventListener.mouseMoved(): drag enabled ? " + dragEnabled);

		if (dragEnabled) {
			NativeEvent nativeEvent = new NativeEvent(EventTypes.PEN_EVENT);
			nativeEvent.intParam1 = EventConstants.DRAGGED; // Event type
			nativeEvent.intParam2 = x; // x
			nativeEvent.intParam3 = y; // y
			// Set event source (intParam4). Fake display with id=1
			nativeEvent.intParam4 = 1;

			EventQueue.getEventQueue().post(nativeEvent);
		}

	}

	private void fireMouseEvent(boolean pressed, int x, int y, int modifiers) {
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] VirtualBackendEventListener.fireMouseEvent()");

		NativeEvent nativeEvent = new NativeEvent(EventTypes.PEN_EVENT);

		if (pressed) {
			dragEnabled = true;
			nativeEvent.intParam1 = EventConstants.PRESSED; // Event type
		} else {
			dragEnabled = false;
			nativeEvent.intParam1 = EventConstants.RELEASED; // Event type
		}

		nativeEvent.intParam2 = x; // x
		nativeEvent.intParam3 = y; // y
		// Set event source (intParam4). Fake display with id=1
		nativeEvent.intParam4 = 1;

		EventQueue.getEventQueue().post(nativeEvent);
	}

	public void mousePressed(int x, int y, int modifiers) {
		fireMouseEvent(true, x, y, modifiers);
	}

	public void mouseReleased(int x, int y, int modifiers) {
		fireMouseEvent(false, x, y, modifiers);
	}

	public void windowClosed() {
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] VirtualBackendEventListener.windowClosed(): Window delete event received");

		NativeEvent nativeEvent = new NativeEvent(EventTypes.SHUTDOWN_EVENT);
		EventQueue.getEventQueue().post(nativeEvent);
	}
	
   /* Note: if needed, to refresh screen from event thread:
		Display d = BaseMIDletSuiteLauncher.displayContainer.findDisplayById(1).getDisplay();
		d.graphicsQ.queueRefresh(0, 0, rootVirtualSurface.width, rootVirtualSurface.height);
		d.scheduleRepaint();
    */

}