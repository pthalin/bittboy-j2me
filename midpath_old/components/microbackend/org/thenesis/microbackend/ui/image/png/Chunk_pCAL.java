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

final class Chunk_pCAL extends Chunk {
	Chunk_pCAL() {
		super(pCAL);
	}

	protected boolean multipleOK() {
		return false;
	}

	protected boolean beforeIDAT() {
		return true;
	}

	protected void readData() throws IOException {
		String purpose, unit_string;

		if ((purpose = in_data.readString()).length() > 79) {
			throw new PngExceptionSoft("pCAL purpose too long");
		}
		purpose = KeyValueChunk.repairKey(purpose);

		int X0 = in_data.readInt();
		int X1 = in_data.readInt();
		if (X1 == X0) {
			throw new PngExceptionSoft("X1 == X0 in pCAL chunk");
		}

		int equation_type = in_data.readUnsignedByte();
		int N = in_data.readUnsignedByte();

		if ((unit_string = in_data.readString()).length() > 79) {
			throw new PngExceptionSoft("pCAL unit string too long");
		}

		double[] P = new double[N];
		for (int i = 0; i < N; i++) {
			P[i] = in_data.readFloatingPoint();
		}

		img.data.properties.put("pixel calibration purpose", purpose);
		img.data.properties.put("pixel calibration x0", new Integer(X0));
		img.data.properties.put("pixel calibration x1", new Integer(X1));
		img.data.properties.put("pixel calibration type", new Integer(equation_type));
		img.data.properties.put("pixel calibration n", new Integer(N));
		img.data.properties.put("pixel calibration unit", unit_string);
		img.data.properties.put("pixel calibration parameters", P);
	}
	
	Chunk copy() {
		return new Chunk_pCAL();
	}
}
