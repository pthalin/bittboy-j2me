package de.avetana.bluetooth.rfcomm;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.StreamConnection;

import de.avetana.bluetooth.connection.BTConnection;
import de.avetana.bluetooth.connection.JSR82URL;
import de.avetana.bluetooth.stack.BlueZ;

/**
 * The top-class for the management of stream-oriented connections.
 *
 * <br><br><b>COPYRIGHT:</b><br> (c) Copyright 2004 Avetana GmbH ALL RIGHTS RESERVED. <br><br>
 *
 * This file is part of the Avetana bluetooth API for Linux.<br><br>
 *
 * The Avetana bluetooth API for Linux is free software; you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2 of
 * the License, or (at your option) any later version. <br><br>
 *
 * The Avetana bluetooth API is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.<br><br>
 *
 * The development of the Avetana bluetooth API is based on the work of
 * Christian Lorenz (see the Javabluetooth Stack at http://www.javabluetooth.org) for some classes,
 * on the work of the jbluez team (see http://jbluez.sourceforge.net/) and
 * on the work of the bluez team (see the BlueZ linux Stack at http://www.bluez.org) for the C code.
 * Classes, part of classes, C functions or part of C functions programmed by these teams and/or persons
 * are explicitly mentioned.<br><br><br><br>
 *
 *
 * <b>Description:</b><br>
 * This class allows the management of data streams between a remote and local device.
 * It provides an InputStream and an OutputStream for easily receiving and sending data from/to a remote device.
 * The InputStream stores all retrieved data into its buffer: no data is therefore lost. <br>
 *
 * @author Julien Campana
 */
public class RFCommConnectionImpl extends BTConnection implements StreamConnection {

	/**
	 * The own defined and implemented input stream.
	 */
	protected MInputStream inStream = new MInputStream();

	/**
	 * The own defined and implemented output stream.
	 */
	protected MOutputStream outStream = new MOutputStream();

	protected RFCommConnectionImpl(int a_fid) {
		super(a_fid);
	}

	/**
	 * @param fid
	 * @param string
	 */
	public RFCommConnectionImpl(int fid, String string) {
		super(fid, string);
	}

	/**
	 * Creates a new RFCOMM connection with a remote BT device.
	 * @param url The connection URL encapsulating all connection options
	 * @return An instance of RFCommConnection with manages the Output and InputStream connections streams
	 * @throws Exception
	 */
	public static RFCommConnectionImpl createRFCommConnection(JSR82URL url, int timeout) throws Exception {
		int fid = -1;

		fid = BlueZ.openRFComm(url, timeout);
		if (fid < 0)
			throw new Exception("Connection could not be created with remote device!");

		RFCommConnectionImpl conn = null;
		conn = new RFCommConnectionImpl(fid, url.getBTAddress().toString());
		return conn;
	}

	/**
	 * If the nested input stream was not opened before, opens it and starts reading. Returns the DataInputStream based
	 * on this opened nested input stream.
	 * @return The DataInputStream based on this opened nested input stream.
	 * @throws java.io.IOException
	 */
	public DataInputStream openDataInputStream() throws java.io.IOException {
		return new DataInputStream(openInputStream());
	}

	/**
	 * Opens and returns the nested output stream.
	 * @return The nested ouput stream
	 * @throws java.io.IOException
	 */
	public OutputStream openOutputStream() throws java.io.IOException {
		return outStream;
	}

	/**
	 * Opens the nested output stream and returns the DataOutputStream based on it.
	 * @return The nested OuputStream.
	 * @throws java.io.IOException
	 */
	public DataOutputStream openDataOutputStream() throws java.io.IOException {
		return new DataOutputStream(outStream);
	}

	/**
	 * Returns the inputstream used by this connection
	 * @return The inputstream used by this connection
	 */
	public InputStream openInputStream() throws IOException {
		return inStream;
	}

	/**
	 * An own extension of the classical java InputStream class.
	 * @author Moritz Gmelin
	 */
	protected class MInputStream extends InputStream {

		private boolean closed = false;

		public synchronized int available() throws IOException {
			if (closed)
				throw new IOException("InputStream closed");
			return RFCommConnectionImpl.this.available();
		}

		public synchronized int read() throws IOException {
			if (closed)
				throw new IOException("InputStream closed");
			byte b[] = RFCommConnectionImpl.this.read(1);
			return (int) (b[0] & 0xff);
		}

		public synchronized int read(byte[] b, int off, int len) throws IOException {
			if (closed)
				throw new IOException("InputStream closed");
			byte b2[] = RFCommConnectionImpl.this.read(len);
			System.arraycopy(b2, 0, b, off, b2.length);
			return b2.length;
		}

		public void close() {
			closed = true;
		}

	}

	/**
	 * An own extension of the classical java OutputStream class.
	 * @author Moritz Gmelin
	 */
	protected class MOutputStream extends OutputStream {

		private boolean closed = false;

		public void write(int data) throws IOException {
			if (closed)
				throw new IOException("OutputStream closed");
			BlueZ.writeBytesS(fid, new byte[] { (byte) data }, 0, 1);
		}

		public void write(byte[] b) throws IOException {
			if (closed)
				throw new IOException("OutputStream closed");
			BlueZ.writeBytesS(fid, b, 0, b.length);
		}

		public void write(byte[] b, int off, int len) throws IOException {
			if (closed)
				throw new IOException("OutputStream closed");
			BlueZ.writeBytesS(fid, b, off, len);
		}

		public void flush() throws IOException {
			if (closed)
				throw new IOException("OutputStream closed");
			BlueZ.flush(fid);
		}

		public void close() {
			closed = true;
		}
	}

}
