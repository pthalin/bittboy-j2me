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
package org.thenesis.midpath.opengles.ogles;

import javax.microedition.khronos.egl.EGL;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.egl.EGLToolkit;

public class NativeToolkit extends EGLToolkit {
	
	private EGLContext noContext = NativeEGLContext.getInstance(0);
	private EGLDisplay noDisplay = NativeEGLDisplay.getInstance(0);
	private EGLSurface noSurface = NativeEGLSurface.getInstance(0);

	public EGL getEGL() {
		return NativeEGL10.getInstance();
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


