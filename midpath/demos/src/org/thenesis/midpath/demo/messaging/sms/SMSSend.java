/*
 *
 * Copyright (c) 2007, Sun Microsystems, Inc.
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  * Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of Sun Microsystems nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.thenesis.midpath.demo.messaging.sms;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;


/**
 * An example MIDlet to send text via an SMS MessageConnection
 */
public class SMSSend extends MIDlet implements CommandListener {
    /** user interface command for indicating Exit request. */
    Command exitCommand = new Command("Exit", Command.EXIT, 2);

    /** user interface command for proceeding to the next screen */
    Command okCommand = new Command("OK", Command.OK, 1);

    /** current display. */
    Display display;

    /** The port on which we send SMS messages */
    String smsPort;

    /** Area where the user enters the phone number to send the message to */
    TextBox destinationAddressBox;

    /** Error message displayed when an invalid phone number is entered */
    Alert errorMessageAlert;

    /** Alert that is displayed when a message is being sent */
    Alert sendingMessageAlert;

    /** Prompts for and sends the text message */
    SMSSender sender;

    /** The last visible screen when we paused */
    Displayable resumeScreen = null;

    /**
     * Initialize the MIDlet with the current display object and
     * graphical components.
     */
    public SMSSend() {
        smsPort = "5000"; //getAppProperty("SMS-Port");

        display = Display.getDisplay(this);

        destinationAddressBox = new TextBox("Destination Address?", null, 256, TextField.PHONENUMBER);
        destinationAddressBox.addCommand(exitCommand);
        destinationAddressBox.addCommand(okCommand);
        destinationAddressBox.setCommandListener(this);

        errorMessageAlert = new Alert("SMS", null, null, AlertType.ERROR);
        errorMessageAlert.setTimeout(5000);

        sendingMessageAlert = new Alert("SMS", null, null, AlertType.INFO);
        sendingMessageAlert.setTimeout(5000);
        sendingMessageAlert.setCommandListener(this);

        sender = new SMSSender(smsPort, display, destinationAddressBox, sendingMessageAlert);

        resumeScreen = destinationAddressBox;
    }

    /**
     * startApp should return immediately to keep the dispatcher
     * from hanging.
     */
    public void startApp() {
        display.setCurrent(resumeScreen);
    }

    /**
     * Remember what screen is showing
     */
    public void pauseApp() {
        resumeScreen = display.getCurrent();
    }

    /**
     * Destroy must cleanup everything.
     * @param unconditional true if a forced shutdown was requested
     */
    public void destroyApp(boolean unconditional) {
    }

    /**
     * Respond to commands, including exit
     * @param c user interface command requested
     * @param s screen object initiating the request
     */
    public void commandAction(Command c, Displayable s) {
        try {
            if ((c == exitCommand) || (c == Alert.DISMISS_COMMAND)) {
                destroyApp(false);
                notifyDestroyed();
            } else if (c == okCommand) {
                promptAndSend();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Prompt for and send the message
     */
    private void promptAndSend() {
        String address = destinationAddressBox.getString();

        if (!SMSSend.isValidPhoneNumber(address)) {
            errorMessageAlert.setString("Invalid phone number");
            display.setCurrent(errorMessageAlert, destinationAddressBox);

            return;
        }

        String statusMessage = "Sending message to " + address + "...";
        sendingMessageAlert.setString(statusMessage);
        sender.promptAndSend("sms://" + address);
    }

    /**
     * Check the phone number for validity
     * Valid phone numbers contain only the digits 0 thru 9, and may contain
     * a leading '+'.
     */
    private static boolean isValidPhoneNumber(String number) {
        char[] chars = number.toCharArray();

        if (chars.length == 0) {
            return false;
        }

        int startPos = 0;

        // initial '+' is OK
        if (chars[0] == '+') {
            startPos = 1;
        }

        for (int i = startPos; i < chars.length; ++i) {
            if (!Character.isDigit(chars[i])) {
                return false;
            }
        }

        return true;
    }
}
