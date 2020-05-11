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

import org.w3c.dom.Document;
import org.w3c.dom.svg.SVGElement;
import org.w3c.dom.svg.SVGLocatableElement;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;


/**
 * The SVGProgressBar class is a primitive component which can be attached to 
 * SVG markup which follows the expected structure for a progress bar. 
 *
 * The expected structure for the progress bar is that there is a background
 * element which defines the maximum size for the progress indicator and there
 * is a progress indicator which should be scaled, along the horizontal axis,
 * to reflect the progress value. The code also expects a text indicator to display
 * the value as <progress>%.
 *
 * The code assumes that the progressBar background and the progress indicator
 * have the same x=axis origin.
 */
public class SVGProgressBar {
    static final String PROGRESS_SUFFIX = "progress";
    static final String BACKGROUND_SUFFIX = "bkg";
    static final String TEXT_SUFFIX = "text";
    
    /**
     * The progress bar progress indicator
     */
    protected SVGLocatableElement progress;
    
    /**
     * The progress's initial transform.
     */
    protected SVGMatrix progressTxf;
    
    /**
     * The progress indicator's bounding box.
     */
    protected SVGRect progressBBox;
    
    /**
     * The min scale of the progress bar along the x-axis
     */
    protected float minScale;
    
    /**
     * The max scale for the progress bar along the y-axis
     */
    protected float maxScale;
    
    /**
     * The document's root svg element.
     */
    protected SVGSVGElement svg;
    
    /**
     * The progress bar's text display.
     */
    protected SVGElement text;
    
    /**
     * The prefix used for all the elements in the scroll bar component.
     */
    protected String idPrefix;
    
    /**
     * The current progress bar position in the [0,1] range.
     */
    protected float pos;
    
    /**
     * @param idPrefix - the progress bar group identifier.
     */
    public SVGProgressBar(final String idPrefix) {
        if (idPrefix == null || "".equals(idPrefix)) {
            throw new IllegalArgumentException("SVGProgressBar id prefix "
                    + "should not be null or empty");
        }
        
        this.idPrefix = idPrefix;
    }
    
    /**
     * Hooks the input skin to this user interface component.
     *
     * @param doc - the new Document skin containing the associated progress
     * bar elements.
     */
    public void hookSkin(final Document doc) {
        svg = (SVGSVGElement) doc.getDocumentElement();
        SVGLocatableElement bkg = (SVGLocatableElement) doc.getElementById(idPrefix + "." + BACKGROUND_SUFFIX);
        
        if (bkg == null) {
            throw new IllegalArgumentException("SVGProgressBar : element with id " + idPrefix + 
                    "." + BACKGROUND_SUFFIX + " does not exist in skin");
        }
        
        progress = (SVGLocatableElement) doc.getElementById(idPrefix + "." + PROGRESS_SUFFIX);
        
        if (progress == null) {
            throw new IllegalArgumentException("SVGProgressBar : element with id " + idPrefix + 
                    "." + PROGRESS_SUFFIX + " does not exist is skin");
        }
        text = (SVGElement) doc.getElementById(idPrefix + "." + TEXT_SUFFIX);
        
        SVGRect bkgBBox = bkg.getBBox();
        progressBBox = progress.getBBox();
        
        progressTxf = progress.getMatrixTrait("transform");
        
        // Min scale is for 1%. Max scale is for 100%. Below 1%, the progress indicator is hidden.   
        float bkgBBoxWidth = bkgBBox.getWidth();
        float progressBBoxWidth = progressBBox.getWidth();
        if (bkgBBoxWidth <= 0) {
            throw new IllegalArgumentException("The progress bar with id prefix " 
                    + idPrefix + " has an invalid zero-width background");
        }
        if (progressBBoxWidth <= 0) {
            throw new IllegalArgumentException("The progress bar with id prefix " 
                    + idPrefix + " has an invalid zero-width progress indicator");
        }
        
        float defaultPos = progressBBoxWidth / bkgBBoxWidth;
        
        // For pos = 1, we need the progressBBox width to be the same width as the background.
        maxScale = 1 / defaultPos;
        
        // For pos = 0.01, we need the progressBBox width to be 1% of the background's width.
        minScale = 0.01f * maxScale;
        
        setProgress(pos);        
    }
    
    /**
     * @param pos the desired thumb position in the [0, 1] interval. If the value is out of range, it
     * is clipped to the valid range.
     */
    public void setProgress(float pos) {
        if (pos < 0) {
            pos = 0;
        } else if (pos > 1) {
            pos = 1;
        }
        
        float xScale = minScale + pos * (maxScale - minScale);
        SVGMatrix scale = svg.createSVGMatrixComponents(xScale, 0, 0, 1, 0, 0);
        SVGMatrix txf = svg.createSVGMatrixComponents(1, 0, 0, 1, 0, 0);
        txf.mMultiply(progressTxf);
        txf.mTranslate(progressBBox.getX(), progressBBox.getY());
        txf.mMultiply(scale);
        txf.mTranslate(-progressBBox.getX(), -progressBBox.getY());
        
        progress.setMatrixTrait("transform", txf);
        
        text.setTrait("#text", "" + (int) Math.ceil(pos * 100) + "%");
        this.pos = pos;
    }
}
