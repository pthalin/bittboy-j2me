/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 */
package org.thenesis.midpath.ui.toolkit.virtual;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.UIToolkit;
import javax.microedition.lcdui.game.Sprite;

import org.thenesis.microbackend.ui.image.png.ColorModel;
import org.thenesis.microbackend.ui.image.png.ImageConsumer;
import org.thenesis.microbackend.ui.image.png.PngImage;

import com.sun.midp.log.Logging;

public class VirtualImage extends Image {

	static {
		PngImage.setProgressiveDisplay(false);
	}

	VirtualSurface surface;
	private boolean isMutable = false;

	public VirtualImage(int w, int h) {
		surface = createSurface(w, h);
		imgWidth = w;
		imgHeight = h;
		isMutable = true;
	}

	private void setDimensions(int w, int h) {
		this.imgWidth = w;
		this.imgHeight = h;
	}

	public VirtualImage(byte[] imageData, int imageOffset, int imageLength) throws IOException {
		this(new ByteArrayInputStream(imageData, imageOffset, imageLength));
	}

	private VirtualSurface createSurface(int w, int h) {
		return ((VirtualToolkit) UIToolkit.getToolkit()).getBackend().createSurface(w, h);
	}

	public VirtualImage(InputStream is) throws IOException {

		// Read PNG image from file
		PngImage png = new PngImage(is);

		final int pngWidth = png.getWidth();
		int pngHeight = png.getHeight();
		surface = createSurface(pngWidth, pngHeight);
		png.setBuffer(surface.data);

		png.startProduction(new ImageConsumer() {
			private ColorModel cm;

			public void imageComplete(int status) {
				for (int i = 0; i < surface.data.length; i++) {
					surface.data[i] = cm.getRGB(surface.data[i]);
					//System.out.print(Integer.toHexString(surface.data[i]) + " ");
				}
			}

			public void setColorModel(ColorModel model) {
				cm = model;
			}

			public void setDimensions(int width, int height) {
			}

			public void setHints(int flags) {
			}

			public void setProperties(Hashtable props) {
			}

			public void setPixels(int x, int y, int w, int h, ColorModel model, byte[] pixels, int offset, int scansize) {
			}

			public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int offset, int scansize) {
			}

		});

		setDimensions(pngWidth, pngHeight);
		isMutable = false;

