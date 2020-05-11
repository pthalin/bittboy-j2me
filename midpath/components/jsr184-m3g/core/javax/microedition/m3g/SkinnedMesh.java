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

public class SkinnedMesh extends Mesh {

	private Group skeleton;

	public SkinnedMesh(VertexBuffer vertices, IndexBuffer[] submeshes, Appearance[] appearances, Group skeleton) {
		super(vertices, submeshes, appearances);
		checkSkeleton(skeleton);
		this.skeleton = skeleton;
	}

	public SkinnedMesh(VertexBuffer vertices, IndexBuffer submeshes, Appearance appearances, Group skeleton) {
		super(vertices, submeshes, appearances);
		checkSkeleton(skeleton);
		this.skeleton = skeleton;
	}

	public void addTransform(Node bone, int weight, int firstVertex, int numVertices) {
	    if (bone == null) {
	    	throw new NullPointerException();
	    }
	    // TODO throw IllegalArgumentException if bone is not the skeleton Group or one of its descendants 
	    if ((weight <= 0) || (numVertices <= 0)) {
	    	throw new IllegalArgumentException();
	    }
	    if ((firstVertex < 0) || (firstVertex + numVertices > 65535)) {
	    	throw new IndexOutOfBoundsException();
	    }
	    //TODO throw ArithmeticException if the at-rest transformation cannot be computed
	    //TODO Implement behavior
	}

	public void getBoneTransform(Node bone, Transform transform) {
		if ((bone == null) || (transform == null)) {
	    	throw new NullPointerException();
	    }
		// TODO throw IllegalArgumentException if bone is not in the skeleton group of this mesh
		// TODO Implement behavior
	}

	public int getBoneVertices(Node bone, int[] indices, float[] weights) {
		if (bone == null) {
	    	throw new NullPointerException();
	    }
		// TODO throw IllegalArgumentException if bone is not in the skeleton group of this mesh
		// TODO throw IllegalArgumentException if neither of indices and weights is null, and the 
		//      length of either is less than the number of vertices queried
		
		// TODO Implement behavior
		return 0;
	}

	public Group getSkeleton() {
		return skeleton;
	}
	
	private void checkSkeleton(Group skeleton) {
		
		if (skeleton == null) {
			throw new NullPointerException();
		}
		
		if (skeleton.getParent() != null) {
			throw new IllegalArgumentException("Skeleton already has a parent");
		}
		
	    // TODO throw IllegalArgumentException if skeleton is a World node 
		// Note: checking if skeleton has a parent is not sufficient ? 
	}

}
