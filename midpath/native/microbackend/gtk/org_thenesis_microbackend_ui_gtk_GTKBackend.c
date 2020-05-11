/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
 * 
 * Copyright (C) Sebastian Mancke
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
 
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <stdbool.h>
#include <gtk/gtk.h>
#include <gdk/gdkkeysyms.h>

#include "midpath_ui_peer.h"
#include "midpath_ui_backend.h"
#include "org_thenesis_microbackend_ui_gtk_GTKBackend.h"

/* References: 
 * - http://developer.gnome.org/doc/API/gdk/gdk-gdkrgb.html 
 * - http://www.linux-france.org/article/devl/gtk/gtk_tut-2.html
 * - http://developer.gnome.org/doc/API/
 */

static GtkWindow *window;
static GtkWidget *darea;

/* jint isMainLoopStarted = FALSE; */
static jint imageWidth = 0;
static jint imageHeight = 0;
static guchar *rgbBuffer; 

jobject gtkBackendObject;
JavaVM *vm = NULL;

enum { KEY_FIRST = 400, KEY_LAST = 402, KEY_TYPED = 400, KEY_PRESSED = 401, KEY_RELEASED = 402,
			VK_ENTER = '\n', VK_BACK_SPACE = '\b', VK_TAB = '\t', VK_CANCEL = 3, VK_CLEAR = 12, VK_SHIFT = 16,
			VK_CONTROL = 17, VK_ALT = 18, VK_PAUSE = 19, VK_CAPS_LOCK = 20, VK_ESCAPE = 27, VK_SPACE = ' ',
			VK_PAGE_UP = 33, VK_PAGE_DOWN = 34, VK_END = 35, VK_HOME = 36, VK_LEFT = 37, VK_UP = 38, VK_RIGHT = 39,
			VK_DOWN = 40, VK_COMMA = ',', VK_MINUS = '-', VK_PERIOD = '.', VK_SLASH = '/', VK_0 = '0', VK_1 = '1',
			VK_2 = '2', VK_3 = '3', VK_4 = '4', VK_5 = '5', VK_6 = '6', VK_7 = '7', VK_8 = '8', VK_9 = '9',
			VK_SEMICOLON = ',', VK_EQUALS = '=', VK_A = 'A', VK_B = 'B', VK_C = 'C', VK_D = 'D', VK_E = 'E',
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
			VK_ALT_GRAPH = 65406, VK_BEGIN = 65368, VK_CONTEXT_MENU = 525, VK_WINDOWS = 524, VK_UNDEFINED = 0 };


static void main_loop_started();

