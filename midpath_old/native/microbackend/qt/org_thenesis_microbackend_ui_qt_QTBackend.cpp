/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * Copyright (C) Henning Heinold
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
 
#include "org_thenesis_microbackend_ui_qt_QTBackend.h"

static jobject javaQtCanvasObject;
static JavaVM *vm = NULL;

#ifdef QT3_BACKEND
CustomCanvas::CustomCanvas (QWidget *parent, const char *name, int w, int h) : QWidget (parent, name) {
   	setBackgroundMode (NoBackground);
	resize(w, h);
	qimage = new QImage (w, h, 32, QImage::BigEndian);
}
 
void CustomCanvas::paintEvent (QPaintEvent *) {
	/* bitBlt (this, 0, 0, qpixmap); // Qt 3.x only */
   	QPainter p(this);
	p.drawImage(0, 0, *qimage, 0, 0, -1, -1, 0);
}
#else
CustomCanvas::CustomCanvas (QWidget *parent, int w, int h) : QWidget (parent) {
	setBackgroundRole (QPalette::NoRole);
	resize(w, h);
	qimage = new QImage (w, h, QImage::Format_ARGB32);
}
 
void CustomCanvas::paintEvent (QPaintEvent *) {
	/* bitBlt (this, 0, 0, qpixmap); // Qt 3.x only */
   	QPainter p;
	p.begin(this);
	p.drawImage(0, 0, *qimage, 0, 0, -1, -1, 0);
	p.end();
}
#endif

void CustomCanvas::mousePressEvent( QMouseEvent *e ) {
	
	//printf("mousePressEvent/n");			

	JNIEnv *env;
	jclass clazz;
	jmethodID callback;


	if (vm->AttachCurrentThread((void **)&env, NULL) < 0) {
		fprintf (stderr, "%s[%d]: AttachCurrentThread ()\n", __FILE__, __LINE__);
		return;
	}

	clazz = env->GetObjectClass(javaQtCanvasObject);
	callback = env->GetMethodID(clazz, "onMouseButtonEvent", "(III)V");
	if (callback == NULL) {
		fprintf (stderr, "%s[%d]: GetMethodID ()\n", __FILE__, __LINE__);
		return;
	}

	env->CallVoidMethod(javaQtCanvasObject, callback, e->x(), e->y(), 1);

	/*if ((*vm)->DetachCurrentThread(vm) < 0) {
	  fprintf (stderr, "%s[%d]: DetachCurrentThread ()\n", __FILE__, __LINE__);
	  }
	*/

}
	
void CustomCanvas::mouseReleaseEvent( QMouseEvent *e ) {
		
	//printf("mouseReleaseEvent/n");

	JNIEnv *env;
	jclass clazz;
	jmethodID callback;

	/*printf("Button pressed\n");*/

	if (vm->AttachCurrentThread((void **)&env, NULL) < 0) {
		fprintf (stderr, "%s[%d]: AttachCurrentThread ()\n", __FILE__, __LINE__);
		return;
	}

	clazz = env->GetObjectClass(javaQtCanvasObject);
	callback = env->GetMethodID(clazz, "onMouseButtonEvent", "(III)V");
	if (callback == NULL) {
		fprintf (stderr, "%s[%d]: GetMethodID ()\n", __FILE__, __LINE__);
		return;
	}

	env->CallVoidMethod(javaQtCanvasObject, callback, e->x(), e->y(), 0);

	/*if ((*vm)->DetachCurrentThread(vm) < 0) {
	  fprintf (stderr, "%s[%d]: DetachCurrentThread ()\n", __FILE__, __LINE__);
	  }
	*/
}
	
