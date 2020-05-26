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
package org.thenesis.midpath.opengles;

import java.util.Hashtable;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.egl.EGLToolkit;
import javax.microedition.lcdui.Graphics;

//import jgl.GLBackend;
//import jgl.context.GLContext;

import org.thenesis.midpath.ui.toolkit.virtual.VirtualGraphics;

import com.sun.jsr239.Errors;
import com.sun.jsr239.GLConfiguration;
import com.sun.midp.lcdui.GameMap;

public abstract class AbstractJavaEGL10 implements EGL10 {

	private JavaEGLConfig defaultConfig = new JavaEGLConfig();
	
	private int largestPBufferWidth = 1024;
	private int largestPBufferHeight = 1024;

//	public static JavaEGL10 getInstance() {
//		return theInstance;
//	}

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

	//	 Make a copy of an int array
	int[] clone(int[] a) {
		int len = a.length;
		int[] clone = new int[len];
		System.arraycopy(a, 0, clone, 0, len);
		return clone;
	}

	/* EGL10 interface */

	public boolean eglChooseConfig(EGLDisplay display, int[] attrib_list, EGLConfig[] configs, int config_size,
			int[] num_config) {
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
		
		// FIXME return false if requested config is not compatible

		num_config[0] = 1;

		if (configs != null) {
			for (int i = 0; i < config_size; i++) {
				configs[i] = defaultConfig;
			}
		}

		return true;
	}

	public boolean eglCopyBuffers(EGLDisplay display, EGLSurface surface, Object native_pixmap) {
		// TODO Auto-generated method stub
		return false;
	}

	public EGLContext eglCreateContext(EGLDisplay display, EGLConfig config, EGLContext share_context, int[] attrib_list) {
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

		if (share_context == EGL_NO_CONTEXT) { // EGL_NO_CONTEXT
			return createEGLContext();
		} else {
			return EGL_NO_CONTEXT;
		}

	}

	public EGLSurface eglCreatePbufferSurface(EGLDisplay display, EGLConfig config, int[] attrib_list) {
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

		int[] val = new int[1];
		int width = 0; // Default value in specs
		int height = 0; // Default value in specs
		
		if(getValue(attrib_list, EGL_WIDTH, val)) {
			width = val[0];	
		}
		if(getValue(attrib_list, EGL_HEIGHT, val)) {
			height = val[0];
		}

		EGLSurface surface = new JavaEGLSurface(new int[width * height], width, height);

		return surface;

	}

	public EGLSurface eglCreatePixmapSurface(EGLDisplay display, EGLConfig config, Object pixmap,
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

		// FIXME Remove cast to VirtualGraphics (by using GraphicsAccess tunnel) ?
		VirtualGraphics imageGraphics = (VirtualGraphics) pixmap;
		int width = GameMap.getGraphicsAccess().getGraphicsWidth(imageGraphics);
		int height = GameMap.getGraphicsAccess().getGraphicsHeight(imageGraphics);

		// Clone the attribute list and check the clone for termination.
		// This prevents another thread from altering the list between
		// the time of the check and the time it is passed to the GL.
		if (attrib_list != null) {
			attrib_list = clone(attrib_list);
		}
		if (!isTerminated(attrib_list)) {
			throwIAE(Errors.EGL_ATTRIBS_NOT_TERMINATED);
		}
		
		JavaEGLSurface surface = new JavaEGLSurface(imageGraphics.getSurface().data, width, height);
		
		surface.setTarget(imageGraphics);
		return surface;
	}

	public EGLSurface eglCreateWindowSurface(EGLDisplay display, EGLConfig config, Object win,
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

//		 FIXME Remove cast to VirtualGraphics (by using GraphicsAccess tunnel) ?
		VirtualGraphics imageGraphics = (VirtualGraphics) win;
		int width = GameMap.getGraphicsAccess().getGraphicsWidth(imageGraphics);
		int height = GameMap.getGraphicsAccess().getGraphicsHeight(imageGraphics);

		// Clone the attribute list and check the clone for termination.
		// This prevents another thread from altering the list between
		// the time of the check and the time it is passed to the GL.
		if (attrib_list != null) {
			attrib_list = clone(attrib_list);
		}
		if (!isTerminated(attrib_list)) {
			throwIAE(Errors.EGL_ATTRIBS_NOT_TERMINATED);
		}
		
		JavaEGLSurface surface = new JavaEGLSurface(imageGraphics.getSurface().data, width, height);

		surface.setTarget(imageGraphics);
		return surface;
	}

	public boolean eglDestroyContext(EGLDisplay display, EGLContext context) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean eglDestroySurface(EGLDisplay display, EGLSurface surface) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean eglGetConfigAttrib(EGLDisplay display, EGLConfig config, int attribute, int[] value) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean eglGetConfigs(EGLDisplay display, EGLConfig[] configs, int config_size, int[] num_config) {
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

		// One config only
		num_config[0] = 1;

		if (configs != null) {
			for (int i = 0; i < num_config[0]; i++) {
				configs[i] = defaultConfig;
			}
		}

		return true;
	}

	public EGLContext eglGetCurrentContext() {
		Thread currentThread = Thread.currentThread();
		EGLContext context = (EGLContext) getContextsByThread().get(currentThread);
		return (context == null) ? EGL_NO_CONTEXT : context;
	}

	public EGLDisplay eglGetCurrentDisplay() {
		AbstractJavaEGLContext cimpl = (AbstractJavaEGLContext) eglGetCurrentContext();
		if (cimpl != EGL_NO_CONTEXT) {
			return cimpl.getDisplay();
		} else {
			return EGL_NO_DISPLAY;
		}
	}

