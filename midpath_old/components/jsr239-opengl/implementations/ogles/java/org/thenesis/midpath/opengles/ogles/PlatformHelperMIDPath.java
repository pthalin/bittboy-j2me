package org.thenesis.midpath.opengles.ogles;

import java.nio.Buffer;
import java.nio.ByteBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

import org.thenesis.midpath.ui.toolkit.virtual.VirtualGraphics;

public class PlatformHelperMIDPath extends PlatformHelper {
    
 
    public Buffer convertToPlatformByteOrder(Buffer buffer) {
        if (isFullNIOAvailable()) {
            return NIOHelperSE.convertToPlatformByteOrder(buffer);
        } else {
            // Do nothing on Java ME because default buffer order is the platform order
            return buffer;
        }
    }

    public boolean isBufferOrderBigEndian(Buffer buffer) {
        if (isFullNIOAvailable()) {
            return NIOHelperSE.isBufferOrderBigEndian(buffer);
        } else {
            // Default buffer order is the platform order
            return NativeGL10._isPlatformBigEndian();
        }
    }
    
    public boolean checkIfFullNIOAvailable() {
        
        String fullNIOStr = System.getProperty("org.thenesis.midpath.opengles.ogles.isFullNIO");
        
        boolean available = true;
        
        if (fullNIOStr != null) {
            fullNIOStr = fullNIOStr.trim();
            if (fullNIOStr.equalsIgnoreCase("yes") || fullNIOStr.equalsIgnoreCase("true")) {
                available = true;
            } else {
                available = false;
            }
        } else {
            String j2meStr = System.getProperty("microedition.platform");
            if (j2meStr != null) {
                j2meStr = j2meStr.trim();
                if (j2meStr.equalsIgnoreCase("j2me")) {
                    available = false;
                } else if (j2meStr.equalsIgnoreCase("j2se")) {
                    available = true;
                } else {
                    available = true;
                }
            }
        }
        
        System.out.println("PlatformHelperMIDPath: checkIfFullNIOAvailable ? " + available);
        
        return available;
        
    }

    public void drawColorBufferToGraphics(GL10 gl, Object g) {

        VirtualGraphics imageGraphics = (VirtualGraphics) g;
        int width = getGraphicsWidth(imageGraphics);
        int height = getGraphicsHeight(imageGraphics);
        //System.out.println("[DEBUG]NativeELG10: drawColorBufferToGraphics(): width="+ width + " height=" + height);
        //System.out.println("[DEBUG]NativeELG10: drawColorBufferToGraphics(): clipX="+ imageGraphics.getTranslateX() + " clipY=" + imageGraphics.getTranslateY());
        // int width = GameMap.getGraphicsAccess().getGraphicsWidth((Graphics)g);
        // int height =GameMap.getGraphicsAccess().getGraphicsHeight((Graphics)g);

        //int[] gData = imageGraphics.getSurface().data;
        // System.out.println("[DEBUG]NativeELG10: drawColorBufferToGraphics(): width*height="
        // + (width*height) + " gData.length=" + gData.length);

        byte[] data = new byte[width * height * 4];
        int[] imgData = new int[width * height];
        ByteBuffer buffer = ByteBuffer.wrap(data);
        gl.glReadPixels(0, 0, width, height, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, buffer);

        // Mirror and convert RGBA to ARGB
        int size = height * width;
        int midH = height / 2;
        for (int j = 0; j < midH; j++) {
            int hiOffset = j * width;
            int loOffset = size - hiOffset - width;
            for (int i = 0; i < width; i++) {

                int hiIndex = hiOffset + i;
                int srcHiIndex = hiIndex << 2;
                int hcR = data[srcHiIndex] & 0xFF;
                int hcG = data[srcHiIndex + 1] & 0xFF;
                int hcB = data[srcHiIndex + 2] & 0xFF;
                int hcA = data[srcHiIndex + 3] & 0xFF;

                int loIndex = loOffset + i;
                int dstLoIndex = loIndex << 2;
                int lcR = data[dstLoIndex] & 0xFF;
                int lcG = data[dstLoIndex + 1] & 0xFF;
                int lcB = data[dstLoIndex + 2] & 0xFF;
                int lcA = data[dstLoIndex + 3] & 0xFF;

                imgData[hiIndex] = (lcA << 24) | (lcR << 16) | (lcG << 8) | lcB;
                imgData[loIndex] = (hcA << 24) | (hcR << 16) | (hcG << 8) | hcB;

            }
        }
        
        Image img = Image.createRGBImage(imgData, width, height, false);
        imageGraphics.drawImage(img, 0, 0, Graphics.TOP | Graphics.LEFT);
        

        // Pixel:
        // 0 0 ffffffff ffffffff
        // ffff0000

        // byte[] b = new byte[4];
        // ByteBuffer bb = ByteBuffer.wrap(b);
        // //ByteBuffer bb =
        // ByteBuffer.allocateDirect(4).order(ByteOrder.LITTLE_ENDIAN);
        // gl.glReadPixels(0, 0, 1, 1, GL10.GL_RGBA , GL10.GL_UNSIGNED_BYTE,
        // bb);
        // System.out.println("drawColorBufferToGraphics: " +
        // Integer.toHexString(bb.asIntBuffer().get(0)) + " ");
        // System.out.println("drawColorBufferToGraphics: ");
        // for (int i = 0; i < 4; i++)
        // System.out.print(Integer.toHexString(bb.get(i)) + " ");
        // System.out.println();

    }

