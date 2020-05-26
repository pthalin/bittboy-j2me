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

import org.thenesis.microbackend.ui.Logging;
import org.thenesis.microbackend.ui.graphics.BaseGraphics;
import org.thenesis.microbackend.ui.graphics.Rectangle;
import org.thenesis.microbackend.ui.graphics.VirtualGraphics;
import org.thenesis.microbackend.ui.graphics.VirtualImage;

import com.sun.pisces.PathSink;
import com.sun.pisces.PiscesRenderer;
import com.sun.pisces.Renderer;
import com.sun.pisces.RendererBase;

public class PiscesGraphics extends BaseGraphics {

    private final byte color[] = new byte[4];
    private PiscesImage piscesSurface;
    private PiscesRenderer renderer;

    PiscesGraphics(VirtualImage image) {
        super(image);
        piscesSurface = (PiscesImage)image;
        
        spanCapacity = image.getHeight();
        spanX = new int[spanCapacity];
        spanY = new int[spanCapacity];
        spanLength = new int[spanCapacity];
        spanDataCapacity = image.getWidth();
        spanDataBuffer = new byte[spanDataCapacity * 4];
        for (int i = 0; i < 4; i++) {
            mask[i] = true;
            color[i] = (byte)0xFF;
        }
        
        int imgWidth = image.getWidth();
        int imgHeight = image.getHeight();
        renderer = new PiscesRenderer(piscesSurface.nativeSurface, imgWidth, imgHeight, 0, imgWidth, 1, RendererBase.TYPE_INT_ARGB);

    }
    
    public void setColor(int red, int green, int blue) {
        super.setColor(red, green, blue);
        renderer.setColor(red, green, blue);
    }

    public void setColor(int RGB) {
        super.setColor(RGB);
        int red = (RGB >> 16) & 0xff;
        int green = (RGB >> 8) & 0xff;
        int blue = (RGB) & 0xff;
        renderer.setColor(red, green, blue);
    }

    public void setGrayScale(int value) {
        super.setGrayScale(value);
        setColor(rgbColor);
    }
    
    public void setClip(int x, int y, int width, int height) {
        super.setClip(x, y, width, height);
        renderer.setClip(x, y, width, height);
    }
    
    public void clipRect(int x, int y, int width, int height) {
        super.clipRect(x, y, width, height);
        renderer.setClip(clipX1, clipY1, clipX2, clipY2);
    }

    Rectangle getClipRectangle() {
        return clipRectangle;
    }