		if (Logging.TRACE_ENABLED) {
			System.out.println("[DEBUG] VirtualImage.<init>(InputStream stream): errors while loading ? "
					+ png.hasErrors());
		}

	}

	public VirtualImage(int[] rgb, int width, int height, boolean processAlpha) { //throws IOException {

		this.imgWidth = width;
		this.imgHeight = height;

		surface = createSurface(width, height);

		// P(a, b) = rgb[a + b * width];
		int size = width * height;
		if (processAlpha) {
			for (int i = 0; i < size; i++) {
				if ((rgb[i] & 0xFF000000) != 0xFF000000)
					surface.data[i] = rgb[i] & 0x00FFFFFF;
				else {
					surface.data[i] = rgb[i];
				}
			}
		} else {
			for (int i = 0; i < size; i++) {
				surface.data[i] = rgb[i] | 0xFF000000;
			}
		}

		isMutable = false;

	}

	public VirtualImage(VirtualImage srcImage) {

		surface = createSurface(srcImage.getWidth(), srcImage.getHeight());

		int[] srcData = srcImage.surface.data;
		int[] destData = surface.data;

		System.arraycopy(srcData, 0, destData, 0, srcData.length);

		setDimensions(srcImage.getWidth(), srcImage.getHeight());
		isMutable = false;

	}

	/**
	 * Create a VirtualImage from a pre-existing surface (doesn't copy it)
	 * @param surface
	 */
	VirtualImage(VirtualSurface surface) {
		this.surface = surface;
		setDimensions(surface.getWidth(), surface.getHeight());
		isMutable = false;
	}

	VirtualImage(VirtualSurface srcSurface, int x, int y, int width, int height, int transform) {

		surface = transform(srcSurface, x, y, width, height, transform);
		setDimensions(surface.getWidth(), surface.getHeight());

		if (Logging.TRACE_ENABLED)
			System.out
					.println("[DEBUG] VirtualImage.<init>(Image image, int x, int y, int width, int height, int transform): not implemented yet");

	}

	public VirtualImage(Image image, int x, int y, int width, int height, int transform) {
		this(((VirtualImage) image).surface, x, y, width, height, transform);
		isMutable = false;
	}

	public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height) {

		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] VirtualImage.getRGB(): rgbData[" + rgbData.length + "] offset=" + offset
					+ " scanlength=" + scanlength + " x=" + x + " y=" + y + " width=" + width + " height=" + height);

		if ((x < 0) || (y < 0) || ((x + width) > imgWidth) || ((y + height) > imgHeight)) {
			throw new IllegalArgumentException();
		}

		if ((width <= 0) || (height <= 0)) {
			return;
		}

		for (int b = y; b < y + height; b++) {
			for (int a = x; a < x + width; a++) {
				//System.out.println("[DEBUG]VirtualImage.getRGB(): a=" + a + "  b=" + b);
				rgbData[offset + (a - x) + (b - y) * scanlength] = surface.data[a + b * scanlength];
				//rgbData[offset + (a - x) + (b - y) * scanlength] = P(a, b);
			}
		}

	}

	/**
	 * Draw the specified region of an Image to the current Image.
	 * This method assumes that coordinates of the source are already translated.
	 * @param g
	 * @param r
	 * @param x 
	 * @param y
	 */
	private boolean render(Graphics g, int x_src, int y_src, int width, int height, int x, int y, int anchor) {

		x += g.getTranslateX();
		y += g.getTranslateY();

		if (x_src < 0 || y_src < 0 || (x_src + width) > surface.getWidth() || (y_src + height) > surface.getHeight())
			return false;

		if ((anchor & Graphics.BOTTOM) == Graphics.BOTTOM) {
			y -= surface.getHeight();
		} else if ((anchor & Graphics.VCENTER) == Graphics.VCENTER) {
			y -= surface.getHeight() / 2;
		}

		if ((anchor & Graphics.RIGHT) == Graphics.RIGHT) {
			x -= surface.getWidth();
		} else if ((anchor & Graphics.HCENTER) == Graphics.HCENTER) {
			x -= surface.getWidth() / 2;
		}

		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG]VirtualImage.render2(): x=" + x + " y=" + y + " width=" + surface.getWidth()
					+ " height=" + surface.getHeight());

		VirtualGraphics vg = (VirtualGraphics) g;
		Rectangle clipRect = vg.clipRectangle;
		VirtualSurface destSurface = vg.getSurface();

		//		for (int iy = y_src; iy < (y_src + height); iy++, y++) {
		//			for (int ix = x_src; ix < (x_src + width); ix++, x++) {
		//			//System.out.println("[DEBUG]VirtualImage.render(): " + (i + dstPosition));
		//			destSurface.data[ y * destSurface.getWidth() + x]=  0xFF00FF00; //surface.data[i + srcPosition]; y * destSurface.getWidth() +
		//			}
		//		}

		// Clip source rectangle in source image.
		//int sxmin=r.xmin, symin=r.ymin, sxmax=r.xmax, symax=r.ymax;
		int sxmin = x_src, symin = y_src, sxmax = x_src + width, symax = y_src + height;
		if (sxmin < 0)
			sxmin = 0;
		if (symin < 0)
			symin = 0;
		if (sxmax > surface.width - 1)
			sxmax = surface.width - 1;
		if (symax > surface.height - 1)
			symax = surface.height - 1;

		// Clip destination rectangle in destination image.
		int dxmin = x + sxmin - x_src, dymin = y + symin - y_src, dxmax = x + sxmax - x_src, dymax = y + symax - y_src;
		if (dxmin < clipRect.xmin)
			dxmin = clipRect.xmin;
		if (dymin < clipRect.ymin)
			dymin = clipRect.ymin;
		if (dxmax > clipRect.xmax - 1)
			dxmax = clipRect.xmax - 1;
		if (dymax > clipRect.ymax - 1)
			dymax = clipRect.ymax - 1;

		// New source rectangle.
		sxmin = dxmin - x + x_src;
		symin = dymin - y + y_src;
		sxmax = dxmax - x + x_src;
		symax = dymax - y + y_src;

		int w = sxmax - sxmin + 1, h = symax - symin + 1;
		for (int ry = 0; ry < h; ry++) {

			int srcPosition = (symin + ry) * surface.width + sxmin;
			int dstPosition = (dymin + ry) * destSurface.width + dxmin;
			int length = w;

			for (int i = 0, sp = srcPosition, dp = dstPosition; i < length; i++, sp += 1, dp += 1) {
				// TODO support transparent pixels
				//System.out.println("[DEBUG]VirtualImage.render(): " + (i + dstPosition));
				if (((surface.data[i + srcPosition]) & 0xFF000000) == 0xFF000000)
					destSurface.data[i + dstPosition] = surface.data[i + srcPosition];
			}

		}

		return true;
	}

	public boolean render(Graphics g, int x, int y, int anchor) {

		//		if (Logging.TRACE_ENABLED)
		//			System.out.println("[DEBUG]VirtualImage.render(): x=" + x + " y=" + y + " width=" + surface.getWidth()
		//					+ " height=" + surface.getHeight());

		return render(g, 0, 0, imgWidth, imgHeight, x, y, anchor);

	}

	protected boolean renderRegion(Graphics g, int x_src, int y_src, int width, int height, int transform, int x_dest,
			int y_dest, int anchor) {

		x_src += g.getTranslateX();
		y_src += g.getTranslateY();

		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG]VirtualImage.renderRegion(): x_src=" + x_src + " y_src=" + y_src + " width="
					+ width + " height= " + height);

		if (transform == Sprite.TRANS_NONE) {
			render(g, x_src, y_src, width, height, x_dest, y_dest, anchor);
		} else {
			VirtualSurface transformedSurface = transform(this.surface, x_src, y_src, width, height, transform);
			VirtualImage transformedImage = new VirtualImage(transformedSurface);
			g.drawImage(transformedImage, x_dest, y_dest, anchor);
		}

		// FIXME Returns false if something goes wrong
		return true;

	}

	private void copy(VirtualSurface srcSurface, int x_src, int y_src, int width, int height,
			VirtualSurface destSurface, int x_dest, int y_dest) {

		int srcOffset = y_src * srcSurface.width + x_src;
		int destOffset = y_dest * destSurface.width + x_dest;

		for (int y = 0; y < height; y++) {

			int srcPosition = srcOffset + y * srcSurface.width;
			int destPosition = destOffset + y * destSurface.width;

			for (int x = 0; x < width; x++) {
				destSurface.data[destPosition + x] = srcSurface.data[srcPosition + x];
			}
		}
	}

	public VirtualSurface transform(VirtualSurface srcSurface, int x_src, int y_src, int width, int height,
			int transform) {

		switch (transform) {

		case Sprite.TRANS_ROT90:
		case Sprite.TRANS_ROT180:
		case Sprite.TRANS_ROT270:
			return rotate(srcSurface, x_src, y_src, width, height, transform);
		case Sprite.TRANS_MIRROR:
			VirtualSurface destSurface = createSurface(width, height);
			copy(srcSurface, x_src, y_src, width, height, destSurface, 0, 0);
			mirror(destSurface, 0, 0, width, height);
			return destSurface;
		case Sprite.TRANS_MIRROR_ROT90:
		case Sprite.TRANS_MIRROR_ROT180:
		case Sprite.TRANS_MIRROR_ROT270:
			destSurface = createSurface(width, height);
			copy(srcSurface, x_src, y_src, width, height, destSurface, 0, 0);
			mirror(destSurface, 0, 0, width, height);
			return rotate(destSurface, 0, 0, width, height, transform);
		}

		return srcSurface;
	}

	public VirtualSurface rotate(VirtualSurface srcSurface, int x_src, int y_src, int width, int height, int transform) {
		switch (transform) {
		case Sprite.TRANS_MIRROR_ROT90:
		case Sprite.TRANS_ROT90:

			VirtualSurface destSurface = createSurface(height, width);
			int[] destData = destSurface.data;

			int srcOffset = y_src * srcSurface.width + x_src;

			for (int y = 0; y < height; y++) {

				int srcPosition = srcOffset + y * srcSurface.width;
				int destPosition = height - y - 1;

				for (int x = 0; x < width; x++) {
					destData[destPosition + x * height] = srcSurface.data[srcPosition + x];
				}

			}

			return destSurface;

		case Sprite.TRANS_MIRROR_ROT180:
		case Sprite.TRANS_ROT180:

			destSurface = createSurface(width, height);
			destData = destSurface.data;

			srcOffset = y_src * srcSurface.width + x_src;
			int destOffset = width * height - 1;

			for (int y = 0; y < height; y++) {

				int srcPosition = srcOffset + y * srcSurface.width;
				int destPosition = destOffset - y * width;

				for (int x = 0; x < width; x++) {
					destData[destPosition - x] = srcSurface.data[srcPosition + x];
				}

			}

			return destSurface;

		case Sprite.TRANS_MIRROR_ROT270:
		case Sprite.TRANS_ROT270:

			destSurface = createSurface(height, width);
			destData = destSurface.data;

			srcOffset = y_src * srcSurface.width + x_src;
			destOffset = (width - 1) * height;

			for (int y = 0; y < height; y++) {

				int srcPosition = srcOffset + y * srcSurface.width;
				int destPosition = destOffset + y;

				for (int x = 0; x < width; x++) {
					destData[destPosition - x * height] = srcSurface.data[srcPosition + x];
				}

			}

			return destSurface;

		}
		return null;
	}

	/**
	 * In place mirror transformation.
	 * @param srcImage
	 */
	public void mirror(VirtualSurface srcSurface, int x_src, int y_src, int width, int height) {

		int[] buffer = srcSurface.data;
		int offset = y_src * srcSurface.width + x_src;

		for (int y = 0; y < height; y++) {

			offset = y * srcSurface.width;

			for (int x = 0; x < width / 2; x++) {
				int tmp = buffer[offset + x];
				buffer[offset + x] = buffer[offset + width - 1 - x];
				buffer[offset + width - 1 - x] = tmp;
			}
		}

	}

	public boolean isMutable() {
		return isMutable;
	}

}
