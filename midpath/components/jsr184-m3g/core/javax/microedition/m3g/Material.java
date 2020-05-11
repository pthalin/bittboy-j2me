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

public class Material extends Object3D {
	public static final int AMBIENT = 1024;
	public static final int DIFFUSE = 2048;
	public static final int EMISSIVE = 4096;
	public static final int SPECULAR = 8192;

	private int ambientColor = 0x00333333;
	private int diffuseColor = 0xFFCCCCCC;
	private int emissiveColor = 0;
	private int specularColor = 0;
	private float shininess = 10.0f;
	private boolean isVertexColorTrackingEnabled = false;

	public Material() {
	}

	Object3D duplicateImpl() {
		Material copy = new Material();
		copy.ambientColor = ambientColor;
		copy.diffuseColor = diffuseColor;
		copy.emissiveColor = emissiveColor;
		copy.specularColor = specularColor;
		copy.shininess = shininess;
		copy.isVertexColorTrackingEnabled = isVertexColorTrackingEnabled;
		return copy;
	}

	public void setColor(int target, int color) {
		if ((target & AMBIENT) != 0)
			this.ambientColor = color;
		if ((target & DIFFUSE) != 0)
			this.diffuseColor = color;
		if ((target & EMISSIVE) != 0)
			this.emissiveColor = color;
		if ((target & SPECULAR) != 0)
			this.specularColor = color;
	}

	public int getColor(int target) {
		if (target == AMBIENT)
			return ambientColor;
		else if (target == DIFFUSE)
			return diffuseColor;
		else if (target == EMISSIVE)
			return emissiveColor;
		else if (target == SPECULAR)
			return specularColor;
		throw new IllegalArgumentException("Invalid color target");
	}

	public void setShininess(float shininess) {
		this.shininess = shininess;
	}

	public float getShininess() {
		return shininess;
	}

	public void setVertexColorTrackingEnable(boolean isVertexColorTrackingEnabled) {
		this.isVertexColorTrackingEnabled = isVertexColorTrackingEnabled;
	}

	public boolean isVertexColorTrackingEnabled() {
		return isVertexColorTrackingEnabled;
	}

	void setupGL(GL10 gl, int lightTarget) {
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_EMISSION, Color.intToFloatArray(emissiveColor), 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, Color.intToFloatArray(ambientColor), 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, Color.intToFloatArray(diffuseColor), 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, Color.intToFloatArray(specularColor), 0);
		gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, shininess);
	}

	boolean isCompatible(AnimationTrack track) {
		switch (track.getTargetProperty()) {
		case AnimationTrack.ALPHA:
		case AnimationTrack.AMBIENT_COLOR:
		case AnimationTrack.DIFFUSE_COLOR:
		case AnimationTrack.EMISSIVE_COLOR:
		case AnimationTrack.SHININESS:
		case AnimationTrack.SPECULAR_COLOR:
			return true;
		default:
			return super.isCompatible(track);
		}
	}
}