void CustomCanvas::mouseMoveEvent(QMouseEvent *e ) {
		
	//printf("mouseMoveEvent/n");

	JNIEnv *env;
	jclass clazz;
	jmethodID callback;

	/*printf("Button pressed\n");*/

	if (vm->AttachCurrentThread((void **)&env, NULL) < 0) {
		fprintf (stderr, "%s[%d]: AttachCurrentThread ()\n", __FILE__, __LINE__);
		return;
	}

	clazz = env->GetObjectClass(javaQtCanvasObject);
	callback = env->GetMethodID(clazz, "onMouseMoveEvent", "(II)V");
	if (callback == NULL) {
		fprintf (stderr, "%s[%d]: GetMethodID ()\n", __FILE__, __LINE__);
		return;
	}

	env->CallVoidMethod(javaQtCanvasObject, callback, e->x(), e->y());

	/*if ((*vm)->DetachCurrentThread(vm) < 0) {
	  fprintf (stderr, "%s[%d]: DetachCurrentThread ()\n", __FILE__, __LINE__);
	  }
	*/
}

enum { 
	KEY_FIRST = 400, 
	KEY_LAST = 402, 
	KEY_TYPED = 400, 
	KEY_PRESSED = 401, 
	KEY_RELEASED = 402,
	VK_ENTER = '\n', 
	VK_BACK_SPACE = '\b', 
	VK_TAB = '\t', 
	VK_CANCEL = 3, 
	VK_CLEAR = 12, 
	VK_SHIFT = 16,
	VK_CONTROL = 17, 
	VK_ALT = 18,
	VK_PAUSE = 19,
	VK_CAPS_LOCK = 20,
	VK_ESCAPE = 27,
	VK_SPACE = ' ',
	VK_PAGE_UP = 33,
	VK_PAGE_DOWN = 34,
	VK_END = 35, 
	VK_HOME = 36, 
	VK_LEFT = 37,
	VK_UP = 38,
	VK_RIGHT = 39,
	VK_DOWN = 40,
	VK_COMMA = ',',
	VK_MINUS = '-',
	VK_PERIOD = '.', 
	VK_SLASH = '/',
	VK_0 = '0', 
	VK_1 = '1',
	VK_2 = '2',
	VK_3 = '3',
	VK_4 = '4',
	VK_5 = '5',
	VK_6 = '6',
	VK_7 = '7',
	VK_8 = '8',
	VK_9 = '9',
	VK_SEMICOLON = ',',
	VK_EQUALS = '=',
	VK_A = 'A',
	VK_B = 'B',
	VK_C = 'C',
	VK_D = 'D',
	VK_E = 'E',
	VK_F = 'F',
	VK_G = 'G',
	VK_H = 'H',
	VK_I = 'I',
	VK_J = 'J',
	VK_K = 'K',
	VK_L = 'L',
	VK_M = 'M',
	VK_N = 'N',
	VK_O = 'O',
	VK_P = 'P',
	VK_Q = 'Q',
	VK_R = 'R',
	VK_S = 'S',
	VK_T = 'T',
	VK_U = 'U',
	VK_V = 'V',
	VK_W = 'W',
	VK_X = 'X', 
	VK_Y = 'Y',
	VK_Z = 'Z',
	VK_OPEN_BRACKET = '[',
	VK_BACK_SLASH = '\\', 
	VK_CLOSE_BRACKET = ']',
	VK_NUMPAD0 = 96,
	VK_NUMPAD1 = 97,
	VK_NUMPAD2 = 98,
	VK_NUMPAD3 = 99,
	VK_NUMPAD4 = 100,
	VK_NUMPAD5 = 101,
	VK_NUMPAD6 = 102,
	VK_NUMPAD7 = 103,
	VK_NUMPAD8 = 104,
	VK_NUMPAD9 = 105,
	VK_MULTIPLY = 106,
	VK_ADD = 107,
	VK_SEPARATER = 108,
	VK_SEPARATOR = 108,
	VK_SUBTRACT = 109,
	VK_DECIMAL = 110,
	VK_DIVIDE = 111,
	VK_DELETE = 127,
	VK_NUM_LOCK = 144,
	VK_SCROLL_LOCK = 145,
	VK_F1 = 112,
	VK_F2 = 113,
	VK_F3 = 114,
	VK_F4 = 115,
	VK_F5 = 116,
	VK_F6 = 117,
	VK_F7 = 118,
	VK_F8 = 119,
	VK_F9 = 120,
	VK_F10 = 121,
	VK_F11 = 122,
	VK_F12 = 123,
	VK_F13 = 61440,
	VK_F14 = 61441,
	VK_F15 = 61442,
	VK_F16 = 61443,
	VK_F17 = 61444,
	VK_F18 = 61445,
	VK_F19 = 61446,
	VK_F20 = 61447,
	VK_F21 = 61448,
	VK_F22 = 61449,
	VK_F23 = 61450,
	VK_F24 = 61451,
	VK_PRINTSCREEN = 154,
	VK_INSERT = 155,
	VK_HELP = 156,
	VK_META = 157,
	VK_BACK_QUOTE = 192,
	VK_QUOTE = 222,
	VK_KP_UP = 224,
	VK_KP_DOWN = 225,
	VK_KP_LEFT = 226,
	VK_KP_RIGHT = 227,
	VK_DEAD_GRAVE = 128,
	VK_DEAD_ACUTE = 129,
	VK_DEAD_CIRCUMFLEX = 130,
	VK_DEAD_TILDE = 131,
	VK_DEAD_MACRON = 132,
	VK_DEAD_BREVE = 133,
	VK_DEAD_ABOVEDOT = 134,
	VK_DEAD_DIAERESIS = 135,
	VK_DEAD_ABOVERING = 136,
	VK_DEAD_DOUBLEACUTE = 137,
	VK_DEAD_CARON = 138,
	VK_DEAD_CEDILLA = 139,
	VK_DEAD_OGONEK = 140,
	VK_DEAD_IOTA = 141,
	VK_DEAD_VOICED_SOUND = 142,
	VK_DEAD_SEMIVOICED_SOUND = 143,
	VK_AMPERSAND = 150,
	VK_ASTERISK = 151,
	VK_QUOTEDBL = 152,
	VK_LESS = 153,
	VK_GREATER = 160,
	VK_BRACELEFT = 161,
	VK_BRACERIGHT = 162,
	VK_AT = 512, VK_COLON = 513,
	VK_CIRCUMFLEX = 514,
	VK_DOLLAR = 515,
	VK_EURO_SIGN = 516,
	VK_EXCLAMATION_MARK = 517,
	VK_INVERTED_EXCLAMATION_MARK = 518,
	VK_LEFT_PARENTHESIS = 519,
	VK_NUMBER_SIGN = 520,
	VK_PLUS = 521,
	VK_RIGHT_PARENTHESIS = 522,
	VK_UNDERSCORE = 523,
	VK_FINAL = 24,
	VK_CONVERT = 28,
	VK_NONCONVERT = 29,
	VK_ACCEPT = 30,
	VK_MODECHANGE = 31,
	VK_KANA = 21,
	VK_KANJI = 25,
	VK_ALPHANUMERIC = 240,
	VK_KATAKANA = 241,
	VK_HIRAGANA = 242,
	VK_FULL_WIDTH = 243,
	VK_HALF_WIDTH = 244,
	VK_ROMAN_CHARACTERS = 245,
	VK_ALL_CANDIDATES = 256,
	VK_PREVIOUS_CANDIDATE = 257,
	VK_CODE_INPUT = 258,
	VK_JAPANESE_KATAKANA = 259,
	VK_JAPANESE_HIRAGANA = 260,
	VK_JAPANESE_ROMAN = 261,
	VK_KANA_LOCK = 262,
	VK_INPUT_METHOD_ON_OFF = 263,
	VK_COMPOSE = 65312,
	VK_BEGIN = 65368,
	VK_ALT_GRAPH = 65406,
	VK_STOP = 65480,
	VK_AGAIN = 65481,
	VK_PROPS = 65482,
	VK_UNDO = 65483,
	VK_COPY = 65485,
	VK_PASTE = 65487,
	VK_FIND = 65488,
	VK_CUT = 65489,
	VK_WINDOWS = 524,
	VK_CONTEXT_MENU = 525,
	VK_UNDEFINED = 0
};

