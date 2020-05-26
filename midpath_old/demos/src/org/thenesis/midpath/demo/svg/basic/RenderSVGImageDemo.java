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
package org.thenesis.midpath.demo.svg.basic;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.m2g.SVGImage;
import javax.microedition.midlet.MIDlet;



/**
 * Simple demo which creates an empty SVGImage, populates it with
 * a graphical content and then displays that content.
 */
public class RenderSVGImageDemo extends MIDlet implements CommandListener {
    public static final String SVG_IMAGE = "thumbsUp.svg";
    private final Command exitCommand = new Command("Exit", Command.EXIT, 1);
    SVGImageCanvas svgCanvas = null;

    public RenderSVGImageDemo() {
    }

    public void startApp() {
        if (svgCanvas == null) {
            InputStream svgDemoStream = getClass().getResourceAsStream(SVG_IMAGE);

            if (svgDemoStream == null) {
                throw new Error("Could not load " + SVG_IMAGE);
            }

            try {
                System.err.print("Loading SVGImage .... ");

                SVGImage svgImage = (SVGImage)SVGImage.createImage(svgDemoStream, null);
                System.err.println(" ... Done");

                svgCanvas = new SVGImageCanvas(svgImage);
                svgCanvas.addCommand(exitCommand);
                svgCanvas.setCommandListener(this);
            } catch (IOException e) {
                e.printStackTrace();
                throw new Error("Could not load " + SVG_IMAGE);
            }
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
}
