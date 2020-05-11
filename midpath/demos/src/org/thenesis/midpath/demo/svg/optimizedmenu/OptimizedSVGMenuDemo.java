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
package org.thenesis.midpath.demo.svg.optimizedmenu;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.m2g.SVGImage;
import javax.microedition.m2g.ScalableGraphics;
import javax.microedition.midlet.MIDlet;

import org.thenesis.midpath.demo.svg.SplashCanvas;
import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGLocatableElement;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;


/**
 * The optimized SVG menu demonstrates how the SVG graphics engine can be used
 * to pre-rasterize menu content. The rasterized images can then be shown as
 * frames in animations. The trade-off is to reduce computer power while using a
 * little more space to hold the rasterized content.
 */
public class OptimizedSVGMenuDemo extends MIDlet implements CommandListener {
    private final Command exitCommand = new Command("Exit", Command.EXIT, 1);
    MenuCanvas svgCanvas = null;

    public OptimizedSVGMenuDemo() {
    }

    public void startApp() {
        System.gc();

        String svgImageFile = "optimizedSVGMenuG.svg";
        String splashImageFile = "SplashScreen.png";

        if (svgCanvas == null) {
            SplashCanvas splashCanvas = new SplashCanvas(splashImageFile);
            splashCanvas.display(Display.getDisplay(this));

            InputStream svgDemoStream = getClass().getResourceAsStream(svgImageFile);

            if (svgDemoStream == null) {
                throw new Error("Could not load " + svgImageFile);
            }

            try {
                System.out.print("Loading SVGImage .... ");

                SVGImage svgImage = (SVGImage)SVGImage.createImage(svgDemoStream, null);
                System.out.println(" ... Done");

                svgCanvas = new MenuCanvas(svgImage, 3, 3, 0.1f, 8, 0.0625f);
                svgCanvas.addCommand(exitCommand);
                svgCanvas.setCommandListener(this);
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Could not load " + svgImageFile);
            }

            splashCanvas.switchTo(Display.getDisplay(this), svgCanvas);
        }

        Display.getDisplay(this).setCurrent(svgCanvas);
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
    }

    public void commandAction(Command c, Displayable d) {
        if (c == exitCommand) {
            destroyApp(false);
            notifyDestroyed();
        }
    }

    /**
     * The MenuCanvas class loads the icons found in the SVG image given
     * at construction time and turns each icon into a bitmap.
     *
     */
    class MenuCanvas extends Canvas {
        /**
         * The SVGImage painted by the canvas.
         */
        protected SVGImage svgImage;

        /**
         * The ScalableGraphics used to paint into the midp
         * Graphics instance.
         */
        protected ScalableGraphics sg = ScalableGraphics.createInstance();

        /**
         * The number of icons, vertically.
         */
        protected int numRows;

        /**
         * The number of icons, horizontally.
         */
        protected int numCols;

        /**
         * The size of a single icon.
         */
        protected int iconWidth;

        /**
         * The size of a single icon.
         */
        protected int iconHeight;

        /**
         * Number of frames in focus selection.
         */
        protected int numFramesFocus;

        /**
         * Frame length.
         */
        protected float frameLength;

        /**
         * The menu raster images.
         */
        protected Image[][][] menuIcons;

        /**
         * The index of the current frame for each icon
         */
        protected int[][] currentFrame;

        /**
         * The row/col index of the currently-focused icon.
         */
        protected int focusRow;

        /**
         * The row/col index of the currently-focused icon.
         */
        protected int focusCol;

        /**
         * The padding ratio.
         */
        protected float padding;

