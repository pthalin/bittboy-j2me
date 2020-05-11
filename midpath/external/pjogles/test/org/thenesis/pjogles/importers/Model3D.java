/*
 * PJOGLES - Copyright (C) 2008 Guillaume Legris, Mathieu Legris
 * 
 * OGLJava - Copyright (C) 2004 Tom Dinneen
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

package org.thenesis.pjogles.importers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.thenesis.pjogles.GL11;
import org.thenesis.pjogles.GLConstants;

/**
 * The Model3D object returned back from a call to Import3DS.import3DS(String).
 *
 * @see Import3DS#import3DS
 *
 * @author tdinneen
 */
public final class Model3D implements GLConstants {
	public final List objects = new ArrayList();
	public final List materials = new ArrayList();

	public final void buildNormals() {
		final Iterator i = objects.iterator();

		while (i.hasNext()) {
			final Import3DS.Object3D object = (Import3DS.Object3D) i.next();
			final Iterator j = object.faces.iterator();

			while (j.hasNext()) {
				Import3DS.Face3D face = (Import3DS.Face3D) j.next();
				final Iterator k = face.vertices.iterator();

				while (k.hasNext()) {
					final Import3DS.Vertex3D vertex = (Import3DS.Vertex3D) k.next();
					vertex.buildNormal();
				}
			}
		}
	}

	public final int toDisplayList(final GL11 gl) {
		final int[] textureIds = createTextures(gl);
		boolean hasTexture;

		final int displayList = gl.glGenLists(1);
		gl.glNewList(displayList, GL_COMPILE);

		final Iterator i = objects.iterator();

		while (i.hasNext()) {
			final Import3DS.Object3D object = (Import3DS.Object3D) i.next();
			final int materialId = object.materialId;

			Import3DS.Material3D material = null;
			hasTexture = false;

			if (materialId != -1) {
				material = (Import3DS.Material3D) materials.get(object.materialId);
				hasTexture = material.filename != null;

				if (material.color != null)
				//gl.glColor3ubv(material.color);
				{
					gl.glColor4f((material.color[0] & 0xff) / 255.0f, (material.color[1] & 0xff) / 255.0f,
							(material.color[2] & 0xff) / 255.0f, 0.5f);
				}
			}

			if (hasTexture) {
				gl.glEnable(GL_TEXTURE_2D);
				gl.glBindTexture(GL_TEXTURE_2D, textureIds[materialId]);
			} else
				gl.glDisable(GL_TEXTURE_2D);

			final Iterator j = object.faces.iterator();

			while (j.hasNext()) {
				final Import3DS.Face3D face = (Import3DS.Face3D) j.next();

				gl.glBegin(GL_POLYGON);

				final Iterator k = face.vertices.iterator();

				while (k.hasNext()) {
					final Import3DS.Vertex3D vertex = (Import3DS.Vertex3D) k.next();

					gl.glNormal3f(vertex.nx, vertex.ny, vertex.nz);

					if (hasTexture)
						gl.glTexCoord2f(vertex.u, vertex.v);

					gl.glVertex3f(vertex.x, vertex.y, vertex.z);
				}

				gl.glEnd();
			}
		}

		gl.glEndList();

		return displayList;
	}

	private int[] createTextures(final GL11 gl) {
		final List textures = new ArrayList();

		final Iterator i = materials.iterator();

		while (i.hasNext()) {
			final Import3DS.Material3D material = (Import3DS.Material3D) i.next();

			if (material.filename != null)
				textures.add(material.filename);
		}

		final int[] textureIds = new int[textures.size()];
		gl.glGenTextures(textures.size(), textureIds);

		gl.glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		int textureId = 0;

		final Iterator j = textures.iterator();

		while (j.hasNext()) {
			final String texture = (String) j.next();

			// FIXME
			System.out.println("[DEBUG] Model3D.createTextures() : not implemented yet");
			byte[] image = new byte[256 * 256];
			for (int k = 0; k < 256 * 256; k += 3) {
				image[k] = -1;
				image[k + 1] = 0;
				image[k + 2] = 0;
			}

			//byte[] image = loadTexture((String)j.next());
			//final byte[] image = loadRGBImage("swirl_256x256.raw", 256, 256);

			gl.glBindTexture(GL_TEXTURE_2D, textureIds[textureId++]);

			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

			gl.glTexImage2D(GL_TEXTURE_2D, 0, 3, 256, 256, 0, GL_RGB, GL_UNSIGNED_BYTE, image);
		}

		return textureIds;
	}

	private byte[] loadRGBImage(final String filename, final int width, final int height) {
		final File file = new File(filename);

		if (!file.exists() || !file.canRead())
			return null;

		final int size = (((3 * width + 3) >> 2) << 2) * height;

		if (file.length() != (long) size)
			return null;

		final byte[] data = new byte[width * height * 3];

		final InputStream in;

		try {
			in = new FileInputStream(file);

			int index;

			for (int x = 0; x < width; ++x) {
				for (int y = 0; y < height; ++y) {
					index = (x + (y * height)) * 3;

					final int red = in.read();

					if (red == -1)
						return null;

					data[index] = (byte) red;

					final int green = in.read();

					if (green == -1)
						return null;

					data[index + 1] = (byte) green;

					final int blue = in.read();

					if (blue == -1)
						return null;

					data[index + 2] = (byte) blue;
				}
			}

			in.close();
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}

		return data;
	}
}
