package org.thenesis.midpath.opengles.ogles;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class NIOHelperSE {
    
    /*
     * @see org.thenesis.midpath.opengles.ogles.Platform#convertToPlatformByteOrder(java.nio.Buffer)
     */
    public static Buffer convertToPlatformByteOrder(Buffer buffer) {
        
        if (buffer instanceof ShortBuffer) {
            ShortBuffer srcBuffer =  ((ShortBuffer)buffer);
            ShortBuffer swapBuffer = srcBuffer;
            if ((srcBuffer.order() == ByteOrder.BIG_ENDIAN)  && !NativeGL10._isPlatformBigEndian()) {
                swapBuffer = ByteBuffer.allocateDirect(srcBuffer.remaining() * 2).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer();
                swapBuffer.put(srcBuffer);
                swapBuffer.rewind();
            } else if ((srcBuffer.order() == ByteOrder.LITTLE_ENDIAN)  && NativeGL10._isPlatformBigEndian()) {
                swapBuffer = ByteBuffer.allocateDirect(srcBuffer.remaining() * 2).order(ByteOrder.BIG_ENDIAN).asShortBuffer();
                swapBuffer.put(srcBuffer);
                swapBuffer.rewind();
            }
            srcBuffer.rewind();
            return swapBuffer;
        } else if (buffer instanceof IntBuffer) {
            IntBuffer srcBuffer =  ((IntBuffer)buffer);
            IntBuffer swapBuffer = srcBuffer;
            if ((srcBuffer.order() == ByteOrder.BIG_ENDIAN)  && !NativeGL10._isPlatformBigEndian()) {
                swapBuffer = ByteBuffer.allocateDirect(srcBuffer.remaining() * 4).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer();
                swapBuffer.put(srcBuffer);
                swapBuffer.rewind();
            } else if ((srcBuffer.order() == ByteOrder.LITTLE_ENDIAN)  && NativeGL10._isPlatformBigEndian()) {
                swapBuffer = ByteBuffer.allocateDirect(srcBuffer.remaining() * 4).order(ByteOrder.BIG_ENDIAN).asIntBuffer();
                swapBuffer.put(srcBuffer);
                swapBuffer.rewind();
            }
            srcBuffer.rewind();
            return swapBuffer;
        } else if (buffer instanceof FloatBuffer) {
            FloatBuffer srcBuffer =  ((FloatBuffer)buffer);
            FloatBuffer swapBuffer = srcBuffer;
            if ((srcBuffer.order() == ByteOrder.BIG_ENDIAN)  && !NativeGL10._isPlatformBigEndian()) {
                swapBuffer = ByteBuffer.allocateDirect(srcBuffer.remaining() * 4).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer();
                swapBuffer.put(srcBuffer);
                swapBuffer.rewind();
            } else if ((srcBuffer.order() == ByteOrder.LITTLE_ENDIAN)  && NativeGL10._isPlatformBigEndian()) {
                swapBuffer = ByteBuffer.allocateDirect(srcBuffer.remaining() * 4).order(ByteOrder.BIG_ENDIAN).asFloatBuffer();
                swapBuffer.put(srcBuffer);
                swapBuffer.rewind();
            }
            srcBuffer.rewind();
            return swapBuffer;
        } else {
            return buffer;
        }
        
        
    }
    
    public static boolean isBufferOrderBigEndian(Buffer buffer) {
        
        if (buffer instanceof ShortBuffer) {
            return (((ShortBuffer)buffer).order() == ByteOrder.BIG_ENDIAN) ? true : false;
        } else if (buffer instanceof IntBuffer) {
            return (((IntBuffer)buffer).order() == ByteOrder.BIG_ENDIAN) ? true : false;
        } else if (buffer instanceof FloatBuffer) {
            return (((FloatBuffer)buffer).order() == ByteOrder.BIG_ENDIAN) ? true : false;
        } else { //if (buffer instanceof ByteBuffer) {
            return (((ByteBuffer)buffer).order() == ByteOrder.BIG_ENDIAN) ? true : false;
        } 
        

    }

}
