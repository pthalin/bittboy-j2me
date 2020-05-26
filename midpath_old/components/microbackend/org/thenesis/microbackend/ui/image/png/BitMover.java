/*
 * MIDPath - Copyright (C) 2006-2007 Guillaume Legris, Mathieu Legris
 * 
 * com.sixlegs.image.png - Java package to read and display PNG images
 * Copyright (C) 1998-2004 Chris Nokleberg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt). 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 * 
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package org.thenesis.microbackend.ui.image.png;

import java.io.IOException;
import java.io.InputStream;

abstract class BitMover {
    int trans = -1;
    int transgray = -1;
    int translow = -1;
    int[] gammaTable;

    abstract int fill(int[] pixels, InputStream str, int off, int len) throws IOException;

    static BitMover getBitMover(PngImage img) throws PngException {
        StringBuffer clsname = new StringBuffer("org.thenesis.microbackend.ui.image.png.BitMover");
        clsname.append(img.data.header.depth);
        if (img.data.header.paletteUsed) {
            clsname.append('P');
        } else {
            clsname.append(img.data.header.colorUsed ? "RGB" : "G");
        }
        if (img.data.header.alphaUsed)
            clsname.append('A');
        try {
            BitMover b = (BitMover) Class.forName(clsname.toString()).newInstance();
            b.gammaTable = img.data.gammaTable;
            if (img.data.header.colorType == PngImage.COLOR_TYPE_GRAY || img.data.header.colorType == PngImage.COLOR_TYPE_RGB) {
                Chunk_tRNS trans = (Chunk_tRNS) img.getChunk(Chunk.tRNS);
                if (trans != null) {
                    b.trans = trans.rgb;
                    b.translow = trans.rgb_low;
                    b.transgray = trans.r;
                }
            }
            return b;
        } catch (Exception e) {
            throw new PngException("Error loading " + clsname);
        }
    }
}
