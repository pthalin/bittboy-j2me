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
 
/* References: 
 *  - http://www.linuxjournal.com/article/1080
 *  - http://www.linuxjournal.com/article/2783
 * 	- http://www.linuxjournal.com/article/6429
 *  - http://www.kernel.org/pub/linux/kernel/people/aeb/kbdbook.tmpl
 *  - http://www.w00w00.org/files/articles/conioctls.txt
 *  - http://huru.imukuppi.org/opengl/plg/node191.html
 */
 
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdbool.h>
#include <stdint.h>

#include <sys/ioctl.h>
#include <sys/mman.h>
#include <termios.h>
#include <sys/vt.h>
#include <sys/time.h>
#include <sys/types.h>

#include <linux/fb.h>
#include <linux/kd.h>
#include <linux/keyboard.h>
#include <linux/input.h>

#include "org_thenesis_microbackend_ui_fb_FBBackend.h"

#define NR_MAPS 16

/* this macro is used to tell if "bit" is set in "array"
 * it selects a byte from the array, and does a boolean AND 
 * operation with a byte that only has the relevant bit set. 
 * eg. to check for the 12th bit, we do (array[1] & 1<<4)
 */
#define test_bit(bit, array)    (array[bit/8] & (1<<(bit%8)))

static int loopEventRunning = true;

/* Keyboard variables */
static int keyboard_fd;
static struct termios old_term, new_term;
static int old_mode = -1;
static int keychar_map[NR_MAPS][NR_KEYS];
static int keycode_map[NR_MAPS][NR_KEYS];
static int map = K_NORMTAB;

/* Pointer variables */
static int mouse_fd = -1; 
static int touchscreen_fd = -1;

/* Framebuffer variables */
static jint imageWidth = 0;
static jint imageHeight = 0;
static struct fb_fix_screeninfo fb_fix_infos;
static struct fb_var_screeninfo fb_var_infos;
static int fb_fd; 
static char *fb_ptr;

