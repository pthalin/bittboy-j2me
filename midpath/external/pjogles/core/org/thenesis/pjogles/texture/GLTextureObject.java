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

package org.thenesis.pjogles.texture;

import org.thenesis.pjogles.GLConstants;
import org.thenesis.pjogles.GLInvalidEnumException;

/**
 * @author tdinneen
 */
public final class GLTextureObject implements GLConstants {
	public final boolean textureEnabled = false;

	/**
	 * Texture name.
	 */
	public final int name;

	/**
	 * Parameter state.
	 */
	public final GLTextureParamState params = new GLTextureParamState();

	/**
	 * Dimension of this texture (GL_TEXTURE_1D, GL_TEXTURE_2D or GL_TEXTURE_3D).
	 */
	public final int target;

	/**
	 * Level information.
	 */
	public int numLevels;
	public final GLTextureImage[] level = new GLTextureImage[MAX_MIPMAP_LEVEL];

	//    public int residence;
	//
	//    public int onebyoneLevel;
	//    public float effectiveMaxLOD;
	//    public float adjustedMinLOD, adjustedMaxLOD;
	//
	//    /* Min/Mag switchover point */
	//    public float c;
	//
	//    /* Color table for EXT_paletted_texture */
	//    //GLColorTable CT;
	//
	// Format of texel (after palette (if any) has been applied)
	//public int baseFormat;

	public GLTextureObject(final int name, final int target) {
		this.name = name;
		this.target = target;
	}

	public final void setTexture(final int level, final int border, final GLTextureBuffer image) {
		this.level[level] = new GLTextureImage(border, image);
	}

	public final void setParameter(final int pname, final float p) {
		setParameter(pname, new float[] { p });
	}

	public final void setParameter(final int pname, final float[] pv) {
		final int e;

		switch (pname) {
		case GL_TEXTURE_WRAP_S:
			switch (e = (int) pv[0]) {
			case GL_REPEAT:
			case GL_CLAMP:
				//case GL_CLAMP_TO_EDGE:
				params.sWrapMode = e;
				break;
			default:
				throw new GLInvalidEnumException("GLTextureObject.setParameter(int, float[])");
			}
			break;
		case GL_TEXTURE_WRAP_T:
			switch (e = (int) pv[0]) {
			case GL_REPEAT:
			case GL_CLAMP:
				//case GL_CLAMP_TO_EDGE:
				params.tWrapMode = e;
				break;
			default:
				throw new GLInvalidEnumException("GLTextureObject.setParameter(int, float[])");
			}
			break;
		case GL_TEXTURE_WRAP_R:
			switch (e = (int) pv[0]) {
			case GL_REPEAT:
			case GL_CLAMP:
				//case GL_CLAMP_TO_EDGE:
				params.rWrapMode = e;
				break;
			default:
				throw new GLInvalidEnumException("GLTextureObject.setParameter(int, float[])");
			}
			break;
		case GL_TEXTURE_MIN_FILTER:
			switch (e = (int) pv[0]) {
			case GL_NEAREST:
			case GL_LINEAR:
			case GL_NEAREST_MIPMAP_NEAREST:
			case GL_LINEAR_MIPMAP_NEAREST:
			case GL_NEAREST_MIPMAP_LINEAR:
			case GL_LINEAR_MIPMAP_LINEAR:
				params.minFilter = e;
				break;
			default:
				throw new GLInvalidEnumException("GLTextureObject.setParameter(int, float[])");
			}
			break;
		case GL_TEXTURE_MAG_FILTER:
			switch (e = (int) pv[0]) {
			case GL_NEAREST:
			case GL_LINEAR:
				params.magFilter = e;
				break;
			default:
				throw new GLInvalidEnumException("GLTextureObject.setParameter(int, float[])");
			}
			break;
		case GL_TEXTURE_BORDER_COLOR:
			params.borderColor.set(pv);
			break;

		//            case GL_TEXTURE_PRIORITY:
		//                state.params.priority = GLUtils.clamp(pv[0], 0.0f, 1.0f);
		//                break;
		//
		//            case GL_TEXTURE_MIN_LOD:
		//                state.params.minLOD = pv[0];
		//                if (Math.abs(state.params.minLOD) > 63.0F)
		//                {
		//                    if (state.params.minLOD > 0.0f)
		//                    {
		//                        adjustedMinLOD = Float.MAX_VALUE;
		//                    }
		//                    else
		//                    {
		//                        adjustedMinLOD = -Float.MAX_VALUE;
		//                    }
		//                }
		//                else
		//                {
		//                    adjustedMinLOD = (float)Math.pow(2.0, state.params.minLOD);
		//                    adjustedMinLOD *= adjustedMinLOD;
		//                }
		//                break;
		//
		//            case GL_TEXTURE_MAX_LOD:
		//                state.params.maxLOD = pv[0];
		//                if (Math.abs(state.params.maxLOD) > 63.0F)
		//                {
		//                    if (state.params.maxLOD > 0.0f)
		//                    {
		//                        adjustedMaxLOD = Float.MAX_VALUE;
		//                    }
		//                    else
		//                    {
		//                        adjustedMaxLOD = -Float.MAX_VALUE;
		//                    }
		//                }
		//                else
		//                {
		//                    adjustedMaxLOD = (float)Math.pow(2.0, state.params.maxLOD);
		//                    adjustedMaxLOD *= adjustedMaxLOD;
		//                }
		//                break;
		//
		//            case GL_TEXTURE_BASE_LEVEL:
		//                if ((int) pv[0] < 0)
		//                throw new GLInvalidValueException("GLTextureObject.setParameter(int, float[])");
		//
		//                state.params.baseLevel = (int) pv[0];
		//                break;
		//
		//            case GL_TEXTURE_MAX_LEVEL:
		//                if ((int) pv[0] < 0)
		//                throw new GLInvalidValueException("GLTextureObject.setParameter(int, float[])");
		//                state.params.maxLevel = (int) pv[0];
		//                break;

		default:
			throw new GLInvalidEnumException("GLTextureObject.setParameter(int, float[])");
		}
	}
}
