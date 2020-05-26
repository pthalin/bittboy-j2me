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
package org.thenesis.midpath.demo.location;

import javax.microedition.lcdui.Image;
import javax.microedition.location.Landmark;


/**
 * This class adds additional information to a landmark. This information is
 * used to display the landmark on the map.
 *
 * @version 1.3
 */
public class MapLandmark {
    private Landmark landmark;
    private int x;
    private int y;
    private Image image;
    protected boolean active;

    /** Creates a new instance of MapLandmark */
    protected MapLandmark(Landmark landmark, int x, int y, Image image) {
        this.landmark = landmark;
        this.x = x;
        this.y = y;
        this.image = image;
    }

    /**
     * Returns the instance of the Landmark class which this map landmark
     * extends.
     */
    public Landmark getLandmark() {
        return landmark;
    }

    /** Returns the x position of the landmark on the map. */
    public int getX() {
        return x;
    }

    /** Returns the y position of the landmark on the map. */
    public int getY() {
        return y;
    }

    /** Returns the image of the landmark. */
    public Image getImage() {
        return image;
    }

    /** Returns the activation status of the landmark. */
    public boolean isActive() {
        return active;
    }
}
