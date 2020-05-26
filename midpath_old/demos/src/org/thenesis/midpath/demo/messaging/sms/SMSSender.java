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

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;
import javax.microedition.lcdui.TextField;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.TextMessage;


/**
 * Prompts for text and sends it via an SMS MessageConnection
 */
public class SMSSender implements CommandListener, Runnable {
    /** user interface command for indicating Send request */
    Command sendCommand = new Command("Send", Command.OK, 1);

    /** user interface command for going back to the previous screen */
    Command backCommand = new Command("Back", Command.BACK, 2);

    /** Display to use. */
    Display display;

    /** The port on which we send SMS messages */
    String smsPort;

    /** The URL to send the message to */
    String destinationAddress;

    /** Area where the user enters a message to send */
    TextBox messageBox;

    /** Where to return if the user hits "Back" */
    Displayable backScreen;

    /** Displayed when a message is being sent */
    Displayable sendingScreen;

    /**
     * Initialize the MIDlet with the current display object and
     * graphical components.
     */
    public SMSSender(String smsPort, Display display, Displayable backScreen,
        Displayable sendingScreen) {
        this.smsPort = smsPort;
        this.display = display;
        this.destinationAddress = null;
        this.backScreen = backScreen;
        this.sendingScreen = sendingScreen;

        messageBox = new TextBox("Enter Message", null, 65535, TextField.ANY);
        messageBox.addCommand(backCommand);
        messageBox.addCommand(sendCommand);
        messageBox.setCommandListener(this);
    }

    /**
     * Prompt for message and send it
     */
    public void promptAndSend(String destinationAddress) {
        this.destinationAddress = destinationAddress;
        display.setCurrent(messageBox);
    }

    /**
     * Respond to commands, including exit
     * @param c user interface command requested
     * @param s screen object initiating the request
     */
    public void commandAction(Command c, Displayable s) {
        try {
            if (c == backCommand) {
                display.setCurrent(backScreen);
            } else if (c == sendCommand) {
                display.setCurrent(sendingScreen);
                new Thread(this).start();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Send the message. Called on a separate thread so we don't have
     * contention for the display
     */
    public void run() {
        String address = destinationAddress + ":" + smsPort;

        MessageConnection smsconn = null;

        try {
            /** Open the message connection. */
            smsconn = (MessageConnection)Connector.open(address);

            TextMessage txtmessage =
                (TextMessage)smsconn.newMessage(MessageConnection.TEXT_MESSAGE);
            txtmessage.setAddress(address);
            txtmessage.setPayloadText(messageBox.getString());
            smsconn.send(txtmessage);
        } catch (Throwable t) {
            System.out.println("Send caught: ");
            t.printStackTrace();
        }

        if (smsconn != null) {
            try {
                smsconn.close();
            } catch (IOException ioe) {
                System.out.println("Closing connection caught: ");
                ioe.printStackTrace();
            }
        }
    }
}
