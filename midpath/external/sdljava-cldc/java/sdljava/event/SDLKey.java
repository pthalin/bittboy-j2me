package sdljava.event;

/**
 *  sdljava - a java binding to the SDL API
 *
 *  Copyright (C) 2004  Ivan Z. Ganza
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *  USA
 *
 *  Ivan Z. Ganza (ivan_ganza@yahoo.com)
 */

/**
 * SDL key bindings.
 *
 */
public final class SDLKey {
	public final static int SDLK_UNKNOWN = 0;
	public final static int SDLK_FIRST = 0;
	public final static int SDLK_BACKSPACE = 8;
	public final static int SDLK_TAB = 9;
	public final static int SDLK_CLEAR = 12;
	public final static int SDLK_RETURN = 13;
	public final static int SDLK_PAUSE = 19;
	public final static int SDLK_ESCAPE = 27;
	public final static int SDLK_SPACE = 32;
	public final static int SDLK_EXCLAIM = 33;
	public final static int SDLK_QUOTEDBL = 34;
	public final static int SDLK_HASH = 35;
	public final static int SDLK_DOLLAR = 36;
	public final static int SDLK_AMPERSAND = 38;
	public final static int SDLK_QUOTE = 39;
	public final static int SDLK_LEFTPAREN = 40;
	public final static int SDLK_RIGHTPAREN = 41;
	public final static int SDLK_ASTERISK = 42;
	public final static int SDLK_PLUS = 43;
	public final static int SDLK_COMMA = 44;
	public final static int SDLK_MINUS = 45;
	public final static int SDLK_PERIOD = 46;
	public final static int SDLK_SLASH = 47;
	public final static int SDLK_0 = 48;
	public final static int SDLK_1 = 49;
	public final static int SDLK_2 = 50;
	public final static int SDLK_3 = 51;
	public final static int SDLK_4 = 52;
	public final static int SDLK_5 = 53;
	public final static int SDLK_6 = 54;
	public final static int SDLK_7 = 55;
	public final static int SDLK_8 = 56;
	public final static int SDLK_9 = 57;
	public final static int SDLK_COLON = 58;
	public final static int SDLK_SEMICOLON = 59;
	public final static int SDLK_LESS = 60;
	public final static int SDLK_EQUALS = 61;
	public final static int SDLK_GREATER = 62;
	public final static int SDLK_QUESTION = 63;
	public final static int SDLK_AT = 64;
	/* 
	 Skip uppercase letters
	 */
	public final static int SDLK_LEFTBRACKET = 91;
	public final static int SDLK_BACKSLASH = 92;
	public final static int SDLK_RIGHTBRACKET = 93;
	public final static int SDLK_CARET = 94;
	public final static int SDLK_UNDERSCORE = 95;
	public final static int SDLK_BACKQUOTE = 96;
	public final static int SDLK_a = 97;
	public final static int SDLK_b = 98;
	public final static int SDLK_c = 99;
	public final static int SDLK_d = 100;
	public final static int SDLK_e = 101;
	public final static int SDLK_f = 102;
	public final static int SDLK_g = 103;
	public final static int SDLK_h = 104;
	public final static int SDLK_i = 105;
	public final static int SDLK_j = 106;
	public final static int SDLK_k = 107;
	public final static int SDLK_l = 108;
	public final static int SDLK_m = 109;
	public final static int SDLK_n = 110;
	public final static int SDLK_o = 111;
	public final static int SDLK_p = 112;
	public final static int SDLK_q = 113;
	public final static int SDLK_r = 114;
	public final static int SDLK_s = 115;
	public final static int SDLK_t = 116;
	public final static int SDLK_u = 117;
	public final static int SDLK_v = 118;
	public final static int SDLK_w = 119;
	public final static int SDLK_x = 120;
	public final static int SDLK_y = 121;
	public final static int SDLK_z = 122;
	public final static int SDLK_DELETE = 127;
	/* End of ASCII mapped keysyms */

