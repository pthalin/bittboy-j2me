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
package org.thenesis.microbackend.ui;

public class KeyConstants {

	/* Same keycodes than AWT */
	public static final int KEY_FIRST = 400, KEY_LAST = 402, KEY_TYPED = 400, KEY_PRESSED = 401, KEY_RELEASED = 402,
			VK_ENTER = '\n', VK_BACK_SPACE = '\b', VK_TAB = '\t', VK_CANCEL = 3, VK_CLEAR = 12, VK_SHIFT = 16,
			VK_CONTROL = 17, VK_ALT = 18, VK_PAUSE = 19, VK_CAPS_LOCK = 20, VK_ESCAPE = 27, VK_SPACE = ' ',
			VK_PAGE_UP = 33, VK_PAGE_DOWN = 34, VK_END = 35, VK_HOME = 36, VK_LEFT = 37, VK_UP = 38, VK_RIGHT = 39,
			VK_DOWN = 40, VK_COMMA = ',', VK_MINUS = '-', VK_PERIOD = '.', VK_SLASH = '/', VK_0 = '0', VK_1 = '1',
			VK_2 = '2', VK_3 = '3', VK_4 = '4', VK_5 = '5', VK_6 = '6', VK_7 = '7', VK_8 = '8', VK_9 = '9',
			VK_SEMICOLON = ';', VK_EQUALS = '=', VK_A = 'A', VK_B = 'B', VK_C = 'C', VK_D = 'D', VK_E = 'E',
			VK_F = 'F', VK_G = 'G', VK_H = 'H', VK_I = 'I', VK_J = 'J', VK_K = 'K', VK_L = 'L', VK_M = 'M', VK_N = 'N',
			VK_O = 'O', VK_P = 'P', VK_Q = 'Q', VK_R = 'R', VK_S = 'S', VK_T = 'T', VK_U = 'U', VK_V = 'V', VK_W = 'W',
			VK_X = 'X', VK_Y = 'Y', VK_Z = 'Z', VK_OPEN_BRACKET = '[', VK_BACK_SLASH = '\\', VK_CLOSE_BRACKET = ']',
			VK_NUMPAD0 = 96, VK_NUMPAD1 = 97, VK_NUMPAD2 = 98, VK_NUMPAD3 = 99, VK_NUMPAD4 = 100, VK_NUMPAD5 = 101,
			VK_NUMPAD6 = 102, VK_NUMPAD7 = 103, VK_NUMPAD8 = 104, VK_NUMPAD9 = 105, VK_MULTIPLY = 106, VK_ADD = 107,
			VK_SEPARATER = 108, VK_SEPARATOR = 108, VK_SUBTRACT = 109, VK_DECIMAL = 110, VK_DIVIDE = 111,
			VK_DELETE = 127, VK_NUM_LOCK = 144, VK_SCROLL_LOCK = 145, VK_F1 = 112, VK_F2 = 113, VK_F3 = 114,
			VK_F4 = 115, VK_F5 = 116, VK_F6 = 117, VK_F7 = 118, VK_F8 = 119, VK_F9 = 120, VK_F10 = 121, VK_F11 = 122,
			VK_F12 = 123, VK_F13 = 61440, VK_F14 = 61441, VK_F15 = 61442, VK_F16 = 61443, VK_F17 = 61444,
			VK_F18 = 61445, VK_F19 = 61446, VK_F20 = 61447, VK_F21 = 61448, VK_F22 = 61449, VK_F23 = 61450,
			VK_F24 = 61451, VK_PRINTSCREEN = 154, VK_INSERT = 155, VK_HELP = 156, VK_META = 157, VK_BACK_QUOTE = 192,
			VK_QUOTE = 222, VK_KP_UP = 224, VK_KP_DOWN = 225, VK_KP_LEFT = 226, VK_KP_RIGHT = 227, VK_DEAD_GRAVE = 128,
			VK_DEAD_ACUTE = 129, VK_DEAD_CIRCUMFLEX = 130, VK_DEAD_TILDE = 131, VK_DEAD_MACRON = 132,
			VK_DEAD_BREVE = 133, VK_DEAD_ABOVEDOT = 134, VK_DEAD_DIAERESIS = 135, VK_DEAD_ABOVERING = 136,
			VK_DEAD_DOUBLEACUTE = 137, VK_DEAD_CARON = 138, VK_DEAD_CEDILLA = 139, VK_DEAD_OGONEK = 140,
			VK_DEAD_IOTA = 141, VK_DEAD_VOICED_SOUND = 142, VK_DEAD_SEMIVOICED_SOUND = 143, VK_AMPERSAND = 150,
			VK_ASTERISK = 151, VK_QUOTEDBL = 152, VK_LESS = 153, VK_GREATER = 160, VK_BRACELEFT = 161,
			VK_BRACERIGHT = 162, VK_AT = 512, VK_COLON = 513, VK_CIRCUMFLEX = 514, VK_DOLLAR = 515, VK_EURO_SIGN = 516,
			VK_EXCLAMATION_MARK = 517, VK_INVERTED_EXCLAMATION_MARK = 518, VK_LEFT_PARENTHESIS = 519,
			VK_NUMBER_SIGN = 520, VK_PLUS = 521, VK_RIGHT_PARENTHESIS = 522, VK_UNDERSCORE = 523, VK_FINAL = 24,
			VK_CONVERT = 28, VK_NONCONVERT = 29, VK_ACCEPT = 30, VK_MODECHANGE = 31, VK_KANA = 21, VK_KANJI = 25,
			VK_ALPHANUMERIC = 240, VK_KATAKANA = 241, VK_HIRAGANA = 242, VK_FULL_WIDTH = 243, VK_HALF_WIDTH = 244,
			VK_ROMAN_CHARACTERS = 245, VK_ALL_CANDIDATES = 256, VK_PREVIOUS_CANDIDATE = 257, VK_CODE_INPUT = 258,
			VK_JAPANESE_KATAKANA = 259, VK_JAPANESE_HIRAGANA = 260, VK_JAPANESE_ROMAN = 261, VK_KANA_LOCK = 262,
			VK_INPUT_METHOD_ON_OFF = 263, VK_CUT = 65489, VK_COPY = 65485, VK_PASTE = 65487, VK_UNDO = 65483,
			VK_AGAIN = 65481, VK_FIND = 65488, VK_PROPS = 65482, VK_STOP = 65480, VK_COMPOSE = 65312,
			VK_ALT_GRAPH = 65406, VK_BEGIN = 65368, VK_CONTEXT_MENU = 525, VK_WINDOWS = 524, VK_UNDEFINED = 0;