    public void drawString(String str, int x, int y, int anchor) {

        x += transX;
        y += transY;

        if ((anchor & VirtualGraphics.BOTTOM) == VirtualGraphics.BOTTOM) {
            y -= currentFont.getHeight() - 1;
        }

        if ((anchor & VirtualGraphics.RIGHT) == VirtualGraphics.RIGHT) {
            x -= currentFont.stringWidth(str) - 1;
        } else if ((anchor & VirtualGraphics.HCENTER) == VirtualGraphics.HCENTER) {
            x -= currentFont.stringWidth(str) / 2 - 1;
        }

        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG] RawFontPeer.render(): " + str + " x=" + x + " y=" + y + " color="
                    + Integer.toHexString(getColor()));

        int n = str.length();
        beginSpans(false);
        for (int i = 0; i < n; i++) {
            x += drawCharacter(str.charAt(i), x, y);
        }
        endSpans();

    }

    private int spanCapacity, spanCount;
    private int spanX[], spanY[], spanLength[];
    private boolean spanDataConstantColorFlag;
    private int spanDataCapacity, spanDataLength, spanDataStride;
    private byte spanDataBuffer[], spanData[];
    private final boolean mask[] = new boolean[4];

    private int drawCharacter(int character, int x, int y) {

        PiscesFont font = (PiscesFont) currentFont;

        int ci = font.findChar(character);
        if (ci < 0)
            ci = 0;

        // Clip.
        Rectangle r = clipRectangle;
        x += font.charX[ci];
        y += font.charY[ci] + font.ascent;
        
        int w = font.charWidth[ci], h = font.charHeight[ci];
        int dxmin = x, dymin = y, dxmax = x + w, dymax = y + h;
        if (dxmin < r.xmin)
            dxmin = r.xmin;
        if (dymin < r.ymin)
            dymin = r.ymin;
        if (dxmax > r.xmax)
            dxmax = r.xmax;
        if (dymax > r.ymax)
            dymax = r.ymax;

        int cxmin = dxmin - x, cymin = dymin - y, cw = dxmax - dxmin, ch = dymax - dymin;
        if (cw > 0 && ch > 0) {
            byte src[] = font.pixels, dst[] = spanDataBuffer;
            int srcIStride, srcI = font.charOffset[ci];
            switch (font.type) {
            case PiscesFont.TYPE_BW:
                srcIStride = w;
                srcI += cymin * srcIStride;
                for (int cy = 0; cy < ch; cy++, srcI += srcIStride) {
                    int dstI = addSpan(dxmin, dymin + cy, cw) << 2;
                    for (int cx = 0; cx < cw; cx++, dstI += 4) {
                        int srcPixelIndex = srcI + cxmin + cx, srcPixelByte = srcPixelIndex >> 3, srcPixelBit = 7 - (srcPixelIndex & 0x7);
                        int srcPixel = (src[srcPixelByte] >> srcPixelBit) & 0x01;
                        if (srcPixel != 0) {
                            for (int c = 0; c < 4; c++)
                                dst[dstI + c] = color[c];
                        } else {
                            for (int c = 0; c < 4; c++)
                                dst[dstI + c] = 0;
                        }
                    }
                }
                break;
            case PiscesFont.TYPE_A:
                srcIStride = w;
                srcI += cymin * srcIStride;
                for (int cy = 0; cy < ch; cy++, srcI += srcIStride) {
                    int dstI = addSpan(dxmin, dymin + cy, cw) << 2;
                    for (int cx = 0; cx < cw; cx++, dstI += 4) {
                        for (int c = 0; c < 3; c++)
                            dst[dstI + c] = color[c];
                        dst[dstI + 3] = (byte) (src[srcI + cxmin + cx]); //(src[srcI+cxmin+cx]*color[3]/255)
                    }
                }
                break;
            case PiscesFont.TYPE_LA:
                srcIStride = w << 1;
                srcI += cymin * srcIStride;
                for (int cy = 0; cy < ch; cy++, srcI += srcIStride) {
                    int dstI = addSpan(dxmin, dymin + cy, cw) << 2;
                    for (int cx = 0; cx < cw; cx++, dstI += 4) {
                        int srcI2 = srcI + ((cxmin + cx) << 1);
                        int alpha = src[srcI2 + 0];
                        for (int c = 0; c < 3; c++)
                            dst[dstI + c] = (byte) ((alpha * color[c]) / 255);
                        dst[dstI + 3] = src[srcI2 + 1];
                    }
                }
                break;
            case PiscesFont.TYPE_RGBA:
                srcIStride = w << 2;
                srcI += cymin * srcIStride;
                for (int cy = 0; cy < ch; cy++, srcI += srcIStride) {
                    int dstI = addSpan(dxmin, dymin + cy, cw) << 2;
                    for (int cx = 0; cx < cw; cx++, dstI += 4) {
                        int srcI2 = srcI + ((cxmin + cx) << 2);
                        for (int c = 0; c < 4; c++)
                            dst[dstI + c] = src[srcI2 + c];
                    }
                }
                break;
            }
        }

        return font.charAdvance[ci];
    }

    private void beginSpans(boolean flag) {
        if (spanCount != 0)
            flushSpans();
        spanDataConstantColorFlag = flag;
        spanDataLength = 0;
        if (flag) {
            spanDataStride = 0;
            spanData = color;
        } else {
            spanDataStride = 4;
            spanData = spanDataBuffer;
        }
    }

    private void endSpans() {
        if (spanCount != 0)
            flushSpans();
    }

    private int addSpan(int x, int y, int l) {
        
        return spanDataLength;
    }

    private void flushSpans() {
        draw_S();
        spanCount = 0;
        spanDataLength = 0;
    }
    
    public void draw_S() {
        int srcI = 0;
        renderer.setStroke();
        renderer.beginRendering(Renderer.WIND_NON_ZERO);
        for (int i = 0; i < spanCount; i++) {
            //int dstI = (spanY[i] * width + spanX[i]);
            int l = spanLength[i];
            for (int x = 0; x < l; x++, srcI += spanDataStride) {
                if (spanData[srcI] != 0) {
                    renderer.moveTo(spanX[i], spanY[i]);  
                    renderer.lineTo(spanX[i], spanY[i]);
                } 
            }
        }
        renderer.close();
        renderer.end();
        renderer.endRendering();
    }

   

    public void resetFont() {
        currentFont = PiscesToolkit.mediumFont;
    }
    
    
