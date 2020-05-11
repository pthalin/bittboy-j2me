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
import java.util.Hashtable;

//import org.gljava.opengl.x.swig.GlewJNI;

import sdljava.SDLException;
import sdljava.SDLMain;
import sdljava.x.swig.SDL_Overlay;
import sdljava.x.swig.SDL_Surface;
import sdljava.x.swig.SDL_VideoInfo;
import sdljava.x.swig.SWIG_SDLVideo;
import sdljava.x.swig.SWIG_SDLVideoConstants;

/**
 * Please see the SDL Documention project page <a href="http://sdldoc.csn.ul.ie/video.php">here</A>
 * 
 * <P>
 * NOTE:  The following methods are not yet implemented:
 * <UL>
 *   <LI>SDL_XXX_YUV* functions (Overlay support is planned but not yet implemented) </LI>
 * </UL>
 *
 *  @see sdljava.video.SDLSurface
 *  @see sdljava.video.SDLPixelFormat
 *  @author Ivan Z. Ganza (ivan_ganza at yahoo.com)
 */
public class SDLVideo {
	/**
	 * Transparency definitions: These define alpha as the opacity of a surface
	 *
	 */
	public static final int SDL_ALPHA_OPAQUE = SWIG_SDLVideoConstants.SDL_ALPHA_OPAQUE;

	/**
	 * Transparency definitions: These define alpha as the opacity of a surface
	 *
	 */
	public static final int SDL_ALPHA_TRANSPARENT = SWIG_SDLVideoConstants.SDL_ALPHA_TRANSPARENT;

	/**
	 * Surface is stored in system memory
	 *
	 */
	public static final long SDL_SWSURFACE = SWIG_SDLVideoConstants.SDL_SWSURFACE;

	/**
	 * Surface is stored in video memory
	 *
	 */
	public static final long SDL_HWSURFACE = SWIG_SDLVideoConstants.SDL_HWSURFACE;

	/**
	 * Surface uses asynchronous blits if possible
	 *
	 */
	public static final long SDL_ASYNCBLIT = SWIG_SDLVideoConstants.SDL_ASYNCBLIT;

	/**
	 * Allows any pixel-format (Display surface)
	 *
	 */
	public static final long SDL_ANYFORMAT = SWIG_SDLVideoConstants.SDL_ANYFORMAT;

	/**
	 * Surface has exclusive palette
	 *
	 */
	public static final long SDL_HWPALETTE = SWIG_SDLVideoConstants.SDL_HWPALETTE;

	/**
	 * Surface is double buffered (Display surface)
	 *
	 */
	public static final long SDL_DOUBLEBUF = SWIG_SDLVideoConstants.SDL_DOUBLEBUF;

	/**
	 * Surface is full screen (Display Surface)
	 *
	 */
	public static final long SDL_FULLSCREEN = SWIG_SDLVideoConstants.SDL_FULLSCREEN;

	/**
	 * Surface has an OpenGL context (Display Surface)
	 *
	 */
	public static final long SDL_OPENGL = SWIG_SDLVideoConstants.SDL_OPENGL;

	/**
	 * Surface supports OpenGL blitting (Display Surface). NOTE: This
	 * option is kept for compatibility only, and is not recommended
	 * for new code.
	 *
	 */
	public static final long SDL_OPENGLBLIT = SWIG_SDLVideoConstants.SDL_OPENGLBLIT;

	/**
	 * Surface is resizable (Display Surface)
	 *
	 */
	public static final long SDL_RESIZABLE = SWIG_SDLVideoConstants.SDL_RESIZABLE;

	/**
	 * No window caption or edge frame
	 *
	 */
	public static final long SDL_NOFRAME = SWIG_SDLVideoConstants.SDL_NOFRAME;

	/**
	 * Surface blit uses hardware acceleration
	 *
	 */
	public static final long SDL_HWACCEL = SWIG_SDLVideoConstants.SDL_HWACCEL;

