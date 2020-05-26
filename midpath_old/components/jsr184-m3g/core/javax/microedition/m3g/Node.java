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

public abstract class Node extends Transformable {

	public static final int NONE = 144;
	public static final int ORIGIN = 145;
	public static final int X_AXIS = 146;
	public static final int Y_AXIS = 147;
	public static final int Z_AXIS = 148;

	protected float alpha = 1.0f;
	protected Node parent = null;
	protected int scope = -1;
	protected boolean pickingEnabled = true;
	protected boolean renderingEnabled = true;
	
	void duplicate(Node copy) {
		duplicate((Transformable)copy);
		copy.alpha = alpha;
		copy.parent = null;
		copy.scope = scope;
		copy.pickingEnabled = pickingEnabled;
		copy.renderingEnabled = renderingEnabled;
	}

	public final void align(Node reference) {
		// TODO
	}

	public float getAlphaFactor() {
		return alpha;
	}

	public Node getParent() {
		return parent;
	}

	public int getScope() {
		return scope;
	}

	public boolean getTransformTo(Node target, Transform transform) {
		if (target == null)
			throw new NullPointerException("target can not be null");
		if (transform == null)
			throw new NullPointerException("transform can not be null");

		Node node = this;
		
		int nodeDepth = node.getDepth();
		int targetDepth = target.getDepth();

		Transform tmp = new Transform();
		Transform targetTransform = new Transform();
		Transform nodeTransform = new Transform();

		// Iterate up in the tree until the paths merge
		while (node != target) {
			int nd = nodeDepth;
			if (nodeDepth >= targetDepth) {
				node.getCompositeTransform(tmp);
				tmp.postMultiply(nodeTransform);
				nodeTransform.set(tmp);

				node = node.getParent();
				--nodeDepth;
			}
			if (targetDepth >= nd) {
				target.getCompositeTransform(tmp);
				tmp.postMultiply(targetTransform);
				targetTransform.set(tmp);

				target = target.getParent();
				--targetDepth;
			}
		}
		// did we find a path? (if one is null, actually both should be null)
		if (node == null || target == null)
			return false;

		//nodeTransform.invert();
		transform.set(nodeTransform);
		transform.postMultiply(targetTransform);
		return true;
	}

	public boolean isPickingEnabled() {
		return pickingEnabled;
	}

	public boolean isRenderingEnabled() {
		return renderingEnabled;
	}

	public void setAlignment(Node zRef, int zTarget, Node yRef, int yTarget) {
		if (((zTarget != Node.NONE) &&  (zTarget != Node.Z_AXIS)) || ((yTarget != Node.NONE) &&  (yTarget != Node.Y_AXIS)))
			throw new IllegalArgumentException();
		if ((zRef == yRef) && (zTarget != NONE || yTarget != NONE))
			throw new IllegalArgumentException();
		if (zRef == this || yRef == this)
			throw new IllegalArgumentException("can not use this as refnode");
		// TODO
	}

	public void setAlphaFactor(float alphaFactor) {
		if (alphaFactor < 0 || alphaFactor > 1)
			throw new IllegalArgumentException("alphaFactor must be in [0,1]");
		alpha = alphaFactor;
	}

	public void setPickingEnable(boolean enable) {
		pickingEnabled = enable;
	}

	public void setRenderingEnable(boolean enable) {
		renderingEnabled = enable;
	}

	public void setScope(int scope) {
		this.scope = scope;
	}

	private int getDepth() {
		int depth = 0;
		Node node = this;
		while (node != null) {
			++depth;
			node = node.getParent();
		}
		return depth;
	}
	
	boolean isCompatible(AnimationTrack track) {
		switch (track.getTargetProperty()) {
		case AnimationTrack.ALPHA:
		case AnimationTrack.VISIBILITY:
		case AnimationTrack.PICKABILITY:
		    return true;
		default:
		    return super.isCompatible(track);
		}
	}
	
}
