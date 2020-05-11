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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.thenesis.microbackend.ui.BackendEventListener;
import org.thenesis.microbackend.ui.Configuration;
import org.thenesis.microbackend.ui.Logging;
import org.thenesis.microbackend.ui.UIBackend;
import org.thenesis.microbackend.ui.UIBackendFactory;
import org.thenesis.microbackend.ui.graphics.toolkit.pure.PureToolkit;

public abstract class VirtualToolkit {

    protected Configuration backendConfig;
    protected BackendEventListener listener;
    protected UIBackend backend;
    protected VirtualFont defaultFont;
    
    private BaseImageDecoder imageDecoder;

    protected VirtualToolkit() {
    }

    public static VirtualToolkit createToolkit(Configuration backendConfig, BackendEventListener listener) {
        VirtualToolkit toolkit = new PureToolkit();
        toolkit.configure(backendConfig, listener);
        return toolkit;
    }

    protected void configure(Configuration backendConfig, BackendEventListener listener) {
        this.backendConfig = backendConfig;
        this.listener = listener;
    }

    public void initialize(Object m) {

        imageDecoder = new BaseImageDecoder(this);

        backend = UIBackendFactory.createBackend(m, backendConfig, listener);

        int w = backend.getWidth();
        int h = backend.getHeight();

        try {
            backend.open();
        } catch (IOException e) {
            Logging.log("VirtualToolkit: Can't open '" + backend.getClass().getName() + "' backend", Logging.ERROR);
            e.printStackTrace();
        }

        initializeRoot(w, h);
    }

    /**
     * Initializes the root Surface and Graphics
     * 
     * @param rootWidth
     * @param rootHeight
     */
    public abstract void initializeRoot(int rootWidth, int rootHeight);

    public abstract VirtualGraphics getRootGraphics();

    public abstract void flushGraphics(int x, int y, long width, long height);

    public abstract VirtualFont createFont(int face, int style, int size);

    public VirtualFont getDefaultFont() {
        if (defaultFont == null) {
            defaultFont = createFont(VirtualFont.FACE_MONOSPACE, VirtualFont.STYLE_PLAIN, VirtualFont.SIZE_SMALL);
        }
        return defaultFont;
    }

    public int getWidth() {
        return backend.getWidth();
    }

    public int getHeight() {
        return backend.getHeight();
    }

    public UIBackend getBackend() {
        return backend;
    }

    public void close() {
        backend.close();
    }

    public abstract VirtualImage createImage(int w, int h);

    public abstract VirtualImage createRGBImage(int[] rgb, int width, int height, boolean processAlpha);

    public abstract VirtualImage createImage(VirtualImage image);

    public abstract VirtualImage createImage(VirtualImage image, int x, int y, int width, int height, int transform);

    public VirtualImage createImage(InputStream stream) throws IOException {
        VirtualImage surface = imageDecoder.decode(stream);
        return createImage(surface);
    }

    public VirtualImage createImage(byte[] imageData, int imageOffset, int imageLength) {
        try {
            return createImage(new ByteArrayInputStream(imageData, imageOffset, imageLength));
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't create image: " + e.getMessage());
        }
    }

}
