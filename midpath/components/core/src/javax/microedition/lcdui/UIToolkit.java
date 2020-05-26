/*
 * MIDPath - Copyright (C) 2006 Guillaume Legris, Mathieu Legris
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
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions. 
 */
package javax.microedition.lcdui;

import java.io.IOException;
import java.io.InputStream;

import org.thenesis.midpath.ui.toolkit.sdl.SDLToolkit;
import org.thenesis.midpath.ui.toolkit.virtual.VirtualToolkit;

import com.sun.midp.events.EventMapper;
import com.sun.midp.main.BaseMIDletSuiteLauncher;
import com.sun.midp.main.Configuration;

public abstract class UIToolkit {

	//private static Toolkit toolkit = new SDLToolkit();
	//private static Toolkit toolkit = new VirtualToolkit();
	private static UIToolkit toolkit;
	
	static {
		String toolkitName = Configuration.getPropertyDefault("javax.microedition.lcdui.toolkit", "virtual");
		if (toolkitName.equalsIgnoreCase("SDL")) {
			toolkit = new SDLToolkit();
		} else {
			toolkit = new VirtualToolkit();
		}
	}
	
	public static void setToolkit(UIToolkit toolkit) {
		UIToolkit.toolkit = toolkit;
	}
	
	public static UIToolkit getToolkit() {
		return toolkit;
	}
	
	public abstract void initialize(int w, int h);
	
	public abstract void close();

	public abstract Graphics getRootGraphics();

	public abstract Graphics createGraphics(Image image);

	public abstract void refresh(int displayId, int x, int y, long widht, long heigth);

	public abstract Image createImage(int w, int h);

	public abstract Image createImage(Image source);

	public abstract Image createImage(byte[] imageData, int imageOffset, int imageLength) throws IOException;

	public abstract Image createImage(InputStream stream) throws IOException;
	
	public Image createImage(String name) throws IOException {
		
		// Load the image from the MIDlet classloader.
		// FIXME Use a better mechanism (?)
		InputStream is = BaseMIDletSuiteLauncher.getResourceAsStream(name);
		//System.out.println("[DEBUG] UIToolkit.createImage(String name): " + is.read());
		return createImage(is);
	}

	public abstract Image createRGBImage(int[] rgb, int width, int height, boolean processAlpha) throws IOException;

	public abstract Image createImage(Image image, int x, int y, int width, int height, int transform) throws IOException;

	public abstract Image createImage(int[] rgb, int width, int height, boolean processAlpha);

	/**
	 * Construct a new FontPeer object
	 *
	 * @param face The face to use to construct the Font
	 * @param style The style to use to construct the Font
	 * @param size The point size to use to construct the Font
	 */
	public abstract FontPeer createFontPeer(int face, int style, int size);
	
	public abstract EventMapper getEventMapper();
	
	

	
}
