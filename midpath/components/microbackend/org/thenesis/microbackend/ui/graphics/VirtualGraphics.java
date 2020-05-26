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

public class VirtualGraphics {

    private VirtualSurface surface;

    // Image blending mode. TODO: replace BLEND by ADD and SUB.
    public static final int REPLACE = 0, BLEND = 1, LOGIC = 2;
    private int blendMode = REPLACE;

    public static final int MAX_CIRCLE_RADIUS = 16384;
    public static final int MAX_ELLIPSE_RADIUS = 16384;
    
    /**
     * Constant for centering text and images horizontally
     * around the anchor point
     *
     * <P>Value <code>1</code> is assigned to <code>HCENTER</code>.</P>
     */
    public static final int HCENTER = 1;
  
    /**
     * Constant for centering images vertically
     * around the anchor point.
     *
     * <P>Value <code>2</code> is assigned to <code>VCENTER</code>.</P>
     */
    public static final int VCENTER = 2;
  
    /**
     * Constant for positioning the anchor point of text and images
     * to the left of the text or image.
     *
     * <P>Value <code>4</code> is assigned to <code>LEFT</code>.</P>
     */
    public static final int LEFT = 4;
  
    /**
     * Constant for positioning the anchor point of text and images
     * to the right of the text or image.
     *
     * <P>Value <code>8</code> is assigned to <code>RIGHT</code>.</P>
     */
    public static final int RIGHT = 8;
  
    /**
     * Constant for positioning the anchor point of text and images
     * above the text or image.
     *
     * <P>Value <code>16</code> is assigned to <code>TOP</code>.</P>
     */
    public static final int TOP = 16;
  
    /**
     * Constant for positioning the anchor point of text and images
     * below the text or image.
     *
     * <P>Value <code>32</code> is assigned to <code>BOTTOM</code>.</P>
     */
    public static final int BOTTOM = 32;
  
    /**
     * Constant for positioning the anchor point at the baseline of text.
     *
     * <P>Value <code>64</code> is assigned to <code>BASELINE</code>.</P>
     */
    public static final int BASELINE = 64;

    /**
     * Constant for the <code>SOLID</code> stroke style.
     *
     * <P>Value <code>0</code> is assigned to <code>SOLID</code>.</P>
     */
    public static final int SOLID = 0;

    /**
     * Constant for the <code>DOTTED</code> stroke style.
     *
     * <P>Value <code>1</code> is assigned to <code>DOTTED</code>.</P>
     */
    public static final int DOTTED = 1;


    // private implementation //

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

    Rectangle clipRectangle = new Rectangle();
    private int internalColor;

    VirtualGraphics(VirtualSurface surface) {
        this.surface = surface;
    }

    public VirtualSurface getSurface() {
        return surface;
    }

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

    public synchronized void drawString(String str, int x, int y, int anchor) {
        
        if (Logging.TRACE_ENABLED) 
            System.out.println("[DEBUG] VirtualGraphics.drawString(): not implemented yet");
    
        if (currentFont != null) {
            currentFont.render(this, str, x, y, anchor);
        }

    }

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

    public synchronized void clipRect(int x, int y, int width, int height) {
        clipRectInternal(x, y, width, height);
        clipRectangle.set(clipX1, clipY1, clipX2, clipY2);
    }
    
