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
package org.thenesis.midpath.demo.ui.choicegroup;

import java.io.InputStream;

import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;


/**
 * The alert demo displays a list of alerts that will be displayed once the
 * user clicks a list item. These alerts try to present the full range of
 * alert types supported in MIDP.
 *
 * @version 2.0
 */
public class ChoiceGroupDemo extends MIDlet implements CommandListener {
    private static final Command CMD_EXIT = new Command("Exit", Command.EXIT, 1);
    private Display display;
    private boolean firsttime;
    private Form mainForm;

    public ChoiceGroupDemo() {
        firsttime = true;
    }

    protected void startApp() {
        if (firsttime) {
            display = Display.getDisplay(this);

            mainForm = new Form("Choice Group");
            mainForm.append("These are the available choice group types");

            // these are the images and strings for the choices.
            Image[] imageArray = null;

            try {
                // load the duke image to place in the image array
            	InputStream is = getClass().getResourceAsStream("Duke.png");
                Image duke = Image.createImage(is);

                // these are the images and strings for the choices.
                imageArray = new Image[] { duke, duke, duke, duke };
            } catch (java.io.IOException err) {
                // ignore the image loading failure the application can recover.
            }

            String[] stringArray = { "Option A", "Option B", "Option C", "Option D" };

            // create the list of choice groups.
            ChoiceGroup[] groups =
                {
                    new ChoiceGroup("Exclusive", ChoiceGroup.EXCLUSIVE, stringArray, imageArray),
                    new ChoiceGroup("Multiple", ChoiceGroup.MULTIPLE, stringArray, imageArray),
                    new ChoiceGroup("Pop-Up", ChoiceGroup.POPUP, stringArray, imageArray)
                };

            for (int iter = 0; iter < groups.length; iter++) {
                mainForm.append(groups[iter]);
            }

            mainForm.addCommand(CMD_EXIT);
            mainForm.setCommandListener(this);
            firsttime = false;
        }

        display.setCurrent(mainForm);
    }

    public void commandAction(Command c, Displayable d) {
        if (c == CMD_EXIT) {
            destroyApp(false);
            notifyDestroyed();
        }
    }

    protected void destroyApp(boolean unconditional) {
    }

    protected void pauseApp() {
    }
}
