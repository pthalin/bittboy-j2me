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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Image;
import javax.microedition.location.*;


/**
 * This class represents a map with a visitor and landmarks. It allows
 * registration of a map listener which gets information about position changes
 * of the visitor, activations and de-activations of the landmarks and changes
 * of the whole landmark set. A class instance registers itself as a location
 * listener and registers each landmark as a proximity listener, so it is
 * notified about position change and proximity events from a location provider.
 * After getting a notification from the location provider it updates its
 * internal state and delegates the notification to its own listeners.
 *
 * @version 1.3
 */
public class CityMap implements LocationListener {
    public static int X = 0;
    public static int Y = 1;
    public static int IMAGE_MAP = 0;
    public static int IMAGE_VISITOR_ON = 1;
    public static int IMAGE_VISITOR_OFF = 2;
    public static int IMAGE_LAST = IMAGE_VISITOR_OFF;
    private static final int ACTIVATION_RADIUS = 50;
    private static final int DEACTIVATION_RADIUS = 100;
    private Coordinates topLeftCoordinates;
    private Coordinates bottomRightCoordinates;
    private ImageManager imageManager;
    private LandmarkStore landmarkStore;
    private LocationProvider locationProvider;
    private Image[] images;
    private Coordinates visitorCoordinates;
    private int[] visitorXY;
    private boolean visitorActive;
    private MyMapLandmark[] landmarks;
    private Vector mapListeners;
    private boolean disabled;

    /** Creates a new instance of CityMap */
    public CityMap(String[] imageNames, Coordinates topLeftCoordinates,
        Coordinates bottomRightCoordinates, Coordinates visitorCoordinates, Vector categories,
        ImageManager imageManager, LandmarkStore landmarkStore, LocationProvider locationProvider) {
        this.topLeftCoordinates = topLeftCoordinates;
        this.bottomRightCoordinates = bottomRightCoordinates;
        this.imageManager = imageManager;
        this.landmarkStore = landmarkStore;
        this.locationProvider = locationProvider;

        this.visitorCoordinates = new Coordinates(0, 0, 0);
        this.visitorXY = new int[2];
        this.visitorActive = (locationProvider.getState() == LocationProvider.AVAILABLE);

        this.mapListeners = new Vector();

        images = new Image[IMAGE_LAST + 1];

        for (int i = 0; i <= IMAGE_LAST; ++i) {
            images[i] = imageManager.getImage(imageNames[i]);
        }

        setCategories(categories);
        setVisitorCoordinates(visitorCoordinates);

        locationProvider.setLocationListener(this, -1, -1, -1);
    }

    /**
     * Changes the landmark set to contain only landmarks of the given
     * categories.
     */
    public void setCategories(Vector categories) {
        Hashtable tmpLandmarks = new Hashtable();

        Enumeration enumCategories = categories.elements();

        while (enumCategories.hasMoreElements()) {
            String category = (String)enumCategories.nextElement();
            Enumeration categoryLandmarks = null;

            try {
                // get the landmarks of the given category from the landmark
                // store
                categoryLandmarks = landmarkStore.getLandmarks(category, null);
            } catch (IOException e) {
            }

            if (categoryLandmarks != null) {
                while (categoryLandmarks.hasMoreElements()) {
                    Landmark landmark = (Landmark)categoryLandmarks.nextElement();

                    if (!tmpLandmarks.containsKey(landmark)) {
                        // set the image of a landmark according to the first 
                        // category the landmark belongs to
                        tmpLandmarks.put(landmark, imageManager.getImage(category));
                    }
                }
            }
        }

        // unregister the old landmarks from the location provider
        synchronized (this) {
            if (landmarks != null) {
                for (int i = 0; i < landmarks.length; ++i) {
                    locationProvider.removeProximityListener(landmarks[i]);
                }
            }
        }

        MyMapLandmark[] landmarks = new MyMapLandmark[tmpLandmarks.size()];
        Enumeration enumKeys = tmpLandmarks.keys();
        Enumeration enumElements = tmpLandmarks.elements();
        int j = 0;
        int[] xy = new int[2];

        while (enumKeys.hasMoreElements()) {
            Landmark landmark = (Landmark)enumKeys.nextElement();
            Image image = (Image)enumElements.nextElement();
            // calculate the xy coordinates of a landmark
            convertCoordinatesToXY(xy, landmark.getQualifiedCoordinates());
            landmarks[j] = new MyMapLandmark(landmark, xy[X], xy[Y], image);
            ++j;
        }

        // update the set of landmarks and notify the listeners about it
        setLandmarks(landmarks);

        // register the new landmarks as proximity listeners
        synchronized (this) {
            for (int i = 0; i < landmarks.length; ++i) {
                try {
                    locationProvider.addProximityListener(landmarks[i],
                        landmarks[i].getLandmark().getQualifiedCoordinates(), ACTIVATION_RADIUS);
                } catch (LocationException e) {
                }
            }
        }
    }

