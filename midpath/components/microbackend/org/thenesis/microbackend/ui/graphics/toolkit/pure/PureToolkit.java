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
package org.thenesis.microbackend.ui.graphics.toolkit.pure;

import org.thenesis.microbackend.ui.graphics.VirtualFont;
import org.thenesis.microbackend.ui.graphics.VirtualGraphics;
import org.thenesis.microbackend.ui.graphics.VirtualImage;
import org.thenesis.microbackend.ui.graphics.VirtualToolkit;

public class PureToolkit extends VirtualToolkit {

    private PureImage rootImage;
    private PureGraphics rootGraphics;
    //static PureFont mediumFont = new Font_CodingFontTobi_regular_16(); //Font_Crisp_regular_16();
    static PureFont mediumFont = new Font_Bitmap("/fonts/", "/fonts/font.bmf");

    public PureToolkit() {
    }

    public void initializeRoot(int rootWidth, int rootHeight) {
        rootImage = new PureImage(rootWidth, rootHeight);
        rootGraphics = new PureGraphics(rootImage);
        rootGraphics.setDimensions(rootWidth, rootHeight);
        rootGraphics.reset();

        // We need VirtualToolkit.imageDecoder, which is only
        // available after this point
        Font_Bitmap f = (Font_Bitmap)mediumFont;
        f.setToolkit(this);
        try {
            f.loadFont();
        } catch (Exception e) {
            System.out.println("Font_Bitmap.loadFont(): " + e);
        }
        try {
            f.loadChars();
        } catch (Exception e) {
            System.out.println("Font_Bitmap.loadChars(): " + e);
        }
    }

    public VirtualGraphics getRootGraphics() {
        return rootGraphics;
    }

    public void setDimensions(int width, int height) {
        rootImage.resetDimensions(width, height);
        rootGraphics.setDimensions(width, height);
        rootGraphics.reset();
    }

    public VirtualFont createFont(int face, int style, int size) {
        
//        switch (size) {
//        case VirtualFont.SIZE_LARGE:
//            return smallFont;
//        case VirtualFont.SIZE_MEDIUM:
//            return smallFont;
//        case VirtualFont.SIZE_SMALL:
//            return smallFont;
//        default:
            return mediumFont;
//        }
        
    }
    
    public void flushGraphics(int x, int y, long width, long height) {
        backend.updateARGBPixels(rootImage.pixels, x, y, (int) width, (int) height);
    }

    public VirtualImage createImage(int w, int h) {
        return new PureImage(w, h);
    }

    public VirtualImage createRGBImage(int[] rgb, int width, int height, boolean processAlpha) {
        return new PureImage(rgb, width, height, processAlpha);
    }
    
    public VirtualImage createImage(VirtualImage image, int x, int y, int width, int height, int transform) {
        return new PureImage(image, x, y, width, height, transform);
    }
    
    public VirtualImage createImage(VirtualImage image) {
        return new PureImage(image);
    }

}