enum { 	VK_ENTER = '\n', VK_BACK_SPACE = '\b', VK_TAB = '\t', VK_CANCEL = 3, VK_CLEAR = 12, VK_SHIFT = 16,
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

static int CHAR_UNDEFINED = 65535; // '\uffff'


static void init_keymap(int kb_fd) {
    struct kbentry entry;
    int keycode, map;

	for (map = 0; map < NR_MAPS; map++) {
    	for (keycode = 0; keycode < NR_KEYS; keycode++) {
            
            keychar_map[map][keycode] = CHAR_UNDEFINED;
        	keycode_map[map][keycode] = VK_UNDEFINED;
        
        	/* Look up this key. If the lookup fails, ignore.
           	If it succeeds, KVAL(entry.kb_value) will be the
           	8-bit representation of the character the kernel
           	has mapped to this keycode. */
        	entry.kb_table = map;
        	entry.kb_index = keycode;
        	if (ioctl(kb_fd, KDGKBENT, &entry)) continue;
        	
        	
        	/* Map visible chars to standard ASCII chars and modifier keys to some useless ASCII chars */
        	int kval = KVAL(entry.kb_value);
        	switch (KTYP(entry.kb_value)) {
			case KT_SHIFT:
				switch (entry.kb_value) {
				case K_ALTGR:
				case K_ALT:
		    		keycode_map[map][keycode] = VK_ALT;
		    		break;
				case K_CTRL:
				case K_CTRLL:
				case K_CTRLR:
		    		keycode_map[map][keycode] = VK_CONTROL;
		    		break;
				case K_SHIFT:
				case K_SHIFTL:
				case K_SHIFTR:
					keycode_map[map][keycode] = VK_SHIFT;
		    		break;
				default:
		    		break;
				}
				break;
			case KT_FN:
				if (kval < 12)
		    		keycode_map[map][keycode] = VK_F1 + kval;
				else switch (entry.kb_value) {
				case K_FIND:
		    		keycode_map[map][keycode] = VK_FIND;
		    		break;
				case K_INSERT:
		    		keycode_map[map][keycode] = VK_INSERT;
		    		break;
				case K_REMOVE:
		    		keycode_map[map][keycode] = VK_DELETE;
		    		break;
				case K_SELECT:
		    		keycode_map[map][keycode] = VK_END;
		    		break;
				case K_PGUP:
		    		keycode_map[map][keycode] = VK_PAGE_UP;
		    		break;
				case K_PGDN:
		    		keycode_map[map][keycode] = VK_PAGE_DOWN;
		    		break;
		    	case K_UNDO:
		    		keycode_map[map][keycode] = VK_UNDO;
				default:
		    		break;
				}
				break;
			case KT_CUR:
				switch (entry.kb_value) {
				case K_DOWN:
		    		keycode_map[map][keycode] = VK_DOWN;
		    		break;
				case K_LEFT:
		    		keycode_map[map][keycode] = VK_LEFT;
		    		break;
				case K_RIGHT:
		    		keycode_map[map][keycode] = VK_RIGHT;
		    		break;
				case K_UP:
		    		keycode_map[map][keycode] = VK_UP;
		    		break;
				}
				break;
			 case KT_SPEC:
				switch (entry.kb_value) {
				case K_ENTER:
		    		keycode_map[map][keycode] = VK_ENTER;
		    		break;   
				case K_CAPS:
					keycode_map[map][keycode] = VK_CAPS_LOCK;
					break;          
				case K_NUM:
					keycode_map[map][keycode] = VK_NUM_LOCK;
					break;          
				}
				break;
			case KT_PAD:
				switch (entry.kb_value) {
				case K_P0:
					keycode_map[map][keycode] = VK_NUMPAD0;
					keychar_map[map][keycode] = kval;
					break;
				case K_P1:
					keycode_map[map][keycode] = VK_NUMPAD1;
					keychar_map[map][keycode] = kval;
					break;
				case K_P2:
					keycode_map[map][keycode] = VK_NUMPAD2;
					keychar_map[map][keycode] = kval;
					break;
				case K_P3:
					keycode_map[map][keycode] = VK_NUMPAD3;
					keychar_map[map][keycode] = kval;
					break;
				case K_P4:
					keycode_map[map][keycode] = VK_NUMPAD4;
					keychar_map[map][keycode] = kval;
					break;
				case K_P5:
					keycode_map[map][keycode] = VK_NUMPAD5;
					keychar_map[map][keycode] = kval;
					break;
				case K_P6:
					keycode_map[map][keycode] = VK_NUMPAD6;
					keychar_map[map][keycode] = kval;
					break;
				case K_P7:
					keycode_map[map][keycode] = VK_NUMPAD7;
					keychar_map[map][keycode] = kval;
					break;
				case K_P8:
					keycode_map[map][keycode] = VK_NUMPAD8;
					keychar_map[map][keycode] = kval;
					break;
				case K_P9:
					keycode_map[map][keycode] = VK_NUMPAD9;
					keychar_map[map][keycode] = kval;
					break;
				case K_PENTER:
		    		keycode_map[map][keycode] = VK_ENTER;
		    		break;
				}
		    	break;
		    case KT_ASCII:
		    	if (((kval >= '0') && (kval <= '9')) || ((kval >= 'a') && (kval <= 'z')) || ((kval >= 'A') && (kval <= 'Z'))) {
		    		keychar_map[map][keycode] = kval;
		    		keycode_map[map][keycode] = kval;
		    		break;
		    	} else if (kval == 13) {
        			keychar_map[map][keycode] = 13;
        			keycode_map[map][keycode] = VK_ENTER;
		    	} else {
		    		keychar_map[map][keycode] = kval;
					keycode_map[map][keycode] = VK_UNDEFINED;
		    	}
		    	break;
			default:
				keychar_map[map][keycode] = kval;
				keycode_map[map][keycode] = VK_UNDEFINED;
				break;
        	}
         
        	//printf("keycode_map[%i][%i]=%i\n", map, keycode, keycode_map[map][keycode]);
        	//printf("keychar_map[%i][%i]=%c\n", map, keycode, keychar_map[map][keycode]);
    
    	}
	}
        
}

static bool initializeKeyboard(char *keyboard_device_name) {
	
	keyboard_fd = open(keyboard_device_name, O_RDONLY, 0);
	if (keyboard_fd < 0) {
		return false;
	}
	
	// Switch to VT 7
	//ioctl(keyboard_fd, VT_ACTIVATE, 7);
	//ioctl(keyboard_fd, VT_WAITACTIVE, 7);
	
	/* Find the keyboard's mode so we can restore it later. */
	if (ioctl(keyboard_fd, KDGKBMODE, &old_mode) != 0) {
	    fprintf (stderr, "[WARNING] %s[%d]: Unable to query keyboard mode\n", __FILE__, __LINE__);
	}
	
	/* Adjust the terminal's settings. In particular, disable
	   echoing, signal generation, and line buffering. Any of
	   these could cause trouble. Save the old settings first. */
	if (tcgetattr(keyboard_fd, &old_term) != 0) {
	    fprintf (stderr, "[WARNING] %s[%d]: Unable to query terminal settings\n", __FILE__, __LINE__);
	}
	
	new_term = old_term;
	new_term.c_iflag = 0;
	new_term.c_lflag &= ~(ECHO | ICANON | ISIG);
	
	/* TCSAFLUSH discards unread input before making the change.
	   A good idea. */
	if (tcsetattr(keyboard_fd, TCSAFLUSH, &new_term) != 0) {
		fprintf (stderr, "[WARNING] %s[%d]: Unable to change terminal settings\n", __FILE__, __LINE__);
	}
	
	/* Put the keyboard in mediumraw mode. */
	if (ioctl(keyboard_fd, KDSKBMODE, K_MEDIUMRAW) != 0) {
		fprintf (stderr, "[WARNING] %s[%d]: Unable to set mediumraw mode\n", __FILE__, __LINE__);
	}
	
	/*Set console in graphics mode */
	ioctl(keyboard_fd, KDSETMODE, KD_GRAPHICS); // vs KD_TEXT. Better to comment this line while debugging
	init_keymap(keyboard_fd);
	
	return true;
}

static void closeKeyboard() {
	
	/* Restore the previous keyboard mode. */
    if (old_mode != -1) {
        ioctl(keyboard_fd, KDSKBMODE, old_mode);
        ioctl(keyboard_fd, KDSETMODE, KD_TEXT);
        //ioctl(keyboard_fd, VT_ACTIVATE, 1);
        //ioctl(keyboard_fd, VT_WAITACTIVE, 1);
        tcsetattr(keyboard_fd, 0, &old_term);
    }
    /* Only bother closing the keyboard fd if it's not stdin, stdout, or stderr. */
    if (keyboard_fd > 3)
        close(keyboard_fd);
}

static bool initializeMouse(char *mouse_device_name) {
	
	/* Initialize the mouse */
	mouse_fd = open(mouse_device_name, O_RDWR);
	if (mouse_fd < 0) {
		fprintf(stderr, "[WARNING] %s[%d]: Can't initialize mouse\n", __FILE__, __LINE__);
		return false;
	}
	
	// Check if the opened device is a mouse
	uint8_t evtype_bitmask[EV_MAX/8 + 1];
  	if (ioctl(mouse_fd, EVIOCGBIT(0, sizeof(evtype_bitmask)), evtype_bitmask) >= 0) {
    	if (test_bit(EV_REL, evtype_bitmask)) {
	 		return true;
    	} else {
    		fprintf(stderr, "[WARNING] %s[%d]: %s is not a mouse device\n", __FILE__, __LINE__, mouse_device_name);
    		return false;
    	} 
  	} else {
  		fprintf(stderr, "[WARNING] %s[%d]: can't determined if %s is a mouse device\n", __FILE__, __LINE__, mouse_device_name);
  		//return false; // ignored ?
  	}
	
	return true;
	
}

static void closeMouse() {
	if (mouse_fd >= 0) 
    	close(mouse_fd);
}

static bool initializeTouchscreen(char *ts_device_name) {
	/* Initialize the touchscreen */
	touchscreen_fd = open(ts_device_name, O_RDWR);
	if (touchscreen_fd < 0) {
		fprintf(stderr, "[WARNING] %s[%d]: Can't initialize touchscreen\n", __FILE__, __LINE__);
		return false;
	}
	
	// Check if the opened device is a touchscreen
	uint8_t evtype_bitmask[EV_MAX/8 + 1];
  	if (ioctl(touchscreen_fd, EVIOCGBIT(0, sizeof(evtype_bitmask)), evtype_bitmask) >= 0) {
    	if (test_bit(EV_ABS, evtype_bitmask)) {
	 		return true;
    	} else {
    		fprintf(stderr, "[WARNING] %s[%d]: %s is not a touchscreen device\n", __FILE__, __LINE__, ts_device_name);
    		return false;
    	} 
  	} else {
  		fprintf(stderr, "[WARNING] %s[%d]: can't determined if %s is a touchscreen device\n", __FILE__, __LINE__, ts_device_name);
  		//return false; // ignored ?
  	}
	
	return true;
}

static void closeTouchscreen() {
	if (touchscreen_fd >= 0) 
    	close(touchscreen_fd);
}

static bool initializeFramebuffer(char *fb_device_name, int width, int height) {
	
	imageWidth = width;
   	imageHeight = height;

  	fb_fd = open(fb_device_name, O_RDWR) ;
  	if (fb_fd < 0) {
     	return false;
    }
  
  	ioctl(fb_fd, FBIOGET_FSCREENINFO, &fb_fix_infos);
   	ioctl(fb_fd, FBIOGET_VSCREENINFO, &fb_var_infos);
   	
   	/* printf("[DEBUG](native) Framebuffer: smem_len=%i type=%i visual=%i line_length=%i\n", fb_fix_infos.smem_len, fb_fix_infos.type, fb_fix_infos.visual, fb_fix_infos.line_length);
   	printf("[DEBUG](native) Framebuffer: xres=%i yres=%i bits_per_pixel=%i\n", fb_var_infos.xres, fb_var_infos.yres, fb_var_infos.bits_per_pixel); */
   	
   	fb_ptr = (char*)mmap(NULL, fb_fix_infos.smem_len, PROT_READ | PROT_WRITE, MAP_SHARED, fb_fd, 0);
   	
   	return true;
}

static void closeFramebuffer() {
	/* Clean the framebuffer context */
    munmap(fb_ptr, fb_fix_infos.smem_len);
	close(fb_fd);
}

/*
 * Class:     org_thenesis_microbackend_ui_fb_FBBackend
 * Method:    initialize
 * Signature: (II)I
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_fb_FBBackend_getKeymap(JNIEnv * env, jobject obj, jobjectArray keymapArray) {

	int map, keycode;

	for (map = 0; map < NR_MAPS; map++) {
		jcharArray oneDim = (jcharArray)(*env)->GetObjectArrayElement(env, keymapArray, map);
		jchar *element = (*env)->GetCharArrayElements(env, oneDim, 0);
    	for (keycode = 0; keycode < NR_KEYS; keycode++) {
    		element[keycode] = (jchar)keychar_map[map][keycode];
    	}
    	/* Release the char array */
  		(*env)->ReleaseCharArrayElements(env, oneDim, element, 0);
	}

}

