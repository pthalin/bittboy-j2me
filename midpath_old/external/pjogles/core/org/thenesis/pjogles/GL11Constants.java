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

package org.thenesis.pjogles;

/**
 * All the shared OpenGL 1.1 constants in an interface that we can implement.
 * Could of course enumerate some of these for Java 1.5, maybe just static imports?
 *
 * @author tdinneen
 */
public interface GL11Constants {
	///////////////////////////////////////////////////////////////

	public static final int NEED_VALIDATE = -2;
	public static final int NOT_IN_BEGIN = -1;

	// Boolean values
	public static final boolean GL_FALSE = false;
	public static final boolean GL_TRUE = true;
	
	// Errors
	
	public static final int GL_NO_ERROR = 0;
	
	/**
	 * <i>enum</i> argument out of range.
	 */
	public static final int GL_INVALID_ENUM = 0x0500;

	/**
	 * Numeric argument out of range.
	 */
	public static final int GL_INVALID_VALUE = 0x0501;

	/**
	 * Operation illegal in current state.
	 */
	public static final int GL_INVALID_OPERATION = 0x0502;

	/**
	 * Command would cause a stack overflow.
	 */
	public static final int GL_STACK_OVERFLOW = 0x0503;

	/**
	 * Command would cause a stack underflow.
	 */
	public static final int GL_STACK_UNDERFLOW = 0x0504;

	/**
	 * Not enough memory left to execute command.
	 */
	public static final int GL_OUT_OF_MEMORY = 0x0505;

	/**
	 * The specified table is too large.
	 */
	public static final int GL_TABLE_TOO_LARGE = 0x0506;

	// Data types
	public static final int GL_BYTE = 0x1400;
	public static final int GL_UNSIGNED_BYTE = 0x1401;
	public static final int GL_SHORT = 0x1402;
	public static final int GL_UNSIGNED_SHORT = 0x1403;
	public static final int GL_INT = 0x1404;
	public static final int GL_UNSIGNED_INT = 0x1405;
	public static final int GL_FLOAT = 0x1406;
	public static final int GL_2_BYTES = 0x1407;
	public static final int GL_3_BYTES = 0x1408;
	public static final int GL_4_BYTES = 0x1409;
	public static final int GL_DOUBLE = 0x140A;
	public static final int GL_FIXED = 0x140C;

	// Primitives
	public static final int GL_POINTS = 0x0000;
	public static final int GL_LINES = 0x0001;
	public static final int GL_LINE_LOOP = 0x0002;
	public static final int GL_LINE_STRIP = 0x0003;
	public static final int GL_TRIANGLES = 0x0004;
	public static final int GL_TRIANGLE_STRIP = 0x0005;
	public static final int GL_TRIANGLE_FAN = 0x0006;
	public static final int GL_QUADS = 0x0007;
	public static final int GL_QUAD_STRIP = 0x0008;
	public static final int GL_POLYGON = 0x0009;

	public static final int GL_EDGE_FLAG = 0x0B43;

	// Matrix Mode
	public static final int GL_MATRIX_MODE = 0x0BA0;
	public static final int GL_MODELVIEW = 0x1700;
	public static final int GL_PROJECTION = 0x1701;
	public static final int GL_TEXTURE = 0x1702;

	// Points
	public static final int GL_POINT_SMOOTH = 0x0B10;
	public static final int GL_POINT_SIZE = 0x0B11;
	public static final int GL_POINT_SIZE_GRANULARITY = 0x0B13;
	public static final int GL_POINT_SIZE_RANGE = 0x0B12;

	// Lines
	public static final int GL_LINE_SMOOTH = 0x0B20;
	public static final int GL_LINE_STIPPLE = 0x0B24;
	public static final int GL_LINE_STIPPLE_PATTERN = 0x0B25;
	public static final int GL_LINE_STIPPLE_REPEAT = 0x0B26;
	public static final int GL_LINE_WIDTH = 0x0B21;
	public static final int GL_LINE_WIDTH_GRANULARITY = 0x0B23;
	public static final int GL_LINE_WIDTH_RANGE = 0x0B22;

	// Polygons
	public static final int GL_POINT = 0x1B00;
	public static final int GL_LINE = 0x1B01;
	public static final int GL_FILL = 0x1B02;

	public static final int GL_CCW = 0x0901;
	public static final int GL_CW = 0x0900;
	public static final int GL_FRONT = 0x0404;
	public static final int GL_BACK = 0x0405;
	public static final int GL_CULL_FACE = 0x0B44;
	public static final int GL_CULL_FACE_MODE = 0x0B45;
	public static final int GL_POLYGON_SMOOTH = 0x0B41;
	public static final int GL_POLYGON_STIPPLE = 0x0B42;
	public static final int GL_FRONT_FACE = 0x0B46;
	public static final int GL_POLYGON_MODE = 0x0B40;

	// Display Lists
	public static final int GL_COMPILE = 0x1300;
	public static final int GL_COMPILE_AND_EXECUTE = 0x1301;
	public static final int GL_LIST_BASE = 0x0B32;
	public static final int GL_LIST_INDEX = 0x0B33;
	public static final int GL_LIST_MODE = 0x0B30;

	// Depth buffer
	public static final int GL_NEVER = 0x0200;
	public static final int GL_LESS = 0x0201;
	public static final int GL_GEQUAL = 0x0206;
	public static final int GL_LEQUAL = 0x0203;
	public static final int GL_GREATER = 0x0204;
	public static final int GL_NOTEQUAL = 0x0205;
	public static final int GL_EQUAL = 0x0202;
	public static final int GL_ALWAYS = 0x0207;
	public static final int GL_DEPTH_TEST = 0x0B71;
	public static final int GL_DEPTH_BITS = 0x0D56;
	public static final int GL_DEPTH_CLEAR_VALUE = 0x0B73;
	public static final int GL_DEPTH_FUNC = 0x0B74;
	public static final int GL_DEPTH_RANGE = 0x0B70;
	public static final int GL_DEPTH_WRITEMASK = 0x0B72;
	public static final int GL_DEPTH_COMPONENT = 0x1902;

	// Lighting
	public static final int GL_LIGHTING = 0x0B50;
	public static final int GL_LIGHT0 = 0x4000;
	public static final int GL_LIGHT1 = 0x4001;
	public static final int GL_LIGHT2 = 0x4002;
	public static final int GL_LIGHT3 = 0x4003;
	public static final int GL_LIGHT4 = 0x4004;
	public static final int GL_LIGHT5 = 0x4005;
	public static final int GL_LIGHT6 = 0x4006;
	public static final int GL_LIGHT7 = 0x4007;
	public static final int GL_SPOT_EXPONENT = 0x1205;
	public static final int GL_SPOT_CUTOFF = 0x1206;
	public static final int GL_CONSTANT_ATTENUATION = 0x1207;
	public static final int GL_LINEAR_ATTENUATION = 0x1208;
	public static final int GL_QUADRATIC_ATTENUATION = 0x1209;
	public static final int GL_AMBIENT = 0x1200;
	public static final int GL_DIFFUSE = 0x1201;
	public static final int GL_SPECULAR = 0x1202;
	public static final int GL_SHININESS = 0x1601;
	public static final int GL_EMISSION = 0x1600;
	public static final int GL_POSITION = 0x1203;
	public static final int GL_SPOT_DIRECTION = 0x1204;
	public static final int GL_AMBIENT_AND_DIFFUSE = 0x1602;
	public static final int GL_COLOR_INDEXES = 0x1603;
	public static final int GL_LIGHT_MODEL_TWO_SIDE = 0x0B52;
	public static final int GL_LIGHT_MODEL_LOCAL_VIEWER = 0x0B51;
	public static final int GL_LIGHT_MODEL_AMBIENT = 0x0B53;
	public static final int GL_FRONT_AND_BACK = 0x0408;
	public static final int GL_SHADE_MODEL = 0x0B54;
	public static final int GL_FLAT = 0x1D00;
	public static final int GL_SMOOTH = 0x1D01;
	public static final int GL_COLOR_MATERIAL = 0x0B57;
	public static final int GL_COLOR_MATERIAL_FACE = 0x0B55;
	public static final int GL_COLOR_MATERIAL_PARAMETER = 0x0B56;
	public static final int GL_NORMALIZE = 0x0BA1;

	public static final int GL_LIGHT_MODEL_COLOR_CONTROL = 0x81F8;
	public static final int GL_SINGLE_COLOR = 0x81F9;
	public static final int GL_SEPARATE_SPECULAR_COLOR = 0x81FA;

	// User clipping planes
	public static final int GL_CLIP_PLANE0 = 0x3000;
	public static final int GL_CLIP_PLANE1 = 0x3001;
	public static final int GL_CLIP_PLANE2 = 0x3002;
	public static final int GL_CLIP_PLANE3 = 0x3003;
	public static final int GL_CLIP_PLANE4 = 0x3004;
	public static final int GL_CLIP_PLANE5 = 0x3005;

