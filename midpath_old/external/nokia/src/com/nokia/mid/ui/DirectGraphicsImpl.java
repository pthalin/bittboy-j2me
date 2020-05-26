
package com.nokia.mid.ui;


import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


/**
 * 
 * @author liang.wu
 *
 */
public class DirectGraphicsImpl
    implements DirectGraphics
{

    Graphics graphics;
    
    final static int MIDP2Manip2NokiaManip[][] = 
    { 
        { 0,                        FLIP_VERTICAL|FLIP_HORIZONTAL|ROTATE_180 },         //Sprite.TRANS_NONE
        { FLIP_VERTICAL,            FLIP_HORIZONTAL|ROTATE_180 },                       //Sprite.TRANS_MIRROR_ROT180
        { FLIP_HORIZONTAL,          FLIP_VERTICAL|ROTATE_180 },                         //Sprite.TRANS_MIRROR
        { ROTATE_180,               FLIP_VERTICAL|FLIP_HORIZONTAL },                    //Sprite.TRANS_ROT180
        { FLIP_VERTICAL|ROTATE_90,  FLIP_HORIZONTAL|ROTATE_270 },                       //Sprite.TRANS_MIRROR_ROT270
        { ROTATE_90,                FLIP_VERTICAL|FLIP_HORIZONTAL|ROTATE_270 },         //Sprite.TRANS_ROT90
        { ROTATE_270,               FLIP_VERTICAL|FLIP_HORIZONTAL|ROTATE_90 },          //Sprite.TRANS_ROT270
        { FLIP_HORIZONTAL|ROTATE_90,FLIP_VERTICAL|ROTATE_270 }                          //Sprite.TRANS_MIRROR_ROT90
    };

    public DirectGraphicsImpl(Graphics g)
    {
        graphics = g;
    }

    /**
     * 
     * @param manip flip or rotate value or a combination of values
     * @return
     */
    public int nokiaManip2MIDP2Manip(int manip)
    {
        for(int i = 0; i < MIDP2Manip2NokiaManip.length; i++)
        {
            for(int j = 0; j < MIDP2Manip2NokiaManip[i].length; j++)
                if(MIDP2Manip2NokiaManip[i][j] == manip)
                    return i;

        }
        return 0;
    }

    public void drawImage(Image image, int x, int y, int anchor, int manipulation)
    {
        int width = image.getWidth();
        int height = image.getHeight();
        int transform = nokiaManip2MIDP2Manip(manipulation);
        graphics.drawRegion(image, 0, 0, width, height, transform, x, y, anchor);
    }

    public void drawPixels(short pixels[], boolean transparency, int offset, int scanlength, int x, int y, int width, int height, int manipulation, int format)
    {
        //System.out.println("drawPixels(short) not implemented");
    }
    
    public void drawPixels(byte pixels[], byte transparencyMask[], int offset, int scanlength, int x, int y, int width, int height, int manipulation, int format)
    {
        //System.out.println("drawPixels(byte) not implemented");
    }
    
    public void drawPixels(int pixels[], boolean transparency, int offset, int scanlength, int x, int y, int width, int height, int manipulation, int format)
    {
        //System.out.println("drawPixels(int) not implemented");
    }

    public void drawPolygon(int xPoints[], int xOffset, int yPoints[], int yOffset, int nPoints, int argbColor)
    {
        int color = graphics.getColor();
        int strokestyle = graphics.getStrokeStyle();
        graphics.setColor(argbColor);
        graphics.setStrokeStyle(Graphics.SOLID);

        for(int i = 0; i < nPoints-1; i++)
        {
            graphics.drawLine(xPoints[xOffset+i], yPoints[yOffset+i],
                              xPoints[xOffset+i+1], yPoints[yOffset+i+1]);
        }

        graphics.drawLine(xPoints[xOffset+nPoints-1],
                          yPoints[yOffset+nPoints-1],
                          xPoints[xOffset],
                          yPoints[yOffset]);

        graphics.setColor(color);
        graphics.setStrokeStyle(strokestyle);
    }

    public void drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int argbColor)
    {
        drawPolygon(new int[] { x1, x2, x3 }, 0, new int[] { y1, y2, y3 }, 0, 3, argbColor);
    }

    public void fillPolygon(int xPoints[], int xOffset, int yPoints[], int yOffset, int nPoints, int argbColor)
    {
        int color = graphics.getColor();
        graphics.setColor(argbColor);
        
        for(int i = 1; i < nPoints - 2; i++)
        {
            graphics.fillTriangle(xPoints[xOffset],
                                  yPoints[yOffset],
                                  xPoints[xOffset + i],
                                  yPoints[yOffset + i],
                                  xPoints[xOffset + i + 1],
                                  yPoints[yOffset + i + 1]);
        }
        
        graphics.setColor(color);
    }

    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int argbColor)
    {
        int color = graphics.getColor();
        graphics.setColor(argbColor);
        graphics.fillTriangle(x1, y1, x2, y2, x3, y3);
        graphics.setColor(color);
    }

    public int getAlphaComponent()
    {
        return (graphics.getColor() >> 24) & 0xFF;
    }

    public int getNativePixelFormat()
    {
        return TYPE_INT_8888_ARGB;
    }

    public void getPixels(int pixels[], int offset, int scanlength, int x, int y, int width, int height, int format)
    {
        //System.out.println("getPixels(int) not implemented");
    }

    public void getPixels(short pixels[], int offset, int scanlength, int x, int y, int width, int height, int format)
    {
        //System.out.println("getPixels(short) not implemented");
    }

    public void getPixels(byte[] pixels, byte[] transparencyMask, int offset, int scanlength, int x, int y, int width, int height, int format)
    {
        //System.out.println("getPixels(byte) not implemented");
    }

    public void setARGBColor(int argbColor)
    {
    	graphics.setColor(argbColor);
    }
}
