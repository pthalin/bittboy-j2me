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
import org.w3c.dom.svg.SVGLocatableElement;
import org.w3c.dom.svg.SVGMatrix;
import org.w3c.dom.svg.SVGRect;
import org.w3c.dom.svg.SVGSVGElement;

/**
 * The convention for a scroll bar is that the background element has the 'bkg' suffix
 * (e.g., "myScrollBar.bkg") and the thumb element has the suffix 'thumb' 
 * (e.g., "myScrollBar.thumb").
 */
public class SVGHorizontalScrollBar {
    static final String BACKGROUND_SUFFIX = "bkg";
    static final String THUMB_SUFFIX = "thumb";
    
    /**
     * The scroll bar thumb
     */
    protected SVGLocatableElement thumb;
    
    /**
     * The thumb's initial transform.
     */
    protected SVGMatrix thumbTxf;
    
    /**
     * The min position of the thumb along the x-axis
     */
    protected float minTranslate;
    
    /**
     * The max position of the thumb along the x-axis
     */
    protected float maxTranslate;
    
    /**
     * The document's root svg element.
     */
    protected SVGSVGElement svg;
    
    /**
     * The scroll bar's id prefix, i.e., the prefix used for all of the 
     * scrollbar component ids.
     */
    protected String idPrefix;
    
    /**
     * The current scrollbar position.
     */
    protected float pos;
    
    /**
     * @param idPrefix the scrollBar id prefix.
     */
    public SVGHorizontalScrollBar(final String idPrefix) {
        if (idPrefix == null || "".equals(idPrefix)) {
            throw new IllegalArgumentException("SVGHorizontalScrollBar " +
                    "idPrefix should not be null or empty string");
        }
        
        this.idPrefix = idPrefix;
    }
    
    /**
     * Hooks this scroll bar with a new skin.
     *
     * @param doc - the new skin content.
     */
    public void hookSkin(final Document doc) {
        svg = (SVGSVGElement) doc.getDocumentElement();
        SVGLocatableElement bkg = (SVGLocatableElement) doc.getElementById(idPrefix + "." + BACKGROUND_SUFFIX);
        
        if (bkg == null) {
            throw new IllegalArgumentException("SVGHorizontalScrollBar : element with id " + idPrefix +
                    "." + BACKGROUND_SUFFIX + " does not exist in skin");
        }
        
        thumb = (SVGLocatableElement) doc.getElementById(idPrefix + "." + THUMB_SUFFIX);
        if (thumb == null) {
            throw new IllegalArgumentException("SVGHorizontalScrollBar : element with id " + idPrefix +
                    "." + THUMB_SUFFIX + " does not exist in skin");
        }
        
        SVGRect bkgBBox = bkg.getBBox();
        SVGRect thumbBBox = thumb.getBBox();
        
        thumbTxf = thumb.getMatrixTrait("transform");
        
        minTranslate = -thumbBBox.getX() + bkgBBox.getX();
        maxTranslate = -thumbBBox.getX() + bkgBBox.getX() + bkgBBox.getWidth() - thumbBBox.getWidth();
        
        setThumbPosition(pos);
        
    }
    
    /**
     * @param pos the desired thumb position in the [0, 1] interval. If the value is out of range, it
     * is clipped to the valid range.
     */
    public void setThumbPosition(float pos) {
        if (pos < 0) {
            pos = 0;
        } else if (pos > 1) {
            pos = 1;
        }
        
        float xTranslate = minTranslate + pos * (maxTranslate - minTranslate);
        SVGMatrix txf = svg.createSVGMatrixComponents(1, 0, 0, 1, 0, 0);
        txf.mMultiply(thumbTxf);
        txf.mTranslate(xTranslate, 0);
        thumb.setMatrixTrait("transform", txf);
        this.pos = pos;
    }

}

