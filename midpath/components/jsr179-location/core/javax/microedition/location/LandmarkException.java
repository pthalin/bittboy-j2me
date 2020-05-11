/*
 * Copyright ThinkTank Mathematics Limited 2006, 2007
 *
 * This file is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version. ThinkTank Mathematics
 * Limited designates this particular file as subject to the "Classpath" exception as provided
 * in the LICENSE file that accompanied this code.
 *
 * This file is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this file.
 * If not, see <http://www.gnu.org/licenses/>.
 * 
 * You should have received a copy of the "Classpath" exception along with this file. If
 * not, see <http://www.gnu.org/software/classpath/license.html>.
 */
package javax.microedition.location;

/**
 * The LandmarkException is thrown when an error related to handling landmarks has
 * occurred.
 */
public class LandmarkException extends Exception {

	/**
	 * Constructs a LandmarkException with no detail message.
	 */
	public LandmarkException() {
	}

	/**
	 * Constructs a LandmarkException with the specified detail message.
	 *
	 * @param s
	 *            the detailed message
	 */
	public LandmarkException(String s) {
		super(s);
	}

	LandmarkException(com.openlapi.LandmarkException e) {
		super(e.getMessage());
	}
}