	public static final char CHAR_UNDEFINED = '\uffff';

	public static String getName(int keyCode) {

		switch (keyCode) {
		case VK_ENTER:
			return "ENTER";
		case VK_BACK_SPACE:
			return "BACK SPACE";
		case VK_TAB:
			return "TAB";
		case VK_CANCEL:
			return "CANCEL";
		case VK_CLEAR:
			return "CLEAR";
		case VK_SHIFT:
			return "SHIFT";
		case VK_CONTROL:
			return "CONTROL";
		case VK_ALT:
			return "ALT";
		case VK_PAUSE:
			return "PAUSE";
		case VK_CAPS_LOCK:
			return "CAPS LOCK";
		case VK_ESCAPE:
			return "ESCAPE";
		case VK_SPACE:
			return "SPACE";
		case VK_PAGE_UP:
			return "PAGE UP";
		case VK_PAGE_DOWN:
			return "PAGE DOWN";
		case VK_END:
			return "END";
		case VK_HOME:
			return "HOME";
		case VK_LEFT:
			return "LEFT";
		case VK_UP:
			return "UP";
		case VK_RIGHT:
			return "RIGHT";
		case VK_DOWN:
			return "DOWN";
		case VK_COMMA:
		case VK_MINUS:
		case VK_PERIOD:
		case VK_SLASH:	
		case VK_SEMICOLON:
		case VK_EQUALS:	
		case VK_0:
		case VK_1:
		case VK_2:
		case VK_3:
		case VK_4:
		case VK_5:
		case VK_6:
		case VK_7:
		case VK_8:
		case VK_9:
		case VK_A:
		case VK_B:
		case VK_C:
		case VK_D:
		case VK_E:
		case VK_F:
		case VK_G:
		case VK_H:
		case VK_I:
		case VK_J:
		case VK_K:
		case VK_L:
		case VK_M:
		case VK_N:
		case VK_O:
		case VK_P:
		case VK_Q:
		case VK_R:
		case VK_S:
		case VK_T:
		case VK_U:
		case VK_V:
		case VK_W:
		case VK_X:
		case VK_Y:
		case VK_Z:
		case VK_OPEN_BRACKET:
		case VK_BACK_SLASH:
		case VK_CLOSE_BRACKET:
			return "" + (char)keyCode;
		case VK_NUMPAD0:
		case VK_NUMPAD1:
		case VK_NUMPAD2:
		case VK_NUMPAD3:
		case VK_NUMPAD4:
		case VK_NUMPAD5:
		case VK_NUMPAD6:
		case VK_NUMPAD7:
		case VK_NUMPAD8:
		case VK_NUMPAD9:
			return "NUMPAD" + (keyCode - VK_NUMPAD0);
		case VK_MULTIPLY:
			return "NUMPAD *";
		case VK_ADD:
			return "NUMPAD +";
		case VK_SEPARATOR:
			return "NUMPAD ,";
		case VK_SUBTRACT:
			return "NUMPAD -";
		case VK_DECIMAL:
			return "NUMPAD .";
		case VK_DIVIDE:
			return "NUMPAD /";
		case VK_DELETE:
			return "DELETE";
		case VK_NUM_LOCK:
			return "NUM LOCK";
		case VK_SCROLL_LOCK:
			return "SCROLL LOCK";
		case VK_F1:
		case VK_F2:
		case VK_F3:
		case VK_F4:
		case VK_F5:
		case VK_F6:
		case VK_F7:
		case VK_F8:
		case VK_F9:
		case VK_F10:
		case VK_F11:
		case VK_F12:
			return "F" + (keyCode - (VK_F1 - 1));
		case VK_F13:
		case VK_F14:
		case VK_F15:
		case VK_F16:
		case VK_F17:
		case VK_F18:
		case VK_F19:
		case VK_F20:
		case VK_F21:
		case VK_F22:
		case VK_F23:
		case VK_F24:
			return "F" + (keyCode - (VK_F13 - 13));
		case VK_PRINTSCREEN:
			return "PRINTSCREEN";
		case VK_INSERT:
			return "INSERT";
		case VK_HELP:
			return "HELP";
		case VK_META:
			return "META";
		case VK_BACK_QUOTE:
			return "BACK QUOTE";
		case VK_QUOTE:
			return "QUOTE";
		case VK_KP_UP:
			return "UP";
		case VK_KP_DOWN:
			return "DOWN";
		case VK_KP_LEFT:
			return "LEFT";
		case VK_KP_RIGHT:
			return "RIGHT";
		case VK_DEAD_GRAVE:
			return "DEAD GRAVE";
		case VK_DEAD_ACUTE:
			return "DEAD ACUTE";
		case VK_DEAD_CIRCUMFLEX:
			return "DEAD CIRCUMFLEX";
		case VK_DEAD_TILDE:
			return "DEAD TILDE";
		case VK_DEAD_MACRON:
			return "DEAD MACRON";
		case VK_DEAD_BREVE:
			return "DEAD BREVE";
		case VK_DEAD_ABOVEDOT:
			return "DEAD ABOVEDOT";
		case VK_DEAD_DIAERESIS:
			return "DEAD DIAERESIS";
		case VK_DEAD_ABOVERING:
			return "DEAD ABOVERING";
		case VK_DEAD_DOUBLEACUTE:
			return "DEAD DOUBLEACUTE";
		case VK_DEAD_CARON:
			return "DEAD CARON";
		case VK_DEAD_CEDILLA:
			return "DEAD CEDILLA";
		case VK_DEAD_OGONEK:
			return "DEAD OGONEK";
		case VK_DEAD_IOTA:
			return "DEAD IOTA";
		case VK_DEAD_VOICED_SOUND:
			return "DEAD VOICED_SOUND";
		case VK_DEAD_SEMIVOICED_SOUND:
			return "DEAD SEMIVOICED_SOUND";
		case VK_AMPERSAND:
			return "AMPERSAND";
		case VK_ASTERISK:
			return "ASTERISK";
		case VK_QUOTEDBL:
			return "QUOTEDBL";
		case VK_LESS:
			return "LESS";
		case VK_GREATER:
			return "GREATER";
		case VK_BRACELEFT:
			return "BRACELEFT";
		case VK_BRACERIGHT:
			return "BRACERIGHT";
		case VK_AT:
			return "AT";
		case VK_COLON:
			return "COLON";
		case VK_CIRCUMFLEX:
			return "COLON";
		case VK_DOLLAR:
			return "DOLLAR";
		case VK_EURO_SIGN:
			return "EURO SIGN";
		case VK_EXCLAMATION_MARK:
			return "EXCLAMATION MARK";
		case VK_INVERTED_EXCLAMATION_MARK:
			return "INVERTED EXCLAMATION MARK";
		case VK_LEFT_PARENTHESIS:
			return "LEFT PARENTHESIS";
		case VK_NUMBER_SIGN:
			return "NUMBER SIGN";
		case VK_PLUS:
			return "PLUS";
		case VK_RIGHT_PARENTHESIS:
			return "RIGHT PARENTHESIS";
		case VK_UNDERSCORE:
			return "UNDERSCORE";
		case VK_FINAL:
			return "FINAL";
		case VK_CONVERT:
			return "CONVERT";
		case VK_NONCONVERT:
			return "NONCONVERT";
		case VK_ACCEPT:
			return "ACCEPT";
		case VK_MODECHANGE:
			return "MODECHANGE";
		case VK_KANA:
			return "KANA";
		case VK_KANJI:
			return "KANJI";
		case VK_ALPHANUMERIC:
			return "ALPHANUMERIC";
		case VK_KATAKANA:
			return "KATAKANA";
		case VK_HIRAGANA:
			return "HIRAGANA";
		case VK_FULL_WIDTH:
			return "FULL WIDTH";
		case VK_HALF_WIDTH:
			return "HALF WIDTH";
		case VK_ROMAN_CHARACTERS:
			return "ROMAN CHARACTERS";
		case VK_ALL_CANDIDATES:
			return "ALL CANDIDATES";
		case VK_PREVIOUS_CANDIDATE:
			return "PREVIOUS CANDIDATE";
		case VK_CODE_INPUT:
			return "CODE INPUT";
		case VK_JAPANESE_KATAKANA:
			return "JAPANESE KATAKANA";
		case VK_JAPANESE_HIRAGANA:
			return "JAPANESE HIRAGANA";
		case VK_JAPANESE_ROMAN:
			return "JAPANESE ROMAN";
		case VK_KANA_LOCK:
			return "KANA LOCK";
		case VK_INPUT_METHOD_ON_OFF:
			return "INPUT METHOD ON OFF";
		case VK_CUT:
			return "CUT";
		case VK_COPY:
			return "COPY";
		case VK_PASTE:
			return "PASTE";
		case VK_UNDO:
			return "UNDO";
		case VK_AGAIN:
			return "AGAIN";
		case VK_FIND:
			return "FIND";
		case VK_PROPS:
			return "PROPS";
		case VK_STOP:
			return "PROPS";
		case VK_COMPOSE:
			return "COMPOSE";
		case VK_ALT_GRAPH:
			return "ALT GRAPH";
		case VK_BEGIN:
			return "BEGIN";
		case VK_CONTEXT_MENU:
			return "CONTEXT MENU";
		case VK_WINDOWS:
			return "WINDOWS";
		default:
			return "UNKNOWN KEYCODE";
		}

	}

}
