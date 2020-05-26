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
 * A rectangle with x, y, width and height.
 *
 * @author  Ivan Z. Ganza
 * @author  Chris Dennett (Dessimat0r@ntlworld.com)
 * @version $Id: SDLRect.java,v 1.6 2005/03/29 02:46:35 ivan_ganza Exp $
 */
public class SDLRect {

	/** The topleft x coordinate of the rectangle. */
	public int x;

	/** The topleft y coordinate of the rectangle. */
	public int y;

	/** The width of the rectangle. */
	public int width;

	/** The height of the rectangle. */
	public int height;

	public SDLRect() {
	}

	/**
	 * Creates an SDLRect with the specified topleft x and y coordinates
	 *
	 * 
	 * @param x      The topleft x coordinate of the SDLRect.
	 * @param y      The topleft y coordinate of the SDLRect.
	 */
	public SDLRect(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Creates an SDLRect with the specified topleft x and y coordinates,
	 * width and height.
	 * 
	 * @param x      The topleft x coordinate of the SDLRect.
	 * @param y      The topleft y coordinate of the SDLRect.
	 * @param width  The width of the SDLRect.
	 * @param height The height of the SDLRect.
	 */
	public SDLRect(int x, int y, int width, int height) {
		this(x, y);
		this.width = width;
		this.height = height;
	}

	/**
	 * Creates an SDLRect with the specified topleft point, height and width.
	 * 
	 * @param pos    The topleft position of the rectangle.
	 * @param width  The width of the rectangle.
	 * @param height The height of the rectangle.
	 */
	public SDLRect(SDLPoint pos, int width, int height) {
		this.x = pos.getX();
		this.y = pos.getY();
	}

	/**
	 * Gets the topleft x coordiante of the SDLRect.
	 *
	 * @return The topleft x coordinate.
	 */
	public int getX() {
		return this.x;
	}

	/**
	 * Sets the topleft x coordiante of the SDLRect.
	 *
	 * @param x The new topleft x coordinate.
	 */
	public void setX(int argX) {
		this.x = argX;
	}

	/**
	 * Gets the topleft y coordiante of the SDLRect.
	 *
	 * @return The topleft y coordinate.
	 */
	public int getY() {
		return this.y;
	}

	/**
	 * Sets the topleft y coordinate of the SDLRect.
	 *
	 * @param y The new topleft y coordinate.
	 */
	public void setY(int argY) {
		this.y = argY;
	}

	/**
	 * Gets the width of the SDLRect.
	 *
	 * @return The width of the SDLRect.
	 */
	public int getWidth() {
		return this.width;
	}

	/**
	 * Sets the width of the SDLRect.
	 *
	 * @param width The new width of the SDLRect.
	 */
	public void setWidth(int argWidth) {
		this.width = argWidth;
	}

	/**
	 * Gets the height of the SDLRect.
	 *
	 * @return The height of the SDLRect.
	 */
	public int getHeight() {
		return this.height;
	}

	/**
	 * Sets the height of the SDLRect.
	 *
	 * @param height The new height of the SDLRect.
	 */
	public void setHeight(int argHeight) {
		this.height = argHeight;
	}

	/**
	 * Sets the topleft position of the SDLRect, using the topleft x and y
	 * coordiantes.
	 * 
	 * @param x The topleft x coordinate.
	 * @param y The topleft y coordinate.
	 */
	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Sets the size of the SDLRect, using width and height values.
	 * 
	 * @param width  The new width of the SDLRect.
	 * @param height The new height of the SDLRect.
	 */
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	/**
	 * Sets the topleft point of the rectangle.
	 * 
	 * @param pos The new topleft point of the rectangle.
	 * 
	 * @see SDLPoint
	 */
	public void setLocation(SDLPoint pos) {
		x = pos.getX();
		y = pos.getY();
	}

	/**
	 * Gets the point at the top left of the rectangle.
	 * 
	 * @return An SDLPoint object containing the top left point of the
	 *         rectangle.
	 * 
	 * @see SDLPoint
	 */
	public SDLPoint getLocation() {
		return new SDLPoint(x, y);
	}

	/**
	 * Gets the point at the top right of the rectangle.
	 * 
	 * @return An SDLPoint object containing the top right point of the
	 *         rectangle.
	 * 
	 * @see SDLPoint
	 */
	public SDLPoint getTopRight() {
		return new SDLPoint(x + width, y);
	}

	/**
	 * Gets the point at the center of the rectangle. The point is rounded to
	 * the nearest point.
	 * 
	 * @return An SDLPoint object containing the point in the center of the
	 *         rectangle.
	 * 
	 * @see SDLPoint
	 */
	public SDLPoint getCenter() {
		int cX = x + (int)Math.floor(width / 2 + 0.5d);
		int cY = y + (int)Math.floor(height / 2 + 0.5d);
		
//		int cX = x + (Math.round(width / 2));
//		int cY = y + (Math.round(height / 2));

		return new SDLPoint(cX, cY);
	}

	/**
	 * Sets the center point of the rectangle using integer X and Y coordinates.
	 * 
	 * @param x The new center x coordiante of the rectangle.
	 * @param y The new center y coordiante of the rectangle.
	 * 
	 * @see SDLPoint
	 */
	public void setCenter(int x, int y) {
		SDLPoint center = getCenter();
		this.x = x - center.getX();
		this.y = y - center.getY();
	}

	/**
	 * Sets the center point of the rectangle using an SDLPoint.
	 * 
	 * @param pos The new center point of the rectangle.
	 * 
	 * @see SDLPoint
	 */
	public void setCenter(SDLPoint pos) {
		setCenter(pos.getX(), pos.getY());
	}

	/**
	 * Gets the point at the bottom right of the rectangle.
	 * 
	 * @return An SDLPoint object containing the bottom right point of the
	 *         rectangle.
	 * 
	 * @see SDLPoint
	 */
	public SDLPoint getBottomRight() {
		return new SDLPoint(x + width, y + height);
	}

	/**
	 * Gets the point at the bottom left of the rectangle.
	 * 
	 * @return An SDLPoint object containing the bottom left point of the
	 *         rectangle.
	 * 
	 * @see SDLPoint
	 */
	public SDLPoint getBottomLeft() {
		return new SDLPoint(x, y + height);
	}

	/**
	 * Creates a clone of the SDLRect.
	 * 
	 * @return A clone of this SDLRect instance.
	 * @see java.lang.Object#clone()
	 */
	public Object clone() {
		
		return new SDLRect(x, y, width, height);
		
//		// If this class ever uses SDLPoint for X and Y, or any immutables,
//		// then they must be cloned also, or the clone will be shallow.
//		Object o = null;
//		try {
//			o = super.clone();
//			// Clone should always be supported.
//		} catch (CloneNotSupportedException e) {
//		}
//		return o;
	}

	/**
	 * Scales this rect by the given x and y factors. This affects the x, y,
	 * width and height of the rect.
	 * (i.e. x:2, y:6, w:10, h:16 becomes x:1, y:3, w:5, h:8 with factor 2)
	 * 
	 * @param xFactor The x factor to scale this rect by.
	 * @param yFactor The y factor to scale this rect by.
	 */
	public void scaleAll(int xFactor, int yFactor) {

		x = (int) Math.floor(x / xFactor + 0.5d);
		y = (int) Math.floor(y / yFactor + 0.5d);
		width = (int) Math.floor(width / xFactor + 0.5d);
		height = (int) Math.floor(height / yFactor + 0.5d);

		//	x      = Math.round(x      / xFactor);
		//	y      = Math.round(y      / yFactor);
		//	width  = Math.round(width  / xFactor);
		//	height = Math.round(height / yFactor);

	}

	/**
	 * Descales (small to big) this rect by the given x and y factors. This
	 * affects the x, y, width and height of the rect.
	 * (i.e. x:2, y:6, w:10, h:16 becomes x:4, y:12, w:20, h:32 with factor 2)
	 * 
	 * @param xFactor The x factor to scale this rect by.
	 * @param yFactor The y factor to scale this rect by.
	 */
	public void descaleAll(int xFactor, int yFactor) {
		x *= xFactor;
		y *= yFactor;
		width *= xFactor;
		height *= yFactor;
	}

	/**
	 * Scales (big to small) this rect by the given x and y factors. This
	 * affects the width and height of the rect only.
	 * (i.e. x:2, y:6, w:10, h:16 becomes x:2, y:6, w:5, h:8 with factor 2)
	 * 
	 * @param xFactor The x factor to scale this rect by.
	 * @param yFactor The y factor to scale this rect by.
	 */
	public void scale(int xFactor, int yFactor) {
		width = (int) Math.floor(width / xFactor + 0.5d);
		height = (int) Math.floor(height / yFactor + 0.5d);

		//	width  = Math.round(width  / xFactor);
		//	height = Math.round(height / yFactor);
	}

	/**
	 * Descales (small to big) this rect by the given x and y factors. This
	 * affects the width and height of the rect only.
	 * (i.e. x:2, y:6, w:10, h:16 becomes x:2, y:6, w:20, h:32 with factor 2)
	 * 
	 * @param xFactor The x factor to scale this rect by.
	 * @param yFactor The y factor to scale this rect by.
	 */
	public void descale(int xFactor, int yFactor) {
		width *= xFactor;
		height *= yFactor;
	}

	/**
	 * Return a string represenation of this object
	 *
	 * @return a String represenation of this object
	 */
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("SDLRect[").append("x=").append(getX()).append(", y=").append(getY()).append(", width=").append(
				getWidth()).append(", height=").append(getHeight()).append("]");

		return buf.toString();
	}
}