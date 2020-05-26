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
package org.thenesis.midpath.demo.game.pushpuzzle;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.Screen;
import javax.microedition.midlet.MIDlet;


/**
 * PushPuzzle is the MIDlet that drives the game.  It puts up the
 * screens and handles all the commands that are invoked on each
 * screen.
 */
public class PushPuzzle extends MIDlet implements CommandListener {
    Display display;
    private PushPuzzleCanvas canvas;
    private Score score;
    private Screen scoreScreen;
    private Screen levelScreen;
    private Alert alert;
    private Command undoCommand = new Command("Undo", Command.BACK, 1);
    private Command restartCommand = new Command("Start Over", Command.CANCEL, 21);
    private Command exitCommand = new Command("Exit", Command.EXIT, 60);
    private Command scoresCommand = new Command("Show Scores", Command.SCREEN, 26);
    private Command okCommand = new Command("OK", Command.OK, 30);
    private Command levelCommand = new Command("Change Level", Command.SCREEN, 24);
    private Command nextCommand = new Command("Next Level", Command.SCREEN, 22);
    private Command prevCommand = new Command("Previous Level", Command.SCREEN, 23);
    private Command themeCommand = new Command("Switch Theme", Command.SCREEN, 25);

    /**
     * Creates new PushPuzzle MIDlet.
     */
    public PushPuzzle() {
        display = Display.getDisplay(this);
        score = new Score();
        canvas = new PushPuzzleCanvas(this, score);
        alert = new Alert("Warning");

        if (!score.open()) {
            System.out.println("Score open failed");
        }

        canvas.init();
        canvas.addCommand(undoCommand);
        canvas.addCommand(scoresCommand);
        canvas.addCommand(restartCommand);
        canvas.addCommand(levelCommand);
        canvas.addCommand(exitCommand);
        canvas.addCommand(nextCommand);
        canvas.addCommand(prevCommand);
        canvas.addCommand(themeCommand);
        canvas.setCommandListener(this);
    }

    /**
     * Start creates the thread to do the timing.
     * It should return immediately to keep the dispatcher
     * from hanging.
     */
    public void startApp() {
        display.setCurrent(canvas);
    }

    /**
     * Pause signals the thread to stop by clearing the thread field.
     * If stopped before done with the iterations it will
     * be restarted from scratch later.
     */
    public void pauseApp() {
    }

    /**
     * Destroy must cleanup everything.
     * Only objects exist so the GC will do all the cleanup
     * after the last reference is removed.
     */
    public void destroyApp(boolean unconditional) {
        display.setCurrent(null);
        canvas.destroy();

        if (score != null) {
            score.close();
        }
    }

    /**
     * Respond to a commands issued on any Screen
     */
    public void commandAction(Command c, Displayable s) {
        if (c == undoCommand) {
            canvas.undoMove();
        } else if (c == restartCommand) {
            canvas.restartLevel();
        } else if (c == levelCommand) {
            levelScreen = canvas.getLevelScreen();
            levelScreen.addCommand(okCommand);
            levelScreen.setCommandListener(this);
            display.setCurrent(levelScreen);
        } else if ((c == okCommand) && (s == levelScreen)) {
            if (!canvas.gotoLevel()) {
                alert.setString("Could not load level");
                display.setCurrent(alert, canvas);
            } else {
                display.setCurrent(canvas);
            }
        } else if (c == scoresCommand) {
            scoreScreen = canvas.getScoreScreen();
            scoreScreen.addCommand(okCommand);
            scoreScreen.setCommandListener(this);
            display.setCurrent(scoreScreen);
        } else if ((c == okCommand) && (s == scoreScreen)) {
            display.setCurrent(canvas);
        } else if (c == exitCommand) {
            destroyApp(false);
            notifyDestroyed();
        } else if ((c == List.SELECT_COMMAND) && (s == canvas)) {
            // Solved the level
            scoreScreen = canvas.getScoreScreen();
            scoreScreen.addCommand(okCommand);
            scoreScreen.setCommandListener(this);
            display.setCurrent(scoreScreen);

            // Read the next screen.
            canvas.nextLevel(1);
        } else if (c == nextCommand) {
            if (!canvas.nextLevel(1)) {
                alert.setString("Could not load level " + (canvas.getLevel() + 1));
                display.setCurrent(alert, canvas);
            } else {
                display.setCurrent(canvas);
            }

            if (s == canvas) {
                canvas.repaint();
            }
        } else if (c == prevCommand) {
            if (!canvas.nextLevel(-1)) {
                alert.setString("Could not load level " + (canvas.getLevel() - 1));
                display.setCurrent(alert, canvas);
            } else {
                display.setCurrent(canvas);
            }

            if (s == canvas) {
                canvas.repaint();
            }
        } else if (c == themeCommand) {
            canvas.changeTheme();
        }
    }
}
