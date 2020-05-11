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
package org.thenesis.m3g.engine.util;

public class Color {

	public float a = 0.0f;
	public float r = 0.0f;
	public float g = 0.0f;
	public float b = 0.0f;

	public Color(float a, float r, float g, float b) {
		this.a = a;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public Color(int color) {
		this.a = ((float) ((color >> 24) & 0xFF)) / 255.0f; 
		this.r = ((float) ((color >> 16) & 0xFF)) / 255.0f;
		this.g = ((float) ((color >> 8) & 0xFF)) / 255.0f;
		this.b = ((float) (color & 0xFF)) / 255.0f;
	}

	public float[] toRBGAArray() {
		float[] c = { r, g, b, a };
		return c;
	}

	public static float[] intToFloatArray(int color) {
		Color c = new Color(color);
		return c.toRBGAArray();
	}

	public String toString() {
		return "{" + r + ", " + g + ", " + b + ", " + a + "}";
	}

}