	// Accumulation buffer
	public static final int GL_ACCUM_RED_BITS = 0x0D58;
	public static final int GL_ACCUM_GREEN_BITS = 0x0D59;
	public static final int GL_ACCUM_BLUE_BITS = 0x0D5A;
	public static final int GL_ACCUM_ALPHA_BITS = 0x0D5B;
	public static final int GL_ACCUM_CLEAR_VALUE = 0x0B80;
	public static final int GL_ACCUM = 0x0100;
	public static final int GL_ADD = 0x0104;
	public static final int GL_LOAD = 0x0101;
	public static final int GL_MULT = 0x0103;
	public static final int GL_RETURN = 0x0102;

	// Alpha testing
	public static final int GL_ALPHA_TEST = 0x0BC0;
	public static final int GL_ALPHA_TEST_REF = 0x0BC2;
	public static final int GL_ALPHA_TEST_FUNC = 0x0BC1;

	// Blending
	public static final int GL_BLEND = 0x0BE2;
	public static final int GL_BLEND_SRC = 0x0BE1;
	public static final int GL_BLEND_DST = 0x0BE0;
	public static final int GL_ZERO = 0;
	public static final int GL_ONE = 1;
	public static final int GL_SRC_COLOR = 0x0300;
	public static final int GL_ONE_MINUS_SRC_COLOR = 0x0301;
	public static final int GL_DST_COLOR = 0x0306;
	public static final int GL_ONE_MINUS_DST_COLOR = 0x0307;
	public static final int GL_SRC_ALPHA = 0x0302;
	public static final int GL_ONE_MINUS_SRC_ALPHA = 0x0303;
	public static final int GL_DST_ALPHA = 0x0304;
	public static final int GL_ONE_MINUS_DST_ALPHA = 0x0305;
	public static final int GL_SRC_ALPHA_SATURATE = 0x0308;

	// Render Mode
	public static final int GL_FEEDBACK = 0x1C01;
	public static final int GL_RENDER = 0x1C00;
	public static final int GL_SELECT = 0x1C02;

	// Feedback
	public static final int GL_2D = 0x0600;
	public static final int GL_3D = 0x0601;
	public static final int GL_3D_COLOR = 0x0602;
	public static final int GL_3D_COLOR_TEXTURE = 0x0603;
	public static final int GL_4D_COLOR_TEXTURE = 0x0604;
	public static final int GL_POINT_TOKEN = 0x0701;
	public static final int GL_LINE_TOKEN = 0x0702;
	public static final int GL_LINE_RESET_TOKEN = 0x0707;
	public static final int GL_POLYGON_TOKEN = 0x0703;
	public static final int GL_BITMAP_TOKEN = 0x0704;
	public static final int GL_DRAW_PIXEL_TOKEN = 0x0705;
	public static final int GL_COPY_PIXEL_TOKEN = 0x0706;
	public static final int GL_PASS_THROUGH_TOKEN = 0x0700;

	// Fog
	public static final int GL_FOG = 0x0B60;
	public static final int GL_FOG_MODE = 0x0B65;
	public static final int GL_FOG_DENSITY = 0x0B62;
	public static final int GL_FOG_COLOR = 0x0B66;
	public static final int GL_FOG_INDEX = 0x0B61;
	public static final int GL_FOG_START = 0x0B63;
	public static final int GL_FOG_END = 0x0B64;
	public static final int GL_LINEAR = 0x2601;
	public static final int GL_EXP = 0x0800;
	public static final int GL_EXP2 = 0x0801;

	// Logic Ops
	public static final int GL_INDEX_LOGIC_OP = 0x0BF1;
	public static final int GL_COLOR_LOGIC_OP = 0x0BF2;
	public static final int GL_LOGIC_OP = 0x0BF1;
	public static final int GL_LOGIC_OP_MODE = 0x0BF0;
	public static final int GL_CLEAR = 0x1500;
	public static final int GL_SET = 0x150F;
	public static final int GL_COPY = 0x1503;
	public static final int GL_COPY_INVERTED = 0x150C;
	public static final int GL_NOOP = 0x1505;
	public static final int GL_INVERT = 0x150A;
	public static final int GL_AND = 0x1501;
	public static final int GL_NAND = 0x150E;
	public static final int GL_OR = 0x1507;
	public static final int GL_NOR = 0x1508;
	public static final int GL_XOR = 0x1506;
	public static final int GL_EQUIV = 0x1509;
	public static final int GL_AND_REVERSE = 0x1502;
	public static final int GL_AND_INVERTED = 0x1504;
	public static final int GL_OR_REVERSE = 0x150B;
	public static final int GL_OR_INVERTED = 0x150D;

	// Stencil
	public static final int GL_STENCIL_TEST = 0x0B90;
	public static final int GL_STENCIL_WRITEMASK = 0x0B98;
	public static final int GL_STENCIL_BITS = 0x0D57;
	public static final int GL_STENCIL_FUNC = 0x0B92;
	public static final int GL_STENCIL_VALUE_MASK = 0x0B93;
	public static final int GL_STENCIL_REF = 0x0B97;
	public static final int GL_STENCIL_FAIL = 0x0B94;
	public static final int GL_STENCIL_PASS_DEPTH_PASS = 0x0B96;
	public static final int GL_STENCIL_PASS_DEPTH_FAIL = 0x0B95;
	public static final int GL_STENCIL_CLEAR_VALUE = 0x0B91;
	public static final int GL_STENCIL_INDEX = 0x1901;
	public static final int GL_KEEP = 0x1E00;
	public static final int GL_REPLACE = 0x1E01;
	public static final int GL_INCR = 0x1E02;
	public static final int GL_DECR = 0x1E03;

	// Buffers; Pixel Drawing/Reading
	public static final int GL_NONE = 0;
	public static final int GL_LEFT = 0x0406;
	public static final int GL_RIGHT = 0x0407;
	public static final int GL_FRONT_LEFT = 0x0400;
	public static final int GL_FRONT_RIGHT = 0x0401;
	public static final int GL_BACK_LEFT = 0x0402;
	public static final int GL_BACK_RIGHT = 0x0403;
	public static final int GL_AUX0 = 0x0409;
	public static final int GL_AUX1 = 0x040A;
	public static final int GL_AUX2 = 0x040B;
	public static final int GL_AUX3 = 0x040C;
	public static final int GL_COLOR_INDEX = 0x1900;
	public static final int GL_RED = 0x1903;
	public static final int GL_GREEN = 0x1904;
	public static final int GL_BLUE = 0x1905;
	public static final int GL_ALPHA = 0x1906;
	public static final int GL_LUMINANCE = 0x1909;
	public static final int GL_LUMINANCE_ALPHA = 0x190A;
	public static final int GL_ALPHA_BITS = 0x0D55;
	public static final int GL_RED_BITS = 0x0D52;
	public static final int GL_GREEN_BITS = 0x0D53;
	public static final int GL_BLUE_BITS = 0x0D54;
	public static final int GL_INDEX_BITS = 0x0D51;
	public static final int GL_SUBPIXEL_BITS = 0x0D50;
	public static final int GL_AUX_BUFFERS = 0x0C00;
	public static final int GL_READ_BUFFER = 0x0C02;
	public static final int GL_DRAW_BUFFER = 0x0C01;
	public static final int GL_DOUBLEBUFFER = 0x0C32;
	public static final int GL_STEREO = 0x0C33;
	public static final int GL_BITMAP = 0x1A00;
	public static final int GL_COLOR = 0x1800;
	public static final int GL_DEPTH = 0x1801;
	public static final int GL_STENCIL = 0x1802;
	public static final int GL_DITHER = 0x0BD0;
	public static final int GL_RGB = 0x1907;
	public static final int GL_RGBA = 0x1908;

	// Implementation limits
	public static final int GL_MAX_MODELVIEW_STACK_DEPTH = 0x0D36;
	public static final int GL_MAX_PROJECTION_STACK_DEPTH = 0x0D38;
	public static final int GL_MAX_TEXTURE_STACK_DEPTH = 0x0D39;
	public static final int GL_MAX_ATTRIB_STACK_DEPTH = 0x0D35;
	public static final int GL_MAX_NAME_STACK_DEPTH = 0x0D37;
	public static final int GL_MAX_LIST_NESTING = 0x0B31;
	public static final int GL_MAX_LIGHTS = 0x0D31;
	public static final int GL_MAX_CLIP_PLANES = 0x0D32;
	public static final int GL_MAX_VIEWPORT_DIMS = 0x0D3A;
	public static final int GL_MAX_PIXEL_MAP_TABLE = 0x0D34;
	public static final int GL_MAX_EVAL_ORDER = 0x0D30;
	public static final int GL_MAX_TEXTURE_SIZE = 0x0D33;
	public static final int GL_MAX_3D_TEXTURE_SIZE = 0x8073;