static int convertKeyCode(int keyCode) {

		switch (keyCode) {
		case GDK_VoidSymbol:
			return VK_UNDEFINED;
		case GDK_BackSpace:
			return VK_BACK_SPACE;
		case GDK_Tab:
			return VK_TAB;
		case GDK_Linefeed:
			return VK_UNDEFINED;
		case GDK_Clear:
			return VK_CLEAR;
		case GDK_Return:
			return VK_ENTER;
		case GDK_Pause:
			return VK_PAUSE;
		case GDK_Scroll_Lock:
			return VK_SCROLL_LOCK;
		case GDK_Sys_Req:
			return VK_UNDEFINED;
		case GDK_Escape:
			return VK_ESCAPE;
		case GDK_Delete:
			return VK_DELETE;
		case GDK_Home:
			return VK_HOME;
		case GDK_Left:
			return VK_LEFT;
		case GDK_Up:
			return VK_UP;
		case GDK_Right:
			return VK_RIGHT;
		case GDK_Down:
			return VK_DOWN;
		case GDK_Page_Up:
			return VK_PAGE_UP;
		case GDK_Page_Down:
			return VK_PAGE_DOWN;
		case GDK_End:
			return VK_END;
		case GDK_Begin:
			return VK_BEGIN;
		case GDK_Select:
			return VK_UNDEFINED;
		case GDK_Print:
			return VK_PRINTSCREEN;
		case GDK_Execute:
			return VK_UNDEFINED;
		case GDK_Insert:
			return VK_INSERT;
		case GDK_Undo:
			return VK_UNDO;
		case GDK_Redo:
			return VK_UNDEFINED;
		case GDK_Menu:
			return VK_UNDEFINED;
		case GDK_Find:
			return VK_FIND;
		case GDK_Cancel:
			return VK_CANCEL;
		case GDK_Help:
			return VK_HELP;
		case GDK_Break:
			return VK_UNDEFINED;
		case GDK_Mode_switch:
			return VK_MODECHANGE;
		case GDK_Num_Lock:
			return VK_NUM_LOCK;
		case GDK_KP_Space:
			return VK_SPACE;
		case GDK_KP_Tab:
			return VK_TAB;
		case GDK_KP_Enter:
			return VK_ENTER;
		case GDK_KP_F1:
			return VK_F1;
		case GDK_KP_F2:
			return VK_F2;
		case GDK_KP_F3:
			return VK_F3;
		case GDK_KP_F4:
			return VK_F4;
		case GDK_KP_Home:
			return VK_HOME;
		case GDK_KP_Left:
			return VK_KP_LEFT;
		case GDK_KP_Up:
			return VK_KP_UP;
		case GDK_KP_Right:
			return VK_KP_RIGHT;
		case GDK_KP_Down:
			return VK_KP_DOWN;
		case GDK_KP_Page_Up:
			return VK_PAGE_UP;
		case GDK_KP_Page_Down:
			return VK_PAGE_DOWN;
		case GDK_KP_End:
			return VK_END;
		case GDK_KP_Begin:
			return VK_BEGIN;
		case GDK_KP_Insert:
			return VK_INSERT;
		case GDK_KP_Delete:
			return VK_DELETE;
		case GDK_KP_Equal:
			return VK_EQUALS;
		case GDK_KP_Multiply:
			return VK_MULTIPLY;
		case GDK_KP_Add:
			return VK_ADD;
		case GDK_KP_Separator:
			return VK_SEPARATOR;
		case GDK_KP_Subtract:
			return VK_SUBTRACT;
		case GDK_KP_Decimal:
			return VK_DECIMAL;
		case GDK_KP_Divide:
			return VK_DIVIDE;
		case GDK_KP_0:
			return VK_0;
		case GDK_KP_1:
			return VK_1;
		case GDK_KP_2:
			return VK_2;
		case GDK_KP_3:
			return VK_3;
		case GDK_KP_4:
			return VK_4;
		case GDK_KP_5:
			return VK_5;
		case GDK_KP_6:
			return VK_6;
		case GDK_KP_7:
			return VK_7;
		case GDK_KP_8:
			return VK_8;
		case GDK_KP_9:
			return VK_9;
		case GDK_F1:
			return VK_F1;
		case GDK_F2:
			return VK_F2;
		case GDK_F3:
			return VK_F3;
		case GDK_F4:
			return VK_F4;
		case GDK_F5:
			return VK_F5;
		case GDK_F6:
			return VK_F6;
		case GDK_F7:
			return VK_F7;
		case GDK_F8:
			return VK_F8;
		case GDK_F9:
			return VK_F9;
		case GDK_F10:
			return VK_F10;
		case GDK_F11:
			return VK_F11;
		case GDK_F12:
			return VK_F12;
		case GDK_F13:
			return VK_F13;
		case GDK_F14:
			return VK_F14;
		case GDK_F15:
			return VK_F15;
		case GDK_F16:
			return VK_F16;
		case GDK_F17:
			return VK_F17;
		case GDK_F18:
			return VK_F18;
		case GDK_F19:
			return VK_F19;
		case GDK_F20:
			return VK_F20;
		case GDK_F21:
			return VK_F21;
		case GDK_F22:
			return VK_F22;
		case GDK_F23:
			return VK_F23;
		case GDK_F24:
			return VK_F24;
		case GDK_Shift_L:
		case GDK_Shift_R:
			return VK_SHIFT;
		case GDK_Control_L:
		case GDK_Control_R:
			return VK_CONTROL;
		case GDK_Caps_Lock:
		case GDK_Shift_Lock:
			return VK_CAPS_LOCK;
		case GDK_Meta_L:
		case GDK_Meta_R:
			return VK_META;
		case GDK_Alt_L:
		case GDK_Alt_R:
			return VK_ALT;
		case GDK_Super_L:
		case GDK_Super_R:
		case GDK_Hyper_L:
		case GDK_Hyper_R:
			return VK_UNDEFINED;
		case GDK_dead_grave:
			return VK_DEAD_GRAVE;
		case GDK_dead_acute:
			return VK_DEAD_ACUTE;
		case GDK_dead_circumflex:
			return VK_DEAD_CIRCUMFLEX;
		case GDK_dead_tilde:
			return VK_DEAD_TILDE;
		case GDK_dead_macron:
			return VK_DEAD_MACRON;
		case GDK_dead_breve:
			return VK_DEAD_BREVE;
		case GDK_dead_abovedot:
			return VK_DEAD_ABOVEDOT;
		case GDK_dead_diaeresis:
			return VK_DEAD_DIAERESIS;
		case GDK_dead_abovering:
			return VK_DEAD_ABOVERING;
		case GDK_dead_doubleacute:
			return VK_DEAD_DOUBLEACUTE;
		case GDK_dead_caron:
			return VK_DEAD_CARON;
		case GDK_dead_cedilla:
			return VK_DEAD_CEDILLA;
		case GDK_dead_ogonek:
			return VK_DEAD_OGONEK;
		case GDK_dead_iota:
			return VK_DEAD_IOTA;
		case GDK_dead_voiced_sound:
			return VK_DEAD_VOICED_SOUND;
		case GDK_dead_semivoiced_sound:
			return VK_DEAD_SEMIVOICED_SOUND;
		case GDK_dead_belowdot:
			return VK_UNDEFINED;
		case GDK_space:
			return VK_SPACE;
		case GDK_exclam:
			return VK_EXCLAMATION_MARK;
		case GDK_quotedbl:
			return VK_QUOTEDBL;
		case GDK_numbersign:
			return VK_NUMBER_SIGN;
		case GDK_dollar:
			return VK_DOLLAR;
		case GDK_percent:
			return VK_UNDEFINED;
		case GDK_ampersand:
			return VK_AMPERSAND;
		case GDK_quoteright:
			return VK_QUOTE;
		case GDK_parenleft:
			return VK_LEFT_PARENTHESIS;
		case GDK_parenright:
			return VK_RIGHT_PARENTHESIS;
		case GDK_asterisk:
			return VK_ASTERISK;
		case GDK_plus:
			return VK_PLUS;
		case GDK_comma:
			return VK_COMMA;
		case GDK_minus:
			return VK_MINUS;
		case GDK_period:
			return VK_PERIOD;
		case GDK_slash:
			return VK_SLASH;
		case GDK_0:
			return VK_0;
		case GDK_1:
			return VK_1;
		case GDK_2:
			return VK_2;
		case GDK_3:
			return VK_3;
		case GDK_4:
			return VK_4;
		case GDK_5:
			return VK_5;
		case GDK_6:
			return VK_6;
		case GDK_7:
			return VK_7;
		case GDK_8:
			return VK_8;
		case GDK_9:
			return VK_9;
		case GDK_colon:
			return VK_COLON;
		case GDK_semicolon:
			return VK_SEMICOLON;
		case GDK_less:
			return VK_LESS;
		case GDK_equal:
			return VK_EQUALS;
		case GDK_greater:
			return VK_GREATER;
		case GDK_question:
			return VK_UNDEFINED;
		case GDK_at:
			return VK_AT;
		case GDK_a:
		case GDK_A:
			return VK_A;
		case GDK_b:
		case GDK_B:
			return VK_B;
		case GDK_c:
		case GDK_C:
			return VK_C;
		case GDK_d:
		case GDK_D:
			return VK_D;
		case GDK_e:
		case GDK_E:
			return VK_E;
		case GDK_f:
		case GDK_F:
			return VK_F;
		case GDK_g:
		case GDK_G:
			return VK_G;
		case GDK_h:
		case GDK_H:
			return VK_H;
		case GDK_i:
		case GDK_I:
			return VK_I;
		case GDK_j:
		case GDK_J:
			return VK_J;
		case GDK_k:
		case GDK_K:
			return VK_K;
		case GDK_l:
		case GDK_L:
			return VK_L;
		case GDK_m:
		case GDK_M:
			return VK_M;
		case GDK_n:
		case GDK_N:
			return VK_N;
		case GDK_o:
		case GDK_O:
			return VK_O;
		case GDK_p:
		case GDK_P:
			return VK_P;
		case GDK_q:
		case GDK_Q:
			return VK_Q;
		case GDK_r:
		case GDK_R:
			return VK_R;
		case GDK_s:
		case GDK_S:
			return VK_S;
		case GDK_t:
		case GDK_T:
			return VK_T;
		case GDK_u:
		case GDK_U:
			return VK_U;
		case GDK_v:
		case GDK_V:
			return VK_V;
		case GDK_w:
		case GDK_W:
			return VK_W;
		case GDK_x:
		case GDK_X:
			return VK_X;
		case GDK_y:
		case GDK_Y:
			return VK_Y;
		case GDK_z:
		case GDK_Z:
			return VK_Z;
		case GDK_bracketleft:
			return VK_OPEN_BRACKET;
		case GDK_backslash:
			return VK_BACK_SLASH;
		case GDK_bracketright:
			return VK_CLOSE_BRACKET;
		case GDK_asciicircum:
			return VK_UNDEFINED;
		case GDK_underscore:
			return VK_UNDERSCORE;
		case GDK_grave:
			return VK_UNDEFINED;
		case GDK_braceleft:
			return VK_BRACELEFT;
		case GDK_bar:
			return VK_UNDEFINED;
		case GDK_braceright:
			return VK_BRACERIGHT;
		case GDK_asciitilde:
			return VK_UNDEFINED;
		case GDK_nobreakspace:
			return VK_UNDEFINED;
		case GDK_exclamdown:
			return VK_EXCLAMATION_MARK;
		case GDK_brokenbar:
			return VK_Z;
		case GDK_EuroSign:
			return VK_EURO_SIGN;
		default:
			return VK_UNDEFINED;
		}
	}

