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

package org.thenesis.pjogles.states;

import org.thenesis.pjogles.math.GLVector4f;

/**
 * @author tdinneen
 */
public final class GLViewportState {
	/**
	 * Viewport parameters from user, as integers.
	 */
	public int x, y;
	public int width, height;

	/**
	 * Depthrange parameters from user.
	 */
	public float zNear = 0.0f, zFar = 1.0f;

	// derived

	private float xScale, xCenter;
	private float yScale, yCenter;
	private float zScale = 0.5f, zCenter = 0.5f;

	public final void setViewport(final int x, final int y, final int width, final int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		xScale = (float) (width - 1) / 2.0f;
		yScale = (float) (height - 1) / 2.0f;
		xCenter = xScale + x;
		yCenter = yScale + y;
	}

	public final void setDepthRange(float near, float far) {
		// clamp depth range to legal values

		if (near < 0.0f)
			near = 0.0f;
		else if (near > 1.0f)
			near = 1.0f;

		if (far < 0.0f)
			far = 0.0f;
		else if (far > 1.0f)
			far = 1.0f;

		zNear = near;
		zFar = far;
		zScale = (far - near) / 2.0f;
		zCenter = zScale + near;
	}

	public static void perspectiveDivision(final GLVector4f clipCoordinates,
			final GLVector4f normalisedDeviceCoordinates) {
		final float invW = clipCoordinates.w != 0.0f ? 1.0f / clipCoordinates.w : 0.0f;

		normalisedDeviceCoordinates.x = clipCoordinates.x * invW;
		normalisedDeviceCoordinates.y = clipCoordinates.y * invW;
		normalisedDeviceCoordinates.z = clipCoordinates.z * invW;
		normalisedDeviceCoordinates.w = clipCoordinates.w;
	}

	/**
	 * Viewport Transformation from normalized device coordinates to window coordinates
	 */
	public final void normalizedDeviceToWindow(final GLVector4f normalisedDeviceCoordinates,
			final GLVector4f windowCoordinates) {
		windowCoordinates.x = xScale * normalisedDeviceCoordinates.x + xCenter;
		windowCoordinates.y = -yScale * normalisedDeviceCoordinates.y + yCenter;
		windowCoordinates.z = zScale * normalisedDeviceCoordinates.z + zCenter;
		windowCoordinates.w = normalisedDeviceCoordinates.w;
	}

	public final GLViewportState get() {
		final GLViewportState s = new GLViewportState();
		s.set(this);

		return s;
	}

	public final void set(final GLViewportState s) {
		x = s.x;
		y = s.y;
		width = s.width;
		height = s.height;

		zNear = s.zNear;
		zFar = s.zFar;

		xScale = s.xScale;
		yScale = s.yScale;
		zScale = s.zScale;
		xCenter = s.xCenter;
		yCenter = s.yCenter;
		zCenter = s.zCenter;
	}
}
