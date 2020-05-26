package org.thenesis.microbackend.ui.graphics;

public interface VirtualGraphics {

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
    
    public void setDimensions(int w, int h);

    public void setColor(int red, int green, int blue);

    public void setColor(int RGB);

    public void setGrayScale(int value);

    public void drawString(String str, int x, int y, int anchor);

    public void setClip(int x, int y, int width, int height);

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
    public void clipRect(int x, int y, int width, int height);
    
    /**
     * Preserve runtime GC.
     * @param systemX The system upper left x coordinate
     * @param systemY The system upper left y coordinate
     * @param systemW The system width of the rectangle
     * @param systemH The system height of the rectangle
     */
    public void preserveRuntimeGC(int systemX, int systemY, int systemW, int systemH);
    
    /**
     * Restore the runtime GC.
     * - Release the internal runtime clip values by
     * unsetting the variable.
     * - Restore the original translation
     */
    public void restoreRuntimeGC();

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
    public void translate(int x, int y);

    /**
     * Gets the X coordinate of the translated origin of this graphics context.
     * @return X of current origin
     */
    public int getTranslateX();

    /**
     * Gets the Y coordinate of the translated origin of this graphics context.
     * @return Y of current origin
     */
    public int getTranslateY();

    /**
     * Gets the current color.
     * @return an integer in form <code>0x00RRGGBB</code>
     * @see #setColor(int, int, int)
     */
    public int getColor();
    
    public void setFont(VirtualFont font);
    
    public VirtualFont getFont();

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
    public int getClipX();

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
    public int getClipY();

    /**
     * Gets the width of the current clipping area.
     * @return width of the current clipping area.
     * @see #clipRect(int, int, int, int)
     * @see #setClip(int, int, int, int)
     */
    public int getClipWidth();

    /**
     * Gets the height of the current clipping area.
     * @return height of the current clipping area.
     * @see #clipRect(int, int, int, int)
     * @see #setClip(int, int, int, int)
     */
    public int getClipHeight();

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
    public void drawChar(char character, int x, int y, int anchor);

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
    public void drawRegion(VirtualImage src, int x_src, int y_src, int width, int height, int transform, int x_dest, int y_dest, int anchor);
    
    /**
     * Native implementation of CopyArea method.
     *
     * @param x_src  the x coordinate of upper left corner of source area
     * @param y_src  the y coordinate of upper left corner of source area
     * @param width  the width of the source area
     * @param height the height of the source area
     * @param x_dest the x coordinate of the destination anchor point
     * @param y_dest the y coordinate of the destination anchor point
     * @param anchor the anchor point for positioning the region within
     *        the destination image
     *
     * @throws IllegalArgumentException if the region to be copied exceeds
     * the bounds of the source image
     */
    public void copyArea(int x_src, int y_src, int width, int height, int x_dest, int y_dest, int anchor);

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
    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3);

    /**
     * Reset this Graphics context with the given coordinates
     *
     * @param x1 The upper left x coordinate
     * @param y1 The upper left y coordinate
     * @param x2 The lower right x coordinate
     * @param y2 The lower right y coordinate
     */
    public void reset(int x1, int y1, int x2, int y2);
    
    /**
     * Reset this Graphics context to its default dimensions
     * (same as reset(0, 0, maxWidth, maxHeight)
     */
    public void reset();
    
    /**
     * Reset the Graphic context with all items related
     * to machine independent context. 
     * There is no translation and clipping involve 
     * since different implementations may map it
     * directly or not.
     * Only Font, Style, and Color are reset in
     * this function.
     */
    public void resetGC();

    //--------------------------------------------------------------------------
    // ------
    // Line.
    //--------------------------------------------------------------------------
    /**
     * Draws a line from point (x0, y0) to point (x1, y1).
     */
    public void drawLine(int x0, int y0, int x1, int y1);

    //--------------------------------------------------------------------------
    // ------
    // Rectangle.
    //--------------------------------------------------------------------------
    // ------
    public void drawRect(int x0, int y0, int width, int height);

    public void fillRect(int x0, int y0, int width, int height);

    public void drawArc(int x, int y, int rx, int ry, int startAngle, int arcAngle);

    public void fillArc(int x, int y, int rx, int ry, int startAngle, int arcAngle);

    public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha);

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
    public void drawImage(VirtualImage img, int x, int y, int anchor);

    public void drawRoundRect(int x, int y, int w, int h, int arcWidth, int arcHeight);

    public void fillRoundRect(int x, int y, int w, int h, int arcWidth, int arcHeight);

}