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
import org.thenesis.microbackend.ui.graphics.Rectangle;
import org.thenesis.microbackend.ui.graphics.VirtualGraphics;
import org.thenesis.microbackend.ui.graphics.VirtualImage;

import com.sun.pisces.NativeSurface;

public class PiscesImage implements VirtualImage {

    NativeSurface nativeSurface;
    private boolean isMutable = false;
    private int imgWidth;
    private int imgHeight;

    //protected Image img;

    public PiscesImage(int w, int h) {
        nativeSurface = new NativeSurface(w, h);
        imgWidth = w;
        imgHeight = h;
        isMutable = true;
    }

    private void setDimensions(int w, int h) {
        this.imgWidth = w;
        this.imgHeight = h;
    }

    public PiscesImage(int[] argb, int width, int height, boolean processAlpha) { //throws IOException {
        nativeSurface = new NativeSurface(width, height);
        nativeSurface.setRGB(argb, 0, width, 0, 0, width, height);
        setDimensions(width, height);
        isMutable = false;
    }

    public PiscesImage(VirtualImage srcImage) {
        int srcWidth = srcImage.getWidth();
        int srcHeight =  srcImage.getHeight();
        nativeSurface = new NativeSurface(srcWidth, srcHeight);
        int[] srcData = new int[srcWidth * srcHeight];
        srcImage.getRGB(srcData, 0, srcWidth, 0, 0, srcWidth, srcHeight);
        nativeSurface.setRGB(srcData, 0, srcWidth, 0, 0, srcWidth, srcHeight);
        setDimensions(srcWidth, srcHeight);
        isMutable = false;
    }
    
    PiscesImage(VirtualImage srcImage, int x, int y, int width, int height, int transform) {
        VirtualImage dstImage = transform((PiscesImage)srcImage, x, y, width, height, transform);
        nativeSurface = ((PiscesImage)dstImage).nativeSurface;
        setDimensions(dstImage.getWidth(), dstImage.getHeight());
        isMutable = false;
    }

