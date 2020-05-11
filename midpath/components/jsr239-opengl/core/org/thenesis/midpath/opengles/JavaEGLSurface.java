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

import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.lcdui.Graphics;

/**
 * A class encapsulating an EGL surface.
 */
public class JavaEGLSurface extends EGLSurface {

	private int width, height;
	private boolean largestPBuffer = false;
	private static int id = -1;
	private int[] buffer;

	/**
	 * An LCDUI Graphics object referencing the surface.
	 */
	private Graphics target;

	public JavaEGLSurface(int[] buf, int width, int height) {
			//this.largestPBuffer = largestPBuffer;
			this.buffer = buf;
			this.width = width;
			this.height = height;
			id++;
	}

	public String toString() {
		return "EGLSurfaceImpl[" + id + "]";
	}

	public void setTarget(Graphics target) {
		this.target = target;
	}

	public Graphics getTarget() {
		return this.target;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}
	
	boolean isLargestPBuffer() {
		return largestPBuffer;
	}

	public void dispose() {
		
	}

	public int getId() {
		return id;
	}

	/**
	 * @return the buffer
	 */
	public int[] getBuffer() {
		return buffer;
	}

	
}
