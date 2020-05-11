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

final class Chunk_PLTE extends Chunk {
	private int size;

	/* package */int[] r_raw;
	/* package */int[] g_raw;
	/* package */int[] b_raw;
	/* package */int[] a_raw;

	/* package */byte[] r;
	/* package */byte[] g;
	/* package */byte[] b;
	/* package */byte[] a;

	Chunk_PLTE() {
		super(PLTE);
	}

	protected boolean multipleOK() {
		return false;
	}

	protected boolean beforeIDAT() {
		return true;
	}

	protected void readData() throws IOException {
		img.data.palette = this;
		if (img.getChunk(bKGD) != null) {
			throw new PngException("bKGD chunk must follow PLTE chunk");
		}
		if (!img.data.header.colorUsed) {
			throw new PngExceptionSoft("PLTE chunk found in grayscale image");
		}
		if (length % 3 != 0) {
			throw new PngException("PLTE chunk length indivisible by 3");
		}
		size = length / 3;

		/// look into this
		if (img.data.header.colorType == PngImage.COLOR_TYPE_PALETTE) {
			if (size > (2 << img.data.header.depth)) {
				throw new PngException("Too many palette entries");
			} else if (size > 256) {
				throw new PngExceptionSoft("Too many palette entries");
			}
		}

		r = new byte[size];
		g = new byte[size];
		b = new byte[size];

		int[][] raw = new int[3][size];
		r_raw = raw[0];
		g_raw = raw[1];
		b_raw = raw[2];

		for (int i = 0; i < size; i++) {
			r_raw[i] = in_data.readUnsignedByte();
			g_raw[i] = in_data.readUnsignedByte();
			b_raw[i] = in_data.readUnsignedByte();
		}

		updateProperties(false);
	}

	// TODO: stop duplication of palette data?
	/* package */void updateProperties(boolean alpha) {
		int[][] prop = new int[alpha ? 4 : 3][size];
		System.arraycopy(r_raw, 0, prop[0], 0, size);
		System.arraycopy(g_raw, 0, prop[1], 0, size);
		System.arraycopy(b_raw, 0, prop[2], 0, size);
		if (alpha) {
			System.arraycopy(a_raw, 0, prop[3], 0, size);
		}
		img.data.properties.put("palette", prop);
		img.data.properties.put("palette size", new Integer(size));
	}

	/* package */void calculate() {
		for (int i = 0; i < size; i++) {
			r[i] = (byte) img.data.gammaTable[r_raw[i]];
			g[i] = (byte) img.data.gammaTable[g_raw[i]];
			b[i] = (byte) img.data.gammaTable[b_raw[i]];
		}
		if (img.data.header.paletteUsed) {
			if (a != null) {
				img.data.header.model = new IndexColorModel(img.data.header.cmBits, size, r, g, b, a);
			} else {
				img.data.header.model = new IndexColorModel(img.data.header.cmBits, size, r, g, b);
			}
		}
	}
	
	Chunk copy() {

		Chunk_PLTE chunk = new Chunk_PLTE();
	
		chunk.size = size;
		if(r_raw != null)
			System.arraycopy(r_raw, 0, chunk.r_raw, 0, r_raw.length);
		if(g_raw != null)
			System.arraycopy(g_raw, 0, chunk.g_raw, 0, g_raw.length);
		if(b_raw != null)
			System.arraycopy(b_raw, 0, chunk.b_raw, 0, b_raw.length);
		if(a_raw != null)
			System.arraycopy(a_raw, 0, chunk.a_raw, 0, a_raw.length);
		if(r != null)
			System.arraycopy(r, 0, chunk.r, 0, r.length);
		if(g != null)
			System.arraycopy(g, 0, chunk.g, 0, g.length);
		if(b != null)
			System.arraycopy(b, 0, chunk.b, 0, b.length);
		if(a != null)
			System.arraycopy(a, 0, chunk.a, 0, a.length);
		
		return chunk;
		
	}
}