/*
 * Class:     org_thenesis_microbackend_ui_gtk_GTKBackend
 * Method:    initialize
 * Signature: (II)I
 */
JNIEXPORT jint JNICALL Java_org_thenesis_microbackend_ui_gtk_GTKBackend_initialize(JNIEnv * env, jobject obj, jint width, jint height) {

  	/* Get VM and current object pointers for use in callbacks */
	if ((*env)->GetJavaVM (env, &vm) < 0)
		return FALSE;
	gtkBackendObject = (*env)->NewGlobalRef (env, obj);
    if (obj == NULL)
    	return FALSE;

	/* Create image buffer */
	imageWidth = width;
	imageHeight = height;
	rgbBuffer = malloc(width * height * 3 * sizeof(char));
	if (rgbBuffer == NULL) (*env)->ThrowNew(env,(*env)->FindClass(env, "java/lang/OutOfMemoryError"), "malloc");

	/* Initialize GTK */
	int argc = 1;
    char **argv = malloc((argc + 1) * sizeof(char *));
    if (argv == NULL) (*env)->ThrowNew(env,(*env)->FindClass(env, "java/lang/OutOfMemoryError"), "malloc");
    argv[0] = "java";
    argv[argc] = NULL;
  	gtk_init(&argc, &argv);
  	gtk_init_add((GtkFunction)main_loop_started, NULL);
  	free(argv);
  
  	/* Initialize GdkRGB */
  	gdk_rgb_init();
  	gtk_widget_set_default_colormap(gdk_rgb_get_cmap());
  	gtk_widget_set_default_visual(gdk_rgb_get_visual());
  
  	window = create_window("MIDPath");
  	/*gtk_window_set_title(GTK_WINDOW(pWindow), "Java");*/
  	darea = gtk_drawing_area_new();
  	gtk_drawing_area_size(GTK_DRAWING_AREA(darea), imageWidth, imageHeight);
  	gtk_container_add(GTK_CONTAINER(window), darea);


    /** key signals */
    gtk_signal_connect(GTK_OBJECT (window), "key_press_event", GTK_SIGNAL_FUNC(key_event), NULL);
    gtk_signal_connect(GTK_OBJECT (window), "key_release_event", GTK_SIGNAL_FUNC(key_event), NULL);

    /** draw area signals */
    gtk_widget_set_events (darea, GDK_EXPOSURE_MASK | GDK_POINTER_MOTION_MASK | GDK_POINTER_MOTION_HINT_MASK | 
                           GDK_BUTTON_MOTION_MASK | GDK_BUTTON_PRESS_MASK | GDK_BUTTON_RELEASE_MASK | GDK_KEY_PRESS_MASK | GDK_KEY_RELEASE_MASK);
    gtk_signal_connect(GTK_OBJECT(darea), "expose-event", GTK_SIGNAL_FUNC(on_darea_expose), NULL);
    gtk_signal_connect(GTK_OBJECT(darea), "motion_notify_event", GTK_SIGNAL_FUNC(motion_notify_event), NULL);
    gtk_signal_connect(GTK_OBJECT(darea), "button_press_event", GTK_SIGNAL_FUNC(button_event), NULL);
    gtk_signal_connect(GTK_OBJECT(darea), "button_release_event", GTK_SIGNAL_FUNC(button_event), NULL);  
  
  	 /** window signals */
  	gtk_signal_connect(GTK_OBJECT(window), "delete_event", GTK_SIGNAL_FUNC(delete_event), NULL);  
    
  	gtk_widget_show_all(GTK_WIDGET(window));

    /* it is important to do this after showing the window! */
    initialize_window_events(window);
    initialize_drawing_area_events(darea);
  	
  	return TRUE;
}



