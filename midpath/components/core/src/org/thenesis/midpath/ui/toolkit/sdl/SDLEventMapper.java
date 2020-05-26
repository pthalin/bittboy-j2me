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
package org.thenesis.midpath.ui.toolkit.sdl;

import javax.microedition.lcdui.Canvas;

import sdljava.event.SDLEvent;
import sdljava.event.SDLKey;

import com.sun.midp.configurator.Constants;
import com.sun.midp.events.EventMapper;
import com.sun.midp.lcdui.EventConstants;

public class SDLEventMapper implements EventMapper {

	public int getGameAction(int keyCode) {
		switch (keyCode) {
		case Constants.KEYCODE_DOWN:
			return Canvas.DOWN;
		case Constants.KEYCODE_SELECT:
			return Canvas.FIRE;
		case SDLKey.SDLK_a:
			return Canvas.GAME_A;
		case SDLKey.SDLK_b:
			return Canvas.GAME_B;
		case SDLKey.SDLK_c:
			return Canvas.GAME_C;
		case SDLKey.SDLK_d:
			return Canvas.GAME_D;
		case Constants.KEYCODE_LEFT:
			return Canvas.LEFT;
		case Constants.KEYCODE_RIGHT:
			return Canvas.RIGHT;
		case Constants.KEYCODE_UP:
			return Canvas.UP;
		default:
			return -1;
		}
	}

	public int getKeyCode(int gameAction) {
		switch (gameAction) {
		case Canvas.DOWN:
			return Constants.KEYCODE_DOWN;
		case Canvas.FIRE:
			return Constants.KEYCODE_SELECT;
		case Canvas.GAME_A:
			return SDLKey.SDLK_a;
		case Canvas.GAME_B:
			return SDLKey.SDLK_b;
		case Canvas.GAME_C:
			return SDLKey.SDLK_c;
		case Canvas.GAME_D:
			return SDLKey.SDLK_d;
		case Canvas.LEFT:
			return Constants.KEYCODE_LEFT;
		case Canvas.RIGHT:
			return Constants.KEYCODE_RIGHT;
		case Canvas.UP:
			return Constants.KEYCODE_UP;
		default:
			return 0;
		}
	}

	public String getKeyName(int keyCode) {
		return SDLEvent.getKeyName(keyCode);
	}

	public int getSystemKey(int keyCode) {
		switch (keyCode) {
		case SDLKey.SDLK_DELETE:
			return EventConstants.SYSTEM_KEY_CLEAR;
		case SDLKey.SDLK_END:
			return EventConstants.SYSTEM_KEY_END;
		case SDLKey.SDLK_POWER:
			return EventConstants.SYSTEM_KEY_POWER;
		case SDLKey.SDLK_RETURN:
			return EventConstants.SYSTEM_KEY_SEND;
		default:
			return 0;
		}
	}

	static int mapToInternalEvent(int keyCode, char c) {
		switch (keyCode) {
		case SDLKey.SDLK_DOWN:
			return Constants.KEYCODE_DOWN;
		case SDLKey.SDLK_LEFT:
			return Constants.KEYCODE_LEFT;
		case SDLKey.SDLK_RIGHT:
			return Constants.KEYCODE_RIGHT;
		case SDLKey.SDLK_RETURN:
			return Constants.KEYCODE_SELECT;
		case SDLKey.SDLK_UP:
			return Constants.KEYCODE_UP;
		case SDLKey.SDLK_F1:
			return EventConstants.SOFT_BUTTON1;
		case SDLKey.SDLK_F2:
			return EventConstants.SOFT_BUTTON2;
		case SDLKey.SDLK_1:
			if (Character.isDigit(c)) {
				return Canvas.KEY_NUM1;
			}
			return 0;
		case SDLKey.SDLK_2:
			if (Character.isDigit(c)) {
				return Canvas.KEY_NUM2;
			}
			return 0;
		case SDLKey.SDLK_3:
			if (Character.isDigit(c)) {
				return Canvas.KEY_NUM3;
			}
			return 0;
		case SDLKey.SDLK_4:
			if (Character.isDigit(c)) {
				return Canvas.KEY_NUM4;
			}
			return 0;
		case SDLKey.SDLK_5:
			if (Character.isDigit(c)) {
				return Canvas.KEY_NUM5;
			}
			return 0;
		case SDLKey.SDLK_6:
			if (Character.isDigit(c)) {
				return Canvas.KEY_NUM6;
			}
			return 0;
		case SDLKey.SDLK_7:
			if (Character.isDigit(c)) {
				return Canvas.KEY_NUM7;
			}
			return 0;
		case SDLKey.SDLK_8:
			if (Character.isDigit(c)) {
				return Canvas.KEY_NUM8;
			}
			return 0;
		case SDLKey.SDLK_9:
			if (Character.isDigit(c)) {
				return Canvas.KEY_NUM9;
			}
			return 0;
		case SDLKey.SDLK_0:
			if (Character.isDigit(c)) {
				return Canvas.KEY_NUM0;
			}
			return 0;
		case SDLKey.SDLK_KP_MULTIPLY:
		case SDLKey.SDLK_ASTERISK:
			return Canvas.KEY_STAR;
		case SDLKey.SDLK_HASH:
			return Canvas.KEY_POUND;
		case SDLKey.SDLK_END:
			return SDLKey.SDLK_END;
		case SDLKey.SDLK_F12:
			return SDLKey.SDLK_F12;
		default:
			return 0;
		}
	}