	// Gets
	public static final int GL_ATTRIB_STACK_DEPTH = 0x0BB0;
	public static final int GL_COLOR_CLEAR_VALUE = 0x0C22;
	public static final int GL_COLOR_WRITEMASK = 0x0C23;
	public static final int GL_CURRENT_INDEX = 0x0B01;
	public static final int GL_CURRENT_COLOR = 0x0B00;
	public static final int GL_CURRENT_NORMAL = 0x0B02;
	public static final int GL_CURRENT_RASTER_COLOR = 0x0B04;
	public static final int GL_CURRENT_RASTER_DISTANCE = 0x0B09;
	public static final int GL_CURRENT_RASTER_INDEX = 0x0B05;
	public static final int GL_CURRENT_RASTER_POSITION = 0x0B07;
	public static final int GL_CURRENT_RASTER_TEXTURE_COORDS = 0x0B06;
	public static final int GL_CURRENT_RASTER_POSITION_VALID = 0x0B08;
	public static final int GL_CURRENT_TEXTURE_COORDS = 0x0B03;
	public static final int GL_INDEX_CLEAR_VALUE = 0x0C20;
	public static final int GL_INDEX_MODE = 0x0C30;
	public static final int GL_INDEX_WRITEMASK = 0x0C21;
	public static final int GL_MODELVIEW_MATRIX = 0x0BA6;
	public static final int GL_MODELVIEW_STACK_DEPTH = 0x0BA3;
	public static final int GL_NAME_STACK_DEPTH = 0x0D70;
	public static final int GL_PROJECTION_MATRIX = 0x0BA7;
	public static final int GL_PROJECTION_STACK_DEPTH = 0x0BA4;
	public static final int GL_RENDER_MODE = 0x0C40;
	public static final int GL_RGBA_MODE = 0x0C31;
	public static final int GL_TEXTURE_MATRIX = 0x0BA8;
	public static final int GL_TEXTURE_STACK_DEPTH = 0x0BA5;
	public static final int GL_VIEWPORT = 0x0BA2;

	// Evaluators
	public static final int GL_AUTO_NORMAL = 0x0D80;
	public static final int GL_MAP1_COLOR_4 = 0x0D90;
	public static final int GL_MAP1_GRID_DOMAIN = 0x0DD0;
	public static final int GL_MAP1_GRID_SEGMENTS = 0x0DD1;
	public static final int GL_MAP1_INDEX = 0x0D91;
	public static final int GL_MAP1_NORMAL = 0x0D92;
	public static final int GL_MAP1_TEXTURE_COORD_1 = 0x0D93;
	public static final int GL_MAP1_TEXTURE_COORD_2 = 0x0D94;
	public static final int GL_MAP1_TEXTURE_COORD_3 = 0x0D95;
	public static final int GL_MAP1_TEXTURE_COORD_4 = 0x0D96;
	public static final int GL_MAP1_VERTEX_3 = 0x0D97;
	public static final int GL_MAP1_VERTEX_4 = 0x0D98;
	public static final int GL_MAP2_COLOR_4 = 0x0DB0;
	public static final int GL_MAP2_GRID_DOMAIN = 0x0DD2;
	public static final int GL_MAP2_GRID_SEGMENTS = 0x0DD3;
	public static final int GL_MAP2_INDEX = 0x0DB1;
	public static final int GL_MAP2_NORMAL = 0x0DB2;
	public static final int GL_MAP2_TEXTURE_COORD_1 = 0x0DB3;
	public static final int GL_MAP2_TEXTURE_COORD_2 = 0x0DB4;
	public static final int GL_MAP2_TEXTURE_COORD_3 = 0x0DB5;
	public static final int GL_MAP2_TEXTURE_COORD_4 = 0x0DB6;
	public static final int GL_MAP2_VERTEX_3 = 0x0DB7;
	public static final int GL_MAP2_VERTEX_4 = 0x0DB8;
	public static final int GL_COEFF = 0x0A00;
	public static final int GL_DOMAIN = 0x0A02;
	public static final int GL_ORDER = 0x0A01;

	// Hints
	public static final int GL_FOG_HINT = 0x0C54;
	public static final int GL_LINE_SMOOTH_HINT = 0x0C52;
	public static final int GL_PERSPECTIVE_CORRECTION_HINT = 0x0C50;
	public static final int GL_POINT_SMOOTH_HINT = 0x0C51;
	public static final int GL_POLYGON_SMOOTH_HINT = 0x0C53;
	public static final int GL_DONT_CARE = 0x1100;
	public static final int GL_FASTEST = 0x1101;
	public static final int GL_NICEST = 0x1102;

	// Scissor box
	public static final int GL_SCISSOR_TEST = 0x0C11;
	public static final int GL_SCISSOR_BOX = 0x0C10;

	// Pixel Mode / Transfer
	public static final int GL_MAP_COLOR = 0x0D10;
	public static final int GL_MAP_STENCIL = 0x0D11;
	public static final int GL_INDEX_SHIFT = 0x0D12;
	public static final int GL_INDEX_OFFSET = 0x0D13;
	public static final int GL_RED_SCALE = 0x0D14;
	public static final int GL_RED_BIAS = 0x0D15;
	public static final int GL_GREEN_SCALE = 0x0D18;
	public static final int GL_GREEN_BIAS = 0x0D19;
	public static final int GL_BLUE_SCALE = 0x0D1A;
	public static final int GL_BLUE_BIAS = 0x0D1B;
	public static final int GL_ALPHA_SCALE = 0x0D1C;
	public static final int GL_ALPHA_BIAS = 0x0D1D;
	public static final int GL_DEPTH_SCALE = 0x0D1E;
	public static final int GL_DEPTH_BIAS = 0x0D1F;
	public static final int GL_PIXEL_MAP_S_TO_S_SIZE = 0x0CB1;
	public static final int GL_PIXEL_MAP_I_TO_I_SIZE = 0x0CB0;
	public static final int GL_PIXEL_MAP_I_TO_R_SIZE = 0x0CB2;
	public static final int GL_PIXEL_MAP_I_TO_G_SIZE = 0x0CB3;
	public static final int GL_PIXEL_MAP_I_TO_B_SIZE = 0x0CB4;
	public static final int GL_PIXEL_MAP_I_TO_A_SIZE = 0x0CB5;
	public static final int GL_PIXEL_MAP_R_TO_R_SIZE = 0x0CB6;
	public static final int GL_PIXEL_MAP_G_TO_G_SIZE = 0x0CB7;
	public static final int GL_PIXEL_MAP_B_TO_B_SIZE = 0x0CB8;
	public static final int GL_PIXEL_MAP_A_TO_A_SIZE = 0x0CB9;
	public static final int GL_PIXEL_MAP_S_TO_S = 0x0C71;
	public static final int GL_PIXEL_MAP_I_TO_I = 0x0C70;
	public static final int GL_PIXEL_MAP_I_TO_R = 0x0C72;
	public static final int GL_PIXEL_MAP_I_TO_G = 0x0C73;
	public static final int GL_PIXEL_MAP_I_TO_B = 0x0C74;
	public static final int GL_PIXEL_MAP_I_TO_A = 0x0C75;
	public static final int GL_PIXEL_MAP_R_TO_R = 0x0C76;
	public static final int GL_PIXEL_MAP_G_TO_G = 0x0C77;
	public static final int GL_PIXEL_MAP_B_TO_B = 0x0C78;
	public static final int GL_PIXEL_MAP_A_TO_A = 0x0C79;
	public static final int GL_PACK_ALIGNMENT = 0x0D05;
	public static final int GL_PACK_LSB_FIRST = 0x0D01;
	public static final int GL_PACK_ROW_LENGTH = 0x0D02;
	public static final int GL_PACK_IMAGE_HEIGHT = 0x806C;
	public static final int GL_PACK_SKIP_PIXELS = 0x0D04;
	public static final int GL_PACK_SKIP_ROWS = 0x0D03;
	public static final int GL_PACK_SKIP_IMAGES = 0x806B;
	public static final int GL_PACK_SWAP_BYTES = 0x0D00;
	public static final int GL_UNPACK_ALIGNMENT = 0x0CF5;
	public static final int GL_UNPACK_LSB_FIRST = 0x0CF1;
	public static final int GL_UNPACK_ROW_LENGTH = 0x0CF2;
	public static final int GL_UNPACK_IMAGE_HEIGHT = 0x806E;
	public static final int GL_UNPACK_SKIP_PIXELS = 0x0CF4;
	public static final int GL_UNPACK_SKIP_ROWS = 0x0CF3;
	public static final int GL_UNPACK_SKIP_IMAGES = 0x806D;
	public static final int GL_UNPACK_SWAP_BYTES = 0x0CF0;
	public static final int GL_ZOOM_X = 0x0D16;
	public static final int GL_ZOOM_Y = 0x0D17;

