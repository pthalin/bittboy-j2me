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

public class AnimationTrack extends Object3D {

	public static final int ALPHA = 256;
	public static final int AMBIENT_COLOR = 257;
	public static final int COLOR = 258;
	public static final int CROP = 259;
	public static final int DENSITY = 260;
	public static final int DIFFUSE_COLOR = 261;
	public static final int EMISSIVE_COLOR = 262;
	public static final int FAR_DISTANCE = 263;
	public static final int FIELD_OF_VIEW = 264;
	public static final int INTENSITY = 265;
	public static final int MORPH_WEIGHTS = 266;
	public static final int NEAR_DISTANCE = 267;
	public static final int ORIENTATION = 268;
	public static final int PICKABILITY = 269;
	public static final int SCALE = 270;
	public static final int SHININESS = 271;
	public static final int SPECULAR_COLOR = 272;
	public static final int SPOT_ANGLE = 273;
	public static final int SPOT_EXPONENT = 274;
	public static final int TRANSLATION = 275;
	public static final int VISIBILITY = 276;

	private KeyframeSequence sequence;
	private int property;
	private AnimationController controller;

	public AnimationTrack(KeyframeSequence sequence, int property) {
		if (sequence == null) {
			throw new NullPointerException("Sequence must not be null");
		}
		if ((property < ALPHA) || (property > VISIBILITY)) {
			throw new IllegalArgumentException("Unknown property");
		}
		if (!isCompatible(sequence.getComponentCount(), property)) {
			throw new IllegalArgumentException("Sequence is not compatible with property");
		}
		this.sequence = sequence;
		this.property = property;
	}

	Object3D duplicateImpl() {
		AnimationTrack copy = new AnimationTrack(sequence, property);
		copy.controller = controller;
		return copy;
	}

	public AnimationController getController() {
		return controller;
	}

	public void setController(AnimationController controller) {
		this.controller = controller;
	}

	public int getTargetProperty() {
		return property;
	}

	public KeyframeSequence getKeyframeSequence() {
		return sequence;
	}

	private boolean isCompatible(int components, int property) {
		switch (property) {
		case ALPHA:
		case DENSITY:
		case FAR_DISTANCE:
		case FIELD_OF_VIEW:
		case INTENSITY:
		case NEAR_DISTANCE:
		case PICKABILITY:
		case SHININESS:
		case SPOT_ANGLE:
		case SPOT_EXPONENT:
		case VISIBILITY:
			return components == 1;
		case CROP:
			return components == 2 || components == 4;
		case AMBIENT_COLOR:
		case COLOR:
		case DIFFUSE_COLOR:
		case EMISSIVE_COLOR:
		case SPECULAR_COLOR:
		case TRANSLATION:
			return components == 3;
		case SCALE:
			return components == 1 || components == 3;
		case ORIENTATION:
			return components == 4;
		case MORPH_WEIGHTS:
			return components > 0;
		default:
			return false; // Shouldn't occur
		}
	}

}
