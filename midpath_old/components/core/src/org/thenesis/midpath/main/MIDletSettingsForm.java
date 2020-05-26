package org.thenesis.midpath.main;

import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.Vector;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;

import com.sun.midp.main.Configuration;
import com.sun.midp.main.ConfigurationProperties;
import com.sun.midp.configurator.Constants;
import com.sun.midp.i18n.Resource;
import com.sun.midp.i18n.ResourceConstants;
import com.sun.midp.chameleon.skins.resources.ScreenResources;
import com.sun.midp.chameleon.skins.resources.SoftButtonResources;

import org.thenesis.midpath.ui.GenericEventMapper;
import org.thenesis.midpath.ui.UIToolkit;
import org.thenesis.microbackend.ui.sdl.SDLBackend;
import org.thenesis.microbackend.ui.graphics.toolkit.pure.PureToolkit;


public class MIDletSettingsForm extends Form {
	private ChoiceGroup screenResolution;
	private ChoiceGroup[] key;
	private ChoiceGroup useE61Key;

	private String midletID;

	private ConfigurationProperties midletProps;

	String[] stringResolution = { "320x240", "240x320", "176x208", "176x220" };
	// resolution array in the form of {width, height, rotate(yes/no)}
	String[][] arrayResolution = { {"320", "240", "no"}, {"240", "320", "yes"}, {"176", "208", "no"}, {"176", "220", "no"} };

	String[] stringE61Key = { null };

	String[] stringHardwareKeys = {
		Resource.getString(ResourceConstants.KEY_UP),
		Resource.getString(ResourceConstants.KEY_DOWN),
		Resource.getString(ResourceConstants.KEY_LEFT),
		Resource.getString(ResourceConstants.KEY_RIGHT),
		 "A", "B", "X", "Y", "L", "R", "SELECT", "START",
		Resource.getString(ResourceConstants.KEY_UNMAPPED) };

	int[] intKeyValue = { 38, 40, 37, 39, 17, 18, 32, 16, 9, 8, 27, 10, 0 };
	// for testing on PC, SELECT = ',', START = '.'
	int[] intPCKeyValue = { 38, 40, 37, 39, 65, 66, 88, 89, 76, 82, 44, 46, 0 };
	int[] keyValueArray;

	String[] stringPhoneKeys = { 
		Resource.getString(ResourceConstants.PHONEPAD_UP),
		Resource.getString(ResourceConstants.PHONEPAD_DOWN),
		Resource.getString(ResourceConstants.PHONEPAD_LEFT),
		Resource.getString(ResourceConstants.PHONEPAD_RIGHT),
		Resource.getString(ResourceConstants.PHONEPAD_SELECT),
		Resource.getString(ResourceConstants.PHONEPAD_SOFTBUTTON1),
		Resource.getString(ResourceConstants.PHONEPAD_SOFTBUTTON2),
		Resource.getString(ResourceConstants.PHONEPAD_STAR),
		Resource.getString(ResourceConstants.PHONEPAD_POUND),
		Resource.getString(ResourceConstants.PHONEPAD_0),
		Resource.getString(ResourceConstants.PHONEPAD_1),
		Resource.getString(ResourceConstants.PHONEPAD_2),
		Resource.getString(ResourceConstants.PHONEPAD_3),
		Resource.getString(ResourceConstants.PHONEPAD_4),
		Resource.getString(ResourceConstants.PHONEPAD_5),
		Resource.getString(ResourceConstants.PHONEPAD_6),
		Resource.getString(ResourceConstants.PHONEPAD_7),
		Resource.getString(ResourceConstants.PHONEPAD_8),
		Resource.getString(ResourceConstants.PHONEPAD_9) };

	String[] stringKeyProperties = { "UP", "DOWN", "LEFT", "RIGHT", "FIRE", "SOFT_BUTTON1", "SOFT_BUTTON2", "STAR", "POUND", "NUM_0", "NUM_1", "NUM_2", "NUM_3", "NUM_4", "NUM_5", "NUM_6", "NUM_7", "NUM_8", "NUM_9" };


