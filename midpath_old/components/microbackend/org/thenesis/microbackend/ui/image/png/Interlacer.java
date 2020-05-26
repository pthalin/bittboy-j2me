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

abstract class Interlacer {
	abstract int numPasses();

	abstract int getSpacingX(int pass);

	abstract int getSpacingY(int pass);

	abstract int getOffsetX(int pass);

	abstract int getOffsetY(int pass);

	private int maxSpacingX = 0;
	private int maxSpacingY = 0;
	private int widestPass = 0;

	protected int w, h;

	Interlacer(int w, int h) {
		this.w = w;
		this.h = h;
		int minSpacingX = w;
		int n = numPasses();
		for (int i = 0; i < n; i++) {
			int sp_x = getSpacingX(i);
			if (sp_x < minSpacingX) {
				minSpacingX = sp_x;
				widestPass = i;
			}
			maxSpacingX = Math.max(maxSpacingX, sp_x);
			maxSpacingY = Math.max(maxSpacingY, getSpacingY(i));
		}
	}

	final int getMaxSpacingX() {
		return maxSpacingX;
	}

	final int getMaxSpacingY() {
		return maxSpacingY;
	}

	final int getMaxPassWidth() {
		return getPassWidth(widestPass);
	}

	final int getPassWidth(int pass) {
		return ((w / maxSpacingX) * countPixelsX(pass, maxSpacingX) + countPixelsX(pass, w % maxSpacingX));
	}

	final int getPassHeight(int pass) {
		return ((h / maxSpacingY) * countPixelsY(pass, maxSpacingY) + countPixelsY(pass, h % maxSpacingY));
	}

	final int getBlockWidth(int pass) {
		return getSpacingX(pass) - getOffsetX(pass);
	}

	final int getBlockHeight(int pass) {
		return getSpacingY(pass) - getOffsetY(pass);
	}

	final int countPixelsX(int pass, int w) {
		int cur = 0;
		int next = getOffsetX(pass);
		int sp = getSpacingX(pass);
		for (int x = 0; x < w; x++) {
			if (x == next) {
				cur++;
				next = x + sp;
			}
		}
		return cur;
	}

	final int countPixelsY(int pass, int h) {
		int cur = 0;
		int next = getOffsetY(pass);
		int sp = getSpacingY(pass);
		for (int y = 0; y < h; y++) {
			if (y == next) {
				cur++;
				next = y + sp;
			}
		}
		return cur;
	}
}
