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
package org.thenesis.microbackend.ui;

import java.io.IOException;

public class NullBackend implements UIBackend {
    
    private int width = 100;
    private int height = 100;

    public NullBackend() {
        // Do nothing
    }

    public void close() {
        // Do nothing
    }

    public void configure(Configuration conf, int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void initialize(Configuration conf, Object container) {
        //   Do nothing       
    }

    public void open() throws IOException {
        //	Do nothing
    }

    public void setBackendEventListener(BackendEventListener listener) {
        //	Do nothing
    }

    public void updateARGBPixels(int[] argbPixels, int x, int y, int widht, int heigth) {
        //	Do nothing
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }

   

}