/*
 * Class:     org_thenesis_microbackend_ui_fb_FBBackend
 * Method:    initialize
 * Signature: (II)I
 */
JNIEXPORT jboolean JNICALL Java_org_thenesis_microbackend_ui_fb_FBBackend_initialize(JNIEnv * env, jobject obj, jstring keyboardDeviceName, jstring mouseDeviceName, jstring touchscreenDeviceName, jstring fbDeviceName, jint width, jint height) {
   	
	char *keyboard_device_name;
	char *utfKeyboardDeviceName;
	char *mouse_device_name;
	char *utfMouseDeviceName;
	char *touchscreen_device_name;
	char *utfTouchscreenDeviceName;
	char *fb_device_name;
	char *utfFbDeviceName;
	
	/* Initialize the Linux Framebuffer */
	if (fbDeviceName == NULL) {
		return JNI_FALSE;
	} else {
		utfFbDeviceName = (char *)(*env)->GetStringUTFChars(env, fbDeviceName, 0);
	    fb_device_name = strdup(utfFbDeviceName);
		(*env)->ReleaseStringUTFChars(env, fbDeviceName, utfFbDeviceName);
	   	initializeFramebuffer(fb_device_name, width, height);
	}
	
	/* Initialize the keyboard */
	if (keyboardDeviceName != NULL) {
		utfKeyboardDeviceName = (char *)(*env)->GetStringUTFChars(env, keyboardDeviceName, 0);
    	keyboard_device_name = strdup(utfKeyboardDeviceName);
		(*env)->ReleaseStringUTFChars(env, keyboardDeviceName, utfKeyboardDeviceName);
		initializeKeyboard(keyboard_device_name);
	}
	
	/* Initialize the mouse */
    if (mouseDeviceName != NULL) {
    	utfMouseDeviceName = (char *)(*env)->GetStringUTFChars(env, mouseDeviceName, 0);
    	mouse_device_name = strdup(utfMouseDeviceName);
		(*env)->ReleaseStringUTFChars(env, mouseDeviceName, utfMouseDeviceName);
		initializeMouse(mouse_device_name);
    }
    
    /* Initialize the touchscreen */
    if (touchscreenDeviceName != NULL) {
    	utfTouchscreenDeviceName = (char *)(*env)->GetStringUTFChars(env, touchscreenDeviceName, 0);
    	touchscreen_device_name = strdup(utfTouchscreenDeviceName);
		(*env)->ReleaseStringUTFChars(env, touchscreenDeviceName, utfTouchscreenDeviceName);
		initializeTouchscreen(touchscreen_device_name);
    }                   

  	return JNI_TRUE;
  	
}