	/**
	 * Surface use colorkey blitting
	 *
	 */
	public static final long SDL_SRCCOLORKEY = SWIG_SDLVideoConstants.SDL_SRCCOLORKEY;

	/**
	 * Colorkey blitting is accelerated with RLE
	 *
	 */
	public static final long SDL_RLEACCEL = SWIG_SDLVideoConstants.SDL_RLEACCEL;

	/**
	 * Surface blit uses alpha blending
	 *
	 */
	public static final long SDL_SRCALPHA = SWIG_SDLVideoConstants.SDL_SRCALPHA;

	/**
	 * Surface uses preallocated memory
	 *
	 */
	public static final long SDL_PREALLOC = SWIG_SDLVideoConstants.SDL_PREALLOC;

	/** Is the cursor visible? 
	 * @see #showCursor(int) */
	public static final int SDL_QUERY = -1;
	/** Hide the cursor.
	 * @see #showCursor(int) */
	public static final int SDL_DISABLE = 0;
	/** Show the cursor
	 * @see #showCursor(int) */
	public static final int SDL_ENABLE = 1;

	private SDLVideo() {
	}

	static boolean didInitOpenGL = false;

	/**
	 * This function returns the current display surface.
	 * If SDL is doing format conversion on the display surface, this
	 * function returns the publicly visible surface, not the real video
	 * surface.
	 *
	 * @return The current display surface
	 * @exception SDLException If an error occurs
	 */
	public static SDLSurface getVideoSurface() throws SDLException {
		SDL_Surface swigSurface = SWIG_SDLVideo.SDL_GetVideoSurface();
		if (swigSurface == null) {
			throw new SDLException(SDLMain.getError());
		}
		return new SDLSurface(swigSurface);
	}

	/**
	 * This function returns information about the
	 * video hardware.  If this is called before SDL_SetVideoMode(), the 'vfmt'
	 * member of the returned structure will contain the pixel format of the
	 * "best" video mode.
	 * @return Information about the Video Mode
	 * @exception SDLException If an error occurs
	 */
	public static SDLVideoInfo getVideoInfo() throws SDLException {
		SDL_VideoInfo swigInfo = SWIG_SDLVideo.SDL_GetVideoInfo();
		if (swigInfo == null) {
			throw new SDLException(SDLMain.getError());
		}
		return new SDLVideoInfo(swigInfo);
	}

	/**
	 * Obtain the name of the video driver
	 *
	 * @return The name of the video driver or NULL if no driver has been initialized.
	 * @exception SDLException If an error occurs
	 */
	public static String videoDriverName() throws SDLException {
		StringBuffer name = new StringBuffer(1024);
		return SWIG_SDLVideo.SDL_VideoDriverName(name.toString(), 1024);
	}

	/**
	 * Return a list of available video modes(screen dimensions) for the
	 * given format and video flags, sorted largest to smallest.  Returns 
	 * NULL if there are no dimensions available for a particular format, 
	 * or (SDL_Rect **)-1 if any dimension is okay for the given format.
	 *
	 * If 'format' is NULL, the mode list will be for the format given 
	 * by SDL_GetVideoInfo()->vfmt
	 * * @exception SDLException If an error occurs
	 */
	//public static native List<SDLVideoMode> listModes(SDLPixelFormat format, int flags) throws SDLException;
	/** 
	 * Check to see if a particular video mode is supported.
	 * It returns 0 if the requested mode is not supported under any bit depth,
	 * or returns the bits-per-pixel of the closest available mode with the
	 * given width and height.  If this bits-per-pixel is different from the
	 * one used when setting the video mode, SDL_SetVideoMode() will succeed,
	 * but will emulate the requested bits-per-pixel with a shadow surface.
	 *
	 * The arguments to SDL_VideoModeOK() are the same ones you would pass to
	 * SDL_SetVideoMode()
	 * 
	 * @param width  The desired width
	 * @param height The desired height
	 * @param bpp    The desired bits per pixel
	 * @param flags  The desired flags
	 * @return 0 if the requested mode is not supported under any bit depth,
	 * or returns the bits-per-pixel of the closest available mode with the
	 * given width and height.  If this bits-per-pixel is different from the
	 * one used when setting the video mode, SDL_SetVideoMode() will succeed,
	 * but will emulate the requested bits-per-pixel with a shadow surface.
	 * @exception SDLException If an error occurs
	 */
	public static int videoModeOK(int width, int height, int bpp, int flags) throws SDLException {
		return SWIG_SDLVideo.SDL_VideoModeOK(width, height, bpp, flags);
	}

