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
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Vector;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.List;
import javax.microedition.location.AddressInfo;
import javax.microedition.location.Coordinates;
import javax.microedition.location.Criteria;
import javax.microedition.location.Landmark;
import javax.microedition.location.LandmarkStore;
import javax.microedition.location.LocationException;
import javax.microedition.location.LocationProvider;
import javax.microedition.location.QualifiedCoordinates;
import javax.microedition.midlet.*;


/**
 * Main CityGuide MIDlet
 */
public class CityGuideMIDlet extends MIDlet implements CommandListener {
    /** landmark store name to store points of interest */
    private static final String LANDMARKSTORE_NAME = "waypoints";

    /** welcome text for welcome screen */
    private static final String WELCOME_TEXT =
        "Welcome to The City Guide. First JSR179 enabled city guide." +
        " Select your favorite landmark categories and never miss interesting place while walking through the city." +
        " Don't forget to run the citywalk.xml script.\n\n" + "Enjoy the show!\n\n";

    /** explanation label for settings screen */
    private static final String SETTINGS_TEXT =
        "The City Guide will alert you whenever you get close to landmark of selected category.";

    /** choice group label for categories */
    private static final String CHOICEGRP_TEXT = "Watch for categories:";

    /** no landmark in proximity */
    private static final String NOLANDMARK_TEXT =
        "Sorry no landmark in proximity now.\nKeep on walking.";

    /** map corners coordinates */
    private static final Coordinates MAP_TOP_LEFT_COORDINATES =
        new Coordinates(50.1049422484619, 14.387594174164514, 310);
    private static final Coordinates MAP_BOTTOM_RIGHT_COORDINATES =
        new Coordinates(50.09531507039965, 14.40423976700292, 310);

    /** initial visitor coordinates */
    private static final Coordinates MAP_VISITOR_COORDINATES =
        new Coordinates(50.09985002736201, 14.389796708964603, 310);
    private static final String[] PRELOAD_IMAGES =
        {
            "map", "visitoron", "visitoroff", "anim1", "anim2", "anim3", "anim4", "anim5", "anim6",
            "anim7", "anim8", "logo"
        };
    private static CityGuideMIDlet instance;

    /** error screen uses last error occurred */
    private String lastError = "";

    /** list of selected categories  */
    private Vector selectedCategories;

    /** landmark store containing points of interest */
    private LandmarkStore landmarkStore = null;

    /** location provider */
    private LocationProvider locationProvider = null;

    /** distance when proximity event is sent */
    private float proximityRadius = 50.0f;

    /** image cache */
    private ImageManager imageManager = null;

    /** landmarks successfully loaded */
    private boolean landmarksLoaded = false;

    /** main MIDlet display */
    private Display display;

    /** map canvas */
    private MapCanvas mapCanvas;

    /** city map */
    private CityMap cityMap;

    /** Screens and commands */
    private Command WelcomeScreen_nextCommand;
    private Command ProgressScreen_nextCommand;
    private Command showErrorCommand;
    private Command SettingsScreen_backCommand;
    private Command DetailsScreen_backCommand;
    private Command exitCommand;
    private Command closeCommand;
    private final Command detailsCommand;
    private final Command settingsCommand;
    private Gauge progressGauge;
    private Form progressScreen;
    private Form welcomeScreen;
    private Form settingsScreen;
    private Form detailsScreen;
    private Alert errorAlert;

    /**
     * Main City Guide MIDlet
     */
    public CityGuideMIDlet() {
        exitCommand = new Command("Exit", Command.EXIT, 1);
        closeCommand = new Command("Close", Command.SCREEN, 1);
        showErrorCommand = new Command("Error", Command.SCREEN, 1);
        detailsCommand = new Command("Detail", Command.SCREEN, 1);
        settingsCommand = new Command("Settings", Command.SCREEN, 1);
        display = Display.getDisplay(this);
        imageManager = ImageManager.getInstance();
        imageManager.getImage("logo");
        createLocationProvider();

        if (locationProvider == null) {
            System.out.println("Cannot run without location provider!");
            destroyApp(false);
            notifyDestroyed();
        }

        instance = this;
    }