    /**
     * Intersects the current clip with the specified rectangle.
     * The resulting clipping area is the intersection of the current
     * clipping area and the specified rectangle.
     * This method can only be used to make the current clip smaller.
     * To set the current clip larger, use the <code>setClip</code> method.
     * Rendering operations have no effect outside of the clipping area.
     * @param x the x coordinate of the rectangle to intersect the clip with
     * @param y the y coordinate of the rectangle to intersect the clip with
     * @param width the width of the rectangle to intersect the clip with
     * @param height the height of the rectangle to intersect the clip with
     * @see #setClip(int, int, int, int)
     */
    public synchronized void clipRectInternal(int x, int y, int width, int height) {

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
     * Translates the origin of the graphics context to the point
     * <code>(x, y)</code> in the current coordinate system. All coordinates
     * used in subsequent rendering operations on this graphics
     * context will be relative to this new origin.<p>
     *
     * The effect of calls to <code>translate()</code> are
     * cumulative. For example, calling
     * <code>translate(1, 2)</code> and then <code>translate(3,
     * 4)</code> results in a translation of
     * <code>(4, 6)</code>. <p>
     *
     * The application can set an absolute origin <code>(ax,
     * ay)</code> using the following
     * technique:<p>
     * <code>
     * g.translate(ax - g.getTranslateX(), ay - g.getTranslateY())
     * </code><p>
     *
     * @param x the x coordinate of the new translation origin
     * @param y the y coordinate of the new translation origin
     * @see #getTranslateX()
     * @see #getTranslateY()
     */
    public synchronized void translate(int x, int y) {
        transX += x;
        transY += y;
    }
    
    /**
     * Gets the X coordinate of the translated origin of this graphics context.
     * @return X of current origin
     */
    public synchronized int getTranslateX() {
        return transX;
    }

    /**
     * Gets the Y coordinate of the translated origin of this graphics context.
     * @return Y of current origin
     */
    public synchronized int getTranslateY() {
        return transY;
    }

    /**
     * Gets the current color.
     * @return an integer in form <code>0x00RRGGBB</code>
     * @see #setColor(int, int, int)
     */
    public synchronized int getColor() {
        return rgbColor;
    }

    
    /**
     * Gets the X offset of the current clipping area, relative
     * to the coordinate system origin of this graphics context.
     * Separating the <code>getClip</code> operation into two methods returning
     * integers is more performance and memory efficient than one
     * <code>getClip()</code> call returning an object.
     * @return X offset of the current clipping area
     * @see #clipRect(int, int, int, int)
     * @see #setClip(int, int, int, int)
     */
    public synchronized int getClipX() {
        return clipX1 - transX;
    }

    /**
     * Gets the Y offset of the current clipping area, relative
     * to the coordinate system origin of this graphics context.
     * Separating the <code>getClip</code> operation into two methods returning
     * integers is more performance and memory efficient than one
     * <code>getClip()</code> call returning an object.
     * @return Y offset of the current clipping area
     * @see #clipRect(int, int, int, int)
     * @see #setClip(int, int, int, int)
     */
    public synchronized int getClipY() {
        return clipY1 - transY;
    }

    /**
     * Gets the width of the current clipping area.
     * @return width of the current clipping area.
     * @see #clipRect(int, int, int, int)
     * @see #setClip(int, int, int, int)
     */
    public synchronized int getClipWidth() {
        return clipX2 - clipX1;
    }


    /**
     * Gets the height of the current clipping area.
     * @return height of the current clipping area.
     * @see #clipRect(int, int, int, int)
     * @see #setClip(int, int, int, int)
     */
    public synchronized int getClipHeight() {
        return clipY2 - clipY1;
    }

    
    /**
     * Draws the specified character using the current font and color.
     * @param character the character to be drawn
     * @param x the x coordinate of the anchor point
     * @param y the y coordinate of the anchor point
     * @param anchor the anchor point for positioning the text; see
     * <a href="#anchor">anchor points</a>
     *
     * @throws IllegalArgumentException if <code>anchor</code>
     * is not a legal value
     *
     * @see #drawString(java.lang.String, int, int, int)
     * @see #drawChars(char[], int, int, int, int, int)
     */
    public void drawChar(char character, int x, int y, int anchor) {
        drawString(String.valueOf(character), x, y, anchor);
    }

    
    /**
     * Copies a region of the specified source image to a location within
     * the destination, possibly transforming (rotating and reflecting)
     * the image data using the chosen transform function.
     *
     * <p>The destination, if it is an image, must not be the same image as
     * the source image.  If it is, an exception is thrown.  This restriction
     * is present in order to avoid ill-defined behaviors that might occur if
     * overlapped, transformed copies were permitted.</p>
     *
     * <p>The transform function used must be one of the following, as defined
     * in the {@link javax.microedition.lcdui.game.Sprite Sprite} class:<br>
     *
     * <code>Sprite.TRANS_NONE</code> - causes the specified image
     * region to be copied unchanged<br>
     * <code>Sprite.TRANS_ROT90</code> - causes the specified image
     * region to be rotated clockwise by 90 degrees.<br>
     * <code>Sprite.TRANS_ROT180</code> - causes the specified image
     * region to be rotated clockwise by 180 degrees.<br>
     * <code>Sprite.TRANS_ROT270</code> - causes the specified image
     * region to be rotated clockwise by 270 degrees.<br>
     * <code>Sprite.TRANS_MIRROR</code> - causes the specified image
     * region to be reflected about its vertical center.<br>
     * <code>Sprite.TRANS_MIRROR_ROT90</code> - causes the specified image
     * region to be reflected about its vertical center and then rotated
     * clockwise by 90 degrees.<br>
     * <code>Sprite.TRANS_MIRROR_ROT180</code> - causes the specified image
     * region to be reflected about its vertical center and then rotated
     * clockwise by 180 degrees.<br>
     * <code>Sprite.TRANS_MIRROR_ROT270</code> - causes the specified image
     * region to be reflected about its vertical center and then rotated
     * clockwise by 270 degrees.<br></p>
     *
     * <p>If the source region contains transparent pixels, the corresponding
     * pixels in the destination region must be left untouched.  If the source
     * region contains partially transparent pixels, a compositing operation
     * must be performed with the destination pixels, leaving all pixels of
     * the destination region fully opaque.</p>
     *
     * <p> The <code>(x_src, y_src)</code> coordinates are relative to
     * the upper left
     * corner of the source image.  The <code>x_src</code>,
     * <code>y_src</code>, <code>width</code>, and <code>height</code>
     * parameters specify a rectangular region of the source image.  It is
     * illegal for this region to extend beyond the bounds of the source
     * image.  This requires that: </P>
     * <TABLE BORDER="2">
     * <TR>
     * <TD ROWSPAN="1" COLSPAN="1">
     *    <pre><code>
     *   x_src &gt;= 0
     *   y_src &gt;= 0
     *   x_src + width &lt;= source width
     *   y_src + height &lt;= source height    </code></pre>
     * </TD>
     * </TR>
     * </TABLE>
     * <P>
     * The <code>(x_dest, y_dest)</code> coordinates are relative to
     * the coordinate
     * system of this Graphics object.  It is legal for the destination
     * area to extend beyond the bounds of the <code>Graphics</code>
     * object.  Pixels
     * outside of the bounds of the <code>Graphics</code> object will
     * not be drawn.</p>
     *
     * <p>The transform is applied to the image data from the region of the
     * source image, and the result is rendered with its anchor point
     * positioned at location <code>(x_dest, y_dest)</code> in the
     * destination.</p>
     *
     * @param src the source image to copy from
     * @param x_src the x coordinate of the upper left corner of the region
     * within the source image to copy
     * @param y_src the y coordinate of the upper left corner of the region
     * within the source image to copy
     * @param width the width of the region to copy
     * @param height the height of the region to copy
     * @param transform the desired transformation for the selected region
     * being copied
     * @param x_dest the x coordinate of the anchor point in the
     * destination drawing area
     * @param y_dest the y coordinate of the anchor point in the
     * destination drawing area
     * @param anchor the anchor point for positioning the region within
     * the destination image
     *
     * @throws IllegalArgumentException if <code>src</code> is the
     * same image as the
     * destination of this <code>Graphics</code> object
     * @throws NullPointerException if <code>src</code> is <code>null</code>
     * @throws IllegalArgumentException if <code>transform</code> is invalid
     * @throws IllegalArgumentException if <code>anchor</code> is invalid
     * @throws IllegalArgumentException if the region to be copied exceeds
     * the bounds of the source image
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
    
    /**
     * Fills the specified triangle will the current color.  The lines
     * connecting each pair of points are included in the filled
     * triangle.
     *
     * @param x1 the x coordinate of the first vertex of the triangle
     * @param y1 the y coordinate of the first vertex of the triangle
     * @param x2 the x coordinate of the second vertex of the triangle
     * @param y2 the y coordinate of the second vertex of the triangle
     * @param x3 the x coordinate of the third vertex of the triangle
     * @param y3 the y coordinate of the third vertex of the triangle
     *
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
    void setDimensions(int w, int h) {
        maxWidth  = (short) (w & 0x7fff);
        maxHeight = (short) (h & 0x7fff);
    }
    
    /**
     * Reset this Graphics context with the given coordinates
     *
     * @param x1 The upper left x coordinate
     * @param y1 The upper left y coordinate
     * @param x2 The lower right x coordinate
     * @param y2 The lower right y coordinate
     */
    void reset(int x1, int y1, int x2, int y2) {
        resetGC();
        transX = transY = 0;
        setClip(x1, y1, x2 - x1, y2 - y1);
    }

    /**
     * Reset this Graphics context to its default dimensions
     * (same as reset(0, 0, maxWidth, maxHeight)
     */
    public void reset() {
        reset(0, 0, maxWidth, maxHeight);
    }

    /**
     * Reset the Graphic context with all items related
     * to machine independent context. 
     * There is no translation and clipping involve 
     * since different implementations may map it
     * directly or not.
     * Only Font, Style, and Color are reset in
     * this function.
     */
    void resetGC() {
        currentFont = VirtualToolkit.getToolkit().getDefaultFont();
        style       = SOLID;
        rgbColor    = gray = 0;
        pixel       = getPixel(rgbColor, gray, true);
    }






    //--------------------------------------------------------------------------
    // ------
    // Span.
    //--------------------------------------------------------------------------
    // ------
    private void drawSpan(int dst[], int dstPosition, int w) {

        // if (Logging.TRACE_ENABLED)
        // System.out.println(
        // "[DEBUG]VirtualGraphics.drawSpan : surface data size= " +
        // surface.data.length + " dstPosition=" + dstPosition + " w=" + w);

        switch (blendMode) {
        case REPLACE:
            for (int x = dstPosition; x < (dstPosition + w); x++) {
                dst[x] = internalColor;
            }
            break;
        }

    }

    //--------------------------------------------------------------------------
    // ------
    // Line.
    //--------------------------------------------------------------------------
    // ------
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
        drawSpan(surface.data, y * surface.width + x0, x1 - x0 + 1);
    }

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
            drawSpan(surface.data, (y0 + ry) * surface.width + x, 1);
    }

    /**
     * Draws a line from point (x0, y0) to point (x1, y1).
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
                    drawSpan(surface.data, y * surface.width + x, 1);
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
                    drawSpan(surface.data, y * surface.width + x, 1);
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
    public void drawRect(int x0, int y0, int width, int height) {

        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG]VirtualGraphics.drawRect(): x=" + x0 + " y0=" + y0 + " width=" + width + " height= " + height
                    + " color=" + Integer.toHexString(rgbColor));

        int x1 = x0 + width;
        int y1 = y0 + height;

        if (surface == null)
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

    public void fillRect(int x0, int y0, int width, int height) {

        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG]VirtualGraphics.fillRect(): x=" + x0 + " y0=" + y0 + " width=" + width + " height= " + height
                    + " color=" + Integer.toHexString(rgbColor));

        if (surface == null)
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
            drawSpan(surface.data, (y0 + ry) * surface.width + x0, w);
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
                drawSpan(surface.data, y0 * surface.width + x0, 1);
            if (right)
                drawSpan(surface.data, y0 * surface.width + x1, 1);
        }
        if ((y1 >= r.ymin) && (y1 <= r.ymax) && (yp != 0)) {
            if (left)
                drawSpan(surface.data, y1 * surface.width + x0, 1);
            if (right)
                drawSpan(surface.data, y1 * surface.width + x1, 1);
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
                drawSpan(surface.data, y0 * surface.width + x0, w);
            if ((y1 >= r.ymin) && (y1 <= r.ymax) && (yp != 0))
                drawSpan(surface.data, y1 * surface.width + x0, w);
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
                    drawSpan(surface.data, y0 * surface.width + x0, 1); // Top
                // left.
                // Second
                // quadrant
                // .
            }
            if (right) {
                insideFlag = reflexAngle ? (xpys - ypxs < 0 || xpye - ypxe > 0) : (xpys - ypxs < 0 && xpye - ypxe > 0);
                if (insideFlag)
                    drawSpan(surface.data, y0 * surface.width + x1, 1); // Top
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
                    drawSpan(surface.data, y1 * surface.width + x0, 1); // Bottom
                // left.
                // Third
                // quadrant
                // .
            }
            if (right) {
                insideFlag = reflexAngle ? (xpys + ypxs < 0 || xpye + ypxe > 0) : (xpys + ypxs < 0 && xpye + ypxe > 0);
                if (insideFlag)
                    drawSpan(surface.data, y1 * surface.width + x1, 1); // Bottom
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
                    drawSpan(surface.data, y0 * surface.width + x0, w);
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
                    drawSpan(surface.data, y0 * surface.width + x0, w);

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
                    drawSpan(surface.data, y0 * surface.width + x0, w);
            }
        }
    }

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

    public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {

        if (Logging.TRACE_ENABLED)
            System.out.println("VirtualGraphics.drawRGB()");
        // System.out.println(
        // "[DEBUG] VirtualGraphics.drawRGB()() : rgbData.length=" +
        // rgbData.length + "  surface.data.length=" + surface.data.length);

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

        if (processAlpha) {
            for (int ry = 0; ry < h; ry++) {

                int srcPosition = offset + (symin + ry) * scanlength + sxmin;
                int dstPosition = (dymin + ry) * surface.width + dxmin;
                int length = w;

                for (int i = 0, sp = srcPosition, dp = dstPosition; i < length; i++, sp += 1, dp += 1) {
                    if (((rgbData[i + srcPosition]) & 0xFF000000) == 0xFF000000)
                        surface.data[i + dstPosition] = rgbData[i + srcPosition];
                }

            }
        } else {
            for (int ry = 0; ry < h; ry++) {

                int srcPosition = offset + (symin + ry) * scanlength + sxmin;
                int dstPosition = (dymin + ry) * surface.width + dxmin;
                int length = w;

                for (int i = 0, sp = srcPosition, dp = dstPosition; i < length; i++, sp += 1, dp += 1) {
                    // System.out.println(
                    // "[DEBUG] VirtualGraphics.drawRGB()() : dest=" + (i +
                    // dstPosition) + " src=" + (i + srcPosition));
                    surface.data[i + dstPosition] = rgbData[i + srcPosition] | 0xFF000000;
                }

            }
        }

    }

    protected void doCopyArea(int x_src, int y_src, int width, int height, int x_dest, int y_dest, int anchor) {

        if (Logging.TRACE_ENABLED)
            System.out.println("VirtualGraphics.doCopyArea()");

        x_src += transX;
        y_src += transY;

        VirtualImage image = new VirtualImage(width, height);

        for (int j = 0; j < height; j++) {

            int srcPosition = (y_src + j) * surface.width + x_src;
            int dstPosition = j * width;

            for (int i = 0; i < width; i++) {
                image.surface.data[dstPosition + i] = surface.data[srcPosition + i];
            }
        }

        drawImage(image, x_dest, y_dest, anchor);

    }
    
    /**
     * Draws the specified image by using the anchor point.
     * The image can be drawn in different positions relative to
     * the anchor point by passing the appropriate position constants.
     * See <a href="#anchor">anchor points</a>.
     *
     * <p>If the source image contains transparent pixels, the corresponding
     * pixels in the destination image must be left untouched.  If the source
     * image contains partially transparent pixels, a compositing operation 
     * must be performed with the destination pixels, leaving all pixels of 
     * the destination image fully opaque.</p>
     *
     * <p>If <code>img</code> is the same as the destination of this Graphics
     * object, the result is undefined.  For copying areas within an
     * <code>Image</code>, {@link #copyArea copyArea} should be used instead.
     * </p>
     *
     * @param img the specified image to be drawn
     * @param x the x coordinate of the anchor point
     * @param y the y coordinate of the anchor point
     * @param anchor the anchor point for positioning the image
     * @throws IllegalArgumentException if <code>anchor</code>
     * is not a legal value
     * @throws NullPointerException if <code>img</code> is <code>null</code>
     * @see Image
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

    public void drawRoundRect(int x, int y, int w, int h, int arcWidth, int arcHeight) {

        // Vertical lines
        drawLine(x, y + arcHeight / 2, x, y + h - arcHeight / 2);
        drawLine(x + w + 1, y + arcHeight / 2, x + w + 1, y + h - arcHeight / 2);

        // Horizontal lines
        drawLine(x + arcWidth / 2, y, x + w - arcWidth / 2, y);
        drawLine(x + arcWidth / 2, y + h + 1, x + w - arcWidth / 2, y + h + 1);

        // Rounded corners
        drawArc(x, y, arcWidth, arcHeight, 90, 90); // top left
        drawArc(x + w - arcWidth, y, arcWidth, arcHeight, 0, 90); // top right
        drawArc(x, y + h - arcHeight, arcWidth, arcHeight, 180, 90); // bottom
        // left
        drawArc(x + w - arcWidth, y + h - arcHeight, arcWidth, arcHeight, 270, 90); // bottom
        // right

    }

    public void fillRoundRect(int x, int y, int w, int h, int arcWidth, int arcHeight) {

        fillRect(x, y + arcHeight / 2, w + 1, h - arcHeight + 1); // center
        fillRect(x + arcWidth / 2, y, w - arcWidth, arcHeight); // top middle
        fillRect(x + arcWidth / 2, y + h - arcHeight, w - arcWidth, arcHeight); // bottom
        // middle

        // Rounded corners
        fillArc(x, y, arcWidth, arcHeight, 90, 90); // top left
        fillArc(x + w - arcWidth, y, arcWidth, arcHeight, 0, 90); // top right
        fillArc(x, y + h - arcHeight, arcWidth, arcHeight, 180, 90); // bottom
        // left
        fillArc(x + w - arcWidth, y + h - arcHeight, arcWidth, arcHeight, 270, 90); // bottom
        // right

    }

}
