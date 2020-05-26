package org.thenesis.midpath.main;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;

import com.sun.midp.midlet.MIDletStateHandler;
import com.sun.midp.i18n.Resource;
import com.sun.midp.i18n.ResourceConstants;

public class ConfiguratorMIDlet extends MIDlet implements CommandListener {

	private static final Command CMD_CANCEL = new Command(Resource.getString(ResourceConstants.CANCEL), Command.EXIT, 1);
	private static final Command CMD_SAVE = new Command(Resource.getString(ResourceConstants.SAVE), Command.ITEM, 1);

	private Display display;
	private MIDletSettingsForm configForm;

	/**
	 * Signals the MIDlet to start and enter the Active state.
	 */
	protected void startApp() {
		display = Display.getDisplay(this);

		configForm = new MIDletSettingsForm(MIDletStateHandler.getMidletStateHandler().getMIDletSuite().getID());
                configForm.addCommand(CMD_CANCEL);
                configForm.addCommand(CMD_SAVE);
                configForm.setCommandListener(this);

               	display.setCurrent(configForm);

         }

	public void commandAction(Command c, Displayable d) {
	    try {	
		if (c == CMD_CANCEL) {
			configForm.setProperties();
			destroyApp(false);
			notifyDestroyed();
		} else if (c == CMD_SAVE) {
			configForm.saveConfig();
			configForm.setProperties();
			destroyApp(false);
			notifyDestroyed();
		}
	    } catch (Exception e) {
                e.printStackTrace();
            }
	}

	/**
	 * Signals the MIDlet to terminate and enter the Destroyed state.
	 */
	protected void destroyApp(boolean unconditional) {
		configForm = null;
	}

	/**
	 * Signals the MIDlet to stop and enter the Paused state.
	 */
	protected void pauseApp() {
	}

}
