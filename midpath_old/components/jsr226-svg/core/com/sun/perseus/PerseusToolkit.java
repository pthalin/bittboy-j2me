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
package com.sun.perseus;

import java.io.InputStream;

import javax.microedition.m2g.SVGAnimator;
import javax.microedition.m2g.SVGImage;
import javax.microedition.m2g.ScalableGraphics;

import com.sun.perseus.j2d.ImageLoaderUtil;
import com.sun.perseus.platform.GZIPSupport;

public abstract class PerseusToolkit {

	private static PerseusToolkit instance;
	
	static {
		String toolkit = System.getProperty("jsr226.toolkit");
		if (toolkit == null) { // Default
			toolkit = "org.thenesis.midpath.svg.midp.MIDPPerseusToolkit";
		}
		try {
			Class toolkitClass = Class.forName(toolkit);
			instance = (PerseusToolkit) toolkitClass.newInstance();
		} catch (Exception e) {
			throw new IllegalArgumentException("Error : No toolkit found for JSR226 (SVG)");
		}
		
	}

	protected PerseusToolkit() {
	}

	public static PerseusToolkit getInstance() {
		return instance;
	}

	public abstract ImageLoaderUtil createImageLoaderUtil();

	public abstract SVGAnimator createAnimator(SVGImage svgImage);

	public abstract SVGAnimator createAnimator(SVGImage svgImage, String componentBaseClass);

	public abstract GZIPSupport getGZIPSupport();

	public abstract InputStream getInitialFontResource();

	public abstract InputStream getDefaultFontResource();

	public abstract ScalableGraphics createScalableGraphics();

}
