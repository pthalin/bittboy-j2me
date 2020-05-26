package org.thenesis.microbackend.ui.image.jpeg;

import java.io.IOException;
import java.io.InputStream;

public class SimpleJPEGDecoder extends JPEGDecoder {

    private int[] pixels;
    private int width;
    private int height;

    public void decode(InputStream is) throws IOException {
        
        PixelArray pa = new PixelArray() {
            public void setPixel(int x, int y, int argb) {
               pixels[y * width + x] = argb;
            }
            
            public void setSize(int width, int height) throws IOException {
                SimpleJPEGDecoder.this.width = width;
                SimpleJPEGDecoder.this.height = height;
                pixels = new int[width * height];
            }
        };
        
        decode(is, pa);
    }
    
    public int[] getPixels() {
        return pixels;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
}
