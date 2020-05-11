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
package org.thenesis.microbackend.ui.swt;

import java.io.IOException;

import org.eclipse.swt.widgets.Canvas;
import org.thenesis.microbackend.ui.Configuration;

public class SWTWrapperBackend extends AbstractSWTBackend {

    private Canvas canvas;
    
    public SWTWrapperBackend(Object m) {
        canvas = (Canvas)m;
        display = canvas.getDisplay();
        canvasWidth = canvas.getSize().x;
        canvasHeight = canvas.getSize().y;
    }

    public SWTWrapperBackend() {
    }

    /* UIBackend interface */

    public void configure(Configuration conf, int width, int height) {
        // Do nothing
    }

    public void open() throws IOException {
        configureCanvas(canvas);
    }

    public void close() {
        // Do nothing
    }
    

}