	public MIDletSettingsForm(String id) {
		super(Resource.getString(ResourceConstants.APPLICATION_SETTINGS) + " - " + id);
		midletID = id;

		if (System.getProperty("runtime.platform") != null) {
			keyValueArray = intPCKeyValue;
		} else {
			keyValueArray = intKeyValue;
		}

		String configPath = Configuration.getPropertyDefault("org.thenesis.midpath.main.configPath", "");
		File configFile = new File(configPath, midletID + ".cfg");
		midletProps = new ConfigurationProperties();
		if (configFile.exists()) {
			try {
				midletProps.load(new FileInputStream(configFile));
			} catch (Exception e) {
				System.out.println("Invalid config file - " + configFile);
			}
		}

		try {
			buildUI();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void buildUI() throws IOException {
		screenResolution = new ChoiceGroup(null, ChoiceGroup.EXCLUSIVE, stringResolution, null);
		String sr = midletProps.getProperty("ScreenResolution");
		for (int i=0; i<stringResolution.length; i++) {
			if (stringResolution[i].equals(sr)) {
				screenResolution.setSelectedIndex(i, true);
			}
		}

		stringE61Key[0] = Resource.getString(ResourceConstants.E61_NUMERIC_KEY);
		useE61Key = new ChoiceGroup(null, ChoiceGroup.MULTIPLE, stringE61Key, null);
		useE61Key.setSelectedIndex(0, getE61KeyFlag());

		key = new ChoiceGroup[stringPhoneKeys.length];
		for (int i=0; i<stringPhoneKeys.length; i++) {
			key[i] = new ChoiceGroup(null, ChoiceGroup.POPUP, stringHardwareKeys, null);
			key[i].setLayout(ChoiceGroup.LAYOUT_2);
			key[i].setSelectedIndex(getAssignedKey(i), true);
		}

		deleteAll();

		StringItem si = new StringItem(Resource.getString(ResourceConstants.SCREEN_RESOLUTION), null);
		si.setLayout(StringItem.LAYOUT_CENTER);
		append(si);
		append(screenResolution);
		si = new StringItem(Resource.getString(ResourceConstants.KEY_MAPPING), null);
		si.setLayout(StringItem.LAYOUT_CENTER);
		append(si);
		append(useE61Key);
		StringItem it;
		for (int i=0; i<stringPhoneKeys.length; i++) {
			it = new StringItem(stringPhoneKeys[i], null);
			it.setPreferredSize(170, 0);
			append(it);
			append(key[i]);
		}

         }

	/**
	 * Check whether to use E61 style numeric key value.
	 * return value - E61_NUMERIC_KEY flag value.
	 */
	private boolean getE61KeyFlag() {
		// first get from midlet config file
		String valueString = midletProps.getProperty("E61_NUMERIC_KEY");
		if (valueString == null) { // not in midlet config file
			valueString = Configuration.getPropertyDefault("org.thenesis.midpath.ui.keys.E61_NUMERIC_KEY", "no");
		}
		return valueString.equals("yes");
	}

	/**
	 * Returns the assigned hardware key.
	 * keyIndex - index into stringPhoneKeys or stringKeyProperties
	 * return value - index into stringHardwareKeys or intKeyValue.
	 */
	private int getAssignedKey(int keyIndex) {
		int value;

		// first get from midlet config file
		String valueString = midletProps.getProperty(stringKeyProperties[keyIndex]);
		if (valueString == null) { // not in midlet config file
			value = Configuration.getIntProperty("org.thenesis.midpath.ui.keys." + stringKeyProperties[keyIndex], 0);
		} else {
			try {
				value = Integer.parseInt(valueString);
			} catch (Exception e) {
				value = 0;
			}
		}
			
		for (int i=0; i<keyValueArray.length; i++) {
			if (value == keyValueArray[i]) return i;
		}

		return keyValueArray.length - 1; // last item is UNKNOWN
	}

	/**
	 * Save the configuration
	 */
	public void saveConfig() {

		String configPath = Configuration.getPropertyDefault("org.thenesis.midpath.main.configPath", "");
		File configFile = new File(configPath, midletID + ".cfg");

		try {
			FileWriter writer = new FileWriter(configFile);

			writer.write("ScreenResolution:" + stringResolution[screenResolution.getSelectedIndex()]);
			writer.write('\n');
			midletProps.setProperty("ScreenResolution", stringResolution[screenResolution.getSelectedIndex()]);

			writer.write("E61_NUMERIC_KEY:" + (useE61Key.isSelected(0)?"yes":"no"));
			writer.write('\n');
			midletProps.setProperty("E61_NUMERIC_KEY", useE61Key.isSelected(0)?"yes":"no");

			for (int i=0; i<stringKeyProperties.length; i++) {
				writer.write(stringKeyProperties[i] + ":" + keyValueArray[key[i].getSelectedIndex()]);
				writer.write('\n');
				midletProps.setProperty(stringKeyProperties[i], Integer.toString(keyValueArray[key[i].getSelectedIndex()]));
			}

			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setProperties() {
		int size = midletProps.size();
		for (int i = 0; i < size; i++) {
			String key = midletProps.getKeyAt(i);
			String value = midletProps.getValueAt(i);
			if (key.equals("ScreenResolution")) {
				for (int j = 0; j < stringResolution.length; j++) {
					if (value.equals(stringResolution[j])) {
						Configuration.setProperty("org.thenesis.microbackend.ui.screenWidth", arrayResolution[j][0]);
						Configuration.setProperty("org.thenesis.microbackend.ui.screenHeight", arrayResolution[j][1]);
						Configuration.setProperty("org.thenesis.microbackend.ui.sdl.rotateScreen", arrayResolution[j][2]);
					}
				}
			} else {
				Configuration.setProperty("org.thenesis.midpath.ui.keys." + key, value);
			}
		}
		refreshStatics();
	}

	public static void refreshStatics() {
		// we have to refresh all static variables affected by 
		// configuration change.
		// Statics are EVIL!!!
		GenericEventMapper.updateKeyMap();
		Constants.overrideConstants();
		ScreenResources.load(true);
		SoftButtonResources.load(true);
		Display.resetDimensions();

		ConfigurationProperties properties = Configuration.getAllProperties();
		org.thenesis.microbackend.ui.Configuration backendConfig = new org.thenesis.microbackend.ui.Configuration();
		for (int i = 0; i < properties.size(); i++) {
			backendConfig.addParameter(properties.getKeyAt(i), properties.getValueAt(i));
		}
		int width = Configuration.getIntProperty("org.thenesis.microbackend.ui.screenWidth", 320);
		int height = Configuration.getIntProperty("org.thenesis.microbackend.ui.screenHeight", 240);
		((PureToolkit)UIToolkit.getToolkit().getVirtualToolkit()).setDimensions(width, height);
		UIToolkit.getToolkit().getVirtualToolkit().getBackend().configure(backendConfig, width, height);
		((SDLBackend)(UIToolkit.getToolkit().getVirtualToolkit().getBackend())).resetRootARGBSurface();

		System.gc();
	}
}
