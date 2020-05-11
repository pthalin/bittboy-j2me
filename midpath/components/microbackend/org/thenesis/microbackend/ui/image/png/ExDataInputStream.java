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

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

final class ExDataInputStream extends DataInputStream {

	public ExDataInputStream(InputStream in) {
		super(in);
	}

	public static long unsign(int x) {
		return 0xFFFFFFFFL & x;
	}

	public long readUnsignedInt() throws IOException {
		return unsign(readInt());
	}

	public String readString() throws IOException {
		return readString(-1, PngImage.LATIN1_ENCODING);
	}

	public String readString(String encoding) throws IOException {
		return readString(-1, encoding);
	}

	public String readString(int limit) throws IOException {
		return readString(limit, PngImage.LATIN1_ENCODING);
	}

	public String readString(int limit, String encoding) throws IOException {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream(limit < 0 ? 80 : limit);
		int i;
		for (i = 0; i != limit; i++) {
			int b = readByte();
			if (b == 0)
				break;
			bytes.write(b);
		}
		//	return bytes.toString(encoding);
		return new String(bytes.toByteArray(), encoding);
	}

	static public double parseFloatingPoint(String token) {
		int st = 0;
		int e1 = Math.max(token.indexOf('e'), token.indexOf('E'));
		double d = Double.valueOf(token.substring(st, (e1 < 0 ? token.length() : e1))).doubleValue();
		if (e1 > 0)
			d *= Util.pow(10d, Double.valueOf(token.substring(e1 + 1)).doubleValue());
		return d;
	}

	public double readFloatingPoint() throws IOException {
		return parseFloatingPoint(readString());
	}

	//     public int readBytes(byte[] b)
	//     throws IOException
	//     {
	//         return readBytes(b, 0, b.length);
	//     }

	//     public int readBytes(byte[] b, int off, int len)
	//     throws IOException
	//     {
	//         int total = 0;
	//         while (total < len) {
	//             int result = in.read(b, off + total, len - total);
	//             if (result == -1) {
	//                 throw new EOFException();
	//             }
	//             total += result;
	//         }
	//         return total;
	//     }
}
