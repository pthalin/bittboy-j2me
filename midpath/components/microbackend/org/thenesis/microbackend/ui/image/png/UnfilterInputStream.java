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

import org.thenesis.microbackend.zip.Inflater;
import org.thenesis.microbackend.zip.InflaterInputStream;

final class UnfilterInputStream extends InputStream {
	final private Chunk_IHDR header;
	final private int rowSize;
	final private int bpp;

	final private InflaterInputStream infstr;
	final private byte[] prev;
	final private byte[] cur;

	private int nextPass;
	private int rowsLeftInPass;
	private int bytesPerRow;
	private int pullSize;
	private int xc, xp, xPtr;

	UnfilterInputStream(PngImage img, InputStream s) {
		header = img.data.header;
		infstr = new InflaterInputStream(s, new Inflater(), PngImage.BUFFER_SIZE);
		bpp = Math.max(1, header.depth * header.samples / 8);
		int maxPassWidth = header.interlacer.getMaxPassWidth();
		int maxBytesPerRow = getByteWidth(maxPassWidth);
		rowSize = maxBytesPerRow + bpp;
		prev = new byte[rowSize];
		cur = new byte[rowSize];
		for (int i = 0; i < rowSize; i++)
			cur[i] = 0;
	}

	private int getByteWidth(int pixels) {
		if (header.samples == 1) {
			int dppb = 16 / header.depth; // == 2 * pixels-per-byte
			int w2 = pixels * 2;
			return (w2 % dppb == 0 ? w2 / dppb : (w2 + dppb - (w2 % dppb)) / dppb);
		} else {
			return pixels * header.samples * header.depth / 8;
		}
	}

	private int readRow() throws IOException {
		if (rowsLeftInPass == 0) {
			while (rowsLeftInPass == 0 || bytesPerRow == 0) {
				if (nextPass >= header.interlacer.numPasses())
					return -1;
				rowsLeftInPass = header.interlacer.getPassHeight(nextPass);
				bytesPerRow = getByteWidth(header.interlacer.getPassWidth(nextPass));
				nextPass++;
			}
			pullSize = bytesPerRow + bpp;
			for (int i = 0; i < pullSize; i++)
				prev[i] = 0;
		}
		rowsLeftInPass--;

		int filterType = infstr.read();
		if (filterType == -1)
			return -1;
		if (filterType > 4 || filterType < 0)
			throw new PngException("Bad filter type: " + filterType);
		int needed = bytesPerRow;
		while (needed > 0) {
			int r = infstr.read(cur, bytesPerRow - needed + bpp, needed);
			if (r == -1)
				return -1;
			needed -= r;
		}

		// TODO: add support for FILTER_TYPE_INTRAPIXEL

		switch (filterType) {
		case 0: // None
			break;
		case 1: // Sub
			for (xc = bpp, xp = 0; xc < rowSize; xc++, xp++) {
				cur[xc] = (byte) (cur[xc] + cur[xp]);
			}
			break;
		case 2: // Up
			for (xc = bpp, xp = 0; xc < rowSize; xc++, xp++) {
				cur[xc] = (byte) (cur[xc] + prev[xc]);
			}
			break;
		case 3: // Average
			for (xc = bpp, xp = 0; xc < rowSize; xc++, xp++) {
				cur[xc] = (byte) (cur[xc] + ((0xFF & cur[xp]) + (0xFF & prev[xc])) / 2);
			}
			break;
		case 4: // Paeth
			for (xc = bpp, xp = 0; xc < rowSize; xc++, xp++) {
				cur[xc] = (byte) (cur[xc] + Paeth(cur[xp], prev[xc], prev[xp]));
			}
			break;
		default:
			throw new PngException("unrecognized filter type " + filterType);
		}

		System.arraycopy(cur, 0, prev, 0, rowSize);
		return 0;
	}

	private int Paeth(byte L, byte u, byte nw) {
		int a = 0xFF & L; //  inline byte->int
		int b = 0xFF & u;
		int c = 0xFF & nw;
		int p = a + b - c;
		int pa = p - a;
		if (pa < 0)
			pa = -pa; // inline Math.abs
		int pb = p - b;
		if (pb < 0)
			pb = -pb;
		int pc = p - c;
		if (pc < 0)
			pc = -pc;
		if (pa <= pb && pa <= pc)
			return a;
		if (pb <= pc)
			return b;
		return c;
	}

	private byte[] _b = new byte[1];

	public int read() throws IOException {
		return read(_b, 0, 1) > 0 ? _b[0] & 0xff : -1;
	}

	public int read(byte[] b, int off, int len) throws IOException {
		int count = 0;
		while (len > 0) {
			if (xPtr == 0) {
				if (readRow() == -1)
					return (count == 0 ? -1 : count);
				xPtr = bpp;
			}
			int L = Math.min(len, pullSize - xPtr);
			System.arraycopy(cur, xPtr, b, off, L);
			count += L;
			xPtr = (xPtr + L) % pullSize;
			off += L;
			len -= L;
		}
		return count;
	}

	public void close() throws IOException {
		infstr.close();
	}
}
