package de.avetana.bluetooth.util;

/**
 * <b>COPYRIGHT:</b><br> (c) Copyright 2004 Avetana GmbH ALL RIGHTS RESERVED. <br><br>
 *
 * This file is part of the Avetana bluetooth API for Linux.<br><br>
 *
 * The Avetana bluetooth API for Linux is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version. <br><br>
 *
 * The Avetana bluetooth API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.<br><br>
 *
 * The development of the Avetana bluetooth API is based on the work of
 * Christian Lorenz (see the Javabluetooth Stack at http://www.javabluetooth.org) for some classes,
 * on the work of the jbluez team (see http://jbluez.sourceforge.net/) and
 * on the work of the bluez team (see the BlueZ linux Stack at http://www.bluez.org) for the C code.
 * Classes, part of classes, C functions or part of C functions programmed by these teams and/or persons
 * are explicitly mentioned.<br><br><br><br>
 *
 *
 * <b>Description:</b><br>
 * Does Base64 encoding/decoding as described in section (6.8.) of RFC 2045.
 */
public class Base64 {

	/**
	 * the Base64 translation table; does the same as 'encode()', but
	 * this way decoding should be faster.
	 */
	public final static char[] translationTable = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
			'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
			'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0',
			'1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/' };

	/* line break characters for use in encoding output */
	private static char[] lbcs = { '\n' };

	/**
	 * Encode char array to Base64 formatted char array.
	 * @param text This argument must contain the text or binary data that shall be
	 *             encoded. This version of the 'encode()' method accepts a char array
	 *             as input and will - in contrast to the other 'encode()' method -
	 *             not try to do any conversion at all.
	 */
	public static char[] encode(byte[] text) {
		if (text == null) {
			return null;
		}
		byte[] a = text;

		/* we first need to calculate the size of our base64 encoded array */
		int size = a.length / 3 * 4;
		if (a.length % 3 > 0) {
			size += 4;
		}
		/* + line break characters */
		if (size % 68 > 0) {
			size += (size / 68 + 1) * lbcs.length;
		} else {
			size += (size / 68) * lbcs.length;
		}

		/* encoding... */
		char[] b = new char[size - 1];
		int p = 0;
		int wp = 0;
		int lc = 0;
		int[] y = new int[4];
		/* encode 24 bit entities */
		while (p / 3 < a.length / 3) {
			y[0] = (a[p] & 252) >>> 2;
			y[1] = (a[p] & 3) << 4 | (a[p + 1] & 240) >>> 4;
			y[2] = (a[p + 1] & 15) << 2 | (a[p + 2] & 192) >>> 6;
			y[3] = a[p + 2] & 63;
			for (int i = 0; i < 4; i++) {
				b[wp++] = translationTable[y[i]];
			}
			lc++;
			/* 68 characters per line */
			if (lc == 17) {
				for (int i = 0; i < lbcs.length; i++) {
					b[wp++] = lbcs[i];
				}
				lc = 0;
			}
			p += 3;
		}
		/* encode remaining partial entities (8 or 16 bits) */
		if (a.length - p == 1) {
			y[0] = (a[p] & 252) >>> 2;
			y[1] = (a[p] & 3) << 4;
			for (int i = 0; i < 2; i++) {
				b[wp++] = translationTable[y[i]];
			}
			b[wp++] = '=';
			b[wp++] = '=';
			lc++;
		} else if (a.length - p == 2) {
			y[0] = (a[p] & 252) >>> 2;
			y[1] = (a[p] & 3) << 4 | (a[p + 1] & 240) >>> 4;
			y[2] = (a[p + 1] & 15) << 2;
			for (int i = 0; i < 3; i++) {
				b[wp++] = translationTable[y[i]];
			}
			b[wp++] = '=';
			lc++;
		}

		return b;
	}

//	public static void main(String args[]) {
//		System.out.println(":::" + new String(Base64.encode(new byte[] { 0x00, 0x08 })) + ":::");
//	}

}