static int convertKey(int key) {
	switch (key) {
		case Qt::Key_Escape:
			return VK_ESCAPE;
		case Qt::Key_Tab:
			return VK_TAB;
		case Qt::Key_Backtab:
			return VK_UNDEFINED;
		case Qt::Key_Backspace:
			return VK_BACK_SPACE;
		case Qt::Key_Return:
		case Qt::Key_Enter:
			return VK_ENTER;
		case Qt::Key_Insert:
			return VK_INSERT;
		case Qt::Key_Delete:
			return VK_DELETE;
		case Qt::Key_Pause:
			return VK_PAUSE;
		case Qt::Key_Print:
			return VK_PRINTSCREEN;
		case Qt::Key_SysReq:
			return VK_UNDEFINED;
		case Qt::Key_Clear:
			return VK_CLEAR;
		case Qt::Key_Home:
			return VK_HOME;
		case Qt::Key_End:
			return VK_END;
		case Qt::Key_Left:
			return VK_LEFT;
		case Qt::Key_Up:
			return VK_UP;
		case Qt::Key_Right:
			return VK_RIGHT;
		case Qt::Key_Down:
			return VK_DOWN;
		case Qt::Key_PageUp:
			return VK_PAGE_UP;
		case Qt::Key_PageDown:
			return VK_PAGE_DOWN;
		case Qt::Key_Shift:
			return VK_SHIFT;
		case Qt::Key_Control:
			return VK_CONTROL;
		case Qt::Key_Meta:
			return VK_META;
		case Qt::Key_Alt:
			return VK_ALT;
		case Qt::Key_CapsLock:
			return VK_CAPS_LOCK;
		case Qt::Key_NumLock:
			return VK_NUM_LOCK;
		case Qt::Key_ScrollLock:
			return VK_SCROLL_LOCK;
		case Qt::Key_F1:
			return VK_F1;
		case Qt::Key_F2:
			return VK_F2;
		case Qt::Key_F3:
			return VK_F3;
		case Qt::Key_F4:
			return VK_F4;
		case Qt::Key_F5:
			return VK_F5;
		case Qt::Key_F6:
			return VK_F6;
		case Qt::Key_F7:
			return VK_F7;
		case Qt::Key_F8:
			return VK_F8;
		case Qt::Key_F9:
			return VK_F9;
		case Qt::Key_F10:
			return VK_F10;
		case Qt::Key_F11:
			return VK_F11;
		case Qt::Key_F12:
			return VK_F12;
		case Qt::Key_F13:
			return VK_F13;
		case Qt::Key_F14:
			return VK_F14;
		case Qt::Key_F15:
			return VK_F15;
		case Qt::Key_F16:
			return VK_F16;
		case Qt::Key_F17:
			return VK_F17;
		case Qt::Key_F18:
			return VK_F18;
		case Qt::Key_F19:
			return VK_F19;
		case Qt::Key_F20:
			return VK_F20;
		case Qt::Key_F21:
			return VK_F21;
		case Qt::Key_F22:
			return VK_F22;
		case Qt::Key_F23:
			return VK_F23;
		case Qt::Key_F24:
			return VK_F24;
		case Qt::Key_F25:
		case Qt::Key_F26:
		case Qt::Key_F27:
		case Qt::Key_F28:
		case Qt::Key_F29:
		case Qt::Key_F30:
		case Qt::Key_F31:
		case Qt::Key_F32:
		case Qt::Key_F33:
		case Qt::Key_F34:
		case Qt::Key_F35:
		case Qt::Key_Super_L:
		case Qt::Key_Super_R:
			return VK_WINDOWS;
		case Qt::Key_Menu:
		case Qt::Key_Hyper_L:
		case Qt::Key_Hyper_R:
			return VK_UNDEFINED;
		case Qt::Key_Help:
			return VK_HELP;
		case Qt::Key_Direction_L:
		case Qt::Key_Direction_R:
			return VK_UNDEFINED;
		case Qt::Key_Space:
			return VK_SPACE;
		case Qt::Key_QuoteDbl:
			return VK_QUOTEDBL;
		case Qt::Key_NumberSign:
			return VK_NUMBER_SIGN;
		case Qt::Key_Dollar:
			return VK_DOLLAR;
		case Qt::Key_Percent:
			return VK_DOLLAR;
		case Qt::Key_Ampersand:
			return VK_AMPERSAND;
		case Qt::Key_Apostrophe:
			return VK_UNDEFINED;
		case Qt::Key_ParenLeft:
			return VK_LEFT_PARENTHESIS;
		case Qt::Key_ParenRight:
			return VK_RIGHT_PARENTHESIS;
		case Qt::Key_Asterisk:
			return VK_ASTERISK;
		case Qt::Key_Plus:
			return VK_PLUS;
		case Qt::Key_Comma:
			return VK_COMMA;
		case Qt::Key_Minus:
			return VK_MINUS;
		case Qt::Key_Period:
			return VK_PERIOD;
		case Qt::Key_Slash:
			return VK_SLASH;
		case Qt::Key_0:
			return VK_0;
		case Qt::Key_1:
			return VK_1;
		case Qt::Key_2:
			return VK_2;
		case Qt::Key_3:
			return VK_3;
		case Qt::Key_4:
			return VK_4;
		case Qt::Key_5:
			return VK_5;
		case Qt::Key_6:
			return VK_6;
		case Qt::Key_7:
			return VK_7;
		case Qt::Key_8:
			return VK_8;
		case Qt::Key_9:
			return VK_9;
		case Qt::Key_Colon:
			return VK_COLON;
		case Qt::Key_Semicolon:
			return VK_SEMICOLON;
		case Qt::Key_Less:
			return VK_LESS;
		case Qt::Key_Equal:
			return VK_EQUALS;
		case Qt::Key_Greater:
			return VK_GREATER;
		case Qt::Key_Question:
			return VK_UNDEFINED;
		case Qt::Key_At:
			return VK_AT;
		case Qt::Key_A:
			return VK_A;
		case Qt::Key_B:
			return VK_B;
		case Qt::Key_C:
			return VK_C;
		case Qt::Key_D:
			return VK_D;
		case Qt::Key_E:
			return VK_E;
		case Qt::Key_F:
			return VK_F;
		case Qt::Key_G:
			return VK_G;
		case Qt::Key_H:
			return VK_H;
		case Qt::Key_I:
			return VK_I;
		case Qt::Key_J:
			return VK_J;
		case Qt::Key_K:
			return VK_K;
		case Qt::Key_L:
			return VK_L;
		case Qt::Key_M:
			return VK_M;
		case Qt::Key_N:
			return VK_N;
		case Qt::Key_O:
			return VK_O;
		case Qt::Key_P:
			return VK_P;
		case Qt::Key_Q:
			return VK_Q;
		case Qt::Key_R:
			return VK_R;
		case Qt::Key_S:
			return VK_S;
		case Qt::Key_T:
			return VK_T;
		case Qt::Key_U:
			return VK_U;
		case Qt::Key_V:
			return VK_V;
		case Qt::Key_W:
			return VK_W;
		case Qt::Key_X:
			return VK_X;
		case Qt::Key_Y:
			return VK_Y;
		case Qt::Key_Z:
			return VK_Z;
		case Qt::Key_BracketLeft:
			return VK_OPEN_BRACKET;
		case Qt::Key_Backslash:
			return VK_BACK_SLASH;
		case Qt::Key_BracketRight:
			return VK_CLOSE_BRACKET;
		case Qt::Key_AsciiCircum:
			return VK_CIRCUMFLEX;
		case Qt::Key_Underscore:
			return VK_UNDERSCORE;
		case Qt::Key_QuoteLeft:
			return VK_QUOTE;
		case Qt::Key_BraceLeft:
			return VK_BRACELEFT;
		case Qt::Key_Bar:
			return VK_UNDEFINED;
		case Qt::Key_BraceRight:
			return VK_BRACERIGHT;
		case Qt::Key_AsciiTilde:
			return VK_DEAD_TILDE;
		case Qt::Key_nobreakspace:
			return VK_UNDEFINED;
		case Qt::Key_exclamdown:
			return VK_EXCLAMATION_MARK;
		case Qt::Key_cent:
		case Qt::Key_sterling:
		case Qt::Key_currency:
		case Qt::Key_yen:
		case Qt::Key_brokenbar:
		case Qt::Key_section:
		case Qt::Key_diaeresis:
		case Qt::Key_copyright:
		case Qt::Key_ordfeminine:
		case Qt::Key_guillemotleft:
		case Qt::Key_notsign:
		case Qt::Key_hyphen:
		case Qt::Key_registered:
		case Qt::Key_macron:
		case Qt::Key_degree:
		case Qt::Key_plusminus:
		case Qt::Key_twosuperior:
		case Qt::Key_threesuperior:
		case Qt::Key_acute:
		case Qt::Key_mu:
		case Qt::Key_paragraph:
		case Qt::Key_periodcentered:
		case Qt::Key_cedilla:
		case Qt::Key_onesuperior:
		case Qt::Key_masculine:
		case Qt::Key_guillemotright:
		case Qt::Key_onequarter:
		case Qt::Key_onehalf:
		case Qt::Key_threequarters:;
		case Qt::Key_questiondown:
		case Qt::Key_Agrave:
		case Qt::Key_Aacute:
		case Qt::Key_Acircumflex:
		case Qt::Key_Atilde:
		case Qt::Key_Adiaeresis:
		case Qt::Key_Aring:
		case Qt::Key_AE:
		case Qt::Key_Ccedilla:
		case Qt::Key_Egrave:
		case Qt::Key_Eacute:
		case Qt::Key_Ecircumflex:
		case Qt::Key_Ediaeresis:
		case Qt::Key_Igrave:
		case Qt::Key_Iacute:
		case Qt::Key_Icircumflex:
		case Qt::Key_Idiaeresis:
		case Qt::Key_ETH:
		case Qt::Key_Ntilde:
		case Qt::Key_Ograve:
		case Qt::Key_Oacute:
		case Qt::Key_Ocircumflex:
		case Qt::Key_Otilde:
		case Qt::Key_Odiaeresis:
			return VK_UNDEFINED;
		case Qt::Key_multiply:
			return VK_MULTIPLY;
		case Qt::Key_Ooblique:
		case Qt::Key_Ugrave:
		case Qt::Key_Uacute:
		case Qt::Key_Ucircumflex:
		case Qt::Key_Udiaeresis:
		case Qt::Key_Yacute:
		case Qt::Key_THORN:
		case Qt::Key_ssharp:
			return VK_UNDEFINED;
		case Qt::Key_division:
			return VK_DIVIDE;
		case Qt::Key_ydiaeresis:
		case Qt::Key_Back:
		case Qt::Key_Forward:
		case Qt::Key_Stop:
		case Qt::Key_Refresh:
		case Qt::Key_VolumeDown:
		case Qt::Key_VolumeMute:
		case Qt::Key_VolumeUp:
		case Qt::Key_BassBoost:
		case Qt::Key_BassUp:
		case Qt::Key_BassDown:
		case Qt::Key_TrebleUp:
		case Qt::Key_TrebleDown:
		case Qt::Key_MediaPlay:
		case Qt::Key_MediaStop:
			//case Qt::Key_MediaPrevious:
		case Qt::Key_MediaNext:
		case Qt::Key_MediaRecord:
		case Qt::Key_HomePage:
		case Qt::Key_Favorites:
		case Qt::Key_Search:
		case Qt::Key_Standby:
		case Qt::Key_OpenUrl:
		case Qt::Key_LaunchMail:
		case Qt::Key_LaunchMedia:
		case Qt::Key_Launch0:
		case Qt::Key_Launch1:
		case Qt::Key_Launch2:
		case Qt::Key_Launch3:
		case Qt::Key_Launch4:
		case Qt::Key_Launch5:
		case Qt::Key_Launch6:
		case Qt::Key_Launch7:
		case Qt::Key_Launch8:
		case Qt::Key_Launch9:
		case Qt::Key_LaunchA:
		case Qt::Key_LaunchB:
		case Qt::Key_LaunchC:
		case Qt::Key_LaunchD:
		case Qt::Key_LaunchE:
		case Qt::Key_LaunchF:
		case Qt::Key_MediaLast:
		case Qt::Key_unknown:
			return VK_UNDEFINED;
		default:
			return VK_UNDEFINED;
	}
}

