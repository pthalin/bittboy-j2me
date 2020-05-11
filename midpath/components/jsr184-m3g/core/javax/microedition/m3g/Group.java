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

import java.util.Enumeration;
import java.util.Vector;

public class Group extends Node {

	protected Vector children;

	public Group() {
		children = new Vector();
	}
	
	Object3D duplicateImpl() {
		Group copy = new Group();
		duplicate((Node)copy);
		Enumeration e = children.elements();
		while(e.hasMoreElements()) {
			Node nodeCopy = (Node)((Object3D)e.nextElement()).duplicate();
			copy.addChild(nodeCopy);
		}
		return copy;
	}

	public void addChild(Node child) {
		if (child == null)
			throw new NullPointerException("child can not be null");
		if (child == this)
			throw new IllegalArgumentException("can not add self as child");
		//		if (child instanceof World)
		//			throw new IllegalArgumentException("node of type World can not be child");
		if (child.parent != null)
			throw new IllegalArgumentException("child already has a parent");

		// todo
		children.addElement(child);
		child.parent = this;
	}

	public Node getChild(int index) {
		return (Node) children.elementAt(index);
	}

	public int getChildCount() {
		return children.size();
	}

	public int getReferences(Object3D[] references) throws IllegalArgumentException {
		int parentCount = super.getReferences(references);
		if (references != null)
			for (int i = 0; i < children.size(); ++i)
				references[parentCount + i] = (Object3D) children.elementAt(i);
		return parentCount + children.size();
	}

	public boolean pick(int scope, float x, float y, Camera camera, RayIntersection ri) {
		return false;
	}

	public boolean pick(int scope, float ox, float oy, float oz, float dx, float dy, float dz, RayIntersection ri) {
		return false;
	}

	public void removeChild(Node child) {
		children.removeElement(child);
		child.parent = null;
	}
}
