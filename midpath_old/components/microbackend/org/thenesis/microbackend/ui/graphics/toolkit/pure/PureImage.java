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
import org.thenesis.microbackend.ui.graphics.Rectangle;
import org.thenesis.microbackend.ui.graphics.VirtualGraphics;
import org.thenesis.microbackend.ui.graphics.VirtualImage;

public class PureImage implements VirtualImage {

    int[] pixels;
    private boolean isMutable = false;
    private int imgWidth;
    private int imgHeight;
    boolean fullAlphaBlending = false;

    //protected Image img;

    public PureImage(int w, int h) {
        pixels = createPixels(w, h);
        imgWidth = w;
        imgHeight = h;
        isMutable = true;
    }

    private void setDimensions(int w, int h) {
        this.imgWidth = w;
        this.imgHeight = h;
    }

    private int[] createPixels(int w, int h) {
        return new int[w * h];
    }

    public PureImage(int[] rgb, int width, int height, boolean processAlpha) { //throws IOException {

        this.imgWidth = width;
        this.imgHeight = height;

        pixels = createPixels(width, height);

        // P(a, b) = rgb[a + b * width];
        int size = width * height;
        if (processAlpha) {
            if (fullAlphaBlending) {
                for (int i = 0; i < size; i++) {
                    pixels[i] = rgb[i];
                }
            } else {
                for (int i = 0; i < size; i++) {
                    if ((rgb[i] & 0xFF000000) != 0xFF000000)
                        pixels[i] = rgb[i] & 0x00FFFFFF;
                    else {
                        pixels[i] = rgb[i];
                    }
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                pixels[i] = rgb[i] | 0xFF000000;
            }
        }

        isMutable = false;

    }

    public PureImage(VirtualImage srcImage) {

        pixels = createPixels(srcImage.getWidth(), srcImage.getHeight());

        int[] srcData = ((PureImage)srcImage).pixels;
        int[] destData = pixels;

        System.arraycopy(srcData, 0, destData, 0, srcData.length);

        setDimensions(srcImage.getWidth(), srcImage.getHeight());
        isMutable = false;

    }
    
    PureImage(VirtualImage srcImage, int x, int y, int width, int height, int transform) {
        VirtualImage dstImage = transform((PureImage)srcImage, x, y, width, height, transform);
        pixels = ((PureImage)dstImage).pixels;  
        setDimensions(dstImage.getWidth(), dstImage.getHeight());
        isMutable = false;
    }

    /**
     * Create a VirtualImage from a pre-existing surface (doesn't copy it)
     * 
     * @param pixels
     */
    PureImage(PureImage image) {
        this.pixels = image.pixels;
        setDimensions(image.getWidth(), image.getHeight());
        isMutable = false;
    }


    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualImageInterface#getRGB(int[], int, int, int, int, int, int)
     */
    public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height) {

        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG] VirtualImage.getRGB(): rgbData[" + rgbData.length + "] offset=" + offset + " scanlength="
                    + scanlength + " x=" + x + " y=" + y + " width=" + width + " height=" + height);

        if ((x < 0) || (y < 0) || ((x + width) > imgWidth) || ((y + height) > imgHeight)) {
            throw new IllegalArgumentException();
        }

        if ((width <= 0) || (height <= 0)) {
            return;
        }

        for (int b = y; b < y + height; b++) {
            for (int a = x; a < x + width; a++) {
                //System.out.println("[DEBUG]VirtualImage.getRGB(): a=" + a + "  b=" + b);
                rgbData[offset + (a - x) + (b - y) * scanlength] = pixels[a + b * imgWidth];
                //rgbData[offset + (a - x) + (b - y) * scanlength] = P(a, b);
            }
        }

    }

    /**
     * Draw the specified region of an Image to the current Image. This method
     * assumes that coordinates of the source are already translated.
     * 
     * @param g
     * @param r
     * @param x
     * @param y
     */
    private boolean render(VirtualGraphics g, int x_src, int y_src, int width, int height, int x, int y, int anchor) {

        x += g.getTranslateX();
        y += g.getTranslateY();

        if (x_src < 0 || y_src < 0 || (x_src + width) > getWidth() || (y_src + height) > getHeight())
            return false;

        if ((anchor & VirtualGraphics.BOTTOM) == VirtualGraphics.BOTTOM) {
            //y -= getHeight();
            y -= height;
        } else if ((anchor & VirtualGraphics.VCENTER) == VirtualGraphics.VCENTER) {
            //y -= getHeight() / 2;
            y -= height / 2;
        }

        if ((anchor & VirtualGraphics.RIGHT) == VirtualGraphics.RIGHT) {
            //x -= getWidth();
            x -= width;
        } else if ((anchor & VirtualGraphics.HCENTER) == VirtualGraphics.HCENTER) {
            //x -= getWidth() / 2;
            x -= width / 2;
        }

        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG]VirtualImage.render2(): x=" + x + " y=" + y + " width=" + getWidth() + " height="
                    + getHeight());

        PureGraphics vg = (PureGraphics) g;
        Rectangle clipRect = vg.getClipRectangle();
        PureImage destSurface = (PureImage) vg.getImage();
        int dstSurfaceWidth = destSurface.getWidth();
        int[] dstSurfaceData = destSurface.pixels;
        
        int srcSurfaceWidth = getWidth();
        int srcSurfaceHeight = getHeight();
        int[] srcSurfaceData = pixels;

        //		for (int iy = y_src; iy < (y_src + height); iy++, y++) {
        //			for (int ix = x_src; ix < (x_src + width); ix++, x++) {
        //			//System.out.println("[DEBUG]VirtualImage.render(): " + (i + dstPosition));
        //			destSurface.data[ y * destSurface.getWidth() + x]=  0xFF00FF00; //surface.data[i + srcPosition]; y * destSurface.getWidth() +
        //			}
        //		}

        // Clip source rectangle in source image.
        //int sxmin=r.xmin, symin=r.ymin, sxmax=r.xmax, symax=r.ymax;
        int sxmin = x_src, symin = y_src, sxmax = x_src + width - 1, symax = y_src + height - 1;
        if (sxmin < 0)
            sxmin = 0;
        if (symin < 0)
            symin = 0;
        if (sxmax > srcSurfaceWidth - 1)
            sxmax = srcSurfaceWidth - 1;
        if (symax > srcSurfaceHeight - 1)
            symax = srcSurfaceHeight - 1;

        // Clip destination rectangle in destination image.
        int dxmin = x + sxmin - x_src, dymin = y + symin - y_src, dxmax = x + sxmax - x_src, dymax = y + symax - y_src;
        if (dxmin < clipRect.xmin)
            dxmin = clipRect.xmin;
        if (dymin < clipRect.ymin)
            dymin = clipRect.ymin;
        if (dxmax > clipRect.xmax - 1)
            dxmax = clipRect.xmax - 1;
        if (dymax > clipRect.ymax - 1)
            dymax = clipRect.ymax - 1;

        // New source rectangle.
        sxmin = dxmin - x + x_src;
        symin = dymin - y + y_src;
        sxmax = dxmax - x + x_src;
        symax = dymax - y + y_src;

        int w = sxmax - sxmin + 1, h = symax - symin + 1;
        
        if (fullAlphaBlending) {
            for (int ry = 0; ry < h; ry++) {
                int srcPosition = (symin + ry) * srcSurfaceWidth + sxmin;
                int dstPosition = (dymin + ry) * dstSurfaceWidth + dxmin;
                int length = w;
                for (int i = 0, sp = srcPosition, dp = dstPosition; i < length; i++, sp += 1, dp += 1) {
                    // Source.
                    int srcColor = srcSurfaceData[i + srcPosition];
                    int sa = (srcColor >> 24) & 0xFF;
                    int sr = (srcColor >> 16) & 0xFF;
                    int sg = (srcColor >> 8) & 0xFF;
                    int sb = srcColor & 0xFF;

                    // Destination.
                    int dstColor = dstSurfaceData[i + dstPosition];
                    int dr = (dstColor >> 16) & 0xFF;
                    int dg = (dstColor >> 8) & 0xFF;
                    int db = dstColor & 0xFF;

                    // Alpha blending
                    int factor = 0x00010000 / 255;
                    dr = ((sa * sr + (0xff - sa) * dr) * factor) >> 16;
                    dg = ((sa * sg + (0xff - sa) * dg) * factor) >> 16;
                    db = ((sa * sb + (0xff - sa) * db) * factor) >> 16;
                    dstSurfaceData[i + dstPosition] = (((dr << 16) + (dg << 8) + db) | 0xFF000000);
                }
            }
        } else {
            for (int ry = 0; ry < h; ry++) {
                int srcPosition = (symin + ry) * srcSurfaceWidth + sxmin;
                int dstPosition = (dymin + ry) * dstSurfaceWidth + dxmin;
                int length = w;
                for (int i = 0, sp = srcPosition, dp = dstPosition; i < length; i++, sp += 1, dp += 1) {
                    if (((srcSurfaceData[i + srcPosition]) & 0xFF000000) == 0xFF000000) {
                        dstSurfaceData[i + dstPosition] = srcSurfaceData[i + srcPosition];
                    }
                }
            }
        }  

        return true;
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualImageInterface#render(org.thenesis.microbackend.ui.graphics.VirtualGraphics, int, int, int)
     */
    public boolean render(VirtualGraphics g, int x, int y, int anchor) {

        //		if (Logging.TRACE_ENABLED)
        //			System.out.println("[DEBUG]VirtualImage.render(): x=" + x + " y=" + y + " width=" + surface.getWidth()
        //					+ " height=" + surface.getHeight());

        return render(g, 0, 0, imgWidth, imgHeight, x, y, anchor);

    }

    public boolean renderRegion(VirtualGraphics g, int x_src, int y_src, int width, int height, int transform, int x_dest, int y_dest,
            int anchor) {

        // why translate source co-ordinates?
        //x_src += g.getTranslateX();
        //y_src += g.getTranslateY();

        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG]VirtualImage.renderRegion(): x_src=" + x_src + " y_src=" + y_src + " width=" + width + " height= "
                    + height);

        if (transform == TRANS_NONE) {
            render(g, x_src, y_src, width, height, x_dest, y_dest, anchor);
        } else {
            VirtualImage transformedImage = transform(this, x_src, y_src, width, height, transform);
            g.drawImage(transformedImage, x_dest, y_dest, anchor);
        }

        // FIXME Returns false if something goes wrong
        return true;

    }

    private void copy(PureImage srcSurface, int x_src, int y_src, int width, int height, PureImage destSurface, int x_dest,
            int y_dest) {
        
        int[] srcSurfaceData = srcSurface.pixels;
        int[] dstSurfaceData = destSurface.pixels;
        int srcSurfaceWidth = srcSurface.getWidth();
        int dstSurfaceWidth = destSurface.getWidth();
        
        int srcOffset = y_src * srcSurfaceWidth + x_src;
        int destOffset = y_dest * dstSurfaceWidth + x_dest;

        for (int y = 0; y < height; y++) {
            int srcPosition = srcOffset + y * srcSurfaceWidth;
            int destPosition = destOffset + y * dstSurfaceWidth;
            for (int x = 0; x < width; x++) {
                dstSurfaceData[destPosition + x] = srcSurfaceData[srcPosition + x];
            }
        }
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualImageInterface#transform(org.thenesis.microbackend.ui.graphics.VirtualSurface, int, int, int, int, int)
     */
    public VirtualImage transform(VirtualImage srcSurface, int x_src, int y_src, int width, int height, int transform) {
        
        switch (transform) {

        case TRANS_ROT90:
        case TRANS_ROT180:
        case TRANS_ROT270:
            return rotate(srcSurface, x_src, y_src, width, height, transform);
        case TRANS_MIRROR:
            PureImage destSurface = new PureImage(width, height);
            copy((PureImage) srcSurface, x_src, y_src, width, height, destSurface, 0, 0);
            mirror(destSurface, 0, 0, width, height);
            return destSurface;
        case TRANS_MIRROR_ROT90:
        case TRANS_MIRROR_ROT180:
        case TRANS_MIRROR_ROT270:
            destSurface = new PureImage(width, height);
            copy((PureImage) srcSurface, x_src, y_src, width, height, destSurface, 0, 0);
            mirror(destSurface, 0, 0, width, height);
            return rotate(destSurface, 0, 0, width, height, transform);
        case TRANS_NONE:
            destSurface = new PureImage(width, height);
            copy((PureImage) srcSurface, x_src, y_src, width, height, destSurface, 0, 0);
            return destSurface;
        }

        return srcSurface;
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualImageInterface#rotate(org.thenesis.microbackend.ui.graphics.VirtualSurface, int, int, int, int, int)
     */
    public VirtualImage rotate(VirtualImage srcSurface, int x_src, int y_src, int width, int height, int transform) {
        switch (transform) {
        case TRANS_MIRROR_ROT90:
        case TRANS_ROT90:
        {
            PureImage destSurface = new PureImage(height, width);
            int[] srcSurfaceData = ((PureImage)srcSurface).pixels;
            int[] dstSurfaceData = destSurface.pixels;
            int srcSurfaceWidth = srcSurface.getWidth();

            int srcOffset = y_src * srcSurfaceWidth + x_src;

            for (int y = 0; y < height; y++) {
                int srcPosition = srcOffset + y * srcSurfaceWidth;
                int destPosition = height - y - 1;
                for (int x = 0; x < width; x++) {
                    dstSurfaceData[destPosition + x * height] = srcSurfaceData[srcPosition + x];
                }
            }
            
            return destSurface;
        }
        case TRANS_MIRROR_ROT180:
        case TRANS_ROT180: {
            PureImage destSurface = new PureImage(width, height);
            int[] srcSurfaceData = ((PureImage)srcSurface).pixels;
            int[] dstSurfaceData = destSurface.pixels;
            int srcSurfaceWidth = srcSurface.getWidth();

            int srcOffset = y_src * srcSurfaceWidth + x_src;
            int destOffset = width * height - 1;

            for (int y = 0; y < height; y++) {
                int srcPosition = srcOffset + y * srcSurfaceWidth;
                int destPosition = destOffset - y * width;
                for (int x = 0; x < width; x++) {
                    dstSurfaceData[destPosition - x] = srcSurfaceData[srcPosition + x];
                }
            }
            
            return destSurface;
        }
        case TRANS_MIRROR_ROT270:
        case TRANS_ROT270:
        {
            PureImage destSurface = new PureImage(height, width);
            int[] srcSurfaceData = ((PureImage)srcSurface).pixels;
            int[] dstSurfaceData = destSurface.pixels;
            int srcSurfaceWidth = srcSurface.getWidth();

            int srcOffset = y_src * srcSurfaceWidth + x_src;
            int destOffset = (width - 1) * height;

            for (int y = 0; y < height; y++) {
                int srcPosition = srcOffset + y * srcSurfaceWidth;
                int destPosition = destOffset + y;
                for (int x = 0; x < width; x++) {
                    dstSurfaceData[destPosition - x * height] = srcSurfaceData[srcPosition + x];
                }
            }

            return destSurface;
        }
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualImageInterface#mirror(org.thenesis.microbackend.ui.graphics.VirtualSurface, int, int, int, int)
     */
    public void mirror(VirtualImage srcImage, int x_src, int y_src, int width, int height) {

        int[] buffer = ((PureImage)srcImage).pixels;
        int srcSurfaceWidth = srcImage.getWidth();
        int offset = y_src * srcSurfaceWidth + x_src;

        for (int y = 0; y < height; y++) {
            //offset = y * srcSurfaceWidth;
            for (int x = 0; x < width / 2; x++) {
                int tmp = buffer[offset + x];
                buffer[offset + x] = buffer[offset + width - 1 - x];
                buffer[offset + width - 1 - x] = tmp;
            }
            offset += srcSurfaceWidth;
        }

    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualImageInterface#isMutable()
     */
    public boolean isMutable() {
        return isMutable;
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualImageInterface#getWidth()
     */
    public int getWidth() {
        return imgWidth;
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualImageInterface#getHeight()
     */
    public int getHeight() {
        return imgHeight;
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualImageInterface#getGraphics()
     */
    public VirtualGraphics getGraphics() {
        if (isMutable()) {

            if (null == this) {
                throw new NullPointerException();
            }

            PureGraphics g = new PureGraphics(this);
            //g.img = img;
            g.setDimensions(this.getWidth(), this.getHeight());
            g.reset();

            // construct and return a new ImageGraphics
            // object that uses the Image img as the 
            // destination.
            return g;
        } else {
            // SYNC NOTE: Not accessing any shared data, no locking necessary
            throw new IllegalStateException();
        }
    }

    public void resetDimensions(int w, int h) {
        pixels = createPixels(w, h);
        imgWidth = w;
        imgHeight = h;
    }

}
