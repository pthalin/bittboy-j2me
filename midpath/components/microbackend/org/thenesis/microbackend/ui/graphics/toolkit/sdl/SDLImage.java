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
package org.thenesis.microbackend.ui.graphics.toolkit.sdl;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;

import org.thenesis.microbackend.ui.Logging;
import org.thenesis.microbackend.ui.graphics.VirtualGraphics;
import org.thenesis.microbackend.ui.graphics.VirtualImage;
import org.thenesis.microbackend.ui.sdl.SDLBackend;

import sdljava.SDLException;
import sdljava.video.SDLPixelFormat;
import sdljava.video.SDLRect;
import sdljava.video.SDLSurface;
import sdljava.video.SDLVideo;
import sdljavax.gfx.SDLGfx;


public class SDLImage implements VirtualImage {

	SDLSurface sdlSurface;
	private boolean isMutable = false;
    private int imgWidth;
    private int imgHeight;

	public SDLImage(int w, int h) {
		try {
			sdlSurface = SDLBackend.createRGBSurface(w, h);
			imgWidth = w;
			imgHeight = h;
			isMutable = true;
		} catch (SDLException e) {
			e.printStackTrace();
		}
	}

	public SDLImage(int[] rgb, int width, int height, boolean processAlpha) { //throws IOException {

		this.imgWidth = width;
		this.imgHeight = height;

		SDLSurface tmpSurface;
		try {
			tmpSurface = SDLVideo.createRGBSurface(SDLVideo.SDL_SWSURFACE, width, height, 32, 0x00ff0000L, 0x0000ff00L,
					0x000000ffL, 0xff000000L);

			// TODO Draw rgb field on the surface
			tmpSurface.setPixelData32(rgb);
			
			// Convert surface format to the display format 
			if (processAlpha) {
				sdlSurface = tmpSurface.displayFormatAlpha();
			} else {
				sdlSurface = tmpSurface.displayFormat();
			}
			
			isMutable = false;
			
//			if (processAlpha) {
//			sdlSurface = tmpSurface;
//		} else {
//			sdlSurface = tmpSurface.displayFormat();
//		}


		} catch (SDLException e) {
			//throw new IOException(e.getMessage());
		}

	}
	
	public SDLImage(SDLImage srcImage) {
		try {
			sdlSurface = srcImage.sdlSurface.displayFormatAlpha();
			this.imgWidth = srcImage.getWidth();
			this.imgHeight = srcImage.getHeight();
			isMutable = false;
		} catch (SDLException e) {
			e.printStackTrace();
		}
	}
	
	public SDLImage(SDLSurface rootARGBSurface) {
        // TODO Auto-generated constructor stub
    }

