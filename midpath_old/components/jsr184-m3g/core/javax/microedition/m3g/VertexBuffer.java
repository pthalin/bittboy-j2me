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

import java.nio.ByteBuffer;

import org.thenesis.m3g.engine.util.Tools;

public class VertexBuffer extends Object3D {

	private int numVertices = 0;
	private VertexArray positions = null;
	private VertexArray normals = null;
	private VertexArray colors = null;
	private VertexArray[] texCoords = null;

	private float[] positionBias = null;
	private float positionScale = 1.0f;
	private float[][] texCoordsBias = null;
	private float[] texCoordsScale = { 1, 1, 1 };
	private int maxTextureUnitIndex = 0;
	private int defaultColor = 0xFFFFFFFF;

	public VertexBuffer() {
		int numTextureUnits = 8;
		maxTextureUnitIndex = numTextureUnits - 1;
		texCoords = new VertexArray[numTextureUnits];

		positionBias = new float[3];
		texCoordsBias = new float[numTextureUnits][3];
		texCoordsScale = new float[numTextureUnits];
	}

	Object3D duplicateImpl() {
		// Copy references
		VertexBuffer copy = new VertexBuffer();
		copy.numVertices = numVertices;
		copy.positions = positions;
		copy.normals = normals;
		copy.colors = colors;

		// Copy data
		if (texCoords != null) {
			for (int i = 0; i < texCoords.length; i++) {
				if (texCoords[i] != null) {
					copy.texCoords[i] = (VertexArray) texCoords[i].duplicateImpl();
				}
			}
		}
		copy.positionBias = Tools.clone(positionBias);
		copy.positionScale = positionScale;
		copy.texCoordsBias = Tools.clone(texCoordsBias);
		copy.texCoordsScale = Tools.clone(texCoordsScale);

		copy.maxTextureUnitIndex = maxTextureUnitIndex;
		copy.defaultColor = defaultColor;
		return copy;
	}

	public void setPositions(VertexArray positions, float scale, float[] bias) {
		if (positions != null) {
			if (positions.getComponentCount() != 3)
				throw new IllegalArgumentException("positions must have component count of 3");
			if ((positions.getVertexCount() != getVertexCount()) && (numVertices > 0))
				throw new IllegalArgumentException(
						"number of vertices in positions does not match number of vertices in other arrays");
			if ((bias != null) && (bias.length < 3))
				throw new IllegalArgumentException("bias must be of length 3");

			this.numVertices = positions.getVertexCount();
			this.positions = positions;
			// save scale and bias, needed by setPositions
			this.positionScale = scale;

			if (bias == null)
				bias = new float[] { 0, 0, 0 };

			this.positionBias[0] = bias[0];
			this.positionBias[1] = bias[1];
			this.positionBias[2] = bias[2];
		} else {
			this.positions = positions;
			resetVertexCount();
		}
	}

	public void setTexCoords(int index, VertexArray texCoords, float scale, float[] bias) {
		if (index < 0 || index > maxTextureUnitIndex)
			throw new IndexOutOfBoundsException("index must be in [0," + maxTextureUnitIndex + "]");
		if (texCoords != null) {
			if ((texCoords.getComponentCount() != 2) && (texCoords.getComponentCount() != 3))
				throw new IllegalArgumentException("texcoord component count must be in [2,3]");
			if ((texCoords.getVertexCount() != getVertexCount()) && (numVertices > 0))
				throw new IllegalArgumentException(
						"number of vertices in positions does not match number of vertices in other arrays");
			if ((bias != null) && (bias.length < texCoords.getComponentCount()))
				throw new IllegalArgumentException("bias length must match number of components");

			this.numVertices = texCoords.getVertexCount();
			this.texCoords[index] = texCoords;
			// save scale and bias, needed by getTexCoords
			this.texCoordsScale[index] = scale;

			if (bias == null)
				bias = new float[] { 0, 0, 0 };
			this.texCoordsBias[index][0] = bias[0];
			this.texCoordsBias[index][1] = bias[1];
			this.texCoordsBias[index][2] = bias[2];
		} else {
			this.texCoords[index] = texCoords;
			resetVertexCount();
		}
	}

	public void setNormals(VertexArray normals) {
		if (normals != null) {
			if (normals.getComponentCount() != 3)
				throw new IllegalArgumentException("normals must have component count of 3");
			if ((normals.getVertexCount() != getVertexCount()) && (numVertices > 0))
				throw new IllegalArgumentException(
						"number of vertices in normals does not match number of vertices in other arrays");

			numVertices = normals.getVertexCount();
			this.normals = normals;
		} else {
			this.normals = normals;
			resetVertexCount();
		}
	}

