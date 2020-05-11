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

public class MorphingMesh extends Mesh {

	private VertexBuffer[] targets;
	private float[] weights;

	public MorphingMesh(VertexBuffer base, VertexBuffer[] targets, IndexBuffer[] submeshes, Appearance[] appearances) {
		super(base, submeshes, appearances);
		checkTargets(targets);
		this.targets = targets;
	}

	public MorphingMesh(VertexBuffer base, VertexBuffer[] targets, IndexBuffer submeshes, Appearance appearances) {
		super(base, submeshes, appearances);
		checkTargets(targets);
		this.targets = targets;
	}

	public VertexBuffer getMorphTarget(int index) {
		return targets[index];
	}

	public int getMorphTargetCount() {
		return targets.length;
	}

	public void setWeights(float[] weights) {
		if (weights == null) {
			throw new NullPointerException("Weights must not be null");
		}
		this.weights = weights;
	}

	public void getWeights(float[] weights) {
		if (weights == null) {
			throw new NullPointerException("Weights must not be null");
		}
		if (weights.length < getMorphTargetCount()) {
			throw new IllegalArgumentException("Number of weights must be greater or equal to getMorphTargetCount()");
		}
		System.arraycopy(this.weights, 0, weights, 0, this.weights.length);
	}

	private void checkTargets(VertexBuffer[] targets) {

		if (targets == null) {
			throw new NullPointerException();
		}
		if (targets.length == 0) {
			throw new IllegalArgumentException("Skeleton already has a parent");
		}

		boolean hasArrayNullElement = false;
		for (int i = 0; i < targets.length; i++) {
			if (targets[i] == null) {
				hasArrayNullElement = true;
			}
		}
		if (hasArrayNullElement) {
			throw new IllegalArgumentException("Target array contains null elements");
		}

	}

	boolean isCompatible(AnimationTrack track) {
		switch (track.getTargetProperty()) {
		case AnimationTrack.MORPH_WEIGHTS:
			return true;
		default:
			return super.isCompatible(track);
		}
	}

}
