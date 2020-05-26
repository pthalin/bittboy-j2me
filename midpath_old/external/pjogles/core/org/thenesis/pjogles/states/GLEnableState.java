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

package org.thenesis.pjogles.states;

import org.thenesis.pjogles.GLConstants;

/**
 * @author tdinneen
 */
public final class GLEnableState implements GLConstants {
	public boolean alphaTest;
	public boolean autoNormal;
	public boolean blend;
	public int clipPlanes;
	public boolean colorMaterial;
	public boolean convolution1D;
	public boolean convolution2D;
	public boolean separable2D;
	public boolean cullFace;
	public boolean depthTest;
	public boolean dither;
	public boolean fog;
	public boolean histogram;
	public int lights;
	public boolean lighting;
	public boolean lineSmooth;
	public boolean lineStipple;
	public boolean indexLogicOp;
	public boolean colorLogicOp;
	public boolean map1Color4;
	public boolean map1Index;
	public boolean map1Normal;
	public boolean map1TextureCoord1;
	public boolean map1TextureCoord2;
	public boolean map1TextureCoord3;
	public boolean map1TextureCoord4;
	public boolean map1Vertex3;
	public boolean map1Vertex4;
	//public boolean[] map1Attrib = new boolean[16];  // GL_NV_vertex_program
	public boolean map2Color4;
	public boolean map2Index;
	public boolean map2Normal;
	public boolean map2TextureCoord1;
	public boolean map2TextureCoord2;
	public boolean map2TextureCoord3;
	public boolean map2TextureCoord4;
	public boolean map2Vertex3;
	public boolean map2Vertex4;
	//public boolean[] map2Attrib = new boolean[16];  // GL_NV_vertex_program
	public boolean minMax;
	public boolean normalize;
	public boolean pixelTexture;
	public boolean pointSmooth;
	public boolean polygonOffsetPoint;
	public boolean polygonOffsetLine;
	public boolean polygonOffsetFill;
	public boolean polygonSmooth;
	public boolean polygonStipple;
	public boolean rescaleNormals;
	public boolean scissor;
	public boolean stencil;
	//    public boolean multisampleEnabled;      // GL_ARB_multisample
	//    public boolean sampleAlphaToCoverage;   // GL_ARB_multisample
	//    public boolean sampleAlphaToOne;        // GL_ARB_multisample
	//    public boolean sampleCoverage;          // GL_ARB_multisample
	//    public boolean sampleCoverageInvert;    // GL_ARB_multisample
	//    public boolean rasterPositionUnclipped; // GL_IBM_rasterpos_clip
	public final int[] texture = new int[NUMBER_OF_TEXTURE_UNITS];
	public final int[] texGen = new int[NUMBER_OF_TEXTURE_UNITS];

	//    public boolean vertexProgram;           // GL_NV_vertex_program
	//    public boolean vertexProgramPointSize;  // GL_NV_vertex_program
	//    public boolean vertexProgramTwoSide;    // GL_NV_vertex_program
	//    public boolean pointSprite;             // GL_NV_point_sprite

	public final GLEnableState get() {
		final GLEnableState s = new GLEnableState();
		s.set(this);

		return s;
	}

	public final void set(final GLEnableState s) {
		alphaTest = s.alphaTest;
		autoNormal = s.autoNormal;
		blend = s.blend;
		clipPlanes = s.clipPlanes;
		colorMaterial = s.colorMaterial;
		convolution1D = s.convolution1D;
		convolution2D = s.convolution2D;
		separable2D = s.separable2D;
		cullFace = s.cullFace;
		depthTest = s.depthTest;
		dither = s.dither;
		fog = s.fog;
		histogram = s.histogram;
		lights = s.lights;
		lighting = s.lighting;
		lineSmooth = s.lineSmooth;
		lineStipple = s.lineStipple;
		indexLogicOp = s.indexLogicOp;
		colorLogicOp = s.colorLogicOp;
		map1Color4 = s.map1Color4;
		map1Index = s.map1Index;
		map1Normal = s.map1Normal;
		map1TextureCoord1 = s.map1TextureCoord1;
		map1TextureCoord2 = s.map1TextureCoord2;
		map1TextureCoord3 = s.map1TextureCoord3;
		map1TextureCoord4 = s.map1TextureCoord4;
		map1Vertex3 = s.map1Vertex3;
		map1Vertex4 = s.map1Vertex4;
		map2Color4 = s.map2Color4;
		map2Index = s.map2Index;
		map2Normal = s.map2Normal;
		map2TextureCoord1 = s.map2TextureCoord1;
		map2TextureCoord2 = s.map2TextureCoord2;
		map2TextureCoord3 = s.map2TextureCoord3;
		map2TextureCoord4 = s.map2TextureCoord4;
		map2Vertex3 = s.map2Vertex3;
		map2Vertex4 = s.map2Vertex4;
		minMax = s.minMax;
		normalize = s.normalize;
		pixelTexture = s.pixelTexture;
		pointSmooth = s.pointSmooth;
		polygonOffsetPoint = s.polygonOffsetPoint;
		polygonOffsetLine = s.polygonOffsetLine;
		polygonOffsetFill = s.polygonOffsetFill;
		polygonSmooth = s.polygonSmooth;
		polygonStipple = s.polygonStipple;
		rescaleNormals = s.rescaleNormals;
		scissor = s.scissor;
		stencil = s.stencil;

		for (int i = NUMBER_OF_TEXTURE_UNITS; --i >= 0;) {
			texture[i] = s.texture[i];
			texGen[i] = s.texGen[i];
		}
	}
}
