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
package org.thenesis.midpath.ui.toolkit.virtual;

import javax.microedition.lcdui.Canvas;

import org.thenesis.microbackend.ui.KeyConstants;

import com.sun.midp.configurator.Constants;
import com.sun.midp.events.EventMapper;
import com.sun.midp.lcdui.EventConstants;
import com.sun.midp.main.Configuration;

public class GenericEventMapper implements EventMapper {

	private static int KEY_LEFT;
	private static int KEY_UP;
	private static int KEY_RIGHT;
	private static int KEY_DOWN;
	private static int KEY_GAME_A;
	private static int KEY_GAME_B;
	private static int KEY_GAME_C;
	private static int KEY_GAME_D;
	private static int KEY_FIRE;
	private static int KEY_STAR;
	private static int KEY_POUND;
	private static int KEY_DELETE;
	private static int KEY_END;
	private static int KEY_POWER;
	private static int KEY_SOFT_BUTTON1;
	private static int KEY_SOFT_BUTTON2;

	static {
		KEY_LEFT = Configuration.getIntProperty("org.thenesis.midpath.ui.keys.LEFT", KeyConstants.VK_LEFT);
		KEY_UP = Configuration.getIntProperty("org.thenesis.midpath.ui.keys.UP", KeyConstants.VK_UP);
		KEY_RIGHT = Configuration.getIntProperty("org.thenesis.midpath.ui.keys.RIGHT", KeyConstants.VK_RIGHT);
		KEY_DOWN = Configuration.getIntProperty("org.thenesis.midpath.ui.keys.DOWN", KeyConstants.VK_DOWN);
		KEY_GAME_A = Configuration.getIntProperty("org.thenesis.midpath.ui.keys.GAME_A", KeyConstants.VK_F4);
		KEY_GAME_B = Configuration.getIntProperty("org.thenesis.midpath.ui.keys.GAME_B", KeyConstants.VK_F5);
		KEY_GAME_C = Configuration.getIntProperty("org.thenesis.midpath.ui.keys.GAME_C", KeyConstants.VK_F6);
		KEY_GAME_D = Configuration.getIntProperty("org.thenesis.midpath.ui.keys.GAME_D", KeyConstants.VK_F7);
		KEY_FIRE = Configuration.getIntProperty("org.thenesis.midpath.ui.keys.FIRE", KeyConstants.VK_ENTER);
		KEY_STAR = Configuration.getIntProperty("org.thenesis.midpath.ui.keys.STAR", KeyConstants.VK_ASTERISK);
		KEY_POUND = Configuration.getIntProperty("org.thenesis.midpath.ui.keys.POUND", KeyConstants.VK_NUMBER_SIGN);
		KEY_DELETE = Configuration.getIntProperty("org.thenesis.midpath.ui.keys.DELETE", KeyConstants.VK_BACK_SPACE);
		KEY_END = Configuration.getIntProperty("org.thenesis.midpath.ui.keys.END", KeyConstants.VK_END);
		KEY_POWER = Configuration.getIntProperty("org.thenesis.midpath.ui.keys.POWER", KeyConstants.VK_F12);
		KEY_SOFT_BUTTON1 = Configuration.getIntProperty("org.thenesis.midpath.ui.keys.SOFT_BUTTON1", KeyConstants.VK_F1);
		KEY_SOFT_BUTTON2 = Configuration.getIntProperty("org.thenesis.midpath.ui.keys.SOFT_BUTTON2", KeyConstants.VK_F2);
	}

	public int getGameAction(int keyCode) {

		if (keyCode == KEY_GAME_A)
			return Canvas.GAME_A;
		else if (keyCode == KEY_GAME_B)
			return Canvas.GAME_B;
		else if (keyCode == KEY_GAME_C)
			return Canvas.GAME_C;
		else if (keyCode == KEY_GAME_D)
			return Canvas.GAME_D;
		else if (keyCode == Constants.KEYCODE_SELECT)
			return Canvas.FIRE;
		else if (keyCode == Constants.KEYCODE_DOWN)
			return Canvas.DOWN;
		else if (keyCode == Constants.KEYCODE_LEFT)
			return Canvas.LEFT;
		else if (keyCode == Constants.KEYCODE_RIGHT)
			return Canvas.RIGHT;
		else if (keyCode == Constants.KEYCODE_UP)
			return Canvas.UP;
		else
			return -1;
	}

	public int getKeyCode(int gameAction) {
		switch (gameAction) {
		case Canvas.GAME_A:
			return KEY_GAME_A;
		case Canvas.GAME_B:
			return KEY_GAME_B;
		case Canvas.GAME_C:
			return KEY_GAME_C;
		case Canvas.GAME_D:
			return KEY_GAME_D;
		case Canvas.FIRE:
			return Constants.KEYCODE_SELECT;
		case Canvas.DOWN:
			return Constants.KEYCODE_DOWN;
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

		if (keyCode == Canvas.KEY_POUND)
			return KeyConstants.getName(KEY_POUND);
		else if (keyCode == Canvas.KEY_STAR)
			return KeyConstants.getName(KEY_STAR);
		else if (keyCode == Constants.KEYCODE_SELECT)
			return KeyConstants.getName(KEY_FIRE);
		else if (keyCode == Constants.KEYCODE_DOWN)
			return KeyConstants.getName(KEY_DOWN);
		else if (keyCode == Constants.KEYCODE_LEFT)
			return KeyConstants.getName(KEY_LEFT);
		else if (keyCode == Constants.KEYCODE_RIGHT)
			return KeyConstants.getName(KEY_RIGHT);
		else if (keyCode == Constants.KEYCODE_UP)
			return KeyConstants.getName(KEY_UP);
		else
			return KeyConstants.getName(keyCode);

	}

	public int getSystemKey(int keyCode) {
		if (keyCode == Constants.KEYCODE_DELETE)
			return EventConstants.SYSTEM_KEY_CLEAR;
		else if (keyCode == Constants.KEYCODE_END)
			return EventConstants.SYSTEM_KEY_END;
		else if (keyCode == Constants.KEYCODE_POWER)
			return EventConstants.SYSTEM_KEY_POWER;
		else if (keyCode == Constants.KEYCODE_SELECT)
			return EventConstants.SYSTEM_KEY_SEND;
		else
			return 0;
	}

	public int mapToInternalEvent(int keyCode, char c) {

		// Convert key code to an internal code
		if (keyCode == KEY_DOWN)
			return Constants.KEYCODE_DOWN;
		else if (keyCode == KEY_LEFT)
			return Constants.KEYCODE_LEFT;
		else if (keyCode == KEY_RIGHT)
			return Constants.KEYCODE_RIGHT;
		else if (keyCode == KEY_UP)
			return Constants.KEYCODE_UP;
		else if (keyCode == KEY_SOFT_BUTTON1)
			return EventConstants.SOFT_BUTTON1;
		else if (keyCode == KEY_SOFT_BUTTON2)
			return EventConstants.SOFT_BUTTON2;
		else if (keyCode == KEY_FIRE)
			return Constants.KEYCODE_SELECT;
		else if (keyCode == KEY_STAR)
			return Canvas.KEY_STAR;
		else if (keyCode == KEY_POUND)
			return Canvas.KEY_POUND;
		else if (keyCode == KEY_DELETE)
			return Constants.KEYCODE_DELETE;
		else if (keyCode == KEY_END)
			return Constants.KEYCODE_END;
		else if (keyCode == KEY_POWER)
			return Constants.KEYCODE_POWER;

		// Return the visible character
		if (c != KeyConstants.CHAR_UNDEFINED) {
			return c;
		} else {
			return keyCode;
		}

	}

}