/*
 * Class:     org_thenesis_microbackend_ui_fb_FBBackend
 * Method:    writeARGB
 * Signature: ([IIIII)V
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_fb_FBBackend_drawARGB(JNIEnv * env, jobject obj, jintArray intBuffer, jint offset, jint scanlength, jint x, jint y, jint width, jint height) {
	
	char *srcBuffer;
  	jint *jarr = (*env)->GetIntArrayElements(env, intBuffer, 0);
  	srcBuffer = (char*)jarr;
 	
 	int dest_bytes_per_pixel = fb_var_infos.bits_per_pixel / 8;
 	
 	/* Clipping */
 	
 	// Clip source rectangle in source image.
	int sxmin = 0, symin = 0, sxmax = width - 1, symax = height - 1;

	// Clip destination rectangle in destination image.
	int dxmin = x + sxmin, dymin = y + symin, dxmax = x + sxmax, dymax = y + symax;
	if (dxmin < 0)
		dxmin = 0;
	if (dymin < 0)
		dymin = 0;
	if (dxmax > imageWidth)
		dxmax = imageWidth - 1;
	if (dymax > imageHeight)
		dymax = imageHeight - 1;

	// New source rectangle.
	sxmin = dxmin - x;
	symin = dymin - y;
	sxmax = dxmax - x;
	symax = dymax - y;

	int w = sxmax - sxmin + 1, h = symax - symin + 1;
	
	/* Drawing */
 	
 	int src_pos = 0;
	int i, j;
	__u32 argb, a, r, g, b, c, color;
	for (j = 0; j < h; j++) {
		
		src_pos = offset + (symin + j) * scanlength + sxmin;
		void *destBuffer = (void*) (fb_ptr + (dymin + j) * fb_fix_infos.line_length + dxmin * dest_bytes_per_pixel);
      	
      	for (i = 0; i < w; i++) {
      		/* printf("%x\n", *srcBuffer); */

			/* Get ARGB components */
			argb = ((__u32*)srcBuffer)[src_pos + i];
			a = (argb >> 24) & 0xFF;
			r = (argb >> 16) & 0xFF;
			g = (argb >> 8) & 0xFF;
			b = argb & 0xFF;    
			/* printf("argb=%x a=%x r=%x g=%x b=%x\n", argb, a, r, g, b); */

			/* Convert ARGB components to the target color */
			c = r >> (8 - fb_var_infos.red.length) ;
			c <<= fb_var_infos.red.offset ;
			color = c ;
			c = g >> (8 - fb_var_infos.green.length) ;
       		c <<= fb_var_infos.green.offset ;
       		color |= c ;
       		c = b >> (8 - fb_var_infos.blue.length) ;
       		c <<= fb_var_infos.blue.offset ;
       		color |= c ;
       		c = a >> (8 - fb_var_infos.transp.length) ;
       		c <<= fb_var_infos.transp.offset ;
       		color |= c ;
       		/* printf("color=%x\n", color); */
       	
       		/* Set pixel in the framebuffer */
       		switch (fb_var_infos.bits_per_pixel) {
         	case 16 :
         		((__u16*)destBuffer)[i] = (__u16)color;
           		break ;
         	case 32 :
           		((__u32*)destBuffer)[i] = color;
           		break ;
       		}
      		
		}
  	}
  
  	/* Release the array and clean context */
  	(*env)->ReleaseIntArrayElements(env, intBuffer, jarr, 0);
	
	/* Sync memory and framebuffer */
  	msync(fb_ptr, fb_fix_infos.smem_len, MS_SYNC | MS_INVALIDATE); 

}

