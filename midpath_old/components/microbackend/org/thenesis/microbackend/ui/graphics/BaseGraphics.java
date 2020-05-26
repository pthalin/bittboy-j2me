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
package org.thenesis.microbackend.ui.graphics;

import org.thenesis.microbackend.ui.Logging;

public abstract class BaseGraphics implements VirtualGraphics {
    
 // Image blending mode. TODO: replace BLEND by ADD and SUB.
    public static final int BLEND = 1;
    // Image blending mode. TODO: replace BLEND by ADD and SUB.
    public static final int LOGIC = 2;
    // Image blending mode. TODO: replace BLEND by ADD and SUB.
    public static final int REPLACE = 0;
    public static final int MAX_CIRCLE_RADIUS = 16384;
    public static final int MAX_ELLIPSE_RADIUS = 16384;

    protected VirtualImage surfaceImage;

    protected int blendMode = REPLACE;

    /**
     * The clip values are in the translated coordinate system and maintained as
     * intersected with 0,0,maxWidth,maxHeight
     */
    protected short clipX1, clipY1, clipX2, clipY2;

    /** Translated x,y coordinates */
    protected int transX, transY;
    /**
     * System clip to hold any system clipping. This saves the clip values
     * imposed by the system (e.g. from Theme support). The clip values are in
     * the translated coordinate system.
     */
    protected short systemClipX1, systemClipY1, systemClipX2, systemClipY2;
    /** used by dsPaint for saving translated values */
    protected int ax, ay;
    /** A flag indicating the clipping state */
    protected boolean clipped = false;
    /** Pixel values (-1 when uninitialized) */
    protected int rgbColor, gray;
    /** pixel value (-1 when uninitialized) */
    protected int pixel;
    /** Line stroke style */
    protected int style;
    /** The current Font */
    protected VirtualFont currentFont;
    /** The maximum width and height */
    protected short maxWidth, maxHeight;
    /** A flag indicating MIDP runtime library clip state */
    protected boolean runtimeClipEnforce = false;

    protected Rectangle clipRectangle = new Rectangle();
    protected int internalColor;

