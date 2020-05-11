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

/**
 * This class represents a point in 2D space.
 *
 * @author Chris Dennett (Dessimat0r@ntlworld.com)
 * @version 0.2
 */
public class SDLPoint { //implements Cloneable {
	/** The x coordinate of the point. */
	public int x;

	/** The y coordiate of the point. */
	public int y;

	/**
	 * Creates a new SDLPoint object.
	 * @param x The x coordiante of the point.
	 * @param y The y coordinate of the point.
	 */
	public SDLPoint(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Gets the x coordiante of the point.
	 * @return Returns the x coordiante.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Sets the x coordiante of the point.
	 * @param x The x coordinate.
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * Gets the y coordiante of the point.
	 * @return Returns the y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Sets the x coordiante of the point.
	 * @param y The y coordiante.
	 */
	public void setY(int y) {
		this.y = y;
	}

	/**
	 * Sets the location of the point.
	 * @param x The x coordinate.
	 * @param y The y coordiate.
	 */
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Returns the string represenation of this point.
	 * 
	 * @return The string represenation of this point.
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("SDLPoint[").append("x=").append(getX()).append(", y=").append(getY()).append("]");

		return buf.toString();
	}

	/**
	 * Checks whether this point lies within the specified rect.
	 * @param rect The rect to test.
	 * @return True if the point lies within the rect, false if it dosen't.
	 * 
	 * @see SDLPoint
	 */
	public boolean liesWithin(SDLRect rect) {
		SDLPoint rectTopLeft = rect.getLocation();
		SDLPoint rectBottomRight = rect.getBottomRight();

		int rectTL_X = rect.getX();
		int rectTL_Y = rect.getY();
		int rectBR_X = rectBottomRight.getX();
		int rectBR_Y = rectBottomRight.getY();

		return (((x >= rectTL_X) && (x <= rectBR_X)) && ((y >= rectTL_Y) && (y <= rectBR_Y)));
	}

	/**
	 * Creates a clone of the SDLPoint.
	 * 
	 * @return A clone of this SDLPoint instance.
	 * @see java.lang.Object#clone()
	 * @author Chris Dennett (Dessimat0r@ntlworld.com)
	 */
	public Object clone() {

		return new SDLPoint(x, y);

		//        Object o = null;
		//        try {
		//	    o = super.clone();
		//	    // Clone should always be supported.
		//        } catch (CloneNotSupportedException e) {}
		//        return o;
	}
}
