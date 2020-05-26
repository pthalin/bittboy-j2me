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
package org.thenesis.midpath.demo.location;

import java.io.IOException;

import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.lcdui.*;
import javax.microedition.location.Landmark;


/**
 * This class represents a "view" of a city map. It shows a visitor and
 * landmarks.
 *
 * @version 1.3
 */
public class MapCanvas extends Canvas implements MapListener {
    private static final int X = 0;
    private static final int Y = 1;
    private static final int LANDMARK_INFO_HEIGHT = Font.getDefaultFont().getHeight() + 10;
    private CityMap cityMap;
    private MapLandmark[] mapLandmarks;
    private int[] tmpXY;
    private Timer timer;
    private MapLandmark lastActivated;

    /** The actual visitor position. */
    private int visitorX;
    private int visitorY;

    /** The last displayed visitor position. */
    private int oldVisitorX;
    private int oldVisitorY;
    private Image[] anim;
    private Image logoImage;

    /** The phase in which is the animation of active landmarks. */
    private int phase = 0;

    /**
     * Creates a new instance of MapCanvas. Register itself as a map listener
     * to the given instance of the CityMap class.
     */
    public MapCanvas(CityMap cityMap) {
        this.cityMap = cityMap;
        this.mapLandmarks = cityMap.getMapLandmarks();

        tmpXY = new int[2];

        ImageManager imageManager = ImageManager.getInstance();

        anim = new Image[8];

        for (int i = 0; i < anim.length; ++i) {
            anim[i] = imageManager.getImage("anim" + (i + 1));
        }

        logoImage = imageManager.getImage("logo");

        cityMap.getVisitorXY(tmpXY);
        visitorX = tmpXY[CityMap.X];
        visitorY = tmpXY[CityMap.Y];

        cityMap.addMapListener(this);
    }

    /**
     * Updates the animation of active landmarks.
     */
    synchronized void updateActivatedLandmarks() {
        calculateViewportOffset(tmpXY, visitorX, visitorY);

        int offsetX = tmpXY[X];
        int offsetY = tmpXY[Y];

        for (int i = 0; i < mapLandmarks.length; ++i) {
            if (mapLandmarks[i].isActive()) {
                int x0 = mapLandmarks[i].getX() - 16 - offsetX;
                int y0 = mapLandmarks[i].getY() - 16 - offsetY;
                repaint(x0, y0, 32, 32);
            }
        }
    }

    /**
     *  Calculates the viewport offset of the map according to the given
     *  visitor xy coordinates. It tries to center the visitor on the screen.
     */
    private void calculateViewportOffset(int[] xy, int visitorX, int visitorY) {
        Image mapImage = cityMap.getMapImage();

        int mapWidth = mapImage.getWidth();
        int mapHeight = mapImage.getHeight();

        int vpWidth = getWidth();
        int vpHeight = getHeight();

        int x = visitorX - (vpWidth / 2);
        int y = visitorY - (vpHeight / 2);

        if ((x + vpWidth) > mapWidth) {
            x = mapWidth - vpWidth;
        }

        if ((y + vpHeight) > mapHeight) {
            y = mapHeight - vpHeight;
        }

        if (x < 0) {
            x = 0;
        }

        if (y < 0) {
            y = 0;
        }

        xy[X] = x;
        xy[Y] = y;
    }

