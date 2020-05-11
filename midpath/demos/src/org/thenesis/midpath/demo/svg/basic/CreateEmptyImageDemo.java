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

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.m2g.SVGImage;
import javax.microedition.midlet.MIDlet;

import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGRGBColor;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;


/**
 * Simple demo which creates an empty SVGImage, populates it with
 * a graphical content and then displays that content.
 */
public class CreateEmptyImageDemo extends MIDlet implements CommandListener {
    public static final String SVG_NAMESPACE_URI = "http://www.w3.org/2000/svg";
    private final Command exitCommand = new Command("Exit", Command.EXIT, 1);
    SVGImageCanvas svgCanvas = null;

    public CreateEmptyImageDemo() {
    }

    public void startApp() {
        if (svgCanvas == null) {
            System.err.print("Building SVGImage .... ");

            SVGImage svgImage = SVGImage.createEmptyImage(null);
            Document doc = svgImage.getDocument();
            SVGSVGElement svg = (SVGSVGElement)doc.getDocumentElement();

            // First, set the viewBox so that we can work in 
            // a 100 by 100 area.
            SVGRect vb = svg.createSVGRect();
            vb.setWidth(100);
            vb.setHeight(100);
            svg.setRectTrait("viewBox", vb);

            SVGElement r = (SVGElement)doc.createElementNS(SVG_NAMESPACE_URI, "rect");
            SVGRGBColor bkgColor = svg.createSVGRGBColor(99, 128, 147);
            r.setRGBColorTrait("fill", bkgColor);
            r.setFloatTrait("x", 25);
            r.setFloatTrait("y", 25);
            r.setFloatTrait("rx", 5);
            r.setFloatTrait("ry", 5);
            r.setFloatTrait("width", 50);
            r.setFloatTrait("height", 50);
            svg.appendChild(r);

            SVGElement c = (SVGElement)doc.createElementNS(SVG_NAMESPACE_URI, "circle");
            SVGRGBColor fgColor = svg.createSVGRGBColor(255, 255, 255);
            c.setRGBColorTrait("fill", fgColor);
            c.setFloatTrait("cx", 50);
            c.setFloatTrait("cy", 50);
            c.setFloatTrait("r", 20);
            svg.appendChild(c);

            System.err.println(" ... Done");

            svgCanvas = new SVGImageCanvas(svgImage);
            svgCanvas.addCommand(exitCommand);
            svgCanvas.setCommandListener(this);
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
