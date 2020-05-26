/*
 * MIDPath - Copyright (C) 2006 Guillaume Legris, Mathieu Legris
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License version
 * 2 only, as published by the Free Software Foundation. 
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License version 2 for more details. 
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
package org.thenesis.midpath.ui;

import org.thenesis.microbackend.ui.BackendEventListener;
import org.thenesis.microbackend.ui.KeyConstants;
import org.thenesis.microbackend.ui.graphics.VirtualToolkit;

import com.sun.midp.events.EventMapper;
import com.sun.midp.events.EventQueue;
import com.sun.midp.events.EventTypes;
import com.sun.midp.events.NativeEvent;
import com.sun.midp.lcdui.EventConstants;
import com.sun.midp.log.Logging;
import com.sun.midp.main.Configuration;
import com.sun.midp.main.ConfigurationProperties;
import com.sun.midp.main.MIDletProxy;
import com.sun.midp.main.MIDletProxyList;
import com.sun.midp.midlet.MIDletStateHandler;

import org.thenesis.midpath.main.MIDletSettingsForm;

public class UIToolkit {

	private static UIToolkit toolkit = new UIToolkit();
	private GenericEventMapper eventMapper = new GenericEventMapper();
	private PrivateBackendEventListener listener = new PrivateBackendEventListener(eventMapper);
	
	private VirtualToolkit virtualToolkit;
	
	
	private UIToolkit() {
	}
	
	public static UIToolkit getToolkit() {
		return toolkit;
	}
	
	public VirtualToolkit getVirtualToolkit() {
        return virtualToolkit;
    }
	
	public void initialize(int w, int h) {
	   
	    String backendName = Configuration.getPropertyDefault("org.thenesis.microbackend.ui.backend", "null");
        if (Logging.TRACE_ENABLED) {
            System.out.println("[DEBUG] VirtualToolkit.initialize(): backendName: " + backendName);
        }
        
        //Copy MIDPath config in the backend config
        ConfigurationProperties properties = Configuration.getAllProperties();
        org.thenesis.microbackend.ui.Configuration backendConfig = new org.thenesis.microbackend.ui.Configuration();
        for (int i = 0; i < properties.size(); i++) {
            backendConfig.addParameter(properties.getKeyAt(i), properties.getValueAt(i));
        }
        
        // Create the backend
        //UIBackend backend = UIBackendFactory.createBackend(backendName, backendConfig, listener);
        virtualToolkit = VirtualToolkit.createToolkit(backendConfig, listener);
        virtualToolkit.initialize(null);
        
	}
	
	
	public void close() {
	    virtualToolkit.close();
	}

	public void refresh(int displayId, int x, int y, long width, long height) {
	    virtualToolkit.flushGraphics(x, y, width, height);
	}

	public EventMapper getEventMapper() {
        return eventMapper;
    }
	
}

/**
 * Listens events coming from the UIBackend and send it to the event queue
 */
class PrivateBackendEventListener implements BackendEventListener {

    private boolean dragEnabled = false;
    private GenericEventMapper eventMapper;

    private boolean startPressed = false;
    private boolean selectPressed = false;
    private boolean rightshoulderPressed = false;

    public PrivateBackendEventListener(GenericEventMapper eventMapper) {
        this.eventMapper = eventMapper;
    }

    private void fireKeyEvent(boolean pressed, int keycode, char c, int modifiers) {

        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG] VirtualBackendEventListener.fireKeyEvent: key code: " + keycode + " char: " + c);

        NativeEvent nativeEvent = new NativeEvent(EventTypes.KEY_EVENT);
        // Set event type (intParam1)
        if (pressed) {
            nativeEvent.intParam1 = EventConstants.PRESSED;
            if (keycode == KeyConstants.VK_ESCAPE) {
                selectPressed = true;
            } else if (keycode == KeyConstants.VK_ENTER) {
                startPressed = true;
            } else if (keycode == KeyConstants.VK_BACK_SPACE) {
                rightshoulderPressed = true;
            }
        } else {
            nativeEvent.intParam1 = EventConstants.RELEASED;
            if (keycode == KeyConstants.VK_ESCAPE) {
                selectPressed = false;
            } else if (keycode == KeyConstants.VK_ENTER) {
                startPressed = false;
            } else if (keycode == KeyConstants.VK_BACK_SPACE) {
                rightshoulderPressed = false;
            }
        }
        
