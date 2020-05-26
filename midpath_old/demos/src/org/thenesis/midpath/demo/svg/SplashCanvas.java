/*
 *
 * Copyright (c) 2007, Sun Microsystems, Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of Sun Microsystems nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.thenesis.midpath.demo.svg;

import java.io.InputStream;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


/**
 * The <code>SplashCanvas</code> class is used to display an image as soon as
 * a MIDlet is started and, possibly, show information about the coming demo
 * while that demo is loading.
 */
public class SplashCanvas extends Canvas {
    /**
     * The minimal amount of time to wait when a splash screen is displayed.
     */
    public static final long SPLASH_MIN_LENGTH = 2500; // 2.5s

    /**
     * The image this splash screen should show.
     */
    Image image;

    /**
     * The time the last display started.
     */
    private long start;

    /**
     * @param image the image this splash screen should show.
     */
    public SplashCanvas(final Image image) {
        this.image = image;
    }

    /**
     * @param imageURL the url for the splash screen image.
     */
    public SplashCanvas(final String imageURL) {
        boolean error = false;

        if (imageURL == null) {
            error = true;
        } else {
            InputStream splashStream = getClass().getResourceAsStream(imageURL);

            try {
                image = Image.createImage(splashStream);
            } catch (Exception e) {
                e.printStackTrace();
                error = true;
            }
        }

        if (error) {
            // Create simple stub splash screen.
            System.err.println("Creating image : " + getWidth() + " / " + getHeight());
            image = Image.createImage(getWidth(), getHeight());
            System.err.println("Image created");

            Graphics g = image.getGraphics();
            System.err.println("Graphics created");
            g.setColor(255, 255, 255);
            System.err.println("Color set");
            g.fillRect(0, 0, getWidth(), getHeight());
            System.err.println("Background filled");
            g.setColor(0, 0, 0);
            System.err.println("Color set 2");

            Font font = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL);
            System.err.println("Font created : " + font);
            g.setFont(font);
            System.err.println("Font set");
            g.drawString("Splash Screen", getWidth() / 2, getHeight() / 2,
                Graphics.TOP | Graphics.LEFT);
            System.err.println("String drawn");
        }
    }

    public void paint(Graphics g) {
        g.setColor(255, 255, 255);
        g.fillRect(0, 0, getWidth(), getHeight());

        int x = (getWidth() - image.getWidth()) / 2;
        int y = (getHeight() - image.getHeight()) / 2;
        g.drawImage(image, x, y, Graphics.TOP | Graphics.LEFT);
    }

    /**
     * @param display the Display on which this splash screen should paint.
     */
    public void display(final Display display) {
        display.setCurrent(this);
        start = System.currentTimeMillis();
    }

    /**
     * Switches to the input Display after the minimal time has elapsed.
     *
     * @param display the display to switch to.
     * @param canvas the canvas to set after the minimal amount of time has elapsed.
     */
    public void switchTo(final Display display, final Canvas newCanvas) {
        long end = System.currentTimeMillis();
        long waitMore = SPLASH_MIN_LENGTH - (end - start);

        if (waitMore > 0) {
            try {
                Thread.currentThread().sleep(waitMore);
            } catch (InterruptedException ie) {
                // Do nothing.
            }
        }

        display.setCurrent(newCanvas);
    }

    /**
     * Shows the splash screen and waits for SPLASH_MIN_LENGTH before restoring
     * the input Displayable.
     *
     * @param display the display on which to show the splash screen.
     * @param displayable the displayable to restore after the help screen has been shown.
     */
    public void showAndWait(final Display display, final Displayable displayable) {
        // Show the splashCanvas for a little while.
        display.setCurrent(this);

        Thread th =
            new Thread() {
                public void run() {
                    try {
                        Thread.currentThread().sleep(SPLASH_MIN_LENGTH);
                    } catch (InterruptedException ie) {
                    }

                    display.setCurrent(displayable);
                }
            };

        th.start();
    }
}
