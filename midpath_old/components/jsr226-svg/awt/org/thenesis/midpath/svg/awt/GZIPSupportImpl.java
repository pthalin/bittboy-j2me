/*
 * MIDPath - Copyright (C) 2006-2008 Guillaume Legris, Mathieu Legris
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
 */
package org.thenesis.midpath.svg.awt;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

import com.sun.perseus.platform.GZIPSupport;

/**
 * This class is used to provide GZIP support to the Perseus engine by using 
 * a custom implementation of GZIPInputStream
 * <code>com.sun.perseus.platform.GZIPInputStream</code>
 * 
 *
 */
public class GZIPSupportImpl extends GZIPSupport {

	/**
	 * If GZIP encoding is supported, this method should setup the
	 * HTTP Request Header to declare that GZIP encoding is supported.
	 *
	 * @param svgURI the url of the requested SVG resource.
	 * @return a stream that does not handles GZIP uncompression.
	 * @throws IOException if an I/O error happens while opening the 
	 *         requested URI.
	 */
	public InputStream openHandleGZIP(String svgURI) throws IOException {

		URI uri = null;
		try {
			uri = new URI(svgURI);
		} catch (URISyntaxException e) {
			throw new IOException(e.getMessage());
		}
		URL url = uri.toURL();
		URLConnection connection = url.openConnection();

		try {
			if (connection instanceof HttpURLConnection) {
				setupHttpEncoding((HttpURLConnection) connection);
			}
			connection.connect();
			return (connection.getInputStream());
		} finally {
			// FIXME ?
			//connection.close(); 
		}

	}

	/**
	 *
	 */
	static void setupHttpEncoding(final HttpURLConnection httpC) throws IOException {
		String encodings = httpC.getRequestProperty(HTTP_ACCEPT_ENCODING);

		if (encodings == null) {
			encodings = "";
		}

		if (encodings.trim().length() > 0) {
			encodings += ",";
		}
		encodings += HTTP_GZIP_ENCODING;
		httpC.setRequestProperty(HTTP_ACCEPT_ENCODING, encodings);
	}

}
