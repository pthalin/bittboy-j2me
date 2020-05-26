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
package org.thenesis.midpath.svg.midp;

import java.io.InputStream;

import javax.microedition.m2g.SVGAnimator;
import javax.microedition.m2g.SVGImage;
import javax.microedition.m2g.ScalableGraphics;

import com.sun.perseus.PerseusToolkit;
import com.sun.perseus.j2d.ImageLoaderUtil;
import com.sun.perseus.platform.GZIPSupport;

public class MIDPPerseusToolkit extends PerseusToolkit {
	
	private GZIPSupportImpl gSupportImpl = new GZIPSupportImpl();
	
	public ImageLoaderUtil createImageLoaderUtil() {
		return new ImageLoaderUtilImpl();
	}
	
	public SVGAnimator createAnimator(SVGImage svgImage) {
		return SVGAnimatorImpl.createAnimator(svgImage, null);
	}
	
	public SVGAnimator createAnimator(SVGImage svgImage,
             String componentBaseClass) {
		return SVGAnimatorImpl.createAnimator(svgImage, componentBaseClass);
	}
	
	public GZIPSupport getGZIPSupport() {
		return gSupportImpl;
	}
	
	public InputStream getInitialFontResource() {
		return ResourceHandler.getInitialFontResource();
	}
	
	public InputStream getDefaultFontResource() {
		return ResourceHandler.getDefaultFontResource();
	}
	
	 public ScalableGraphics createScalableGraphics() {
		 return new ScalableGraphicsIpml();
	 }

}
