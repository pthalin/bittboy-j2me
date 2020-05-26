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

class Chunk { //implements Cloneable {
	/* package */int length;
	/* package */int type;

	protected PngImage img;
	protected ExDataInputStream in_data;

	Chunk(int type) {
		this.type = type;
	}

	//	Chunk copy() {
	//		try {
	//			return (Chunk) clone();
	//		} catch (CloneNotSupportedException e) {
	//			return null;
	//		}
	//	}

	Chunk copy() {

		Chunk chunk = new Chunk(type);
		chunk.img = img;
		chunk.in_data = in_data;
		chunk.length = length;

		return chunk;
	}

	boolean isAncillary() {
		return ((type & 0x20000000) != 0);
	}

	final boolean isPrivate() {
		return ((type & 0x00200000) != 0);
	}

	final boolean isReservedSet() {
		return ((type & 0x00002000) != 0);
	}

	final boolean isSafeToCopy() {
		return ((type & 0x00000020) != 0);
	}

	final boolean isUnknown() {
		return getClass() == Chunk.class;
	}

	int bytesRemaining() {
		return Math.max(0, length + 4 - img.data.in_idat.count());
	}

	protected boolean multipleOK() {
		return true;
	}

	protected boolean beforeIDAT() {
		return false;
	}

	static String typeToString(int x) {
		return ("" + (char) ((x >>> 24) & 0xFF) + (char) ((x >>> 16) & 0xFF) + (char) ((x >>> 8) & 0xFF) + (char) ((x) & 0xFF));
	}

	static int stringToType(String id) {
		return ((((int) id.charAt(0) & 0xFF) << 24) | (((int) id.charAt(1) & 0xFF) << 16)
				| (((int) id.charAt(2) & 0xFF) << 8) | (((int) id.charAt(3) & 0xFF)));
	}

	final void badLength(int correct) throws PngException {
		throw new PngException("Bad " + typeToString(type) + " chunk length: " + in_data.unsign(length) + " (expected "
				+ correct + ")");
	}

	final void badLength() throws PngException {
		throw new PngException("Bad " + typeToString(type) + " chunk length: " + in_data.unsign(length));
	}

	protected void readData() throws IOException {
		in_data.skipBytes(length);
	}

	// Do not remove
	//	static final int IHDR = stringToType("IHDR");
	//	static final int PLTE = stringToType("PLTE");
	//	static final int IDAT = stringToType("IDAT");
	//	static final int IEND = stringToType("IEND");
	//	static final int bKGD = stringToType("bKGD");
	//	static final int cHRM = stringToType("cHRM");
	//	static final int gAMA = stringToType("gAMA");
	//	static final int hIST = stringToType("hIST");
	//	static final int pHYs = stringToType("pHYs");
	//	static final int sBIT = stringToType("sBIT");
	//	static final int tEXt = stringToType("tEXt");
	//	static final int tIME = stringToType("tIME");
	//	static final int tRNS = stringToType("tRNS");
	//	static final int zTXt = stringToType("zTXt");
	//	static final int sRGB = stringToType("sRGB");
	//	static final int sPLT = stringToType("sPLT");
	//	static final int oFFs = stringToType("oFFs");
	//	static final int sCAL = stringToType("sCAL");
	//	static final int iCCP = stringToType("iCCP");
	//	static final int pCAL = stringToType("pCAL");
	//	static final int iTXt = stringToType("iTXt");
	//	static final int gIFg = stringToType("gIFg");
	//	static final int gIFx = stringToType("gIFx");

	static final int IHDR = 1229472850;
	static final int PLTE = 1347179589;
	static final int IDAT = 1229209940;
	static final int IEND = 1229278788;
	static final int bKGD = 1649100612;
	static final int cHRM = 1665684045;
	static final int gAMA = 1732332865;
	static final int hIST = 1749635924;
	static final int pHYs = 1883789683;
	static final int sBIT = 1933723988;
	static final int tEXt = 1950701684;
	static final int tIME = 1950960965;
	static final int tRNS = 1951551059;
	static final int zTXt = 2052348020;
	static final int sRGB = 1934772034;
	static final int sPLT = 1934642260;
	static final int oFFs = 1866876531;
	static final int sCAL = 1933787468;
	static final int iCCP = 1766015824;
	static final int pCAL = 1883455820;
	static final int iTXt = 1767135348;
	static final int gIFg = 1732855399;
	static final int gIFx = 1732855416;
}