    public static CityGuideMIDlet getInstance() {
        if (instance == null) {
            instance = new CityGuideMIDlet();
        }

        return instance;
    }

    public void startApp() {
        if (display.getCurrent() == null) {
            // startApp called for the first time
            showWelcomeScreen();
        } else {
            if (cityMap != null) {
                cityMap.enable();
            }
        }
    }

    public void pauseApp() {
        if (cityMap != null) {
            cityMap.disable();
        }
    }

    public void destroyApp(boolean unconditional) {
        if (cityMap != null) {
            cityMap.cleanup();
        }
    }

    /**
     * Action handler
     */
    public void commandAction(Command c, Displayable s) {
        if (c == detailsCommand) {
            showDetailsScreen();
        }

        if (c == settingsCommand) {
            showSettingsScreen(true);
        }

        if (c == WelcomeScreen_nextCommand) {
            if (landmarkStore == null) {
                showProgressScreen("Loading landmarks ...", 20);
            }

            //load landmarks from resources
            Thread loadLandmarksThread =
                new Thread() {
                    public void run() {
                        loadLandmarks();
                    }
                };

            loadLandmarksThread.start();

            //load images from resources
            Thread loadImagesThread =
                new Thread() {
                    public void run() {
                        loadImages();
                    }
                };

            loadImagesThread.start();
        }

        if (c == ProgressScreen_nextCommand) {
            //initialize categories
            showSettingsScreen(false);
            //update map display with changed set of selected categories
            selectedCategoriesChanged();
            //initialize map display
            showMapCanvas(true);
        }

        if (c == SettingsScreen_backCommand) {
            selectedCategoriesChanged();
            showMapCanvas(true);
        }

        if (c == DetailsScreen_backCommand) {
            showMapCanvas(false);
        }

        if (c == showErrorCommand) {
            showErrorForm(lastError);
        }

        if (c == closeCommand) {
            if (display.getCurrent() == errorAlert) {
                display.setCurrent(welcomeScreen);
            }
        }

        if (c == exitCommand) {
            if ((display.getCurrent() == settingsScreen) || (display.getCurrent() == mapCanvas)) {
                display.setCurrent(welcomeScreen);
            }

            if (display.getCurrent() == welcomeScreen) {
                destroyApp(false);
                notifyDestroyed();
            }
        }
    }

    /**
     * Initializes LocationProvider
     * uses default criteria
     */
    void createLocationProvider() {
        if (locationProvider == null) {
            Criteria criteria = new Criteria();

            try {
                locationProvider = LocationProvider.getInstance(criteria);
            } catch (LocationException le) {
                System.out.println("Cannot create LocationProvider for this criteria.");
                le.printStackTrace();
            }
        }
    }

    private void generateAndDisplayMap() {
        if (cityMap == null) {
            try {
                cityMap = new CityMap(new String[] { "map", "visitoron", "visitoroff" },
                        MAP_TOP_LEFT_COORDINATES, MAP_BOTTOM_RIGHT_COORDINATES,
                        MAP_VISITOR_COORDINATES, selectedCategories, ImageManager.getInstance(),
                        LandmarkStore.getInstance(LANDMARKSTORE_NAME),
                        LocationProvider.getInstance(new Criteria()));
            } catch (LocationException e) {
                e.printStackTrace();
                destroyApp(false);
                notifyDestroyed();

                return;
            }
        } else {
            cityMap.setCategories(selectedCategories);
        }

        if (mapCanvas == null) {
            mapCanvas = new MapCanvas(cityMap);
            mapCanvas.addCommand(exitCommand);
            mapCanvas.addCommand(detailsCommand);
            mapCanvas.addCommand(settingsCommand);
            mapCanvas.setCommandListener(this);
        }

        display.setCurrent(mapCanvas);
    }

