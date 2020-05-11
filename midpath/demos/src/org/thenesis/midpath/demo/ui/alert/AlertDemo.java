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
package org.thenesis.midpath.demo.ui.alert;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.ChoiceGroup;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Gauge;
import javax.microedition.midlet.MIDlet;


/**
 * The alert demo displays a list of alerts that will be displayed once the
 * user clicks a list item. These alerts try to present the full range of
 * alert types supported in MIDP.
 *
 * @version 2.0
 */
public class AlertDemo extends MIDlet {
    private static final Command CMD_EXIT = new Command("Exit", Command.EXIT, 1);
    private static final Command CMD_SHOW = new Command("Show", Command.SCREEN, 1);
    private static final String[] typeStrings =
        { "Alarm", "Confirmation", "Error", "Info", "Warning" };
    private static final String[] timeoutStrings =
        { "2 Seconds", "4 Seconds", "8 Seconds", "Forever" };
    private static final int SECOND = 1000;
    private Display display;
    private boolean firstTime;
    private Form mainForm;

    public AlertDemo() {
        firstTime = true;
        mainForm = new Form("Alert Options");
    }

    protected void startApp() {
        display = Display.getDisplay(this);
        showOption();
    }

    /**
     * Creates the main display of the MIDlet.
     * In this form the user will choose the properties of the alert
     */
    private void showOption() {
        if (firstTime) {
            // choice-group for the type of the alert:
            // "Alarm", "Confirmation", "Error", "Info" or  "Warning"
            ChoiceGroup types = new ChoiceGroup("Type", ChoiceGroup.POPUP, typeStrings, null);
            mainForm.append(types);

            // choice-group for the timeout of the alert:
            // "2 Seconds", "4 Seconds", "8 Seconds" or "Forever"
            ChoiceGroup timeouts =
                new ChoiceGroup("Timeout", ChoiceGroup.POPUP, timeoutStrings, null);
            mainForm.append(timeouts);

            // a check-box to add an indicator to the alert
            String[] optionStrings = { "Show Indicator" };
            ChoiceGroup options = new ChoiceGroup("Options", Choice.MULTIPLE, optionStrings, null);
            mainForm.append(options);
            mainForm.addCommand(CMD_SHOW);
            mainForm.addCommand(CMD_EXIT);
            mainForm.setCommandListener(new AlertListener(types, timeouts, options));
            firstTime = false;
        }

        display.setCurrent(mainForm);
    }

    protected void destroyApp(boolean unconditional) {
    }

    protected void pauseApp() {
    }

    /**
     * Creates the alert's indicator.
     * If there is no timeout (maxValue == Alert.FOREVER), the indicator will be
     * an "indefinite-running" gauge.
     * If there is a timeout, the indicator will be a "non-interactive" gauge
     * that is updated by a background thread.
     */
    private Gauge createIndicator(int maxValue) {
        if (maxValue == Alert.FOREVER) {
            return new Gauge(null, false, Gauge.INDEFINITE, Gauge.CONTINUOUS_RUNNING);
        }

        final int max = maxValue / SECOND;
        final Gauge indicator = new Gauge(null, false, max, 0);

        //        if (maxValue != Gauge.INDEFINITE) {
        new Thread() {
                public void run() {
                    int value = 0;

                    while (value < max) {
                        indicator.setValue(value);
                        ++value;

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ie) {
                            // ignore
                        }
                    }
                }
            }.start();

        //        }
        return indicator;
    }

    private class AlertListener implements CommandListener {
        AlertType[] alertTypes =
            {
                AlertType.ALARM, AlertType.CONFIRMATION, AlertType.ERROR, AlertType.INFO,
                AlertType.WARNING
            };
        ChoiceGroup typesCG;
        int[] timeouts = { 2 * SECOND, 4 * SECOND, 8 * SECOND, Alert.FOREVER };
        ChoiceGroup timeoutsCG;
        ChoiceGroup indicatorCG;

        public AlertListener(ChoiceGroup types, ChoiceGroup timeouts, ChoiceGroup indicator) {
            typesCG = types;
            timeoutsCG = timeouts;
            indicatorCG = indicator;
        }

        public void commandAction(Command c, Displayable d) {
            if (c == CMD_SHOW) {
                int typeIndex = typesCG.getSelectedIndex();
                Alert alert = new Alert("Alert");
                alert.setType(alertTypes[typeIndex]);

                int timeoutIndex = timeoutsCG.getSelectedIndex();
                alert.setTimeout(timeouts[timeoutIndex]);
                alert.setString(typeStrings[typeIndex] + " Alert, Running " +
                    timeoutStrings[timeoutIndex]);

                boolean[] SelectedFlags = new boolean[1];
                indicatorCG.getSelectedFlags(SelectedFlags);

                if (SelectedFlags[0]) {
                    Gauge indicator = createIndicator(timeouts[timeoutIndex]);
                    alert.setIndicator(indicator);
                }

                display.setCurrent(alert);
            } else if (c == CMD_EXIT) {
                destroyApp(false);
                notifyDestroyed();
            }
        }
    }
}
