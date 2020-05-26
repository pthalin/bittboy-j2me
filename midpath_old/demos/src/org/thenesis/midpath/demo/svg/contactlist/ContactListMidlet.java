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
package org.thenesis.midpath.demo.svg.contactlist;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.m2g.SVGAnimator;
import javax.microedition.m2g.SVGImage;
import javax.microedition.midlet.MIDlet;

import org.thenesis.midpath.demo.svg.DefaultSVGAnimator;
import org.thenesis.midpath.demo.svg.LoadingScreen;

public class ContactListMidlet extends MIDlet 
                               implements LoadingScreen.Listener {
    /**
     * List of skins
     */
    private static final String[] SKIN_DIRS = { "skin1", "skin2" };
    
    /**
     * List of skin file sizes
     */
    private static final int[] SKINS_SIZES = { 38000, 38000 };
    
    /**
     * This contact list's skin index.
     */
    private final int skinIndex;

    private final Display display;

    private ContactListScreen contactListScreen;

    private boolean loading;

    /** Creates a new instance of ContactListMidlet */
    protected ContactListMidlet(int skinIndex) {
        this.skinIndex = skinIndex;
        this.display = Display.getDisplay(this);
    }

    synchronized public void startApp() {
        if ((contactListScreen == null) && (!loading)) {
            InputStream imageStream = 
                    ContactListMidlet.class.getResourceAsStream(
                        SKIN_DIRS[skinIndex] + "/loadScreen.svg");
            if (imageStream == null) {
                destroyApp(false);
                notifyDestroyed();
                return;
            }
            SVGImage loadingImage;
            try {
                try {
                    loadingImage = 
                            (SVGImage)SVGImage.createImage(imageStream, null);
                } finally {
                    try {
                        imageStream.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
            } catch (IOException e) {
                destroyApp(false);
                notifyDestroyed();
                return;
            }

            SVGAnimator loadingAnimator = 
                    DefaultSVGAnimator.createAnimator(loadingImage);
            Canvas loadingCanvas = (Canvas)loadingAnimator.getTargetComponent();
            display.setCurrent(loadingCanvas);

            // start the loading of the contact list svg file             
            new LoadingScreen(loadingAnimator, loadingImage, 
                    SKIN_DIRS[skinIndex] + "/list.svg", SKINS_SIZES[skinIndex], 
                    this);
            
            loading = true;
        }
    }

    synchronized public void svgImageLoaded(SVGImage svgImage) {
        SVGAnimator contactListAnimator = 
                DefaultSVGAnimator.createAnimator(svgImage);
        ContactListSource contactListSource = new ContactListSource();
        Canvas contactListCanvas = 
                (Canvas)contactListAnimator.getTargetComponent();
        display.setCurrent(contactListCanvas);

        contactListScreen = new ContactListScreen(contactListAnimator, svgImage, 
                                                  contactListSource);        
        
        loading = false;    
    }
    
    public void pauseApp() {
    }
    
    public void destroyApp(boolean unconditional) {
    }
}




