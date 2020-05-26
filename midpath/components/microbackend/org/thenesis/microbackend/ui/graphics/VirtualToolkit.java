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
package org.thenesis.microbackend.ui.graphics;

import java.io.IOException;

import org.thenesis.microbackend.ui.BackendEventListener;
import org.thenesis.microbackend.ui.Configuration;
import org.thenesis.microbackend.ui.Logging;
import org.thenesis.microbackend.ui.UIBackend;
import org.thenesis.microbackend.ui.UIBackendFactory;

public class VirtualToolkit {

    private VirtualSurface rootSurface;
    private VirtualGraphics rootPeer;
    private UIBackend backend;
    private VirtualFont defaultFont;

    private static VirtualToolkit toolkit = new VirtualToolkit();

    private VirtualToolkit() {
    }

    public static VirtualToolkit getToolkit() {
        return toolkit;
    }

    public void initialize(Object m, Configuration backendConfig, BackendEventListener listener) {

        backend = UIBackendFactory.createBackend(m, backendConfig, listener);
        
        int w = backend.getWidth();
        int h = backend.getHeight();

        // Wrap it
        rootSurface = new VirtualSurfaceImpl(w, h);
        rootPeer = new VirtualGraphics(rootSurface);
        rootPeer.setDimensions(w, h);
        rootPeer.reset();
       
        try {
            backend.open();
        } catch (IOException e) {
            Logging.log("VirtualToolkit: Can't open '" + backend.getClass().getName() + "' backend", Logging.ERROR);
            e.printStackTrace();
        }

    }

    public VirtualGraphics getRootGraphics() {
        return rootPeer;
    }

    public void flushGraphics(int x, int y, long width, long height) {
        backend.updateARGBPixels(rootSurface.data, x, y, (int) width, (int) height);
    }

    public VirtualFont createFont(int face, int style, int size) {
        return new VirtualFontImpl(face, style, size);
    }

    public VirtualFont getDefaultFont() {
        if (defaultFont == null) {
            defaultFont = createFont(VirtualFont.FACE_MONOSPACE, VirtualFont.STYLE_PLAIN, VirtualFont.SIZE_LARGE);
        }
        return defaultFont;
    }

    public void setDefaultFont(VirtualFont f) {
        if (f != null) {
            defaultFont = f;
        }
    }

    public int getWidth() {
        return backend.getWidth();
    }

    public int getHeight() {
        return backend.getHeight();
    }

    UIBackend getBackend() {
        return backend;
    }

    VirtualSurface getRootSurface() {
        return rootSurface;
    }

    public VirtualSurface createSurface(int w, int h) {
        return new VirtualSurfaceImpl(w, h);
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

    //	public Image createImage(int w, int h) {
    //		return new VirtualImage(w, h);
    //	}
    //
    //	public Image createImage(Image source) {
    //		if (!source.isMutable()) {
    //			return source;
    //		}
    //		return new VirtualImage((VirtualImage) source);
    //	}
    //
    //	public Image createImage(byte[] imageData, int imageOffset, int imageLength) throws IOException {
    //		return new VirtualImage(imageData, imageOffset, imageLength);
    //	}
    //
    //	public Image createImage(InputStream stream) throws IOException {
    //		return new VirtualImage(stream);
    //	}
    //
    //	public Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha) throws IOException {
    //		return new VirtualImage(rgb, width, height, processAlpha);
    //	}
    //
    //	public Image createImage(Image image, int x, int y, int width, int height, int transform) throws IOException {
    //		return new VirtualImage(image, x, y, width, height, transform);
    //	}

    //	/**
    //	 * Construct a new FontPeer object
    //	 *
    //	 * @param face The face to use to construct the Font
    //	 * @param style The style to use to construct the Font
    //	 * @param size The point size to use to construct the Font
    //	 */
    //	public FontPeer createFontPeer(int face, int style, int size) {
    //
    //		if (Logging.TRACE_ENABLED)
    //			System.out.println("[DEBUG]VirtualToolkit.createFontPeer(): size=" + size);
    //
    //			return new RawFontPeer(face, style, size);
    //		
    //		
    //		//return new RawFontPeer(face, style, size);
    //		//return new BDFFontPeer(face, style, size);
    //	}
    //
    //	public Image createImage(int[] rgb, int width, int height, boolean processAlpha) {
    //		return new VirtualImage(rgb, width, height, processAlpha);
    //	}

}