	public EGLSurface eglGetCurrentSurface(int readdraw) {
		if (readdraw != EGL_READ && readdraw != EGL_DRAW) {
			throwIAE(Errors.EGL_READDRAW_BAD);
		}

		AbstractJavaEGLContext cimpl = (AbstractJavaEGLContext) eglGetCurrentContext();
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

	public EGLDisplay eglGetDisplay(Object native_display) {
		if (native_display != EGL11.EGL_DEFAULT_DISPLAY) {
			throwIAE(Errors.EGL_DISPLAY_NOT_EGL_DEFAULT_DISPLAY);
		}

		return new JavaEGLDisplay();
	}

	public int eglGetError() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean eglInitialize(EGLDisplay display, int[] major_minor) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		if (major_minor != null && major_minor.length < 2) {
			throwIAE(Errors.EGL_MAJOR_MINOR_SHORT);
		}

		// Workaround - the underlying engine is really 1.1 but we
		// will only report 1.0 if the system.config file has 
		// "jsr239.supportsEGL11=false"
		if ((!GLConfiguration.supportsEGL11) && (major_minor != null)) {
			major_minor[0] = 1;
			major_minor[1] = 0;
		}

		return true;
	}

	public boolean eglMakeCurrent(EGLDisplay display, EGLSurface draw, EGLSurface read, EGLContext context) {
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

		Thread currentThread = Thread.currentThread();
		setCurrentContext(context);

		// Update thread-to-context mapping and context state
		if (context == EGL_NO_CONTEXT) {
			// Locate the old context for the current thread
			Object oldContext = getContextsByThread().get(currentThread);
			if (oldContext != null) {
				AbstractJavaEGLContext ocimpl = (AbstractJavaEGLContext) oldContext;

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
				getContextsByThread().remove(currentThread);
			}
		} else {
			// Cache the associated thread and surfaces in the
			// context object.
			AbstractJavaEGLContext cimpl = (AbstractJavaEGLContext) context;
			cimpl.setBoundThread(currentThread);
			cimpl.setDisplay((JavaEGLDisplay) display);
			cimpl.setDrawSurface((JavaEGLSurface) draw);
			cimpl.setReadSurface((JavaEGLSurface) read);

			// Add the new context to the thread map
			getContextsByThread().put(currentThread, context);
		}
		
	
		makeCurrent(display, draw, read, context);
		
		return true;
	}
	
	public abstract void makeCurrent(EGLDisplay display, EGLSurface draw, EGLSurface read, EGLContext context);
	
	public abstract Hashtable getContextsByThread();
	
	public abstract void setCurrentContext(EGLContext context);
	
	public abstract void grabContext();
	
	public abstract EGLContext createEGLContext();

	public boolean eglQueryContext(EGLDisplay display, EGLContext context, int attribute, int[] value) {
		// TODO Auto-generated method stub
		return false;
	}

	public String eglQueryString(EGLDisplay display, int name) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean eglQuerySurface(EGLDisplay display, EGLSurface surface, int attribute, int[] value) {
		
		boolean success = true;
		int val = 0;
		JavaEGLSurface surfaceImpl = (JavaEGLSurface) surface;
		
		switch (attribute) {
		case EGL_CONFIG_ID:
			val = surfaceImpl.getId();
			break;
		case EGL_WIDTH:
			val = surfaceImpl.getWidth();
			break;
		case EGL_HEIGHT:
			val = surfaceImpl.getHeight();
			break;
		case EGL_LARGEST_PBUFFER:
			val = surfaceImpl.isLargestPBuffer() ? EGL_TRUE : EGL_FALSE;
			break;
			/*case EGL_TEXTURE_FORMAT: // (1.1 only). Returns format of texture. Possible values are EGL_NO_TEXTURE, EGL_TEXTURE_RGB, and EGL_TEXTURE_RGBA.
			 case EGL_TEXTURE_TARGET: // (1.1 only) Returns type of texture. Possible values are EGL_NO_TEXTURE, or EGL_TEXTURE_2D.
			 case EGL_MIPMAP_TEXTURE: // (1.1 only) Returns EGL_TRUE if texture has mipmaps, EGL_FALSE otherwise.
			 case EGL_MIPMAP_LEVEL: // (1.1 only) Specifies whether storage for mipmaps should be allocated. Space for mipmaps will be set aside if the attribute value is EGL_TRUE and EGL_TEXTURE_FORMAT is not EGL_NO_TEXTURE. The default value is EGL_FALSE.
			 */
		default:
			success = false;
		}
		
		value[0] = val;
		
		return success;

	}

	public boolean eglSwapBuffers(EGLDisplay display, EGLSurface surface) {
		//	Nothing to do here because rendering is direct
		return true;
	}

	public boolean eglTerminate(EGLDisplay display) {
		if (display == null) {
			throwIAE(Errors.EGL_DISPLAY_NULL);
		}
		return true;
	}

	public boolean eglWaitGL() {
		grabContext();
		// Nothing to do here because GL painting is done synchronously
		return true;
	}

	public boolean eglWaitNative(int engine, Object bindTarget) {
		// Nothing to do here because MIDP2 painting is done synchronously
		grabContext();
		return true;
	}
	
	private boolean getValue(int[] attrib_list, int attribute, int[] value) {
		if (attrib_list == null) {
			return false;
		}
		
		for (int i = 0; i < attrib_list.length; i++) {
			if (attrib_list[i] == attribute) {
				value[0] = attrib_list[i + 1];
				return true;
			} 
		}
		return false;
	}
	

}
