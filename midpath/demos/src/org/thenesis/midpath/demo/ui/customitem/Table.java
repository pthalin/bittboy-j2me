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
package org.thenesis.midpath.demo.ui.customitem;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CustomItem;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.ItemCommandListener;


/**
 *
 * @version 2.0
 */
public class Table extends CustomItem implements ItemCommandListener {
    private static final Command CMD_EDIT = new Command("Edit", Command.ITEM, 1);
    private static final int UPPER = 0;
    private static final int IN = 1;
    private static final int LOWER = 2;
    private Display display;
    private int rows = 5;
    private int cols = 3;
    private int dx = 50;
    private int dy = 20;
    private int location = UPPER;
    private int currentX = 0;
    private int currentY = 0;
    private String[][] data = new String[rows][cols];

    // Traversal stuff     
    // indicating support of horizontal traversal internal to the CustomItem
    boolean horz;

    // indicating support for vertical traversal internal to the CustomItem.
    boolean vert;

    public Table(String title, Display d) {
        super(title);
        display = d;
        setDefaultCommand(CMD_EDIT);
        setItemCommandListener(this);

        int interactionMode = getInteractionModes();
        horz = ((interactionMode & CustomItem.TRAVERSE_HORIZONTAL) != 0);
        vert = ((interactionMode & CustomItem.TRAVERSE_VERTICAL) != 0);
    }

    protected int getMinContentHeight() {
        return (rows * dy) + 1;
    }

    protected int getMinContentWidth() {
        return (cols * dx) + 1;
    }

    protected int getPrefContentHeight(int width) {
        return (rows * dy) + 1;
    }

    protected int getPrefContentWidth(int height) {
        return (cols * dx) + 1;
    }

    protected void paint(Graphics g, int w, int h) {
        for (int i = 0; i <= rows; i++) {
            g.drawLine(0, i * dy, cols * dx, i * dy);
        }

        for (int i = 0; i <= cols; i++) {
            g.drawLine(i * dx, 0, i * dx, rows * dy);
        }

        int oldColor = g.getColor();
        g.setColor(0x00D0D0D0);
        g.fillRect((currentX * dx) + 1, (currentY * dy) + 1, dx - 1, dy - 1);
        g.setColor(oldColor);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (data[i][j] != null) {
                    // store clipping properties
                    int oldClipX = g.getClipX();
                    int oldClipY = g.getClipY();
                    int oldClipWidth = g.getClipWidth();
                    int oldClipHeight = g.getClipHeight();
                    g.setClip((j * dx) + 1, i * dy, dx - 1, dy - 1);
                    g.drawString(data[i][j], (j * dx) + 2, ((i + 1) * dy) - 2,
                        Graphics.BOTTOM | Graphics.LEFT);

                    // restore clipping properties
                    g.setClip(oldClipX, oldClipY, oldClipWidth, oldClipHeight);
                }
            }
        }
    }

    protected boolean traverse(int dir, int viewportWidth, int viewportHeight, int[] visRect_inout) {
        if (horz && vert) {
            switch (dir) {
            case Canvas.DOWN:

                if (location == UPPER) {
                    location = IN;
                } else {
                    if (currentY < (rows - 1)) {
                        currentY++;
                        repaint(currentX * dx, (currentY - 1) * dy, dx, dy);
                        repaint(currentX * dx, currentY * dy, dx, dy);
                    } else {
                        location = LOWER;

                        return false;
                    }
                }

                break;

            case Canvas.UP:

                if (location == LOWER) {
                    location = IN;
                } else {
                    if (currentY > 0) {
                        currentY--;
                        repaint(currentX * dx, (currentY + 1) * dy, dx, dy);
                        repaint(currentX * dx, currentY * dy, dx, dy);
                    } else {
                        location = UPPER;

                        return false;
                    }
                }

                break;

            case Canvas.LEFT:

                if (currentX > 0) {
                    currentX--;
                    repaint((currentX + 1) * dx, currentY * dy, dx, dy);
                    repaint(currentX * dx, currentY * dy, dx, dy);
                }

                break;

            case Canvas.RIGHT:

                if (currentX < (cols - 1)) {
                    currentX++;
                    repaint((currentX - 1) * dx, currentY * dy, dx, dy);
                    repaint(currentX * dx, currentY * dy, dx, dy);
                }
            }
        } else if (horz || vert) {
            switch (dir) {
            case Canvas.UP:
            case Canvas.LEFT:

                if (location == LOWER) {
                    location = IN;
                } else {
                    if (currentX > 0) {
                        currentX--;
                        repaint((currentX + 1) * dx, currentY * dy, dx, dy);
                        repaint(currentX * dx, currentY * dy, dx, dy);
                    } else if (currentY > 0) {
                        currentY--;
                        repaint(currentX * dx, (currentY + 1) * dy, dx, dy);
                        currentX = cols - 1;
                        repaint(currentX * dx, currentY * dy, dx, dy);
                    } else {
                        location = UPPER;

                        return false;
                    }
                }

                break;

            case Canvas.DOWN:
            case Canvas.RIGHT:

                if (location == UPPER) {
                    location = IN;
                } else {
                    if (currentX < (cols - 1)) {
                        currentX++;
                        repaint((currentX - 1) * dx, currentY * dy, dx, dy);
                        repaint(currentX * dx, currentY * dy, dx, dy);
                    } else if (currentY < (rows - 1)) {
                        currentY++;
                        repaint(currentX * dx, (currentY - 1) * dy, dx, dy);
                        currentX = 0;
                        repaint(currentX * dx, currentY * dy, dx, dy);
                    } else {
                        location = LOWER;

                        return false;
                    }
                }
            }
        } else {
            // In case of no Traversal at all: (horz|vert) == 0
        }

        visRect_inout[0] = currentX;
        visRect_inout[1] = currentY;
        visRect_inout[2] = dx;
        visRect_inout[3] = dy;

        return true;
    }

    public void setText(String text) {
        data[currentY][currentX] = text;
        repaint(currentY * dx, currentX * dy, dx, dy);
    }

    public void commandAction(Command c, Item i) {
        if (c == CMD_EDIT) {
            TextInput textInput = new TextInput(data[currentY][currentX], this, display);
            display.setCurrent(textInput);
        }
    }
}