/*
 * Clean context
 */ 
static void clean() {
    closeFramebuffer();
	closeKeyboard();
	closeMouse();
	closeTouchscreen();
}

static void handleKeyEvent(JNIEnv* env, jobject fbCanvasObject) {

	unsigned char c;
	jclass clazz;
    jmethodID callback;
	
	/*int bytes = */read(keyboard_fd, &c, sizeof(c));
 	  	
 	// Get key infos
	int pressed = !(c & 0x80);
	int keycode = (unsigned int)c & 0x7F;
	/* Print the keycode. The top bit is the pressed/released flag, and the lower seven are the keycode. */
   	/*printf("%s: %2Xh (%i)\n", (c & 0x80) ? "Released" : " Pressed", (unsigned int)c & 0x7F, (unsigned int)c & 0x7F);*/
 	   	
 	// Handle modifiers (check if we need another keymap)
 	char modifierKeyCode = keycode_map[K_NORMTAB][keycode];
	if (modifierKeyCode == VK_SHIFT) {
		map = pressed ? K_SHIFTTAB : K_NORMTAB;
		 printf("shift ==> map=%i\n", map);
	} else if (modifierKeyCode == VK_ALT) {
		map = pressed ? K_ALTTAB : K_NORMTAB;
	}
			
	// Fire event
    clazz = (*env)->GetObjectClass(env, fbCanvasObject);
    callback = (*env)->GetMethodID(env, clazz, "onKeyEvent", "(ZIC)V");
    if (callback == NULL) {
    	fprintf (stderr, "[ERROR] %s[%d]: onKeyEvent method not found\n", __FILE__, __LINE__);
    	return;
    }
    
    //printf("keychar_map[%i][%i]=%c\n", map, keycode, keychar_map[map][keycode]);
    //printf("keycode_map[%i][%i]=%i\n", map, keycode, keycode_map[map][keycode]);
    
   	(*env)->CallVoidMethod(env, fbCanvasObject, callback, (pressed ? JNI_TRUE : JNI_FALSE), (jint)keycode_map[map][keycode], (jchar)keychar_map[map][keycode]);

}

