/*
 * Copyright  1990-2007 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt).
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions.
 */

package org.thenesis.midpath.opengles.ogles;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.lcdui.Graphics;


import com.sun.jsr239.Errors;
import com.sun.jsr239.GLConfiguration;
import com.sun.midp.lcdui.GameMap;

class NativeEGL10 implements EGL10 {

	static NativeEGL10 theInstance;

	static {
		if (GLConfiguration.supportsEGL11) {
			theInstance = new NativeEGL11();
		} else {
			theInstance = new NativeEGL10();
		}
	}

	static final boolean DEBUG = false;

	// eglCreateWindowSurface strategies
	static final int STRATEGY_USE_WINDOW = 0;
	static final int STRATEGY_USE_PIXMAP = 1;
	static final int STRATEGY_USE_PBUFFER = 2;

	native int _eglGetError();

	native int _eglGetDisplay(int displayID);

	native int _eglInitialize(int display, int[] major_minor);

	native int _eglTerminate(int display);

	native String _eglQueryString(int display, int name);

	native int _eglGetConfigs(int display, int[] configs, int config_size, int[] num_config);

	native int _eglChooseConfig(int display, int[] attrib_list, int[] configs, int config_size, int[] num_config);

	native int _eglGetConfigAttrib(int display, int config, int attribute, int[] value);

	native int _getWindowStrategy(Graphics winGraphics);

	native int _getWindowNativeID(Graphics winGraphics);

	native int _getWindowPixmap(int displayId, int configId, Graphics winGraphics, int width, int height);

	native int _getImagePixmap(int displayId, int configId, Graphics imageGraphics, int width, int height);

	native void _destroyPixmap(int pixmapPtr);

	native void _getWindowContents(Graphics winGraphics, int deltaHeight, int pixmapPointer);

	native void _putWindowContents(Graphics target, int deltaHeight, int pixmapPointer);

	native int _eglCreateWindowSurface(int display, int config, int win, int[] attrib_list);

	native int _eglCreatePixmapSurface(int display, int config, int pixmap, int[] attrib_list);

	native int _eglCreatePbufferSurface(int display, int config, int[] attrib_list);

	native int _eglDestroySurface(int display, int surface);

	native int _eglQuerySurface(int display, int surface, int attribute, int[] value);

	native int _eglCreateContext(int display, int config, int share_context, int[] attrib_list);

	native int _eglDestroyContext(int display, int context);

	native int _eglMakeCurrent(int display, int draw, int read, int context);

	native int _getCurrentContext();

	native int _getCurrentSurface(int readdraw);

	native int _getCurrentDisplay();

	native int _eglQueryContext(int display, int context, int attribute, int[] value);

	native int _eglWaitGL();

	native int _eglWaitNative(int engine);

	native int _eglSwapBuffers(int display, int surface);

	native int _eglCopyBuffers(int display, int surface, Graphics target, int width, int height, int deltaHeight);

	native int _eglSurfaceAttrib(int display, int surface, int attribute, int value);

	native int _eglBindTexImage(int display, int surface, int buffer);

	native int _eglReleaseTexImage(int display, int surface, int buffer);

	native int _eglSwapInterval(int display, int interval);

	private native int _getFullDisplayWidth();

	private native int _getFullDisplayHeight();

	private native int _garbageCollect(boolean fullGC);

	public static NativeEGL10 getInstance() {
		return theInstance;
	}

	void throwIAE(String message) {
		throw new IllegalArgumentException(message);
	}

	/**
	 * Utility method to determine if an attribute list consisting of
	 * (<token>, <value>) pairs is properly terminated by an EGL_NONE
	 * token.
	 */
	boolean isTerminated(int[] attrib_list) {
		if (attrib_list == null) {
			return true; // Empty list is considered terminated
		}

		int idx = 0;
		while (idx < attrib_list.length) {
			if (attrib_list[idx] == EGL_NONE) {
				return true;
			}
			idx += 2;
		}
		return false;
	}

	public synchronized EGLDisplay eglGetDisplay(Object displayID) {
		int _displayId = -1;
		if (displayID == EGL11.EGL_DEFAULT_DISPLAY) {
			_displayId = 0;
		} else {
			throwIAE(Errors.EGL_DISPLAY_NOT_EGL_DEFAULT_DISPLAY);
		}

		int display = _eglGetDisplay(_displayId);
		return NativeEGLDisplay.getInstance(display);
	}

