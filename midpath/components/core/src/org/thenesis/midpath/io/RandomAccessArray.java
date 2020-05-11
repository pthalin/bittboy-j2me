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

import java.io.IOException;

public class RandomAccessArray {

	private static final int DEFAULT_SIZE = 10000;
	private byte[] data;
	private int currentPos = 0;

	public RandomAccessArray() {
		this(DEFAULT_SIZE);
	}

	public RandomAccessArray(int initialSize) {
		data = new byte[initialSize];
	}

	public void seek(int pos) throws IOException {
		if (pos < 0 || pos >= data.length)
			throw new IOException("Can't go to position: " + pos);

		currentPos = pos;
	}

	public void setLength(int length) throws IOException {
		if (length > data.length) {
			throw new IOException("Can't set file length up to its actual size ");
		}
		byte[] newData = new byte[length];
		System.arraycopy(data, 0, newData, 0, length);
		data = newData;
	}

	public int read(byte[] buffer, int offset, int length) throws IOException {
		int remain = data.length - currentPos;
		if (remain <= 0)
			return -1;

		if (length > remain)
			length = remain;

		System.arraycopy(data, currentPos, buffer, offset, length);
		currentPos += length;

		return length;
	}

	public void write(byte[] bytes, int offset, int length) throws IOException {

		if ((currentPos + length) > data.length) {
			grow(currentPos + length);
		}

		System.arraycopy(bytes, offset, data, currentPos, length);
		currentPos += length;

	}

	public void grow(int size) {
		byte[] newData = new byte[size];
		System.arraycopy(data, 0, newData, 0, data.length);
		data = newData;
	}

	public int getPosition() {
		return currentPos;
	}
	
	public int getLength() {
		return data.length;
	}

}