    /**
     * Create a VirtualImage from a pre-existing surface (doesn't copy it)
     * 
     * @param pixels
     */
    PiscesImage(PiscesImage image) {
        this.nativeSurface = image.nativeSurface;
        setDimensions(image.getWidth(), image.getHeight());
        isMutable = false;
    }


    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualImage#getRGB(int[], int, int, int, int, int, int)
     */
    public void getRGB(int[] rgbData, int offset, int scanLength, int x, int y, int width, int height) {

        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG] VirtualImage.getRGB(): rgbData[" + rgbData.length + "] offset=" + offset + " scanlength="
                    + scanLength + " x=" + x + " y=" + y + " width=" + width + " height=" + height);

        if ((x < 0) || (y < 0) || ((x + width) > imgWidth) || ((y + height) > imgHeight)) {
            throw new IllegalArgumentException();
        }

        if ((width <= 0) || (height <= 0)) {
            return;
        }

        nativeSurface.getRGB(rgbData, offset, scanLength, x, y, width, height);

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
            y -= getHeight();
        } else if ((anchor & VirtualGraphics.VCENTER) == VirtualGraphics.VCENTER) {
            y -= getHeight() / 2;
        }

        if ((anchor & VirtualGraphics.RIGHT) == VirtualGraphics.RIGHT) {
            x -= getWidth();
        } else if ((anchor & VirtualGraphics.HCENTER) == VirtualGraphics.HCENTER) {
            x -= getWidth() / 2;
        }

        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG]VirtualImage.render2(): x=" + x + " y=" + y + " width=" + getWidth() + " height="
                    + getHeight());

        NativeSurface srcNativeSurface = nativeSurface;
        int[] srcData = new int[width * height]; 
        srcNativeSurface.getRGB(srcData, 0, width, x_src, y_src, width, height);
        
        PiscesGraphics vg = (PiscesGraphics) g;
        PiscesImage destSurface = (PiscesImage) vg.getImage();
        NativeSurface dstNativeSurface = destSurface.nativeSurface;
        dstNativeSurface.setRGB(srcData, 0, width, x, y, width, height);

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

        x_src += g.getTranslateX();
        y_src += g.getTranslateY();

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

    static void copy(PiscesImage srcSurface, int x_src, int y_src, int width, int height, PiscesImage destSurface, int x_dest,
            int y_dest) {
        int[] srcData = new int[width * height]; 
        srcSurface.nativeSurface.getRGB(srcData, 0, width, x_src, y_src, width, height);
        destSurface.nativeSurface.setRGB(srcData, 0, width, x_dest, y_dest, width, height);
        
//        int[] srcSurfaceData = srcSurface.pixels;
//        int[] dstSurfaceData = destSurface.pixels;
//        int srcSurfaceWidth = srcSurface.getWidth();
//        int dstSurfaceWidth = destSurface.getWidth();
//        
//        int srcOffset = y_src * srcSurfaceWidth + x_src;
//        int destOffset = y_dest * dstSurfaceWidth + x_dest;
//
//        for (int y = 0; y < height; y++) {
//            int srcPosition = srcOffset + y * srcSurfaceWidth;
//            int destPosition = destOffset + y * dstSurfaceWidth;
//            for (int x = 0; x < width; x++) {
//                dstSurfaceData[destPosition + x] = srcSurfaceData[srcPosition + x];
//            }
//        }
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
            PiscesImage destSurface = new PiscesImage(width, height);
            copy((PiscesImage) srcSurface, x_src, y_src, width, height, destSurface, 0, 0);
            mirror(destSurface, 0, 0, width, height);
            return destSurface;
        case TRANS_MIRROR_ROT90:
        case TRANS_MIRROR_ROT180:
        case TRANS_MIRROR_ROT270:
            destSurface = new PiscesImage(width, height);
            copy((PiscesImage) srcSurface, x_src, y_src, width, height, destSurface, 0, 0);
            mirror(destSurface, 0, 0, width, height);
            return rotate(destSurface, 0, 0, width, height, transform);
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
            PiscesImage destSurface = new PiscesImage(height, width);
            int[] srcData = new int[height * width];
            srcSurface.getRGB(srcData, 0, width, x_src, y_src, width, height);
            int[] dstData = new int[height * width];
            int srcSurfaceWidth = srcSurface.getWidth();

            int srcOffset = y_src * srcSurfaceWidth + x_src;

            for (int y = 0; y < height; y++) {
                int srcPosition = srcOffset + y * srcSurfaceWidth;
                int destPosition = height - y - 1;
                for (int x = 0; x < width; x++) {
                    dstData[destPosition + x * height] = srcData[srcPosition + x];
                }
            }
            destSurface.nativeSurface.setRGB(srcData, 0, height, x_src, y_src, height, width);
            return destSurface;
        }
        case TRANS_MIRROR_ROT180:
        case TRANS_ROT180: {
            PiscesImage destSurface = new PiscesImage(height, width);
            int[] srcData = new int[height * width];
            srcSurface.getRGB(srcData, 0, width, x_src, y_src, width, height);
            int[] dstData = new int[height * width];
            int srcSurfaceWidth = srcSurface.getWidth();

            int srcOffset = y_src * srcSurfaceWidth + x_src;
            int destOffset = width * height - 1;

            for (int y = 0; y < height; y++) {
                int srcPosition = srcOffset + y * srcSurfaceWidth;
                int destPosition = destOffset - y * width;
                for (int x = 0; x < width; x++) {
                    dstData[destPosition - x] = srcData[srcPosition + x];
                }
            }
            destSurface.nativeSurface.setRGB(srcData, 0, height, x_src, y_src, height, width);
            return destSurface;
        }
        case TRANS_MIRROR_ROT270:
        case TRANS_ROT270:
        {
            PiscesImage destSurface = new PiscesImage(height, width);
            int[] srcData = new int[height * width];
            srcSurface.getRGB(srcData, 0, width, x_src, y_src, width, height);
            int[] dstData = new int[height * width];
            int srcSurfaceWidth = srcSurface.getWidth();

            int srcOffset = y_src * srcSurfaceWidth + x_src;
            int destOffset = (width - 1) * height;

            for (int y = 0; y < height; y++) {
                int srcPosition = srcOffset + y * srcSurfaceWidth;
                int destPosition = destOffset + y;
                for (int x = 0; x < width; x++) {
                    dstData[destPosition - x * height] = srcData[srcPosition + x];
                }
            }
            destSurface.nativeSurface.setRGB(srcData, 0, height, x_src, y_src, height, width);
            return destSurface;
        }
        }
        
        return null;
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualImageInterface#mirror(org.thenesis.microbackend.ui.graphics.VirtualSurface, int, int, int, int)
     */
    public void mirror(VirtualImage srcImage, int x_src, int y_src, int width, int height) {

        int srcWidth = srcImage.getWidth();
        int srcHeight = srcImage.getHeight();
        int[] buffer = new int[srcWidth * srcHeight];
        srcImage.getRGB(buffer, 0, width, x_src, y_src, width, height);
        
        int offset = y_src * srcWidth + x_src;
        for (int y = 0; y < height; y++) {
            offset = y * srcWidth;
            for (int x = 0; x < width / 2; x++) {
                int tmp = buffer[offset + x];
                buffer[offset + x] = buffer[offset + width - 1 - x];
                buffer[offset + width - 1 - x] = tmp;
            }
        }
        
        ((PiscesImage)srcImage).nativeSurface.setRGB(buffer, 0, width, x_src, y_src, width, height);

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

            PiscesGraphics g = new PiscesGraphics(this);
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

}