//    public void drawSpan(int x, int y, int w) {
//        beginSpans(true);
//        addSpan(x, y, w);
//        endSpans();
//    }

    //--------------------------------------------------------------------------
    // ------
    // Span.
    //--------------------------------------------------------------------------
    // ------
    public void drawSpan(int x, int y, int w) {
        renderer.drawLine(x, y, x + w, y);
    }
    
    public void drawLine(int x0, int y0, int x1, int y1) {
        renderer.drawLine(x0, y0, x1, y1);
    }
    
    public void drawRect(int x, int y, int width, int height) {
        renderer.drawRect(x, y, width, height);
    }

    public void fillRect(int x, int y, int width, int height){
        renderer.fillRect(x, y, width, height);
    }
    
    public void drawArc(int x, int y, int rx, int ry, int startAngle, int arcAngle){
        renderer.drawArc(x, y, rx, ry, startAngle, arcAngle, PiscesRenderer.ARC_PIE);
    }
    
    public void fillArc(int x, int y, int rx, int ry, int startAngle, int arcAngle){
        renderer.fillArc(x, y, rx, ry, startAngle, arcAngle, PiscesRenderer.ARC_PIE);
    }
    
    public void drawRoundRect(int x, int y, int w, int h, int arcWidth, int arcHeight) {
        renderer.fillRoundRect(x, y, w, h, arcWidth, arcHeight);
    }

    public void fillRoundRect(int x, int y, int w, int h, int arcWidth, int arcHeight) {
        renderer.fillRoundRect(x, y, w, h, arcWidth, arcHeight);
    }
    
    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        
        PathSink filler = renderer.getFiller();
        renderer.beginRendering(Renderer.WIND_NON_ZERO);
        filler.moveTo(x1, y1);
        filler.lineTo(x2, y2);
        filler.lineTo(x3, y3);
        filler.close();
        filler.end();
        renderer.endRendering();
        
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#drawRGB(int[], int, int, int, int, int, int, boolean)
     */
    public void drawRGB(int[] rgbData, int offset, int scanLength, int x, int y, int width, int height, boolean processAlpha) {

        if (Logging.TRACE_ENABLED)
            System.out.println("VirtualGraphics.drawRGB()");

        x += transX;
        y += transY;

        piscesSurface.nativeSurface.setRGB(rgbData, offset, scanLength, x, y, width, height);

    }

    public void copyArea(int x_src, int y_src, int width, int height, int x_dest, int y_dest, int anchor) {

        if (Logging.TRACE_ENABLED)
            System.out.println("VirtualGraphics.doCopyArea()");

        PiscesImage dstImage = new PiscesImage(width, height);
        PiscesImage.copy(piscesSurface, x_src, y_src, width, height, dstImage, 0, 0);
        drawImage(dstImage, x_dest, y_dest, anchor);

    }

}
