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

import org.thenesis.microbackend.ui.Logging;
import org.thenesis.microbackend.ui.graphics.BaseGraphics;
import org.thenesis.microbackend.ui.graphics.Rectangle;
import org.thenesis.microbackend.ui.graphics.VirtualGraphics;
import org.thenesis.microbackend.ui.graphics.VirtualImage;

public class PureGraphics extends BaseGraphics {

    private final byte color[] = new byte[4];

    PureGraphics(VirtualImage image) {
        super(image);
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
        //beginSpans(false);
        for (int i = 0; i < n; i++) {
        //    x += drawCharacter(str.charAt(i), x, y);
            x += ((Font_Bitmap)currentFont).drawChar(this, str.charAt(i), x, y);
        }
        //endSpans();

    }

    private int spanCapacity, spanCount;
    private int spanX[], spanY[], spanLength[];
    private boolean spanDataConstantColorFlag;
    private int spanDataCapacity, spanDataLength, spanDataStride;
    private byte spanDataBuffer[], spanData[];
    private final boolean mask[] = new boolean[4];

    private int drawCharacter(int character, int x, int y) {

        PureFont font = (PureFont) currentFont;

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
            case PureFont.TYPE_BW:
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
            case PureFont.TYPE_A:
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
            case PureFont.TYPE_LA:
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
            case PureFont.TYPE_RGBA:
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
        if (spanCount >= spanCapacity || l > (spanDataCapacity - spanDataLength))
            flushSpans();
        spanX[spanCount] = x;
        spanY[spanCount] = y;
        spanLength[spanCount] = l;
        spanCount++;
        int offset = spanDataLength;
        if (!spanDataConstantColorFlag)
            spanDataLength += l;
        return offset;
    }

    private void flushSpans() {
        draw_S();
        spanCount = 0;
        spanDataLength = 0;
    }

    private void draw_S() {
        int dst[] = ((PureImage) surfaceImage).pixels;
        int width = surfaceImage.getWidth();
        int srcI = 0;
        
        for (int i = 0; i < spanCount; i++) {
            int dstI = (spanY[i] * width + spanX[i]);
            int l = spanLength[i];
            for (int x = 0; x < l; x++, dstI++, srcI += spanDataStride) {
                if (spanData[srcI] != 0) {
                    dst[dstI] = internalColor;
                } 
            }

        }

    }

    public void resetFont() {
        currentFont = PureToolkit.mediumFont;
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

        // if (Logging.TRACE_ENABLED)
        // System.out.println(
        // "[DEBUG]VirtualGraphics.drawSpan : surface data size= " +
        // surface.data.length + " dstPosition=" + dstPosition + " w=" + w);

        int dstPosition = x + y * surfaceImage.getWidth();

        int[] dst = ((PureImage) surfaceImage).pixels;

        switch (blendMode) {
        case REPLACE:
            for (int i = dstPosition; i < (dstPosition + w); i++) {
                dst[i] = internalColor;
            }
            break;
        }

    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#drawRGB(int[], int, int, int, int, int, int, boolean)
     */
    public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {

        if (Logging.TRACE_ENABLED)
            System.out.println("VirtualGraphics.drawRGB()");

        x += transX;
        y += transY;

        // Clip source rectangle in source image.
        int sxmin = 0, symin = 0, sxmax = width, symax = height;

        // Clip destination rectangle in destination image.
        int dxmin = x + sxmin, dymin = y + symin, dxmax = x + sxmax, dymax = y + symax;
        if (dxmin < clipRectangle.xmin)
            dxmin = clipRectangle.xmin;
        if (dymin < clipRectangle.ymin)
            dymin = clipRectangle.ymin;
        if (dxmax > clipRectangle.xmax)
            dxmax = clipRectangle.xmax;
        if (dymax > clipRectangle.ymax)
            dymax = clipRectangle.ymax;

        // New source rectangle.
        sxmin = dxmin - x;
        symin = dymin - y;
        sxmax = dxmax - x;
        symax = dymax - y;
        int w = sxmax - sxmin, h = symax - symin;

        int[] surfaceData = ((PureImage) surfaceImage).pixels;
        boolean fullAlphaBlending = ((PureImage) surfaceImage).fullAlphaBlending;

        if (processAlpha) {
            if (fullAlphaBlending) {
                for (int ry = 0; ry < h; ry++) {
                    int srcPosition = offset + (symin + ry) * scanlength + sxmin;
                    int dstPosition = (dymin + ry) * surfaceImage.getWidth() + dxmin;
                    int length = w;
                    for (int i = 0, sp = srcPosition, dp = dstPosition; i < length; i++, sp += 1, dp += 1) {
                        // Source.
                        int srcColor = rgbData[i + srcPosition];
                        int sa = (srcColor >> 24) & 0xFF;
                        int sr = (srcColor >> 16) & 0xFF;
                        int sg = (srcColor >> 8) & 0xFF;
                        int sb = srcColor & 0xFF;

                        // Destination.
                        int dstColor = surfaceData[i + dstPosition];
                        int dr = (dstColor >> 16) & 0xFF;
                        int dg = (dstColor >> 8) & 0xFF;
                        int db = dstColor & 0xFF;

                        // Alpha blending
                        int factor = 0x00010000 / 255;
                        dr = ((sa * sr + (0xff - sa) * dr) * factor) >> 16;
                        dg = ((sa * sg + (0xff - sa) * dg) * factor) >> 16;
                        db = ((sa * sb + (0xff - sa) * db) * factor) >> 16;
                        surfaceData[i + dstPosition] = (((dr << 16) + (dg << 8) + db) | 0xFF000000);
                    }
                }
            } else {
                for (int ry = 0; ry < h; ry++) {
                    int srcPosition = offset + (symin + ry) * scanlength + sxmin;
                    int dstPosition = (dymin + ry) * surfaceImage.getWidth() + dxmin;
                    int length = w;
                    for (int i = 0, sp = srcPosition, dp = dstPosition; i < length; i++, sp += 1, dp += 1) {
                        if (((rgbData[i + srcPosition]) & 0xFF000000) == 0xFF000000)
                            surfaceData[i + dstPosition] = rgbData[i + srcPosition];
                    }
                }
            }
        } else {
            for (int ry = 0; ry < h; ry++) {
                int srcPosition = offset + (symin + ry) * scanlength + sxmin;
                int dstPosition = (dymin + ry) * surfaceImage.getWidth() + dxmin;
                int length = w;
                for (int i = 0, sp = srcPosition, dp = dstPosition; i < length; i++, sp += 1, dp += 1) {
                    surfaceData[i + dstPosition] = rgbData[i + srcPosition] | 0xFF000000;
                }
            }
        }

    }

    public void copyArea(int x_src, int y_src, int width, int height, int x_dest, int y_dest, int anchor) {

        if (Logging.TRACE_ENABLED)
            System.out.println("VirtualGraphics.doCopyArea()");

        x_src += transX;
        y_src += transY;

        PureImage dstImage = new PureImage(width, height);
        int[] srcSurfaceData = ((PureImage) surfaceImage).pixels;
        int[] destSurfaceData = dstImage.pixels;

        for (int j = 0; j < height; j++) {
            int srcPosition = (y_src + j) * surfaceImage.getWidth() + x_src;
            int dstPosition = j * width;
            for (int i = 0; i < width; i++) {
                destSurfaceData[dstPosition + i] = srcSurfaceData[srcPosition + i];
            }
        }

        drawImage(surfaceImage, x_dest, y_dest, anchor);

    }

}
