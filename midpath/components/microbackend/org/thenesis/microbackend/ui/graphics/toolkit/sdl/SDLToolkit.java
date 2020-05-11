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
package org.thenesis.microbackend.ui.graphics.toolkit.sdl;

import org.thenesis.microbackend.ui.Logging;
import org.thenesis.microbackend.ui.graphics.VirtualFont;
import org.thenesis.microbackend.ui.graphics.VirtualGraphics;
import org.thenesis.microbackend.ui.graphics.VirtualImage;
import org.thenesis.microbackend.ui.graphics.VirtualToolkit;
import org.thenesis.microbackend.ui.sdl.SDLBackend;

import sdljava.SDLException;
import sdljava.video.SDLSurface;

public class SDLToolkit extends VirtualToolkit {

    private SDLBackend backend;
    private SDLImage rootImage;
    private SDLGraphics rootGraphics;

    public SDLToolkit() {
    }

    public void initializeRoot(int w, int h) {
        backend = new SDLBackend();
        backend.configure(backendConfig, w, h);
        rootImage = new SDLImage(backend.getRootARGBSurface());
        rootGraphics = new SDLGraphics(rootImage);       
    }

    public VirtualGraphics getRootGraphics() {
		return rootGraphics;
	}

    public void flushGraphics(int x, int y, long width, long height) {
        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG] SDLToolkit.flushGraphics(): x=" + x + " y=" + y + " widht=" + width + " heigth=" + height);

        SDLSurface rootARGBSurface = backend.getRootARGBSurface();
        SDLSurface screenSurface = backend.getScreenSurface();
        
        try {
            rootARGBSurface.blitSurface(screenSurface);
            screenSurface.updateRect(x, y, width, height);
        } catch (SDLException e) {
            e.printStackTrace();
        }
    }

    public VirtualImage createImage(int w, int h) {
        return new SDLImage(w, h);
    }
    
    public VirtualImage createRGBImage(int[] rgb, int width, int height, boolean processAlpha) {
        return new SDLImage(rgb, width, height, processAlpha);
    }

    public VirtualImage createImage(VirtualImage source) {
        return new SDLImage((SDLImage) source);
    }

    public VirtualImage createImage(VirtualImage image, int x, int y, int width, int height, int transform) {
        return new SDLImage(((SDLImage) image).sdlSurface, x, y, width, height, transform);
    }

    public VirtualFont createFont(int face, int style, int size) {
        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG]SDLToolkit.createFont(): size=" + size);
        //return new SDLTtfFont(face, style, size);
        return new SDLGfxFont(face, style, size);
    }

    public void close() {
        backend.close();
    }

}