void CustomCanvas::keyPressEvent(QKeyEvent *e) {

	//printf("keyPressEvent/n");

	JNIEnv *env;
	jclass clazz;
	jmethodID callback;

	/*printf("Button pressed\n");*/

	if (vm->AttachCurrentThread((void **)&env, NULL) < 0) {
		fprintf (stderr, "%s[%d]: AttachCurrentThread ()\n", __FILE__, __LINE__);
		return;
	}

	clazz = env->GetObjectClass(javaQtCanvasObject);
	callback = env->GetMethodID(clazz, "onKeyEvent", "(III)V");
	if (callback == NULL) {
		fprintf (stderr, "%s[%d]: GetMethodID ()\n", __FILE__, __LINE__);
		return;
	}

	env->CallVoidMethod(javaQtCanvasObject, callback, 1, 
			    convertKey(e->key()),
			    (int)(e->text().unicode()->unicode()));

	/*if ((*vm)->DetachCurrentThread(vm) < 0) {
	  fprintf (stderr, "%s[%d]: DetachCurrentThread ()\n", __FILE__, __LINE__);
	  }*/
}

void CustomCanvas::keyReleaseEvent(QKeyEvent *e) {
	
	//printf("keyReleaseEvent/n");

	JNIEnv *env;
	jclass clazz;
	jmethodID callback;

	/*printf("Button pressed\n");*/

	if (vm->AttachCurrentThread((void **)&env, NULL) < 0) {
		fprintf (stderr, "%s[%d]: AttachCurrentThread ()\n", __FILE__, __LINE__);
		return;
	}

	clazz = env->GetObjectClass(javaQtCanvasObject);
	callback = env->GetMethodID(clazz, "onKeyEvent", "(III)V");
	if (callback == NULL) {
		fprintf (stderr, "%s[%d]: GetMethodID ()\n", __FILE__, __LINE__);
		return;
	}

	env->CallVoidMethod(javaQtCanvasObject, callback, 0, 
			    convertKey(e->key()),
			    (int)(e->text().unicode()->unicode()));

	/*if ((*vm)->DetachCurrentThread(vm) < 0) {
	  fprintf (stderr, "%s[%d]: DetachCurrentThread ()\n", __FILE__, __LINE__);
	  }
	*/
} 

