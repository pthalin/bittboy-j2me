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


public class World extends Group {
	private Camera activeCamera;
	private Background background;

	public World() {
	}
	
	Object3D duplicateImpl() {
		super.duplicateImpl();
		World copy = new World();
		copy.activeCamera = activeCamera;
		copy.background = background;
		return copy;
	}

	public Camera getActiveCamera() {
		return activeCamera;
	}

	public void setActiveCamera(Camera camera) {
		activeCamera = camera;
	}

	public Background getBackground() {
		return background;
	}

	public void setBackground(Background background) {
		this.background = background;
	}

	public int getReferences(Object3D[] references) throws IllegalArgumentException {
		int parentCount = super.getReferences(references);

		if (activeCamera != null) {
			if (references != null)
				references[parentCount] = activeCamera;
			++parentCount;
		}

		if (background != null) {
			if (references != null)
				references[parentCount] = background;
			++parentCount;
		}

		return parentCount;
	}
}
