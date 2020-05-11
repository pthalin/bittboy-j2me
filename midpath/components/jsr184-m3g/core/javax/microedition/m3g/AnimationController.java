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

public class AnimationController extends Object3D {
	
	private int activeIntervalStart = 0;
	private int activeIntervalEnd = 0;
	private float speed = 1.0f;
	private int referenceWorldTime = 0;
	private float referenceSequenceTime = 0;
	private float weight = 1.0f;
	
	Object3D duplicateImpl() {
		AnimationController copy = new AnimationController();
		copy.activeIntervalStart = activeIntervalStart;
		copy.activeIntervalEnd = activeIntervalEnd;
		copy.speed = speed;
		copy.referenceWorldTime = referenceWorldTime;
		copy.referenceSequenceTime = referenceSequenceTime;
		copy.weight = weight;
		return copy;
	}
	
	public void setActiveInterval(int start,
            int end) {
		
	    if (start > end) {
	    	 throw new IllegalArgumentException("Start time must be inferior to end time");
	    }
		
		activeIntervalStart = start;
		activeIntervalEnd = end;
	}
	
	public int getActiveIntervalStart() {
		return activeIntervalStart;
	}
	
	public int getActiveIntervalEnd() {
		return activeIntervalEnd;
	}
	
	public void setSpeed(float speed,
            int worldTime) {
		this.referenceWorldTime = worldTime;
		this.referenceSequenceTime = getPosition(worldTime);
		this.speed = speed;
	}
	
	public float getSpeed() {
		return speed;
	}
	
	public void setPosition(float sequenceTime,
            int worldTime) {
		this.referenceSequenceTime = sequenceTime;
		this.referenceWorldTime = worldTime;
	}
	
	public float getPosition(int worldTime) {

		int tw = worldTime;
		int tsref = referenceWorldTime;
		float twref = referenceSequenceTime;
		float s = getSpeed();
		
		float ts = tsref  + s * (tw  - twref ) ;
		
		return ts;
	}
	
	public int getRefWorldTime() {
		return referenceWorldTime;
	}
	
	public void setWeight(float weight) {
		
		if (weight < 0) {
	    	 throw new IllegalArgumentException("Weight must be positive or zero");
	    }
		
		this.weight = weight;
	}
	
	public float getWeight() {
		return weight;
	}

}
