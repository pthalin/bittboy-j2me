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

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.thenesis.microbackend.ui.Configuration;

public class SWTBackend extends AbstractSWTBackend implements Runnable {

    private Shell shell;
    private Thread swtThread;

    public SWTBackend(int w, int h) {
        canvasWidth = w;
        canvasHeight = h;
    }

    public SWTBackend() {
    }

    /* UIBackend interface */

    public void configure(Configuration conf, int width, int height) {
        canvasWidth = width;
        canvasHeight = height;
    }

    public void open() throws IOException {
        start();
    }

    public void close() {
        stop();
    }

    /*
     * SWT thread
     */

    private void start() {

        swtThread = new Thread(this);
        swtThread.start();

        // Wait until SWT initialization is done in the SWT thread
        synchronized (this) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void stop() {
        if (!shell.isDisposed()) {
            display.syncExec(new Runnable() {
                public void run() {
                    shell.dispose();
                }
            });
        }
    }

    public void run() {

        int w = canvasWidth;
        int h = canvasHeight;

        display = new Display();
        shell = new Shell(display);
        shell.setText("");

        Canvas canvas = new Canvas(shell, SWT.NO_BACKGROUND);
        canvas.setSize(w, h);
        configureCanvas(canvas);
        canvas.forceFocus();
        shell.pack();
        shell.open();

        // Notify the main thread that SWT initialization is done
        synchronized (this) {
            notify();
        }

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }
        display.dispose();

    }

}
