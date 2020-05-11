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
package org.thenesis.microbackend.ui.graphics.toolkit.gxj;

import org.thenesis.microbackend.ui.graphics.VirtualFont;
import org.thenesis.microbackend.ui.graphics.VirtualGraphics;
import org.thenesis.microbackend.ui.graphics.VirtualImage;
import org.thenesis.microbackend.ui.graphics.VirtualToolkit;

import com.sun.cldchi.jvm.JVM;

public class GXJToolkit extends VirtualToolkit {

    private GXJImage rootImage;
    private int[] rootPixels;
    private GXJGraphics rootGraphics;

    public GXJToolkit() {
    }

    public void initializeRoot(int rootWidth, int rootHeight) {
        JVM.loadLibrary("libmicrobackend-gxj.so");
        ImageData.initFieldIDs();
        GXJGraphics.initFieldIDs();
        
        rootImage = GXJImage.createImage(rootWidth, rootHeight);
        rootPixels = new int[rootWidth * rootHeight];
        rootGraphics = (GXJGraphics)rootImage.getGraphics();
        rootGraphics.setDimensions(rootWidth, rootHeight);
        rootGraphics.reset();
    }

    public VirtualGraphics getRootGraphics() {
        return rootGraphics;
    }

    public VirtualFont createFont(int face, int style, int size) { 
        return GXJFont.getFont(face, style, size);
    }
    
    public void flushGraphics(int x, int y, long width, long height) {
        int rootImageWidth = rootImage.getWidth();
        int rootImageHeight = rootImage.getHeight();
        rootImage.getRGB(rootPixels, 0, rootImageWidth, 0, 0, rootImageWidth, rootImageHeight);
        backend.updateARGBPixels(rootPixels, x, y, (int) width, (int) height);
    }

    public VirtualImage createImage(int w, int h) {
        return GXJImage.createImage(w, h);
    }

    public VirtualImage createRGBImage(int[] rgb, int width, int height, boolean processAlpha) {
        return GXJImage.createRGBImage(rgb, width, height, processAlpha);
    }
    
    public VirtualImage createImage(VirtualImage image, int x, int y, int width, int height, int transform) {
        return GXJImage.createImage((GXJImage)image, x, y, width, height, transform);
    }
    
    public VirtualImage createImage(VirtualImage image) {
        return GXJImage.createImage((GXJImage)image);
    }
    
//    // Overrides method in VirtualToolkit
//    public VirtualImage createImage(InputStream stream) throws IOException {
//        return GXJImage.createImage(stream);
//    }
//
//    // Overrides method in VirtualToolkit
//    public VirtualImage createImage(byte[] imageData, int imageOffset, int imageLength) {
//        return GXJImage.createImage(imageData, imageOffset, imageLength);
//    }

}
