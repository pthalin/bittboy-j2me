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

final class Chunk_gIFx extends Chunk implements GifExtension {
	private String identifier;
	private byte[] auth_code;
	private byte[] data;

	Chunk_gIFx() {
		super(gIFx);
	}

	protected void readData() throws IOException {
		identifier = in_data.readString(8, PngImage.ASCII_ENCODING);
		in_data.skip(8 - identifier.length());

		auth_code = new byte[3];
		in_data.readFully(auth_code);

		data = new byte[bytesRemaining()];
		in_data.readFully(data);

		img.data.gifExtensions.addElement(this);
	}

	public String getIdentifier() {
		return identifier;
	}

	public byte[] getAuthenticationCode() {
		return auth_code;
	}

	public byte[] getData() {
		return data;
	}
	
	Chunk copy() {

		Chunk_gIFx chunk = new Chunk_gIFx();
		chunk.identifier = identifier;
		System.arraycopy(auth_code, 0, chunk.auth_code, 0, auth_code.length);
		System.arraycopy(data, 0, chunk.data, 0, data.length);
		
		return chunk;

	}
	
}