	public void setColors(VertexArray colors) {
		if (colors != null) {
			// todo: fill actual vertexbuffer with colors

			if (colors.getComponentType() != 1)
				throw new IllegalArgumentException("colors must of type byte");
			if ((colors.getComponentCount() != 3) && (colors.getComponentCount() != 4))
				throw new IllegalArgumentException("color component count must be in [3,4]");
			if ((colors.getVertexCount() != getVertexCount()) && (numVertices > 0))
				throw new IllegalArgumentException(
						"number of vertices in colors does not match number of vertices in other arrays");

			numVertices = colors.getVertexCount();
			this.colors = colors;

			// Hack required because OpenGL ES 1.0 supports only RGBA colors
			if (colors.getComponentCount() == 3) {
				int count = colors.getVertexCount();
				byte[] srcBuffer = new byte[count * 3];
				colors.get(0, count, srcBuffer);
				byte[] dstBuffer = new byte[count * 4];
				for (int i = 0; i < count; i++) {
					int srcOffset = i * 3;
					int dstOffset = i * 4;
					dstBuffer[dstOffset] = srcBuffer[srcOffset];
					dstBuffer[dstOffset + 1] = srcBuffer[srcOffset + 1];
					dstBuffer[dstOffset + 2] = srcBuffer[srcOffset + 2];
					dstBuffer[dstOffset + 3] = -1;
				}

				ByteBuffer argbBuffer = ByteBuffer.allocateDirect(count * 4);
				argbBuffer.put(dstBuffer);
				colors.setARGBBuffer(argbBuffer);
			} else {
				// FIXME 
				colors.setARGBBuffer((ByteBuffer) colors.getBuffer());
			}

		} else {
			this.colors = colors;
			resetVertexCount();
		}
	}

	public VertexArray getPositions(float[] scaleBias) {
		if (this.positions != null && scaleBias != null) {
			if (scaleBias.length < 4)
				throw new IllegalArgumentException("scaleBias must be of length 4");
			scaleBias[0] = positionScale;
			scaleBias[1] = positionBias[0];
			scaleBias[2] = positionBias[1];
			scaleBias[3] = positionBias[2];
		}
		return this.positions;
	}

	public VertexArray getTexCoords(int index, float[] scaleBias) {
		if (index < 0 || index > maxTextureUnitIndex)
			throw new IndexOutOfBoundsException("index must be in [0," + maxTextureUnitIndex + "]");

		VertexArray texCoords = this.texCoords[index];
		if (texCoords != null && scaleBias != null) {
			if (scaleBias.length < (texCoords.getComponentCount() + 1))
				throw new IllegalArgumentException("scaleBias must be of length 4");
			scaleBias[0] = texCoordsScale[index];
			scaleBias[1] = texCoordsBias[index][0];
			scaleBias[2] = texCoordsBias[index][1];
			scaleBias[3] = texCoordsBias[index][2];
		}
		return texCoords;
	}

	public VertexArray getNormals() {
		return this.normals;
	}

	public VertexArray getColors() {
		return this.colors;
	}

	public int getVertexCount() {
		return this.numVertices;
	}

	public void setDefaultColor(int color) {
		this.defaultColor = color;
	}

	public int getDefaultColor() {
		return this.defaultColor;
	}

	private void resetVertexCount() {
		if (!isAnyArraySet())
			this.numVertices = 0;
	}

	private boolean isAnyArraySet() {
		boolean isTexCoordsSet = false;
		for (int i = 0; i <= maxTextureUnitIndex; i++)
			isTexCoordsSet |= (texCoords[i] != null);

		return (positions != null) || (normals != null) || (colors != null) || isTexCoordsSet;
	}

	public int getReferences(Object3D[] references) throws IllegalArgumentException {
		int parentCount = super.getReferences(references);

		if (positions != null) {
			if (references != null)
				references[parentCount] = positions;
			++parentCount;
		}

		if (normals != null) {
			if (references != null)
				references[parentCount] = normals;
			++parentCount;
		}

		if (colors != null) {
			if (references != null)
				references[parentCount] = colors;
			++parentCount;
		}

		for (int i = 0; i < texCoords.length; ++i) {
			if (texCoords[i] != null) {
				if (references != null)
					references[parentCount] = texCoords[i];
				++parentCount;
			}
		}

		return parentCount;
	}

	boolean isCompatible(AnimationTrack track) {
		switch (track.getTargetProperty()) {
		case AnimationTrack.ALPHA:
		case AnimationTrack.COLOR:
			return true;
		default:
			return super.isCompatible(track);
		}
	}
}
