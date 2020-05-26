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
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Spacer;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.midlet.MIDlet;

import com.sun.midp.midletsuite.MIDletInfo;
import com.sun.midp.midlet.InternalMIDletSuiteImpl;
import com.sun.midp.i18n.Resource;
import com.sun.midp.i18n.ResourceConstants;

public class SuiteManagerMIDlet extends MIDlet implements CommandListener {

	private static final Command CMD_EXIT = new Command(Resource.getString(ResourceConstants.EXIT), Command.EXIT, 1);
	private static final Command CMD_RUN  = new Command(Resource.getString(ResourceConstants.LAUNCH), Command.ITEM, 1);
	private static final Command CMD_BACK = new Command(Resource.getString(ResourceConstants.BACK), Command.ITEM, 1);
	private static final Command CMD_CANCEL = new Command(Resource.getString(ResourceConstants.CANCEL), Command.ITEM, 1);
	private static final Command CMD_INSTALL = new Command(Resource.getString(ResourceConstants.INSTALL), Command.ITEM, 3);
	private static final Command CMD_SETTINGS = new Command(Resource.getString(ResourceConstants.APPLICATION_SETTINGS), Command.ITEM, 2);
	private static final Command CMD_SAVE = new Command(Resource.getString(ResourceConstants.SAVE), Command.ITEM, 1);

	static JarInspectorSE jarInspector;
	static MIDletInfo launchMidletInfo;

	private Display display;
	private MIDletRepository repository;
	private List installedGroup;
	private List notInstalledGroup;
	private List midletList;
	private Form loadingForm;
	private Gauge loadingGauge;
	private MIDletSettingsForm settingsForm;

	private static boolean uiBuilt = false;

	/**
	 * Signals the MIDlet to start and enter the Active state.
	 */
	protected void startApp() {
		if (uiBuilt) return;

		display = Display.getDisplay(this);

		installedGroup = new List(Resource.getString(ResourceConstants.MIDLET_MANAGER), List.IMPLICIT);
		installedGroup.addCommand(CMD_EXIT);
		installedGroup.addCommand(CMD_SETTINGS);
		installedGroup.addCommand(CMD_INSTALL);
		installedGroup.setSelectCommand(CMD_RUN);
		installedGroup.setCommandListener(this);

		notInstalledGroup = new List(Resource.getString(ResourceConstants.MIDLET_MANAGER) + " - " + Resource.getString(ResourceConstants.INSTALL), List.IMPLICIT);
		notInstalledGroup.addCommand(CMD_BACK);
		notInstalledGroup.setCommandListener(this);

		midletList = new List("MIDlets", List.IMPLICIT);
		midletList.setCommandListener(this);  
		midletList.setSelectCommand(CMD_RUN);

		loadingForm = new Form(Resource.getString(ResourceConstants.LOADING));

		repository = SuiteManager.repository;
		try {
			buildUI();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void buildUI() throws IOException {

		installedGroup.deleteAll();
		notInstalledGroup.deleteAll();
		loadingForm.deleteAll();

		repository.scanRepository();
		Vector installedList = repository.getInstalledJars();

		if (installedList.size() > 0) {
			loadingGauge = new Gauge("", false, installedList.size(), 0);
			loadingGauge.setLayout(Gauge.LAYOUT_CENTER);
			loadingForm.append(new Spacer(0, 80));
			loadingForm.append(loadingGauge);
                	display.setCurrent(loadingForm);
		}

		installedGroup.deleteAll();

		Image duke = Image.createImage(getClass().getResourceAsStream("Duke.png"));
		for (int i = 0; i < installedList.size(); i++) {
			JarInspectorSE jar = (JarInspectorSE) installedList.elementAt(i);
                        Image icon = null;
			try {
				icon = Image.createImage(jar.getIcon());
			} catch (IOException e) {
				//e.printStackTrace();
				//System.out.println("Unable to load icon from MIDlet " + jar.getSuiteName() + ". Using default.");
				icon = duke;
			}
			installedGroup.append(jar.getSuiteName(), icon);
			loadingGauge.setValue(i+1);
		}

		uiBuilt = true;

                display.setCurrent(installedGroup);

         }

	public void commandAction(Command c, Displayable d) {
	    try {	
		if (c == CMD_EXIT) {
			destroyApp(false);
			notifyDestroyed();
		} else if (c == CMD_BACK || c == CMD_CANCEL) {
			display.setCurrent(installedGroup);
		} else if (c == CMD_INSTALL) {
			if (d == installedGroup) { // build install screen
				Vector notInstalledList = repository.getNotInstalledJars();
				notInstalledGroup.deleteAll();
				for (int i = 0; i < notInstalledList.size(); i++) {
					JarInspectorSE jar = (JarInspectorSE) notInstalledList.elementAt(i);
					notInstalledGroup.append(jar.getFile().getName(), null);
				}
				// Remove install command first and test
				// whether there is MIDlet to install
				notInstalledGroup.removeCommand(CMD_INSTALL);
				if (notInstalledGroup.size() > 0) {
					notInstalledGroup.setSelectCommand(CMD_INSTALL);
				}
				display.setCurrent(notInstalledGroup);
			} else { // Install the selected midlet
				String text = Resource.getString(ResourceConstants.INSTALLING);
				Alert a = new Alert("Action", text, null, AlertType.INFO);
				display.setCurrent(a);
				int index = notInstalledGroup.getSelectedIndex();
				repository.installJar(notInstalledGroup.getString(index));
				// Refresh the installed MIDlets
				buildUI();
			}
		} else if (c == CMD_SETTINGS) {
			int index = installedGroup.getSelectedIndex();
			String id = InternalMIDletSuiteImpl.buildSuiteID(installedGroup.getString(index));
			settingsForm = new MIDletSettingsForm(id);
			settingsForm.addCommand(CMD_CANCEL);
			settingsForm.addCommand(CMD_SAVE);
			settingsForm.setCommandListener(this);
			display.setCurrent(settingsForm);
		} else if (c == CMD_SAVE) {
			settingsForm.saveConfig();
			display.setCurrent(installedGroup);
                } else if (d == installedGroup) { // CMD_RUN from here down
			midletList.deleteAll();
			int index = installedGroup.getSelectedIndex();
			jarInspector = repository.getJarFromSuiteName(installedGroup.getString(index));
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
		} else if (d == midletList) {
			int index = midletList.getSelectedIndex();
			MIDletInfo[] infos = jarInspector.getMIDletInfo();
			launchMidletInfo = infos[index];
			destroyApp(false);
			notifyDestroyed();
		}
	    } catch (IOException e) {
                e.printStackTrace();
            }
	}

	/**
	 * Signals the MIDlet to terminate and enter the Destroyed state.
	 */
	protected void destroyApp(boolean unconditional) {
		installedGroup.deleteAll();
		installedGroup = null;
		notInstalledGroup.deleteAll();
		notInstalledGroup = null;
		midletList.deleteAll();
		midletList = null;
		settingsForm = null;
	}

	/**
	 * Signals the MIDlet to stop and enter the Paused state.
	 */
	protected void pauseApp() {
	}

}