/*
 * Class:     org_thenesis_microbackend_ui_gtk_GTKBackend
 * Method:    gtkMain
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_thenesis_microbackend_ui_gtk_GTKBackend_gtkMainIterationDo(JNIEnv * env, jobject obj) {
	 /* enter the GTK main loop */
  	//gtk_main();
  	
  	/* Runs a single iteration of the mainloop and don't block*/
  	return gtk_main_iteration_do(FALSE);

}

/*
 * Class:     org_thenesis_microbackend_ui_gtk_GTKBackend
 * Method:    isMainLoopStarted
 * Signature: ()I
 */
/*JNIEXPORT jint JNICALL Java_org_thenesis_microbackend_ui_gtk_GTKBackend_isMainLoopStarted(JNIEnv * env, jobject obj) {
	return isMainLoopStarted;
}*/

/*
 * Class:     org_thenesis_microbackend_ui_gtk_GTKBackend
 * Method:    destroy
 * Signature: ()I
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_gtk_GTKBackend_destroy(JNIEnv * env, jobject obj) {
    /* FIXME: Gtk-CRITICAL when called:
      gtk_main_quit(); */ 
	free(rgbBuffer);
}
	

/*
 * Class:     org_thenesis_microbackend_ui_gtk_GTKBackend
 * Method:    writeARGB
 * Signature: ([IIIII)V
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_gtk_GTKBackend_writeARGB(JNIEnv * env, jobject obj, jintArray intBuffer, jint x_src, jint y_src, jint width, jint height) {
	
	gint x, y;	
	GdkRectangle update_rect;
	guchar *srcBuffer;
	guchar *destBuffer;  
	
	// Lock the array (http://www.iam.ubc.ca/guides/javatut99/native1.1/implementing/array.html)
    // arraySize not used
  	//jsize arraySize = (*env)->GetArrayLength(env, intBuffer);
  	jint *jarr = (*env)->GetIntArrayElements(env, intBuffer, 0);
  	
  	srcBuffer = (guchar*)jarr;
 	destBuffer = rgbBuffer;
 	
 	int src_offset = (y_src * imageWidth + x_src) * 4 ;
 	int dest_offset = (y_src * imageWidth + x_src ) * 3 ;
 	int src_pos = 0;
 	int dest_pos = 0;

#ifndef WORDS_BIGENDIAN
    for (y = 0; y < height; y++) {
    	src_pos = src_offset + y * imageWidth * 4;
		dest_pos = dest_offset + y * imageWidth * 3;
		//printf("B%i\n", y);
      	for (x = 0; x < width; x++) {
	  		destBuffer[dest_pos] = srcBuffer[src_pos + 2];
	  		//printf("%i: %x\n", x, destBuffer[dest_pos]); 
	  		destBuffer[dest_pos + 1] = srcBuffer[src_pos + 1];
	  		destBuffer[dest_pos + 2] = srcBuffer[src_pos];
	  		src_pos += 4;
	  		dest_pos += 3;
		}
  	}
#else
	for (y = 0; y < height; y++) {
		src_pos = src_offset + y * imageWidth * 4;
		dest_pos = dest_offset + y * imageWidth * 3;
      	for (x = 0; x < width; x++) {
      		//printf("%x\n", *srcBuffer); 
      		src_pos++;
      		//printf("%x\n", *srcBuffer); 
	  		destBuffer[dest_pos++] = srcBuffer[src_pos++];
	  		//printf("%x\n", *srcBuffer); 
	  		destBuffer[dest_pos++] = srcBuffer[src_pos++];
	  		//printf("%x\n", *srcBuffer); 
	  		destBuffer[dest_pos++] = srcBuffer[src_pos++];
		}
  	}
#endif
  
  // Release the array and clean context
  (*env)->ReleaseIntArrayElements(env, intBuffer, jarr, 0);
	
  update_rect.x = x_src;
  update_rect.y = y_src;
  update_rect.width = width;
  update_rect.height = height;
  
  //printf("[native] GTKCanvas_writeARGB() : x=%i y=%i w=%i h=%i \n", x_src, y_src, width, height);
  //gtk_widget_queue_draw_area (darea, update_rect.x, update_rect.y, update_rect.width, update_rect.height);
  //gdk_window_invalidate_rect(window, &update_rect, TRUE);
  //printf("redraw area\n");
  gtk_widget_draw (darea, &update_rect);
  
  /* Make sure all X commands are sent to the X server; not strictly
   * necessary here, but always a good idea when you do anything
   * from a thread other than the one where the main loop is running.
   */
   //gdk_flush ();


}