	// Texture mapping
	public static final int GL_TEXTURE_ENV = 0x2300;
	public static final int GL_TEXTURE_ENV_MODE = 0x2200;
	public static final int GL_TEXTURE_1D = 0x0DE0;
	public static final int GL_TEXTURE_2D = 0x0DE1;
	public static final int GL_TEXTURE_3D = 0x806F;
	public static final int GL_TEXTURE_WRAP_S = 0x2802;
	public static final int GL_TEXTURE_WRAP_T = 0x2803;
	public static final int GL_TEXTURE_WRAP_R = 0x8072;
	public static final int GL_TEXTURE_MAG_FILTER = 0x2800;
	public static final int GL_TEXTURE_MIN_FILTER = 0x2801;
	public static final int GL_TEXTURE_ENV_COLOR = 0x2201;
	public static final int GL_TEXTURE_GEN_S = 0x0C60;
	public static final int GL_TEXTURE_GEN_T = 0x0C61;
	public static final int GL_TEXTURE_GEN_R = 0x0C62;
	public static final int GL_TEXTURE_GEN_Q = 0x0C63;
	public static final int GL_TEXTURE_GEN_MODE = 0x2500;
	public static final int GL_TEXTURE_BORDER_COLOR = 0x1004;
	public static final int GL_TEXTURE_WIDTH = 0x1000;
	public static final int GL_TEXTURE_HEIGHT = 0x1001;
	public static final int GL_TEXTURE_DEPTH = 0x8071;
	public static final int GL_TEXTURE_BORDER = 0x1005;
	public static final int GL_TEXTURE_COMPONENTS = 0x1003;
	public static final int GL_NEAREST_MIPMAP_NEAREST = 0x2700;
	public static final int GL_NEAREST_MIPMAP_LINEAR = 0x2702;
	public static final int GL_LINEAR_MIPMAP_NEAREST = 0x2701;
	public static final int GL_LINEAR_MIPMAP_LINEAR = 0x2703;
	public static final int GL_OBJECT_LINEAR = 0x2401;
	public static final int GL_OBJECT_PLANE = 0x2501;
	public static final int GL_EYE_LINEAR = 0x2400;
	public static final int GL_EYE_PLANE = 0x2502;
	public static final int GL_SPHERE_MAP = 0x2402;
	public static final int GL_DECAL = 0x2101;
	public static final int GL_MODULATE = 0x2100;
	public static final int GL_NEAREST = 0x2600;
	public static final int GL_REPEAT = 0x2901;
	public static final int GL_CLAMP = 0x2900;
	public static final int GL_S = 0x2000;
	public static final int GL_T = 0x2001;
	public static final int GL_R = 0x2002;
	public static final int GL_Q = 0x2003;

	// Utility
	public static final int GL_VENDOR = 0x1F00;
	public static final int GL_RENDERER = 0x1F01;
	public static final int GL_VERSION = 0x1F02;
	public static final int GL_EXTENSIONS = 0x1F03;

	// GLbitfield
	public static final int GL_CURRENT_BIT = 0x00000001;
	public static final int GL_POINT_BIT = 0x00000002;
	public static final int GL_LINE_BIT = 0x00000004;
	public static final int GL_POLYGON_BIT = 0x00000008;
	public static final int GL_POLYGON_STIPPLE_BIT = 0x00000010;
	public static final int GL_PIXEL_MODE_BIT = 0x00000020;
	public static final int GL_LIGHTING_BIT = 0x00000040;
	public static final int GL_FOG_BIT = 0x00000080;
	public static final int GL_DEPTH_BUFFER_BIT = 0x00000100;
	public static final int GL_ACCUM_BUFFER_BIT = 0x00000200;
	public static final int GL_STENCIL_BUFFER_BIT = 0x00000400;
	public static final int GL_VIEWPORT_BIT = 0x00000800;
	public static final int GL_TRANSFORM_BIT = 0x00001000;
	public static final int GL_ENABLE_BIT = 0x00002000;
	public static final int GL_COLOR_BUFFER_BIT = 0x00004000;
	public static final int GL_HINT_BIT = 0x00008000;
	public static final int GL_EVAL_BIT = 0x00010000;
	public static final int GL_LIST_BIT = 0x00020000;
	public static final int GL_TEXTURE_BIT = 0x00040000;
	public static final int GL_SCISSOR_BIT = 0x00080000;
	public static final int GL_ALL_ATTRIB_BIT = 0x000fffff;

	// polygon_offset
	public static final int GL_POLYGON_OFFSET_FACTOR = 0x8038;
	public static final int GL_POLYGON_OFFSET_UNITS = 0x2A00;
	public static final int GL_POLYGON_OFFSET_POINT = 0x2A01;
	public static final int GL_POLYGON_OFFSET_LINE = 0x2A02;
	public static final int GL_POLYGON_OFFSET_FILL = 0x8037;

	// vertex_array
	public static final int GL_VERTEX_ARRAY = 0x8074;
	public static final int GL_NORMAL_ARRAY = 0x8075;
	public static final int GL_COLOR_ARRAY = 0x8076;
	public static final int GL_INDEX_ARRAY = 0x8077;
	public static final int GL_TEXTURE_COORD_ARRAY = 0x8078;
	public static final int GL_EDGE_FLAG_ARRAY = 0x8079;
	public static final int GL_VERTEX_ARRAY_SIZE = 0x807A;
	public static final int GL_VERTEX_ARRAY_TYPE = 0x807B;
	public static final int GL_VERTEX_ARRAY_STRIDE = 0x807C;
	public static final int GL_NORMAL_ARRAY_TYPE = 0x807E;
	public static final int GL_NORMAL_ARRAY_STRIDE = 0x807F;
	public static final int GL_COLOR_ARRAY_SIZE = 0x8081;
	public static final int GL_COLOR_ARRAY_TYPE = 0x8082;
	public static final int GL_COLOR_ARRAY_STRIDE = 0x8083;
	public static final int GL_INDEX_ARRAY_TYPE = 0x8085;
	public static final int GL_INDEX_ARRAY_STRIDE = 0x8086;
	public static final int GL_TEXTURE_COORD_ARRAY_SIZE = 0x8088;
	public static final int GL_TEXTURE_COORD_ARRAY_TYPE = 0x8089;
	public static final int GL_TEXTURE_COORD_ARRAY_STRIDE = 0x808A;
	public static final int GL_EDGE_FLAG_ARRAY_STRIDE = 0x808C;
	public static final int GL_VERTEX_ARRAY_POINTER = 0x808E;
	public static final int GL_NORMAL_ARRAY_POINTER = 0x808F;
	public static final int GL_COLOR_ARRAY_POINTER = 0x8090;
	public static final int GL_INDEX_ARRAY_POINTER = 0x8091;
	public static final int GL_TEXTURE_COORD_ARRAY_POINTER = 0x8092;
	public static final int GL_EDGE_FLAG_ARRAY_POINTER = 0x8093;
	public static final int GL_V2F = 0x2A20;
	public static final int GL_V3F = 0x2A21;
	public static final int GL_C4UB_V2F = 0x2A22;
	public static final int GL_C4UB_V3F = 0x2A23;
	public static final int GL_C3F_V3F = 0x2A24;
	public static final int GL_N3F_V3F = 0x2A25;
	public static final int GL_C4F_N3F_V3F = 0x2A26;
	public static final int GL_T2F_V3F = 0x2A27;
	public static final int GL_T4F_V4F = 0x2A28;
	public static final int GL_T2F_C4UB_V3F = 0x2A29;
	public static final int GL_T2F_C3F_V3F = 0x2A2A;
	public static final int GL_T2F_N3F_V3F = 0x2A2B;
	public static final int GL_T2F_C4F_N3F_V3F = 0x2A2C;
	public static final int GL_T4F_C4F_N3F_V4F = 0x2A2D;

	public static final int GL_INTENSITY = 0x8049;

	public static final int GL_CONSTANT_COLOR = 0x8001;
	public static final int GL_ONE_MINUS_CONSTANT_COLOR = 0x8002;
	public static final int GL_CONSTANT_ALPHA = 0x8003;
	public static final int GL_ONE_MINUS_CONSTANT_ALPHA = 0x8004;

	public static final int GL_FUNC_ADD_EXT = 0x8006;
	public static final int GL_FUNC_SUBTRACT_EXT = 0x800A;
	public static final int GL_FUNC_REVERSE_SUBTRACT_EXT = 0x800B;

	public static final int GL_POST_CONVOLUTION_RED_SCALE = 0x801C;
	public static final int GL_POST_CONVOLUTION_GREEN_SCALE = 0x801D;
	public static final int GL_POST_CONVOLUTION_BLUE_SCALE = 0x801E;
	public static final int GL_POST_CONVOLUTION_ALPHA_SCALE = 0x801F;
	public static final int GL_POST_CONVOLUTION_RED_BIAS = 0x8020;
	public static final int GL_POST_CONVOLUTION_GREEN_BIAS = 0x8021;
	public static final int GL_POST_CONVOLUTION_BLUE_BIAS = 0x8022;
	public static final int GL_POST_CONVOLUTION_ALPHA_BIAS = 0x8023;

