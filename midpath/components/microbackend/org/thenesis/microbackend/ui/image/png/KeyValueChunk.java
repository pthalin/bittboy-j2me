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
import java.io.IOException;

import org.thenesis.microbackend.zip.DataFormatException;
import org.thenesis.microbackend.zip.Inflater;

abstract class KeyValueChunk extends Chunk {
	protected String key;
	protected String value;

	KeyValueChunk(int type) {
		super(type);
	}

	protected abstract boolean isCompressed();

	protected String getEncoding() {
		return PngImage.LATIN1_ENCODING;
	};

	protected void readData() throws IOException {
		key = readKey();
		value = readValue();
	}

	protected String readKey() throws IOException {
		String raw_key = in_data.readString();
		if (raw_key.length() > 79)
			throw new PngExceptionSoft(typeToString(type) + " string too long");
		return repairKey(raw_key);
	}

	protected String readValue() throws IOException {
		int L = bytesRemaining();
		byte[] buf = new byte[L];
		in_data.readFully(buf);

		if (isCompressed()) {
			byte method = buf[0];
			if (method != PngImage.COMPRESSION_TYPE_BASE) {
				throw new PngExceptionSoft("Unrecognized " + typeToString(type) + " compression method: " + method);
			}
			ByteArrayOutputStream bytes = new ByteArrayOutputStream(L * 3);
			byte[] tbuf = new byte[512];
			Inflater inf = new Inflater();
			inf.reset();
			inf.setInput(buf, 1, L - 1);
			try {
				while (!inf.needsInput()) {
					bytes.write(tbuf, 0, inf.inflate(tbuf));
				}
			} catch (DataFormatException e) {
				throw new PngExceptionSoft("Error inflating " + typeToString(type) + " chunk: " + e);
			}
			// return bytes.toString(getEncoding());
			return new String(bytes.toByteArray(), getEncoding());
		} else {
			return new String(buf, 0, L, getEncoding());
		}
	}

	/* package */static String repairKey(String k) {
		char[] chs = k.toCharArray();
		int i = 0, p = 0;
		int L = chs.length;
		BIGLOOP: while (p < L) {
			char ch = chs[p++];
			//if (Character.isWhitespace(ch)) {
			if (isWhiteSpace(ch)) {
				if (i > 0)
					chs[i++] = ' ';
				//while (Character.isWhitespace(ch = chs[p++]))
				while (isWhiteSpace(ch = chs[p++]))
					if (p == L)
						break BIGLOOP;
			}
			chs[i++] = ch;
		}
		//if (Character.isWhitespace(chs[i - 1]))
		if (isWhiteSpace(chs[i - 1]))
			i--;
		return new String(chs, 0, i);
	}
	
	public static boolean isWhiteSpace(char ch) {
	    return ((ch == ' ') || (ch == '\t') || (ch == '\n') || (ch == '\f') || 
		    (ch == '\r'));
	  }
}
