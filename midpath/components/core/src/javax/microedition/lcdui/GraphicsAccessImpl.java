/*
 *
 *
 * Copyright  1990-2007 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details (a copy is
 * included at /legal/license.txt). 
 * 
 * You should have received a copy of the GNU General Public License
 * version 2 along with this work; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA 
 * 
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa
 * Clara, CA 95054 or visit www.sun.com if you need additional
 * information or have any questions. 
 */

package javax.microedition.lcdui;


/**
 * Class to access extended Image and Graphics functionality needed to
 * Chameleon, GameCanvas or to some other clients not satisified with 
 * public API
 */
class GraphicsAccessImpl implements com.sun.midp.lcdui.GraphicsAccess {

    /**
     * Get maximal width of the Graphics context
     * Whether the Graphics object was created by GameCanvas,
     * current width of the creator GameCanvas will be returned instead
     *
     * @param g The Graphics context
     * @return The width of the Graphics context
     */
    public int getGraphicsWidth(Graphics g) {
        return g.getMaxWidth();
    }

    /**
     * Get maximal height of the Graphics context
     * Whether the Graphics object was created by GameCanvas,
     * current height of the creator GameCanvas will be returned instead
     *
     * @param g The Graphics context
     * @return The height of the Graphics context
     */
    public int getGraphicsHeight(Graphics g) {
        return g.getMaxHeight();
    }

    /**
     * Get creator of the Graphics object
     * @param g Graphics object to get creator from
     * @return Graphics creator reference
     */
    public Object getGraphicsCreator(Graphics g) {
        return null; //g.getCreator();
    }

    /**
     * Set the creator of the Graphics object
     * @param g Graphics object to set creator for
     * @param creator Graphics creator reference
     */
    public void setGraphicsCreator(Graphics g, Object creator) {
        //g.setCreator(creator);
    }

    /**
     * Get current screen width dependent on rotation mode
     * @return screen width in pixels
     */
    public int getScreenWidth() {
        return Display.WIDTH;
    }

    /**
     * Get current screen height dependent on rotation mode
     * @return screen height in pixels
     */
    public int getScreenHeight() {
        return Display.HEIGHT;
    }
}