	/**
	 * Same as videoModeOK(int,int,int,int) but gets the width, height, BPP,
	 * and flag values from the SDLScreenMode instance instead.
	 * 
	 * @author <a href="Dessimat0r@ntlworld.com">Chris Dennett</a>
	 * @param  videoMode The video mode to test
	 * @return 0 if the requested mode is not supported under any bit depth,
	 * or returns the bits-per-pixel of the closest available mode with the
	 * given width and height.  If this bits-per-pixel is different from the
	 * one used when setting the video mode, SDL_SetVideoMode() will succeed,
	 * but will emulate the requested bits-per-pixel with a shadow surface.
	 * @exception SDLException If an error occurs
	 */
	public static int videoModeOK(SDLVideoMode videoMode) throws SDLException {
		return videoModeOK(videoMode.getWidth(), videoMode.getHeight(), videoMode.getBpp(), videoMode.getFlags());
	}

	/**
	 * Set up a video mode with the specified width, height and bits-per-pixel.
	 * <P>
	 * If 'bpp' is 0, it is treated as the current display bits per pixel.
	 * <P>
	 * If SDL_ANYFORMAT is set in 'flags', the SDL library will try to set the
	 * requested bits-per-pixel, but will return whatever video pixel format is
	 * available.  The default is to emulate the requested pixel format if it
	 * is not  natively available.
	 * <P>
	 * If SDL_HWSURFACE is set in 'flags', the video surface will be placed in
	 * video memory, if possible, and you may have to call SDL_LockSurface()
	 * in order to access the raw framebuffer.  Otherwise, the video surface
	 * will be created in system memory.
	 * <P>
	 * If SDL_ASYNCBLIT is set in 'flags', SDL will try to perform rectangle
	 * updates asynchronously, but you must always lock before accessing pixels.
	 * SDL will wait for updates to complete before returning from the lock.
	 * <P>
	 * If SDL_HWPALETTE is set in 'flags', the SDL library will guarantee
	 * that the colors set by SDL_SetColors() will be the colors you get.
	 * Otherwise, in 8-bit mode, SDL_SetColors() may not be able to set all
	 * of the colors exactly the way they are requested, and you should look
	 * at the video surface structure to determine the actual palette.
	 * If SDL cannot guarantee that the colors you request can be set, 
	 * i.e. if the colormap is shared, then the video surface may be created
	 * under emulation in system memory, overriding the SDL_HWSURFACE flag.
	 * <P>
	 * If SDL_FULLSCREEN is set in 'flags', the SDL library will try to set
	 * a fullscreen video mode.  The default is to create a windowed mode
	 * if the current graphics system has a window manager.
	 * If the SDL library is able to set a fullscreen video mode, this flag 
	 * will be set in the surface that is returned.
	 * <P>
	 * If SDL_DOUBLEBUF is set in 'flags', the SDL library will try to set up
	 * two surfaces in video memory and swap between them when you call 
	 * SDL_Flip().  This is usually slower than the normal single-buffering
	 * scheme, but prevents "tearing" artifacts caused by modifying video 
	 * memory while the monitor is refreshing.  It should only be used by 
	 * applications that redraw the entire screen on every update.
	 * <P>
	 * If SDL_RESIZABLE is set in 'flags', the SDL library will allow the
	 * window manager, if any, to resize the window at runtime.  When this
	 * occurs, SDL will send a SDL_VIDEORESIZE event to you application,
	 * and you must respond to the event by re-calling SDL_SetVideoMode()
	 * with the requested size (or another size that suits the application).
	 * <P>
	 * If SDL_NOFRAME is set in 'flags', the SDL library will create a window
	 * without any title bar or frame decoration.  Fullscreen video modes have
	 * this flag set automatically.
	 * <P>
	 * This function returns the video framebuffer surface, or NULL if it fails.
	 * <P>
	 * If you rely on functionality provided by certain video flags, check the
	 * flags of the returned surface to make sure that functionality is available.
	 * SDL will fall back to reduced functionality if the exact flags you wanted
	 * are not available.
	 *
	 * @param width  desired width
	 * @param height desired height
	 * @param bpp    bits per pixel or 0 to use the current display bits per pixel
	 * @param flags The flags parameter is the same as the flags field
	 * of the SDL_Surface structure.
	 * @return The framebuffer surface
	 * @exception SDLException If an error occurs
	 */
	public static SDLSurface setVideoMode(int width, int height, int bpp, long flags) throws SDLException {
		SDL_Surface s = SWIG_SDLVideo.SDL_SetVideoMode(width, height, bpp, flags);
		if (s == null) {
			throw new SDLException(SDLMain.getError());
		}

//		SDLSurface surface = new SDLSurface(s);
//		if (surface.isOpenGL()) {// init OpenGL Binding for the caller
//			if (!didInitOpenGL) {
//				GlewJNI.SWIG_glew_init();
//				didInitOpenGL = true;
//			}
//		}
		return new SDLSurface(s);
	}

