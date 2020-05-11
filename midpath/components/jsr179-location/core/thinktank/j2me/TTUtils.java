/*
 * Copyright ThinkTank Mathematics Limited 2006 - 2008
 *
 * This file is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This file is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this file. If not, see <http://www.gnu.org/licenses/>.
 */
package thinktank.j2me;

import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import com.sun.midp.log.LogChannels;
import com.sun.midp.log.Logging;

/**
 * Useful J2ME utility methods which offer some level of similarity to familiar J2SE
 * methods.
 * 
 * @author Samuel Halliday, ThinkTank Mathematics Limited
 */
public final class TTUtils {

	/**
	 * Allows TTUtils to redirect the log info to a class of your choice.
	 * 
	 * @author Samuel Halliday, ThinkTank Mathematics Limited
	 */
	public interface ILogger {
		/**
		 * @param message
		 */
		public void log(String message);
	}

	private volatile static ILogger logger = null;

	/**
	 * If an {@link ILogger} has been registered via
	 * {@link #registerLogger(thinktank.j2me.TTUtils.ILogger)} then the message will be
	 * sent there, otherwise will be printed on System.out.
	 * 
	 * @param message
	 */
	private static void log(String message) {
		if (!message.endsWith("\n"))
			message = message + "\n";
		if (logger != null)
			logger.log(message);
	}

	public static void logInfo(String message) {
		log(message);
		if (Logging.REPORT_LEVEL <= Logging.INFORMATION) {
			Logging.report(Logging.INFORMATION, LogChannels.LC_JSR179, message);
		}
	}

	public static void logWarning(String message) {
		log(message);
		if (Logging.REPORT_LEVEL <= Logging.WARNING) {
			Logging.report(Logging.WARNING, LogChannels.LC_JSR179, message);
		}
	}

	/**
	 * Read a line from the input, with newlines stripped. Consecutive newlines are
	 * ignored. null is returned when we attempt to read beyond the end of the stream.
	 * 
	 * @param input
	 * @return
	 * @throws IOException
	 */
	public static String readLine(InputStream input) throws IOException {
		// ??: StringBuffer is slow... use something faster
		StringBuffer builder = new StringBuffer(80);

		int i;
		while ((i = input.read()) != -1) {
			char c = (char) i;
			if ((c == '\n') || (c == '\r')) {
				// end of line
				if (builder.length() == 0)
					// newline at start of line, ignore
					continue;

				return builder.toString();
			}
			builder.append(c);
		}
		if (builder.length() > 0)
			return builder.toString();
		return null;
	}

	/**
	 * @param logger
	 *            null to deregister
	 */
	public static void registerLogger(ILogger logger) {
		TTUtils.logger = logger;
	}

	/**
	 * J2ME implementation of String.split(). Is very fragile.
	 * 
	 * @param string
	 * @param separator
	 * @return
	 */
	public static String[] stringSplit(String string, char separator) {
		Vector parts = new Vector();
		int start = 0;
		for (int i = 0; i <= string.length(); i++) {
			if ((i == string.length()) || (string.charAt(i) == separator)) {
				if (start == i) {
					// no data between separators
					parts.addElement("");
				} else {
					// data between separators
					String part = string.substring(start, i);
					parts.addElement(part);
				}
				// start of next part is the char after this separator
				start = i + 1;
			}
		}
		// return as array
		String[] partsArray = new String[parts.size()];
		for (int i = 0; i < partsArray.length; i++) {
			partsArray[i] = (String) parts.elementAt(i);
		}
		return partsArray;
	}

}
