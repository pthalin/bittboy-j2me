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

final class Chunk_sPLT extends Chunk {
	Chunk_sPLT() {
		super(sPLT);
	}

	protected boolean beforeIDAT() {
		return true;
	}

	protected void readData() throws IOException {
		String name;
		if ((name = in_data.readString()).length() > 79)
			throw new PngExceptionSoft("sPLT palette name too long");
		name = KeyValueChunk.repairKey(name);
		if (img.data.palettes.containsKey(name))
			throw new PngExceptionSoft("Duplicate sPLT names");
		int depth = in_data.readUnsignedByte();
		int L = length - name.length();
		int[][] prop;
		if (depth == 8) {
			if (L % 6 != 0)
				badLength();
			int n = L / 6;
			prop = new int[5][n];
			for (int i = 0; i < n; i++) {
				prop[0][i] = in_data.readUnsignedByte();
				prop[1][i] = in_data.readUnsignedByte();
				prop[2][i] = in_data.readUnsignedByte();
				prop[3][i] = in_data.readUnsignedByte();
				prop[4][i] = in_data.readUnsignedShort();
			}
		} else if (depth == 16) {
			if (L % 10 != 0)
				badLength();
			int n = L / 10;
			prop = new int[5][n];
			for (int i = 0; i < n; i++) {
				prop[0][i] = in_data.readUnsignedShort();
				prop[1][i] = in_data.readUnsignedShort();
				prop[2][i] = in_data.readUnsignedShort();
				prop[3][i] = in_data.readUnsignedShort();
				prop[4][i] = in_data.readUnsignedShort();
			}
		} else {
			throw new PngExceptionSoft("Bad sPLT sample depth: " + depth);
		}
		img.data.palettes.put(name, prop);
	}
	
	Chunk copy() {
		return new Chunk_sPLT();
	}
}
