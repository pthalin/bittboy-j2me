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

//import java.io.BufferedInputStream;
//import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
//import java.util.ArrayList;
import java.util.Vector;
import java.util.Iterator;

/**
 * Code for importing a binary 3D Studio file.
 *
 * @author tdinneen
 */
public final class Import3DS {
	// Primary Chunk, at the beginning of each file
	private static final int PRIMARY = 0x4D4D;

	// Main Chunks
	private static final int OBJECTINFO = 0x3D3D; // This gives the version of the mesh and is found right before the material and object information
	private static final int VERSION = 0x0002; // This gives the version of the .3ds file
	//private static final int EDITKEYFRAME = 0xB000; // This is the header for all of the key frame info

	// sub defines of OBJECTINFO
	private static final int MATERIAL = 0xAFFF; // This stored the texture info
	private static final int OBJECT = 0x4000; // This stores the triangles, vertices, etc...

	// sub defines of MATERIAL
	private static final int MATNAME = 0xA000; // This holds the material name
	private static final int MATDIFFUSE = 0xA020; // This holds the color of the object/material
	private static final int MATMAP = 0xA200; // This is a header for a new material
	private static final int MATMAPFILE = 0xA300; // This holds the file name of the texture

	private static final int OBJECT_MESH = 0x4100; // This lets us know that we are reading a new object

	// sub defines of OBJECT_MESH
	private static final int OBJECT_VERTICES = 0x4110; // The objects vertices
	private static final int OBJECT_FACES = 0x4120; // The objects triangles
	private static final int OBJECT_MATERIAL = 0x4130; // This is found if the object has a material, either texture map or color
	private static final int OBJECT_UV = 0x4140; // The UV texture coordinates

	protected static final class Vertex3D {
		public float x, y, z; // position
		public float nx, ny, nz; // normal
		public float u, v; // texture coordinates

		public final Vector faces = new Vector();

		public Vertex3D(final float x, final float y, final float z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		public final float length() {
			return (float) Math.sqrt(x * x + y * y + z * z);
		}

		public final void normalize() {
			final float l = length();

			if (l == 0) {
				x = y = z = 0;
				return;
			}

			final float scale = 1.0f / l;

			x *= scale;
			y *= scale;
			z *= scale;
		}

		public final void add(final Vertex3D v) {
			x += v.x;
			y += v.y;
			z += v.z;
		}

		public static Vertex3D sub(final Vertex3D v1, final Vertex3D v2) {
			return new Vertex3D(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
		}

		public static Vertex3D crossProduct(final Vertex3D a, final Vertex3D b) {
			return new Vertex3D(a.y * b.z - b.y * a.z, a.z * b.x - b.z * a.x, a.x * b.y - b.x * a.y);
		}

		public static Vertex3D crossProduct(final Vertex3D a, final Vertex3D b, final Vertex3D c) {
			return crossProduct(sub(b, a), sub(c, a));
		}

		public final void buildNormal() {
			nx = ny = nz = 0.0f;

			final Iterator i = faces.iterator();

			while (i.hasNext()) {
				final Face3D face = (Face3D) i.next();

				nx += face.nx;
				ny += face.ny;
				nz += face.nz;
			}

			final float l = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);

			if (l == 0) {
				nx = ny = nz = 0.0f;
				return;
			}

			final float scale = 1.0f / l;

			nx *= scale;
			ny *= scale;
			nz *= scale;
		}
	}

	protected final class Face3D {
		public final Vector vertices = new Vector();

		public final float nx;
		public final float ny;
		public final float nz; // face normal

		public Face3D(final Vector v, final int v1, final int v2, final int v3) {
			final Vertex3D a = (Vertex3D) v.get(v1);
			vertices.add(a);
			a.faces.add(this);

			final Vertex3D b = (Vertex3D) v.get(v2);
			vertices.add(b);
			b.faces.add(this);

			final Vertex3D c = (Vertex3D) v.get(v3);
			vertices.add(c);
			c.faces.add(this);

			// build the face normal

			final Vertex3D normal = Vertex3D.crossProduct(a, b, c);
			normal.normalize();

			nx = normal.x;
			ny = normal.y;
			nz = normal.z;
		}
	}

	protected final class Object3D {
		public final String name;

		public final Vector vertices = new Vector();
		public final Vector faces = new Vector();

		public int materialId;

		public Object3D(final String name) {
			this.name = name;
		}
	}

	protected final class Material3D {
		public String name;
		public String filename;
		public byte[] color;
	}

	private int chunkId;
	private int nextOffset;
	private boolean endOfStream;

	private Model3D model3d;

	private Object3D object3d;
	private Material3D material3d;

	public final Model3D import3DS(InputStream in) throws ImportException, IOException {
		readHeader(in);

		if (chunkId != PRIMARY)
			throw new ImportException("Import3DS.import3DS(String) - Invalid 3DS file");

		model3d = new Model3D();

		while (!endOfStream)
			readChunk(in);

		return model3d;
	}

	private void readHeader(final InputStream in) throws IOException {
		chunkId = readShort(in);
		nextOffset = readInt(in);
		endOfStream = chunkId == -1;
	}