void CustomCanvas::closeEvent(QCloseEvent * e) {
	
	JNIEnv *env;
	jclass clazz;
	jmethodID callback;

	if (vm->AttachCurrentThread((void **)&env, NULL) < 0) {
		fprintf (stderr, "%s[%d]: AttachCurrentThread ()\n", __FILE__, __LINE__);
		return;
	}

	clazz = env->GetObjectClass(javaQtCanvasObject);
	callback = env->GetMethodID(clazz, "onCloseEvent", "()V");
	if (callback == NULL) {
		fprintf (stderr, "%s[%d]: GetMethodID ()\n", __FILE__, __LINE__);
		return;
	}

	env->CallVoidMethod(javaQtCanvasObject, callback);

	/*if ((*vm)->DetachCurrentThread(vm) < 0) {
	  fprintf (stderr, "%s[%d]: DetachCurrentThread ()\n", __FILE__, __LINE__);
	  }
	*/

	// Finally, accept to close the widget
	e->accept();
}

static jint imageWidth = 0;
static jint imageHeight = 0;
static CustomCanvas *nativeQtCanvas;
static int s_argc = 1;

/*
 * Class:     org_thenesis_microbackend_ui_qt_QTBackend
 * Method:    initialize
 * Signature: (II)I
 */
JNIEXPORT jboolean JNICALL Java_org_thenesis_microbackend_ui_qt_QTBackend_initialize(JNIEnv * env, jobject obj, jint width, jint height) {

	if (env->GetJavaVM (&vm) < 0)
		return JNI_FALSE;
	javaQtCanvasObject = env->NewGlobalRef(obj);
	if (javaQtCanvasObject == NULL)
		return JNI_FALSE;

	/* Create image buffer */
	imageWidth = width;
	imageHeight = height;

	/* Initialize QT */
	char s_java[5] = "java";
	char **argv = (char**) malloc((s_argc + 1) * sizeof(char *));

	if (argv == NULL) env->ThrowNew(env->FindClass("java/lang/OutOfMemoryError"), "malloc");
	argv[0] = s_java;
	argv[s_argc] = NULL;

#ifdef QTOPIA4_BACKEND
	if (!qApp){ (void)new QtopiaApplication(s_argc, argv);}
#else
	if (!qApp){ (void)new QApplication(s_argc, argv, TRUE);}
#endif
#ifdef QT3_BACKEND
	nativeQtCanvas = new CustomCanvas (NULL, "Canvas", imageWidth, imageHeight);
#else
	nativeQtCanvas = new CustomCanvas (NULL, imageWidth, imageHeight);
#endif
	nativeQtCanvas->setMouseTracking(TRUE);
	nativeQtCanvas->show();

	return JNI_TRUE;
}

