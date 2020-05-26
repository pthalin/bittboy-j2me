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
package org.thenesis.midpath.ui.toolkit.sdl;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.FontPeer;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.UIToolkit;

import sdljava.SDLException;
import sdljava.SDLMain;
import sdljava.video.SDLPixelFormat;
import sdljava.video.SDLSurface;
import sdljava.video.SDLVideo;

import com.sun.midp.events.EventMapper;
import com.sun.midp.log.Logging;
import com.sun.midp.main.Configuration;

public class SDLToolkit extends UIToolkit {
	
	private SDLSurface screenSurface;
	private SDLSurface rootARGBSurface;
	private SDLGraphics rootPeer;
	private SDLEventPump eventPump;
	private EventMapper eventMapper = new SDLEventMapper();

	public SDLToolkit() {
	}

	public void initialize(int w, int h) {
		
		int bitsPerPixel = Configuration.getPositiveIntProperty("org.thenesis.microbackend.ui.sdl.bitsPerPixel", 32);
		String videoMode = Configuration.getPropertyDefault("org.thenesis.microbackend.ui.sdl.videoMode", "SW");
		long flags = videoMode.equalsIgnoreCase("HW") ? SDLVideo.SDL_HWSURFACE : SDLVideo.SDL_SWSURFACE;

		try {
			SDLMain.init(SDLMain.SDL_INIT_VIDEO);
			screenSurface = SDLVideo.setVideoMode(w, h, bitsPerPixel, flags);
			rootARGBSurface = SDLVideo.createRGBSurface(SDLVideo.SDL_SWSURFACE, w, h, 32, 0x00ff0000L,
					0x0000ff00L, 0x000000ffL, 0xff000000L);
			if (Logging.TRACE_ENABLED)
				System.out.println("[DEBUG] Toolkit.initialize(): VideoSurface: " + rootARGBSurface);
			rootPeer = new SDLGraphics(rootARGBSurface);
			eventPump = new SDLEventPump();
			eventPump.start();
			
		} catch (SDLException e) {
			e.printStackTrace();
		}
	}

	public Graphics getRootGraphics() {
		return rootPeer;
	}

//	public SDLGraphics createGraphics(int w, int h) {
//		try {
//			return new SDLGraphics(createSDLSurface(w, h));
//		} catch (SDLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return null;
//		}
//	}
	
	public Graphics createGraphics(Image image) {
		if (image instanceof SDLImage) {
			return new SDLGraphics(((SDLImage)image).sdlSurface);
		} else {
			// FIXME ??
			return null;
		}
	}

	public void refresh(int displayId, int x, int y, long widht, long heigth) {

		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] Toolkit.refresh(): x=" + x + " y=" + y + " widht=" + widht + " heigth=" + heigth);

		try {
			
			rootARGBSurface.blitSurface(screenSurface);
			screenSurface.updateRect(x, y, widht, heigth);
		} catch (SDLException e) {
			e.printStackTrace();
		}
	}

	SDLSurface createSDLSurface(int w, int h) throws SDLException {

		// Create surface compatible with display surface
		long flags = rootARGBSurface.getFlags();
		SDLPixelFormat format = rootARGBSurface.getFormat();
		int pitch = format.getBitsPerPixel();
		long rMask = format.getRMask();
		long gMask = format.getGMask();
		long bMask = format.getBMask();
		long aMask = format.getAMask();

		SDLSurface surface = SDLVideo.createRGBSurface(flags, w, h, pitch, rMask, gMask, bMask, aMask);
		/*SDLSurface surface = SDLVideo.createRGBSurface(SDLVideo.SDL_SWSURFACE, w, h, 32, 0x00000000ff000000L,
		 0x00ff0000L, 0x0000ff00L, 0x000000ffL);*/
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] SDLToolkit.createSDLSurface(): " + surface);
		//surface = surface.displayFormat();
		//System.out.println("createSDLSurface: display format"+ surface);
		//System.out.println(screenSurface);
		return surface;
	}

	public Image createImage(int w, int h) {
		return new SDLImage(w, h);
	}

	public Image createImage(Image source) {
		return new SDLImage((SDLImage)source);
	}

	public Image createImage(byte[] imageData, int imageOffset, int imageLength) throws IOException {
			return new SDLImage(imageData, imageOffset, imageLength);
	}

	public Image createImage(InputStream stream) throws IOException {
			return new SDLImage(stream);
	}

	public Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha) throws IOException {
			return new SDLImage(rgb, width, height, processAlpha);
	}
	
	public Image createImage(Image image, int x, int y, int width, int height, int transform) throws IOException {
		return new SDLImage(image, x, y, width, height, transform);
}
	
	public EventMapper getEventMapper() {
		return eventMapper;
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
			System.out.println("[DEBUG]SDLToolkit.createFontPeer(): size=" + size);
		
		//return new SDLTtfFontPeer(face, style, size);
		return new SDLGfxFontPeer(face, style, size);
	}

	public Image createImage(int[] rgb, int width, int height, boolean processAlpha) {
		 return new SDLImage(rgb, width, height, processAlpha);
	}

	public void close() {
		eventPump.stop();
		SDLMain.quitSubSystem(SDLMain.SDL_INIT_VIDEO);
	}
	
	// Note : Events processing : DisplayEventListener class

}
