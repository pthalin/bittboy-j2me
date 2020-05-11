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

import javax.microedition.lcdui.Graphics;

import com.sun.pisces.GraphicsSurfaceDestination;

public final class GraphicsSurfaceDestinationImpl extends GraphicsSurfaceDestination {
    private final Graphics g;
    
    public GraphicsSurfaceDestinationImpl(Graphics g) {
        this.g = g;
    }
   
    protected void drawRGBOnGraphics(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
    	g.drawRGB(rgbData, offset, scanlength, x, y, width, height, processAlpha);
    }
}
