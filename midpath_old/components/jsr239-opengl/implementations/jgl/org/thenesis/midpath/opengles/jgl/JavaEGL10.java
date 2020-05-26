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
package org.thenesis.midpath.opengles.jgl;

import java.util.Hashtable;

import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import jgl.GLBackend;
import jgl.context.GLContext;

import org.thenesis.midpath.opengles.AbstractJavaEGL10;
import org.thenesis.midpath.opengles.JavaEGLSurface;

public class JavaEGL10 extends AbstractJavaEGL10 {

	private EGLContext currentContext;
	private EGLSurface currentSurface;
	
	public void makeCurrent(EGLDisplay display, EGLSurface draw, EGLSurface read, EGLContext context) {
		
		if ((draw == EGL_NO_SURFACE) || (read == EGL_NO_SURFACE) || (context == EGL_NO_CONTEXT)) {
			return;
		}
			
		// Call glXMakeCurrent only if needed (it clears all the jGL buffers !)
		if ((currentContext != context) || (currentSurface != draw)) {
			currentContext = context;
			currentSurface = draw;
			JavaEGLContext jContext = (JavaEGLContext) context;
			MIDPBackend backend = new MIDPBackend(((JavaEGLSurface) draw));
			GLContext jglContext = jContext.getJGLContext();
			JavaGL10 gl = (JavaGL10)jContext.getGL();
			gl.getJGL().glXMakeCurrent(jglContext, backend);
		}
	}
	
	public boolean eglWaitGL() {
		super.eglWaitGL();
		
		// Make the surface opaque. TODO: don't do it the EGLConfig required surface transparency
		JavaEGLSurface s = (JavaEGLSurface)currentSurface;
		int[] buffer = s.getBuffer();
		int length = buffer.length;
		for (int i = 0; i < length; i++) {
			buffer[i] = buffer[i] | 0xFF000000;
		}
		return true;
	}
	
	public Hashtable getContextsByThread() {
		return JavaGL10.contextsByThread;
	}

	public void grabContext() {
		JavaGL10.grabContext();
	}

	public void setCurrentContext(EGLContext context) {
		JavaGL10.currentContext = context;
	}
	
	public EGLContext createEGLContext() {
		return new JavaEGLContext();
	}

	private class MIDPBackend implements GLBackend {
		
		private JavaEGLSurface surface;

		public MIDPBackend(JavaEGLSurface surface) {
			this.surface = surface;
		}

		public int[] getColorBuffer(int size) {
			return surface.getBuffer();
		}

		public float[] getDepthBuffer(int size) {
			return new float[size];
		}

		public int getHeight() {
			return surface.getHeight();
		}

		public int[] getStencilBuffer(int size) {
			return new int[size];
		}

		public int getWidth() {
			return surface.getWidth();
		}

		public int getX() {
			return 0;
		}

		public int getY() {
			return 0;
		}

		public void sync() {
			// Not used because direct drawing
		}

		public void updatePixels(int w, int h, int[] pix, int off, int scan) {
			// Not used because direct drawing
		}
		
	}


	
}