	/**
	 * Same as setVideoMode(int,int,int,int) but gets the width, height, BPP,
	 * and flag values from the SDLVideoMode instance instead.
	 * 
	 * @author <a href="Dessimat0r@ntlworld.com">Chris Dennett</a>
	 * @param videoMode The video mode to set
	 * @return The framebuffer surface
	 * @throws SDLException If an error occurs
	 */
	public static SDLSurface setVideoMode(SDLVideoMode videoMode) throws SDLException {
		return setVideoMode(videoMode.getWidth(), videoMode.getHeight(), videoMode.getBpp(), videoMode.getFlags());
	}

	/**
	 * Set the gamma correction for each of the color channels.
	 * The gamma values range (approximately) between 0.1 and 10.0
	 * 
	 * If this function isn't supported directly by the hardware, it will
	 * be emulated using gamma ramps, if available.
	 *
	 * @param red   The red value
	 * @param green The green value
	 * @param blue  The blue value
	 * @exception SDLException If an error occurs
	 */
	public static void setGamma(float red, float green, float blue) throws SDLException {
		int result = SWIG_SDLVideo.SDL_SetGamma(red, green, blue);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}
	}

	/**
	 * Retrieve the current values of the gamma translation tables.
	 * 
	 * You must pass in valid pointers to arrays of 256 16-bit quantities.
	 * Any of the pointers may be NULL to ignore that channel.
	 * If the call succeeds, it will return 0.  If the display driver or
	 * hardware does not support gamma translation, or otherwise fails,
	 * this function will return -1.
	 *
	 * @return The current values of the gamma translation tables.
	 *
	 * @exception SDLException If an error occurs
	 */
	public static GammaTable getGammaRamp() throws SDLException {
		int red[] = new int[256];
		int green[] = new int[256];
		int blue[] = new int[256];

		int result = SWIG_SDLVideo.SDL_GetGammaRamp(red, green, blue);
		if (result == -1) {
			throw new SDLException(SDLMain.getError());
		}

		return new GammaTable(red, green, blue);
	}

	/**
	 * Set the gamma translation table for the red, green, and blue channels
	 * of the video hardware.  Each table is an array of 256 16-bit quantities,
	 * representing a mapping between the input and output for that channel.
	 * The input is the index into the array, and the output is the 16-bit
	 * gamma value at that index, scaled to the output color precision.
	 * 
	 * You may pass NULL for any of the channels to leave it unchanged.
	 * If the call succeeds, it will return 0.  If the display driver or
	 * hardware does not support gamma translation, or otherwise fails,
	 * this function will return -1.
	 * @param red an <code>int[]</code> value
	 * @param green an <code>int[]</code> value
	 * @param blue an <code>int[]</code> value
	 * @return a <code>boolean</code> value
	 * @exception SDLException If an error occurs
	 */
	public static void setGammaRamp(int[] red, int[] green, int[] blue) throws SDLException {
		int result = SWIG_SDLVideo.SWIG_SDL_SetGammaRamp(red, green, blue);
		if (result == -1) {
			String error = SDLMain.getError();
			throw new SDLException(
					error == null ? "Failed but SDL did not set error.  Assuming gamma adjustment is not supported."
							: error);
		}
	}

	/**
	 * Maps an RGB triple to an opaque pixel value for a given pixel format
	 *
	 * If the format has a palette (8-bit) the index of the closest matching color in the palette will be returned.
	 * 
	 * If the specified pixel format has an alpha component it will be returned as all 1 bits (fully opaque).
	 *
	 * @param format
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
	public static long mapRGB(SDLPixelFormat format, int r, int g, int b) throws SDLException {
		return SWIG_SDLVideo.SDL_MapRGB(format.getSwigPixelFormat(), (short) r, (short) g, (short) b);
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
	 * @param format
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
	public static long mapRGBA(SDLPixelFormat format, int r, int g, int b, int a) throws SDLException {
		return SWIG_SDLVideo.SDL_MapRGBA(format.getSwigPixelFormat(), (short) r, (short) g, (short) b, (short) a);
	}

	/**
	 * Maps a pixel value into the RGB components for a given pixel format
	 * 
	 * This function uses the entire 8-bit [0..255] range when
	 * converting color components from pixel formats with less than
	 * 8-bits per RGB component (e.g., a completely white pixel in
	 * 16-bit RGB565 format would return [0xff, 0xff, 0xff] not [0xf8,
	 * 0xfc, 0xf8]).
	 *
	 * @param pixel Pixel to get color value for
	 * @param fmt   The pixel format to use
	 * @return      The color information
	 * @exception SDLException If an error occurs
	 */
	public static SDLColor getRGB(int pixel, SDLPixelFormat fmt) throws SDLException {
		short[] r = { 0 };
		short[] g = { 0 };
		short[] b = { 0 };
		SWIG_SDLVideo.SDL_GetRGB(pixel, fmt.getSwigPixelFormat(), r, g, b);
		return new SDLColor(r[0], g[0], b[0]);
	}

	/**
	 * Maps a pixel value into the RGBA components for a given pixel format
	 * 
	 * This function uses the entire 8-bit [0..255] range when
	 * converting color components from pixel formats with less than
	 * 8-bits per RGB component (e.g., a completely white pixel in
	 * 16-bit RGB565 format would return [0xff, 0xff, 0xff] not [0xf8,
	 * 0xfc, 0xf8]).
	 * <P>
	 * If the surface has no alpha component, the alpha will be returned as 0xff (100% opaque).
	 *
	 * @param pixel Pixel to get color value for
	 * @param fmt   The pixel format to use
	 * @return      The color information
	 * @exception SDLException If an error occurs
	 */
	public static SDLColor getRGBA(int pixel, SDLPixelFormat fmt) throws SDLException {
		short[] r = { 0 };
		short[] g = { 0 };
		short[] b = { 0 };
		short[] a = { 0 };
		SWIG_SDLVideo.SDL_GetRGBA(pixel, fmt.getSwigPixelFormat(), r, g, b, a);
		return new SDLColor(r[0], g[0], b[0], a[0]);
	}

	/**
	 * Allocate and free an RGB surface (must be called after SDL_SetVideoMode)
	 * If the depth is 4 or 8 bits, an empty palette is allocated for the surface.
	 * If the depth is greater than 8 bits, the pixel format is set using the
	 * flags '[RGB]mask'.
	 * If the function runs out of memory, it will return NULL.
	 * <P>
	 * The 'flags' tell what kind of surface to create.
	 * SDL_SWSURFACE means that the surface should be created in system memory.
	 * <P>
	 * SDL_HWSURFACE means that the surface should be created in video memory,
	 * with the same format as the display surface.  This is useful for surfaces
	 * that will not change much, to take advantage of hardware acceleration
	 * when being blitted to the display surface.
	 * <P>
	 * SDL_ASYNCBLIT means that SDL will try to perform asynchronous blits with
	 * this surface, but you must always lock it before accessing the pixels.
	 * SDL will wait for current blits to finish before returning from the lock.
	 * <P>
	 * SDL_SRCCOLORKEY indicates that the surface will be used for colorkey blits.
	 * If the hardware supports acceleration of colorkey blits between
	 * two surfaces in video memory, SDL will try to place the surface in
	 * video memory. If this isn't possible or if there is no hardware
	 * acceleration available, the surface will be placed in system memory.
	 * <P>
	 * SDL_SRCALPHA means that the surface will be used for alpha blits and 
	 * if the hardware supports hardware acceleration of alpha blits between
	 * two surfaces in video memory, to place the surface in video memory
	 * if possible, otherwise it will be placed in system memory.
	 * <P>
	 * If the surface is created in video memory, blits will be _much_ faster,
	 * but the surface format must be identical to the video surface format,
	 * and the only way to access the pixels member of the surface is to use
	 * the SDL_LockSurface() and SDL_UnlockSurface() calls.
	 * <P>
	 * If the requested surface actually resides in video memory, SDL_HWSURFACE
	 * will be set in the flags member of the returned surface.  If for some
	 * reason the surface could not be placed in video memory, it will not have
	 * the SDL_HWSURFACE flag set, and will be created in system memory instead.
	 * 
	 * @param flags  Flags for this surface
	 * @param height Desired width
	 * @param height Desired height
	 * @param depth  Desidred depth (bits per pixel)
	 * @param rMask  Red Mask
	 * @param gMask  Green Mask
	 * @param bMask  Blue Mask
	 * @param aMask  Alpha Mask
	 * @return       The newly created surface
	 * @exception SDLException If an error occurs
	 */
	public static SDLSurface createRGBSurface(long flags, int width, int height, int depth, long rMask, long gMask,
			long bMask, long aMask) throws SDLException {
		SDL_Surface swigSurface = SWIG_SDLVideo.SDL_CreateRGBSurface(flags, width, height, depth, rMask, gMask, bMask,
				aMask);
		if (swigSurface == null) {
			throw new SDLException(SDLMain.getError());
		}
		return new SDLSurface(swigSurface);
	}

	/**
	 * Create an SDL_Surface from pixel data
	 * <P>
	 * Creates an SDL_Surface from the provided pixel data.
	 * <P>
	 * The data stored in pixels is assumed to be of the depth specified in
	 * the parameter list. The pixel data is not copied into the SDL_Surface
	 * structure so it should not be freed until the surface has been freed
	 * with a called to SDL_FreeSurface. pitch is the length of each scanline
	 * in bytes.
	 * <P>
	 * See SDL_CreateRGBSurface for a more detailed description of the other parameters. 
	 *
	 * @param pixels an <code>int[]</code> value
	 * @param width an <code>int</code> value
	 * @param height an <code>int</code> value
	 * @param depth an <code>int</code> value
	 * @param pitch an <code>int</code> value
	 * @param rMask an <code>int</code> value
	 * @param gMask an <code>int</code> value
	 * @param bMask an <code>int</code> value
	 * @param aMask an <code>int</code> value
	 * @return a <code>SDLSurface</code> value
	 * @exception SDLException if an error occurs
	 */
	public static SDLSurface createRGBSurfaceFrom(int[] pixels, int width, int height, int depth, int pitch,
			long rMask, long gMask, long bMask, long aMask) throws SDLException {
		SDL_Surface swigSurface = SWIG_SDLVideo.SWIG_SDL_CreateRGBSurfaceFrom(pixels, width, height, depth, pitch,
				rMask, gMask, bMask, aMask);
		if (swigSurface == null) {
			throw new SDLException(SDLMain.getError());
		}
		return new SDLSurface(swigSurface);
	}

	/**
	 * Load a surface from a BMP file located at path.
	 *
	 * @param path The filesystem path of the file with the image data in BMP format
	 * @return the new surface
	 * @exception SDLException If an error occurs
	 */
	public static SDLSurface loadBMP(String path) throws SDLException {
		SDL_Surface swigSurface = SWIG_SDLVideo.SWIG_SDL_LoadBMP(path);
		if (swigSurface == null) {
			throw new SDLException(SDLMain.getError());
		}
		return new SDLSurface(swigSurface);
	}

	/**
	 * Set the position of the mouse cursor (generates a mouse motion event).
	 *
	 * @param x The x co-ordinate
	 * @param y The y co-ordinate
	 * @exception SDLException If an error occurs
	 */
	public static void warpMouse(int x, int y) throws SDLException {
		SWIG_SDLVideo.SDL_WarpMouse(x, y);
	}
	
	/**
	 * Toggle whether or not the cursor is shown on the screen. 
	 * Passing SDL_ENABLE displays the cursor and passing SDL_DISABLE hides it. 
	 * The current state of the mouse cursor can be queried by passing SDL_QUERY, either SDL_DISABLE or SDL_ENABLE will be returned.<br>
     * The cursor starts off displayed, but can be turned off. 
	 * @param toggle 
	 * @exception SDLException If an error occurs
	 */
	public static void showCursor(int toggle) throws SDLException {
		SWIG_SDLVideo.SDL_ShowCursor(toggle);
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	/* These functions allow interaction with the window manager, if any.        */
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	/**
	 * Sets the window tile and icon name.
	 *
	 * @param title the window title
	 * @param icon  the icon name
	 */
	public static void wmSetCaption(String title, String icon) {
		SWIG_SDLVideo.SDL_WM_SetCaption(title, icon);
	}

	/**
	 * Gets the window tile and icon name.
	 *
	 * 
	 * @return A HashMap with the the keys title and icon set to the
	 *         title and icon
	 */
	public static Hashtable wmGetCaption() {
		String title = "";
		String icon = "";

		SWIG_SDLVideo.SWIG_SDL_WM_GetCaption(title, icon);

		Hashtable m = new Hashtable();
		m.put("title", title);
		m.put("icon", icon);

		return m;
	}

	/**
	 * Sets the icon for the display window.
	 * <P>
	 * Sets the icon for the display window. Win32 icons must be 32x32.
	 * <P>
	 * This function must be called before the first call to SDL_SetVideoMode.
	 * <P>
	 *      The mask is a bitmask that describes the shape of the icon. If mask is
	 *      NULL, the shape is determined by the colorkey or alpha channel of the
	 *      icon, if any. If neither of those are present, the icon is made opaque
	 *      (no transparency).
	 * <P>
	 *      If mask is non-NULL, it points to a bitmap with bits set where the
	 *      corresponding pixel should be visible. The format of the bitmap is as
	 *      follows: Scanlines come in the usual top-down order. Each scanline
	 *      consists of (width / 8) bytes, rounded up. The most significant bit of
	 *      each byte represents the leftmost pixel. Example
	 * <P>
	 *      SDL_WM_SetIcon(SDL_LoadBMP("icon.bmp"), NULL);
	 * <P>
	 * <I><B>Note:  using the mask param is currently not supported</B></I>
	 *
	 * @param icon a <code>SDLSurface</code> value
	 * @param mask a <code>short</code> value
	 */
	public static void wmSetIcon(SDLSurface icon, short mask) {
		SWIG_SDLVideo.SDL_WM_SetIcon(icon.getSwigSurface(), mask);
	}

	/**
	 * Iconify/Minimise the window
	 * <P>
	 * If the application is running in a window managed environment
	 * SDL attempts to iconify/minimise it. If SDL_WM_IconifyWindow is
	 * successful, the application will receive a SDL_APPACTIVE loss
	 * event (see SDL_ActiveEvent).
	 *
	 * @return If the window was iconifies
	 */
	public static boolean wmIconifyWindow() {
		return SWIG_SDLVideo.SDL_WM_IconifyWindow() != 0;
	}

	/**
	 * Grabs mouse and keyboard input.
	 * <P>
	 * Grabbing means that the mouse is confined to the application
	 * window, and nearly all keyboard input is passed directly to the
	 * application, and not interpreted by a window manager, if any.
	 * <P>
	 * When mode is SDL_GRAB_QUERY the grab mode is not changed, but the current grab mode is returned. 
	 * @param mode a <code>SDLGrabMode</code> value
	 * @return a <code>SDLGrabMode</code> value
	 */
	public static SDLGrabMode wmGrabInput(SDLGrabMode mode) {
		return SDLGrabMode.swigToEnum(SWIG_SDLVideo.SDL_WM_GrabInput(mode.swigValue()));
	}

//	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
//	/* YUV video surface overlay functions                                       */
//	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
//
//	/**
//	 * Describe <code>createYUVOverlay</code> method here.
//	 *
//	 * @param width an <code>int</code> value
//	 * @param height an <code>int</code> value
//	 * @param format an <code>int</code> value
//	 * @param display a <code>SDLSurface</code> value
//	 * @return a <code>SDLOverlay</code> value
//	 * @exception SDLException if an error occurs
//	 */
//	public static SDLOverlay createYUVOverlay(int width, int height, int format, SDLSurface display)
//			throws SDLException {
//		SDL_Overlay overlay = SWIG_SDLVideo.SDL_CreateYUVOverlay(width, height, format, display.getSwigSurface());
//		if (overlay != null) {
//			return new SDLOverlay(overlay);
//		}
//		throw new SDLException(SDLMain.getError());
//	}
//
//	public static int lockYUVOVerlay(SDLOverlay overlay) {
//		return SWIG_SDLVideo.SDL_LockYUVOverlay(overlay.getSwigOverlay());
//	}
//
//	public static int displayYUVOverlay(SDLOverlay overlay, SDLRect rect) {
//		return SWIG_SDLVideo.SWIG_displayYUVOverlay(overlay.getSwigOverlay(), rect.getX(), rect.getY(),
//				rect.getWidth(), rect.getHeight());
//	}
//
//	public static void freeYUVOverlay(SDLOverlay overlay) {
//		SWIG_SDLVideo.SDL_FreeYUVOverlay(overlay.getSwigOverlay());
//	}
//
//	//  public final static native int SDL_LockYUVOverlay(long jarg1);
//	//  public final static native void SDL_UnlockYUVOverlay(long jarg1);
//	//  public final static native int SDL_DisplayYUVOverlay(long jarg1, long jarg2);
//	//  public final static native void SDL_FreeYUVOverlay(long jarg1);
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	/* OpenGL support functions.                                                 */
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	// not yet implemented
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	/* These functions allow interaction with the window manager, if any.        */
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
}
