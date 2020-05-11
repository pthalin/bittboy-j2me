package sdljava.video;

/**
 *  sdljava - a java binding to the SDL API
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
import sdljava.x.swig.SDL_Overlay;

/**
 * A SDL_Overlay is similar to a SDL_Surface except it stores a YUV overlay.
 * All the fields are read only, except for pixels which should be locked before use.
 * The format field stores the format of the overlay which is one of the following:
 *
 * <code><pre>
 * #define SDL_YV12_OVERLAY  0x32315659  // Planar mode: Y + V + U
 * #define SDL_IYUV_OVERLAY  0x56555949  // Planar mode: Y + U + V
 * #define SDL_YUY2_OVERLAY  0x32595559  // Packed mode: Y0+U0+Y1+V0
 * #define SDL_UYVY_OVERLAY  0x59565955  // Packed mode: U0+Y0+V0+Y1
 * #define SDL_YVYU_OVERLAY  0x55595659  // Packed mode: Y0+V0+Y1+U0
 * </code></pre>
 * <P>
 * Also see the documentation here:
 *     <a href="http://www.libsdl.org/cgi/docwiki.cgi/SDL_5fOverlay">SDL_Overlay</a>
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLOverlay.java,v 1.5 2006/03/14 03:41:54 ivan_ganza Exp $
 * @todo Finish SWIG integration
 */
public class SDLOverlay {

	SDL_Overlay swigOverlay;

	public SDLOverlay(SDL_Overlay swigOverlay) {
		this.swigOverlay = swigOverlay;
	}

	SDL_Overlay getSwigOverlay() {
		return swigOverlay;
	}

	/**
	 * Overlay format
	 *
	 *
	 */
	public long getFormat() {
		return swigOverlay.getFormat();
	}

	/**
	 * @return The YUV Overlay width
	 *
	 *
	 */
	public int getWidth() {
		return swigOverlay.getW();
	}

	/**
	 * @return The YUV Overlay height
	 *
	 *
	 */
	public int getHeight() {
		return swigOverlay.getH();
	}

	/**
	 * @return The YUV Overlay planes
	 *
	 *
	 */
	public int getPlanes() {
		return swigOverlay.getPlanes();
	}

	/**
	 * @return if the overlay is hardware accelerated
	 *
	 *
	 */
	public boolean hwOverlay() {
		return swigOverlay.getHw_overlay() == 1;
	}

//	ByteBuffer getPixelsForPlane(int plane) {
//		return (ByteBuffer) SWIG_SDLVideo.SWIG_getOverlayPixelsDirectByteBuffer(swigOverlay, plane);
//	}

}
