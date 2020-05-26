/*
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


package com.sun.pisces;

public abstract class GraphicsSurfaceDestination implements SurfaceDestination {
    
    public void drawSurface(Surface ps, int srcX, int srcY, 
            int dstX, int dstY, int width, int height, float opacity) {
        int srcW = ps.getWidth();
        int srcH = ps.getHeight();

        if (srcX < 0) {
            dstX -= srcX;
            width += srcX;
            srcX = 0;
        }

        if (srcY < 0) {
            dstY -= srcY;
            height += srcY;
            srcY = 0;
        }

        if ((srcX + width) > srcW) {
            width = srcW - srcX;
        }

        if ((srcY + height) > srcH) {
            height = srcH - srcY;
        }

        if ((width < 0) || (height < 0) || (opacity == 0)) {
            return;
        }
        
        if (ps instanceof NativeSurface) {
            NativeSurface ns = (NativeSurface)ps;
            drawRGBImpl(ns.getData(), srcY * srcW + srcX, srcW, dstX, dstY, 
                    width, height, opacity);
            return;
        }

        int size = width * height;
        int[] srcRGB = new int[size];

        ps.getRGB(srcRGB, 0, width, srcX, srcY, width, height);
        drawRGBImpl(srcRGB, 0, width, dstX, dstY, width, height, 
                opacity);
    }
    
    public void drawRGB(int[] argb, int offset, int scanLength, 
            int x, int y, int width, int height, float opacity) {
        drawRGBImpl(argb, offset, scanLength, x, y, width, height, opacity);
    }

    private void drawRGBImpl(int[] argb, int offset, int scanLength, int x, int y, 
            int width, int height, float opacity) {
        if (opacity < 1) {
            int[] buffer = new int[width * height];
            int op = (int)(0x100 * opacity);
            int scanRest = scanLength - width;
            int sidx = offset;
            int didx = 0;
            for (int h = height; h > 0; --h) {
                for (int sidx2 = sidx + width; sidx < sidx2; ++sidx) {
                    int pixel = argb[sidx];
                    int alpha = ((pixel >>> 24) * op + 128) >> 8;
                    buffer[didx++] = (pixel & 0xffffff) | (alpha << 24);
                }
                sidx += scanRest;
            }
            
            drawRGBOnGraphics(buffer, 0, width, x, y, width, height, true);
            return;
        }
        
        drawRGBOnGraphics(argb, offset, scanLength, x, y, width, height, true);
    }
    
    protected abstract void drawRGBOnGraphics(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha);
}
