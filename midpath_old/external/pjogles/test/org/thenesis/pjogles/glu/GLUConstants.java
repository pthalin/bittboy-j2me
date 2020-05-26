/*
 * PJOGLES - Copyright (C) 2008 Guillaume Legris, Mathieu Legris
 * 
 * OGLJava - Copyright (C) 2004 Tom Dinneen
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

package org.thenesis.pjogles.glu;

/**
 * All the shared GLU constants in an interface that we can implement.
 * Could of course enumerate some of these for Java 1.5, maybe just static imports?
 *
 * @author tdinneen
 */
public interface GLUConstants {
	/**
	 * Boolean
	 */
	public static final boolean GLU_TRUE = true;
	public static final boolean GLU_FALSE = false;

	/**
	 * String names:
	 */
	public static final int GLU_VERSION = 100800;
	public static final int GLU_EXTENSIONS = 100801;

	// Quadric constants

	/**
	 * Types of normals:
	 */
	public static final int GLU_SMOOTH = 100000;
	public static final int GLU_FLAT = 100001;
	public static final int GLU_NONE = 100002;

	/**
	 * DrawStyle types:
	 */
	public static final int GLU_POINT = 100010;
	public static final int GLU_LINE = 100011;
	public static final int GLU_FILL = 100012;
	public static final int GLU_SILHOUETTE = 100013;

	/**
	 * Orientation types:
	 */
	public static final int GLU_OUTSIDE = 100020;
	public static final int GLU_INSIDE = 100021;

	public static final int GLU_ERROR = 100103;

	// NURBS constants

	/**
	 * Property types:
	 */
	public static final int GLU_AUTO_LOAD_MATRIX = 100200;
	public static final int GLU_CULLING = 100201;
	public static final int GLU_PARAMETRIC_TOLERANCE = 100202;
	public static final int GLU_SAMPLING_TOLERANCE = 100203;
	public static final int GLU_DISPLAY_MODE = 100204;
	public static final int GLU_SAMPLING_METHOD = 100205;
	public static final int GLU_U_STEP = 100206;
	public static final int GLU_V_STEP = 100207;

	/**
	 * Sampling types:
	 */
	public static final int GLU_PATH_LENGTH = 100215;
	public static final int GLU_PARAMETRIC_ERROR = 100216;
	public static final int GLU_DOMAIN_DISTANCE = 100217;

	/**
	 * Trim types:
	 */
	public static final int GLU_MAP1_TRIM_2 = 100210;
	public static final int GLU_MAP1_TRIM_3 = 100211;

	/**
	 * Display types:
	 */
	public static final int GLU_OUTLINE_POLYGON = 100240;
	public static final int GLU_OUTLINE_PATCH = 100241;

	/**
	 * Error codes:
	 */
	public static final int GLU_NURBS_ERROR1 = 100251;
	public static final int GLU_NURBS_ERROR2 = 100252;
	public static final int GLU_NURBS_ERROR3 = 100253;
	public static final int GLU_NURBS_ERROR4 = 100254;
	public static final int GLU_NURBS_ERROR5 = 100255;
	public static final int GLU_NURBS_ERROR6 = 100256;
	public static final int GLU_NURBS_ERROR7 = 100257;
	public static final int GLU_NURBS_ERROR8 = 100258;
	public static final int GLU_NURBS_ERROR9 = 100259;
	public static final int GLU_NURBS_ERROR10 = 100260;
	public static final int GLU_NURBS_ERROR11 = 100261;
	public static final int GLU_NURBS_ERROR12 = 100262;
	public static final int GLU_NURBS_ERROR13 = 100263;
	public static final int GLU_NURBS_ERROR14 = 100264;
	public static final int GLU_NURBS_ERROR15 = 100265;
	public static final int GLU_NURBS_ERROR16 = 100266;
	public static final int GLU_NURBS_ERROR17 = 100267;
	public static final int GLU_NURBS_ERROR18 = 100268;
	public static final int GLU_NURBS_ERROR19 = 100269;
	public static final int GLU_NURBS_ERROR20 = 100270;
	public static final int GLU_NURBS_ERROR21 = 100271;
	public static final int GLU_NURBS_ERROR22 = 100272;
	public static final int GLU_NURBS_ERROR23 = 100273;
	public static final int GLU_NURBS_ERROR24 = 100274;
	public static final int GLU_NURBS_ERROR25 = 100275;
	public static final int GLU_NURBS_ERROR26 = 100276;
	public static final int GLU_NURBS_ERROR27 = 100277;
	public static final int GLU_NURBS_ERROR28 = 100278;
	public static final int GLU_NURBS_ERROR29 = 100279;
	public static final int GLU_NURBS_ERROR30 = 100280;
	public static final int GLU_NURBS_ERROR31 = 100281;
	public static final int GLU_NURBS_ERROR32 = 100282;
	public static final int GLU_NURBS_ERROR33 = 100283;
	public static final int GLU_NURBS_ERROR34 = 100284;
	public static final int GLU_NURBS_ERROR35 = 100285;
	public static final int GLU_NURBS_ERROR36 = 100286;
	public static final int GLU_NURBS_ERROR37 = 100287;
}
