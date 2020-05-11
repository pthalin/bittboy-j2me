/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
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
package org.thenesis.midpath.svg.awt;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.sun.pisces.GraphicsSurfaceDestination;

public final class GraphicsSurfaceDestinationImpl extends GraphicsSurfaceDestination {
    private final Graphics g;
    
    public GraphicsSurfaceDestinationImpl(Graphics g) {
        this.g = g;
    }
   
    protected void drawRGBOnGraphics(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
    	 int type = processAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
    	 BufferedImage image = new BufferedImage(width, height, type);
    	 image.setRGB(0, 0, width, height, rgbData, offset, scanlength);
    	 g.drawImage(image, x, y, null);
    	 
    	//g.drawRGB(rgbData, offset, scanlength, x, y, width, height, processAlpha);
    }
}

