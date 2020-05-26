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

import org.thenesis.m3g.engine.util.StrictMath;
import org.thenesis.m3g.engine.vecMath.Vector3;

public abstract class Transformable extends Object3D {
	//private boolean hasComponentTransform;
	private Vector3 translation = new Vector3();
	private Vector3 scale = new Vector3(1, 1, 1);
	private Transform orientation = new Transform();
	private Transform transform = new Transform();
	
	void duplicate(Transformable copy) {
		copy.translation = new Vector3(translation);
		copy.scale = new Vector3(scale);
		copy.orientation = new Transform(orientation);
		copy.transform = new Transform(transform);
	}

	public void getCompositeTransform(Transform transform) {
		if (transform == null)
			throw new NullPointerException("transform can not be null");

		// transform = T R S M

		// Combine translation and rotation (TR)
		float[] m = new float[16];
		orientation.get(m);
		m[3] = translation.x;
		m[7] = translation.y;
		m[11] = translation.z;
		transform.set(m);

		// Apply scale (S)
		transform.postScale(scale.x, scale.y, scale.z);

		// Apply custom (M)
		transform.postMultiply(this.transform);
	}

	public void getOrientation(float[] angleAxis) {
		if (angleAxis == null)
			throw new NullPointerException("angleAxis can not be null");
		if (angleAxis.length < 4)
			throw new IllegalArgumentException("length must be greater than 3");

		float[] m = new float[16];
		orientation.get(m);

		Vector3 axis = new Vector3(m[6] - m[9], m[8] - m[2], m[1] - m[4]);
		axis.normalize();

		float angle = (float) StrictMath.acos(0.5 * (m[0] + m[5] + m[10] - 1));

		angleAxis[0] = angle;
		angleAxis[1] = axis.x;
		angleAxis[2] = axis.y;
		angleAxis[3] = axis.z;

		// TODO: Handle singularities for angle = 0 and angle = 180 degrees
	}

	public void getScale(float[] xyz) {
		if (xyz == null)
			throw new NullPointerException("xyz can not be null");
		if (xyz.length < 3)
			throw new IllegalArgumentException("length must be greater than 2");

		xyz[0] = scale.x;
		xyz[1] = scale.y;
		xyz[2] = scale.z;
	}

	public void getTransform(Transform transform) {
		if (transform == null)
			throw new NullPointerException("transform can not be null");

		transform.set(this.transform);
	}

	public void getTranslation(float[] xyz) {
		if (xyz == null)
			throw new NullPointerException("xyz can not be null");
		if (xyz.length < 3)
			throw new IllegalArgumentException("length must be greater than 2");

		xyz[0] = translation.x;
		xyz[1] = translation.y;
		xyz[2] = translation.z;
	}

	public void postRotate(float angle, float ax, float ay, float az) {
		orientation.postRotate(angle, ax, ay, az);
	}

	public void preRotate(float angle, float ax, float ay, float az) {
		Transform t = new Transform();
		t.postRotate(angle, ax, ay, az);
		t.postMultiply(orientation);
		orientation.set(t);
	}

	public void scale(float sx, float sy, float sz) {
		scale.x *= sx;
		scale.y *= sy;
		scale.z *= sz;
	}

	public void setOrientation(float angle, float ax, float ay, float az) {
		orientation.setIdentity();
		orientation.postRotate(angle, ax, ay, az);
	}

	public void setScale(float sx, float sy, float sz) {
		scale.set(sx, sy, sz);
	}

	public void setTransform(Transform transform) {
		if (transform == null)
			throw new NullPointerException("transform can not be null");

		this.transform.set(transform);
	}

	public void setTranslation(float tx, float ty, float tz) {
		translation.set(tx, ty, tz);
	}

	public void translate(float tx, float ty, float tz) {
		translation.x += tx;
		translation.y += ty;
		translation.z += tz;
	}
	
	boolean isCompatible(AnimationTrack track) {
		switch (track.getTargetProperty()) {
		case AnimationTrack.ORIENTATION:
		case AnimationTrack.SCALE:
		case AnimationTrack.TRANSLATION:
		    return true;
		default:
		    return super.isCompatible(track);
		}
	}
	
}
