package org.thenesis.microbackend.ui.graphics;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import org.thenesis.microbackend.ui.Logging;
import org.thenesis.microbackend.ui.image.jpeg.SimpleJPEGDecoder;
import org.thenesis.microbackend.ui.image.png.ColorModel;
import org.thenesis.microbackend.ui.image.png.ImageConsumer;
import org.thenesis.microbackend.ui.image.png.PngImage;
import org.thenesis.microbackend.util.PushbackInputStream;

public class BaseImageDecoder {

    static {
        PngImage.setProgressiveDisplay(false);
    }

    private VirtualToolkit toolkit;

    public BaseImageDecoder(VirtualToolkit toolkit) {
        this.toolkit = toolkit;
    }

    public VirtualImage decode(InputStream is) throws IOException {

        PushbackInputStream pis = new PushbackInputStream(is, 2);

        // Read header
        int b1 = pis.read();
        int b2 = pis.read();
        pis.unread(b2);
        pis.unread(b1);

        b1 = b1 & 0xff;
        b2 = b2 & 0xff;

        if (b1 == 0x89 && b2 == 0x50) {
            return decodePNG(pis);
        } else if (b1 == 0xff && b2 == 0xd8) {
            return decodeJPEG(pis);
        } else {
            throw new IOException("Unknown image format");
        }

    }

    protected VirtualImage decodeJPEG(InputStream is) throws IOException {
        SimpleJPEGDecoder decoder = new SimpleJPEGDecoder();
        decoder.decode(is);
        return toolkit.createRGBImage(decoder.getPixels(), decoder.getWidth(), decoder.getHeight(), true);
    }

    protected VirtualImage decodePNG(InputStream is) throws IOException {
        // Read PNG image from file
        PngImage png = new PngImage(is);

        final int pngWidth = png.getWidth();
        int pngHeight = png.getHeight();
        final int[] pngData = new int[pngWidth * pngHeight];
        png.setBuffer(pngData);

        png.startProduction(new ImageConsumer() {
            private ColorModel cm;

            public void imageComplete(int status) {
                for (int i = 0; i < pngData.length; i++) {
                    pngData[i] = cm.getRGB(pngData[i]);
                }
            }

            public void setColorModel(ColorModel model) {
                cm = model;
            }

            public void setDimensions(int width, int height) {
            }

            public void setHints(int flags) {
            }

            public void setProperties(Hashtable props) {
            }

            public void setPixels(int x, int y, int w, int h, ColorModel model, byte[] pixels, int offset, int scansize) {
            }

            public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int offset, int scansize) {
            }
        });

        if (Logging.TRACE_ENABLED) {
            System.out.println("[DEBUG] BaseImageDecoder.<init>(InputStream stream): errors while loading ? " + png.hasErrors());
        }

        return toolkit.createRGBImage(pngData, pngWidth, pngHeight, true);
    }

}