    protected BaseGraphics(VirtualImage image) {
        this.surfaceImage = image;
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#getSurface()
     */
    public VirtualImage getImage() {
        return surfaceImage;
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#setColor(int, int, int)
     */
    public synchronized void setColor(int red, int green, int blue) {
        if ((red < 0) || (red > 255) || (green < 0) || (green > 255) || (blue < 0) || (blue > 255)) {
            throw new IllegalArgumentException("Value out of range");
        }

        rgbColor = (red << 16) | (green << 8) | blue;
        gray = grayVal(red, green, blue);
        pixel = getPixel(rgbColor, gray, false);
        setInternalColor();
    }
    
    /**
     * Get a gray value given the RGB values
     *
     * @param red The Red pixel value
     * @param green The Green pixel value
     * @param blue The Blue pixel value
     * @return int The grayscale value corresponding to the RGB color
     */
    private static int grayVal(int red, int green, int blue) {
        /* CCIR Rec 601 luma (nonlinear rgb to nonlinear "gray") */
        return (red*76 + green*150 + blue*29) >> 8;
    }


    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#setColor(int)
     */
    public synchronized void setColor(int RGB) {
        if (pixel == -1 || (RGB & 0x00ffffff) != rgbColor) {
            int red = (RGB >> 16) & 0xff;
            int green = (RGB >> 8) & 0xff;
            int blue = (RGB) & 0xff;

            rgbColor = RGB & 0x00ffffff;
            gray = grayVal(red, green, blue);
            pixel = getPixel(rgbColor, gray, false);
        }
        setInternalColor();
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#setGrayScale(int)
     */
    public synchronized void setGrayScale(int value) {
        if ((value < 0) || (value > 255)) {
            throw new IllegalArgumentException("Gray value out of range");
        }

        if (pixel == -1 || gray != value) {
            rgbColor = (value << 16) | (value << 8) | value;
            gray = value;
            pixel = getPixel(rgbColor, gray, true);
        }
        setInternalColor();
    }

    /**
     * Convert MIDP color (0x00RRGGBB) to internal format (0xFFRRGGBB).
     */
    private void setInternalColor() {
        internalColor = (rgbColor | 0xFF000000);
    }

    public int getInternalColor() {
        return internalColor;
    }

    /**
     * Get a specific pixel value
     * 
     * @param rgb
     * @param gray
     * @param isGray
     * @return int
     */
    protected synchronized int getPixel(int rgb, int gray, boolean isGray) {
        // TODO
        // if (Logging.TRACE_ENABLED)
        // System.out.println("[DEBUG]SDLGraphics.getPixel : " +
        // Integer.toHexString(rgb));
        if (isGray) {
            return (gray << 16) | (gray << 8) | gray;
        } else {
            return rgb;
        }
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#drawString(java.lang.String, int, int, int)
     */
    public abstract void drawString(String str, int x, int y, int anchor);

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#setClip(int, int, int, int)
     */
    public synchronized void setClip(int x, int y, int width, int height) {
        setClipInternal(x, y, width, height);
        clipRectangle.set(clipX1, clipY1, clipX2, clipY2);
    }

    /**
     * Sets the current clip to the rectangle specified by the given
     * coordinates. Rendering operations have no effect outside of the clipping
     * area.
     * 
     * @param x
     *            the x coordinate of the new clip rectangle
     * @param y
     *            the y coordinate of the new clip rectangle
     * @param width
     *            the width of the new clip rectangle
     * @param height
     *            the height of the new clip rectangle
     * @see #clipRect(int, int, int, int)
     */
    private void setClipInternal(int x, int y, int width, int height) {

        int translatedX1, translatedY1;
        int translatedX2, translatedY2;

        // If width or height is zero or less then zero,
        // we do not preserve the current clipping and
        // set all clipping values to zero.
        if ((width <= 0) || (height <= 0)) {
            clipX1 = clipY1 = clipX2 = clipY2 = 0;
            clipped = true;
            return;
        }

        // Translate the given coordinates
        translatedX1 = x + transX;
        translatedY1 = y + transY;

        // Detect Overflow
        if (translatedX1 < 0) {
            translatedX1 = (x < 0 || transX < 0) ? 0 : maxWidth;
        }
        if (translatedY1 < 0) {
            translatedY1 = (y < 0 || transY < 0) ? 0 : maxHeight;
        }

        clipX1 = (short) (translatedX1 & 0x7fff);
        clipY1 = (short) (translatedY1 & 0x7fff);

        if ((translatedX1 >= maxWidth) || (translatedY1 >= maxHeight)) {
            clipX1 = clipY1 = clipX2 = clipY2 = 0;
            clipped = true;
            return;
        }

        // Check against the runtime library clip values
        if (runtimeClipEnforce) {
            if (clipX1 < systemClipX1)
                clipX1 = systemClipX1;
            if (clipY1 < systemClipY1) {
                clipY1 = systemClipY1;
            }
        }

        // Translate the given width, height to abs. coordinates
        translatedX2 = x + transX + width;
        translatedY2 = y + transY + height;

        // Detect overflow
        if (translatedX2 < 0) {
            translatedX2 = (x < 0 || transX < 0) ? translatedX1 : maxWidth;
        } else {
            if (translatedX2 > maxWidth)
                translatedX2 = maxWidth;
        }
        if (translatedY2 < 0) {
            translatedY2 = (y < 0 || transY < 0) ? translatedY1 : maxHeight;
        } else {
            if (translatedY2 > maxHeight)
                translatedY2 = maxHeight;
        }

        clipX2 = (short) (translatedX2 & 0x7FFF);
        clipY2 = (short) (translatedY2 & 0x7FFF);

        // Check against the runtime library clip values
        if (runtimeClipEnforce) {
            if (clipX2 > systemClipX2)
                clipX2 = systemClipX2;
            if (clipY2 > systemClipY2)
                clipY2 = systemClipY2;
        }

        if ((clipX1 != 0) || (clipY1 != 0) || (clipX2 != maxWidth) || (clipY2 != maxHeight)) {
            clipped = true;
        }

        /**
         * sanity check if (clipX1 < 0 || clipY1 < 0 || clipX2 > maxWidth ||
         * clipY2 > maxHeight || clipX1 > clipX2 || clipY1 > clipY2)
         * System.out.println("Graphics:setClip error: clipX1 = "+clipX1+
         * " clipY1 = "+clipY1+" clipX2 = "+clipX2+" clipY2 = "+clipY2+
         * " maxWidth = "+maxWidth+" maxHeight = "+maxHeight); if
         * (runtimeClipEnforce)
         * System.out.println("Graphics:setClip runtimeClipEnforce:"+
         * " systemClipX1 = "+systemClipX1+" systemClipY1 = "+systemClipY1+
         * " systemClipX2 = "+systemClipX2+" systemClipY2 = "+systemClipY2);
         * 
         * end sanity check
         */

    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#clipRect(int, int, int, int)
     */
    public synchronized void clipRect(int x, int y, int width, int height) {
        clipRectInternal(x, y, width, height);
        clipRectangle.set(clipX1, clipY1, clipX2, clipY2);
    }
    
    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#clipRectInternal(int, int, int, int)
     */
    private synchronized void clipRectInternal(int x, int y, int width, int height) {

        int translatedX1, translatedY1;
        int translatedX2, translatedY2;

        if (width <= 0 || height <= 0) {
            clipX1 = clipY1 = clipX2 = clipY2 = 0;
            clipped = true;
            return;
        }

        // Translate the given coordinates
        translatedX1 = x + transX;
        translatedY1 = y + transY;

        // Detect overflow
        if (translatedX1 < 0) {
            translatedX1 = (x < 0 || transX < 0) ? 0 : maxWidth;
        }
        if (translatedY1 < 0) {
            translatedY1 = (y < 0 || transY < 0) ? 0 : maxHeight;
        }

        // If the passed in rect is below our current clip
        if ((clipX2 < translatedX1) || (clipY2 < translatedY1)) {
            // we have no intersection
            clipX1 = clipY1 = clipX2 = clipY2 = 0;
            clipped = true;
            return;
        }

        if (translatedX1 > clipX1) {
            clipX1 = (short) (translatedX1 & 0x7fff);
            clipped = true;
        }

        if (translatedY1 > clipY1) {
            clipY1 = (short) (translatedY1 & 0x7fff);
            clipped = true;
        }

        // Start handling bottom right area

        translatedX2 = x + transX + width;
        translatedY2 = y + transY + height;

        // Detect Overflow
        if (translatedX2 < 0) {
            translatedX2 = (x < 0 || transX < 0) ? translatedX1 : maxWidth;
        }
        if (translatedY2 < 0) {
            translatedY2 = (y < 0 || transY < 0) ? translatedY1 : maxHeight;
        }

        // If the passed in rect is above our current clip
        if (translatedX2 < clipX1 || translatedY2 < clipY1) {
            // we have no intersection
            clipX1 = clipY1 = clipX2 = clipY2 = 0;
            clipped = true;
            return;
        }

        if (translatedX2 <= clipX2) {
            clipX2 = (short) translatedX2;
            clipped = true;
        }

        if (translatedY2 <= clipY2) {
            clipY2 = (short) translatedY2;
            clipped = true;
        }

        if (clipped == true) {
            if (clipX2 < clipX1)
              clipX2 = clipX1;
            if (clipY2 < clipY1)
              clipY2 = clipY1;
        }
        /**
         *  sanity check 

         if (clipX1 < 0 || clipY1 < 0 ||
             clipX2 > maxWidth || clipY2 > maxHeight ||
             clipX1 > clipX2 || clipY1 > clipY2)
             System.out.println("Graphics:clipRect error: clipX1 = "+clipX1+
             " clipY1 = "+clipY1+" clipX2 = "+clipX2+" clipY2 = "+clipY2+
              " maxWidth = "+maxWidth+" maxHeight = "+maxHeight);
         if (runtimeClipEnforce)
             System.out.println("Graphics:clipRect runtimeClipEnforce:"+
             " systemClipX1 = "+systemClipX1+" systemClipY1 = "+systemClipY1+
             " systemClipX2 = "+systemClipX2+" systemClipY2 = "+systemClipY2);

         * end sanity check 
         */

    }
    
    /**
     * Preserve MIDP runtime GC.
     * - Our internal MIDP clip to protect 
     * it from MIDlet drawing on top of our widget.
     * - Translation
     *
     * @param systemX The system upper left x coordinate
     * @param systemY The system upper left y coordinate
     * @param systemW The system width of the rectangle
     * @param systemH The system height of the rectangle
     */
    public void preserveRuntimeGC(int systemX, int systemY, int systemW,
                               int systemH) {
        runtimeClipEnforce = true;
        clipRect(systemX, systemY, systemW, systemH);

        // this is the first time, we setup 
        // the systemClip values.
        systemClipX1 = clipX1;
        systemClipY1 = clipY1;
        
        // FIXME Temporary hack to prevent clipping bug in VirtualGraphics.
        systemClipX2 = (short)(clipX2 - 1);
        systemClipY2 = (short)(clipY2 - 1);
//        systemClipX2 = clipX2;
//        systemClipY2 = clipY2;

        // Preserve the translation system
        translate(systemX, systemY);
        ax = getTranslateX();
        ay = getTranslateY();
    }

    /**
     * Restore the runtime GC.
     * - Release the internal runtime clip values by
     * unsetting the variable.
     * - Restore the original translation
     */
    public void restoreRuntimeGC() {
        runtimeClipEnforce = false;
        translate(ax-getTranslateX(), ay-getTranslateY());
    }
    
    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#translate(int, int)
     */
    public synchronized void translate(int x, int y) {
        transX += x;
        transY += y;
    }
    
    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#getTranslateX()
     */
    public synchronized int getTranslateX() {
        return transX;
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#getTranslateY()
     */
    public synchronized int getTranslateY() {
        return transY;
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#getColor()
     */
    public synchronized int getColor() {
        return rgbColor;
    }

    
    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#getClipX()
     */
    public synchronized int getClipX() {
        return clipX1 - transX;
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#getClipY()
     */
    public synchronized int getClipY() {
        return clipY1 - transY;
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#getClipWidth()
     */
    public synchronized int getClipWidth() {
        return clipX2 - clipX1;
    }


    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#getClipHeight()
     */
    public synchronized int getClipHeight() {
        return clipY2 - clipY1;
    }

    
    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#drawChar(char, int, int, int)
     */
    public void drawChar(char character, int x, int y, int anchor) {
        drawString(String.valueOf(character), x, y, anchor);
    }

    
    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#drawRegion(org.thenesis.microbackend.ui.graphics.VirtualImage, int, int, int, int, int, int, int, int)
     */
    public void drawRegion(VirtualImage src, 
                                  int x_src, int y_src,
                                  int width, int height, 
                                  int transform,
                                  int x_dest, int y_dest, 
                                  int anchor) {
        if (!src.renderRegion(this, x_src, y_src, width, height,
                              transform, x_dest, y_dest, anchor)) {
            throw new IllegalArgumentException("");
        }
    }
    
    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#fillTriangle(int, int, int, int, int, int)
     */
    public void fillTriangle(int x1, int y1, 
                                    int x2, int y2,
                                    int x3, int y3) {
        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG] VirtualGraphics.fillTriangle(): not yet implemented");
        
    }
    
    /**
     * Sets the width and height member variables of this
     * Graphics object to reflect the correct values e.g. for
     * clipping correctly
     *
     * @param w the width of this Graphics object
     * @param h the height of this Graphics object
     */
    public void setDimensions(int w, int h) {
        maxWidth  = (short) ((w) & 0x7fff);
        maxHeight = (short) ((h) & 0x7fff);
    }
    
    public void reset(int x1, int y1, int x2, int y2) {
        resetGC();
        transX = transY = 0;
        setClip(x1, y1, x2 - x1, y2 - y1);
        fillRect(0, 0, maxWidth, maxHeight);
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#reset()
     */
    public void reset() {
        reset(0, 0, maxWidth, maxHeight);
    }
    
    public void resetGC() {
        resetFont();
        style       = SOLID;
        rgbColor    = gray = 0;
        pixel       = getPixel(rgbColor, gray, true);
        setInternalColor();
    }

    public abstract void resetFont();

    //--------------------------------------------------------------------------
    // ------
    // Span.
    //--------------------------------------------------------------------------
    // ------
    public abstract void drawSpan(int x, int y, int w);

    //--------------------------------------------------------------------------
    // ------
    // Line.
    //--------------------------------------------------------------------------
    // ------
    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#drawHLine(int, int, int)
     */
    public void drawHLine(int x0, int x1, int y) {
        // Clip.
        Rectangle r = clipRectangle;
        x0 += transX;
        x1 += transX;
        y += transY;
        if (x0 > x1) {
            int t = x0;
            x0 = x1;
            x1 = t;
        }
        if ((y < r.ymin) || (y > r.ymax) || (x1 < r.xmin) || (x0 > r.xmax))
            return;
        if (x0 < r.xmin)
            x0 = r.xmin;
        if (x1 > r.xmax - 1)
            x1 = r.xmax - 1;

        // Draw.
        drawSpan(x0, y, x1 - x0 + 1);
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#drawVLine(int, int, int)
     */
    public void drawVLine(int y0, int y1, int x) {
        // Clip.
        Rectangle r = clipRectangle;
        y0 += transY;
        y1 += transY;
        x += transX;
        if (y0 > y1) {
            int t = y0;
            y0 = y1;
            y1 = t;
        }
        if ((x < r.xmin) || (x > r.xmax) || (y1 < r.ymin) || (y0 > r.ymax))
            return;
        if (y0 < r.ymin)
            y0 = r.ymin;
        if (y1 > r.ymax - 1)
            y1 = r.ymax - 1;

        // Draw.
        int h = y1 - y0 + 1;
        for (int ry = 0; ry < h; ry++)
            drawSpan(x, (y0 + ry), 1);
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#drawLine(int, int, int, int)
     */
    public void drawLine(int x0, int y0, int x1, int y1) {
        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG] VirtualGraphics.drawLine() : x0=" + x0 + " y0=" + y0 + " x1=" + x1 + " y1= " + y1 + " color="
                    + Integer.toHexString(rgbColor));

        drawLine(x0, y0, x1, y1, true);
    }

    private void drawLine(int x0, int y0, int x1, int y1, boolean lastPixelFlag) {
        if (y0 == y1) {
            drawHLine(x0, x1, y0);
            return;
        }
        if (x0 == x1) {
            drawVLine(y0, y1, x0);
            return;
        }

        // Clip.
        Line sl = srcLine, dl = dstLine;

        // sl.x0=x0-translationX; sl.x1=x1-translationX; sl.y0=y0-translationY;
        // sl.y1=y1-translationY;
        sl.x0 = x0 + transX;
        sl.x1 = x1 + transX;
        sl.y0 = y0 + transY;
        sl.y1 = y1 + transY;

        if (clipLine(clipRectangle, sl, dl))
            return;

        // Draw.
        {
            int x = dl.x0, y = dl.y0, w = dl.x1 - dl.x0, h = dl.y1 - dl.y0;
            int ex = dl.x0 - sl.x0, ey = dl.y0 - sl.y0;
            int dx = sl.x1 - sl.x0, dy = sl.y1 - sl.y0;
            int sx = 1, sy = 1;
            if (dx < 0) {
                dx = -dx;
                sx = -sx;
                ex = -ex;
                w = -w;
            }
            if (dy < 0) {
                dy = -dy;
                sy = -sy;
                ey = -ey;
                h = -h;
            }

            if (dx >= dy) {
                int decision = (ex + ex + 2) * dy - (ey + ey + 1) * dx;
                if (sx > 0)
                    decision--; // Modify the decision variable for generating
                // the same pattern from left to right and right
                // to left.
                dx += dx;
                dy += dy;
                if (lastPixelFlag)
                    w++;
                while (w > 0) {
                    drawSpan(x, y, 1);
                    if (decision >= 0) {
                        decision -= dx;
                        y += sy;
                    }
                    decision += dy;
                    x += sx;
                    w--;
                }
            } else {
                int decision = (ey + ey + 2) * dx - (ex + ex + 1) * dy;
                if (sy > 0)
                    decision--; // Modify the decision variable for generating
                // the same pattern from bottom to top and top
                // to bottom.
                dx += dx;
                dy += dy;
                if (lastPixelFlag)
                    h++;
                while (h > 0) {
                    drawSpan(x, y, 1);
                    if (decision >= 0) {
                        decision -= dy;
                        x += sx;
                    }
                    decision += dx;
                    y += sy;
                    h--;
                }
            }
        }
    }

    private class Line {
        public int x0, y0, x1, y1;
    }

    private Line srcLine = new Line(), dstLine = new Line();

    // Edge to clip.
    private static final int CLIP_LEFT = 0x01, CLIP_RIGHT = 0x02, CLIP_BOTTOM = 0x04, CLIP_TOP = 0x08, CLIP_HORIZONTAL = CLIP_LEFT
            | CLIP_RIGHT, CLIP_VERTICAL = CLIP_BOTTOM | CLIP_TOP;

    // Clip zone.
    private static final byte CLIP_ZONE_CENTER = 0, CLIP_ZONE_BORDERS = 1, CLIP_ZONE_DIAGONALS = 2;

    // Table of zones.
    private static final byte CLIP_ZONE_FOR_CODE[] = { CLIP_ZONE_CENTER, CLIP_ZONE_BORDERS, CLIP_ZONE_BORDERS, -1, CLIP_ZONE_BORDERS,
            CLIP_ZONE_DIAGONALS, CLIP_ZONE_DIAGONALS, -1, CLIP_ZONE_BORDERS, CLIP_ZONE_DIAGONALS, CLIP_ZONE_DIAGONALS, -1, -1, -1, -1, -1 };

    // cf. Steven Eker. Faster "Pixel-Perfect" Line Clipping. Graphics Gems V:
    // 314-322.
    // - Pixels are not missed at the ends of a clipped segment.
    // - Visible pixels are the same as if there was no clipping.
    private boolean clipLine(Rectangle r, final Line sl, Line dl) {
        int code1 = 0, code2 = 0;

        dl.x0 = sl.x0;
        dl.y0 = sl.y0;
        dl.x1 = sl.x1;
        dl.y1 = sl.y1;

        if (sl.x0 < r.xmin)
            code1 |= CLIP_LEFT;
        if (sl.x0 > r.xmax)
            code1 |= CLIP_RIGHT;
        if (sl.y0 < r.ymin)
            code1 |= CLIP_BOTTOM;
        if (sl.y0 > r.ymax)
            code1 |= CLIP_TOP;

        if (sl.x1 < r.xmin)
            code2 |= CLIP_LEFT;
        if (sl.x1 > r.xmax)
            code2 |= CLIP_RIGHT;
        if (sl.y1 < r.ymin)
            code2 |= CLIP_BOTTOM;
        if (sl.y1 > r.ymax)
            code2 |= CLIP_TOP;

        if ((code1 | code2) == 0)
            return false; // Trivial accept.
        if ((code1 & code2) != 0)
            return true; // Trivial reject.

        // Clip first end point.
        if (code1 != 0)
            if (clipEndPoint(r, sl, dl, code1, false))
                return true;
        // Clip second end point.
        if (code2 != 0)
            if (clipEndPoint(r, sl, dl, code2, true))
                return true;

        return false;
    }

    private boolean clipEndPoint(Rectangle r, final Line sl, Line dl, int code, boolean swap) {
        // Rounding mode: 0=round toward 0, 1=round toward +infinity.
        int x, y, dx, dy, sx = 1, sy = 1, roundX = 0, roundY = 0;
        if (swap) {
            x = sl.x1;
            y = sl.y1;
            dx = sl.x0 - sl.x1;
            dy = sl.y0 - sl.y1;
        } else {
            x = sl.x0;
            y = sl.y0;
            dx = sl.x1 - sl.x0;
            dy = sl.y1 - sl.y0;
        }
        if (dx < 0) {
            dx = -dx;
            sx = -1;
            roundX ^= 1;
        }
        if (dy < 0) {
            dy = -dy;
            sy = -1;
            roundY ^= 1;
        }

        int dx2 = dx << 1, dy2 = dy << 1, t0 = 0, t1 = 0;
        int xc = x, yc = y;

        if ((code & CLIP_LEFT) != 0) {
            t0 = dy2 * (r.xmin - x);
            xc = r.xmin;
        }
        if ((code & CLIP_RIGHT) != 0) {
            t0 = dy2 * (x - r.xmax);
            xc = r.xmax;
        }
        if ((code & CLIP_BOTTOM) != 0) {
            t1 = dx2 * (r.ymin - y);
            yc = r.ymin;
        }
        if ((code & CLIP_TOP) != 0) {
            t1 = dx2 * (y - r.ymax);
            yc = r.ymax;
        }

        if (CLIP_ZONE_FOR_CODE[code] == CLIP_ZONE_DIAGONALS) {
            // Find which edge clips the line first and remove a clip flag.
            if (dx >= dy)
                code &= ((t0 - t1 + dx - (roundX ^ 1)) < 0) ? ~CLIP_HORIZONTAL : ~CLIP_VERTICAL;
            else
                code &= ((t1 - t0 + dy - (roundY ^ 1)) < 0) ? ~CLIP_VERTICAL : ~CLIP_HORIZONTAL;
        }

        if ((code & CLIP_HORIZONTAL) != 0) {
            // Clip to left or right edge.
            t0 = (dx >= dy) ? (t0 + dx - (roundX ^ 1)) / dx2 : (t0 - dy + dx2 - roundY) / dx2;
            yc = (sy < 0) ? (y - t0) : (y + t0);
            if ((yc < r.ymin) || (yc > r.ymax))
                return true;
        } else {
            // Clip to bottom or top edge.
            t1 = (dx >= dy) ? (t1 - dx + dy2 - roundX) / dy2 : (t1 + dy - (roundY ^ 1)) / dy2;
            xc = (sx < 0) ? (x - t1) : (x + t1);
            if ((xc < r.xmin) || (xc > r.xmax))
                return true;
        }

        if (swap) {
            dl.x1 = xc;
            dl.y1 = yc;
        } else {
            dl.x0 = xc;
            dl.y0 = yc;
        }

        return false;
    }

    //--------------------------------------------------------------------------
    // ------
    // Rectangle.
    //--------------------------------------------------------------------------
    // ------
    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#drawRect(int, int, int, int)
     */
    public void drawRect(int x0, int y0, int width, int height) {

        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG]VirtualGraphics.drawRect(): x=" + x0 + " y0=" + y0 + " width=" + width + " height= " + height
                    + " color=" + Integer.toHexString(rgbColor));

        int x1 = x0 + width;
        int y1 = y0 + height;

        if (surfaceImage == null)
            return;
        int t, w, h;
        if (x0 > x1) {
            t = x0;
            x0 = x1;
            x1 = t;
        }
        if (y0 > y1) {
            t = y0;
            y0 = y1;
            y1 = t;
        }
        w = x1 - x0 + 1;
        h = y1 - y0 + 1;
        drawHLine(x0, x1, y0);
        if (h > 1)
            drawHLine(x0, x1, y1);
        if (h > 2) {
            drawVLine(y0 + 1, y1 - 1, x0);
            if (w > 1)
                drawVLine(y0 + 1, y1 - 1, x1);
        }
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#fillRect(int, int, int, int)
     */
    public void fillRect(int x0, int y0, int width, int height) {

        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG]VirtualGraphics.fillRect(): x=" + x0 + " y0=" + y0 + " width=" + width + " height= " + height
                    + " color=" + Integer.toHexString(internalColor));

        if (surfaceImage == null)
            return;
        x0 += transX;
        y0 += transY;
        int x1 = x0 + width;
        int y1 = y0 + height;
        int t;
        if (x0 > x1) {
            t = x0;
            x0 = x1;
            x1 = t;
        }
        if (y0 > y1) {
            t = y0;
            y0 = y1;
            y1 = t;
        }
        if (x0 < clipRectangle.xmin)
            x0 = clipRectangle.xmin;
        if (x1 > clipRectangle.xmax)
            x1 = clipRectangle.xmax;
        if (y0 < clipRectangle.ymin)
            y0 = clipRectangle.ymin;
        if (y1 > clipRectangle.ymax)
            y1 = clipRectangle.ymax;
        // int w = x1 - x0 + 1, h = y1 - y0 + 1;
        int w = x1 - x0, h = y1 - y0;
        if ((w <= 0) || (h <= 0))
            return;

        for (int ry = 0; ry < h; ry++) {
            drawSpan(x0, (y0 + ry), w);
        }
    }

    //--------------------------------------------------------------------------
    // ------
    // Circle.
    //--------------------------------------------------------------------------
    // ------
    private void drawQuadrantPoints(int x, int y, int xp, int yp) {
        Rectangle r = clipRectangle;
        int x0 = x - xp, x1 = x + xp, y0 = y - yp, y1 = y + yp;
        boolean left = (x0 >= r.xmin) && (x0 <= r.xmax), right = (x1 >= r.xmin) && (x1 <= r.xmax) && (xp != 0);
        if ((y0 >= r.ymin) && (y0 <= r.ymax)) {
            if (left)
                drawSpan(x0, y0, 1);
            if (right)
                drawSpan(x1, y0, 1);
        }
        if ((y1 >= r.ymin) && (y1 <= r.ymax) && (yp != 0)) {
            if (left)
                drawSpan(x0, y1, 1);
            if (right)
                drawSpan(x1, y1, 1);
        }
    }

    private void drawQuadrantSpans(int x, int y, int xp, int yp) {
        Rectangle r = clipRectangle;
        int x0 = x - xp, x1 = x + xp, y0 = y - yp, y1 = y + yp;
        if (x0 < r.xmin)
            x0 = r.xmin;
        if (x1 > r.xmax)
            x1 = r.xmax;
        int w = x1 - x0 + 1;
        if (w > 0) {
            if ((y0 >= r.ymin) && (y0 <= r.ymax))
                drawSpan(x0, y0, w);
            if ((y1 >= r.ymin) && (y1 <= r.ymax) && (yp != 0))
                drawSpan(x0, y1, w);
        }
    }

    // Horn's algorithm.
    private void drawCircle(int x, int y, int radius) {
        if ((radius < 0) || (radius > MAX_CIRCLE_RADIUS))
            return;
        x += transX;
        y += transY;

        Rectangle r = clipRectangle;
        int xmin = x - radius, xmax = x + radius, ymin = y - radius, ymax = y + radius;
        if ((xmin > r.xmax) || (xmax < r.xmin) || (ymin > r.ymax) || (ymax < r.ymin))
            return;

        int xp = 0, yp = radius, d = 1 - radius;
        do {
            drawQuadrantPoints(x, y, xp, yp);
            if (yp > xp)
                drawQuadrantPoints(x, y, yp, xp);

            // Update.
            if (d < 0) {
                d += (xp << 1) + 3;
            } else {
                d += ((xp - yp) << 1) + 5;
                yp--;
            }
            xp++;
        } while (yp >= xp);
    }

    // Horn's algorithm.
    private void fillCircle(int x, int y, int radius) {
        if ((radius < 0) || (radius > MAX_CIRCLE_RADIUS))
            return;
        x += transX;
        y += transY;

        Rectangle r = clipRectangle;
        int xmin = x - radius, xmax = x + radius, ymin = y - radius, ymax = y + radius;
        if ((xmin > r.xmax) || (xmax < r.xmin) || (ymin > r.ymax) || (ymax < r.ymin))
            return;

        int xp = 0, yp = radius, d = 1 - radius;
        do {
            if (yp > xp)
                drawQuadrantSpans(x, y, yp, xp);

            // Update.
            if (d < 0) {
                d += (xp << 1) + 3;
            } else {
                drawQuadrantSpans(x, y, xp, yp);
                d += ((xp - yp) << 1) + 5;
                yp--;
            }
            xp++;
        } while (yp >= xp);
    }

    //--------------------------------------------------------------------------
    // ------
    // Ellipse.
    //--------------------------------------------------------------------------
    // ------
    // Cf: Graphics Programming Methods; 2.3 - A fast all-integer ellipse
    // discretization algorithm; page 121.
    private void drawEllipse(int x, int y, int rx, int ry) {
        if ((rx < 0) || (rx > MAX_ELLIPSE_RADIUS) || (ry < 0) || (ry > MAX_ELLIPSE_RADIUS))
            return;

        if (ry == 0) {
            drawHLine(x - rx, x + rx, y);
            return;
        }
        if (rx == 0) {
            drawVLine(y - ry, y + ry, x);
            return;
        }
        if (rx == ry) {
            drawCircle(x, y, rx);
            return;
        }

        x += transX;
        y += transY;

        int xp, yp;
        long rx2, ry2;
        long d, u, v, s, t, du, dv;

        rx2 = (long) rx * (long) rx;
        ry2 = (long) ry * (long) ry;

        s = 0;
        t = rx2 * ry;
        d = ry2 - t;
        du = ry2 << 1;
        dv = -rx2 << 1;
        u = du + ry2;
        v = dv * (ry - 1);
        xp = 0;
        yp = ry;

        while (s < t) {
            drawQuadrantPoints(x, y, xp, yp);
            if (d >= 0) {
                d += v;
                v -= dv;
                yp--;
                t -= rx2;
            }
            d += u;
            u += du;
            xp++;
            s += ry2;
        }

        s = 0;
        t = ry2 * rx;
        d = rx2 - t;
        du = rx2 << 1;
        dv = -ry2 << 1;
        u = du + rx2;
        v = dv * (rx - 1);
        xp = rx;
        yp = 0;

        while (s <= t) {
            drawQuadrantPoints(x, y, xp, yp);
            if (d >= 0) {
                d += v;
                v -= dv;
                xp--;
                t -= ry2;
            }
            d += u;
            u += du;
            yp++;
            s += rx2;
        }
    }

    private void fillEllipse(int x, int y, int rx, int ry) {
        if ((rx < 0) || (rx > 16384) || (ry < 0) || (ry > MAX_ELLIPSE_RADIUS))
            return;

        if (ry == 0) {
            drawHLine(x - rx, x + rx, y);
            return;
        }
        if (rx == 0) {
            drawVLine(y - ry, y + ry, x);
            return;
        }
        if (rx == ry) {
            fillCircle(x, y, rx);
            return;
        }

        x += transX;
        y += transY;

        int xp, yp;
        long rx2 = (long) rx * (long) rx, ry2 = (long) ry * (long) ry;
        long rxy2, t0, t1, d;
        if (rx > ry) {
            xp = rx;
            yp = 0;
            rxy2 = (long) rx * ry2;
        } else {
            xp = 0;
            yp = ry;
            rxy2 = (long) ry * rx2;
        }
        t0 = rxy2 << 3;
        t1 = 0;
        d = rx2 + ry2 - (rxy2 << 2);
        rx2 <<= 3;
        ry2 <<= 3;

        boolean drawFlag = rx > ry;
        for (int i = rx + ry; i >= 0; i--) {
            if (drawFlag)
                drawQuadrantSpans(x, y, xp, yp);

            // Update.
            if (rx > ry) {
                if (d < 0) {
                    yp++;
                    t1 += rx2;
                    d += t1;
                    drawFlag = true;
                } else {
                    xp--;
                    t0 -= ry2;
                    d -= t0;
                    drawFlag = false;
                }
            } else {
                if (d < 0) {
                    xp++;
                    t1 += ry2;
                    d += t1;
                    drawFlag = false;
                } else {
                    yp--;
                    t0 -= rx2;
                    d -= t0;
                    drawFlag = true;
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    // ------
    // Arc.
    //--------------------------------------------------------------------------
    // ------
    // Cf: Graphics Programming Methods; 2.3 - A fast all-integer ellipse
    // discretization algorithm; page 121.

    private void drawArcPoints(int x, int y, int xp, int yp, int xs, int ys, int xe, int ye, boolean reflexAngle) {
        Rectangle r = clipRectangle;
        int x0 = x - xp, x1 = x + xp, y0 = y - yp, y1 = y + yp;
        int xpys = xp * ys, ypxs = yp * xs, xpye = xp * ye, ypxe = yp * xe;
        boolean left = (x0 >= r.xmin) && (x0 <= r.xmax), right = (x1 >= r.xmin) && (x1 <= r.xmax) && (xp != 0), insideFlag;
        if ((y0 >= r.ymin) && (y0 <= r.ymax)) {
            if (left) {
                insideFlag = reflexAngle ? (-xpys - ypxs < 0 || -xpye - ypxe > 0) : (-xpys - ypxs < 0 && -xpye - ypxe > 0);
                if (insideFlag)
                    drawSpan(x0, y0, 1); // Top
                // left.
                // Second
                // quadrant
                // .
            }
            if (right) {
                insideFlag = reflexAngle ? (xpys - ypxs < 0 || xpye - ypxe > 0) : (xpys - ypxs < 0 && xpye - ypxe > 0);
                if (insideFlag)
                    drawSpan(x1, y0, 1); // Top
                // right
                // .
                // First
                // quadrant
                // .
            }
        }
        if ((y1 >= r.ymin) && (y1 <= r.ymax) && (yp != 0)) {
            if (left) {
                insideFlag = reflexAngle ? (-xpys + ypxs < 0 || -xpye + ypxe > 0) : (-xpys + ypxs < 0 && -xpye + ypxe > 0);
                if (insideFlag)
                    drawSpan(x0, y1, 1); // Bottom
                // left.
                // Third
                // quadrant
                // .
            }
            if (right) {
                insideFlag = reflexAngle ? (xpys + ypxs < 0 || xpye + ypxe > 0) : (xpys + ypxs < 0 && xpye + ypxe > 0);
                if (insideFlag)
                    drawSpan(x1, y1, 1); // Bottom
                // right
                // .
                // Fourth
                // quadrant
                // .
            }
        }
    }

    // Check whether an angle is comprised between a start and an end angle.
    private boolean checkAngle(int start, int end, int angle) {
        if (start < end)
            return angle > start && angle < end;
        else
            return angle > start || angle < end;
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#drawArc(int, int, int, int, int, int)
     */
    public void drawArc(int x, int y, int rx, int ry, int startAngle, int arcAngle) {

        // Change to coordinates relative to the center of the arc
        rx = rx / 2;
        ry = ry / 2;
        x = x + rx;
        y = y + ry;

        if ((rx < 0) || (rx > MAX_ELLIPSE_RADIUS) || (ry < 0) || (ry > MAX_ELLIPSE_RADIUS))
            return;
        if (arcAngle == 0)
            return;

        int endAngle;
        startAngle %= 360;
        if (startAngle < 0)
            startAngle += 360;
        endAngle = (startAngle + arcAngle) % 360;
        if (endAngle < 0)
            endAngle += 360;
        if (arcAngle < 0) {
            int swap = startAngle;
            startAngle = endAngle;
            endAngle = swap;
            arcAngle = -arcAngle;
        }
        if (arcAngle >= 360) {
            drawEllipse(x, y, rx, ry);
            return;
        }

        boolean reflexAngle = arcAngle >= 180;
        double start = startAngle / 180.0 * Math.PI, end = endAngle / 180.0 * Math.PI;
        int xs = (int) (Math.cos(start) * MAX_ELLIPSE_RADIUS), ys = (int) (Math.sin(start) * MAX_ELLIPSE_RADIUS);
        int xe = (int) (Math.cos(end) * MAX_ELLIPSE_RADIUS), ye = (int) (Math.sin(end) * MAX_ELLIPSE_RADIUS);

        if (ry == 0) {
            int rx0, rx1;
            rx0 = checkAngle(startAngle, endAngle, 180) ? -rx : 0;
            rx1 = checkAngle(startAngle, endAngle, 0) ? rx : 0;
            drawHLine(x + rx0, x + rx1, y);
            return;
        }

        if (rx == 0) {
            int ry0, ry1;
            ry0 = checkAngle(startAngle, endAngle, 90) ? -ry : 0;
            ry1 = checkAngle(startAngle, endAngle, 270) ? ry : 0;
            drawVLine(y + ry0, y + ry1, x);
            return;
        }

        x += transX;
        y += transY;

        int xp, yp;
        long rx2, ry2;
        long d, u, v, s, t, du, dv;

        rx2 = (long) rx * (long) rx;
        ry2 = (long) ry * (long) ry;

        s = 0;
        t = rx2 * ry;
        d = ry2 - t;
        du = ry2 << 1;
        dv = -rx2 << 1;
        u = du + ry2;
        v = dv * (ry - 1);
        xp = 0;
        yp = ry;

        while (s < t) {
            drawArcPoints(x, y, xp, yp, xs, ys, xe, ye, reflexAngle);
            if (d >= 0) {
                d += v;
                v -= dv;
                yp--;
                t -= rx2;
            }
            d += u;
            u += du;
            xp++;
            s += ry2;
        }

        s = 0;
        t = ry2 * rx;
        d = rx2 - t;
        du = rx2 << 1;
        dv = -ry2 << 1;
        u = du + rx2;
        v = dv * (rx - 1);
        xp = rx;
        yp = 0;

        while (s <= t) {
            drawArcPoints(x, y, xp, yp, xs, ys, xe, ye, reflexAngle);
            if (d >= 0) {
                d += v;
                v -= dv;
                xp--;
                t -= ry2;
            }
            d += u;
            u += du;
            yp++;
            s += rx2;
        }
    }

    private void fillArcSpan(int x, int y, int xp, int yp, int xs, int ys, int xe, int ye) {
        Rectangle r = clipRectangle;
        int y0 = y - yp;
        if ((y0 >= r.ymin) && (y0 <= r.ymax)) {
            int xs0, xe0, xi0, xi1, x0, x1, w;
            boolean invertFlag;

            xs0 = ys != 0 ? ((xs * yp << 1) + ys) / (ys << 1) : (xs > 0 ? MAX_ELLIPSE_RADIUS : -MAX_ELLIPSE_RADIUS);
            xe0 = ye != 0 ? ((xe * yp << 1) + ye) / (ye << 1) : (xe > 0 ? MAX_ELLIPSE_RADIUS : -MAX_ELLIPSE_RADIUS);

            if (yp >= 0) {
                if (ys >= 0) {
                    if (ye >= 0) {
                        if (xs >= xe) {
                            xi0 = xe0;
                            xi1 = xs0;
                            invertFlag = false;
                        } else {
                            xi0 = xs0;
                            xi1 = xe0;
                            invertFlag = true;
                        }
                    } else {
                        xi0 = -MAX_ELLIPSE_RADIUS;
                        xi1 = xs0;
                        invertFlag = false;
                    }
                } else {
                    if (ye >= 0) {
                        xi0 = xe0;
                        xi1 = MAX_ELLIPSE_RADIUS;
                        invertFlag = false;
                    } else {
                        if (xs >= xe) {
                            xi0 = -MAX_ELLIPSE_RADIUS;
                            xi1 = MAX_ELLIPSE_RADIUS;
                            invertFlag = false;
                        } else
                            return;
                    }
                }
            } else {
                if (ys < 0) {
                    if (ye < 0) {
                        if (xs <= xe) {
                            xi0 = xs0;
                            xi1 = xe0;
                            invertFlag = false;
                        } else {
                            xi0 = xe0;
                            xi1 = xs0;
                            invertFlag = true;
                        }
                    } else {
                        xi0 = xs0;
                        xi1 = MAX_ELLIPSE_RADIUS;
                        invertFlag = false;
                    }
                } else {
                    if (ye < 0) {
                        xi0 = -MAX_ELLIPSE_RADIUS;
                        xi1 = xe0;
                        invertFlag = false;
                    } else {
                        if (xs <= xe) {
                            xi0 = -MAX_ELLIPSE_RADIUS;
                            xi1 = MAX_ELLIPSE_RADIUS;
                            invertFlag = false;
                        } else
                            return;
                    }
                }
            }

            if (!invertFlag) {
                x0 = -xp;
                x1 = xp;
                if (x0 < xi0)
                    x0 = xi0;
                if (x1 > xi1)
                    x1 = xi1;
                x0 += x;
                x1 += x;
                if (x0 < r.xmin)
                    x0 = r.xmin;
                if (x1 > r.xmax)
                    x1 = r.xmax;
                w = x1 - x0 + 1;
                if (w > 0)
                    drawSpan(x0, y0, w);
            } else {
                x0 = -xp;
                x1 = xp;
                if (x1 > xi0)
                    x1 = xi0;
                x0 += x;
                x1 += x;
                if (x0 < r.xmin)
                    x0 = r.xmin;
                if (x1 > r.xmax)
                    x1 = r.xmax;
                w = x1 - x0 + 1;
                if (w > 0)
                    drawSpan(x0, y0, w);

                x0 = -xp;
                x1 = xp;
                if (x0 < xi1)
                    x0 = xi1;
                x0 += x;
                x1 += x;
                if (x0 < r.xmin)
                    x0 = r.xmin;
                if (x1 > r.xmax)
                    x1 = r.xmax;
                w = x1 - x0 + 1;
                if (w > 0)
                    drawSpan(x0, y0, w);
            }
        }
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#fillArc(int, int, int, int, int, int)
     */
    public void fillArc(int x, int y, int rx, int ry, int startAngle, int arcAngle) {

        // Change coordinates to be relative to the center of the arc
        rx = rx / 2;
        ry = ry / 2;
        x = x + rx;
        y = y + ry;

        if ((rx < 0) || (rx > MAX_ELLIPSE_RADIUS) || (ry < 0) || (ry > MAX_ELLIPSE_RADIUS))
            return;
        if (arcAngle == 0)
            return;

        int endAngle;
        startAngle %= 360;
        if (startAngle < 0)
            startAngle += 360;
        endAngle = (startAngle + arcAngle) % 360;
        if (endAngle < 0)
            endAngle += 360;
        if (arcAngle < 0) {
            int swap = startAngle;
            startAngle = endAngle;
            endAngle = swap;
            arcAngle = -arcAngle;
        }
        if (arcAngle >= 360) {
            fillEllipse(x, y, rx, ry);
            return;
        }

        double start = startAngle / 180.0 * Math.PI, end = endAngle / 180.0 * Math.PI;
        int xs = (int) (Math.cos(start) * MAX_ELLIPSE_RADIUS), ys = (int) (Math.sin(start) * MAX_ELLIPSE_RADIUS);
        int xe = (int) (Math.cos(end) * MAX_ELLIPSE_RADIUS), ye = (int) (Math.sin(end) * MAX_ELLIPSE_RADIUS);

        x += transX;
        y += transY;

        int xp, yp;
        long rx2 = (long) rx * (long) rx, ry2 = (long) ry * (long) ry;
        long rxy2, t0, t1, d;
        if (rx > ry) {
            xp = rx;
            yp = 0;
            rxy2 = (long) rx * ry2;
        } else {
            xp = 0;
            yp = ry;
            rxy2 = (long) ry * rx2;
        }
        t0 = rxy2 << 3;
        t1 = 0;
        d = rx2 + ry2 - (rxy2 << 2);
        rx2 <<= 3;
        ry2 <<= 3;

        boolean drawFlag = rx > ry;
        for (int i = rx + ry; i >= 0; i--) {
            if (drawFlag) {
                fillArcSpan(x, y, xp, yp, xs, ys, xe, ye);
                if (yp != 0)
                    fillArcSpan(x, y, xp, -yp, xs, ys, xe, ye);
            }

            // Update.
            if (rx > ry) {
                if (d < 0) {
                    yp++;
                    t1 += rx2;
                    d += t1;
                    drawFlag = true;
                } else {
                    xp--;
                    t0 -= ry2;
                    d -= t0;
                    drawFlag = false;
                }
            } else {
                if (d < 0) {
                    xp++;
                    t1 += ry2;
                    d += t1;
                    drawFlag = false;
                } else {
                    yp--;
                    t0 -= rx2;
                    d -= t0;
                    drawFlag = true;
                }
            }
        }
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#drawRGB(int[], int, int, int, int, int, int, boolean)
     */
    public abstract void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha);

    public abstract void copyArea(int x_src, int y_src, int width, int height, int x_dest, int y_dest, int anchor);
    
    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#drawImage(org.thenesis.microbackend.ui.graphics.VirtualImage, int, int, int)
     */
    public void drawImage(VirtualImage img, int x, int y, int anchor) {
        
        if (Logging.TRACE_ENABLED)
            System.out.println("VirtualGraphics.drawImage(): not yet implemented");
        
        if (!img.render(this, x, y, anchor)) {
            throw new IllegalArgumentException("");
        }
    }

    //
    // public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3)
    // {
    //
    // x1 += transX;
    // y1 += transY;
    // x2 += transX;
    // y2 += transY;
    // x3 += transX;
    // y3 += transY;
    //
    // SDLGfx.trigonColor(surface, x1, y1, x2, y2, x3, y3, sdlGfxColor);
    // }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#drawRoundRect(int, int, int, int, int, int)
     */
    public void drawRoundRect(int x, int y, int w, int h, int arcWidth, int arcHeight) {
        
        drawRect(x, y, w, h);

//        // Vertical lines
//        drawLine(x, y + arcHeight / 2, x, y + h - arcHeight / 2);
//        drawLine(x + w + 1, y + arcHeight / 2, x + w + 1, y + h - arcHeight / 2);
//
//        // Horizontal lines
//        drawLine(x + arcWidth / 2, y, x + w - arcWidth / 2, y);
//        drawLine(x + arcWidth / 2, y + h + 1, x + w - arcWidth / 2, y + h + 1);
//
//        // Rounded corners
//        drawArc(x, y, arcWidth, arcHeight, 90, 90); // top left
//        drawArc(x + w - arcWidth, y, arcWidth, arcHeight, 0, 90); // top right
//        drawArc(x, y + h - arcHeight, arcWidth, arcHeight, 180, 90); // bottom
//        // left
//        drawArc(x + w - arcWidth, y + h - arcHeight, arcWidth, arcHeight, 270, 90); // bottom
//        // right

    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualGraphicsInterface#fillRoundRect(int, int, int, int, int, int)
     */
    public void fillRoundRect(int x, int y, int w, int h, int arcWidth, int arcHeight) {
        
        fillRect(x, y, w, h);

//        fillRect(x, y + arcHeight / 2, w + 1, h - arcHeight + 1); // center
//        fillRect(x + arcWidth / 2, y, w - arcWidth, arcHeight); // top middle
//        fillRect(x + arcWidth / 2, y + h - arcHeight, w - arcWidth, arcHeight); // bottom
//        // middle
//
//        // Rounded corners
//        fillArc(x, y, arcWidth, arcHeight, 90, 90); // top left
//        fillArc(x + w - arcWidth, y, arcWidth, arcHeight, 0, 90); // top right
//        fillArc(x, y + h - arcHeight, arcWidth, arcHeight, 180, 90); // bottom
//        // left
//        fillArc(x + w - arcWidth, y + h - arcHeight, arcWidth, arcHeight, 270, 90); // bottom
//        // right

    }
    
    public void setFont(VirtualFont font) {
        currentFont = font;
    }

    public VirtualFont getFont() {
        return currentFont;
    }

}
