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

/**
 * Simple helper class to bind an SVG <code>&lt;text&gt;</code> element with a 
 * data source.
 */
public class SVGTextBinding {
    /**
     * The text binding's default value.
     */
    public static final String DEFAULT_TEXT_VALUE = "---";
    
    /**
     * The id of the bound <text> element.
     */
    protected String id;
    
    /**
     * A reference to the bound <text> element instance in the 
     * current skin.
     */
    protected SVGElement text;
    
    /**
     * @param id the &lt;text&gt; element in which the "#text" trait should 
     * be set.
     */
    public SVGTextBinding(final String id) {
        if (id == null || "".equals(id)) {
            throw new IllegalArgumentException();
        }
        
        this.id = id;
    }
    
    /**
     * @param doc - the <code>Document</code> skin where an element with this
     * binding's id should be found.
     */
    public void hookSkin(final Document doc) {
        text = (SVGElement) doc.getElementById(id);
        
        if (text == null) {
            throw new IllegalArgumentException("SVGTextBinding: Element with id " + id +
                    " does not exist in skin");
        }
        
        // Initially set the text to the default value.
        set(DEFAULT_TEXT_VALUE);        
    }

    /**
     * Sets the binding's value.
     * @param value the new value to display in the text element.
     */
    public void set(String value) {
        if (value == null) {
            value = "null";
        }
        text.setTrait("#text", value);
    }
}
