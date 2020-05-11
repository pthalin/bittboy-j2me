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

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.Enumeration;
import java.util.Vector;

public class Tools {
	
	 public static Buffer clone(Buffer src) {
		 	
		 	if (src == null) {
	 			return null;
	 		}
		 
	        int length = src.remaining();
	        ByteBuffer copy;
	        if (src instanceof ByteBuffer) {
	            copy = ByteBuffer.allocateDirect(length);
	            copy.put((ByteBuffer)src);
	            return copy;
	        } else if (src instanceof ShortBuffer) {
	            copy = ByteBuffer.allocateDirect(2*length);
	            ShortBuffer directShort = copy.asShortBuffer();
	            directShort.put((ShortBuffer)src);
	            return directShort;
	        } else if (src instanceof IntBuffer) {
	            copy = ByteBuffer.allocateDirect(4*length);
	            IntBuffer directInt = copy.asIntBuffer();
	            directInt.put((IntBuffer)src);
	            return directInt;
	        } else if (src instanceof FloatBuffer) {
	            copy = ByteBuffer.allocateDirect(4*length);
	            FloatBuffer directFloat = copy.asFloatBuffer();
	            directFloat.put((FloatBuffer)src);
	            return directFloat;
	        } else {
	            throw new IllegalArgumentException();
	        }
	    }
	 
	 	public static byte[] clone(byte[] src) {
	 		if (src == null) {
	 			return null;
	 		}
	 		int length = src.length;
	 		byte[] copy = new byte[length];
	 		System.arraycopy(src, 0, copy, 0, length);
	 		return copy;
	 	}
	 	
	 	public static float[] clone(float[] src) {
	 		if (src == null) {
	 			return null;
	 		}
	 		int length = src.length;
	 		float[] copy = new float[length];
	 		System.arraycopy(src, 0, copy, 0, length);
	 		return copy;
	 	}
	 	
	 	public static float[][] clone(float[][] src) {
	 		if (src == null) {
	 			return null;
	 		}
	 		float[][] copy = new float[src.length][src[0].length];
	 		for (int i = 0; i < src.length; i++) {
	 			System.arraycopy(src[i], 0, copy[i], 0, src[i].length);
	 		}
	 		return copy;
	 	}
	 	
	 	public static int[] clone(int[] src) {
	 		if (src == null) {
	 			return null;
	 		}
	 		int length = src.length;
	 		int[] copy = new int[length];
	 		System.arraycopy(src, 0, copy, 0, length);
	 		return copy;
	 	}
	 	
	 	public static Vector clone(Vector src) {
	 		Vector dest = new Vector();
	 		Enumeration e = src.elements();
			while(e.hasMoreElements()) {
				dest.addElement(e.nextElement());
			}
			return dest;
	 	}
	 	
	 	public static boolean isPowerOfTwo(int x) {
	 		return ((x & (x -1)) == 0); 
	 	}

}
