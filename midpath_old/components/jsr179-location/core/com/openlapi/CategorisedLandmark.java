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

import java.util.Enumeration;
import java.util.Vector;

/**
 * A container class for a Landmark with an unspecified number of categories. Spelt the
 * good and proper British way ;-).
 */
class CategorisedLandmark {
	private final Vector categories = new Vector();

	private final Landmark landmark;

	/**
	 * A container class for a Landmark with an unspecified number of categories.
	 * Categories are added separately. It is these objects that are actually serialised
	 * in the RecordStore.
	 *
	 * @param landmark
	 */
	public CategorisedLandmark(Landmark landmark) {
		this.landmark = landmark;
	}

	/**
	 * Add a category, silently ignores duplicates. All categories are limited to 32
	 * characters.
	 *
	 * @param category
	 * @return true if the category was successfully added (or already existed), false if
	 *         the category was too long.
	 * @throws NullPointerException
	 *             if the string was null
	 */
	public boolean addCategory(String category) {
		if (category == null)
			throw new NullPointerException();
		if (category.length() > 32)
			return false;
		if (!categories.contains(category)) {
			categories.addElement(category);
		}
		return true;
	}

	/**
	 * @return the categories associated to the Landmark. If there are no categories, null
	 *         is returned.
	 */
	public String[] getCategories() {
		if (categories.size() == 0)
			return null;
		String[] cats = new String[categories.size()];
		Enumeration en = categories.elements();
		for (int i = 0; en.hasMoreElements(); i++) {
			cats[i] = (String) en.nextElement();
		}
		return cats;
	}

	/**
	 * @return the landmark
	 */
	public Landmark getLandmark() {
		return landmark;
	}

	/**
	 * @param category
	 * @return true iff the Landmark is in the specified category.
	 */
	public boolean inCategory(String category) {
		if (categories.contains(category))
			return true;
		return false;
	}

	/**
	 * Remove a category from the Landmark
	 *
	 * @param category
	 */
	public void removeCategory(String category) {
		if (categories.contains(category)) {
			categories.removeElement(category);
		}
	}
}