    /**
     * Initializes map display and sets listener for location updates
     * use LocationProvider defaults
     */
    public void showMapCanvas(boolean updateLandmarks) {
        if (updateLandmarks || (mapCanvas == null)) {
            Form progressForm = new Form(null);
            progressForm.append(new StringItem("Generating map...", null));
            display.setCurrent(progressForm);

            Thread mapGeneratorThread =
                new Thread() {
                    public void run() {
                        generateAndDisplayMap();
                    }
                };

            mapGeneratorThread.start();
        } else {
            display.setCurrent(mapCanvas);
        }
    }

    /**
     * Show error screen with last error
     */
    public void showErrorForm(String message) {
        if (errorAlert == null) {
            errorAlert = new Alert("Error", message, null, AlertType.ERROR);
            errorAlert.addCommand(closeCommand);
            errorAlert.setCommandListener(this);
        }

        errorAlert.setString(message);
        display.setCurrent(errorAlert);
    }

    /**
     * Progress screen while loading landmarks
     * Especially while creating new landmark store from
     * text resource file
     */
    public void showProgressScreen(String title, int size) {
        if (progressGauge == null) {
            progressGauge = new Gauge(title, false, size, 0);
            progressGauge.setLayout(Item.LAYOUT_CENTER | Item.LAYOUT_EXPAND | Item.LAYOUT_VCENTER);
        }

        progressGauge.setValue(0);

        if (progressScreen == null) {
            progressScreen = new Form(null);
            progressScreen.append(progressGauge);
            progressScreen.addCommand(exitCommand);
            ProgressScreen_nextCommand = new Command("Next", Command.SCREEN, 1);
            progressScreen.setCommandListener(this);
        }

        display.setCurrent(progressScreen);
    }

    /**
     * Settings screen let user select categories
     * of points of interest he/she is interested in.
     * Only those will be displayed on the map.
     * Contains choice group of categories
     */
    public void showSettingsScreen(boolean show) {
        if (settingsScreen == null) {
            String[] categories = Util.asArray(landmarkStore.getCategories());
            Image[] images = new Image[categories.length];

            for (int i = 0; i < categories.length; i++)
                images[i] = imageManager.getImage(categories[i]);

            Item[] items =
                new Item[] {
                    new StringItem(null, SETTINGS_TEXT),
                    new ChoiceGroup(CHOICEGRP_TEXT, ChoiceGroup.MULTIPLE, categories, images)
                };
            settingsScreen = new Form("Settings", items);

            boolean[] flags = new boolean[categories.length];

            for (int i = 0; i < flags.length; i++) {
                flags[i] = true;
            }

            ((ChoiceGroup)settingsScreen.get(1)).setSelectedFlags(flags);
            SettingsScreen_backCommand = new Command("Back", Command.SCREEN, 1);
            settingsScreen.addCommand(SettingsScreen_backCommand);
            settingsScreen.setCommandListener(this);
        }

        if (show) {
            display.setCurrent(settingsScreen);
        }
    }

    /**
     * Details screen contains detailed info about
     * landmarks. It's used for landmarks which
     * appear in proximity radius.
     */
    public void showDetailsScreen() {
        MapLandmark[] mapLandmarks = cityMap.getMapLandmarks();
        int numActive = 0;

        for (int i = 0; i < mapLandmarks.length; ++i) {
            if (mapLandmarks[i].isActive()) {
                ++numActive;
            }
        }

        Item[] items = null;

        if (numActive == 0) {
            items = new Item[] { new StringItem(null, NOLANDMARK_TEXT) };
        } else {
            int NUMBER_OF_ITEMS = 7;
            items = new Item[numActive * NUMBER_OF_ITEMS];

            Landmark l = null;
            AddressInfo address = null;
            int i = 0;

            for (int j = 0; j < mapLandmarks.length; ++j) {
                if (mapLandmarks[j].isActive()) {
                    l = (Landmark)mapLandmarks[j].getLandmark();
                    address = l.getAddressInfo();
                    items[i] = new StringItem("Name:", l.getName());
                    items[i + 1] = new StringItem("Description:", l.getDescription());
                    items[i + 2] = new StringItem("Street:", address.getField(AddressInfo.STREET));
                    items[i + 3] = new StringItem("Postal Code:",
                            address.getField(AddressInfo.POSTAL_CODE));
                    items[i + 4] = new StringItem("City:", address.getField(AddressInfo.CITY));
                    items[i + 5] = new StringItem("Phone No:",
                            address.getField(AddressInfo.PHONE_NUMBER));
                    items[i + 6] = new StringItem(" ", " ");
                    i += NUMBER_OF_ITEMS;
                }
            }
        }

        detailsScreen = new Form("Details", items);
        DetailsScreen_backCommand = new Command("Back", Command.SCREEN, 1);
        detailsScreen.addCommand(DetailsScreen_backCommand);
        detailsScreen.setCommandListener(this);
        display.setCurrent(detailsScreen);
    }

