/*
 * Copyright ThinkTank Mathematics Limited 2006, 2007
 *
 * This file is free software: you can redistribute it and/or modify it under the terms of
 * the GNU General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * This file is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this file.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.openlapi;

/**
 * The LocationException is thrown when a location API specific error has occurred. The
 * detailed conditions when this exception is thrown are documented in the methods that
 * throw this exception.
 */
public class LocationException extends Exception {
	/**
	 * Constructs a LocationException with no detail message.
	 */
	public LocationException() {
	}

	/**
	 * Constructs a LocationException with the specified detail message.
	 *
	 * @param s
	 *            the detailed message
	 */
	public LocationException(String s) {
		super(s);
	}
}