/*
 * Class:     org_thenesis_microbackend_ui_qt_QTBackend
 * Method:    writeARGB
 * Signature: ([IIIII)V
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_qt_QTBackend_writeARGB(JNIEnv * env, jobject obj, jintArray intBuffer, jint x_src, jint y_src, jint width, jint height) {
	
	QRgb *srcBuffer;
	jint *jarr = env->GetIntArrayElements(intBuffer, 0);
	srcBuffer = (QRgb*)jarr;

	int src_offset = y_src * imageWidth + x_src;
	int src_pos = 0;

	int x, y;
	for (y = 0; y < height; y++) {
		src_pos = src_offset + y * imageWidth;
		QRgb *destBuffer =
			(QRgb*)nativeQtCanvas->qimage->scanLine(y_src + y) + x_src;
		for (x = 0; x < width; x++) {
			//printf("%x\n", *srcBuffer); 
			destBuffer[x] = srcBuffer[src_pos + x];
		}
	}

	// Release the array and clean context
	env->ReleaseIntArrayElements(intBuffer, jarr, 0);

	// Request an update of the canvas
	nativeQtCanvas->update();
}

/*
 * Class:     org_thenesis_microbackend_ui_qt_QTBackend
 * Method:    startMainLoop
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_thenesis_microbackend_ui_qt_QTBackend_startMainLoop(JNIEnv * env, jobject obj) {
	 /* enter the QT main loop */
  	return qApp->exec();
}


/*
 * Class:     org_thenesis_microbackend_ui_qt_QTBackend
 * Method:    destroy
 * Signature: ()I
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_qt_QTBackend_quit(JNIEnv * env, jobject obj) {
    qApp->quit();
}