static void handleMouseEvent(JNIEnv* env, jobject fbCanvasObject) {

	struct input_event mouse_events[64];
	int button=0, x=0, y=0, i, rd;
	jclass clazz;
    jmethodID callback;
  	
  	rd = read(mouse_fd, mouse_events, sizeof(struct input_event) * 64);
  	if( rd < sizeof(struct input_event) ) {
  		return;
  	}

  	for (i = 0; i < rd / sizeof(struct input_event); i++) {
    	if(mouse_events[i].type == EV_REL && mouse_events[i].code == REL_X) 
    		x = mouse_events[i].value;
    	else if(mouse_events[i].type == EV_REL && mouse_events[i].code == REL_Y) 
    		y = mouse_events[i].value;
    	else if(mouse_events[i].type == EV_KEY) {
			if(mouse_events[i].code == BTN_MOUSE) { 
	  			button &= ~0x4; 
	  			button |= mouse_events[i].value << 2; 
			} else if(mouse_events[i].code == BTN_LEFT) {
	  			button &= ~0x1; 
	 			button |= mouse_events[i].value; 
        	} else if(mouse_events[i].code == BTN_RIGHT) { 
	  			button &= ~0x2; 
	  			button |= mouse_events[i].value << 1; 
        	}
    	} else if(mouse_events[i].type == EV_SYN && mouse_events[i].code == SYN_REPORT && mouse_events[i].value == 0) {
    		//printf("mouse event: x=%i y=%i button=%i\n", x, y, button);
			// Fire event
    		clazz = (*env)->GetObjectClass(env, fbCanvasObject);
    		callback = (*env)->GetMethodID(env, clazz, "onRawMouseEvent", "(III)V");
    		if (callback == NULL) {
   				fprintf (stderr, "[ERROR] %s[%d]: onRawMouseEvent method not found\n", __FILE__, __LINE__);
    			return;
    		}
   			(*env)->CallVoidMethod(env, fbCanvasObject, callback, (jint)x, (jint)y, (jchar)button);			

			// Reset x,y
			x=0, y=0;
    	}	
  	}
			
}

