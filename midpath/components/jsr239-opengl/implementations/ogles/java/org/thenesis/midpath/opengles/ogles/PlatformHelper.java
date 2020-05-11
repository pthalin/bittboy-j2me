package org.thenesis.midpath.opengles.ogles;

import java.nio.Buffer;

import javax.microedition.khronos.opengles.GL10;

public abstract class PlatformHelper {
    
    private static boolean fullNIOAvailable;
    private static PlatformHelper platformHelper;
    
    static {
        platformHelper = new PlatformHelperMIDPath();
        fullNIOAvailable = platformHelper.checkIfFullNIOAvailable();
    }
    
    public boolean isFullNIOAvailable() {
        return fullNIOAvailable;
    }
    
    public abstract boolean checkIfFullNIOAvailable();
    
    public static PlatformHelper getPlatformHelper() {
       return platformHelper;
    }

    public abstract Buffer convertToPlatformByteOrder(Buffer buffer);

    public abstract boolean isBufferOrderBigEndian(Buffer buffer);
    
    public abstract void drawColorBufferToGraphics(GL10 egl, Object g);
    
    public abstract int getGraphicsWidth(Object g);
    
    public abstract int getGraphicsHeight(Object g);
    
    public abstract boolean isGraphicsCompatibleWithPixmapSurface(Object g);
    
    public abstract boolean isGraphicsCompatibleWithWindowSurface(Object g);
    
    public abstract boolean copySurfaceToPixmap(Object srcGraphics, Object dstGraphics);

}