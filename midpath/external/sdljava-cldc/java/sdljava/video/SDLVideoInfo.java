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
import sdljava.x.swig.SDL_VideoInfo;

/**
 * Video Target information
 * <P>
 * Useful for determining the video hardware capabilities
 *
 * @author Ivan Z. Ganza
 * @version $Id: SDLVideoInfo.java,v 1.10 2004/12/27 06:23:50 ivan_ganza Exp $
 * @todo Finish SWIG integration
 */
public class SDLVideoInfo {

	SDL_VideoInfo swigVideoInfo;

	protected SDLVideoInfo(SDL_VideoInfo swigVideoInfo) {
		this.swigVideoInfo = swigVideoInfo;
	}

	/**
	 * Gets the value of hwAvailable
	 *
	 * @return the value of hwAvailable
	 */
	public long getHwAvailable() {
		return swigVideoInfo.getHw_available();
	}

	/**
	 * Gets the value of wmVailable
	 *
	 * @return the value of wmVailable
	 */
	public long getWmAvailable() {
		return swigVideoInfo.getWm_available();
	}

	/**
	 * Gets the value of blit_hw
	 *
	 * @return the value of blit_hw
	 */
	public long getBlit_hw() {
		return swigVideoInfo.getBlit_hw();
	}

	/**
	 * Gets the value of blit_hw_CC
	 *
	 * @return the value of blit_hw_CC
	 */
	public long getBlit_hw_CC() {
		return swigVideoInfo.getBlit_hw_CC();
	}

	/**
	 * Gets the value of blit_hw_A
	 *
	 * @return the value of blit_hw_A
	 */
	public long getBlit_hw_A() {
		return swigVideoInfo.getBlit_hw_A();
	}

	/**
	 * Gets the value of blit_sw
	 *
	 * @return the value of blit_sw
	 */
	public long getBlit_sw() {
		return swigVideoInfo.getBlit_sw();
	}

	/**
	 * Gets the value of blit_sw_CC
	 *
	 * @return the value of blit_sw_CC
	 */
	public long getBlit_sw_CC() {
		return swigVideoInfo.getBlit_sw_CC();
	}

	/**
	 * Gets the value of blit_sw_A
	 *
	 * @return the value of blit_sw_A
	 */
	public long getBlit_sw_A() {
		return swigVideoInfo.getBlit_sw_A();
	}

	/**
	 * Gets the value of blit_fill
	 *
	 * @return the value of blit_fill
	 */
	public long getBlit_fill() {
		return swigVideoInfo.getBlit_fill();
	}

	/**
	 * Gets the value of video_mem
	 *
	 * @return the value of video_mem
	 */
	public long getVideoMemory() {
		return swigVideoInfo.getVideo_mem();
	}

	/**
	 * @return The <code>SDLPixelFormat</code> value
	 *
	 * 
	 */
	public SDLPixelFormat getFormat() {
		return new SDLPixelFormat(swigVideoInfo.getVfmt());
	}

	/**
	 * @return A String representation of myself
	 *
	 * 
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("SDLVideoInfo[").append("hwAvailable=").append(getHwAvailable()).append(", wmAvailable=").append(
				getWmAvailable()).append(", blit_hw=").append(getBlit_hw()).append(", blit_hw_CC=").append(
				getBlit_hw_CC()).append(", blit_hw_A=").append(getBlit_hw_A()).append(", blit_sw=")
				.append(getBlit_sw()).append(", blit_sw_CC=").append(getBlit_sw_CC()).append(", blit_sw_A=").append(
						getBlit_sw_A()).append(", blit_fill=").append(getBlit_fill()).append(", video_mem=").append(
						getVideoMemory()).append(", ").append(getFormat()).append("]");

		return buf.toString();
	}

} // class SDLVideoInfo
