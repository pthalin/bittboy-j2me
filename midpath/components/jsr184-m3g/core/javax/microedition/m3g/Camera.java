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
package javax.microedition.m3g;

import org.thenesis.m3g.engine.vecMath.Constants;

public class Camera extends Node {
	public static final int GENERIC = 48;
	public static final int PARALLEL = 49;
	public static final int PERSPECTIVE = 50;

	private int projectionType = GENERIC;
	private float fovy;
	private float aspectRatio;
	private float near;
	private float far;
	private Transform transform = new Transform();

	public Camera() {
	}

	Object3D duplicateImpl() {
		Camera copy = new Camera();
		duplicate((Node) copy);
		copy.projectionType = projectionType;
		copy.fovy = fovy;
		copy.aspectRatio = aspectRatio;
		copy.near = near;
		copy.far = far;
		copy.transform = new Transform(transform);
		return copy;
	}

	public void setParallel(float fovy, float aspectRatio, float near, float far) {
		this.projectionType = PARALLEL;
		this.fovy = fovy;
		this.aspectRatio = aspectRatio;
		this.near = near;
		this.far = far;
	}

	public void setPerspective(float fovy, float aspectRatio, float near, float far) {
		this.projectionType = PERSPECTIVE;
		this.fovy = fovy;
		this.aspectRatio = aspectRatio;
		this.near = near;
		this.far = far;
	}

	public void setGeneric(Transform transform) {
		this.projectionType = GENERIC;
		this.transform.set(transform);
	}

	public int getProjection(Transform transform) throws ArithmeticException {
		if (transform != null) {
			if (projectionType == GENERIC) {
				transform.set(this.transform);
			} else if (projectionType == PARALLEL) {
				if (far == near)
					throw new ArithmeticException(
							"Unable to compute projection matrix. Illegal parameters (near == far).");

				float[] m = new float[16];
				m[1] = m[2] = m[3] = m[4] = m[6] = m[7] = m[8] = m[9] = m[12] = m[13] = m[14] = 0;
				m[0] = 2 / (aspectRatio * fovy);
				m[5] = 2 / fovy;
				m[10] = -2 / (far - near);
				m[11] = -(near + far) / (far - near);
				m[15] = 1;
				transform.set(m);
			} else if (projectionType == PERSPECTIVE) {
				if (far == near)
					throw new ArithmeticException(
							"Unable to compute projection matrix. Illegal parameters (near == far).");

				float h = (float) Math.tan(fovy * Constants.TO_RADIANS / 2);

				float[] m = new float[16];
				m[1] = m[2] = m[3] = m[4] = m[6] = m[7] = m[8] = m[9] = m[12] = m[13] = m[14] = 0;
				m[0] = 1 / (aspectRatio * h);
				m[5] = 1 / h;
				m[10] = -(near + far) / (far - near);
				m[11] = -2 * near * far / (far - near);
				m[14] = -1;
				m[15] = 0;
				transform.set(m);
			}
		}
		return projectionType;
	}

	public int getProjection(float[] params) throws IllegalArgumentException {
		if (params != null) {
			if (params.length < 4)
				throw new IllegalArgumentException("Params");

			params[0] = fovy;
			params[1] = aspectRatio;
			params[2] = near;
			params[3] = far;
		}
		return projectionType;
	}

	boolean isCompatible(AnimationTrack track) {
		switch (track.getTargetProperty()) {
		case AnimationTrack.FAR_DISTANCE:
		case AnimationTrack.FIELD_OF_VIEW:
		case AnimationTrack.NEAR_DISTANCE:
			return true;
		default:
			return super.isCompatible(track);
		}
	}
}
