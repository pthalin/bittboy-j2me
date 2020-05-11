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

final class Chunk_iTXt extends AbstractTextChunk implements TextChunk {
	private boolean compressed;
	private String language;
	private String translated;

	Chunk_iTXt() {
		super(iTXt);
	}

	public String getTranslatedKeyword() {
		return translated;
	}

	public String getLanguage() {
		return language;
	}

	protected boolean isCompressed() {
		return compressed;
	}

	protected String readValue() throws IOException {
		int flag = in_data.readByte();
		int method = in_data.readByte();
		if (flag == 1) {
			compressed = true;
			if (method != PngImage.COMPRESSION_TYPE_BASE) {
				throw new PngExceptionSoft("Unrecognized " + typeToString(type) + " compression method: " + method);
			}
		} else if (flag != 0) {
			throw new PngExceptionSoft("Illegal " + typeToString(type) + " compression flag: " + flag);
		}
		language = in_data.readString(PngImage.LATIN1_ENCODING);
		translated = in_data.readString(PngImage.UTF8_ENCODING);
		return super.readValue();
	}
	
	Chunk copy() {
		Chunk_iTXt chunk = new Chunk_iTXt();
		chunk.compressed = compressed;
		chunk.language = new String(language);
		chunk.translated = new String(translated);
		return chunk;
	}
}