static void handleTouchscreenEvent(JNIEnv* env, jobject fbCanvasObject) {

	struct input_event touchscreen_events[64];
	int button=0, x=0, y=0, i, rd;
	jclass clazz;
    jmethodID callback;
  	
  	rd = read(touchscreen_fd, touchscreen_events, sizeof(struct input_event) * 64);
  	if( rd < sizeof(struct input_event) ) {
  		return;
  	}
  	
  	for (i = 0; i < rd / sizeof(struct input_event); i++) {
    	if(touchscreen_events[i].type == EV_ABS && touchscreen_events[i].code == ABS_X) 
    		x = touchscreen_events[i].value;
    	else if(touchscreen_events[i].type == EV_ABS && touchscreen_events[i].code == ABS_Y) 
    		y = touchscreen_events[i].value;
    	else if(touchscreen_events[i].type == EV_KEY && touchscreen_events[i].code == BTN_TOUCH) 
    		button = touchscreen_events[i].value << 2;
    	else if(touchscreen_events[i].type == EV_SYN && touchscreen_events[i].code == SYN_REPORT && touchscreen_events[i].value == 0) {
			//printf("touchscreen event: x=%i y=%i button=%i", x, y, button);
			// Fire event
    		clazz = (*env)->GetObjectClass(env, fbCanvasObject);
    		callback = (*env)->GetMethodID(env, clazz, "onRawTouchscreenEvent", "(III)V");
    		if (callback == NULL) {
   				fprintf (stderr, "[ERROR] %s[%d]: onRawTouchscreenEvent method not found\n", __FILE__, __LINE__);
    			return;
    		}
   			(*env)->CallVoidMethod(env, fbCanvasObject, callback, (jint)x, (jint)y, (jchar)button);
    	}		
  	}
			
}


/*
 * Class:     org_thenesis_microbackend_ui_fb_FBBackend
 * Method:    eventLoop
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_fb_FBBackend_eventLoop(JNIEnv* env, jobject fbCanvasObject) {
	
	static fd_set fds;
	static struct timeval tv;
	int retval;
	int max_fd;
	
	max_fd = (mouse_fd > keyboard_fd) ? mouse_fd:keyboard_fd;
	max_fd = (touchscreen_fd > max_fd) ? touchscreen_fd:max_fd;
	max_fd++;
	
	// Exit event loop if no input available
	if (max_fd <= 0) {
		clean();
		return;
	}
    
    // Event loop
    while(loopEventRunning) {
    
		/* Check if data are available*/
		FD_ZERO(&fds);
		if (keyboard_fd > 0)
			FD_SET(keyboard_fd, &fds);
		if (mouse_fd > 0)
			FD_SET(mouse_fd, &fds);
		if (touchscreen_fd > 0)
			FD_SET(touchscreen_fd, &fds);
		tv.tv_sec = 0; 
		tv.tv_usec = 100000; /* Unblock every 100ms to let a chance to exit the loop */
		retval = select(max_fd, &fds, NULL, NULL, &tv);
		if (retval == 0) { // Timeout;
			continue;
		} else if (retval < 0) { // Error
			fprintf (stderr, "[ERROR] %s[%d]: Select error. Exiting event loop...\n", __FILE__, __LINE__);
			break;
		}
		
		/*  Check if there is something to read */
		if ((keyboard_fd > 0) && FD_ISSET(keyboard_fd, &fds)) {
 			handleKeyEvent(env, fbCanvasObject);
		} 
		if ((mouse_fd > 0) && FD_ISSET(mouse_fd, &fds)) {
			handleMouseEvent(env, fbCanvasObject);
    	}
    	if ((touchscreen_fd > 0) && FD_ISSET(touchscreen_fd, &fds)) {
			handleTouchscreenEvent(env, fbCanvasObject);
    	}
		
    }
    
    /* Clean all before exiting */
   clean();
    
}


/*
 * Class:     org_thenesis_microbackend_ui_fb_FBBackend
 * Method:    quit
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_org_thenesis_microbackend_ui_fb_FBBackend_quit(JNIEnv * env, jobject obj) {
    loopEventRunning = false;
}