    public int getGraphicsHeight(Object g) {
        VirtualGraphics imageGraphics = (VirtualGraphics) g;
        int height = imageGraphics.getSurface().getHeight();
        //int height = GameMap.getGraphicsAccess().getGraphicsHeight(imageGraphics);
        return height;
    }

    public int getGraphicsWidth(Object g) {
        VirtualGraphics imageGraphics = (VirtualGraphics) g;
        int width = imageGraphics.getSurface().getWidth();
        //int width = GameMap.getGraphicsAccess().getGraphicsWidth(imageGraphics);
        return width;
    }

    public boolean isGraphicsCompatibleWithPixmapSurface(Object g) {
        // TODO Check if Graphics is created from a mutable Imabe
        return (g instanceof javax.microedition.lcdui.Graphics);
    }

    public boolean isGraphicsCompatibleWithWindowSurface(Object g) {
        // TODO Check if Graphics is created from GameCanvas or Canvas.paint()
        return (g instanceof javax.microedition.lcdui.Graphics);
    }

    public boolean copySurfaceToPixmap(Object src, Object dst) {

        VirtualGraphics srcGraphics = (VirtualGraphics) src;
        int srcWidth = getGraphicsWidth(src);
        int srcHeight = getGraphicsHeight(src);
        int[] srcData = srcGraphics.getSurface().data;

        VirtualGraphics dstGraphics = (VirtualGraphics) dst;
        int dstWidth = getGraphicsWidth(dst);
        int dstHeight = getGraphicsHeight(dst);
        int[] dstData = dstGraphics.getSurface().data;

        if ((srcWidth == dstWidth) && (srcHeight == dstHeight)) {
            System.arraycopy(srcData, 0, dstData, 0, srcData.length);
            return true;
        } else {
            return false;
        }

    }

    // private boolean isBigEndian() {
    //
    // int bulkVal = 0xFF000000;
    // ByteBuffer buffer = ByteBuffer.allocateDirect(4);
    // buffer.put((byte) 0xFF);
    // buffer.put((byte) 0x00);
    // buffer.put((byte) 0x00);
    // buffer.put((byte) 0x00);
    // // buffer.putInt(bulkVal);
    // buffer.rewind();
    //
    // IntBuffer intBuffer = buffer.asIntBuffer();
    //
    // int val = intBuffer.get();
    // if (val != bulkVal) {
    // return false;
    // } else {
    // return true;
    // }
    // }

}
