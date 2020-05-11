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

final class PixelReader {
	final private BitMover mover;
	final private InputStream str;
	final private int[] leftover = new int[8];
	private int leftamt = 0;

	/* package */final int fillSize;

	PixelReader(PngImage img, InputStream str) throws PngException {
		this.str = str;
		fillSize = Math.max(1, 8 / img.data.header.depth);
		mover = BitMover.getBitMover(img);
	}

	int read(int b[], int off, int len) throws IOException {
		int needed = len;
		int total = len;
		if (leftamt > 0) {
			int fromleft = (needed > leftamt ? leftamt : needed);
			System.arraycopy(leftover, 8 - leftamt, b, off, fromleft);
			needed -= fromleft;
			leftamt -= fromleft;
		}
		if (needed > 0) {
			off = mover.fill(b, str, off, needed / fillSize);
			needed %= fillSize;
			if (needed > 0) {
				leftamt = fillSize - needed;
				mover.fill(leftover, str, 8 - fillSize, 1);
				System.arraycopy(leftover, 8 - fillSize, b, off, needed);
			}
		}
		return total;
	}
}