        /**
         * @param svgImage the SVGImage this canvas should paint.
         * @param numRows the number of rows of icons.
         * @param numCols the number of columns of icons.
         * @param padding the margin around each icons, as a percentage of the
         *     icon's bounding box.
         * @param numFramesFocus the number of frames to sample in order to get
         *     from the unselected frame to the focused state.
         * @param frameLength the amount of time between frames.
         */
        protected MenuCanvas(final SVGImage svgImage, final int numRows, final int numCols,
            final float padding, final int numFramesFocus, final float frameLength) {
            if ((svgImage == null) || (numRows <= 0) || (numCols <= 0) || (padding < 0) ||
                    (numFramesFocus < 1) || (frameLength <= 0)) {
                throw new IllegalArgumentException();
            }

            this.svgImage = svgImage;
            this.numRows = numRows;
            this.numCols = numCols;
            this.numFramesFocus = numFramesFocus;
            this.frameLength = frameLength;
            this.padding = padding;

            // The input svgImage should have numRows * numCols icons under the 
            // root svg element.
            final int numIcons = numRows * numCols;
            Document doc = svgImage.getDocument();
            SVGSVGElement svg = (SVGSVGElement)doc.getDocumentElement();

            // Load all the icons in a Vector for future manipulation
            Vector iconVector = new Vector();

            for (int i = 0; i < numIcons; i++) {
                SVGElement iconElt = (SVGElement)doc.getElementById("icon_" + i);

                if (iconElt == null) {
                    throw new IllegalArgumentException("The SVG Image does not have " + numIcons +
                        " icons under the root svg element" + " icon_" + i +
                        " does not exist in the document");
                }

                if (!(iconElt instanceof SVGLocatableElement)) {
                    throw new IllegalArgumentException("The " + (i + 1) + "th icon under the " +
                        "root svg element is not a <g>");
                }

                // Hide all icons initially
                iconElt.setTrait("display", "none");

                iconVector.addElement(iconElt);
            }

            // Now, compute the size allocated to each icon.
            int width = getWidth();
            int height = getHeight();

            iconWidth = width / numCols;
            iconHeight = height / numRows;

            // Render each icon in a bitmap.
            svgImage.setViewportWidth(iconWidth);
            svgImage.setViewportHeight(iconHeight);

            final int numFrames = 1 + numFramesFocus;
            menuIcons = new Image[numRows][numCols][numFrames];
            currentFrame = new int[numRows][numCols];

            // calculate viewBox for each icon

            // svg -> screen
            SVGMatrix svgCTM = svg.getScreenCTM();

            // screen -> svg
            SVGMatrix svgICTM = svgCTM.inverse();

            SVGRect[] iconViewBox = new SVGRect[numIcons];

            for (int i = 0; i < numIcons; ++i) {
                SVGLocatableElement icon = (SVGLocatableElement)iconVector.elementAt(i);

                // Get the user space bounding box for the icon
                SVGRect bbox = icon.getBBox();
                if (bbox == null) {
                    // If someone tampered with the svg menu file, the bbox 
                    // could be null
                    iconViewBox[i] = null;
                    continue;
                }
                

                // icon -> svg -> screen
                SVGMatrix iconCTM = icon.getScreenCTM();

                // icon -> svg
                SVGMatrix iconToSvg =
                    svg.createSVGMatrixComponents(svgICTM.getComponent(0), svgICTM.getComponent(1),
                        svgICTM.getComponent(2), svgICTM.getComponent(3), svgICTM.getComponent(4),
                        svgICTM.getComponent(5));
                iconToSvg.mMultiply(iconCTM);

                // get the icon bounding box in svg coordinates
                float x0 = bbox.getX();
                float y0 = bbox.getY();
                float x1 = x0 + bbox.getWidth();
                float y1 = y0 + bbox.getHeight();
                float[] pointsX = { x0, x0, x1, x1 };
                float[] pointsY = { y0, y1, y0, y1 };
                float minX = Float.MAX_VALUE;
                float minY = Float.MAX_VALUE;
                float maxX = -Float.MAX_VALUE;
                float maxY = -Float.MAX_VALUE;
                float a = iconToSvg.getComponent(0);
                float b = iconToSvg.getComponent(1);
                float c = iconToSvg.getComponent(2);
                float d = iconToSvg.getComponent(3);
                float e = iconToSvg.getComponent(4);
                float f = iconToSvg.getComponent(5);

                for (int j = 0; j < pointsX.length; ++j) {
                    float nx = (a * pointsX[j]) + (c * pointsY[j]) + e;
                    float ny = (b * pointsX[j]) + (d * pointsY[j]) + f;

                    if (nx < minX) {
                        minX = nx;
                    }

                    if (nx > maxX) {
                        maxX = nx;
                    }

                    if (ny < minY) {
                        minY = ny;
                    }

                    if (ny > maxY) {
                        maxY = ny;
                    }
                }

                bbox.setX(minX);
                bbox.setY(minY);
                bbox.setWidth(maxX - minX);
                bbox.setHeight(maxY - minY);

                iconViewBox[i] = pad(bbox);
            }

            // do the rendering
            int i = 0;

            for (int ri = 0; ri < numRows; ri++) {
                for (int ci = 0; ci < numCols; ci++, i++) {
                    // Get the icon we want to draw
                    SVGLocatableElement icon = (SVGLocatableElement)iconVector.elementAt(i);

                    // Now, set the icon's display to 'inline' before drawing
                    // it to the offscreen.
                    icon.setTrait("display", "inline");

                    // "zoom" the icon
                    if (iconViewBox[i] != null) {
                        svg.setRectTrait("viewBox", iconViewBox[i]);
                    }

                    // Create a bitmap to draw into
                    svg.setCurrentTime(0);

                    for (int fi = 0; fi < numFrames; fi++) {
                        menuIcons[ri][ci][fi] = Image.createImage(iconWidth, iconHeight);

                        // Get a Graphics instance that we can draw into
                        Graphics g = menuIcons[ri][ci][fi].getGraphics();
                        g.setColor(255, 0, 0);
                        g.fillRect(0, 0, iconWidth, iconHeight);
                        sg.bindTarget(g);
                        sg.render(0, 0, svgImage);
                        sg.releaseTarget();

                        svgImage.incrementTime(frameLength);
                    }

                    icon.setTrait("display", "none");
                }
            }

            // The following thread handles animating the currently focused item.
            final long frameLengthMs = (long)(frameLength * 1000);
            Thread th =
                new Thread() {
                    public void run() {
                        long start = 0;
                        long end = 0;
                        long sleep = 0;
                        boolean interrupted = false;

                        while (!interrupted) {
                            start = System.currentTimeMillis();

                            int cr = focusRow;
                            int cc = focusCol;
                            boolean needUpdate = false;

                            for (int ri = 0; ri < numRows; ri++) {
                                for (int ci = 0; ci < numCols; ci++) {
                                    // Process icon (ri, ci)

                                    // Frames are:
                                    // [0] : unselected
                                    // [1, numFramesFocusIn -1] : focusIn anim
                                    // [numFramesFocus] : focused
                                    int curFrame = currentFrame[ri][ci];

                                    if ((cr == ri) && (cc == ci)) {
                                        // We are processing the focused icon.
                                        // If we are below the focused frame, just increase the frame index
                                        if (curFrame < numFramesFocus) {
                                            // Move towards focused state on the focusIn animation
                                            curFrame += 1;
                                            needUpdate = true;
                                        } else {
                                            // Do nothing, we are in the right frame already.
                                        }
                                    } else {
                                        // We are _not_ on the focused frame.
                                        if (curFrame > 0) {
                                            curFrame -= 1;
                                            needUpdate = true;
                                        }
                                    }

                                    currentFrame[ri][ci] = curFrame;
                                }
                            }

                            if (needUpdate) {
                                repaint();
                                serviceRepaints();
                            }

                            end = System.currentTimeMillis();
                            sleep = frameLengthMs - (end - start);

                            if (sleep < 10) {
                                sleep = 10;
                            }

                            try {
                                sleep(sleep);
                            } catch (InterruptedException ie) {
                                interrupted = true;
                            }
                        }
                    }
                };

            th.start();
        }

