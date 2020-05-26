package sdljava.video;

/**
 *  sdljava - a java binding to the SDL API
 *
 *  Copyright (C) 2004  Ivan Z. Ganza
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *  USA
 *
 *  Ivan Z. Ganza (ivan_ganza@yahoo.com)
 */
//import java.nio.ByteBuffer;

//import org.gljava.opengl.GL;
//import org.gljava.opengl.impl.glew.GlewImpl;

import sdljava.SDLException;
import sdljava.SDLMain;
import sdljava.x.swig.SDL_Color;
import sdljava.x.swig.SDL_Rect;
import sdljava.x.swig.SDL_Surface;
import sdljava.x.swig.SWIG_SDLVideo;

/**
 * Graphical Surface Structure
 * <P>
 * SDL_Surface's represent areas of "graphical" memory, memory that
 * can be drawn to. The video framebuffer is returned as a SDL_Surface
 * by SDL_SetVideoMode and SDL_GetVideoSurface
 * <P>
 * This class encapsulates the SDL C structure SDL_Surface.  It also defines
 * methods for each function found in SDL_video.h which <I>takes an SDL_Surface
 * as an argument</I>.
 * <P>
 * Also see the documentation here:
 *     <a href="http://www.libsdl.org/cgi/docwiki.cgi/SDL_5fSurface">SDL_Surface</a>
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLSurface.java,v 1.35 2006/09/24 17:23:37 ivan_ganza Exp $
 * @see SDLPixelFormat
 */
public class SDLSurface {

	/** Handle to the SWIG SDL_Surface */
	SDL_Surface swigSurface;

	/**
	 * Direct byte buffer reference to the pixel data (null until first time getPixelData is called)
	 *
	 */
	//ByteBuffer pixelData;

	//static List<QueuedBlit> blitQueue = new ArrayList<QueuedBlit>();

	//    /**
	//     * Reference to GL context (can be null if GL mode is not being used)
	//     *
	//     */
	//    GL gl;

	/**
	 * Creates a new <code>SDLSurface</code> instance.
	 *
	 * @param surfaceHandle The swig proxy
	 */
	public SDLSurface(SDL_Surface swigSurface) {
		this.swigSurface = swigSurface;
	}

	/**
	 * Get the width of this surface
	 *
	 *  @return The Width of this surface (in pixels) 
	 */
	public int getWidth() {
		return swigSurface.getW();
	}

	/**
	 * Get the heigh of this surface
	 *
	 * @return The Height of this surface (in pixels)
	 */
	public int getHeight() {
		return swigSurface.getH();
	}

	/**
	 * Get the flags set in this surface
	 *
	 * @return The Flags set in this surface
	 */
	public long getFlags() {
		return swigSurface.getFlags();
	}

	/**
	 * @return The Pitch of this surface
	 *
	 * 
	 */
	public int getPitch() {
		return swigSurface.getPitch();
	}

	/**
	 * @return The Pixel Format of this service (see SDLPixelFormat structure)
	 *
	 * @see SDLPixelFormat
	 */
	public SDLPixelFormat getFormat() {
		return new SDLPixelFormat(swigSurface.getFormat());
	}

	/**
	 * Get the swig proxy
	 *
	 * @return The SWIG Proxy instance
	 */
	public SDL_Surface getSwigSurface() {
		return swigSurface;
	}

	//    /**
	//     * Get the OPEN GL Context.  Returns an instance of GL suitable for drawing
	//     * to if this surface was configured with SDL_OPENGL
	//     * <P>
	//     * When SDL has implemented multi-window support this method will return
	//     * the GL instance properly configured to draw to the surface
	//     *
	//     * @return a <code>GL</code> value
	//     * @exception SDLException if an error occurs
	//     */
	//    public GL getGL() throws SDLException {
	//	if (!isOpenGL()) throw new SDLException("This surface does not have an OPENGL Context");
	//
	//	if (gl == null) {
	//	    gl = new GlewImpl();
	//	}
	//
	//	return gl;
	//    }

	/**
	 * Makes sure the given area is updated on the given screen.
	 * The rectangle must be confined within the screen boundaries (no clipping is done).
	 * <P>
	 * This method should not be called while 'screen' is locked
	 * <P>
	 * @param x an <code>int</code> value
	 * @param y an <code>int</code> value
	 * @param w an <code>int</code> value
	 * @param h an <code>int</code> value
	 * @exception SDLException if an error occurs
	 */
	public void updateRect(int x, int y, long w, long h) throws SDLException {
		SWIG_SDLVideo.SDL_UpdateRect(swigSurface, x, y, w, h);
	}

	/**
	 * Makes sure the given area is updated on the given screen.
	 * The rectangle must be confined within the screen boundaries (no clipping is done).
	 *
	 * @param r a <code>Rectangle</code> value
	 * @exception SDLException if an error occurs
	 */
	public void updateRect(SDLRect r) throws SDLException {
		updateRect(r.getX(), r.getY(), r.getWidth(), r.getHeight());
	}

