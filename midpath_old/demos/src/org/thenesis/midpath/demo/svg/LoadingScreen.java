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

import javax.microedition.m2g.SVGAnimator;
import javax.microedition.m2g.SVGImage;

import org.thenesis.midpath.demo.svg.contactlist.ContactListMidlet;

public class LoadingScreen implements ProgressiveInputStream.Listener {
    /**
     * Interface that a class waiting for the SVGImage resource to be loaded
     * should implement.
     */
    public interface Listener {
        public void svgImageLoaded(SVGImage svgImage);
    }
    
    /**
     * The class which is waiting for the associated SVGImage to complete
     * loading.
     */
    private Listener listener;
    
    /**
     * The SVGAnimator playing the loading animation.
     */
    private SVGAnimator svgAnimator;
    
    /**
     * The SVGImamge containing the loading animation definition.
     */
    private SVGImage svgImage;
    
    /**
     * The path to the SVGImage to load
     */
    private String svgImagePath;
    
    /**
     * The expected length for the SVGImage to load.
     */
    private int svgImageSize;
    
    /**
     * Used to track progress in the loading screen.
     */
    private UpdateProgress updateProgress = new UpdateProgress();    
    
    /**
     * Horizontal Progress Bar. The Progress Bar is used in the initial load screen.
     */
    protected SVGProgressBar loadProgressBar;

    class UpdateProgress implements Runnable {
        float value;
        public void run() {
            loadProgressBar.setProgress(value);
        }
    }
    
    class SVGImageLoadingTask implements Runnable {
        SVGImage loadedSVGImage;
        public void run() {

            try {         
                loadedSVGImage = (javax.microedition.m2g.SVGImage) javax.microedition.m2g.SVGImage.createImage
                        (new ProgressiveInputStream(ContactListMidlet.class.getResourceAsStream(svgImagePath), svgImageSize, LoadingScreen.this), null);
            } catch (java.io.IOException exception) {
                exception.printStackTrace();
            }       
            
            listener.svgImageLoaded(loadedSVGImage);
        }
    }    

    /** 
     * Creates a new instance of SVGImageLoadingScreen 
     * @param svgAnimator the associated SVGAnimator instance
     * @param svgImage the associated SVGImage instance
     * @param svgImagePath the path of the SVG image file to load
     * @param svgImageSize the expected size for the SVGImage to load.
     */
    public LoadingScreen(final SVGAnimator svgAnimator,
            final SVGImage svgImage,
            final String svgImagePath,
            final int svgImageSize,
            final Listener listener) {
        this.svgAnimator = svgAnimator;
        this.svgImage = svgImage;
        this.svgImagePath = svgImagePath;
        this.svgImageSize = svgImageSize;
        this.listener = listener;
        
        loadProgressBar = new SVGProgressBar("loadProgressBar");
        loadProgressBar.hookSkin(svgImage.getDocument());
        
        // Load the ContactList skin here
        Thread th = new Thread(new SVGImageLoadingTask());
        th.start();
    }

    /**
     * Called when more bytes have been read from the input stream.
     *
     * @param p the current penetration in the input stream, in the [0, 1] interval.
     */
    public void streamProgress(final float p) {
        if (p < 0.90f && (p - updateProgress.value) < 0.1f) {
            return;
        }
        
        updateProgress.value = p;
        try {
            svgAnimator.invokeAndWait(updateProgress);
        } catch (InterruptedException ie) {
        } catch (IllegalStateException ise) {
        }
    }

}
