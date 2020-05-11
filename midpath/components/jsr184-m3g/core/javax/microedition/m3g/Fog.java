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

import javax.microedition.khronos.opengles.GL10;

import org.thenesis.m3g.engine.util.Color;

public class Fog extends Object3D {

	public static final int EXPONENTIAL = 80;
	public static final int LINEAR = 81;

	private int color = 0;
	private int mode = LINEAR;
	private float density = 1.0f;
	private float nearDistance = 0.0f;
	private float farDistance = 1.0f;
	
	Object3D duplicateImpl() {
		Fog copy = new Fog();
		copy.color = color;
		copy.mode = mode;
		copy.density = density;
		copy.nearDistance = nearDistance;
		copy.farDistance = farDistance;
		return copy;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getColor() {
		return color;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getMode() {
		return mode;
	}

	public void setLinear(float near, float far) {
		this.nearDistance = near;
		this.farDistance = far;
	}

	public void setDensity(float density) {
		this.density = density;
	}

	public float getDensity() {
		return density;
	}

	public void setNearDistance(float nearDistance) {
		this.nearDistance = nearDistance;
	}

	public float getNearDistance() {
		return nearDistance;
	}

	public void setFarDistance(float farDistance) {
		this.farDistance = farDistance;
	}

	public float getFarDistance() {
		return farDistance;
	}

	void setupGL(GL10 gl) {
		gl.glFogf(GL10.GL_FOG_MODE, getGLFogMode(this.mode));
		gl.glFogfv(GL10.GL_FOG_COLOR, Color.intToFloatArray(this.color), 0);
		gl.glFogf(GL10.GL_FOG_DENSITY, this.density);
		gl.glFogf(GL10.GL_FOG_START, this.nearDistance);
		gl.glFogf(GL10.GL_FOG_END, this.farDistance);
		gl.glEnable(GL10.GL_FOG);
	}

	private int getGLFogMode(int mode) {
		switch (mode) {
		case EXPONENTIAL:
			return GL10.GL_EXP;
		default:
			return GL10.GL_LINEAR;
		}
	}
	
	boolean isCompatible(AnimationTrack track) {
		switch (track.getTargetProperty()) {
		case AnimationTrack.COLOR:
		case AnimationTrack.DENSITY:
		case AnimationTrack.FAR_DISTANCE:
		case AnimationTrack.NEAR_DISTANCE:
			return true;
		default:
			return super.isCompatible(track);
		}
	}
}
