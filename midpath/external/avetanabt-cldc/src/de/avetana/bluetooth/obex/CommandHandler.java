/*
 * Created on 27.10.2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package de.avetana.bluetooth.obex;

import java.io.IOException;

import javax.obex.Authenticator;
import javax.obex.HeaderSet;
/**
 * @author gmelin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface CommandHandler {

	 public byte[] receiveCommand() throws IOException ;
	 public void sendCommand(int type, byte b[]) throws IOException;
	 public int getMTU();
	 public HeaderSet createHeaderSet();
	 public Authenticator getAuthenticator();
}
