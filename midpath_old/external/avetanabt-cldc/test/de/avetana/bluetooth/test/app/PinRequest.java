/*
 * Created on 03.12.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.avetana.bluetooth.test.app;

import javax.swing.JOptionPane;

/**
 * @author gmelin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PinRequest {

	/**
	 * 
	 */
	public static String requestPin () {
		return JOptionPane.showInputDialog(null, "Please enter PIN-Code", "PIN-Request", JOptionPane.QUESTION_MESSAGE);
	}

}