	public synchronized boolean eglInitialize(EGLDisplay display, int[] major_minor) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		if (major_minor != null && major_minor.length < 2) {
			throwIAE(Errors.EGL_MAJOR_MINOR_SHORT);
		}
		boolean retval = EGL_TRUE == _eglInitialize(((NativeEGLDisplay) display).nativeId(), major_minor);

		// Workaround - the underlying engine is really 1.1 but we
		// will only report 1.0 if the system.config file has 
		// "jsr239.supportsEGL11=false"
		if ((!GLConfiguration.supportsEGL11) && (major_minor != null)) {
			major_minor[0] = 1;
			major_minor[1] = 0;
		}

		return retval;
	}

	public synchronized boolean eglTerminate(EGLDisplay display) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		boolean success = EGL_TRUE == _eglTerminate(((NativeEGLDisplay) display).nativeId());

		return success;
	}

	public synchronized String eglQueryString(EGLDisplay display, int name) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		String s = _eglQueryString(((NativeEGLDisplay) display).nativeId(), name);
		return s;
	}

	public synchronized boolean eglGetConfigs(EGLDisplay display, EGLConfig[] configs, int config_size, int[] num_config) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		if ((configs != null) && (configs.length < config_size)) {
			throwIAE(Errors.EGL_CONFIG_SHORT);
		}
		if (num_config != null && num_config.length < 1) {
			throwIAE(Errors.EGL_NUM_CONFIG_SHORT);
		}

		if (config_size < 0) {
			config_size = 0;
		}

		int[] iconfigs = (configs == null) ? null : new int[config_size];
		boolean success = EGL_TRUE == _eglGetConfigs(((NativeEGLDisplay) display).nativeId(), iconfigs, config_size,
				num_config);
		if (success && (configs != null)) {
			for (int i = 0; i < num_config[0]; i++) {
				configs[i] = NativeEGLConfig.getInstance(iconfigs[i]);
			}
		}

		return success;
	}

	// Make a copy of an int array
	int[] clone(int[] a) {
		int len = a.length;
		int[] clone = new int[len];
		System.arraycopy(a, 0, clone, 0, len);
		return clone;
	}

	public synchronized boolean eglChooseConfig(EGLDisplay display, int[] attrib_list, EGLConfig[] configs,
			int config_size, int[] num_config) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		if ((configs != null) && (configs.length < config_size)) {
			throwIAE(Errors.EGL_CONFIG_SHORT);
		}

		// Clone the attribute list and check the clone for termination.
		// This prevents another thread from altering the list between
		// the time of the check and the time it is passed to the GL.
		if (attrib_list != null) {
			attrib_list = clone(attrib_list);
		}
		if (!isTerminated(attrib_list)) {
			throwIAE(Errors.EGL_ATTRIBS_NOT_TERMINATED);
		}
		if (num_config != null && num_config.length < 1) {
			throwIAE(Errors.EGL_NUM_CONFIG_SHORT);
		}

		int[] iconfigs = (configs == null) ? null : new int[config_size];
		boolean success = EGL_TRUE == _eglChooseConfig(((NativeEGLDisplay) display).nativeId(), attrib_list, iconfigs,
				config_size, num_config);
		if (success && configs != null) {
			for (int i = 0; i < config_size; i++) {
				configs[i] = NativeEGLConfig.getInstance(iconfigs[i]);
			}
		}

		return success;
	}

	public synchronized boolean eglGetConfigAttrib(EGLDisplay display, EGLConfig config, int attribute, int[] value) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		if (config == null) {
			throwIAE(Errors.EGL_CONFIG_NULL);
		}
		if (value == null || value.length < 1) {
			throwIAE(Errors.EGL_VALUE_SHORT);
		}

		int displayID = ((NativeEGLDisplay) display).nativeId();
		int configID = ((NativeEGLConfig) config).nativeId();

		boolean retval = EGL_TRUE == _eglGetConfigAttrib(displayID, configID, attribute, value);

		// Workaround for Gerbera bug, see CR 6349801
		if (retval && (attribute == EGL10.EGL_BUFFER_SIZE)) {
			int[] v = new int[1];
			_eglGetConfigAttrib(displayID, configID, EGL_RED_SIZE, v);
			value[0] = v[0];
			_eglGetConfigAttrib(displayID, configID, EGL_GREEN_SIZE, v);
			value[0] += v[0];
			_eglGetConfigAttrib(displayID, configID, EGL_BLUE_SIZE, v);
			value[0] += v[0];
			_eglGetConfigAttrib(displayID, configID, EGL_ALPHA_SIZE, v);
			value[0] += v[0];
		}

		// Workaround for Gerbera bug, see CR 6401394
		// Gerbera returns 0 whereas the value should be EGL_NONE,
		// EGL_SLOW_CONFIG, or EGL_NON_CONFORMANT.
		if (retval && (attribute == EGL10.EGL_CONFIG_CAVEAT)) {
			value[0] = EGL10.EGL_NONE;
		}

		return retval;
	}

	private int createWindowPixmap(int displayId, int configId, Graphics winGraphics, int width, int height) {
		int pixmapPointer;

		// Duplicate mutable image contents
		try {
			pixmapPointer = _getWindowPixmap(displayId, configId, winGraphics, width, height);

		} catch (OutOfMemoryError e) {
			_garbageCollect(false);

			try {
				pixmapPointer = _getWindowPixmap(displayId, configId, winGraphics, width, height);
			} catch (OutOfMemoryError e2) {
				_garbageCollect(true);

				pixmapPointer = _getWindowPixmap(displayId, configId, winGraphics, width, height);
			}
		}
		return pixmapPointer;
	}

	public synchronized EGLSurface eglCreateWindowSurface(EGLDisplay display, EGLConfig config, Object win,
			int[] attrib_list) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		if (config == null) {
			throwIAE(Errors.EGL_CONFIG_NULL);
		}
		// Clone the attribute list and check the clone for termination.
		// This prevents another thread from altering the list between
		// the time of the check and the time it is passed to the GL.
		if (attrib_list != null) {
			attrib_list = clone(attrib_list);
		}
		if (!isTerminated(attrib_list)) {
			throwIAE(Errors.EGL_ATTRIBS_NOT_TERMINATED);
		}

		if (!(win instanceof Graphics)) {
			throwIAE(Errors.EGL_BAD_WINDOW_SURFACE);
		}

		Graphics winGraphics = (Graphics) win;

		int width = _getFullDisplayWidth();
		int height = _getFullDisplayHeight();

		int displayId = ((NativeEGLDisplay) display).nativeId();
		int configId = ((NativeEGLConfig) config).nativeId();

		NativeEGLSurface surface;
		int strategy = _getWindowStrategy(winGraphics);
		if (strategy == STRATEGY_USE_WINDOW) {
			int winId = _getWindowNativeID(winGraphics);
			int surf = _eglCreateWindowSurface(displayId, configId, winId, attrib_list);
			surface = NativeEGLSurface.getInstance(surf, width, height);
		} else if (strategy == STRATEGY_USE_PIXMAP) {
			int pixmapPointer = createWindowPixmap(displayId, configId, winGraphics, width, height);
			int surf = _eglCreatePixmapSurface(displayId, configId, pixmapPointer, attrib_list);
			surface = NativeEGLSurface.getInstance(surf, width, height);
			surface.setPixmapPointer(pixmapPointer);
		} else if (strategy == STRATEGY_USE_PBUFFER) {
			int attrib_size = (attrib_list != null) ? attrib_list.length : 0;
			int[] new_attrib_list = new int[attrib_size + 5];

			int sidx = 0;
			int didx = 0;
			while (sidx < attrib_size - 1) {
				if (attrib_list[sidx] == EGL_WIDTH || attrib_list[sidx] == EGL_HEIGHT) {
					sidx += 2;
					continue;
				} else if (attrib_list[sidx] == EGL_NONE) {
					break;
				}

				new_attrib_list[didx++] = attrib_list[sidx++];
				new_attrib_list[didx++] = attrib_list[sidx++];
			}
			new_attrib_list[didx++] = EGL_WIDTH;
			new_attrib_list[didx++] = width;
			new_attrib_list[didx++] = EGL_HEIGHT;
			new_attrib_list[didx++] = height;
			new_attrib_list[didx] = EGL_NONE;

			int surf = _eglCreatePbufferSurface(displayId, configId, new_attrib_list);
			surface = NativeEGLSurface.getInstance(surf, width, height);
		} else {
			// This should never happen
			throw new RuntimeException(Errors.EGL_CANT_HAPPEN);
		}

		surface.setTarget(winGraphics);
		return surface;
	}

	private int createImagePixmap(int displayId, int configId, Graphics imageGraphics, int width, int height) {
		int pixmapPointer;

		// Duplicate mutable image contents
		try {
			pixmapPointer = _getImagePixmap(displayId, configId, imageGraphics, width, height);

		} catch (OutOfMemoryError e) {
			_garbageCollect(false);

			try {
				pixmapPointer = _getImagePixmap(displayId, configId, imageGraphics, width, height);
			} catch (OutOfMemoryError e2) {
				_garbageCollect(true);

				pixmapPointer = _getImagePixmap(displayId, configId, imageGraphics, width, height);
			}
		}
		return pixmapPointer;
	}

	public synchronized EGLSurface eglCreatePixmapSurface(EGLDisplay display, EGLConfig config, Object pixmap,
			int[] attrib_list) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		if (config == null) {
			throwIAE(Errors.EGL_CONFIG_NULL);
		}
		if (pixmap == null) {
			throwIAE(Errors.EGL_PIXMAP_NULL);
		}
		if (!(pixmap instanceof Graphics)) {
			throwIAE(Errors.EGL_BAD_PIXMAP);
		}

		Graphics imageGraphics = (Graphics) pixmap;
		int width = GameMap.getGraphicsAccess().getGraphicsWidth(imageGraphics);
		int height = GameMap.getGraphicsAccess().getGraphicsHeight(imageGraphics);

		int displayId = ((NativeEGLDisplay) display).nativeId();
		int configId = ((NativeEGLConfig) config).nativeId();

		// Clone the attribute list and check the clone for termination.
		// This prevents another thread from altering the list between
		// the time of the check and the time it is passed to the GL.
		if (attrib_list != null) {
			attrib_list = clone(attrib_list);
		}
		if (!isTerminated(attrib_list)) {
			throwIAE(Errors.EGL_ATTRIBS_NOT_TERMINATED);
		}

		int pixmapPointer = createImagePixmap(displayId, configId, imageGraphics, width, height);

		int surf = _eglCreatePixmapSurface(displayId, configId, pixmapPointer, attrib_list);
		NativeEGLSurface surface = NativeEGLSurface.getInstance(surf, width, height);
		surface.setPixmapPointer(pixmapPointer);
		surface.setTarget(imageGraphics);

		return surface;
	}

	public synchronized EGLSurface eglCreatePbufferSurface(EGLDisplay display, EGLConfig config, int[] attrib_list) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		if (config == null) {
			throwIAE(Errors.EGL_CONFIG_NULL);
		}
		// Clone the attribute list and check the clone for termination.
		// This prevents another thread from altering the list between
		// the time of the check and the time it is passed to the GL.
		if (attrib_list != null) {
			attrib_list = clone(attrib_list);
		}
		if (!isTerminated(attrib_list)) {
			throwIAE(Errors.EGL_ATTRIBS_NOT_TERMINATED);
		}

		int surf = _eglCreatePbufferSurface(((NativeEGLDisplay) display).nativeId(), ((NativeEGLConfig) config).nativeId(),
				attrib_list);

		if (surf != 0) {
			int[] val = new int[1];
			int width, height;

			_eglQuerySurface(((NativeEGLDisplay) display).nativeId(), surf, EGL10.EGL_WIDTH, val);
			width = val[0];

			_eglQuerySurface(((NativeEGLDisplay) display).nativeId(), surf, EGL10.EGL_HEIGHT, val);
			height = val[0];

			NativeEGLSurface surface = NativeEGLSurface.getInstance(surf, width, height);

			return surface;
		} else {
			return EGL_NO_SURFACE;
		}
	}

	public synchronized boolean eglDestroySurface(EGLDisplay display, EGLSurface surface) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		if (surface == null) {
			throwIAE(Errors.EGL_SURFACE_NULL);
		}

		NativeEGLDisplay disp = (NativeEGLDisplay) display;
		NativeEGLSurface surf = (NativeEGLSurface) surface;

		boolean success = EGL_TRUE == _eglDestroySurface(disp.nativeId(), surf.nativeId());

		if (success) {
			int pixmapPtr = surf.getPixmapPointer();
			if (pixmapPtr != 0) {
				_destroyPixmap(pixmapPtr);
				surf.setPixmapPointer(0);
			}

			surf.dispose();
		}

		return success;
	}

	public synchronized boolean eglQuerySurface(EGLDisplay display, EGLSurface surface, int attribute, int[] value) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		if (surface == null) {
			throwIAE(Errors.EGL_SURFACE_NULL);
		}
		if (value == null || value.length < 1) {
			throwIAE(Errors.EGL_VALUE_SHORT);
		}
		return EGL_TRUE == _eglQuerySurface(((NativeEGLDisplay) display).nativeId(), ((NativeEGLSurface) surface)
				.nativeId(), attribute, value);
	}

	public synchronized int eglGetError() {
		NativeGL10.grabContext();
		int retval = _eglGetError();
		return retval;
	}

	public synchronized EGLContext eglCreateContext(EGLDisplay display, EGLConfig config, EGLContext share_context,
			int[] attrib_list) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		if (config == null) {
			throwIAE(Errors.EGL_CONFIG_NULL);
		}
		if (share_context == null) {
			throwIAE(Errors.EGL_SHARE_CONTEXT_NULL);
		}
		// Clone the attribute list and check the clone for termination.
		// This prevents another thread from altering the list between
		// the time of the check and the time it is passed to the GL.
		if (attrib_list != null) {
			attrib_list = clone(attrib_list);
		}
		if (!isTerminated(attrib_list)) {
			throwIAE(Errors.EGL_ATTRIBS_NOT_TERMINATED);
		}

		int contextID = _eglCreateContext(((NativeEGLDisplay) display).nativeId(), ((NativeEGLConfig) config).nativeId(),
				((NativeEGLContext) share_context).nativeId(), attrib_list);

		if (contextID != 0) { // EGL_NO_CONTEXT
			NativeEGLContext context = NativeEGLContext.getInstance(contextID);

			return context;
		} else {
			return EGL_NO_CONTEXT;
		}
	}

	// If the context is current on some thread, flag it for
	// destruction whenever another context (or EGL_NO_CONTEXT)
	// replaces it.  If it is not current on any thread, just delete
	// it outright.
	public synchronized boolean eglDestroyContext(EGLDisplay display, EGLContext context) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		if (context == null) {
			throwIAE(Errors.EGL_CONTEXT_NULL);
		}

		boolean success = true;
		if (GLConfiguration.singleThreaded && NativeGL10.contextsByThread.containsKey(context)) {
			// Mark the context as 'to be destroyed'.  We may need to
			// make an internal call to release the context and
			// restore it later in order to run pending commands; we
			// can only truly destroy it when an application-level
			// call to 'eglMakeCurrent' causes it to be released.

			((NativeEGLContext) context).setDestroyed(true);

			// Return success even though nothing has happened.
		} else {
			NativeGL10.grabContext();
			success = EGL_TRUE == _eglDestroyContext(((NativeEGLDisplay) display).nativeId(), ((NativeEGLContext) context)
					.nativeId());
			if (success) {
				((NativeEGLContext) context).dispose();
			}
		}

		return success;
	}

	public synchronized boolean eglMakeCurrent(EGLDisplay display, EGLSurface draw, EGLSurface read, EGLContext context) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		if (draw == null) {
			// Note: null is _not_ a synonym for EGL_NO_SURFACE
			throwIAE(Errors.EGL_DRAW_NULL);
		}
		if (read == null) {
			// Note: null is _not_ a synonym for EGL_NO_SURFACE
			throwIAE(Errors.EGL_READ_NULL);
		}
		if (context == null) {
			// Note: null is _not_ a synonym for EGL_NO_CONTEXT
			throwIAE(Errors.EGL_CONTEXT_NULL);
		}

		int displayId = ((NativeEGLDisplay) display).nativeId();

		int retVal = _eglMakeCurrent(displayId, ((NativeEGLSurface) draw).nativeId(), ((NativeEGLSurface) read).nativeId(),
				((NativeEGLContext) context).nativeId());

		if (retVal == EGL_TRUE) {
			Thread currentThread = Thread.currentThread();
			NativeGL10.currentContext = context;

			// Update thread-to-context mapping and context state
			if (context == EGL_NO_CONTEXT) {
				// Locate the old context for the current thread
				Object oldContext = NativeGL10.contextsByThread.get(currentThread);
				if (oldContext != null) {
					NativeEGLContext ocimpl = (NativeEGLContext) oldContext;

					// If the old context was previously destroyed by the
					// application, perform the actual destruction now.
					if (ocimpl.isDestroyed()) {
						eglDestroyContext(ocimpl.getDisplay(), ocimpl);
					}

					// Clear the associated thread and surfaces in the
					// context object.
					ocimpl.setBoundThread(null);
					ocimpl.setDisplay(null);
					ocimpl.setDrawSurface(null);
					ocimpl.setReadSurface(null);

					// Remove the old context from the thread map
					NativeGL10.contextsByThread.remove(currentThread);
				}
			} else {
				// Cache the associated thread and surfaces in the
				// context object.
				NativeEGLContext cimpl = (NativeEGLContext) context;
				cimpl.setBoundThread(currentThread);
				cimpl.setDisplay((NativeEGLDisplay) display);
				cimpl.setDrawSurface((NativeEGLSurface) draw);
				cimpl.setReadSurface((NativeEGLSurface) read);

				// Add the new context to the thread map
				NativeGL10.contextsByThread.put(currentThread, context);
			}
		}

		return retVal == EGL_TRUE;
	}

	public synchronized EGLContext eglGetCurrentContext() {
		Thread currentThread = Thread.currentThread();
		EGLContext context = (EGLContext) NativeGL10.contextsByThread.get(currentThread);
		return (context == null) ? EGL_NO_CONTEXT : context;
	}

	public synchronized EGLSurface eglGetCurrentSurface(int readdraw) {
		if (readdraw != EGL_READ && readdraw != EGL_DRAW) {
			throwIAE(Errors.EGL_READDRAW_BAD);
		}

		NativeEGLContext cimpl = (NativeEGLContext) eglGetCurrentContext();
		if (cimpl != EGL_NO_CONTEXT) {
			if (readdraw == EGL_READ) {
				return cimpl.getReadSurface();
			} else {
				return cimpl.getDrawSurface();
			}
		} else {
			return EGL_NO_SURFACE;
		}
	}

	public synchronized EGLDisplay eglGetCurrentDisplay() {
		NativeEGLContext cimpl = (NativeEGLContext) eglGetCurrentContext();
		if (cimpl != EGL_NO_CONTEXT) {
			return cimpl.getDisplay();
		} else {
			return EGL_NO_DISPLAY;
		}
	}

	public synchronized boolean eglQueryContext(EGLDisplay display, EGLContext context, int attribute, int[] value) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		if (context == null) {
			throwIAE(Errors.EGL_CONTEXT_NULL);
		}
		if (value == null || value.length < 1) {
			throwIAE(Errors.EGL_VALUE_SHORT);
		}
		boolean retval = EGL_TRUE == _eglQueryContext(((NativeEGLDisplay) display).nativeId(), ((NativeEGLContext) context)
				.nativeId(), attribute, value);
		return retval;
	}

	public synchronized boolean eglWaitGL() {
		// Ensure all queued commands have been submitted to the GL

		NativeEGLContext cimpl = (NativeEGLContext) eglGetCurrentContext();
		GL currGL = cimpl.getGL();
		((NativeGL10) currGL).qflush();

		NativeGL10.grabContext();
		boolean returnValue = EGL_TRUE == _eglWaitGL();

		NativeEGLSurface currentDrawSurface = cimpl.getDrawSurfaceImpl();

		if (currentDrawSurface != null) {
			final Graphics targetGraphics = currentDrawSurface.getTarget();
			// Creator is null if Graphics is obtained from Image.
			// In case of Image there are no manus and hence no
			// shift is required.
			final Object creator = (targetGraphics != null) ? GameMap.getGraphicsAccess().getGraphicsCreator(
					targetGraphics) : null;
			int deltaHeight = 0;
			if (creator != null) {
				deltaHeight = _getFullDisplayHeight() - GameMap.getGraphicsAccess().getGraphicsHeight(targetGraphics);
			}
			_putWindowContents(targetGraphics, deltaHeight, currentDrawSurface.getPixmapPointer());
		} else {
			// Do nothing
		}

		return returnValue;
	}

	public synchronized boolean eglWaitNative(int engine, Object bindTarget) {
		NativeEGLContext cimpl = (NativeEGLContext) eglGetCurrentContext();
		// IMPL_NOTE: should we really check for cimpl == null here?
		if (cimpl != null) {
			NativeEGLSurface currentDrawSurface = cimpl.getDrawSurfaceImpl();

			if (currentDrawSurface != null) {
				Graphics targetGraphics = currentDrawSurface.getTarget();
				// Creator is null if Graphics is obtained from Image.
				// In case of Image there are no manus and hence no
				// shift is required.
				Object creator = (targetGraphics != null) ? GameMap.getGraphicsAccess().getGraphicsCreator(
						targetGraphics) : null;
				int deltaHeight = 0;
				if (creator != null) {
					deltaHeight = _getFullDisplayHeight()
							- GameMap.getGraphicsAccess().getGraphicsHeight(targetGraphics);
				}
				_getWindowContents(targetGraphics, deltaHeight, currentDrawSurface.getPixmapPointer());
			} else {
				// Do nothing
			}
		}

		NativeGL10.grabContext();
		boolean retval = EGL_TRUE == _eglWaitNative(engine);
		return retval;
	}

	public synchronized boolean eglSwapBuffers(EGLDisplay display, EGLSurface surface) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		if (surface == null) {
			throwIAE(Errors.EGL_SURFACE_NULL);
		}

		// Need revisit - what if pixmap or pbuffer?
		NativeGL10.grabContext();
		boolean retval = EGL_TRUE == _eglSwapBuffers(((NativeEGLDisplay) display).nativeId(), ((NativeEGLSurface) surface)
				.nativeId());
		return retval;
	}

	public synchronized boolean eglCopyBuffers(EGLDisplay display, EGLSurface surface, Object native_pixmap) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		if (surface == null) {
			throwIAE(Errors.EGL_SURFACE_NULL);
		}
		if (native_pixmap == null) {
			throwIAE(Errors.EGL_NATIVE_PIXMAP_NULL);
		}
		if (!(native_pixmap instanceof Graphics)) {
			throwIAE(Errors.EGL_BAD_PIXMAP);
		}

		Graphics imageGraphics = (Graphics) native_pixmap;

		NativeGL10.grabContext();

		NativeEGLSurface surf = (NativeEGLSurface) surface;

		final Graphics targetGraphics = surf.getTarget();
		// Creator is null if Graphics is obtained from Image.
		// In case of Image there are no manus and hence no
		// shift is required.
		final Object creator = (targetGraphics != null) ? GameMap.getGraphicsAccess()
				.getGraphicsCreator(targetGraphics) : null;
		int deltaHeight = 0;
		if (creator != null) {
			deltaHeight = _getFullDisplayHeight() - GameMap.getGraphicsAccess().getGraphicsHeight(targetGraphics);
		}
		int pixmapPointer;

		boolean retval;
		// Duplicate mutable image contents
		try {
			retval = EGL_TRUE == _eglCopyBuffers(((NativeEGLDisplay) display).nativeId(), surf.nativeId(), imageGraphics,
					surf.getWidth(), surf.getHeight(), deltaHeight);
		} catch (OutOfMemoryError e) {
			_garbageCollect(false);

			try {
				retval = EGL_TRUE == _eglCopyBuffers(((NativeEGLDisplay) display).nativeId(), surf.nativeId(),
						imageGraphics, surf.getWidth(), surf.getHeight(), deltaHeight);
			} catch (OutOfMemoryError e2) {
				_garbageCollect(true);

				retval = EGL_TRUE == _eglCopyBuffers(((NativeEGLDisplay) display).nativeId(), surf.nativeId(),
						imageGraphics, surf.getWidth(), surf.getHeight(), deltaHeight);
			}
		}
		return retval;
	}

	public NativeEGL10() {
	}
}
