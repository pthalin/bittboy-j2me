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

import org.thenesis.m3g.engine.util.Tools;

public class KeyframeSequence extends Object3D {

	public static final int CONSTANT = 192;
	public static final int LINEAR = 176;
	public static final int LOOP = 193;
	public static final int SLERP = 177;
	public static final int SPLINE = 178;
	public static final int SQUAD = 179;
	public static final int STEP = 180;

	private int repeatMode = CONSTANT;
	private int duration;
	private int validRangeFirst;
	private int validRangeLast;
	private int interpolationType;
	private int keyframeCount;
	private int componentCount;

	private float[][] keyFrames;
	private int[] keyFrameTimes;
	
	private KeyframeSequence() {
	}

	public KeyframeSequence(int numKeyframes, int numComponents, int interpolation) {

		if ((numKeyframes < 1) || (numComponents < 1)) {
			throw new IllegalArgumentException("Number of keyframes/components must be >= 1");
		}

		// Check given interpolation mode
		switch (interpolation) {
		case SLERP:
		case SQUAD:
			if (numComponents != 4) {
				throw new IllegalArgumentException("SLERP and SQUAD mode requires 4 components in each keyframe");
			}
			break;
		case STEP:
		case LINEAR:
		case SPLINE:
			break;
		default:
			throw new IllegalArgumentException("Unknown interpolation mode");
		}

		this.keyframeCount = numKeyframes;
		this.componentCount = numComponents;
		this.interpolationType = interpolation;

		// Initialize the sequence with default values
		keyFrames = new float[numKeyframes][numComponents];
		keyFrameTimes = new int[numKeyframes];
		validRangeFirst = 0;
		validRangeLast = numKeyframes - 1;

	}
	
	Object3D duplicateImpl() {
		KeyframeSequence copy = new KeyframeSequence();
		copy.repeatMode = repeatMode;
		copy.duration = duration;
		copy.validRangeFirst = validRangeFirst;
		copy.validRangeLast = validRangeLast;
		copy.interpolationType = interpolationType;
		copy.keyframeCount = keyframeCount;
		copy.componentCount = componentCount;
		copy.keyFrames = Tools.clone(keyFrames);
		copy.keyFrameTimes = Tools.clone(keyFrameTimes);
		return copy;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public void setKeyframe(int index, int time, float[] value) {

		if (value == null) {
			throw new NullPointerException("Keyframe value vector must not be null");
		}

		if ((index < 0) || (index >= keyframeCount)) {
			throw new IndexOutOfBoundsException();
		}

		if ((value.length < componentCount) || (time < 0)) {
			throw new IllegalArgumentException();
		}

		System.arraycopy(value, 0, keyFrames[index], 0, componentCount);
		keyFrameTimes[index] = time;

	}

	public int getKeyframe(int index, float[] value) {
		
		if ((index < 0) || (index >= keyframeCount)) {
			throw new IndexOutOfBoundsException();
		}
		
		if ((value != null) && (value.length < componentCount)) {
			throw new IllegalArgumentException();
		}

		if (value != null) {
			System.arraycopy(keyFrames[index], 0, value, 0, componentCount);
		}
		
		return keyFrameTimes[index];
	}

	public int getRepeatMode() {
		return repeatMode;
	}

	public void setRepeatMode(int repeatMode) {
		this.repeatMode = repeatMode;
	}

	public int getValidRangeFirst() {
		return validRangeFirst;
	}

	public void setValidRange(int first, int last) {

		if ((first < 0) || (first >= keyframeCount) || (last < 0) || (last >= keyframeCount)) {
			throw new IndexOutOfBoundsException("Invalid range");
		}

		validRangeFirst = first;
		validRangeLast = last;
	}

	public int getComponentCount() {
		return componentCount;
	}

	public int getInterpolationType() {
		return interpolationType;
	}

	public int getKeyframeCount() {
		return keyframeCount;
	}

	public int getValidRangeLast() {
		return validRangeLast;
	}

}
