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
package org.thenesis.m3g.engine.vecMath;

public class Vector2 {

	public float x, y;

	public Vector2() {
		x = 0;
		y = 0;
	}

	public Vector2(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vector2(Vector2 v) {
		x = v.x;
		y = v.y;
	}

	public Vector2(float[] v) {
		if (v == null)
			throw new NullPointerException();
		if (v.length < 2)
			throw new IllegalArgumentException("v must be of lenght 3 or larger");

		x = v[0];
		y = v[1];
	}

	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public void set(Vector2 v) {
		x = v.x;
		y = v.y;
	}

	public void set(float[] v) {
		if (v == null)
			throw new NullPointerException();
		if (v.length < 2)
			throw new IllegalArgumentException("v must be of lenght 3 or larger");

		x = v[0];
		y = v[1];
	}

	public void add(Vector2 v) {
		x += v.x;
		y += v.y;
	}

	public void add(float scalar) {
		x += scalar;
		y += scalar;
	}

	public void add(Vector2 v, float scalar) {
		x = v.x + scalar;
		y = v.y + scalar;
	}

	public void add(Vector2 v1, Vector2 v2) {
		x = v1.x + v2.x;
		y = v1.y + v2.y;
	}

	public void subtract(Vector2 v) {
		x -= v.x;
		y -= v.y;
	}

	public void subtract(float scalar) {
		x -= scalar;
		y -= scalar;
	}

	public void subtract(Vector2 v, float scalar) {
		x = v.x - scalar;
		y = v.y - scalar;
	}

	public void subtract(Vector2 v1, Vector2 v2) {
		x = v1.x - v2.x;
		y = v1.y - v2.y;
	}

	public void multiply(float scalar) {
		x *= scalar;
		y *= scalar;
	}

	public void multiply(Vector2 v, float scalar) {
		x = v.x * scalar;
		y = v.y * scalar;
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y);
	}

	public float length2() {
		return x * x + y * y;
	}

	public void normalize() {
		float length = length();
		if (length < Constants.EPSILON)
			throw new ArithmeticException("Can't normalize zero lenght vector.");

		multiply(1.0f / length);
	}

	public float dot(Vector2 v) {
		return x * v.x + y * v.y;
	}

	// gets the projection of this vector on v
	public Vector2 getProjection(Vector2 v) {
		Vector2 e = new Vector2(v);
		e.normalize();
		e.multiply(this.dot(e));
		return e;
	}

	// gets the rejection of this vector on v
	public Vector2 getRejection(Vector2 v) {
		Vector2 u = new Vector2(this);
		u.subtract(getProjection(v));
		return u;
	}

	// gets the reflection of this vector around v
	public Vector2 getReflection(Vector2 v) {
		Vector2 u = new Vector2(this);
		Vector2 w = getRejection(v);
		w.multiply(2);
		u.subtract(w);
		return u;
	}

	public boolean equals(Vector2 v) {
		if (v == null)
			throw new NullPointerException();
		return (x == v.x && y == v.y);
	}

	public float[] toArray() {
		float[] a = { x, y };
		return a;
	}

	public String toString() {
		return "(" + x + ", " + y + ")";
	}
}