	public static final int GL_POST_COLOR_MATRIX_RED_SCALE = 0x80B4;
	public static final int GL_POST_COLOR_MATRIX_GREEN_SCALE = 0x80B5;
	public static final int GL_POST_COLOR_MATRIX_BLUE_SCALE = 0x80B6;
	public static final int GL_POST_COLOR_MATRIX_ALPHA_SCALE = 0x80B7;
	public static final int GL_POST_COLOR_MATRIX_RED_BIAS = 0x80B8;
	public static final int GL_POST_COLOR_MATRIX_GREEN_BIAS = 0x80B9;
	public static final int GL_POST_COLOR_MATRIX_BLUE_BIAS = 0x80BA;
	public static final int GL_POST_COLOR_MATRIX_ALPHA_BIAS = 0x80BB;

	//////////////////////////////

	public static final int MAX_WINDOW_WIDTH = 2048;
	public static final int MAX_WINDOW_HEIGHT = 2048;

	public static final int MAX_USER_CLIP_PLANES = 6;
	public static final int NUMBER_OF_TEXTURE_UNITS = 4;

	public static final int MAX_NUMBER_OF_LIGHTS = 8;

	public static final float MIN_POINT_SIZE = 0.5f;
	public static final float MAX_POINT_SIZE = 10.0f;
	public static final float POINT_SIZE_GRANULARITY = 0.125f;
	public static final int MIN_ALIASED_POINT_SIZE = 1;
	public static final int MAX_ALIASED_POINT_SIZE = 64;

	public static final float MIN_LINE_WIDTH = 0.5f;
	public static final float MAX_LINE_WIDTH = 10.0f;
	public static final float LINE_WIDTH_GRANULARITY = 0.125f;
	public static final int MIN_ALIASED_LINE_WIDTH = 1;
	public static final int MAX_ALIASED_LINE_WIDTH = 64;

	public static final int MAX_MIPMAP_LEVEL = 11;
	public static final int MAX_TEXTURE_SIZE = 1 << (MAX_MIPMAP_LEVEL - 1);

	public static final int MAX_NAME_STACK_DEPTH = 128;

	public static final int CLIP_LEFT = (1 << 0);
	public static final int CLIP_RIGHT = (1 << 1);
	public static final int CLIP_BOTTOM = (1 << 2);
	public static final int CLIP_TOP = (1 << 3);
	public static final int CLIP_NEAR = (1 << 4);
	public static final int CLIP_FAR = (1 << 5);

	public static final int CLIP_FRUSTUM_MASK = 0x3F;

	public static final int CLIP_USER0 = (1 << 6);
	public static final int CLIP_USER_MASK = 0xFFC0; // (1 << 16) - CLIP_USER0;

	public static final int CLIP_MASK = CLIP_USER_MASK | CLIP_FRUSTUM_MASK;

	public static final int VALIDATE_BUFFER = (1 << 0);
	public static final int VALIDATE_TEXTURE = (1 << 1);

	public static final int VALIDATE_ALL = 0x3; // (1 << 2) - 1;

	public static final int TEXTURE_1D_ENABLE = (1 << 0);
	public static final int TEXTURE_2D_ENABLE = (1 << 1);
	public static final int TEXTURE_3D_ENABLE = (1 << 2);

	public static final int TEXTURE_xD_ENABLE_MASK = 0x7; // (1 << 3) - 1;

	public static final int TEXTURE_GEN_S_ENABLE = (1 << 3);
	public static final int TEXTURE_GEN_T_ENABLE = (1 << 4);
	public static final int TEXTURE_GEN_R_ENABLE = (1 << 5);
	public static final int TEXTURE_GEN_Q_ENABLE = (1 << 6);

	public static final int TEXTURE_GEN_ENABLE_MASK = 0x78; // 0111 1000

	/**
	 * Number of environment bindings supported by each texture unit.
	 */
	public static final int NUMBER_OF_TEXTURE_ENV_BINDINGS_PER_UNIT = 1;

	public static final int MAX_EVAL_ORDER = 30;
	public static final int MAP_RANGE_COUNT = 9;

	public static final int C4 = 0;
	public static final int I = 1;
	public static final int N3 = 2;
	public static final int T1 = 3;
	public static final int T2 = 4;
	public static final int T3 = 5;
	public static final int T4 = 6;
	public static final int V3 = 7;
	public static final int V4 = 8;

	// Bits in "eval1" enable word
	public static final int MAP1_VERTEX_3_ENABLE = (1 << V3);
	public static final int MAP1_VERTEX_4_ENABLE = (1 << V4);
	public static final int MAP1_COLOR_4_ENABLE = (1 << C4);
	public static final int MAP1_INDEX_ENABLE = (1 << I);
	public static final int MAP1_NORMAL_ENABLE = (1 << N3);
	public static final int MAP1_TEXTURE_COORD_1_ENABLE = (1 << T1);
	public static final int MAP1_TEXTURE_COORD_2_ENABLE = (1 << T2);
	public static final int MAP1_TEXTURE_COORD_3_ENABLE = (1 << T3);
	public static final int MAP1_TEXTURE_COORD_4_ENABLE = (1 << T4);

	// Bits in "eval2" enable word
	public static final int MAP2_VERTEX_3_ENABLE = (1 << V3);
	public static final int MAP2_VERTEX_4_ENABLE = (1 << V4);
	public static final int MAP2_COLOR_4_ENABLE = (1 << C4);
	public static final int MAP2_INDEX_ENABLE = (1 << I);
	public static final int MAP2_NORMAL_ENABLE = (1 << N3);
	public static final int MAP2_TEXTURE_COORD_1_ENABLE = (1 << T1);
	public static final int MAP2_TEXTURE_COORD_2_ENABLE = (1 << T2);
	public static final int MAP2_TEXTURE_COORD_3_ENABLE = (1 << T3);
	public static final int MAP2_TEXTURE_COORD_4_ENABLE = (1 << T4);

	//    public static final int __GL_TEXTURE_2D_ENABLE = (1 << 1);
	//    public static final int __GL_TEXTURE_3D_ENABLE = (1 << 2);
	//    public static final int __GL_TEXTURE_GEN_S_ENABLE = (1 << 3);
	//    public static final int __GL_TEXTURE_GEN_T_ENABLE = (1 << 4);
	//    public static final int __GL_TEXTURE_GEN_R_ENABLE = (1 << 5);
	//    public static final int __GL_TEXTURE_GEN_Q_ENABLE = (1 << 6);

	/*
	 ** Number of texture units.  Each texture unit includes a complete
	 ** set of texture bindings and a complete set of environment bindings.
	 */
	//    public static final int __GL_NUM_TEXTURE_UNITS = 4;
	/*
	 ** Number of texture bindings supported by each texture unit.
	 */
	//    public static final int __GL_NUM_TEXTURE_BINDINGS = 3;
	//    public static final int __GL_TEXTURE_1D_INDEX = 0;
	//    public static final int __GL_TEXTURE_2D_INDEX = 1;
	//    public static final int __GL_TEXTURE_3D_INDEX = 2;
	/*
	 ** Number of environment bindings supported by each texture unit.
	 */
	//    public static final int __GL_NUM_TEXTURE_ENV_BINDINGS = 1;
	/*
	 ** Bit values for dirtyMask word.
	 **
	 ** These are all for delayed validation.  There are a few things that do
	 ** not trigger delayed validation.  They are:
	 **
	 ** Matrix operations -- matrices (but not inverse) are validated immediately.
	 ** Material changes -- they also validate immediately.
	 ** Color Material change -- validated immediately.
	 ** Color Material enable -- validated immediately.
	 ** Pixel Map changes -- no validation.
	 */

