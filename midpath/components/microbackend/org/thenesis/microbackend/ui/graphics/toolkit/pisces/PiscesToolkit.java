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
package org.thenesis.microbackend.ui.graphics.toolkit.pisces;

import org.thenesis.microbackend.ui.graphics.VirtualFont;
import org.thenesis.microbackend.ui.graphics.VirtualGraphics;
import org.thenesis.microbackend.ui.graphics.VirtualImage;
import org.thenesis.microbackend.ui.graphics.VirtualToolkit;

public class PiscesToolkit extends VirtualToolkit {

    private PiscesImage rootImage;
    private int[] rootPixels;
    private PiscesGraphics rootGraphics;
    static PiscesFont mediumFont = new Font8x16();

    public PiscesToolkit() {
    }

    public void initializeRoot(int rootWidth, int rootHeight) {
        rootImage = new PiscesImage(rootWidth, rootHeight);
        rootPixels = new int[rootWidth * rootHeight];
        rootGraphics = new PiscesGraphics(rootImage);
        rootGraphics.setDimensions(rootWidth, rootHeight);
        rootGraphics.reset();
    }

    public VirtualGraphics getRootGraphics() {
        return rootGraphics;
    }

    public VirtualFont createFont(int face, int style, int size) {
        
        switch (size) {
//        case VirtualFont.SIZE_LARGE:
//            return smallFont;
//        case VirtualFont.SIZE_MEDIUM:
//            return smallFont;
//        case VirtualFont.SIZE_SMALL:
//            return smallFont;
        default:
            return mediumFont;
        }
        
    }
    
    public void flushGraphics(int x, int y, long width, long height) {
        int rootImageWidth = rootImage.getWidth();
        int rootImageHeight = rootImage.getWidth();
        rootImage.getRGB(rootPixels, 0, rootImageWidth, 0, 0, rootImageWidth, rootImageHeight);
        backend.updateARGBPixels(rootPixels, x, y, (int) width, (int) height);
    }

    public VirtualImage createImage(int w, int h) {
        return new PiscesImage(w, h);
    }

    public VirtualImage createRGBImage(int[] rgb, int width, int height, boolean processAlpha) {
        return new PiscesImage(rgb, width, height, processAlpha);
    }
    
    public VirtualImage createImage(VirtualImage image, int x, int y, int width, int height, int transform) {
        return new PiscesImage(image, x, y, width, height, transform);
    }
    
    public VirtualImage createImage(VirtualImage image) {
        return new PiscesImage(image);
    }

}