	// quit if start + select + right shoulder buttons are pressed
	if (startPressed && selectPressed && rightshoulderPressed) {
            windowClosed();
            return;
        }

        if (startPressed && selectPressed) {
            //System.out.println("[DEBUG] VirtualBackendEventListener.fireKeyEvent: start + select pressed");
	    // Ignore this request while Suite Manager or Configurator is running
	    String classname = MIDletProxyList.getMIDletProxyList().getForegroundMIDlet().getClassName();
	    if (classname  == "org.thenesis.midpath.main.SuiteManagerMIDlet" || classname == "org.thenesis.midpath.main.ConfiguratorMIDlet") return;

	    MIDletProxyList.getMIDletProxyList().getForegroundMIDlet().pauseMidlet();
	    try {
		Configuration.load();
		MIDletSettingsForm.refreshStatics();
		MIDletStateHandler.getMidletStateHandler().startMIDlet("org.thenesis.midpath.main.ConfiguratorMIDlet", "Configurator");
		return;
	    } catch (Exception e) {
		System.out.println(e);
		e.printStackTrace();
	    }
        }
        // Filter unwanted events 
        //if ((keycode == KeyConstants.VK_SHIFT) || (keycode == KeyConstants.VK_CONTROL) || (keycode == KeyConstants.VK_ALT)) {
        //    return;
        //}
        
        // Set event key code (intParam2)
        int internalCode = eventMapper.mapToInternalEvent(keycode, c);
        nativeEvent.intParam2 = internalCode;
        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG] VirtualBackendEventListener.fireKeyEvent: key code: " + keycode + " char: " + c + " internalCode: " + internalCode);
        
        // Set event source (intParam4). Fake display with id=1
        // Hardcode does not work when Configurator midlet is shown
	// There is a very small time window when the midlet is just starting
	// up, there is no foreground midlet yet. In this case, we will get
	// a java.lang.NullPointerException. Just ignore the key event.
	try {
            nativeEvent.intParam4 = MIDletProxyList.getMIDletProxyList().getForegroundMIDlet().getDisplayId();
	} catch (Exception e) {
	    return;
	}

        EventQueue.getEventQueue().post(nativeEvent);

    }

    public void keyPressed(int keycode, char c, int modifiers) {
        fireKeyEvent(true, keycode, c, modifiers);
    }

    public void keyReleased(int keycode, char c, int modifiers) {
        fireKeyEvent(false, keycode, c, modifiers);
    }

    public void pointerMoved(int x, int y, int modifiers) {
        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG] VirtualBackendEventListener.mouseMoved(): drag enabled ? " + dragEnabled);

        if (dragEnabled) {
            NativeEvent nativeEvent = new NativeEvent(EventTypes.PEN_EVENT);
            nativeEvent.intParam1 = EventConstants.DRAGGED; // Event type
            nativeEvent.intParam2 = x; // x
            nativeEvent.intParam3 = y; // y
            // Set event source (intParam4). Fake display with id=1
            nativeEvent.intParam4 = 1;

            EventQueue.getEventQueue().post(nativeEvent);
        }

    }

    private void firePointerEvent(boolean pressed, int x, int y, int modifiers) {
        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG] VirtualBackendEventListener.fireMouseEvent()");

        NativeEvent nativeEvent = new NativeEvent(EventTypes.PEN_EVENT);

        if (pressed) {
            dragEnabled = true;
            nativeEvent.intParam1 = EventConstants.PRESSED; // Event type
        } else {
            dragEnabled = false;
            nativeEvent.intParam1 = EventConstants.RELEASED; // Event type
        }

        nativeEvent.intParam2 = x; // x
        nativeEvent.intParam3 = y; // y
        // Set event source (intParam4). Fake display with id=1
        nativeEvent.intParam4 = 1;

        EventQueue.getEventQueue().post(nativeEvent);
    }

    public void pointerPressed(int x, int y, int modifiers) {
        firePointerEvent(true, x, y, modifiers);
    }

    public void pointerReleased(int x, int y, int modifiers) {
        firePointerEvent(false, x, y, modifiers);
    }

    public void windowClosed() {
        if (Logging.TRACE_ENABLED)
            System.out.println("[DEBUG] VirtualBackendEventListener.windowClosed(): Window delete event received");

        NativeEvent nativeEvent = new NativeEvent(EventTypes.SHUTDOWN_EVENT);
        EventQueue.getEventQueue().post(nativeEvent);
    }
}
