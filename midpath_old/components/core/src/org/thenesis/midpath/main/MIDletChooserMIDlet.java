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
package org.thenesis.midpath.main;

import java.io.IOException;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.midlet.MIDlet;

import com.sun.midp.midletsuite.MIDletInfo;

import org.thenesis.midpath.main.JarInspectorME;

public class MIDletChooserMIDlet extends MIDlet implements CommandListener {

	private static final Command CMD_EXIT = new Command("Exit", Command.EXIT, 1);
	private static final Command CMD_START_MIDLET = new Command("Start", Command.ITEM, 1);

	static JarInspectorME jarInspector = MIDletLauncher.jarInspector;
	static MIDletInfo launchMidletInfo;

	private Display display;
	private List midletList;

	/**
	 * Signals the MIDlet to start and enter the Active state.
	 */
	protected void startApp() {
		display = Display.getDisplay(this);

		midletList = new List("MIDlets", List.IMPLICIT);
		midletList.addCommand(CMD_EXIT);
		//midletList.addCommand(CMD_START_MIDLET);
		midletList.setCommandListener(this);  
		midletList.setSelectCommand(CMD_START_MIDLET);

		try {
			buildUI();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void buildUI() throws IOException {

		MIDletInfo[] infos = jarInspector.getMIDletInfo();
		// If there are many MIDlets, let the user choose from a list
		if (infos.length > 1) {
			for (int i = 0; i < infos.length; i++) {
				midletList.append(infos[i].name, null);
			}
			display.setCurrent(midletList);
		} else {
			launchMidletInfo = infos[0];
			destroyApp(false);
			notifyDestroyed();
		}

	}

	public void commandAction(Command c, Displayable d) {
		
		if (c == CMD_EXIT) {
			destroyApp(false);
			notifyDestroyed();
		} else if (d == midletList) {
			try {
				int index = midletList.getSelectedIndex();
				if (index >= 0) {
					MIDletInfo[] infos = jarInspector.getMIDletInfo();
					launchMidletInfo = infos[index];
					destroyApp(false);
					notifyDestroyed();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Signals the MIDlet to terminate and enter the Destroyed state.
	 */
	protected void destroyApp(boolean unconditional) {
	}

	/**
	 * Signals the MIDlet to stop and enter the Paused state.
	 */
	protected void pauseApp() {
	}
}
