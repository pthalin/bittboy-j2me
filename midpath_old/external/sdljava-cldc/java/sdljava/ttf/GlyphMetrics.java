package sdljava.ttf;

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
/**
 * The Glyph Metrics of a font.
 *
 * @author Ivan Z. Ganza
 * @version $Id: GlyphMetrics.java,v 1.2 2004/12/24 17:32:17 ivan_ganza Exp $
 */
public class GlyphMetrics {
	int minx, maxx, miny, maxy, advance;

	/**
	 * Creates a new <code>GlyphMetrics</code> instance.
	 *
	 * @param minx an <code>int</code> value
	 * @param maxx an <code>int</code> value
	 * @param miny an <code>int</code> value
	 * @param maxy an <code>int</code> value
	 * @param advance an <code>int</code> value
	 */
	public GlyphMetrics(int minx, int maxx, int miny, int maxy, int advance) {
		this.minx = minx;
		this.maxx = maxx;
		this.miny = miny;
		this.maxy = maxy;
		this.advance = advance;
	}

	/**
	 * Gets the value of minx
	 *
	 * @return the value of minx
	 */
	public int getMinX() {
		return this.minx;
	}

	/**
	 * Sets the value of minx
	 *
	 * @param argMinx Value to assign to this.minx
	 */
	public void setMinX(int argMinx) {
		this.minx = argMinx;
	}

	/**
	 * Gets the value of maxx
	 *
	 * @return the value of maxx
	 */
	public int getMaxX() {
		return this.maxx;
	}

	/**
	 * Sets the value of maxx
	 *
	 * @param argMaxx Value to assign to this.maxx
	 */
	public void setMaxX(int argMaxx) {
		this.maxx = argMaxx;
	}

	/**
	 * Gets the value of miny
	 *
	 * @return the value of miny
	 */
	public int getMinY() {
		return this.miny;
	}

	/**
	 * Sets the value of miny
	 *
	 * @param argMiny Value to assign to this.miny
	 */
	public void setMinY(int argMiny) {
		this.miny = argMiny;
	}

	/**
	 * Gets the value of maxy
	 *
	 * @return the value of maxy
	 */
	public int getMaxY() {
		return this.maxy;
	}

	/**
	 * Sets the value of maxy
	 *
	 * @param argMaxy Value to assign to this.maxy
	 */
	public void setMaxY(int argMaxy) {
		this.maxy = argMaxy;
	}

	/**
	 * Gets the value of advance
	 *
	 * @return the value of advance
	 */
	public int getAdvance() {
		return this.advance;
	}

	/**
	 * Sets the value of advance
	 *
	 * @param argAdvance Value to assign to this.advance
	 */
	public void setAdvance(int argAdvance) {
		this.advance = argAdvance;
	}

	/**
	 * Return a string represenation of this object
	 *
	 * @return a String represenation of this object
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("GlyphMetrics[minx=").append(getMinX()).append(", maxx=").append(getMaxX()).append(", miny=")
				.append(getMinY()).append(", maxy=").append(getMaxY()).append(", advance=").append(getAdvance())
				.append("]");

		return buf.toString();
	}
}