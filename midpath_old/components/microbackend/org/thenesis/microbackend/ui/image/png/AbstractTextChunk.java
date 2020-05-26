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
import java.util.Hashtable;

abstract class AbstractTextChunk extends KeyValueChunk implements TextChunk {
	static private Hashtable special_keys = new Hashtable();
	static {
		special_keys.put("Title", Boolean.TRUE);
		special_keys.put("Author", Boolean.TRUE);
		special_keys.put("Description", Boolean.TRUE);
		special_keys.put("Copyright", Boolean.TRUE);
		special_keys.put("Creation Time", Boolean.TRUE);
		special_keys.put("Software", Boolean.TRUE);
		special_keys.put("Disclaimer", Boolean.TRUE);
		special_keys.put("Warning", Boolean.TRUE);
		special_keys.put("Source", Boolean.TRUE);
		special_keys.put("Comment", Boolean.TRUE);
	}

	public String toString() {
		return getText();
	}

	public String getKeyword() {
		return key;
	}

	public String getText() {
		return value;
	}

	abstract public String getTranslatedKeyword();

	abstract public String getLanguage();

	AbstractTextChunk(int type) {
		super(type);
	}

	protected String readKey() throws IOException {
		String key = super.readKey();
		if (special_keys.containsKey(key)) {
			String lowerkey = key.toLowerCase();
			Object replace = img.data.properties.get(lowerkey);
			if (replace == null || ((Chunk) replace).type != iTXt)
				img.data.properties.put(lowerkey, this);
		}
		img.data.textChunks.put(key, this);
		return key;
	}

	protected String readValue() throws IOException {
		return repairValue(super.readValue());
	}

	private static String repairValue(String val) {
		CharArrayWriter out_chars = new CharArrayWriter(val.length());
		try {
			char[] chs = val.toCharArray();
			int p = 0;
			int L = chs.length;
			String endl = "\n"; //System.getProperty("line.separator");
			while (p < L) {
				char ch = chs[p++];
				switch (ch) {
				case '\r':
					if (p < L && chs[p + 1] == '\n')
						break;
				case '\n':
					out_chars.write(endl);
					break;
				case '\t':
					out_chars.write('\t');
					break;
				default:
					if (ch <= 31 || (ch >= 127 && ch <= 159)) {
						out_chars.write('\\');
						out_chars.write(Integer.toOctalString(ch));
					} else {
						out_chars.write(ch);
					}
				}
			}
		} catch (IOException e) {
		}
		return out_chars.toString();
	}

	public String getChunkType() {
		return typeToString(type);
	}
}
