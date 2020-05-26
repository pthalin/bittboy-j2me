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

import org.thenesis.m3g.engine.util.Tools;

public class Mesh extends Node {
	private VertexBuffer vertices;
	private Vector submeshes = new Vector();
	private Vector appearances = new Vector();

	private Mesh() {
	}

	public Mesh(VertexBuffer vertices, IndexBuffer submesh, Appearance appearance) {
		if ((vertices == null) || (submesh == null)) {
			throw new NullPointerException();
		}
		this.vertices = vertices;
		this.submeshes.addElement(submesh);
		this.appearances.addElement(appearance);
	}

	public Mesh(VertexBuffer vertices, IndexBuffer[] submeshes, Appearance[] appearances) {
		if ((vertices == null) || (submeshes == null) || hasArrayNullElement(submeshes)) {
			throw new NullPointerException();
		}
		if ((submeshes.length == 0) || ((appearances != null) && (appearances.length < submeshes.length))) {
			throw new IllegalArgumentException();
		}
		this.vertices = vertices;
		for (int i = 0; i < submeshes.length; ++i)
			this.submeshes.addElement(submeshes[i]);
		for (int i = 0; i < appearances.length; ++i)
			this.appearances.addElement(appearances[i]);
	}

	Object3D duplicateImpl() {
		Mesh copy = new Mesh();
		duplicate((Node) copy);
		copy.vertices = vertices;
		copy.submeshes = submeshes;
		copy.appearances = Tools.clone(appearances);
		return copy;
	}

	public Appearance getAppearance(int index) {
		return (Appearance) appearances.elementAt(index);
	}

	public IndexBuffer getIndexBuffer(int index) {
		return (IndexBuffer) submeshes.elementAt(index);
	}

	public int getSubMeshCount() {
		return submeshes.size();
	}

	public VertexBuffer getVertexBuffer() {
		return vertices;
	}

	public void setAppearance(int index, Appearance appearance) {
		appearances.setElementAt(appearance, index);
	}

	public int getReferences(Object3D[] references) throws IllegalArgumentException {
		int parentCount = super.getReferences(references);

		if (vertices != null) {
			if (references != null)
				references[parentCount] = vertices;
			++parentCount;
		}

		for (int i = 0; i < submeshes.size(); ++i) {
			if (references != null)
				references[parentCount] = (Object3D) submeshes.elementAt(i);
			++parentCount;
		}

		for (int i = 0; i < appearances.size(); ++i) {
			if (references != null)
				references[parentCount] = (Object3D) appearances.elementAt(i);
			++parentCount;
		}

		return parentCount;
	}

	private boolean hasArrayNullElement(IndexBuffer[] buffer) {
		for (int i = 0; i < buffer.length; i++) {
			if (buffer[i] == null) {
				return true;
			}
		}
		return false;
	}
}