    /**
     * Simple welcome screen
     */
    public void showWelcomeScreen() {
        if (welcomeScreen == null) {
            Item[] items =
                new Item[] {
                    new StringItem(null, WELCOME_TEXT),
                    new ImageItem(null, imageManager.getImage("logo"), Item.LAYOUT_CENTER, "logo"),
                };
            welcomeScreen = new Form("City Guide", items);
            WelcomeScreen_nextCommand = new Command("Next", Command.SCREEN, 2);
            welcomeScreen.addCommand(exitCommand);
            welcomeScreen.addCommand(WelcomeScreen_nextCommand);
            welcomeScreen.setCommandListener(this);
        }

        display.setCurrent(welcomeScreen);
    }

    /** is map display on */
    public boolean isMapDisplayed() {
        return (display.getCurrent() == mapCanvas);
    }

    /** which categories are selected */
    public Vector getSelectedCategories() {
        return selectedCategories;
    }

    /**
     * Update map display - categories have changed
     */
    public void selectedCategoriesChanged() {
        selectedCategories = new Vector();

        ChoiceGroup cg = (ChoiceGroup)settingsScreen.get(1);

        for (int i = 0; i < cg.size(); i++) {
            if (cg.isSelected(i)) {
                selectedCategories.addElement(cg.getString(i));
            }
        }
    }

    /**
     * Get landmarks of the categories in given extent
     */
    public Vector getLandmarks(String[] categories, double minLat, double minLon, double maxLat,
        double maxLon) {
        Vector landmarks = new Vector();

        for (int i = 0; i < categories.length; i++) {
            Enumeration l = null;

            try {
                l = landmarkStore.getLandmarks(categories[i], minLat, minLon, maxLat, maxLon);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            for (; l.hasMoreElements();) {
                landmarks.addElement(l.nextElement());
            }
        }

        return landmarks;
    }

    /**
     * Load all images
     */
    private void loadImages() {
        try {
            String name = null;

            while (!landmarksLoaded) {
                synchronized (this) {
                    wait();
                }
            }

            imageManager.loadImagesCache(landmarkStore.getCategories());
            imageManager.loadImagesCache(PRELOAD_IMAGES);
            commandAction(ProgressScreen_nextCommand, null);
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Load landmarks from text resources
     */
    private void loadLandmarks() {
        final InputStream is = getClass().getResourceAsStream("data/waypoints.txt");

        try {
            landmarkStore = LandmarkStore.getInstance(LANDMARKSTORE_NAME);

            if (null == landmarkStore) {
                LandmarkStore.createLandmarkStore(LANDMARKSTORE_NAME);
                landmarkStore = LandmarkStore.getInstance(LANDMARKSTORE_NAME);
                Util.readLandmarksFromStream(landmarkStore, is, progressGauge);
            }

            landmarksLoaded = true;

            synchronized (this) {
                notify();
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
            lastError = "Cannot read landmarks.\n Landmark store wasn't created." +
                ioe.getMessage();

            try {
                LandmarkStore.deleteLandmarkStore(LANDMARKSTORE_NAME);
                commandAction(showErrorCommand, null);
            } catch (Exception e) {
                //do nothing
            }
        }
    }
}
