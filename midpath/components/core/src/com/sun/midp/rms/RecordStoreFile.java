package com.sun.midp.rms;

import java.io.IOException;
import java.util.Vector;

import javax.microedition.rms.RecordStoreException;

import com.sun.midp.io.IOToolkit;
import com.sun.midp.io.j2me.file.BaseFileHandler;
import com.sun.midp.io.j2me.file.RandomAccessStream;
import com.sun.midp.log.Logging;
import com.sun.midp.main.Configuration;

public class RecordStoreFile implements AbstractRecordStoreFile {

	private static String RMS_ROOT_PATH;
	
	static {
		RMS_ROOT_PATH = Configuration.getPropertyDefault("com.sun.midp.rms.rootPath", "");
	}
	
	private IOToolkit toolkit;
	private RandomAccessStream stream;
	private BaseFileHandler fileHandler;


	public RecordStoreFile(String suiteID, String name, String idx_extension) throws IOException {
		toolkit = IOToolkit.getToolkit();
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] RecordStoreFile.<init>(): " + suiteID + "-" + name + "." + idx_extension);
		
		//stream =  toolkit.createRandomAccessStream(suiteID + "-" + name + "." + idx_extension);
		//stream =  toolkit.getBaseFileHandler().getRandomAccessStream();
		fileHandler = toolkit.createBaseFileHandler();
		fileHandler.connect(RMS_ROOT_PATH, buildRecordStoreName(suiteID, name, idx_extension));
		fileHandler.openForRead();
		fileHandler.openForWrite();
		stream = fileHandler.getRandomAccessStream();
		//		} catch (IOException e) {
		//			Logging.trace(e,"");
		//			//e.printStackTrace();
		//		}
	}

	public void close() throws IOException {
		stream.close();
	}

	public void commitWrite() throws IOException {
		stream.flush();
	}

	public int read(byte[] buf) throws IOException {
		return stream.read(buf);
	}

	public int read(byte[] buf, int offset, int numBytes) throws IOException {
		return stream.read(buf, offset, numBytes);
	}

	public void seek(int pos) throws IOException {
		stream.seek(pos);
	}

	public int spaceAvailable(String suiteID) {
		// TODO
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] RecordStoreFile.spaceAvailable(): not implemented yet");
		return (int) fileHandler.availableSize();
	}

	public void truncate(int size) throws IOException {
		stream.setLength(size);
	}

	public void write(byte[] buf) throws IOException {
		stream.write(buf);
	}

	public void write(byte[] buf, int offset, int numBytes) throws IOException {
		stream.write(buf, offset, numBytes);
	}

	public static int spaceAvailableNewRecordStore(String suiteID) {
		// TODO
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] RecordStoreFile.spaceAvailableNewRecordStore(): not implemented yet");
		return 200000;
	}

	public static String[] listRecordStores(String suiteID) {
		
		String[] stores = null;
		
		try {
			BaseFileHandler fileHandler = IOToolkit.getToolkit().createBaseFileHandler();
			fileHandler.connect(RMS_ROOT_PATH, "");
			Vector v = fileHandler.list(suiteID + "*", true);
			stores = new String[v.size()];
			for (int i = 0; i < stores.length; i++) {
				stores[i] = getRecordStoreName((String)v.elementAt(i));
			}
		} catch (IOException e) {
			if (Logging.TRACE_ENABLED)
				Logging.trace(e, "Error while listing record stores");
		}

		return stores;
	}

	public static void removeRecordStores(String suiteID) {
		try {
			BaseFileHandler rootHandler = IOToolkit.getToolkit().createBaseFileHandler();
			rootHandler.connect(RMS_ROOT_PATH, "");
			Vector v = rootHandler.list(suiteID + "*", true);
			for (int i = 0; i < v.size(); i++) {
				BaseFileHandler fileHandler = IOToolkit.getToolkit().createBaseFileHandler();
				fileHandler.connect(RMS_ROOT_PATH, (String)v.elementAt(i));
				fileHandler.delete();
				fileHandler.close();
			}
		} catch (IOException e) {
			if (Logging.TRACE_ENABLED)
				Logging.trace(e, "Error while removing record stores");
		}
	}

	public static String buildRecordStoreName(String suiteID, String name, String idx_extension) {
		return suiteID + "-" + name + "." + idx_extension;
	}
	
	public static String getRecordStoreName(String fileName) {
		int indentIndex = fileName.indexOf('-');
		int pointIndex = fileName.lastIndexOf('.');
		return fileName.substring(indentIndex + 1, pointIndex);
	}

	/**
	 * Looks to see if the storage file for record store
	 * identified by <code>uidPath</code> exists
	 *
	 * @param suiteID ID of the MIDlet suite that owns the record store
	 * @param name name of the record store
	 * @param extension the extension for the record store file
	 *
	 * @return true if the file exists, false if it does not.
	 */
	static boolean exists(String suiteID, String name, String extension) {
		BaseFileHandler fileHandler = IOToolkit.getToolkit().createBaseFileHandler();
		fileHandler.connect(RMS_ROOT_PATH, buildRecordStoreName(suiteID, name, extension));
		return fileHandler.exists();
		//		System.out.println("[DEBUG] RecordStoreUtil.exists(): not implemented yet");
		//		return true;
	}

	/**
	 * Removes the storage file for record store <code>filename</code>
	 * if it exists.
	 *
	 * @param suiteID ID of the MIDlet suite that owns the record store
	 * @param name name of the record store
	 * @param extension the extension for the record store file
	 *
	 * @throws RecordStoreException if deletion encountered an error
	 *         internally.
	 */
	static void deleteFile(String suiteID, String name, String extension) throws RecordStoreException {
		BaseFileHandler fileHandler = IOToolkit.getToolkit().createBaseFileHandler();
		fileHandler.connect(RMS_ROOT_PATH, buildRecordStoreName(suiteID, name, extension));
		try {
			fileHandler.delete();
		} catch (IOException e) {
			// e.printStackTrace();
			throw new RecordStoreException(e.getMessage());
		}

	}

	/**
	 * Removes record store file without throwing an exception on failure.
	 *
	 * @param suiteID ID of the MIDlet suite that owns the record store
	 * @param name name of the record store
	 * @param extension the extension for the record store file
	 *
	 * @return <code>true</code> if file was found and deleted successfully,
	 *         <code>false</code> otherwise.
	 */
	static boolean quietDeleteFile(String suiteID, String name, String extension) {
		try {
			deleteFile(suiteID, name, extension);
		} catch (Throwable t) {
			return false;
		}
		return true;
	}

}
