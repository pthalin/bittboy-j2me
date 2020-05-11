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
import javax.microedition.io.PushRegistry;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.midlet.MIDlet;
import javax.wireless.messaging.BinaryMessage;
import javax.wireless.messaging.Message;
import javax.wireless.messaging.MessageConnection;
import javax.wireless.messaging.MessageListener;
import javax.wireless.messaging.TextMessage;


/**
 * An example MIDlet displays text from an SMS MessageConnection
 */
public class SMSReceive extends MIDlet implements CommandListener, Runnable, MessageListener {
    /** user interface command for indicating Exit request. */
    Command exitCommand = new Command("Exit", Command.EXIT, 2);

    /** user interface command for indicating Reply request */
    Command replyCommand = new Command("Reply", Command.OK, 1);

    /** user interface text box for the contents of the fetched URL. */
    Alert content;

    /** current display. */
    Display display;

    /** instance of a thread for asynchronous networking and user interface. */
    Thread thread;

    /** Connections detected at start up. */
    String[] connections;

    /** Flag to signal end of processing. */
    boolean done;

    /** The port on which we listen for SMS messages */
    String smsPort;

    /** SMS message connection for inbound text messages. */
    MessageConnection smsconn;

    /** Current message read from the network. */
    Message msg;

    /** Address of the message's sender */
    String senderAddress;

    /** Alert that is displayed when replying */
    Alert sendingMessageAlert;

    /** Prompts for and sends the text reply */
    SMSSender sender;

    /** The screen to display when we return from being paused */
    Displayable resumeScreen;

    /**
     * Initialize the MIDlet with the current display object and
     * graphical components.
     */
    public SMSReceive() {
        smsPort = "5000"; //getAppProperty("SMS-Port");

        display = Display.getDisplay(this);

        content = new Alert("SMS Receive");
        content.setTimeout(Alert.FOREVER);
        content.addCommand(exitCommand);
        content.setCommandListener(this);
        content.setString("Receiving...");

        sendingMessageAlert = new Alert("SMS", null, null, AlertType.INFO);
        sendingMessageAlert.setTimeout(5000);
        sendingMessageAlert.setCommandListener(this);

        sender = new SMSSender(smsPort, display, content, sendingMessageAlert);

        resumeScreen = content;
    }

    /**
     * Start creates the thread to do the MessageConnection receive
     * text.
     * It should return immediately to keep the dispatcher
     * from hanging.
     */
    public void startApp() {
        /** SMS connection to be read. */
        String smsConnection = "sms://:" + smsPort;

        /** Open the message connection. */
        if (smsconn == null) {
            try {
                smsconn = (MessageConnection)Connector.open(smsConnection);
                smsconn.setMessageListener(this);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        /** Initialize the text if we were started manually. */
        connections = PushRegistry.listConnections(true);

        if ((connections == null) || (connections.length == 0)) {
            content.setString("Waiting for SMS on port " + smsPort + "...");
        }

        done = false;
        thread = new Thread(this);
        thread.start();

        display.setCurrent(resumeScreen);
    }

    /**
     * Notification that a message arrived.
     * @param conn the connection with messages available
     */
    public void notifyIncomingMessage(MessageConnection conn) {
        if (thread == null) {
            done = false;
            thread = new Thread(this);
            thread.start();
        }
    }

    /** Message reading thread. */
    public void run() {
        /** Check for sms connection. */
        try {
            msg = smsconn.receive();

            if (msg != null) {
                senderAddress = msg.getAddress();
                content.setTitle("From: " + senderAddress);

                if (msg instanceof TextMessage) {
                    content.setString(((TextMessage)msg).getPayloadText());
                } else {
                    StringBuffer buf = new StringBuffer();
                    byte[] data = ((BinaryMessage)msg).getPayloadData();

                    for (int i = 0; i < data.length; i++) {
                        int intData = (int)data[i] & 0xFF;

                        if (intData < 0x10) {
                            buf.append("0");
                        }

                        buf.append(Integer.toHexString(intData));
                        buf.append(' ');
                    }

                    content.setString(buf.toString());
                }

                content.addCommand(replyCommand);
                display.setCurrent(content);
            }
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

    /**
     * Pause signals the thread to stop by clearing the thread field.
     * If stopped before done with the iterations it will
     * be restarted from scratch later.
     */
    public void pauseApp() {
        done = true;
        thread = null;
        resumeScreen = display.getCurrent();
    }

    /**
     * Destroy must cleanup everything.  The thread is signaled
     * to stop and no result is produced.
     * @param unconditional true if a forced shutdown was requested
     */
    public void destroyApp(boolean unconditional) {
        done = true;
        thread = null;

        if (smsconn != null) {
            try {
                smsconn.close();
            } catch (IOException e) {
                // Ignore any errors on shutdown
            }
        }
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
            } else if (c == replyCommand) {
                reply();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Allow the user to reply to the received message
     */
    private void reply() {
        // remove the leading "sms://" for displaying the destination address
        String address = senderAddress.substring(6);
        String statusMessage = "Sending message to " + address + "...";
        sendingMessageAlert.setString(statusMessage);
        sender.promptAndSend(senderAddress);
    }
}