	/* International keyboard syms */
	public final static int SDLK_WORLD_0 = 160; /* 0xA0 */
	public final static int SDLK_WORLD_1 = 161;
	public final static int SDLK_WORLD_2 = 162;
	public final static int SDLK_WORLD_3 = 163;
	public final static int SDLK_WORLD_4 = 164;
	public final static int SDLK_WORLD_5 = 165;
	public final static int SDLK_WORLD_6 = 166;
	public final static int SDLK_WORLD_7 = 167;
	public final static int SDLK_WORLD_8 = 168;
	public final static int SDLK_WORLD_9 = 169;
	public final static int SDLK_WORLD_10 = 170;
	public final static int SDLK_WORLD_11 = 171;
	public final static int SDLK_WORLD_12 = 172;
	public final static int SDLK_WORLD_13 = 173;
	public final static int SDLK_WORLD_14 = 174;
	public final static int SDLK_WORLD_15 = 175;
	public final static int SDLK_WORLD_16 = 176;
	public final static int SDLK_WORLD_17 = 177;
	public final static int SDLK_WORLD_18 = 178;
	public final static int SDLK_WORLD_19 = 179;
	public final static int SDLK_WORLD_20 = 180;
	public final static int SDLK_WORLD_21 = 181;
	public final static int SDLK_WORLD_22 = 182;
	public final static int SDLK_WORLD_23 = 183;
	public final static int SDLK_WORLD_24 = 184;
	public final static int SDLK_WORLD_25 = 185;
	public final static int SDLK_WORLD_26 = 186;
	public final static int SDLK_WORLD_27 = 187;
	public final static int SDLK_WORLD_28 = 188;
	public final static int SDLK_WORLD_29 = 189;
	public final static int SDLK_WORLD_30 = 190;
	public final static int SDLK_WORLD_31 = 191;
	public final static int SDLK_WORLD_32 = 192;
	public final static int SDLK_WORLD_33 = 193;
	public final static int SDLK_WORLD_34 = 194;
	public final static int SDLK_WORLD_35 = 195;
	public final static int SDLK_WORLD_36 = 196;
	public final static int SDLK_WORLD_37 = 197;
	public final static int SDLK_WORLD_38 = 198;
	public final static int SDLK_WORLD_39 = 199;
	public final static int SDLK_WORLD_40 = 200;
	public final static int SDLK_WORLD_41 = 201;
	public final static int SDLK_WORLD_42 = 202;
	public final static int SDLK_WORLD_43 = 203;
	public final static int SDLK_WORLD_44 = 204;
	public final static int SDLK_WORLD_45 = 205;
	public final static int SDLK_WORLD_46 = 206;
	public final static int SDLK_WORLD_47 = 207;
	public final static int SDLK_WORLD_48 = 208;
	public final static int SDLK_WORLD_49 = 209;
	public final static int SDLK_WORLD_50 = 210;
	public final static int SDLK_WORLD_51 = 211;
	public final static int SDLK_WORLD_52 = 212;
	public final static int SDLK_WORLD_53 = 213;
	public final static int SDLK_WORLD_54 = 214;
	public final static int SDLK_WORLD_55 = 215;
	public final static int SDLK_WORLD_56 = 216;
	public final static int SDLK_WORLD_57 = 217;
	public final static int SDLK_WORLD_58 = 218;
	public final static int SDLK_WORLD_59 = 219;
	public final static int SDLK_WORLD_60 = 220;
	public final static int SDLK_WORLD_61 = 221;
	public final static int SDLK_WORLD_62 = 222;
	public final static int SDLK_WORLD_63 = 223;
	public final static int SDLK_WORLD_64 = 224;
	public final static int SDLK_WORLD_65 = 225;
	public final static int SDLK_WORLD_66 = 226;
	public final static int SDLK_WORLD_67 = 227;
	public final static int SDLK_WORLD_68 = 228;
	public final static int SDLK_WORLD_69 = 229;
	public final static int SDLK_WORLD_70 = 230;
	public final static int SDLK_WORLD_71 = 231;
	public final static int SDLK_WORLD_72 = 232;
	public final static int SDLK_WORLD_73 = 233;
	public final static int SDLK_WORLD_74 = 234;
	public final static int SDLK_WORLD_75 = 235;
	public final static int SDLK_WORLD_76 = 236;
	public final static int SDLK_WORLD_77 = 237;
	public final static int SDLK_WORLD_78 = 238;
	public final static int SDLK_WORLD_79 = 239;
	public final static int SDLK_WORLD_80 = 240;
	public final static int SDLK_WORLD_81 = 241;
	public final static int SDLK_WORLD_82 = 242;
	public final static int SDLK_WORLD_83 = 243;
	public final static int SDLK_WORLD_84 = 244;
	public final static int SDLK_WORLD_85 = 245;
	public final static int SDLK_WORLD_86 = 246;
	public final static int SDLK_WORLD_87 = 247;
	public final static int SDLK_WORLD_88 = 248;
	public final static int SDLK_WORLD_89 = 249;
	public final static int SDLK_WORLD_90 = 250;
	public final static int SDLK_WORLD_91 = 251;
	public final static int SDLK_WORLD_92 = 252;
	public final static int SDLK_WORLD_93 = 253;
	public final static int SDLK_WORLD_94 = 254;
	public final static int SDLK_WORLD_95 = 255; /* 0xFF */