	/*
	 ** All things not listed elsewhere.
	 */
	//    public static final int __GL_DIRTY_GENERIC = 0x00000001;
	/*
	 ** Line stipple, line stipple enable, line width, line smooth enable,
	 ** line smooth hint.
	 */
	//    public static final int __GL_DIRTY_LINE = 0x00000002;
	/*
	 ** Polygon stipple, polygon stipple enable, polygon smooth enable, face
	 ** culling, front face orientation, polygon mode, point smooth hint.
	 ** EXT Extension: polygon offset
	 */
	//    public static final int __GL_DIRTY_POLYGON = 0x00000004;
	/*
	 ** Point smooth, point smooth hint, point width.
	 */
	//    public static final int __GL_DIRTY_POINT = 0x00000008;
	/*
	 ** Pixel store, pixel zoom, pixel transfer, (pixel maps don't cause
	 ** validation), read buffer.
	 */
	//    public static final int __GL_DIRTY_PIXEL = 0x00000010;
	/*
	 ** Light, Light Model, lighting enable, lightx enable, (color material
	 ** validates immediately), (NOT shade model -- it is generic), (color material
	 ** enable validates immediately)
	 */
	//    public static final int __GL_DIRTY_LIGHTING = 0x00000020;
	/*
	 ** Polygon stipple
	 */
	//    public static final int __GL_DIRTY_POLYGON_STIPPLE = 0x00000040;
	/*
	 ** The depth mode has changed.
	 */
	//    public static final int __GL_DIRTY_DEPTH = 0x00000080;
	/*
	 ** The scissor mode has changed.
	 */
	//    public static final int __GL_DIRTY_SCISSOR = 0x00000100;
	/*
	 ** The vertices array enables and/or pointers have changed.
	 */
	//    public static final int __GL_DIRTY_VERTARRAY = 0x00000200;
	/*
	 ** The model-view matrix has changed.
	 */
	//    public static final int __GL_DIRTY_MODELVIEW = 0x00000400;
	//    public static final int __GL_DIRTY_ALL = 0x000007ff;
	/*
	 ** The distinction between __GL_SHADE_SMOOTH and __GL_SHADE_SMOOTH_LIGHT is
	 ** simple.  __GL_SHADE_SMOOTH indicates if the polygon will be smoothly
	 ** shaded, and __GL_SHADE_SMOOTH_LIGHT indicates if the polygon will be
	 ** lit at each vertices.  Note that __GL_SHADE_SMOOTH might be set while
	 ** __GL_SHADE_SMOOTH_LIGHT is not set if the lighting model is GL_FLAT, but
	 ** the polygons are fogged.
	 */
	//    public static final int __GL_SHADE_RGB = 0x0001;
	//    public static final int __GL_SHADE_SMOOTH = 0x0002; /* smooth shaded polygons */
	//    public static final int __GL_SHADE_DEPTH_TEST = 0x0004;
	//    public static final int __GL_SHADE_TEXTURE = 0x0008;
	//    public static final int __GL_SHADE_STIPPLE = 0x0010; /* polygon stipple */
	//    public static final int __GL_SHADE_STENCIL_TEST = 0x0020;
	//    public static final int __GL_SHADE_DITHER = 0x0040;
	//    public static final int __GL_SHADE_LOGICOP = 0x0080;
	//    public static final int __GL_SHADE_BLEND = 0x0100;
	//    public static final int __GL_SHADE_ALPHA_TEST = 0x0200;
	//    public static final int __GL_SHADE_TWOSIDED = 0x0400;
	//    public static final int __GL_SHADE_MASK = 0x0800;
	/* Two kinds of fog... */
	//    public static final int __GL_SHADE_SLOW_FOG = 0x1000;
	//    public static final int __GL_SHADE_CHEAP_FOG = 0x2000;
	/* do we iterate depth values in software */
	//    public static final int __GL_SHADE_DEPTH_ITER = 0x4000;
	//
	//    public static final int __GL_SHADE_LINE_STIPPLE = 0x8000;
	//
	//    public static final int __GL_SHADE_CULL_FACE = 0x00010000;
	//    public static final int __GL_SHADE_SMOOTH_LIGHT = 0x00020000; /* smoothly lit polygons */
	//
	//    public static final int __GL_SHADE_POLYGON_OFFSET_POINT = 0x00040000; /* point offset */
	//    public static final int __GL_SHADE_POLYGON_OFFSET_LINE = 0x00080000; /* line offset */
	//    public static final int __GL_SHADE_POLYGON_OFFSET_FILL = 0x00100000; /* polygon offset */
	//
	//    public static final int __GL_SHADE_OWNERSHIP_TEST = 0x00200000;
	/* are texture coordinates projected before rasterization */
	//    public static final int __GL_SHADE_PROJSCALED_TEXTURE = 0x00400000;
	/* do we iterate scaled texture coordinates */
	//    public static final int __GL_SHADE_UVSCALED_TEXTURE = 0x00800000;
	/* do we iterate perspective correct texture coordinates */
	//    public static final int __GL_SHADE_PERSP_TEXTURE = 0x01000000;
	/* are we mipmapping */
	//    public static final int __GL_SHADE_MIPMAP_TEXTURE = 0x02000000;
	/* do we need source alpha (for blending, alpha testing, dest alpha) */
	//    public static final int __GL_SHADE_SRC_ALPHA = 0x04000000;
	//    public static final int __GL_SHADE_SECONDARY_COLOR = 0x08000000;
	/*
	 ** ScriptConstants for access shader and fragment colors / interpolators.
	 */
	public static final int __GL_PRIMARY_COLOR = 0;
	public static final int __GL_SECONDARY_COLOR = 1;
	public static final int __GL_NUM_FRAGMENT_COLORS = 2;

	/*
	 ** Some pixel code depends upon this constant in deciding what the longest
	 ** horizontal span of a DrawPixels command can possibly be.  With 2048, it
	 ** is allocating an array of size 32K.  If this constant increases too much,
	 ** then the pixel code might need to be revisited.
	 */
	//    public static final int __GL_MAX_MAX_VIEWPORT = 2048;
	/*
	 ** __GL_STIPPLE_COUNT_BITS is the number of bits needed to represent a
	 ** stipple count (5 bits).
	 **
	 ** __GL_STIPPLE_BITS is the number of bits in a stipple word (32 bits).
	 */
	//    public static final int __GL_STIPPLE_COUNT_BITS = 5;
	//    public static final int __GL_STIPPLE_BITS = (1 << __GL_STIPPLE_COUNT_BITS);
	//
	//    public static final int __GL_X_MAJOR = 0;
	//    public static final int __GL_Y_MAJOR = 1;
	//
	//    public static final float __TWO_31 = 2147483648.0f;
	/* Indicies for colors[] array in vertices */
	public static final int __GL_FRONTFACE = 0;
	public static final int __GL_BACKFACE = 1;
	public static final int __GL_FRONTFACE_SPECULAR = 2;
	public static final int __GL_BACKFACE_SPECULAR = 3;

	/*
	 ** Definitions of bits for flags
	 */