	/*
	 *     
	 SDLK_BACKSPACE: retroceso
	 SDLK_TAB: tabulador
	 SDLK_CLEAR: clear
	 SDLK_RETURN: return
	 SDLK_PAUSE: pausa
	 SDLK_ESCAPE: escape
	 SDLK_SPACE: espacio
	 SDLK_EXCLAIM: '!' exclamación
	 SDLK_QUOTEDBL: '"' dobles comillas
	 SDLK_HASH: '#' hash
	 SDLK_DOLLAR: '$' dollar
	 SDLK_AMPERSAND: '&' ampersand
	 SDLK_QUOTE: ''' comillas simplesSDLK_LEFTPAREN: '(' paréntesis izquierdo
	 SDLK_RIGHTPAREN: ')' paréntesis derecho
	 SDLK_ASTERISK: '*' asterisco
	 SDLK_PLUS: '+' signo de suma
	 SDLK_COMMA: ',' coma
	 SDLK_MINUS: '-' signo menos
	 SDLK_PERIOD: '.' periodo
	 SDLK_SLASH: '/ barra derecha
	 SDLK_0: '0' número 0. Todos los números hasta el 9 llevan la misma forma.
	 SDLK_COLON: ':' dos puntos
	 SDLK_SEMICOLON: ';' punto y coma
	 SDLK_LESS: '<' signo menor que
	 SDLK_EQUALS: '=' signo igual
	 SDLK_GREATER: '>' signo mayor que
	 SDLK_QUESTION: '?' signo de interrogación
	 SDLK_AT: '@' arroba
	 SDLK_LEFTBRACKET: '[' corchete izquierdo
	 SDLK_BACKSLASH: '\' barra izquierda
	 SDLK_RIGHTBRACKET: ']' corchete derecho
	 SDLK_CARET: '^' caret
	 SDLK_UNDERSCORE: '_' guión bajo
	 SDLK_BACKQUOTE: '`' grave
	 SDLK_a: 'a' a. Todas las letras llevan la misma forma.
	 SDLK_DELETE: '^?' delete
	 SDLK_KP0: keypad 0. Todos los números del keypad hasta el 9 llevan la misma forma.
	 SDLK_KP_PERIOD: '.' periodo del keypad
	 SDLK_KP_DIVIDE: '/' división del keypad
	 SDLK_KP_MULTIPLY: '' multiplicación del keypad
	 SDLK_KP_MINUS: '-' signo menos del keypad
	 SDLK_KP_PLUS: '+' signo más del keypad
	 SDLK_KP_ENTER: '\r' enter del keypad
	 SDLK_KP_EQUALS: '=' signo igual del keypad
	 SDLK_UP: cursor arribaSDLK_DOWN: cursor abajo
	 SDLK_RIGHT: cursor derecha
	 SDLK_LEFT: cursor izquierdaSDLK_INSERT: tecla insert
	 SDLK_HOME: tecla inicio
	 SDLK_END: tecla fin
	 SDLK_PAGEUP: tecla avanzar página
	 SDLK_PAGEDOWN: tecla retroceder página
	 SDLK_F1: F1. Todas las funciones "F" llevan la misma forma.
	 SDLK_NUMLOCK: bloqueo numérico
	 SDLK_CAPSLOCK: bloqueo mayúsculas
	 SDLK_SCROLLOCK: scrollock
	 SDLK_RSHIFT: shift derecho
	 SDLK_LSHIFT: shift izquierdo
	 SDLK_RCTRL: ctrl derecho
	 SDLK_LCTRL: ctrl izquierdo
	 SDLK_RALT: alt derecho
	 SDLK_LALT: alt izquierdo
	 SDLK_RMETA: meta derecho    
	 SDLK_RMETA: meta derecho    
	 SDLK_LMETA: meta izquierdo
	 SDLK_LSUPER: tecla windows izquierda    
	 SDLK_RSUPER: tecla windows derecha    
	 SDLK_MODE: mode shift 
	 SDLK_HELP: ayuda    
	 SDLK_PRINT: imprimir    
	 SDLK_SYSREQ: SysRq
	 SDLK_BREAK: break
	 SDLK_MENU: menu
	 SDLK_POWER: power
	 SDLK_EURO: euro
	 */

}