	/* Numeric keypad */
	public final static int SDLK_KP0 = 256;
	public final static int SDLK_KP1 = 257;
	public final static int SDLK_KP2 = 258;
	public final static int SDLK_KP3 = 259;
	public final static int SDLK_KP4 = 260;
	public final static int SDLK_KP5 = 261;
	public final static int SDLK_KP6 = 262;
	public final static int SDLK_KP7 = 263;
	public final static int SDLK_KP8 = 264;
	public final static int SDLK_KP9 = 265;
	public final static int SDLK_KP_PERIOD = 266;
	public final static int SDLK_KP_DIVIDE = 267;
	public final static int SDLK_KP_MULTIPLY = 268;
	public final static int SDLK_KP_MINUS = 269;
	public final static int SDLK_KP_PLUS = 270;
	public final static int SDLK_KP_ENTER = 271;
	public final static int SDLK_KP_EQUALS = 272;

	/* Arrows + Home/End pad */
	public final static int SDLK_UP = 273;
	public final static int SDLK_DOWN = 274;
	public final static int SDLK_RIGHT = 275;
	public final static int SDLK_LEFT = 276;
	public final static int SDLK_INSERT = 277;
	public final static int SDLK_HOME = 278;
	public final static int SDLK_END = 279;
	public final static int SDLK_PAGEUP = 280;
	public final static int SDLK_PAGEDOWN = 281;

	/* Function keys */
	public final static int SDLK_F1 = 282;
	public final static int SDLK_F2 = 283;
	public final static int SDLK_F3 = 284;
	public final static int SDLK_F4 = 285;
	public final static int SDLK_F5 = 286;
	public final static int SDLK_F6 = 287;
	public final static int SDLK_F7 = 288;
	public final static int SDLK_F8 = 289;
	public final static int SDLK_F9 = 290;
	public final static int SDLK_F10 = 291;
	public final static int SDLK_F11 = 292;
	public final static int SDLK_F12 = 293;
	public final static int SDLK_F13 = 294;
	public final static int SDLK_F14 = 295;
	public final static int SDLK_F15 = 296;

	/* Key state modifier keys */
	public final static int SDLK_NUMLOCK = 300;
	public final static int SDLK_CAPSLOCK = 301;
	public final static int SDLK_SCROLLOCK = 302;
	public final static int SDLK_RSHIFT = 303;
	public final static int SDLK_LSHIFT = 304;
	public final static int SDLK_RCTRL = 305;
	public final static int SDLK_LCTRL = 306;
	public final static int SDLK_RALT = 307;
	public final static int SDLK_LALT = 308;
	public final static int SDLK_RMETA = 309;
	public final static int SDLK_LMETA = 310;
	public final static int SDLK_LSUPER = 311; /* Left "Windows" key */
	public final static int SDLK_RSUPER = 312; /* Right "Windows" key */
	public final static int SDLK_MODE = 313; /* "Alt Gr" key */
	public final static int SDLK_COMPOSE = 314; /* Multi-key compose key */

	/* Miscellaneous function keys */
	public final static int SDLK_HELP = 315;
	public final static int SDLK_PRINT = 316;
	public final static int SDLK_SYSREQ = 317;
	public final static int SDLK_BREAK = 318;
	public final static int SDLK_MENU = 319;
	public final static int SDLK_POWER = 320; /* Power Macintosh power key */
	public final static int SDLK_EURO = 321; /* Some european keyboards */
}
