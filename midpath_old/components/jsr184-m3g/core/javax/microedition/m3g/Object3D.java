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

import java.util.Vector;

public abstract class Object3D {

	protected int userID = 0;
	protected Object userObject = null;

	Vector animationTracks = new Vector();

	public final Object3D duplicate() {
		return duplicateImpl();
	}

	abstract Object3D duplicateImpl();

	public Object3D find(int userID) {
		// TODO
		return null;
	}

	public int getReferences(Object3D[] references) throws IllegalArgumentException {
		// TODO
		return 0;
	}

	public int getUserID() {
		return userID;
	}

	public void setUserID(int userID) {
		this.userID = userID;
	}

	public Object getUserObject() {
		return this.userObject;
	}

	public void setUserObject(Object userObject) {
		this.userObject = userObject;
	}

	public void addAnimationTrack(AnimationTrack animationTrack) {
		
		if (animationTrack == null) {
			throw new NullPointerException();
		}
		if ((!isCompatible(animationTrack)) || animationTracks.contains(animationTrack)) {
			throw new IllegalArgumentException("AnimationTrack is already existing or incompatible");
		}
			
		// Check if AnimationTrack targeting the same property as a previously added AnimationTrack have the same keyframe size
		int newTrackTarget = animationTrack.getTargetProperty();
		int components = animationTrack.getKeyframeSequence().getComponentCount();
		for (int i = 0; i < animationTracks.size(); i++) {
			AnimationTrack track = (AnimationTrack) animationTracks.elementAt(i);
			if (track.getTargetProperty() == newTrackTarget && (track.getKeyframeSequence().getComponentCount() != components)) {
				throw new IllegalArgumentException();
			}
		}
		
		animationTracks.addElement(animationTrack);
	}

	public AnimationTrack getAnimationTrack(int index) {
		return (AnimationTrack) animationTracks.elementAt(index);
	}

	public void removeAnimationTrack(AnimationTrack animationTrack) {
		animationTracks.removeElement(animationTrack);
	}

	public int getAnimationTrackCount() {
		return animationTracks.size();
	}

	public final int animate(int time) {
		// TODO
		return 0;
	}
	
	boolean isCompatible(AnimationTrack animationtrack) {
		return false;
	}

}