static void main_loop_started() {
	/*isMainLoopStarted = TRUE;
	printf("main_loop_started %i\n", isMainLoopStarted);*/
}


gboolean on_darea_expose (GtkWidget *widget, GdkEventExpose *event, gpointer user_data) {

  int x = event->area.x;
  int y = event->area.y;
  int w = event->area.width;
  int h = event->area.height;
  
  guchar *srcBuffer = &(rgbBuffer[(y * imageWidth + x) * 3]);
  int rowstride = imageWidth * 3;
	
  gdk_draw_rgb_image(widget->window, widget->style->fg_gc[GTK_STATE_NORMAL], x, y, w, h,
		      GDK_RGB_DITHER_MAX, srcBuffer , rowstride);
		 
//printf("[native] on_darea_expose(): x=%i y=%i w=%i h=%i \n", x, y, w, h);		 
//  gdk_draw_rgb_image (widget->window, widget->style->fg_gc[GTK_STATE_NORMAL],
//		      0, 0, imageWidth, imageHeight,
//		      GDK_RGB_DITHER_MAX, rgbBuffer, imageWidth * 3);
		   
  return TRUE;
}


void key_entered(int eventtype, int keycode, int unicode_character) {

	JNIEnv *env = NULL;
    jclass class;
    jmethodID callback;

    if ((*vm)->AttachCurrentThread(vm, (void **)&env, NULL) < 0) {
    	fprintf (stderr, "%s[%d]: AttachCurrentThread ()\n", __FILE__, __LINE__);
        return;
    }

    class = (*env)->GetObjectClass (env, gtkBackendObject);
    callback = (*env)->GetMethodID (env, class, "onKeyEvent", "(ZII)V");
    if (callback == NULL) {
    	 fprintf (stderr, "%s[%d]: GetMethodID ()\n", __FILE__, __LINE__);
         return;
    }
    
     bool pressed = (eventtype == GDK_KEY_PRESS) ? true : false;
    
   	(*env)->CallVoidMethod(env, gtkBackendObject, callback, pressed, convertKeyCode(keycode), unicode_character);

    
    /*if ((*vm)->DetachCurrentThread(vm) < 0) {
        fprintf (stderr, "%s[%d]: DetachCurrentThread ()\n", __FILE__, __LINE__);
    }*/
}