	/**
	 * Update the entire screen, calls updateRect(0,0,0,0)
	 *
	 * @exception SDLException if an error occurs
	 */
	public void updateRect() throws SDLException {
		updateRect(0, 0, 0, 0);
	}

	/**
	 * Makes sure the given list of rectangles is updated on the given screen.
	 * The rectangles must all be confined within the screen boundaries (no clipping is done).
	 * This function should not be called while screen is locked.
	 * <P>
	 * Note: It is adviced to call this function only once per frame, since each call has some
	 * processing overhead. This is no restriction since you can pass any number of rectangles each time.
	 * The rectangles are not automatically merged or checked for overlap. In general, the programmer
	 * can use his knowledge about his particular rectangles to merge them in an efficient way,
	 * to avoid overdraw.
	 * <P>
	 * <B><I>NOTE:  currently this method is not as efficient as it could be.  Instead of invoking the
	 * native SDL API SDL_UpdateRects() method it calls the java method updateRect once for each
	 * rectangle it has been passed.  We need to implement a custom type in SWIG for this to work
	 * natively</B></I>
	 *
	 * @param rects A List of Rectangles to update
	 * @exception SDLException if an error occurs
	 */
	//public void updateRects(SDLRect[] rects) throws SDLException {
	//    for (int i = 0; i < rects.length; i++) {
	//        updateRect(rects[i]);
	//    }
	//}
	/**
	 * On hardware that supports double-buffering, this function sets up a flip
	 * and returns.  The hardware will wait for vertical retrace, and then swap
	 * video buffers before the next video surface blit or lock will return.
	 * On hardware that doesn not support double-buffering, this is equivalent
	 * to calling SDL_UpdateRect(screen, 0, 0, 0, 0);
	 * <P>
	 * The SDL_DOUBLEBUF flag must have been passed to SDL_SetVideoMode() when
	 * setting the video mode for this function to perform hardware flipping.
	 * 
	 * @exception SDLException if an error occurs
	 */
	public void flip() throws SDLException {
		int result = SWIG_SDLVideo.SDL_Flip(swigSurface);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * Sets a portion of the colormap for the given 8-bit surface.  If 'surface'
	 * is not a palettized surface, this function does nothing, returning 0.
	 * If all of the colors were set as passed to SDL_SetColors(), it will
	 * return 1.  If not all the color entries were set exactly as given,
	 * it will return 0, and you should look at the surface palette to
	 * determine the actual color palette.
	 * <P>
	 * When 'surface' is the surface associated with the current display, the
	 * display colormap will be updated with the requested colors.  If 
	 * SDL_HWPALETTE was set in SDL_SetVideoMode() flags, SDL_SetColors()
	 * will always return 1, and the palette is guaranteed to be set the way
	 * you desire, even if the window colormap has to be warped or run under
	 * emulation.
	 */
	public void setColors(SDLColor[] colors) throws SDLException {
		if (colors.length != 256)
			throw new SDLException("size of colors array must be 256!");

		SDL_Color swigColors[] = new SDL_Color[256];
		for (int i = 0; i < 256; i++) {
			swigColors[i] = new SDL_Color();
			swigColors[i].setR((short) colors[i].getRed());
			swigColors[i].setG((short) colors[i].getGreen());
			swigColors[i].setB((short) colors[i].getBlue());
		}

		SWIG_SDLVideo.SWIG_SDL_SetColors(swigSurface, swigColors, 0, 256);
	}

	/**
	 * Sets a portion of the palette for the given 8-bit surface.
	 * <P>
	 *      Palettized (8-bit) screen surfaces with the SDL_HWPALETTE flag
	 *      have two palettes, a logical palette that is used for mapping
	 *      blits to/from the surface and a physical palette (that determines
	 *      how the hardware will map the colors to the
	 *      display). SDL_BlitSurface always uses the logical palette when
	 *      blitting surfaces (if it has to convert between surface pixel
	 *      formats). Because of this, it is often useful to modify only one
	 *      or the other palette to achieve various special color effects
	 *      (e.g., screen fading, color flashes, screen dimming).
	 *      <P>
	 *      This function can modify either the logical or physical palette
	 *      by specifying SDL_LOGPAL or SDL_PHYSPAL the in the flags
	 *      parameter.
	 *      <P>
	 *      When surface is the surface associated with the current display, the
	 *      display colormap will be updated with the requested colors. If
	 *      SDL_HWPALETTE was set in SDL_SetVideoMode flags, SDL_SetPalette will
	 *      always return 1, and the palette is guaranteed to be set the way you
	 *      desire, even if the window colormap has to be warped or run under
	 *      emulation.
	 *      <P>
	 *      The color components of a SDL_Color structure are 8-bits in size,
	 *      giving you a total of 2563 = 16777216 colors.
	 *
	 * @param flags an <code>int</code> value
	 * @param colors a <code>SDLColor[]</code> value
	 * @return If surface is not a palettized surface, this function
	 * does nothing, returning false. If all of the colors were set as
	 * passed to SDL_SetPalette, it will return true. If not all the
	 * color entries were set exactly as given, it will return false, and
	 * you should look at the surface palette to determine the actual
	 * color palette.
	 * @exception SDLException if an error occurs
	 */
	public boolean setPalette(int flags, SDLColor[] colors) throws SDLException {
		if (colors.length != 256)
			throw new SDLException("size of colors array must be 256!");

		SDL_Color swigColors[] = new SDL_Color[256];
		for (int i = 0; i < 256; i++) {
			swigColors[i] = new SDL_Color();
			swigColors[i].setR((short) colors[i].getRed());
			swigColors[i].setG((short) colors[i].getGreen());
			swigColors[i].setB((short) colors[i].getBlue());
		}

		return SWIG_SDLVideo.SWIG_SDL_SetPalette(swigSurface, flags, swigColors, 0, 256) == 1;
	}

	/**
	 * SDL_LockSurface() sets up a surface for directly accessing the pixels.
	 * Between calls to SDL_LockSurface()/SDL_UnlockSurface(), you can write
	 * to and read from 'surface->pixels', using the pixel format stored in 
	 * 'surface->format'.  Once you are done accessing the surface, you should 
	 * use SDL_UnlockSurface() to release it.
	 * <P>
	 * Not all surfaces require locking.  If SDL_MUSTLOCK(surface) evaluates
	 * to 0, then you can read and write to the surface at any time, and the
	 * pixel format of the surface will not change.  In particular, if the
	 * SDL_HWSURFACE flag is not given when calling SDL_SetVideoMode(), you
	 * will not need to lock the display surface before accessing it.
	 * <P>
	 * No operating system or library calls should be made between lock/unlock
	 * pairs, as critical system locks may be held during this time.
	 * <P>
	 *
	 * @return if the surface could be locked
	 * @exception SDLException if an error occurs
	 */
	public boolean lockSurface() throws SDLException {
		return SWIG_SDLVideo.SDL_LockSurface(swigSurface) == 0;
	}

	/**
	 * Unlock the surface.
	 *
	 * @exception SDLException if an error occurs
	 */
	public void unlockSurface() throws SDLException {
		SWIG_SDLVideo.SDL_UnlockSurface(swigSurface);
	}

	/**
	 * Save this surface as a BMP to the given path
	 *
	 * @param path a <code>String</code> value
	 * @exception SDLException if an error occurs
	 */
	public void saveBMP(String path) throws SDLException {
		int result = SWIG_SDLVideo.SWIG_SDL_SaveBMP(swigSurface, path);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * Sets the color key (transparent pixel) in a blittable surface
	 * and enables or disables RLE blit acceleration.
	 * <P>
	 * RLE acceleration can substantially speed up blitting of images with large horizontal runs
	 * of transparent pixels (i.e., pixels that match the key value). The key must be of the same
	 * pixel format as the surface, SDL_MapRGB is often useful for obtaining an acceptable value.
	 * <P>
	 * If flag is SDL_SRCCOLORKEY then key is the transparent pixel value in the source image of a blit.
	 * <P>
	 * If flag is OR'd with SDL_RLEACCEL then the surface will be draw using RLE acceleration when drawn
	 * with SDL_BlitSurface. The surface will actually be encoded for RLE acceleration the first time
	 * SDL_BlitSurface or SDL_DisplayFormat is called on the surface.
	 *
	 * @param flag an <code>int</code> value
	 * @param key an <code>int</code> value
	 * @exception SDLException if an error occurs
	 */
	public void setColorKey(long flag, long key) throws SDLException {
		int result = SWIG_SDLVideo.SDL_SetColorKey(swigSurface, flag, key);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * This function sets the alpha value for the entire surface, as opposed to
	 * using the alpha component of each pixel. This value measures the range
	 * of transparency of the surface, 0 being completely transparent to 255
	 * being completely opaque. An 'alpha' value of 255 causes blits to be
	 * opaque, the source pixels copied to the destination (the default). Note
	 * that per-surface alpha can be combined with colorkey transparency.
	 * <P>
	 * If 'flag' is 0, alpha blending is disabled for the surface.
	 * If 'flag' is SDL_SRCALPHA, alpha blending is enabled for the surface.
	 * OR:ing the flag with SDL_RLEACCEL requests RLE acceleration for the
	 * surface; if SDL_RLEACCEL is not specified, the RLE accel will be removed.
	 * <P>
	 * The 'alpha' parameter is ignored for surfaces that have an alpha channel.
	 *
	 * @param flag a <code>long</code> value
	 * @param alpha a <code>short</code> value
	 * @exception SDLException if an error occurs
	 */
	public void setAlpha(long flag, int alpha) throws SDLException {
		int result = SWIG_SDLVideo.SDL_SetAlpha(swigSurface, flag, (short) alpha);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * Sets the clipping rectangle for the destination surface in a blit.
	 * <P>
	 * If the clip rectangle is NULL, clipping will be disabled.
	 * If the clip rectangle doesn't intersect the surface, the function will
	 * return SDL_FALSE and blits will be completely clipped.  Otherwise the
	 * function returns SDL_TRUE and blits to the surface will be clipped to
	 * the intersection of the surface area and the clipping rectangle.
	 * <P>
	 * Note that blits are automatically clipped to the edges of the source
	 * and destination surfaces.
	 *
	 * @param rect a <code>Rectangle</code> value
	 * @exception SDLException if an error occurs
	 */
	public void setClipRect(SDLRect rect) throws SDLException {
		SDL_Rect swigRect = new SDL_Rect();
		swigRect.setX((short) rect.getX());
		swigRect.setY((short) rect.getY());
		swigRect.setW((int) rect.getWidth());
		swigRect.setH((int) rect.getHeight());
		SWIG_SDLVideo.SWIG_SDL_SetClipRect(swigSurface, swigRect);
	}

	/*
	 * Gets the clipping rectangle for the destination surface in a blit.
	 * 'rect' must be a pointer to a valid rectangle which will be filled
	 * with the correct values.
	 *
	 * @return The clipping Rectangle
	 */
	public SDLRect getClipRect() throws SDLException {
		SDL_Rect swigRect = new SDL_Rect();
		SWIG_SDLVideo.SDL_GetClipRect(swigSurface, swigRect);
		return new SDLRect(swigRect.getX(), swigRect.getY(), swigRect.getW(), swigRect.getH());
	}

	/**
	 * Creates a new surface of the specified format, and then copies and maps 
	 * the given surface to it so the blit of the converted surface will be as 
	 * fast as possible.  If this function fails, an exception will be thrown.
	 * <P>
	 * The 'flags' parameter is passed to SDL_CreateRGBSurface() and has those 
	 * semantics.  You can also pass SDL_RLEACCEL in the flags parameter and
	 * SDL will try to RLE accelerate colorkey and alpha blits in the resulting
	 * surface.
	 * <P>
	 * This function is used internally by SDL_DisplayFormat().
	 *
	 * @return The converted surface (newly created)
	 */
	public SDLSurface convertSurface(SDLPixelFormat fmt, long flags) throws SDLException {
		SDL_Surface s = SWIG_SDLVideo.SDL_ConvertSurface(swigSurface, fmt.getSwigPixelFormat(), flags);
		if (s == null) {
			throw new SDLException(SDLMain.getError());
		}
		return new SDLSurface(s);
	}

	/**
	 * This performs a fast blit from the source surface to the destination
	 * surface.  It assumes that the source and destination rectangles are
	 * the same size.  If either 'srcrect' or 'dstrect' are NULL, the entire
	 * surface (src or dst) is copied.  The final blit rectangles are saved
	 * in 'srcrect' and 'dstrect' after all clipping is performed.
	 * If the blit is successful, it returns 0, otherwise it returns -1.
	 * <P>
	 * The blit function should not be called on a locked surface.
	 * <P>
	 * The blit semantics for surfaces with and without alpha and colorkey
	 * are defined as follows:
	 * <P>
	 * <pre>
	 * RGBA->RGB:
	 *     SDL_SRCALPHA set:
	 * 	alpha-blend (using alpha-channel).
	 * 	SDL_SRCCOLORKEY ignored.
	 *     SDL_SRCALPHA not set:
	 * 	copy RGB.
	 * 	if SDL_SRCCOLORKEY set, only copy the pixels matching the
	 * 	RGB values of the source colour key, ignoring alpha in the
	 * 	comparison.
	 * 
	 * RGB->RGBA:
	 *     SDL_SRCALPHA set:
	 * 	alpha-blend (using the source per-surface alpha value);
	 * 	set destination alpha to opaque.
	 *     SDL_SRCALPHA not set:
	 * 	copy RGB, set destination alpha to source per-surface alpha value.
	 *     both:
	 * 	if SDL_SRCCOLORKEY set, only copy the pixels matching the
	 * 	source colour key.
	 * 
	 * RGBA->RGBA:
	 *     SDL_SRCALPHA set:
	 * 	alpha-blend (using the source alpha channel) the RGB values;
	 * 	leave destination alpha untouched. [Note: is this correct?]
	 * 	SDL_SRCCOLORKEY ignored.
	 *     SDL_SRCALPHA not set:
	 * 	copy all of RGBA to the destination.
	 * 	if SDL_SRCCOLORKEY set, only copy the pixels matching the
	 * 	RGB values of the source colour key, ignoring alpha in the
	 * 	comparison.
	 * 
	 * RGB->RGB: 
	 *     SDL_SRCALPHA set:
	 * 	alpha-blend (using the source per-surface alpha value).
	 *     SDL_SRCALPHA not set:
	 * 	copy RGB.
	 *     both:
	 * 	if SDL_SRCCOLORKEY set, only copy the pixels matching the
	 * 	source colour key.
	 *
	 * If either of the surfaces were in video memory, and the blit returns -2,
	 * the video memory was lost, so it should be reloaded with artwork and 
	 * re-blitted:
	 *   while ( SDL_BlitSurface(image, imgrect, screen, dstrect) == -2 ) {
	 *       while ( SDL_LockSurface(image) < 0 )
	 *       Sleep(10);
	 *       -- Write image pixels to image->pixels --
	 *       SDL_UnlockSurface(image);
	 *   }
	 * </pre>
	 * <P>
	 * This happens under DirectX 5.0 when the system switches away from your
	 * fullscreen application.  The lock will also fail until you have access
	 * to the video memory again.
	 *
	 * @param src        The source rectangle
	 * @param dstSurface The surface to blit to
	 * @param dst        The destination rectangle
	 * @return If the blit is successful, it returns 0, otherwise it returns -1.
	 *         If either of the surfaces were in video memory, and the blit returns
	 *         -2, the video memory was lost, so it should be reloaded with artwork
	 *         and re-blitted.  (please see documentation <a href="http://www.libsdl.org/cgi/docwiki.cgi/SDL_5fBlitSurface">here</a>
	 * @exception SDLException if an error occurs
	 */
	public int blitSurface(SDLRect src, SDLSurface dstSurface, SDLRect dst) throws SDLException {
		if (src == null) {
			src = new SDLRect(0, 0, getWidth(), getHeight());
		}
		if (dst == null) {
			dst = new SDLRect(0, 0, -1, -1);
		}
		return SWIG_SDLVideo.SWIG_SDL_BlitSurface_FAST(swigSurface, src.getX(), src.getY(), src.getWidth(), src
				.getHeight(), dstSurface.getSwigSurface(), dst.getX(), dst.getY(), dst.getWidth(), dst.getHeight());
	}

	public int blitSurface(SDLSurface dstSurface, SDLRect dst) throws SDLException {
		return blitSurface(null, dstSurface, dst);
	}

	public int blitSurface(SDLSurface dstSurface) throws SDLException {
		return blitSurface(null, dstSurface, null);
	}

	//public void queueBlit(SDLRect src, SDLSurface dstSurface, SDLRect dst) throws SDLException {
	//    if (src == null) {
	//        src = new SDLRect(0, 0, getWidth(), getHeight());
	//    }
	//    if (dst == null) {
	//        dst = new SDLRect(0, 0, -1, -1);
	//    }
	//    
	//    blitQueue.add(new QueuedBlit(swigSurface, src, dstSurface.getSwigSurface(), dst));
	//}
	//
	//public void queueBlit(SDLSurface dstSurface, SDLRect dst) throws SDLException {
	//    queueBlit(null, dstSurface, dst);
	//}
	//
	//public void queueBlit(SDLSurface dstSurface) throws SDLException {
	//    queueBlit(null, dstSurface, null);
	//}
	//
	//public static void flushBlitQueue() {
	//    int count = blitQueue.size();
	//    
	//    SDL_Surface[] src = new SDL_Surface[count];
	//    int[] sx = new int[count];
	//    int[] sy = new int[count];
	//    int[] sWidth = new int[count];
	//    int[] sHeight = new int[count];
	//
	//    SDL_Surface[] dst = new SDL_Surface[count];
	//    int[] dx = new int[count];
	//    int[] dy = new int[count];
	//    int[] dWidth = new int[count];
	//    int[] dHeight = new int[count];
	//
	//    for (int i = 0; i < count; i++) {
	//        QueuedBlit queuedBlit = blitQueue.get(i);
	//        
	//        src[i] = queuedBlit.src;
	//        SDLRect srcRect = queuedBlit.srcRect;
	//        sx[i] = srcRect.getX();
	//        sy[i] = srcRect.getY();
	//        sWidth[i] = srcRect.getWidth();
	//        sHeight[i] = srcRect.getHeight();
	//
	//        dst[i] = queuedBlit.dst;
	//        SDLRect dstRect = queuedBlit.dstRect;
	//        dx[i] = dstRect.getX();
	//        dy[i] = dstRect.getY();
	//        dWidth[i] = dstRect.getWidth();
	//        dHeight[i] = dstRect.getHeight();
	//    }
	//
	//    SWIG_SDLVideo.SWIG_QueueBlits(src, sx, sy, sWidth, sHeight,
	//    			      dst, dx, dy, dWidth, dHeight,
	//    			      count);
	//    blitQueue.clear();
	//}

	//    /**
	//     * Blit this surface onto dstSurface at the given point -- copy its entire width and height
	//     * onto the dstSurface
	//     *
	//     * @param dstSurface a <code>SDLSurface</code> value
	//     * @param dst a <code>Point</code> value
	//     * @return an <code>int</code> value
	//     * @exception SDLException if an error occurs
	//     */
	//    public int blitSurface(SDLSurface dstSurface, Point dst) throws SDLException {
	//	return SWIG_SDLVideo.SWIG_SDL_BlitSurface_FAST(swigSurface,
	//						       (int) 0,
	//						       (int) 0,
	//						       (int) getWidth(),
	//						       (int) getHeight(),
	//						       dstSurface.getSwigSurface(),
	//						       (int) dst.getX(),
	//						       (int) dst.getY(),
	//						       (int) getWidth(),
	//						       (int) getHeight()
	//						       );
	//    }
	//
	/**
	 * This function performs a fast fill of the given rectangle with
	 * 'color' The given rectangle is clipped to the destination
	 * surface clip area and the final fill rectangle is saved in the
	 * passed in pointer.
	 * <P>
	 * If 'dstrect' is NULL, the whole surface
	 * will be filled with 'color' The color should be a pixel of the
	 * format used by the surface, and can be generated by the
	 * SDL_MapRGB() function.
	 * <P>
	 * If there is a clip rectangle set on
	 * the destination (set via SDL_SetClipRect) then this function
	 * will clip based on the intersection of the clip rectangle and
	 * the dstrect rectangle and the dstrect rectangle will be
	 * modified to represent the area actually filled.
	 *
	 * @param dstrect The destination rect
	 * @param color   The color to fill with 
	 * @exception SDLException if an error occurs
	 * @throw SDLException If an error occurs
	 */
	public void fillRect(SDLRect dstrect, long color) throws SDLException {
		int result = SWIG_SDLVideo.SWIG_SDL_FillRect_FAST(swigSurface, (int) dstrect.getX(), (int) dstrect.getY(),
				(int) dstrect.getWidth(), (int) dstrect.getHeight(), color);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * Fill the entire surface area with color
	 *
	 * @param color a <code>long</code> value
	 * @exception SDLException if an error occurs
	 */
	public void fillRect(long color) throws SDLException {
		fillRect(new SDLRect(0, 0, getWidth(), getHeight()), color);
	}

	/**
	 * This function takes a surface and copies it to a new surface of the
	 * pixel format and colors of the video framebuffer, suitable for fast
	 * blitting onto the display surface.  It calls SDL_ConvertSurface()
	 * <P>
	 * If you want to take advantage of hardware colorkey or alpha blit
	 * acceleration, you should set the colorkey and alpha value before
	 * calling this function.
	 * <P>
	 * If the conversion fails or runs out of memory an exception will be thrown.
	 * @throw SDLException If an error occurs
	 */
	public SDLSurface displayFormat() throws SDLException {
		SDL_Surface s = SWIG_SDLVideo.SDL_DisplayFormat(swigSurface);
		if (s == null) {
			throw new SDLException(SDLMain.getError());
		}
		return new SDLSurface(s);
	}

	/**
	 * This function takes a surface and copies it to a new surface of the
	 * pixel format and colors of the video framebuffer (if possible),
	 * suitable for fast alpha blitting onto the display surface.
	 * The new surface will always have an alpha channel.
	 * <P>
	 * If you want to take advantage of hardware colorkey or alpha blit
	 * acceleration, you should set the colorkey and alpha value before
	 * calling this function.
	 * <P>
	 * If the conversion fails or runs out of memory an exception will be thrown
	 * @throw SDLException If an error occurs
	 */
	public SDLSurface displayFormatAlpha() throws SDLException {
		SDL_Surface s = SWIG_SDLVideo.SDL_DisplayFormatAlpha(swigSurface);
		if (s == null) {
			throw new SDLException(SDLMain.getError());
		}
		return new SDLSurface(s);
	}

	/**
	 * If this surface was loaded rom an image, and that image support
	 * a transparent pixel, calling this method will set the colorkey
	 * for the surface
	 *
	 */
	//    public void enableRLEAcceleration() {
	//	//SDL_SetColorKey(image, SDL_RLEACCEL, image->format->colorkey);
	//    }
	/**
	 * Get if this surface is stored in system memory
	 *
	 * @return if this surface is stored in system memory
	 */
	public boolean isSoftwareSurface() {
		return ((getFlags() & SDLVideo.SDL_SWSURFACE) == SDLVideo.SDL_SWSURFACE);
	}

	/**
	 * Get if this surface is stored in video memory
	 *
	 * @return if this surface is stored in video memory
	 */
	public boolean isHardwareSurface() {
		return ((getFlags() & SDLVideo.SDL_HWSURFACE) == SDLVideo.SDL_HWSURFACE);
	}

	/**
	 * Get if this surface uses asynchronous blits
	 *
	 * @return if this surface uses asynchronous blits
	 */
	public boolean isAsyncBlit() {
		return ((getFlags() & SDLVideo.SDL_ASYNCBLIT) == SDLVideo.SDL_ASYNCBLIT);
	}

	/**
	 * Get if this surface allows any pixel format
	 *
	 * @return if this surface allows any pixel format
	 */
	public boolean isAnyFormat() {
		return ((getFlags() & SDLVideo.SDL_ANYFORMAT) == SDLVideo.SDL_ANYFORMAT);
	}

	/**
	 * Get if this surface has exclusive palette
	 *
	 * @return if this surface has exclusive palette
	 */
	public boolean isHardwarePalette() {
		return ((getFlags() & SDLVideo.SDL_HWPALETTE) == SDLVideo.SDL_HWPALETTE);
	}

	/**
	 * Get if this surface is double buffered
	 *
	 * @return if this surface is double buffered
	 */
	public boolean isDoubleBuffered() {
		return ((getFlags() & SDLVideo.SDL_DOUBLEBUF) == SDLVideo.SDL_DOUBLEBUF);
	}

	/**
	 * Get if this surface is a full screen surface
	 *
	 * @return if this surface is a full screen surface
	 */
	public boolean isFullScreen() {
		return ((getFlags() & SDLVideo.SDL_FULLSCREEN) == SDLVideo.SDL_FULLSCREEN);
	}

	/**
	 * Get if this surface has an OpenGL context (Display Surface)
	 *
	 * @return if this surface has an OpenGL context (Display Surface)
	 */
	public boolean isOpenGL() {
		return ((getFlags() & SDLVideo.SDL_OPENGL) == SDLVideo.SDL_OPENGL);
	}

	/**
	 * Get if this surface supports OpenGL blitting
	 *
	 * @return if this surface supports OpenGL blitting
	 */
	public boolean isOpenGLBlit() {
		return ((getFlags() & SDLVideo.SDL_OPENGLBLIT) == SDLVideo.SDL_OPENGLBLIT);
	}

	/**
	 * Get if this surface is resizable
	 *
	 * @return if this surface is resizable
	 */
	public boolean isResizable() {
		return ((getFlags() & SDLVideo.SDL_RESIZABLE) == SDLVideo.SDL_RESIZABLE);
	}

	/**
	 * Get if this window has no window caption or edge frame
	 *
	 * @return if this window has no window caption or edge frame
	 */
	public boolean isNoFrame() {
		return ((getFlags() & SDLVideo.SDL_NOFRAME) == SDLVideo.SDL_NOFRAME);
	}

	/**
	 * Get if this surface's blit uses hardware acceleration
	 *
	 * @return if this surface's blit uses hardware acceleration
	 */
	public boolean isHardwareAccelerated() {
		return ((getFlags() & SDLVideo.SDL_HWACCEL) == SDLVideo.SDL_HWACCEL);
	}

	/**
	 * Get if this surface uses colorkey blitting
	 *
	 * @return if this surface uses colorkey blitting
	 */
	public boolean isColorKeyBlit() {
		return ((getFlags() & SDLVideo.SDL_SRCCOLORKEY) == SDLVideo.SDL_SRCCOLORKEY);
	}

	/**
	 * Get if colorkey blitting is acceleration with RLE
	 *
	 * @return if colorkey blitting is acceleration with RLE
	 */
	public boolean isRLEAccelerated() {
		return ((getFlags() & SDLVideo.SDL_RLEACCEL) == SDLVideo.SDL_RLEACCEL);
	}

	/**
	 * Get if this surface's blit uses alpha blending
	 *
	 * @return a <code>boolean</code> value
	 */
	public boolean isSrcAlphaBlit() {
		return ((getFlags() & SDLVideo.SDL_SRCCOLORKEY) == SDLVideo.SDL_SRCALPHA);
	}

	/**
	 * Get if this surface uses pre-allocated memory
	 *
	 * @return if this surface uses pre-allocated memory
	 */
	public boolean isPreAlloc() {
		return ((getFlags() & SDLVideo.SDL_PREALLOC) == SDLVideo.SDL_PREALLOC);
	}

	/**
	 * Maps an RGB triple to an opaque pixel value for a given pixel format
	 *
	 * If the format has a palette (8-bit) the index of the closest matching color in the palette will be returned.
	 * 
	 * If the specified pixel format has an alpha component it will be returned as all 1 bits (fully opaque).
	 *
	 * @param r
	 * @param g
	 * @param b
	 *
	 * @return A pixel value best approximating the given RGB color
	 * value for a given pixel format.
	 * <P>If the pixel format bpp (color
	 * depth) is less than 32-bpp then the unused upper bits of the
	 * return value can safely be ignored (e.g., with a 16-bpp format
	 * the return value can be assigned to a Uint16, and similarly a
	 * Uint8 for an 8-bpp format).
	 * @exception SDLException If an error occurs
	 */
	public long mapRGB(int r, int g, int b) throws SDLException {
		return SDLVideo.mapRGB(getFormat(), r, g, b);
	}

	/**
	 * Maps an RGBA quadruple to a pixel value for a given pixel format
	 *
	 * If the format has a palette (8-bit) the index of the closest
	 * matching color in the palette will be returned.
	 * <P>
	 * If the specified pixel format has no alpha component the alpha value
	 * will be ignored (as it will be in formats with a palette).
	 *
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 *
	 * @return A pixel value best approximating the given RGBA color
	 * value for a given pixel format. If the pixel format bpp (color
	 * depth) is less than 32-bpp then the unused upper bits of the
	 * return value can safely be ignored (e.g., with a 16-bpp format
	 * the return value can be assigned to a Uint16, and similarly a
	 * Uint8 for an 8-bpp format).
	 * @exception SDLException If an error occurs
	 */
	public long mapRGBA(int r, int g, int b, int a) throws SDLException {
		return SDLVideo.mapRGBA(getFormat(), r, g, b, a);
	}

	/**
	 * Returns a <I>direct</I> byte buffer which referes to the pixel data of this surface
	 *
	 * @return a <code>ByteBuffer</code> value
	 */
//	public ByteBuffer getPixelData() {
//		if (pixelData == null) {
//			pixelData = (ByteBuffer) SWIG_SDLVideo.SWIG_getPixelDirectByteBuffer(swigSurface);
//		}
//
//		return pixelData;
//	}

	/**
	 * Get the pixel data as a long array
	 *
	 * @return a <code>long[]</code> value
	 */
	public int[] getPixelData32() {
		int pixelData[] = new int[getWidth() * getHeight()];
		SWIG_SDLVideo.SWIG_GetPixelData32(this.getSwigSurface(), pixelData);
		return pixelData;
	}

	/**
	 * Get the pixel data as an int array
	 *
	 * @return an <code>int[]</code> value
	 */
	public int[] getPixelData16() {
		int[] pixelData = new int[getWidth() * getHeight()];
		SWIG_SDLVideo.SWIG_GetPixelData16(this.getSwigSurface(), pixelData);
		return pixelData;
	}

	/**
	 * Get the pixel data as a short array
	 *
	 * @return a <code>short[]</code> value
	 */
	public short[] getPixelData8() {
		short[] pixelData = new short[getWidth() * getHeight()];
		SWIG_SDLVideo.SWIG_GetPixelData8(this.getSwigSurface(), pixelData);
		return pixelData;
	}

	/**
	 * Set the pixel data from the given long array
	 *
	 * @param pixelData a <code>long[]</code> value
	 */
	public void setPixelData32(int[] pixelData) {
		SWIG_SDLVideo.SWIG_SetPixelData32(this.getSwigSurface(), pixelData);
	}

	/**
	 * Set the pixel data from the given int array
	 *
	 * @param pixelData an <code>int[]</code> value
	 */
	public void setPixelData16(int[] pixelData) {
		SWIG_SDLVideo.SWIG_SetPixelData16(this.getSwigSurface(), pixelData);
	}

	/**
	 * Set the pixel data from the given short array
	 *
	 * @param pixelData a <code>short[]</code> value
	 */
	public void setPixelData8(short[] pixelData) {
		SWIG_SDLVideo.SWIG_SetPixelData8(this.getSwigSurface(), pixelData);
	}

	/**
	 * Frees (deletes) an SDL_Surface
	 * <P>
	 * Frees the resources used by a previously created SDL_Surface.
	 * If the surface was created using SDL_CreateRGBSurfaceFrom then the
	 * pixel data is not freed.
	 *
	 * @exception SDLException if an error occurs
	 */
	public void freeSurface() throws SDLException {
		swigSurface = null;
		//pixelData = null;
	}

	/**
	 * Toggles fullscreen mode
	 * <P>
	 * Toggles the application between windowed and fullscreen mode,
	 * if supported. (X11 is the only target currently supported, BeOS
	 * support is experimental).
	 *
	 * @return If the toggle was successfull
	 */
	public boolean wmToggleFullScreen() {
		return SWIG_SDLVideo.SDL_WM_ToggleFullScreen(swigSurface) == 1;
	}

	/**
	 * SWAP The GL Buffer
	 *
	 */
	public void glSwapBuffers() {
		SWIG_SDLVideo.SDL_GL_SwapBuffers();
	}

	/**
	 * Get if the surface needs to be locked before access
	 *
	 * @return if the surface needs to be locked before access
	 */
	public boolean mustLock() {
		return SWIG_SDLVideo.SWIG_SDL_MUSTLOCK(swigSurface) == 1;
	}

	/**
	 * Returns an SDLRect which has the same size as this surface, but has
	 * topleft x and y at the given coordinates.
	 * 
	 * @param x The x coordiante of the created SDLRect.
	 * @param y The y coordiante of the created SDLRect.
	 * 
	 * @return An SDLRect which has the given x and y, but the width and height
	 *         of the surface.
	 */
	public SDLRect getRect(int x, int y) {
		return new SDLRect(x, y, getWidth(), getHeight());
	}

	/**
	 * Returns an SDLRect which has the same size as this surface, but has
	 * topleft x and y at 0.
	 * 
	 * @return An SDLRect which has the x and y at 0, and the width and height
	 *         of the surface.
	 */
	public SDLRect getRect() {
		return getRect(0, 0);
	}

	/**
	 * Returns an SDLRect which has the same size as this surface, but has
	 * topleft x and y at the position defined by the given SDLPoint instance.
	 * 
	 * @param pos An SDLPoint which defines the topleft position of the new
	 *            SDLRect.
	 * 
	 * @return An SDLRect that has the topleft position defined in the SDLPoint.
	 * 
	 * @see SDLPoint
	 */
	public SDLRect getRect(SDLPoint pos) {
		return getRect(pos.getX(), pos.getY());
	}

	/**
	 * Free SDL_Surface (native world) data.
	 *
	 */
	protected void finalize() {
		try {
			if (swigSurface != null)
				freeSurface();
		} catch (SDLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Return a string represenation of this object
	 *
	 * @return a String represenation of this object
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();

		SDLPixelFormat format = getFormat();

		buf.append("SDLSurface[").append("flags=").append(getFlags()).append(", width=").append(getWidth()).append(
				", height=").append(getHeight()).append(", pitch=").append(getPitch()).append(", format=").append(
				format == null ? "(NO PIXELFORMAT)" : format.toString()).append("]");

		return buf.toString();
	}

} // class SDLSurface
