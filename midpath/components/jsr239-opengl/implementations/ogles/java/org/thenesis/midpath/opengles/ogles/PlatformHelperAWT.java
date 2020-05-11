package org.thenesis.midpath.opengles.ogles;

import java.nio.Buffer;

import javax.microedition.khronos.opengles.GL10;

public class PlatformHelperAWT extends PlatformHelper {
    
    /*
     * @see org.thenesis.midpath.opengles.ogles.Platform#convertToPlatformByteOrder(java.nio.Buffer)
     */
    public Buffer convertToPlatformByteOrder(Buffer buffer) { 
        return NIOHelperSE.convertToPlatformByteOrder(buffer);
    }
    
    public boolean isBufferOrderBigEndian(Buffer buffer) {
        return NIOHelperSE.isBufferOrderBigEndian(buffer);
    }
    
    public boolean checkIfFullNIOAvailable() {
        return true;
    }
    
    public void drawColorBufferToGraphics(GL10 gl, Object g) {
        // TODO
    }

    public int getGraphicsHeight(Object g) {
        // TODO Auto-generated method stub
        return 0;
    }

    public int getGraphicsWidth(Object g) {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isGraphicsCompatibleWithPixmapSurface(Object g) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean isGraphicsCompatibleWithWindowSurface(Object g) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean copySurfaceToPixmap(Object srcGraphics, Object dstGraphics) {
        // TODO Auto-generated method stub
        return false;
    }

    
    

}