    /** Updates the set of map landmarks and notifies the listeners about it. */
    private synchronized void setLandmarks(MyMapLandmark[] mapLandmarks) {
        landmarks = mapLandmarks;

        synchronized (mapListeners) {
            Enumeration listeners = mapListeners.elements();

            while (listeners.hasMoreElements()) {
                ((MapListener)listeners.nextElement()).landmarksChanged(this);
            }
        }
    }

    /**
     * Activates / deactivates the given landmark and notifies listeners about
     * it.
     */
    private synchronized void activateLandmark(MyMapLandmark landmark, boolean activate) {
        if (disabled) {
            return;
        }

        if (landmark.isActive() != activate) {
            landmark.setActive(activate);

            synchronized (mapListeners) {
                Enumeration listeners = mapListeners.elements();

                while (listeners.hasMoreElements()) {
                    ((MapListener)listeners.nextElement()).landmarkStateChanged(this, landmark);
                }
            }
        }
    }

    /**
     * Changes the coordinates of the visitor and notifies the listeners about
     * it.
     */
    public synchronized void setVisitorCoordinates(Coordinates newCoordinates) {
        if ((visitorCoordinates.getLatitude() != newCoordinates.getLatitude()) ||
                (visitorCoordinates.getLongitude() != newCoordinates.getLongitude())) {
            visitorCoordinates.setLatitude(newCoordinates.getLatitude());
            visitorCoordinates.setLongitude(newCoordinates.getLongitude());
            convertCoordinatesToXY(visitorXY, visitorCoordinates);

            synchronized (mapListeners) {
                Enumeration listeners = mapListeners.elements();

                while (listeners.hasMoreElements()) {
                    ((MapListener)listeners.nextElement()).visitorPositionChanged(this);
                }
            }

            // deactivate active landmarks, which are now too away from the 
            // visitor
            for (int i = 0; i < landmarks.length; ++i) {
                if (landmarks[i].isActive() &&
                        (newCoordinates.distance(landmarks[i].getLandmark().getQualifiedCoordinates()) > DEACTIVATION_RADIUS)) {
                    activateLandmark(landmarks[i], false);

                    try {
                        // re-register a deactivated landmark to the location
                        // provider, so we can get notified again
                        locationProvider.addProximityListener(landmarks[i],
                            landmarks[i].getLandmark().getQualifiedCoordinates(), ACTIVATION_RADIUS);
                    } catch (LocationException e) {
                    }
                }
            }
        }
    }

    /**
     * Changes the state of the visitor. A deactivated visitor doesn't change
     * his position.
     */
    public synchronized void setVisitorActive(boolean active) {
        if (visitorActive != active) {
            visitorActive = active;

            synchronized (mapListeners) {
                Enumeration listeners = mapListeners.elements();

                while (listeners.hasMoreElements()) {
                    ((MapListener)listeners.nextElement()).visitorStateChanged(this);
                }
            }
        }
    }

    /** Returns the xy coordinates of the visitor. */
    public synchronized int[] getVisitorXY(int[] dest) {
        if (dest == null) {
            dest = new int[2];
        }

        dest[X] = visitorXY[X];
        dest[Y] = visitorXY[Y];

        return dest;
    }

    /** Returns the visitor icon based on his state. */
    public Image getVisitorImage() {
        return images[visitorActive ? IMAGE_VISITOR_ON : IMAGE_VISITOR_OFF];
    }

    /** Returns the map image. */
    public Image getMapImage() {
        return images[IMAGE_MAP];
    }

    /** Returns the set of the map landmarks. */
    public MapLandmark[] getMapLandmarks() {
        return landmarks;
    }

    /** Registers a map listener. */
    void addMapListener(MapListener listener) {
        synchronized (mapListeners) {
            mapListeners.addElement(listener);
        }
    }