    public void paint(Graphics g) {
        int visitorX;
        int visitorY;
        MapLandmark[] mapLandmarks;

        synchronized (this) {
            visitorX = this.visitorX;
            visitorY = this.visitorY;
            mapLandmarks = this.mapLandmarks;
        }

        Image mapImage = cityMap.getMapImage();
        ImageManager imageManager = ImageManager.getInstance();

        calculateViewportOffset(tmpXY, visitorX, visitorY);

        int translateX = -tmpXY[X];
        int translateY = -tmpXY[Y];

        if ((visitorX != oldVisitorX) || (visitorY != oldVisitorY)) {
            g.setClip(0, 0, getWidth(), getHeight());
        }

        if (lastActivated != null) {
            // show the name of the last activated landmark
            paintLandmarkInfo(g, lastActivated.getLandmark());
            g.clipRect(0, LANDMARK_INFO_HEIGHT, getWidth(), getHeight() - LANDMARK_INFO_HEIGHT);
        }

        g.translate(translateX, translateY);

        // draw the map
        g.drawImage(mapImage, 0, 0, Graphics.LEFT | Graphics.TOP);

        int clipX0 = g.getClipX();
        int clipY0 = g.getClipY();
        int clipX1 = clipX0 + g.getClipWidth();
        int clipY1 = clipY0 + g.getClipHeight();

        // draw the images of the landmarks
        for (int i = 0; i < mapLandmarks.length; ++i) {
            int x0 = mapLandmarks[i].getX() - 16;
            int y0 = mapLandmarks[i].getY() - 16;
            int x1 = x0 + 32;
            int y1 = y0 + 32;

            if ((x1 >= clipX0) && (x0 < clipX1) && (y1 >= clipY0) && (y0 < clipY1)) {
                g.drawImage(mapLandmarks[i].getImage(), mapLandmarks[i].getX(),
                    mapLandmarks[i].getY(), Graphics.HCENTER | Graphics.VCENTER);
            }
        }

        // draw the visitor icon
        g.drawImage(cityMap.getVisitorImage(), visitorX, visitorY,
            Graphics.HCENTER | Graphics.VCENTER);

        // show the animation on the top of activate landmarks
        for (int i = 0; i < mapLandmarks.length; ++i) {
            if (mapLandmarks[i].isActive()) {
                int x0 = mapLandmarks[i].getX() - 16;
                int y0 = mapLandmarks[i].getY() - 16;
                g.drawImage(anim[phase], x0, y0, Graphics.LEFT | Graphics.TOP);
            }
        }

        g.translate(-translateX, -translateY);

        // draw the logo
        int x0 = getWidth() - logoImage.getWidth();
        int y0 = getHeight() - logoImage.getHeight();
        g.drawImage(logoImage, getWidth(), getHeight(), Graphics.BOTTOM | Graphics.RIGHT);
        g.drawRect(x0, y0, logoImage.getWidth() - 1, logoImage.getHeight() - 1);

        oldVisitorX = visitorX;
        oldVisitorY = visitorY;
    }

    /**
     * Draws the name of the given landmark in the blue rectangle with the black
     * border.
     */
    private void paintLandmarkInfo(Graphics g, Landmark l) {
        int oldColor = g.getColor();
        int width = getWidth();
        g.setColor(0, 0, 0);
        g.drawRect(0, 0, width - 1, LANDMARK_INFO_HEIGHT - 1);
        g.setColor(0x40, 0x40, 0x80);
        g.fillRect(1, 1, width - 2, LANDMARK_INFO_HEIGHT - 2);
        g.setColor(0xff, 0xff, 0xff);
        g.drawString(l.getName(), 4, 4, Graphics.LEFT | Graphics.TOP);
        g.setColor(oldColor);
    }

    /**
     * A method which is called by the city map, when the visitor changes his
     * position.
     */
    public synchronized void visitorPositionChanged(CityMap sender) {
        cityMap.getVisitorXY(tmpXY);
        visitorX = tmpXY[CityMap.X];
        visitorY = tmpXY[CityMap.Y];
        repaint();
    }

    /**
     * A method which is called by the city map, when the visitor changes his
     * state.
     */
    public void visitorStateChanged(CityMap sender) {
        repaint();
    }

    /**
     * A method which is called by the city map, when a landmark gets activated
     * or deactivated.
     */
    public synchronized void landmarkStateChanged(CityMap sender, MapLandmark mapLandmark) {
        if (mapLandmark.isActive()) {
            lastActivated = mapLandmark;
            repaint();
        } else {
            calculateViewportOffset(tmpXY, visitorX, visitorY);

            int offsetX = tmpXY[X];
            int offsetY = tmpXY[Y];
            int x0 = mapLandmark.getX() - 16 - offsetX;
            int y0 = mapLandmark.getY() - 16 - offsetY;
            repaint(x0, y0, 32, 32);

            if (lastActivated == mapLandmark) {
                lastActivated = null;
                repaint();
            }
        }
    }

    /**
     * A method which is called by the city map, when the whole set of landmarks
     * changes.
     */
    public synchronized void landmarksChanged(CityMap sender) {
        mapLandmarks = cityMap.getMapLandmarks();
        lastActivated = null;
        repaint();
    }

    /**
     * Starts an animation timer when the canvas gets visible and registers the
     * canvas instance to get the notifications from the CityMap instance.
     */
    protected void showNotify() {
        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer();
        timer.schedule(new updateTask(), 500, 500);
        cityMap.addMapListener(this);
    }

    /**
     * Stops the animation timer when the canvas gets invisible and unregisters
     * itself from receiving the notifications from the CityMap instance.
     */
    protected void hideNotify() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        cityMap.removeMapListener(this);
    }

    /** The final unregistration. */
    public void cleanup() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        cityMap.removeMapListener(this);
    }

    /**
     * This task is executed periodically to update animated parts of the
     * map.
     */
    private class updateTask extends TimerTask {
        public void run() {
            phase = (phase + 1) % anim.length;
            updateActivatedLandmarks();
        }
    }
}
