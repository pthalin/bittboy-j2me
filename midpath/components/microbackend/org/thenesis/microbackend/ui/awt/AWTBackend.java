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

import java.awt.Frame;
import java.awt.Panel;
import java.io.IOException;

import org.thenesis.microbackend.ui.BackendEventListener;
import org.thenesis.microbackend.ui.Configuration;

public class AWTBackend extends AbstractAWTBackend {

    private Frame frame;

    public AWTBackend(int w, int h) {
        canvasWidth = w;
        canvasHeight = h;
    }

    public AWTBackend() {
    }

    /* UIBackend interface */

    public void configure(Configuration conf, int width, int height) {
        canvasWidth = width;
        canvasHeight = height;
    }

    public void setBackendEventListener(BackendEventListener listener) {
        this.listener = listener;
    }

    public void open() throws IOException {
        Panel panel = createPanel();
        
        frame = new Frame();
        frame.setResizable(false);
        frame.addWindowListener(converter);
        frame.add(panel);
        frame.pack();
        frame.setVisible(true);
        
        panel.requestFocus();
    }

    public void close() {
        frame.dispose();
    }

}
