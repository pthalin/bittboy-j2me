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

final class Chunk_tRNS extends Chunk {
	/* package */int rgb;
	/* package */int rgb_low;
	/* package */int r, g, b;

	Chunk_tRNS() {
		super(tRNS);
	}

	protected boolean multipleOK() {
		return false;
	}

	protected boolean beforeIDAT() {
		return true;
	}

	protected void readData() throws IOException {
		int d = img.data.header.outputDepth;
		switch (img.data.header.colorType) {
		case PngImage.COLOR_TYPE_GRAY:
			if (length != 2)
				badLength(2);
			if (img.data.header.depth == 16) {
				r = g = b = in_data.readUnsignedByte();
				int low = in_data.readUnsignedByte();
				img.data.properties.put("transparency low bytes", new Color(low, low, low));
			} else {
				r = g = b = in_data.readUnsignedShort();
			}
			rgb = r | r << d | r << (d * 2);
			img.data.header.model = img.data.header.alphaModel;
			img.data.properties.put("transparency", new Color(r, g, b));
			break;

		case PngImage.COLOR_TYPE_RGB:
			if (length != 6)
				badLength(6);
			if (img.data.header.depth == 16) {
				r = in_data.readUnsignedByte();
				int low_r = in_data.readUnsignedByte();
				g = in_data.readUnsignedByte();
				int low_g = in_data.readUnsignedByte();
				b = in_data.readUnsignedByte();
				int low_b = in_data.readUnsignedByte();
				rgb_low = low_b | low_g << 8 | low_r << 16;
				img.data.properties.put("transparency low bytes", new Color(low_r, low_g, low_b));
			} else {
				r = in_data.readUnsignedShort();
				g = in_data.readUnsignedShort();
				b = in_data.readUnsignedShort();
			}
			rgb = b | g << d | r << (d * 2);
			img.data.header.model = img.data.header.alphaModel;
			img.data.properties.put("transparency", new Color(r, g, b));
			break;

		case PngImage.COLOR_TYPE_PALETTE:
			if (img.data.palette == null)
				throw new PngException("tRNS chunk must follow PLTE chunk");
			Chunk_PLTE p = img.data.palette;
			int size = p.r.length;
			if (length > size)
				badLength();
			p.a_raw = new int[size];
			p.a = new byte[size];
			int i = 0;
			while (i < length) {
				p.a[i] = (byte) (p.a_raw[i] = in_data.readUnsignedByte());
				i++;
			}
			while (i < size) {
				p.a[i] = (byte) (p.a_raw[i] = 0xFF);
				i++;
			}
			img.data.properties.put("transparency size", new Integer(length));
			p.updateProperties(true);
			break;

		default:
			throw new PngException("tRNS prohibited for color type " + img.data.header.colorType);
		}
	}
	
	Chunk copy() {

		Chunk_tRNS chunk = new Chunk_tRNS();
		
		chunk.rgb=rgb;
		chunk.rgb_low=rgb_low;
		chunk.r=r;
		chunk.g=g;
		chunk.b=b;
		
		return chunk;
	}
}
