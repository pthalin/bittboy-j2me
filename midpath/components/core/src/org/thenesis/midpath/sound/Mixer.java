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
package org.thenesis.midpath.sound;

import java.util.Vector;

public abstract class Mixer {
	
	Vector lineList = new Vector();
	
	public abstract Line createLine(AudioFormat format);

	public void addLine(Line line) {
		lineList.addElement(line);
	}

	public void removeLine(Line line) {
		lineList.removeElement(line);
	}

	public Line[] getLines() {

		Line[] lines = new Line[lineList.size()];
		lineList.copyInto(lines);

		return lines;
	}


}