    /** Unregisters a map listener. */
    void removeMapListener(MapListener listener) {
        synchronized (mapListeners) {
            mapListeners.removeElement(listener);
        }
    }

    /**
     * Converts from the given latitude / longitude coordinates to the map
     * xy coordinates.
     */
    public int[] convertCoordinatesToXY(int[] dest, Coordinates coords) {
        if (dest == null) {
            dest = new int[2];
        }

        double leftLongitude = topLeftCoordinates.getLongitude();
        double rightLongitude = bottomRightCoordinates.getLongitude();
        double topLatitude = topLeftCoordinates.getLatitude();
        double bottomLatitude = bottomRightCoordinates.getLatitude();

        double normalizedX = (coords.getLongitude() - leftLongitude) / (rightLongitude - leftLongitude);
        double normalizedY =
            (coords.getLatitude() - topLatitude) / (bottomLatitude - topLatitude);

        dest[X] = (int)(normalizedX * images[IMAGE_MAP].getWidth());
        dest[Y] = (int)(normalizedY * images[IMAGE_MAP].getHeight());

        return dest;
    }

    /**
     * Converts from the given map xy coordinates to the latitude / longitude
     * coordinates.
     */
    public Coordinates convertXYToCoordinates(Coordinates dest, int[] xy) {
        double latitude =
            topLeftCoordinates.getLatitude() +
            (((bottomRightCoordinates.getLatitude() - topLeftCoordinates.getLatitude()) * xy[Y]) / images[IMAGE_MAP].getWidth());
        double longitude =
            topLeftCoordinates.getLongitude() +
            (((bottomRightCoordinates.getLongitude() - topLeftCoordinates.getLongitude()) * xy[X]) / images[IMAGE_MAP].getHeight());
        float altitude = topLeftCoordinates.getAltitude();

        if (dest == null) {
            dest = new Coordinates(latitude, longitude, altitude);
        } else {
            dest.setLatitude(latitude);
            dest.setLongitude(longitude);
            dest.setAltitude(altitude);
        }

        return dest;
    }

    /**
     * A method which is called by the location provider when the current
     * location is changed.
     */
    public synchronized void locationUpdated(LocationProvider provider, Location location) {
        if (disabled) {
            return;
        }

        Coordinates coordinates = location.getQualifiedCoordinates();
        double latitude = coordinates.getLatitude();
        double longitude = coordinates.getLongitude();
        double lat0 = topLeftCoordinates.getLatitude();
        double lat1 = bottomRightCoordinates.getLatitude();
        double lon0 = topLeftCoordinates.getLongitude();
        double lon1 = bottomRightCoordinates.getLongitude();

        if ((((latitude >= lat0) && (latitude <= lat1)) ||
                ((latitude >= lat1) && (latitude <= lat0))) &&
                (((longitude >= lon0) && (longitude <= lon1)) ||
                ((longitude >= lon1) && (longitude <= lon0)))) {
            setVisitorCoordinates(coordinates);
        }
    }

    /**
     * A method which is called by the location provider when its state changes
     * (for example, when its services are temporary unavailable).
     */
    public synchronized void providerStateChanged(LocationProvider provider, int newState) {
        if (disabled) {
            return;
        }

        setVisitorActive(newState == LocationProvider.AVAILABLE);
    }

    /**
     * Sets the city map to the disabled state. In the disabled state it ignores
     * all notifications from the location provider.
     */
    public synchronized void disable() {
        disabled = true;
    }

    /**
     * Sets the city map to the enabled state.
     */
    public synchronized void enable() {
        disabled = false;
    }

    /** The final unregistration. */
    public synchronized void cleanup() {
        for (int i = 0; i < landmarks.length; ++i) {
            locationProvider.removeProximityListener(landmarks[i]);
        }

        locationProvider.setLocationListener(null, -1, -1, -1);
    }

    /**
     * This class extends the MapLandmark class to support getting of proximity
     * events from a location provider. It ignores the monitoring state changed
     * events and delegates the proximity events to the CityMap instance.
     */
    private class MyMapLandmark extends MapLandmark implements ProximityListener {
        private int index;

        public MyMapLandmark(Landmark landmark, int x, int y, Image image) {
            super(landmark, x, y, image);
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        public void monitoringStateChanged(boolean isMonitoringActive) {
        }

        public void proximityEvent(Coordinates coordinates, Location location) {
            activateLandmark(this, true);
        }
    }
}
