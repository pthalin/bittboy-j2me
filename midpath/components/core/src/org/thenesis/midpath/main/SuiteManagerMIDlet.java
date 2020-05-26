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
import java.util.Vector;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Spacer;
import javax.microedition.midlet.MIDlet;

import com.sun.midp.midletsuite.MIDletInfo;

public class SuiteManagerMIDlet extends MIDlet implements CommandListener, ItemCommandListener {

	private static final Command CMD_EXIT = new Command("Exit", Command.EXIT, 1);
	private static final Command CMD_START_SUITE = new Command("Start", Command.ITEM, 1);
	private static final Command CMD_START_MIDLET = new Command("Start", Command.ITEM, 1);
	private static final Command CMD_UNINSTALL = new Command("Uninstall", Command.ITEM, 2);
	private static final Command CMD_REMOVE_JAR = new Command("Remove", Command.ITEM, 2);
	private static final Command CMD_INSTALL = new Command("Install", Command.ITEM, 1);

	static JarInspectorSE jarInspector;
	static MIDletInfo launchMidletInfo;

	private Display display;
	private Form mainForm;
	private MIDletRepository repository;
	private ChoiceGroup installedGroup;
	private ChoiceGroup notInstalledGroup;
	private List midletList;

	/**
	 * Signals the MIDlet to start and enter the Active state.
	 */
	protected void startApp() {
		display = Display.getDisplay(this);
		mainForm = new Form("MIDlet Manager");
		installedGroup = new ChoiceGroup("Installed", Choice.EXCLUSIVE);
		installedGroup.addCommand(CMD_UNINSTALL);
		installedGroup.setDefaultCommand(CMD_START_SUITE);
		installedGroup.setItemCommandListener(this);
		notInstalledGroup = new ChoiceGroup("Not Installed", Choice.EXCLUSIVE);
		notInstalledGroup.addCommand(CMD_REMOVE_JAR);
		notInstalledGroup.setDefaultCommand(CMD_INSTALL);
		notInstalledGroup.setItemCommandListener(this);

		midletList = new List("MIDlets", List.IMPLICIT);
		//midletList.addCommand(CMD_START_MIDLET);
		midletList.setCommandListener(this);  
		midletList.setSelectCommand(CMD_START_MIDLET);

		repository = SuiteManager.repository;
		try {
			buildUI();
		} catch (IOException e) {
			e.printStackTrace();
		}
		display.setCurrent(mainForm);

	}

	public void buildUI() throws IOException {

		repository.scanRepository();
		mainForm.deleteAll();
		installedGroup.deleteAll();
		notInstalledGroup.deleteAll();

		Vector installedList = repository.getInstalledJars();
		for (int i = 0; i < installedList.size(); i++) {
			JarInspectorSE jar = (JarInspectorSE) installedList.elementAt(i);
			try {
				installedGroup.append(jar.getSuiteName(), null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (installedGroup.size() > 0) {

			mainForm.append(installedGroup);
		}

		//put some space between the items to segregate
		Spacer spacer = new Spacer(5, 5);
		mainForm.append(spacer);

		Vector notInstalledList = repository.getNotInstalledJars();
		for (int i = 0; i < notInstalledList.size(); i++) {
			JarInspectorSE jar = (JarInspectorSE) notInstalledList.elementAt(i);
			notInstalledGroup.append(jar.getFile().getName(), null);
		}

		if (notInstalledGroup.size() > 0) {
			mainForm.append(notInstalledGroup);
		}

		mainForm.addCommand(CMD_EXIT);
		mainForm.setCommandListener(this);

	}

	public void commandAction(Command c, Item item) {

		System.out.println("commandAction " + c.getLabel());

		try {
			if (c == CMD_INSTALL) {
				String text = "Installing MIDlet Suite...";
				Alert a = new Alert("Action", text, null, AlertType.INFO);
				display.setCurrent(a);
				ChoiceGroup cg = (ChoiceGroup) item;
				int index = cg.getSelectedIndex();
				if (index >= 0) {
					repository.installJar(cg.getString(index));
				}
			} else if (c == CMD_REMOVE_JAR) {
				String text = "Removing Jar file...";
				Alert a = new Alert("Action", text, null, AlertType.INFO);
				display.setCurrent(a);
				ChoiceGroup cg = (ChoiceGroup) item;
				int index = cg.getSelectedIndex();
				if (index >= 0) {
					repository.removeJar(cg.getString(index));
				}
			} else if (c == CMD_START_SUITE) {
				midletList.deleteAll();
				ChoiceGroup cg = (ChoiceGroup) item;
				int index = cg.getSelectedIndex();
				if (index >= 0) {
					jarInspector = repository.getJarFromSuiteName(cg.getString(index));
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
			} else if (c == CMD_UNINSTALL) {
				String text = "Uninstalling MIDlet Suite...";
				Alert a = new Alert("Action", text, null, AlertType.INFO);
				display.setCurrent(a);
				ChoiceGroup cg = (ChoiceGroup) item;
				int index = cg.getSelectedIndex();
				if (index >= 0) {
					repository.uninstallSuite(cg.getString(index));
				}
			}

			buildUI();

		} catch (IOException e) {
			e.printStackTrace();
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
