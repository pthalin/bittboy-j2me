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

class Chunk_cHRM extends Chunk {
	protected long xwlong, ywlong, xrlong, yrlong, xglong, yglong, xblong, yblong;
	protected double xw, yw, xr, yr, xg, yg, xb, yb;
	protected double Xw, Yw, Zw;
	protected double Xr, Yr, Zr;
	protected double Xg, Yg, Zg;
	protected double Xb, Yb, Zb;

	Chunk_cHRM() {
		super(cHRM);
	}

	protected boolean multipleOK() {
		return false;
	}

	protected boolean beforeIDAT() {
		return true;
	}

	/*
	 zr = 1 - (xr + yr)
	 zg = 1 - (xg + yg)
	 zb = 1 - (xb + yb)
	 zw = 1 - (xw + yw)

	 / r \   / xr xg xb \-1 / xw / yw \
	 | g | = | yr yg yb |   | yw / yw |
	 \ b /   \ zr zg zb /   \ zw / yw /

	 det:  xr*yg*zb + xg*yb*zr + xb*yr*zg - xg*yr*zb - xr*yb*zg - xb*yg*zr

	 Yr = r * yr
	 Yg = g * yg
	 Yb = b * yb
	 */

	protected void calc() {
		double zr = 1 - (xr + yr);
		double zg = 1 - (xg + yg);
		double zb = 1 - (xb + yb);
		double zw = 1 - (xw + yw);

		Xw = xw / yw;
		Yw = 1; /* yw / yw; */
		Zw = zw / yw;

		double det = xr * (yg * zb - zg * yb) - xg * (yr * zb - zr * yb) + xb * (yr * zg - zr * yg);
		double fr = (Xw * (yg * zb - zg * yb) - xg * (zb - Zw * yb) + xb * (zg - Zw * yg)) / det;
		double fg = (xr * (zb - Zw * yb) - Xw * (yr * zb - zr * yb) + xb * (yr * Zw - zr)) / det;
		double fb = (xr * (yg * Zw - zg) - xg * (yr * Zw - zr) + Xw * (yr * zg - zr * yg)) / det;

		Xr = fr * xr;
		Yr = fr * yr;
		Zr = fr * zr;
		Xg = fg * xg;
		Yg = fg * yg;
		Zg = fg * zg;
		Xb = fb * xb;
		Yb = fb * yb;
		Zb = fb * zb;

		if (img.getChunk(sRGB) == null) {
			img.data.properties.put("chromaticity xy", new long[][] { { xwlong, ywlong }, { xrlong, yrlong },
					{ xglong, yglong }, { xblong, yblong } });
			img.data.properties.put("chromaticity xyz", new double[][] { { Xw, Yw, Zw }, { Xr, Yr, Zr },
					{ Xg, Yg, Zg }, { Xb, Yb, Zb } });
		}
	}

	protected void readData() throws IOException {
		if (img.data.palette != null)
			throw new PngException("cHRM chunk must precede PLTE chunk");
		if (length != 32)
			badLength(32);
		checkRange(xw = (double) (xwlong = in_data.readUnsignedInt()) / 100000, "white");
		checkRange(yw = (double) (ywlong = in_data.readUnsignedInt()) / 100000, "white");
		checkRange(xr = (double) (xrlong = in_data.readUnsignedInt()) / 100000, "red");
		checkRange(yr = (double) (yrlong = in_data.readUnsignedInt()) / 100000, "red");
		checkRange(xg = (double) (xglong = in_data.readUnsignedInt()) / 100000, "green");
		checkRange(yg = (double) (yglong = in_data.readUnsignedInt()) / 100000, "green");
		checkRange(xb = (double) (xblong = in_data.readUnsignedInt()) / 100000, "blue");
		checkRange(yb = (double) (yblong = in_data.readUnsignedInt()) / 100000, "blue");

		calc();
	}

	private void checkRange(double value, String color) throws PngException {
		if (value < 0 || value > 0.8)
			throw new PngExceptionSoft("Invalid cHRM " + color + " point");
	}

	Chunk copy() {

		Chunk_cHRM chunk = new Chunk_cHRM();

		chunk.xwlong = xwlong;
		chunk.ywlong = ywlong;
		chunk.xrlong = xrlong;
		chunk.yrlong = yrlong;
		chunk.xglong = xglong;
		chunk.yglong = yglong;
		chunk.xblong = xblong;
		chunk.yblong = yblong;
		chunk.xw = xw;
		chunk.yw = yw;
		chunk.xr = xr;
		chunk.yr = yr;
		chunk.xg = xg;
		chunk.yg = yg;
		chunk.xb = xb;
		chunk.yb = yb;
		chunk.Xw = Xw;
		chunk.Yw = Yw;
		chunk.Zw = Zw;
		chunk.Xr = Xr;
		chunk.Yr = Yr;
		chunk.Zr = Zr;
		chunk.Xg = Xg;
		chunk.Yg = Yg;
		chunk.Zg = Zg;
		chunk.Xb = Xb;
		chunk.Yb = Yb;
		chunk.Zb = Zb;

		return chunk;

	}

}
