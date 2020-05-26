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

final class IDATInputStream extends InputStream {
	private static final int[] signature = { 137, 80, 78, 71, 13, 10, 26, 10 };

	private InputStream in_raw;
	private CRCInputStream in_crc;
	private ExDataInputStream in_data;

	private PngImage img;
	private Chunk cur;
	private int chunk_left;
	private boolean close;

	public IDATInputStream(PngImage img, InputStream in_raw, boolean close) {
		this.img = img;
		this.in_raw = in_raw;
		this.close = close;
		in_crc = new CRCInputStream(in_raw);
		in_data = new ExDataInputStream(in_crc);
	}

	// TODO: worry about non-consecutive IDAT chunks

	/* package */void readToData() throws IOException {
		if (cur != null)
			return;
		for (int i = 0; i < 8; i++)
			if (in_data.readUnsignedByte() != signature[i]) {
				throw new PngException("Improper signature");
			}
		try {
			if (getNextChunk().type != Chunk.IHDR) {
				throw new PngException("IHDR chunk must be first chunk");
			}
			while (getNextChunk().type != Chunk.IDAT)
				;
			if (img.data.palette == null) {
				if (img.data.header.paletteUsed) {
					throw new PngException("Required PLTE chunk not found");
				}
			}
			img.fillGammaTable();
		} catch (NullPointerException e) {
			e.printStackTrace();
			throw new PngException("Can't find data chunk");
		}
	}

	/* package */int count() {
		return in_crc.count();
	}

	private void readChunk(Chunk chunk) throws IOException {
		try {
			if (!chunk.multipleOK() && img.getChunk(chunk.type) != null) {
				String msg = "Multiple " + Chunk.typeToString(chunk.type) + " chunks are not allowed";
				if (chunk.isAncillary()) {
					throw new PngExceptionSoft(msg);
				} else {
					throw new PngException(msg);
				}
			}
			chunk.readData();
			img.putChunk(chunk.type, chunk);
		} catch (PngExceptionSoft e) {
			if (PngImage.allFatal)
				throw e;
			img.addError(e);
		} finally {
			in_data.skipBytes(chunk.bytesRemaining());
			long crc_is = in_crc.getValue();
			long crc_should_be = in_data.readUnsignedInt();
			if (crc_is != crc_should_be) {
				throw new PngException("Bad CRC value for " + Chunk.typeToString(chunk.type) + " chunk");
			}
		}
	}

	private Chunk getNextChunk() throws IOException {
		if (cur != null) {
			readChunk(cur);
			if (cur.type == Chunk.IEND) {
				return null;
			}
		}

		chunk_left = in_data.readInt();

		in_crc.reset();
		int type = in_data.readInt();

		if (chunk_left < 0) {
			throw new PngException("Bad " + Chunk.typeToString(type) + " chunk length: " + in_data.unsign(chunk_left));
		}

		cur = PngImage.getRegisteredChunk(type);
		cur.img = img;
		cur.length = chunk_left;
		cur.in_data = in_data;

		if (cur.isUnknown()) {
			String type_string = Chunk.typeToString(type);
			if (!cur.isAncillary()) {
				throw new PngException("Private critical chunk encountered: " + type_string);
			}
			for (int i = 0; i < 4; i++) {
				char c = type_string.charAt(i);
				if (c < 65 || (c > 90 && c < 97) || c > 122) {
					throw new PngException("Corrupted chunk type: " + type_string);
				}
			}
		}
		return cur;
	}

	public int read(byte b[], int off, int len) throws IOException {
		if (cur == null)
			readToData();
		if (chunk_left == 0)
			return -1;
		int need = chunk_left < len ? chunk_left : len;
		in_data.readFully(b, off, need);
		chunk_left -= need;
		if (chunk_left == 0 && getNextChunk().type != Chunk.IDAT) {
			Chunk chunk;
			while ((chunk = getNextChunk()) != null) {
				if (chunk.beforeIDAT()) {
					throw new PngException(Chunk.typeToString(chunk.type) + " chunk must precede first IDAT chunk");
				}
			}
			if (close)
				close();
		}
		return need;
	}

	private byte[] _b = new byte[1];

	public int read() throws IOException {
		return read(_b, 0, 1) > 0 ? _b[0] & 0xff : -1;
	}

	public void close() throws IOException {
		in_data.close();
	}
}