	/* Bits for has */
	//    public static final int __GL_HAS_SHIFT = 0;
	//    public static final int __GL_HAS_FRONT_COLOR = (0x0001 << __GL_HAS_SHIFT);
	//    public static final int __GL_HAS_BACK_COLOR = (0x0002 << __GL_HAS_SHIFT);
	//    public static final int __GL_HAS_TEXTURE = (0x0004 << __GL_HAS_SHIFT);
	//    public static final int __GL_HAS_NORMAL = (0x0008 << __GL_HAS_SHIFT);
	//    public static final int __GL_HAS_EYE = (0x0010 << __GL_HAS_SHIFT);
	//    public static final int __GL_HAS_CLIP = (0x0020 << __GL_HAS_SHIFT) | __GL_HAS_EYE;
	//    public static final int __GL_HAS_CLIPCODES = (0x0040 << __GL_HAS_SHIFT) | __GL_HAS_CLIP;
	//    public static final int __GL_HAS_FOG = (0x0080 << __GL_HAS_SHIFT) | __GL_HAS_EYE;
	//    public static final int __GL_HAS_NORMALIZED_DEVICE = (0x0100 << __GL_HAS_SHIFT) | __GL_HAS_CLIP;
	//    public static final int __GL_HAS_WINDOW = (0x0200 << __GL_HAS_SHIFT) | __GL_HAS_NORMALIZED_DEVICE;
	//    public static final int __GL_HAS_MASK = (0x02FF << __GL_HAS_SHIFT);
	//    public static final int __GL_HAS_BOTH = (__GL_HAS_FRONT_COLOR | __GL_HAS_BACK_COLOR);
	//    public static final int __GL_HAS_LIGHTING = (__GL_HAS_EYE | __GL_HAS_NORMAL);
	/* Bits for general */
	//    public static final int __GL_VERTEX_GENERAL_SHIFT = 12;
	//    public static final int __GL_VERTEX_EDGE_FLAG = (0x1 << __GL_VERTEX_GENERAL_SHIFT);
	//    public static final int __GL_VERTEX_CULL_FLAG = (0x2 << __GL_VERTEX_GENERAL_SHIFT);
	//    public static final int __GL_VERTEX_GENERAL_MASK = (0x3 << __GL_VERTEX_GENERAL_SHIFT);
	/* Bits for vertices sizes (2,3,4 D) */
	//    public static final int __GL_VERTEX_SIZE_SHIFT = 14;
	//    public static final int __GL_VERTEX_0D_INDEX = 0x0;
	//    public static final int __GL_VERTEX_0D_SIZE = (__GL_VERTEX_0D_INDEX << __GL_VERTEX_SIZE_SHIFT);
	//    public static final int __GL_VERTEX_2D_INDEX = 0x1;
	//    public static final int __GL_VERTEX_2D_SIZE = (__GL_VERTEX_2D_INDEX << __GL_VERTEX_SIZE_SHIFT);
	//    public static final int __GL_VERTEX_3D_INDEX = 0x2;
	//    public static final int __GL_VERTEX_3D_SIZE = (__GL_VERTEX_3D_INDEX << __GL_VERTEX_SIZE_SHIFT);
	//    public static final int __GL_VERTEX_4D_INDEX = 0x3;
	//    public static final int __GL_VERTEX_4D_SIZE = (__GL_VERTEX_4D_INDEX << __GL_VERTEX_SIZE_SHIFT);
	//    public static final int __GL_VERTEX_SIZE_MASK = (0x3 << __GL_VERTEX_SIZE_SHIFT);
	//    public static final int __GL_MAX_VERTEX_SIZE = 4;
	/* Bits for clip codes (view frustum and user defined) */
	//    public static final int __GL_CLIP_SHIFT = 16;
	//    public static final int __GL_CLIP_LEFT = (0x0001 << __GL_CLIP_SHIFT);
	//    public static final int __GL_CLIP_RIGHT = (0x0002 << __GL_CLIP_SHIFT);
	//    public static final int __GL_CLIP_BOTTOM = (0x0004 << __GL_CLIP_SHIFT);
	//    public static final int __GL_CLIP_TOP = (0x0008 << __GL_CLIP_SHIFT);
	//    public static final int __GL_CLIP_NEAR = (0x0010 << __GL_CLIP_SHIFT);
	//    public static final int __GL_CLIP_FAR = (0x0020 << __GL_CLIP_SHIFT);
	//    public static final int __GL_CLIP_FRUSTUM_MASK = (0x003f << __GL_CLIP_SHIFT);
	//    public static final int __GL_CLIP_USER0 = (0x0040 << __GL_CLIP_SHIFT);
	//    public static final int __GL_CLIP_USER_MASK = (0xffc0 << __GL_CLIP_SHIFT);
	//    public static final int __GL_CLIP_MASK = (__GL_CLIP_USER_MASK | __GL_CLIP_FRUSTUM_MASK);
	//
	//    public static final int __GL_CLIP_ALL_MASK = (__GL_CLIP_MASK | __GL_VERTEX_CULL_FLAG);
	//    public static final int __GL_MAX_USER_CLIP_PLANES = 6;
	// Bits in "general" enable word
	//    public static final int __GL_ALPHA_TEST_ENABLE = (1 << 0);
	//    public static final int __GL_BLEND_ENABLE = (1 << 1);
	//    public static final int __GL_INDEX_LOGIC_OP_ENABLE = (1 << 2);
	//    public static final int __GL_DITHER_ENABLE = (1 << 3);
	//    public static final int __GL_DEPTH_TEST_ENABLE = (1 << 4);
	//    public static final int __GL_FOG_ENABLE = (1 << 5);
	//    public static final int __GL_LIGHTING_ENABLE = (1 << 6);
	//    public static final int __GL_COLOR_MATERIAL_ENABLE = (1 << 7);
	//    public static final int __GL_LINE_STIPPLE_ENABLE = (1 << 8);
	//    public static final int __GL_LINE_SMOOTH_ENABLE = (1 << 9);
	//    public static final int __GL_POINT_SMOOTH_ENABLE = (1 << 10);
	//    public static final int __GL_POLYGON_SMOOTH_ENABLE = (1 << 11);
	//    public static final int __GL_CULL_FACE_ENABLE = (1 << 12);
	//    public static final int __GL_POLYGON_STIPPLE_ENABLE = (1 << 13);
	//    public static final int __GL_SCISSOR_TEST_ENABLE = (1 << 14);
	//    public static final int __GL_STENCIL_TEST_ENABLE = (1 << 15);
	//    public static final int __GL_NORMALIZE_ENABLE = (1 << 16);
	//    public static final int __GL_AUTO_NORMAL_ENABLE = (1 << 17);
	//    public static final int __GL_POLYGON_OFFSET_POINT_ENABLE = (1 << 18);
	//    public static final int __GL_POLYGON_OFFSET_LINE_ENABLE = (1 << 19);
	//    public static final int __GL_POLYGON_OFFSET_FILL_ENABLE = (1 << 20);
	//    public static final int __GL_COLOR_LOGIC_OP_ENABLE = (1 << 21);
	//    public static final int __GL_CULL_VERTEX_ENABLE = (1 << 22);
	//    public static final int __GL_RESCALE_NORMAL_ENABLE = (1 << 23);
	//    public static final int __GL_POLYGON_OFFSET_ENABLES = (__GL_POLYGON_OFFSET_POINT_ENABLE | __GL_POLYGON_OFFSET_LINE_ENABLE | __GL_POLYGON_OFFSET_FILL_ENABLE);
	/*
	 ** Composities of the above bits for each glPushAttrib group that has
	 ** multiple enables, except for those defined below
	 */
	//    public static final int __GL_COLOR_BUFFER_ENABLES = (__GL_ALPHA_TEST_ENABLE | __GL_BLEND_ENABLE | __GL_INDEX_LOGIC_OP_ENABLE | __GL_COLOR_LOGIC_OP_ENABLE | __GL_DITHER_ENABLE);
	//
	//    public static final int __GL_LIGHTING_ENABLES = (__GL_LIGHTING_ENABLE | __GL_COLOR_MATERIAL_ENABLE);
	//    public static final int __GL_LINE_ENABLES = (__GL_LINE_STIPPLE_ENABLE | __GL_LINE_SMOOTH_ENABLE);
	//    public static final int __GL_POLYGON_ENABLES = (__GL_POLYGON_SMOOTH_ENABLE | __GL_CULL_FACE_ENABLE | __GL_POLYGON_STIPPLE_ENABLE | __GL_POLYGON_OFFSET_ENABLES);
	// Bits in "texture" enable word
	//    public static final int __GL_TEXTURE_1D_ENABLE = (1 << 0);
	//    public static final int __GL_TEXTURE_2D_ENABLE = (1 << 1);
	//    public static final int __GL_TEXTURE_3D_ENABLE = (1 << 2);
	//    public static final int __GL_TEXTURE_GEN_S_ENABLE = (1 << 3);
	//    public static final int __GL_TEXTURE_GEN_T_ENABLE = (1 << 4);
	//    public static final int __GL_TEXTURE_GEN_R_ENABLE = (1 << 5);
	//    public static final int __GL_TEXTURE_GEN_Q_ENABLE = (1 << 6);
	//    public static final int __GL_MAP_RANGE_COUNT = 9;
	//
	//    public static final int __GL_C4 = 0;
	//    public static final int __GL_I = 1;
	//    public static final int __GL_N3 = 2;
	//    public static final int __GL_T1 = 3;
	//    public static final int __GL_T2 = 4;
	//    public static final int __GL_T3 = 5;
	//    public static final int __GL_T4 = 6;
	//    public static final int __GL_V3 = 7;
	//    public static final int __GL_V4 = 8;
	/* Bits in "eval1" enable word */
	//    public static final int __GL_MAP1_VERTEX_3_ENABLE = (1 << __GL_V3);
	//    public static final int __GL_MAP1_VERTEX_4_ENABLE = (1 << __GL_V4);
	//    public static final int __GL_MAP1_COLOR_4_ENABLE = (1 << __GL_C4);
	//    public static final int __GL_MAP1_INDEX_ENABLE = (1 << __GL_I);
	//    public static final int __GL_MAP1_NORMAL_ENABLE = (1 << __GL_N3);
	//    public static final int __GL_MAP1_TEXTURE_COORD_1_ENABLE = (1 << __GL_T1);
	//    public static final int __GL_MAP1_TEXTURE_COORD_2_ENABLE = (1 << __GL_T2);
	//    public static final int __GL_MAP1_TEXTURE_COORD_3_ENABLE = (1 << __GL_T3);
	//    public static final int __GL_MAP1_TEXTURE_COORD_4_ENABLE = (1 << __GL_T4);
	/* Bits in "eval2" enable word */
	//    public static final int __GL_MAP2_VERTEX_3_ENABLE = (1 << __GL_V3);
	//    public static final int __GL_MAP2_VERTEX_4_ENABLE = (1 << __GL_V4);
	//    public static final int __GL_MAP2_COLOR_4_ENABLE = (1 << __GL_C4);
	//    public static final int __GL_MAP2_INDEX_ENABLE = (1 << __GL_I);
	//    public static final int __GL_MAP2_NORMAL_ENABLE = (1 << __GL_N3);
	//    public static final int __GL_MAP2_TEXTURE_COORD_1_ENABLE = (1 << __GL_T1);
	//    public static final int __GL_MAP2_TEXTURE_COORD_2_ENABLE = (1 << __GL_T2);
	//    public static final int __GL_MAP2_TEXTURE_COORD_3_ENABLE = (1 << __GL_T3);
	//    public static final int __GL_MAP2_TEXTURE_COORD_4_ENABLE = (1 << __GL_T4);
	// constants
	//    public static final float __GL_DEFAULT_POINT_SIZE_MINIMUM = 0.5f;
	//    public static final float __GL_DEFAULT_POINT_SIZE_MAXIMUM = 10.0f;
	//    public static final float __GL_DEFAULT_POINT_SIZE_GRANULARITY = 0.125f;
	//    public static final int __GL_DEFAULT_ALIASED_POINT_SIZE_MINIMUM = 1;
	//    public static final int __GL_DEFAULT_ALIASED_POINT_SIZE_MAXIMUM = 64;
	//
	//    public static final float __GL_DEFAULT_LINE_WIDTH_MINIMUM = 0.5f;
	//    public static final float __GL_DEFAULT_LINE_WIDTH_MAXIMUM = 10.0f;
	//    public static final float __GL_DEFAULT_LINE_WIDTH_GRANULARITY = 0.125f;
	//    public static final int __GL_DEFAULT_ALIASED_LINE_WIDTH_MINIMUM = 1;
	//    public static final int __GL_DEFAULT_ALIASED_LINE_WIDTH_MAXIMUM = 64;
	//
	//    public static final int __GL_DEFAULT_MAX_WINDOW_HEIGHT = 2048;
	//    public static final int __GL_DEFAULT_MAX_WINDOW_WIDTH = 2048;
	//
	//    public static final int __GL_DEFAULT_NUMBER_OF_CLIP_PLANES = 6;
	//    public static final int __GL_DEFAULT_NUMBER_OF_LIGHTS = 8;
	//
	//    public static final int __GL_DEFAULT_MAX_EVAL_ORDER = 30;
	//
	//    public static final int __GL_DEFAULT_MAX_MIPMAP_LEVEL = 11;
	//    public static final int __GL_DEFAULT_MAX_TEXTURE_SIZE = 1 << (__GL_DEFAULT_MAX_MIPMAP_LEVEL - 1);
	//
	//    public static final int __GL_DEFAULT_MAX_NAME_STACK_DEPTH = 128;
	//
	//    public static final int __GL_CULL_FLAG_FRONT = __GL_FRONTFACE;
	//    public static final int __GL_CULL_FLAG_BACK = __GL_BACKFACE;
	//    public static final int __GL_CULL_FLAG_DONT = 2;
	//
	//    public static final int __GL_CW = 0;
	//    public static final int __GL_CCW = 1;
	//
	//    /* Internal numbering for polymode values */
	//    public static final int __GL_POLYGON_MODE_FILL = GL_FILL;
	//    public static final int __GL_POLYGON_MODE_LINE = GL_LINE;
	//    public static final int __GL_POLYGON_MODE_POINT = GL_POINT;
	//
	//    /* masks for the buffers */
	//    public static final int __GL_FRONT_BUFFER_MASK = 0x00000001;
	//    public static final int __GL_FRONT_LEFT_BUFFER_MASK = 0x00000001;
	//    public static final int __GL_FRONT_RIGHT_BUFFER_MASK = 0x00000002;
	//    public static final int __GL_BACK_BUFFER_MASK = 0x00000004;
	//    public static final int __GL_BACK_LEFT_BUFFER_MASK = 0x00000004;
	//    public static final int __GL_BACK_RIGHT_BUFFER_MASK = 0x00000008;
	//    public static final int __GL_ACCUM_BUFFER_MASK = 0x00000010;
	//    public static final int __GL_DEPTH_BUFFER_MASK = 0x00000020;
	//    public static final int __GL_STENCIL_BUFFER_MASK = 0x00000040;
	//
	//    public static final int __GL_NUM_COLOR_TABLE_TARGETS = 3;
	//    public static final int __GL_COLOR_TABLE_INDEX = 0;
	//    public static final int __GL_POST_CONVOLUTION_COLOR_TABLE_INDEX = 1;
	//    public static final int __GL_POST_COLOR_MATRIX_COLOR_TABLE_INDEX = 2;
	//
	//    public static final int __GL_NUM_CONVOLUTION_TARGETS = 3;
	//    public static final int __GL_CONVOLUTION_1D_INDEX = 0;
	//    public static final int __GL_CONVOLUTION_2D_INDEX = 1;
	//    public static final int __GL_SEPARABLE_2D_INDEX = 2;
	//
	//    /*
	//    ** Bit values for the validateMask word
	//    */
	//    public static final int __GL_VALIDATE_ALPHA_FUNC = 0x00000001;
	//    public static final int __GL_VALIDATE_STENCIL_FUNC = 0x00000002;
	//    public static final int __GL_VALIDATE_STENCIL_OP = 0x00000004;
	//    public static final int __GL_VALIDATE_INDEX_FUNC = 0x00000008;
	//
	//    /*
	//    ** Bit values for changes to material colors
	//    */
	//    public static final int __GL_MATERIAL_AMBIENT = 0x00000001;
	//    public static final int __GL_MATERIAL_DIFFUSE = 0x00000002;
	//    public static final int __GL_MATERIAL_SPECULAR = 0x00000004;
	//    public static final int __GL_MATERIAL_EMISSIVE = 0x00000008;
	//    public static final int __GL_MATERIAL_SHININESS = 0x00000010;
	//    public static final int __GL_MATERIAL_COLORINDEXES = 0x00000020;
	//    public static final int __GL_MATERIAL_ALL = 0x0000003f;
	/**
	 * This defines the stuff in gc->vertArrayState.signature, which is a
	 * bitmask describing current vertex array enables. ven_get.c has to see
	 * these definitions
	 */

