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

import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

import com.sun.jsr239.Errors;

class NativeEGL11 extends NativeEGL10 implements EGL11 {

    public synchronized boolean eglSurfaceAttrib(EGLDisplay display,
                                                 EGLSurface surface,
                                                 int attribute,
                                                 int value) {
	if (display == null) {
	    throw new IllegalArgumentException(Errors.EGL_DISPLAY_NULL);
	}
	if (surface == null) {
	    throw new IllegalArgumentException(Errors.EGL_SURFACE_NULL);
	}

	return EGL_TRUE ==
	    _eglSurfaceAttrib(((NativeEGLDisplay)display).nativeId(),
			      ((NativeEGLSurface)surface).nativeId(),
			      attribute, value);
    }
    
    public synchronized boolean eglBindTexImage(EGLDisplay display,
                                                EGLSurface surface,
                                                int buffer) {
	if (display == null) {
	    throw new IllegalArgumentException(Errors.EGL_DISPLAY_NULL);
	}
	if (surface == null) {
	    throw new IllegalArgumentException(Errors.EGL_SURFACE_NULL);
	}

	return EGL_TRUE ==
	    _eglBindTexImage(((NativeEGLDisplay)display).nativeId(),
			     ((NativeEGLSurface)surface).nativeId(),
			     buffer);
    }
    
    public synchronized boolean eglReleaseTexImage(EGLDisplay display,
                                                   EGLSurface surface,
                                                   int buffer) {
	if (display == null) {
	    throw new IllegalArgumentException(Errors.EGL_DISPLAY_NULL);
	}
	if (surface == null) {
	    throw new IllegalArgumentException(Errors.EGL_SURFACE_NULL);
	}

	return EGL_TRUE ==
	    _eglReleaseTexImage(((NativeEGLDisplay)display).nativeId(),
				((NativeEGLSurface)surface).nativeId(),
				buffer);
    }
    
    public synchronized boolean eglSwapInterval(EGLDisplay display,
                                                int interval) {
	if (display == null) {
	    throw new IllegalArgumentException(Errors.EGL_DISPLAY_NULL);
	}

	return EGL_TRUE ==
	    _eglSwapInterval(((NativeEGLDisplay)display).nativeId(),
			     interval);
    }

    public NativeEGL11() {}
}
