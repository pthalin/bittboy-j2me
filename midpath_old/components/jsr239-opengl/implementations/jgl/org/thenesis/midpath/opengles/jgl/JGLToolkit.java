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

import javax.microedition.khronos.egl.EGL;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.egl.EGLToolkit;

import org.thenesis.midpath.opengles.JavaEGLDisplay;
import org.thenesis.midpath.opengles.JavaEGLSurface;

public class JGLToolkit extends EGLToolkit {
	
	private JavaEGL10 egl = new JavaEGL10();
	
	private EGLContext noContext = new JavaEGLContext();
	private EGLDisplay noDisplay = new JavaEGLDisplay();
	private EGLSurface noSurface = new JavaEGLSurface(null, 0, 0);

	public EGL getEGL() {
		return egl;
	}

	public EGLContext getNoContext() {
		return noContext;
	}

	public EGLDisplay getNoDisplay() {
		return noDisplay;
	}

	public EGLSurface getNoSurface() {
		return noSurface;
	}


}


