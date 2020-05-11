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
package org.thenesis.midpath.io;

import java.io.EOFException;
import java.io.IOException;
import java.util.Vector;

import com.sun.midp.io.j2me.file.BaseFileHandler;
import com.sun.midp.io.j2me.file.RandomAccessStream;

public abstract class AbstractFileHandler implements BaseFileHandler, RandomAccessStream{

	/**
	 * Parses a separated list of strings into a string array.
	 * An escaped separator (backslash followed by separatorChar) is not
	 * treated as a separator.
	 * @param data string to be processed
	 * @param separatorChar the character used to separate items
	 * @param startingPoint Only use the part of the string that follows this
	 * index
	 *
	 * @return a non-null string array containing string elements
	 */
	private static String[] split(String data, char separatorChar, int startingPoint) {
	
	    if (startingPoint == data.length()) {
	        return new String[0];
	    }
	    Vector elementList = new Vector();
	
	    if (data.charAt(startingPoint) == separatorChar) {
	        startingPoint++;
	    }
	
	    int startSearchAt = startingPoint;
	    int startOfElement = startingPoint;
	
	    for (int i; (i = data.indexOf(separatorChar, startSearchAt)) != -1; ) {
	        if (i != 0 && data.charAt(i - 1) == '\\') {
	            // escaped semicolon. don't treat it as a separator
	            startSearchAt = i + 1;
	        } else {
	            String element = data.substring(startOfElement, i);
	            elementList.addElement(element);
	            startSearchAt = startOfElement = i + 1;
	        }
	    }
	
	    if (data.length() > startOfElement) {
	        if (elementList.size() == 0) {
	            return new String[] { data.substring(startOfElement) };
	        }
	        elementList.addElement(data.substring(startOfElement));
	    }
	
	    String[] elements = new String[elementList.size()];
	    for (int i = 0; i < elements.length; i++) {
	        elements[i] = (String) elementList.elementAt(i);
	    }
	    return elements;
	}

	protected boolean filterAccept(String filter, String str) {
	
	    if (filter == null) {
	        return true;
	    }
	
	    if (filter.length() == 0) {
	        return false;
	    }
	
	    int  currPos = 0;
	    int currComp = 0, firstSigComp = 0;
	    int idx;
	
	    // Splitted string does not contain separators themselves
	    String components[] = split(filter, '*', 0);
	
	    // if filter does not begin with '*' check that string begins with
	    // filter's first component
	    if (filter.charAt(0) != '*') {
	        if (!str.startsWith(components[0])) {
	            return false;
	        } else {
	            currPos += components[0].length();
	            currComp++;
	            firstSigComp = currComp;
	        }
	    }
	
	    // Run on the string and check that it contains all filter
	    // components sequentially
	    for (; currComp < components.length; currComp++) {
	        if ((idx = str.indexOf(components[currComp], currPos)) != -1) {
	            currPos = idx + components[currComp].length();
	        } else {
	            // run out of the string while filter components remain
	            return false;
	        }
	    }
	
	    // At this point we run out of filter. First option is that
	    // filter ends with '*', or string is finished,
	    // we are fine then, and accept the string.
	    //
	    // In the other case we check that string ends with the last component
	    // of the filter (given that there was an asterisk before the last
	    // component
	    if (!(filter.charAt(filter.length() - 1) == '*'
	            || currPos == str.length())) {
	        if (components.length > firstSigComp) {
	            // does string end with the last filter component?
	            if (!str.endsWith(components[components.length - 1])) {
	                return false;
	            }
	        } else {
	            // there was no asteric before last filter component
	            return false;
	        }
	    }
	
	    // If we got here string is accepted
	    return true;
	}
	
	public void readFully(byte b[], int off, int len) throws IOException {
        if (len < 0) {
            throw new IndexOutOfBoundsException();
        }
        int n = 0;
        while (n < len) {
            int count = read(b, off + n, len - n);
            if (count < 0) {
                throw new EOFException();
            }
            n += count;
        }
    }

}