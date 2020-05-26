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
package org.thenesis.microbackend.ui.awt;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.MemoryImageSource;
import java.io.IOException;

import org.thenesis.microbackend.ui.BackendEventListener;
import org.thenesis.microbackend.ui.NullBackendEventListener;
import org.thenesis.microbackend.ui.UIBackend;

public abstract class AbstractAWTBackend implements UIBackend {

    Panel panel;
    protected Image screenImage;
    protected MemoryImageSource memorySource;
    protected int[] pixels;

    int canvasWidth;
    int canvasHeight;

    BackendEventListener listener = new NullBackendEventListener();
    AWTEventConverter converter = new AWTEventConverter();

    Panel createPanel() throws IOException {

        final Dimension dimension = new Dimension(canvasWidth, canvasHeight);
        panel = new Panel() {

            public void update(Graphics g) {
                paint(g);
            }

            public Dimension getMinimumSize() {
                return dimension;
            }

            public Dimension getPreferredSize() {
                return dimension;
            }

            public void paint(Graphics g) {
                if (screenImage != null) {
                    g.drawImage(screenImage, 0, 0, null);
                }
            }
        };
        panel.addKeyListener(converter);
        panel.addMouseListener(converter);
        panel.addMouseMotionListener(converter);

        return panel;

    }

    /* Common UIBackend interface methods */

    public void setBackendEventListener(BackendEventListener listener) {
        this.listener = listener;
    }
    
    public int getWidth() {
        return canvasWidth;
    }

    public int getHeight() {
        return canvasHeight;
    }

    public void updateARGBPixels(int[] argbPixels, int x, int y, int width, int height) {

        if (screenImage == null) {
            pixels = new int[canvasWidth * canvasHeight];
            memorySource = new MemoryImageSource(canvasWidth, canvasHeight, pixels, 0, canvasWidth);
            memorySource.setAnimated(true);
            screenImage = Toolkit.getDefaultToolkit().createImage(memorySource);
        }
        
        // Clipping
        if (x < 0) {
            x = 0;
        }
        if  (x >= canvasWidth) {
            return;
        }
        if (y < 0) {
            y = 0;
        }
        if  (y >= canvasHeight) {
            return;
        }
        if (x + width > canvasWidth) {
            width = canvasWidth - x;
        }
        if (y + height > canvasHeight) {
            height = canvasHeight - y;
        }
        
        for (int i = 0; i < height; i++) {
            int offset = (y + i) * canvasWidth + x;
            System.arraycopy(argbPixels, offset, pixels, offset, width);
        }
        memorySource.newPixels(x, y, width, height);

        panel.repaint();
    }

    /* Internals */

    private class AWTEventConverter implements KeyListener, MouseListener, MouseMotionListener, WindowListener {

        public void keyPressed(KeyEvent e) {
            //System.out.println("[DEBUG] AWTBackend.keyPressed(): key code: " + e.getKeyCode() + " char: "+ e.getKeyChar());
            listener.keyPressed(e.getKeyCode(), e.getKeyChar(), e.getModifiers());
        }

        public void keyReleased(KeyEvent e) {
            //System.out.println("[DEBUG] AWTBackend.keyReleased(): key code: " + e.getKeyCode() + " char: " + e.getKeyChar());
            listener.keyReleased(e.getKeyCode(), e.getKeyChar(), e.getModifiers());
        }

        // Not used
        public void keyTyped(KeyEvent e) {
        }

        public void mouseClicked(MouseEvent e) {
        }

        public void mouseEntered(MouseEvent e) {
        }

        public void mouseExited(MouseEvent e) {
        }

        public void mouseDragged(MouseEvent e) {
            listener.pointerMoved(e.getX(), e.getY(), e.getModifiers());
        }

        public void mousePressed(MouseEvent e) {
            //System.out.println("[DEBUG] AWTBackend.mousePressed()");
            listener.pointerPressed(e.getX(), e.getY(), e.getModifiers());
        }

        public void mouseReleased(MouseEvent e) {
            //System.out.println("[DEBUG] AWTBackend.mouseReleased()");
            listener.pointerReleased(e.getX(), e.getY(), e.getModifiers());
        }

        public void mouseMoved(MouseEvent e) {
            //System.out.println("[DEBUG] AWTBackend.mouseMoved()");
            listener.pointerMoved(e.getX(), e.getY(), e.getModifiers());
        }

        public void windowClosing(WindowEvent e) {
            listener.windowClosed();
        }

        // Not used
        public void windowClosed(WindowEvent e) {
        }

        public void windowActivated(WindowEvent arg0) {
        }

        public void windowDeactivated(WindowEvent arg0) {
        }

        public void windowDeiconified(WindowEvent arg0) {
        }

        public void windowIconified(WindowEvent arg0) {
        }

        public void windowOpened(WindowEvent arg0) {
        }

    }

}