	private void readChunk(final InputStream in) throws ImportException, IOException {
		readHeader(in);

		final int version;

		switch (chunkId) {
		case VERSION: // This holds the version of the file

			// This chunk has an unsigned short that holds the file version.
			// Since there might be new additions to the 3DS file format in 4.0,
			// we give a warning to that problem.

			// Read the file version and add the bytes read to our bytesRead variable
			version = readInt(in);

			// If the file version is over 3, give a warning that there could be a problem
			if (version > 0x03)
				throw new ImportException("Import3DS.readChunk(InputStream) - The 3DS file version is greater than 3.");

			break;

		case OBJECTINFO: // This holds the version of the mesh

			// This chunk holds the version of the mesh.  It is also the head of the MATERIAL
			// and OBJECT chunks.  From here on we start reading in the material and object info.

			readHeader(in);

			// Get the version of the mesh

			version = readInt(in);

			break;

		case MATERIAL:

			material3d = new Material3D();
			model3d.materials.add(material3d);
			break;

		case OBJECT: // This holds the name of the object being read

			// This chunk is the header for the object info chunks.
			// It also holds the name of the object.

			object3d = new Object3D(readString(in));
			model3d.objects.add(object3d);
			break;

		// below is for OBJECT chunks

		case OBJECT_MESH: // This lets us know that we are reading a new object

			// do nothing !
			break;

		case OBJECT_VERTICES: // This is the objects vertices

			readVertices(in);
			break;

		case OBJECT_FACES: // This is the objects face information

			readFaces(in);
			break;

		case OBJECT_UV: // This holds the UV texture coordinates for the object

			readUVCoordinates(in);
			break;

		case OBJECT_MATERIAL: // This holds the material name that the object has

			// This chunk holds the name of the material that the object has assigned to it.
			// This could either be just a color or a texture map.  This chunk also holds
			// the triangles that the texture is assigned to (In the case that there is multiple
			// textures assigned to one object, or it just has a texture on a part of the object).

			readMaterial(in);
			break;

		// below is for MATERIAL chunks

		case MATNAME: // This chunk holds the name of the material

			material3d.name = readString(in);
			break;

		case MATDIFFUSE: // This holds the R G B color of our object

			readColor(in);
			break;

		case MATMAP: // This is the header for the texture info

			// do nothing ?
			break;

		case MATMAPFILE: // This stores the file name of the material

			material3d.filename = readString(in);
			break;

		default:

			skipChunk(in);
		}
	}

	private void skipChunk(final InputStream in) throws IOException {
		for (int i = 0; (i < nextOffset - 6) && (!endOfStream); ++i)
			// 6 is the length of a Chunk Header
			endOfStream = in.read() < 0;
	}

	// This function reads in the vertices for the object
	private void readVertices(final InputStream in) throws IOException {
		final int numberOfVertices = readShort(in);
		final Vector vertices = object3d.vertices;

		for (int i = numberOfVertices; --i >= 0;)
			vertices.add(new Vertex3D(readFloat(in), readFloat(in), readFloat(in)));
	}

	// This function reads in the indices for the vertex array
	private void readFaces(final InputStream in) throws IOException {
		final int numberOfTriangles = readShort(in);
		final Vector faces = object3d.faces;

		for (int i = numberOfTriangles; --i >= 0;) {
			// We read in the A then B then C index for the face, but ignore the 4th value.
			// The fourth value is a visibility flag for 3D Studio Max, we don't care about this.

			faces.add(new Face3D(object3d.vertices, readShort(in), readShort(in), readShort(in)));
			readShort(in);
		}
	}

	// This function reads in the UV coordinates for the object
	private void readUVCoordinates(final InputStream in) throws IOException {
		final int numberOfVertices = readShort(in);
		final Vector vertices = object3d.vertices;

		for (int i = 0; i < numberOfVertices; ++i) {
			Vertex3D vertex = (Vertex3D) vertices.get(i);

			vertex.u = readFloat(in);
			vertex.v = readFloat(in);
		}
	}

	// This function reads in the material name assigned to the object and sets the materialID
	private void readMaterial(final InputStream in) throws IOException {
		// Here we read the material name that is assigned to the current object.
		final String materialName = readString(in);

		// Now that we have a material name, we need to go through all of the materials
		// and check the name against each material.  When we find a material in our material
		// list that matches this name we just read in, then we assign the material
		// of the object to that material.

		int materalId = 0;

		final Iterator i = model3d.materials.iterator();

		while (i.hasNext()) {
			final Material3D material = (Material3D) i.next();

			if (material.name.equals(materialName)) {
				object3d.materialId = materalId;
				break;
			}

			++materalId;
		}
	}

	private void readColor(final InputStream in) throws IOException {
		readHeader(in); // Read the color chunk info

		material3d.color = new byte[] { (byte) readByte(in), (byte) readByte(in), (byte) readByte(in) };
	}

	private String readString(final InputStream in) throws IOException {
		byte b;
		final StringBuffer buffer = new StringBuffer();

		while ((b = (byte) in.read()) != 0)
			buffer.append((char) b);

		return buffer.toString();
	}

	private int readByte(final InputStream in) throws IOException {
		return in.read();
	}

	private int readInt(final InputStream in) throws IOException {
		return in.read() | (in.read() << 8) | (in.read() << 16) | (in.read() << 24);
	}

	private int readShort(final InputStream in) throws IOException {
		return (in.read() | (in.read() << 8));
	}

	private float readFloat(final InputStream in) throws IOException {
		return Float.intBitsToFloat(readInt(in));
	}

	//    public static void main(final String[] args)
	//    {
	//        try
	//        {
	//            final Import3DS i = new Import3DS();
	//            i.import3DS("demos/opengl/data/mech.3ds");
	//        }
	//        catch (ImportException e)
	//        {
	//            e.printStackTrace();
	//        }
	//        catch (IOException e)
	//        {
	//            e.printStackTrace();
	//        }
	//    }
}
