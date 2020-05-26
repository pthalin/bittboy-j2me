/*
 * Created on 27.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.avetana.bluetooth.obex;

import java.io.IOException;

import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;
import javax.obex.Authenticator;
import javax.obex.ServerRequestHandler;
import javax.obex.SessionNotifier;

import de.avetana.bluetooth.stack.BlueZ;

/**
 * @author gmelin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

public class SessionNotifierImpl implements SessionNotifier {

	private StreamConnectionNotifier locConNot;

	public SessionNotifierImpl(StreamConnectionNotifier locConNot) {
		this.locConNot = locConNot;
	}

	/* (non-Javadoc)
	 * 	 * @see javax.obex.SessionNotifier#acceptAndOpen(javax.obex.ServerRequestHandler)
	 */
	public synchronized Connection acceptAndOpen(ServerRequestHandler handler) throws IOException {
		return this.acceptAndOpen(handler, null);
	}

	public synchronized Connection acceptAndOpen(ServerRequestHandler handler, Authenticator auth) throws IOException {
		if (locConNot == null)
			throw new IOException("ConnectionNotifier is null ! maybe it was closed previousely..");
		StreamConnection streamCon = locConNot.acceptAndOpen();
		SessionHandler sh = new SessionHandler(streamCon, auth, handler);
		try {
			BlueZ.executor.execute(sh);
		} catch (Exception e) {
			e.printStackTrace();
			new Thread(sh).start();
		}
		return sh;
	}

	public StreamConnectionNotifier getConnectionNotifier() {
		return (StreamConnectionNotifier) locConNot;
	}

	public void close() throws IOException {
		if (locConNot != null)
			locConNot.close();
		locConNot = null;
	}

}
