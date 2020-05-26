package org.thenesis.microbackend.ui.graphics;

public interface VirtualImage {

    /**
     * No transform is applied to the Sprite. This constant has a value of
     * <code>0</code>.
     */
    public static final int TRANS_NONE = 0;
    /**
     * Causes the Sprite to appear rotated clockwise by 90 degrees. This
     * constant has a value of <code>5</code>.
     */
    public static final int TRANS_ROT90 = 5;
    /**
     * Causes the Sprite to appear rotated clockwise by 180 degrees. This
     * constant has a value of <code>3</code>.
     */
    public static final int TRANS_ROT180 = 3;
    /**
     * Causes the Sprite to appear rotated clockwise by 270 degrees. This
     * constant has a value of <code>6</code>.
     */
    public static final int TRANS_ROT270 = 6;
    /**
     * Causes the Sprite to appear reflected about its vertical center. This
     * constant has a value of <code>2</code>.
     */
    public static final int TRANS_MIRROR = 2;
    /**
     * Causes the Sprite to appear reflected about its vertical center and then
     * rotated clockwise by 90 degrees. This constant has a value of
     * <code>7</code>.
     */
    public static final int TRANS_MIRROR_ROT90 = 7;
    /**
     * Causes the Sprite to appear reflected about its vertical center and then
     * rotated clockwise by 180 degrees. This constant has a value of
     * <code>1</code>.
     */
    public static final int TRANS_MIRROR_ROT180 = 1;
    /**
     * Causes the Sprite to appear reflected about its vertical center and then
     * rotated clockwise by 270 degrees. This constant has a value of
     * <code>4</code>.
     */
    public static final int TRANS_MIRROR_ROT270 = 4;

    public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height);

    public boolean render(VirtualGraphics g, int x, int y, int anchor);
    
    public boolean renderRegion(VirtualGraphics g, int x_src, int y_src, int width, int height, int transform, int x_dest, int y_dest,
            int anchor);

    public boolean isMutable();

    public int getWidth();

    public int getHeight();

    public VirtualGraphics getGraphics();

}