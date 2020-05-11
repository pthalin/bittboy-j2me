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
package org.thenesis.midpath.demo.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;


/**
 * The text field demo displays all of the text field types on the screen
 * allowing the user to edit them at will.
 *
 * @version 2.0
 */
public class CommandDemo extends MIDlet implements CommandListener {
    private Command exitCommand = new Command("Exit", Command.EXIT, 1);
    private Command command1 = new Command("command 1", Command.ITEM, 2);
    private Command command2 = new Command("command 2", Command.ITEM, 2);
    private Command command3 = new Command("command 3", Command.ITEM, 2);
    private Command command4 = new Command("command 4", Command.ITEM, 2);
    private boolean firstTime;
    private Form mainForm;

    public CommandDemo() {
        firstTime = true;
        mainForm = new Form("Text Field");
    }

    protected void startApp() {
        if (firstTime) {
            mainForm.append("This demo contains text fields each one " +
                "with a different constraint");

            mainForm.append(new TextField("Any Character", "", 15, TextField.PLAIN));

            mainForm.addCommand(exitCommand);
           mainForm.addCommand(command1);
            mainForm.addCommand(command2);
            mainForm.addCommand(command3);
            mainForm.addCommand(command4);
            mainForm.setCommandListener(this);
            firstTime = false;
        }

        Display.getDisplay(this).setCurrent(mainForm);
    }

    public void commandAction(Command c, Displayable s) {
        if (c == exitCommand) {
            destroyApp(false);
            notifyDestroyed();
        }
    }

    protected void destroyApp(boolean unconditional) {
    }

    protected void pauseApp() {
    }
}
