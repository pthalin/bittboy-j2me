/*
 * MIDPath - Copyright (C) 2006-2007 Guillaume Legris, Mathieu Legris
 * 
 * com.sixlegs.image.png - Java package to read and display PNG images
 * Copyright (C) 1998-2004 Chris Nokleberg
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt). 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 * 
 * Linking this library statically or dynamically with other modules is
 * making a combined work based on this library.  Thus, the terms and
 * conditions of the GNU General Public License cover the whole
 * combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent
 * modules, and to copy and distribute the resulting executable under
 * terms of your choice, provided that you also meet, for each linked
 * independent module, the terms and conditions of the license of that
 * module.  An independent module is a module which is not derived from
 * or based on this library.  If you modify this library, you may extend
 * this exception to your version of the library, but you are not
 * obligated to do so.  If you do not wish to do so, delete this
 * exception statement from your version.
 */
package org.thenesis.microbackend.ui.image.png;

final class Adam7Interlacer extends Interlacer {
	static final int[] init = { 0, 0, 4, 0, 2, 0, 1, 0 };
	static final int[] sp = { 8, 8, 8, 4, 4, 2, 2, 1 };

	Adam7Interlacer(int w, int h) {
		super(w, h);
	}

	final int numPasses() {
		return 7;
	}

	final int getSpacingX(int pass) {
		return sp[pass + 1];
	}

	final int getSpacingY(int pass) {
		return sp[pass];
	}

	final int getOffsetX(int pass) {
		return init[pass + 1];
	}

	final int getOffsetY(int pass) {
		return init[pass];
	}
}