	SDLImage(SDLSurface srcSurface, int x, int y, int width, int height, int transform) {
		try {

			SDLSurface croppedSurface = cropSurface(srcSurface, x, y, width, height);

			switch (transform) {
			case Sprite.TRANS_NONE:
				sdlSurface = croppedSurface;
				break;
			case Sprite.TRANS_ROT90:
				sdlSurface = SDLGfx.rotozoomSurface(croppedSurface, -90, 1, false);
				break;
			case Sprite.TRANS_ROT180:
				sdlSurface = SDLGfx.rotozoomSurface(croppedSurface, -180, 1, false);
				break;
			case Sprite.TRANS_ROT270:
				sdlSurface = SDLGfx.rotozoomSurface(croppedSurface, -270, 1, false);
				break;
			case Sprite.TRANS_MIRROR:
				sdlSurface = SDLGfx.zoomSurface(croppedSurface, -1, 1, false);
				break;
			case Sprite.TRANS_MIRROR_ROT90:
				SDLSurface mirroredSurface = SDLGfx.zoomSurface(croppedSurface, -1, 1, false);
				sdlSurface = SDLGfx.rotozoomSurface(mirroredSurface, -90, 1, false);
				break;
			case Sprite.TRANS_MIRROR_ROT180:
				mirroredSurface = SDLGfx.zoomSurface(croppedSurface, -1, 1, false);
				sdlSurface = SDLGfx.rotozoomSurface(mirroredSurface, -180, 1, false);
				break;
			case Sprite.TRANS_MIRROR_ROT270:
				mirroredSurface = SDLGfx.zoomSurface(croppedSurface, -1, 1, false);
				sdlSurface = SDLGfx.rotozoomSurface(mirroredSurface, -270, 1, false);
				break;
			}

			this.imgWidth = sdlSurface.getWidth();
			this.imgHeight = sdlSurface.getHeight();

		} catch (SDLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


    private SDLSurface cropSurface(SDLSurface srcSurface, int x, int y, int width, int height) throws SDLException {

		// Create surface compatible with display surface
		long flags = srcSurface.getFlags();
		SDLPixelFormat format = srcSurface.getFormat();
		int pitch = format.getBitsPerPixel();
		long rMask = format.getRMask();
		long gMask = format.getGMask();
		long bMask = format.getBMask();
		long aMask = format.getAMask();
		SDLSurface dstSurface = SDLVideo.createRGBSurface(flags, width, height, pitch, rMask, gMask, bMask, aMask);

		// Fill the destination with a given transparent color
		long c = dstSurface.mapRGB(0x99, 0x33, 0x99); //#993399
		dstSurface.fillRect(c);
		SDLRect srcRect = new SDLRect(x, y, width, height);
		SDLRect dstRect = new SDLRect(0, 0, width, height);
		srcSurface.blitSurface(srcRect, dstSurface, dstRect);

		// Set the transparent color
		dstSurface.setColorKey(SDLVideo.SDL_SRCCOLORKEY, c); // rootSurface.mapRGB(0xFF, 0, 0)

		return dstSurface.displayFormat();

		//		SDLSurface dstSurface = SDLToolkit.getToolkit().createSDLSurface(width, height);
		//		
		////		//if (srcSurface.isColorKeyBlit()) {
		////			System.out.println("[DEBUG] SDLImage.cropSurface(): ColorKeyBlit");
		////			long c = srcSurface.getFormat().getColorKey();
		////			System.out.println("[DEBUG] SDLImage.cropSurface(): ColorKeyBlit : " + Long.toHexString(c));
		////			//srcSurface.setColorKey(SDLVideo.SDL_SRCCOLORKEY, 0xffffffff);
		////			//srcSurface.setColorKey(SDLVideo.SDL_SRCCOLORKEY, 0xaaaaaaaaL);
		////			//srcSurface.getFormat().setColorKey(arg0)
		////			dstSurface.fillRect(c);
		////		//}
		//		
		//		SDLRect srcRect = new SDLRect(x, y, width, height);
		//		SDLRect dstRect = new SDLRect(0, 0, width, height);
		//		srcSurface.blitSurface(srcRect, dstSurface, dstRect);
		//		
		//		//srcSurface.setColorKey(SDLVideo.SDL_SRCCOLORKEY, c);
		//		
		//		return dstSurface;

	}

	public static void copy(SDLImage source, SDLImage dest) {
		try {
			if ((source.sdlSurface != null) && (dest.sdlSurface != null)) {
				source.sdlSurface.blitSurface(dest.sdlSurface);
			}
		} catch (SDLException e) {
			e.printStackTrace();
		}
	}

	public void getRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height) {

		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] SDLImage.getRGB(): rgbData[" + rgbData.length + "] offset=" + offset + " scanlength=" + scanlength+ " x=" + x + " y=" + y + " width=" + width + " height=" + height);
		
		if ((x < 0) || (y < 0) || ((x + width) > imgWidth) || ((y + height) > imgHeight)) {
			throw new IllegalArgumentException();
		}
		
		if ((width <= 0) || (height <= 0)) {
			return;
		}
		
		try {

			//		// Get the surface for the region
			//		long flags = sdlSurface.getFlags();
			//		SDLPixelFormat format = sdlSurface.getFormat();
			//		int pitch = format.getBitsPerPixel();
			//		long rMask = format.getRMask();
			//		long gMask = format.getGMask();
			//		long bMask = format.getBMask();
			//		long aMask = format.getAMask();
			//		SDLSurface copiedSurface = SDLVideo.createRGBSurface(flags, width, height, pitch, rMask, gMask, bMask, aMask);
			//		
			//		SDLRect srcRect = new SDLRect(x, y, width, height);
			//		SDLRect dstRect = new SDLRect(0, 0, width, height);
			//		sdlSurface.blitSurface(srcRect, copiedSurface, dstRect);
			//		
			//		// convert the cropped surface to ARGB
			//		SDLSurface fakeARGBSurface = SDLVideo.createRGBSurface(SDLVideo.SDL_SWSURFACE, 2, 2, 32, 0x00ff0000L, 0x0000ff00L,
			//				0x000000ffL, 0xff000000L);
			//		SDLSurface dstSurface = copiedSurface.convertSurface(fakeARGBSurface.getFormat(), SDLVideo.SDL_SWSURFACE);

			// Convert the surface to ARGB
			SDLSurface fakeARGBSurface = SDLVideo.createRGBSurface(SDLVideo.SDL_SWSURFACE, 2, 2, 32, 0x00ff0000L,
					0x0000ff00L, 0x000000ffL, 0xff000000L);
			SDLSurface dstSurface = sdlSurface.convertSurface(fakeARGBSurface.getFormat(), SDLVideo.SDL_SWSURFACE);

			// Get the pixels
			//int[] buf = new int[this.imgWidth * this.imgHeight]; // dstSurface.getPixel32();
			int[] buf = dstSurface.getPixelData32();
			
			for (int b = y; b < y + height; b++) {
				for (int a = x; a < x + width; a++) {
					//System.out.println("[DEBUG]SDLImage.getRGB(): a=" + a + "  b=" + b);
					rgbData[offset + (a - x) + (b - y) * scanlength] = buf[a + b * scanlength];
					//rgbData[offset + (a - x) + (b - y) * scanlength] = P(a, b);
				}
			}

		} catch (SDLException e) {
			//throw new IOException(e.getMessage());
			e.printStackTrace();
		}

	}

