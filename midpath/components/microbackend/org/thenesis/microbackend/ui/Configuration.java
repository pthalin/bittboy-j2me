/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details. 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA  
 */
package org.thenesis.microbackend.ui;

import java.util.Vector;

public class Configuration {

	private static int INITIAL_SIZE = 10;

	private Vector keys;
	private Vector values;

	public Configuration() {
		keys = new Vector(INITIAL_SIZE);
		values = new Vector(INITIAL_SIZE);
	}

	public void addParameter(String key, String value) {
		keys.addElement(key);
		values.addElement(value);
	}

	public String getParameter(String key) {
		String p = null;

		if (key == null) {
			throw new NullPointerException();
		}

		int idx = keys.indexOf(key);
		if (idx > -1) {
			p = (String) values.elementAt(idx);
		}

		return p;
	}
	
	public String getParameterDefault(String key, String defaultValue) {
		String v = getParameter(key);
		return (v != null) ? v : defaultValue;
	}

	public String getParameterIgnoreCase(String key) {
		String p = null;

		if (key == null) {
			throw new NullPointerException();
		}

		int index = -1;
		for (int count = 0; count < keys.size(); count++) {
			if (((String) keys.elementAt(count)).equalsIgnoreCase(key)) {
				index = count;
			}
		}
		if (index > -1) {
			p = (String) values.elementAt(index);
		}

		return p;
	}
	
	public int getIntParameter(String key, int defaultValue) {
       
        String p = getParameter(key);
        if (p == null) {
            return defaultValue;
        }

        try {
            int v = Integer.parseInt(p);
            return v;
        } catch (NumberFormatException e) {
            // Do nothing
        }

        return defaultValue;
    }

	public String getValueAt(int index) {
		return (String) values.elementAt(index);
	}

	public String getKeyAt(int index) {
		return (String) keys.elementAt(index);
	}

	public int size() {
		return keys.size();
	}

	public synchronized String removeParameter(String key) {
		int index;
		String p = null;

		index = keys.indexOf(key);
		if (index > -1) {
			p = (String) values.elementAt(index);
			keys.removeElementAt(index);
			values.removeElementAt(index);
		}

		return p;
	}

}