	/**
	 * The signature field is set up the following way:
	 * (hi)
	 * 4 bits of index type 	0xf0000000
	 * 1 bit of edge existence	0x08000000
	 * 3 bits of texture size	0x07000000
	 * 4 bits of texture type	0x00f00000
	 * 4 bits of color size		0x000f0000
	 * 4 bits of color type		0x0000f000
	 * 4 bits of normal type 	0x00000f00
	 * 4 bits of vertex size	0x000000f0
	 * 4 bits of vertex type	0x0000000f
	 * (lo)
	 *
	 * Note that edge existence flag is cramped in with texture size (just because
	 * we don't have room for everything to have 4 bits)
	 * Types are mapped into numbers from by TypeFlag(GLenum)
	 */

	public static int VERTARRAY_V_MASK = 0x000000ff;
	public static int VERTARRAY_V_INDEX = 32;
	public static int VERTARRAY_VTYPE_SHIFT = 0;
	public static int VERTARRAY_VTYPE_MASK = (0xf << VERTARRAY_VTYPE_SHIFT);
	public static int VERTARRAY_VSIZE_SHIFT = 4;
	public static int VERTARRAY_VSIZE_MASK = (0xf << VERTARRAY_VSIZE_SHIFT);

	public static int VERTARRAY_N_MASK = 0x00000f00;
	public static int VERTARRAY_N_INDEX = 1;
	public static int VERTARRAY_NTYPE_SHIFT = 8;
	public static int VERTARRAY_NTYPE_MASK = (0xf << VERTARRAY_NTYPE_SHIFT);

	public static int VERTARRAY_C_MASK = 0x000ff000;
	public static int VERTARRAY_C_INDEX = 2;
	public static int VERTARRAY_CTYPE_SHIFT = 12;
	public static int VERTARRAY_CTYPE_MASK = (0xf << VERTARRAY_CTYPE_SHIFT);
	public static int VERTARRAY_CSIZE_SHIFT = 16;
	public static int VERTARRAY_CSIZE_MASK = (0xf << VERTARRAY_CSIZE_SHIFT);

	public static int VERTARRAY_T_MASK = 0x07f00000;
	public static int VERTARRAY_T_INDEX = 4;
	public static int VERTARRAY_TTYPE_SHIFT = 20;
	public static int VERTARRAY_TTYPE_MASK = (0xf << VERTARRAY_TTYPE_SHIFT);
	public static int VERTARRAY_TSIZE_SHIFT = 24;
	public static int VERTARRAY_TSIZE_MASK = (0x7 << VERTARRAY_TSIZE_SHIFT);

	public static int VERTARRAY_E_MASK = 0x08000000;
	public static int VERTARRAY_E_INDEX = 16;

	public static int VERTARRAY_I_MASK = 0xf0000000;
	public static int VERTARRAY_I_INDEX = 8;
	public static int VERTARRAY_ITYPE_SHIFT = 28;
	public static int VERTARRAY_ITYPE_MASK = (0xf << VERTARRAY_ITYPE_SHIFT);

	/**
	 * type flags for signatures.  we add one here to make sure that
	 * the part of the signature corresponding to something that is enabled
	 * (and has been correctly specified) is non-zero.  Kona ucode uses
	 * this fact, and also the connection to the GL_<whatever> values.
	 * also, all values must be representable as a 4-bit number.
	 */

	public static int VERTARRAY_BYTE_FLAG = (GL_BYTE - GL_BYTE + 1);
	public static int VERTARRAY_UNSIGNED_BYTE_FLAG = (GL_UNSIGNED_BYTE - GL_BYTE + 1);
	public static int VERTARRAY_SHORT_FLAG = (GL_SHORT - GL_BYTE + 1);
	public static int VERTARRAY_UNSIGNED_SHORT_FLAG = (GL_UNSIGNED_SHORT - GL_BYTE + 1);
	public static int VERTARRAY_INT_FLAG = (GL_INT - GL_BYTE + 1);
	public static int VERTARRAY_UNSIGNED_INT_FLAG = (GL_UNSIGNED_INT - GL_BYTE + 1);
	public static int VERTARRAY_FLOAT_FLAG = (GL_FLOAT - GL_BYTE + 1);
	public static int VERTARRAY_DOUBLE_FLAG = (GL_DOUBLE - GL_BYTE + 1);
}