        /**
         * Helper method. Pads the input bounding box.
         *
         * @param bbox the box to pad.
         */
        SVGRect pad(final SVGRect bbox) {
            float hPad = bbox.getWidth() * padding;
            float vPad = bbox.getHeight() * padding;
            bbox.setX(bbox.getX() - hPad);
            bbox.setY(bbox.getY() - vPad);
            bbox.setWidth(bbox.getWidth() + (2 * hPad));
            bbox.setHeight(bbox.getHeight() + (2 * vPad));

            return bbox;
        }

        public void keyPressed(int keyCode) {
            int r = focusRow;
            int c = focusCol;

            switch (getGameAction(keyCode)) {
            case LEFT:
                c--;

                if (c < 0) {
                    c = numCols - 1;
                }

                break;

            case RIGHT:
                c++;

                if (c == numCols) {
                    c = 0;
                }

                break;

            case UP:
                r--;

                if (r < 0) {
                    r = numRows - 1;
                }

                break;

            case DOWN:
                r++;

                if (r == numRows) {
                    r = 0;
                }

                break;

            default:

                // do nothing
                break;
            }

            focusRow = r;
            focusCol = c;
        }

        public void paint(Graphics g) {
            int fi = 0;

            for (int ri = 0; ri < numRows; ri++) {
                for (int ci = 0; ci < numCols; ci++) {
                    fi = currentFrame[ri][ci];
                    g.drawImage(menuIcons[ri][ci][fi], ci * iconWidth, ri * iconHeight,
                        Graphics.TOP | Graphics.LEFT);
                }
            }
        }
    }
}
