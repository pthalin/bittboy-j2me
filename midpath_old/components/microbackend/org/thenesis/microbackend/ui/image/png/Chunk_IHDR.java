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

//import java.awt.image.ColorModel;
//import java.awt.image.DirectColorModel;
import java.io.IOException;

final class Chunk_IHDR extends Chunk {
	/* package */int width;
	/* package */int height;
	/* package */int depth;
	/* package */int outputDepth;
	/* package */int compress;
	/* package */int filter;
	/* package */int interlace;
	/* package */Interlacer interlacer;

	/* package */int samples = 1;
	/* package */int colorType;
	/* package */int cmBits;

	/* package */boolean paletteUsed = false;
	/* package */boolean colorUsed = false;
	/* package */boolean alphaUsed = false;

	/* package */ColorModel alphaModel;
	/* package */ColorModel model;

	Chunk_IHDR() {
		super(IHDR);
	}

	protected boolean multipleOK() {
		return false;
	}

	// TODO: shorten
	protected void readData() throws IOException {
		img.data.header = this;
		if (length != 13)
			badLength(13);

		width = in_data.readInt();
		height = in_data.readInt();
		if (width <= 0 || height <= 0) {
			throw new PngException("Bad image size: " + ExDataInputStream.unsign(width) + "x" + ExDataInputStream.unsign(height));
		}

		depth = in_data.readUnsignedByte();
		outputDepth = (depth == 16 ? 8 : depth);

		int blue = 0;
		switch (outputDepth) {
		case 1:
			blue = 0x01;
			break;
		case 2:
			blue = 0x03;
			break;
		case 4:
			blue = 0x0F;
			break;
		case 8:
			blue = 0xFF;
			break;
		default:
			throw new PngException("Bad bit depth: " + depth);
		}

		byte[] sbit = null;

		int green = blue << outputDepth;
		int red = green << outputDepth;
		int alpha = red << outputDepth;

		byte b_depth = (byte) depth;

		colorType = in_data.readUnsignedByte();
		switch (colorType) {
		case PngImage.COLOR_TYPE_GRAY:
			sbit = new byte[] { b_depth, b_depth, b_depth };
			cmBits = 3 * outputDepth;
			break;
		case PngImage.COLOR_TYPE_RGB:
			sbit = new byte[] { b_depth, b_depth, b_depth };
			cmBits = 3 * outputDepth;
			samples = 3;
			colorUsed = true;
			break;
		case PngImage.COLOR_TYPE_PALETTE:
			sbit = new byte[] { 8, 8, 8 };
			cmBits = outputDepth;
			colorUsed = paletteUsed = true;
			break;
		case PngImage.COLOR_TYPE_GRAY_ALPHA:
			sbit = new byte[] { b_depth, b_depth, b_depth, b_depth };
			cmBits = 4 * outputDepth;
			samples = 2;
			alphaUsed = true;
			break;
		case PngImage.COLOR_TYPE_RGB_ALPHA:
			sbit = new byte[] { b_depth, b_depth, b_depth, b_depth };
			cmBits = 4 * outputDepth;
			samples = 4;
			alphaUsed = colorUsed = true;
			break;
		default:
			cmBits = 0;
			throw new PngException("Bad color type: " + colorType);
		}

		img.data.properties.put("significant bits", sbit);

		if (!paletteUsed) {
			if (alphaUsed) {
				model = alphaModel = new DirectColorModel(cmBits, red, green, blue, alpha);
			} else {
				// we may switch to alphaModel if a tRNS chunk is found later
				alphaModel = ColorModel.getRGBdefault();
				model = new DirectColorModel(24, 0xFF0000, 0x00FF00, 0x0000FF);
			}
		}

		switch (colorType) {
		case PngImage.COLOR_TYPE_GRAY:
			break;
		case PngImage.COLOR_TYPE_PALETTE:
			if (depth == 16)
				throw new PngException("Bad bit depth for color type " + colorType + ": " + depth);
			break;
		default:
			if (depth <= 4)
				throw new PngException("Bad bit depth for color type " + colorType + ": " + depth);
		}

		if ((compress = in_data.readUnsignedByte()) != PngImage.COMPRESSION_TYPE_BASE)
			throw new PngException("Unrecognized compression method: " + compress);

		if ((filter = in_data.readUnsignedByte()) != PngImage.FILTER_TYPE_BASE)
			throw new PngException("Unrecognized filter method: " + filter);

		interlace = in_data.readUnsignedByte();
		switch (interlace) {
		case PngImage.INTERLACE_TYPE_NONE:
			interlacer = new NullInterlacer(width, height);
			break;
		case PngImage.INTERLACE_TYPE_ADAM7:
			interlacer = new Adam7Interlacer(width, height);
			break;
		default:
			throw new PngException("Unrecognized interlace method: " + interlace);
		}

		img.data.properties.put("width", new Integer(width));
		img.data.properties.put("height", new Integer(height));
		img.data.properties.put("bit depth", new Integer(depth));
		img.data.properties.put("interlace type", new Integer(interlace));
		img.data.properties.put("compression type", new Integer(compress));
		img.data.properties.put("filter type", new Integer(filter));
		img.data.properties.put("color type", new Integer(colorType));
	}
	
	Chunk copy() {

		Chunk_IHDR chunk = new Chunk_IHDR();
		chunk.width=width;
		chunk.height=height;
		chunk.depth=depth;
		chunk.outputDepth=outputDepth;
		chunk.compress=compress;
		chunk.filter=filter;
		chunk.interlace=interlace;
		chunk.interlacer=interlacer;

		chunk.samples = samples;
		chunk.colorType=colorType;
		chunk.cmBits=cmBits;

		chunk.paletteUsed = paletteUsed;
		chunk.colorUsed = colorUsed;
		chunk.alphaUsed = alphaUsed;

		chunk.alphaModel=alphaModel;
		chunk.model=model;
		
		return chunk;

	}
}