gboolean key_event(GtkWidget *widget, GdkEventKey *event) {
	
    guint32 unicode;
    
    unicode = gdk_keyval_to_unicode(event->keyval);
    key_entered(event->type, event->keyval, unicode);
    
    /*printf("Key pressed\n"); */

    
  	return TRUE;
}


gboolean button_event(GtkWidget *widget, GdkEventButton *event) {
  /*if (event->button == 1 && pixmap != NULL)
      draw_brush (widget, event->x, event->y);*/
      
    JNIEnv *env = NULL;
    jclass class;
    jmethodID callback;
    
    /*printf("Button pressed\n");*/

    if ((*vm)->AttachCurrentThread(vm, (void **)&env, NULL) < 0) {
    	fprintf (stderr, "%s[%d]: AttachCurrentThread ()\n", __FILE__, __LINE__);
        return FALSE;
    }

    class = (*env)->GetObjectClass (env, gtkBackendObject);
    callback = (*env)->GetMethodID (env, class, "onButtonEvent", "(ZIII)V");
    if (callback == NULL) {
   		fprintf (stderr, "%s[%d]: GetMethodID ()\n", __FILE__, __LINE__);
      	return FALSE;
    }
    
    bool pressed = (event->type == GDK_BUTTON_PRESS) ? true : false;
    
    (*env)->CallVoidMethod(env, gtkBackendObject, callback, pressed, (int)event->x, (int)event->y, (int)event->state);
    
     /*if ((*vm)->DetachCurrentThread(vm) < 0) {
        fprintf (stderr, "%s[%d]: DetachCurrentThread ()\n", __FILE__, __LINE__);
    }*/

  	return TRUE;
}


