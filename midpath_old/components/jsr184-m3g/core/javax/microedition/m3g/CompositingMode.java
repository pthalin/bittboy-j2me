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

public class CompositingMode extends Object3D {

	public static final int ALPHA = 64;
	public static final int ALPHA_ADD = 65;
	public static final int MODULATE = 66;
	public static final int MODULATE_X2 = 67;
	public static final int REPLACE = 68;

	/* Attributes set to default values */
	private boolean depthTestEnabled = true; 
	private boolean depthWriteEnabled = true; 
	private boolean colorWriteEnabled = true; 
	private boolean alphaWriteEnabled = true;
	private int blending = REPLACE;
	private float alphaThreshold = 0.0f; 
	private float depthOffsetFactor = 0.0f;
	private float depthOffsetUnits = 0.0f;

	public CompositingMode() {
		depthTestEnabled = true;
		depthWriteEnabled = true;
		colorWriteEnabled = true;
		alphaWriteEnabled = true;
	}
	
	Object3D duplicateImpl() {
		CompositingMode copy = new CompositingMode();
		copy.depthTestEnabled = depthTestEnabled;
		copy.depthWriteEnabled = depthWriteEnabled;
		copy.colorWriteEnabled = colorWriteEnabled;
		copy.alphaWriteEnabled = alphaWriteEnabled;
		copy.blending = blending;
		copy.alphaThreshold = alphaThreshold;
		copy.depthOffsetFactor = depthOffsetFactor;
		copy.depthOffsetUnits = depthOffsetUnits;
		return copy;
	}

	public void setDepthTestEnabled(boolean depthTestEnabled) {
		this.depthTestEnabled = depthTestEnabled;
	}

	public boolean isDepthTestEnabled() {
		return depthTestEnabled;
	}

	public void setDepthWriteEnabled(boolean depthWriteEnabled) {
		this.depthWriteEnabled = depthWriteEnabled;
	}

	public boolean isDepthWriteEnabled() {
		return depthWriteEnabled;
	}

	public void setColorWriteEnabled(boolean colorWriteEnabled) {
		this.colorWriteEnabled = colorWriteEnabled;
	}

	public boolean isColorWriteEnabled() {
		return colorWriteEnabled;
	}

	public void setAlphaWriteEnabled(boolean alphaWriteEnabled) {
		this.alphaWriteEnabled = alphaWriteEnabled;
	}

	public boolean isAlphaWriteEnabled() {
		return alphaWriteEnabled;
	}

	public void setBlending(int blending) {
		this.blending = blending;
	}

	public int getBlending() {
		return blending;
	}

	public void setAlphaThreshold(float alphaThreshold) {
		this.alphaThreshold = alphaThreshold;
	}

	public float getAlphaThreshold() {
		return alphaThreshold;
	}

	public void setDepthOffsetFactor(float depthOffsetFactor) {
		this.depthOffsetFactor = depthOffsetFactor;
	}

	public float getDepthOffsetFactor() {
		return depthOffsetFactor;
	}

	public void setDepthOffsetUnits(float depthOffsetUnits) {
		this.depthOffsetUnits = depthOffsetUnits;
	}

	public float getDepthOffsetUnits() {
		return depthOffsetUnits;
	}

	void setupGL(GL10 gl, boolean depthBufferEnabled) {
		// TODO: move to one-time-initilize func
		gl.glDepthFunc(GL10.GL_LEQUAL);
		//gl.glBlendEquation(GL.GL_FUNC_ADD); // TODO Replace by an OES 1.0 call

		// Setup depth testing		
		if (depthBufferEnabled && depthTestEnabled)
			gl.glEnable(GL10.GL_DEPTH_TEST);
		else
			gl.glDisable(GL10.GL_DEPTH_TEST);

		// Setup depth and color writes
		gl.glDepthMask(depthWriteEnabled);
		gl.glColorMask(colorWriteEnabled, colorWriteEnabled, colorWriteEnabled, alphaWriteEnabled);

		// Setup alpha testing		
		if (alphaThreshold > 0) {
			gl.glAlphaFunc(GL10.GL_GEQUAL, alphaThreshold);
			gl.glEnable(GL10.GL_ALPHA_TEST);
		} else
			gl.glDisable(GL10.GL_ALPHA_TEST);

		// Setup blending
		if (blending != REPLACE) {
			switch (blending) {
			case ALPHA_ADD:
				gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
				break;
			case ALPHA:
				gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
				break;
			case MODULATE:
				gl.glBlendFunc(GL10.GL_DST_COLOR, GL10.GL_ZERO);
				break;
			case MODULATE_X2:
				gl.glBlendFunc(GL10.GL_DST_COLOR, GL10.GL_SRC_COLOR);
			}
			gl.glEnable(GL10.GL_BLEND);
		} else
			gl.glDisable(GL10.GL_BLEND);

		// Setup depth offset
		if (depthOffsetFactor != 0 || depthOffsetUnits != 0) {
			gl.glPolygonOffset(depthOffsetFactor, depthOffsetUnits);
			gl.glEnable(GL10.GL_POLYGON_OFFSET_FILL);
		} else
			gl.glDisable(GL10.GL_POLYGON_OFFSET_FILL);
	}
}
