package sdljava.event;

/**
 *  sdljava - a java binding to the SDL API
 *
 *  Copyright (C) 2004  Ivan Z. Ganza
 * 
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 * 
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 * 
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *  USA
 *
 *  Ivan Z. Ganza (ivan_ganza@yahoo.com)
 */

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import sdljava.SDLException;
import sdljava.SDLMain;

/**
 * The SDLEventManager is a thread helping us to handling SDL events. 
 * <P>
 * @author Bart LEBOEUF
 * @version $Id: SDLEventManager.java,v 1.6 2005/07/13 16:33:57 ivan_ganza Exp $ 
 */
public class SDLEventManager implements Runnable {

	/**
	 * Class Instance 
	 */
	private static SDLEventManager instance;
	/**
	 * Inner repository contains listener list by event type.
	 */
	private Hashtable repository;
	/**
	 * Internal daemon thread reference
	 */
	private Thread managerThread;
	/**
	 * Stop thread flag
	 */
	private volatile boolean isStopped;

	static {
		instance = new SDLEventManager();
	}

	/**
	 * Constructor
	 */
	private SDLEventManager() {
		repository = new Hashtable();
		isStopped = false;
	}

	/**
	 * Get instance of this class
	 * @return SDLEventManager return this class instance
	 */
	public static SDLEventManager getInstance() {
		return instance;
	}

	/**
	 * Register a listener for a list of events
	 * @param listener The class implements SDLEventListener interface
	 * @param events A list of events or event types.
	 * @return boolean return true if registration is done.
	 */
	public boolean register(SDLEventListener listener, Vector events) {
		if (listener == null || events == null)
			return false;
		Enumeration ite = events.elements();
		while (ite.hasMoreElements()) {
			Object o = ite.nextElement();
			if (SDLEvent.class.isAssignableFrom(o.getClass()))
				register(listener, o.getClass());
			else
				return false;
		}
		return true;
	}

	/**
	 * Register a listener for an event type
	 * @param listener The class implements SDLEventListener interface
	 * @param eventType An SDL event type.
	 * @return boolean return true if registration is done.
	 */
	public boolean register(SDLEventListener listener, Class eventType) {
		if (listener == null)
			return false;
		if (!SDLEvent.class.isAssignableFrom(eventType))
			return false;
		Vector list = (Vector) repository.get(eventType);
		if (list == null)
			list = new Vector();
		list.addElement(listener);
		repository.put(eventType, list);
		return true;
	}

	/**
	 * Unregister a SDLEventListener 
	 * @param listener The class implements SDLEventListener interface
	 * @return boolean return true if unregistration is done.
	 */
	public boolean unregister(SDLEventListener listener) {
		Enumeration ite = repository.elements();
		Vector list;
		//SDLEventListener l;
		while (ite.hasMoreElements()) {
			list = (Vector) ite.nextElement();
			while (list.contains(listener)) {
				list.removeElement(listener);
			}
		}
		return true;
	}

	/**
	 * Get an <code>Iterator</code> of registered listeners for a particular event type.
	 * @param eventType SDLEvent type.
	 * @return Iterator return an Iterator of listeners 
	 */
	public Enumeration getRegisteredListeners(Class eventType) {
		return ((Vector) repository.get(eventType)).elements();
	}

	/**
	 * Get an <code>Iterator</code> of events type listen.
	 * @return Iterator return an Iterator of Event type. 
	 */
	public Enumeration getEventListeners() {
		return repository.keys();
	}

	/**
	 * Unregistered all listeners.
	 * @return boolean return true if all listeners are unregistered 
	 */
	public boolean unregisterAll() {
		repository.clear();
		return true;
	}

	/**
	 * Count how many event type is listened.
	 * @return int number of event type registered.
	 */
	public int countEventListeners() {
		return repository.size();
	}

	/**
	 * Start listening and Wait a for events.
	 */
	public void startAndWait() {
		if (managerThread != null && managerThread.isAlive())
			return;
		managerThread = null;
		managerThread = new Thread(this);
		//managerThread.setDaemon(true);
		managerThread.start();
	}

	/**
	 * Stop handling the events.
	 */
	public void stop() {
		isStopped = true;
	}

	/**
	 * Run method for our thread. If an SDLException as occured in a listener, it will not stop manager.
	 */
	public void run() {
		isStopped = false;
		do {
			try {
				notifyEvent(SDLEvent.waitEvent(!isStopped));
			} catch (SDLException se) {
				if (!isStopped)
					System.err.println("An error has occured while listening events : " + SDLMain.getError());
			}
		} while (!isStopped);
	}

	/**
	 * Notify all listeners registered for the event type of the new SDLEvent.
	 * @param event The new SDLEvent 
	 */
	public void notifyEvent(SDLEvent event) {
		if (event != null)
			synchronized (repository) {
				Vector listeners = (Vector) repository.get(event.getClass());
				// if no list available, exit method and wait for a new event
				if (listeners == null)
					return;
				// if empty listener list exist for this event type, remove it and get out. 
				else if (listeners.size() == 0) {
					repository.remove(event.getClass());
					return;
				}
				// for all listeners of the event type, notify them.
				Enumeration ite = listeners.elements();
				while (ite.hasMoreElements()) {
					((SDLEventListener) ite.nextElement()).handleEvent(event);
				}
			}
	}
}