	public boolean render(VirtualGraphics g, int x, int y, int anchor) {

                System.out.println("calling render in SDLImage.java");
		x += g.getTranslateX();
		y += g.getTranslateY();

		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG]SDLImage.render(): x=" + x + " y=" + y + " width=" + sdlSurface.getWidth()
				+ " height=" + sdlSurface.getHeight());

		if ((anchor & Graphics.BOTTOM) == Graphics.BOTTOM) {
			y -= sdlSurface.getHeight() - 1;
		} else if ((anchor & Graphics.VCENTER) == Graphics.VCENTER) {
			y -= sdlSurface.getHeight() / 2 - 1;
		}

		if ((anchor & Graphics.RIGHT) == Graphics.RIGHT) {
			x -= sdlSurface.getWidth() - 1;
		} else if ((anchor & Graphics.HCENTER) == Graphics.HCENTER) {
			x -= sdlSurface.getWidth() / 2 - 1;
		}

		//SDLSurface rootSurface = SDLToolkit.getToolkit().getRootGraphics().getSurface();
		SDLSurface rootSurface = ((SDLGraphics)g).getSDLImage().sdlSurface;
		SDLRect dstRect = new SDLRect(x, y, sdlSurface.getWidth(), sdlSurface.getHeight());

		try {
			sdlSurface.blitSurface(rootSurface, dstRect);
		} catch (SDLException e) {
			e.printStackTrace();
		}
		
		//System.out.println("[DEBUG]SDLImage.render(): color key : " + sdlSurface.getFormat().getColorKey());

		// FIXME test if anchor has a correct value
		return true;
	}

	public boolean renderRegion(VirtualGraphics g, int x_src, int y_src, int width, int height, int transform, int x_dest,
			int y_dest, int anchor) {

	    
		x_src += g.getTranslateX();
		y_src += g.getTranslateY();
		x_dest += g.getTranslateX();
		y_dest += g.getTranslateY();

		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG]SDLImage.renderRegion(): x_src=" + x_src + " y_src=" + y_src + " width=" + width
				+ " height= " + height);

		if (transform == Sprite.TRANS_NONE) {
			if ((anchor & Graphics.BOTTOM) == Graphics.BOTTOM) {
				y_dest -= height - 1;
			} else if ((anchor & Graphics.VCENTER) == Graphics.VCENTER) {
				y_dest -= height / 2 - 1;
			}

			if ((anchor & Graphics.RIGHT) == Graphics.RIGHT) {
				x_dest -= width - 1;
			} else if ((anchor & Graphics.HCENTER) == Graphics.HCENTER) {
				x_dest -= width / 2 - 1;
			}

			SDLSurface destSurface = ((SDLGraphics)g).getSDLImage().sdlSurface;
			SDLRect srcRect = new SDLRect(x_src, y_src, width, height);
			SDLRect dstRect = new SDLRect(x_dest, y_dest, width, height);

			try {
				sdlSurface.blitSurface(srcRect, destSurface, dstRect);
			} catch (SDLException e) {
				e.printStackTrace();
			}
		} else {

			//System.out.println("[DEBUG] SDLImage.renderRegion(): ColorKeyBlit : " + Long.toHexString(sdlSurface.getFormat().getColorKey()));
			//System.out.println("[DEBUG] SDLImage.renderRegion(): ColorKeyBlit : " + sdlSurface.isColorKeyBlit());

			SDLImage transformedImage = new SDLImage(this.sdlSurface, x_src, y_src, width, height, transform);

			if ((anchor & Graphics.BOTTOM) == Graphics.BOTTOM) {
				y_dest -= transformedImage.getHeight() - 1;
			} else if ((anchor & Graphics.VCENTER) == Graphics.VCENTER) {
				y_dest -= transformedImage.getHeight() / 2 - 1;
			}

			if ((anchor & Graphics.RIGHT) == Graphics.RIGHT) {
				x_dest -= transformedImage.getWidth() - 1;
			} else if ((anchor & Graphics.HCENTER) == Graphics.HCENTER) {
				x_dest -= transformedImage.getWidth() / 2 - 1;
			}

			//SDLSurface destSurface = SDLToolkit.getToolkit().getRootGraphics().getSurface();
			SDLSurface destSurface = ((SDLGraphics)g).getSDLImage().sdlSurface;
			SDLRect dstRect = new SDLRect(x_dest, y_dest, transformedImage.getWidth(), transformedImage.getHeight());

			try {
				transformedImage.sdlSurface.blitSurface(destSurface, dstRect);
			} catch (SDLException e) {
				e.printStackTrace();
			}
		}

		// FIXME test if anchor has a correct value
		return true;
	}

	public boolean isMutable() {
		return isMutable;
	}
	
	/* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualImageInterface#getGraphics()
     */
    public VirtualGraphics getGraphics() {
        if (isMutable()) {

            if (null == this) {
                throw new NullPointerException();
            }

            SDLGraphics g = new SDLGraphics(this);
            //g.img = img;
            g.setDimensions(this.getWidth(), this.getHeight());
            g.reset();

            // construct and return a new ImageGraphics
            // object that uses the Image img as the 
            // destination.
            return g;
        } else {
            // SYNC NOTE: Not accessing any shared data, no locking necessary
            throw new IllegalStateException();
        }
    }
    
    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualImageInterface#getWidth()
     */
    public int getWidth() {
        return imgWidth;
    }

    /* (non-Javadoc)
     * @see org.thenesis.microbackend.ui.graphics.VirtualImageInterface#getHeight()
     */
    public int getHeight() {
        return imgHeight;
    }

}