gboolean motion_notify_event(GtkWidget *widget, GdkEventMotion *event ) {
	
	JNIEnv *env = NULL;
    jclass class;
    jmethodID callback;
    int x, y;
  	GdkModifierType state;
    
  	/*printf("Motion detetected: %i \n", env);*/

    if ((*vm)->AttachCurrentThread(vm, (void **)&env, NULL) < 0) {
    	fprintf (stderr, "%s[%d]: AttachCurrentThread ()\n", __FILE__, __LINE__);
        return FALSE;
    }
    
    class = (*env)->GetObjectClass (env, gtkBackendObject);
    
    callback = (*env)->GetMethodID (env, class, "onMotionEvent", "(III)V");
    if (callback == NULL) {
   		fprintf (stderr, "%s[%d]: GetMethodID ()\n", __FILE__, __LINE__);
      	return FALSE;
    }
    
    if (event->is_hint) {
    	gdk_window_get_pointer (event->window, &x, &y, &state);
  	} else {
    	x = event->x;
      	y = event->y;
      	state = event->state;
   	}
    
    (*env)->CallVoidMethod(env, gtkBackendObject, callback, x, y, (int)state);

    /*if ((*vm)->DetachCurrentThread(vm) < 0) {
        fprintf (stderr, "%s[%d]: DetachCurrentThread ()\n", __FILE__, __LINE__);
    }*/
    
  	return TRUE;
  
}

/* GDK_DELETE means that the window manager has asked the application to destroy this window. 
 * If a widget receives the signal corresponding to this event, and the signal emission returns FALSE, 
 * the widget is automatically destroyed by the GTK+ main loop. */
gint delete_event(GtkWidget *widget, GdkEvent *event, gpointer data) {
   
	JNIEnv *env = NULL;
    jclass class;
    jmethodID callback;
    
    /*printf("window delete event\n");*/

    if ((*vm)->AttachCurrentThread(vm, (void **)&env, NULL) < 0) {
    	fprintf (stderr, "%s[%d]: AttachCurrentThread ()\n", __FILE__, __LINE__);
        return FALSE;
    }

    class = (*env)->GetObjectClass (env, gtkBackendObject);
    callback = (*env)->GetMethodID (env, class, "onWindowDeleteEvent", "()V");
    if (callback == NULL) {
   		fprintf (stderr, "%s[%d]: GetMethodID ()\n", __FILE__, __LINE__);
      	return FALSE;
    }
    
    (*env)->CallVoidMethod(env, gtkBackendObject, callback);
    
     /*if ((*vm)->DetachCurrentThread(vm) < 0) {
        fprintf (stderr, "%s[%d]: DetachCurrentThread ()\n", __FILE__, __LINE__);
    }*/

 
    return FALSE; 
}

