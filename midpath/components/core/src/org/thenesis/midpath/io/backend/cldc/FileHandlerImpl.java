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
package org.thenesis.midpath.io.backend.cldc;

import java.io.IOException;
import java.util.Vector;

import org.thenesis.midpath.io.AbstractFileHandler;

import com.sun.midp.io.j2me.file.RandomAccessStream;
import com.sun.midp.log.Logging;


public class FileHandlerImpl extends AbstractFileHandler {
	
	private File file;
	//private String rootName;
	//private String absFile;
	private RandomAccessFile randomAccessFile;
	
	public FileHandlerImpl() {}

	public long availableSize() {
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] FileHandlerImpl.availableSize(): not implemented yet");
		return 200000;
		//return -1;
	}

	public boolean canRead() {
		return file.canRead();
	}

	public boolean canWrite() {
		return file.canWrite();
	}

	public void closeForRead() throws IOException {
		// TODO
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] FileHandlerImpl.closeForRead(): not implemented yet");
	}

	public void closeForWrite() throws IOException {
		// TODO
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] FileHandlerImpl.closeForRead(): not implemented yet");
	}

	public void connect(String rootName, String absFile) {
		//this.rootName = rootName;
		//this.absFile = absFile;
		if (rootName.equals("")) {
			file = new File(absFile);
		} else {
			file = new File(rootName, absFile);
		}
		file = file.getAbsoluteFile();
		
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] FileHandlerImpl.connect(): " + file.getPath());
	}

	public void create() throws IOException {
		if (!file.createNewFile()) {
			throw new IOException("Can't create file");
		}
	}

	public void createPrivateDir(String rootName) throws IOException {
		// TODO Auto-generated method stub
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] FileHandlerImpl.createPrivateDir(): not implemented yet");
	}

	public void delete() throws IOException {
		if (randomAccessFile != null) {
			randomAccessFile.close();
		}
		
		file.delete();
	}

	public boolean exists() {
		return file.exists();
	}

	public long fileSize() throws IOException {
		return file.length();
	}

	public String illegalFileNameChars() {
		// TODO
		return "";
	}

	public boolean isDirectory() {
		return file.isDirectory();
	}

	public boolean isHidden() {
		return file.isHidden();
	}

	public long lastModified() {
		return file.lastModified();
	}

	public Vector list(final String filter, boolean includeHidden) throws IOException {
		
		if(!file.isDirectory()) {
			new IOException("File is not a directory");
		}
		
		FilenameFilter fileFilter = new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				boolean res = filterAccept(filter, filename);
				//System.out.println("[DEBUG] FileHandlerImpl.list(): filter " + filename + " ? " + res);
				return res;
			}
		};
		
		String[] filenames;
		if(filter == null) {
			filenames = file.list();
		} else {
			filenames = file.list(fileFilter);
		}
		
		Vector v = new Vector();
		
		if (filenames != null) {
			for (int i = 0; i < filenames.length; i++) {
				v.addElement(filenames[i]);
			}
		}
		
		return v;
	}
	

	public Vector listRoots() {
		
		File[] files = File.listRoots();
		Vector v = new Vector();
		for (int i = 0; i < files.length; i++) {
			// Add a path separator to the end of the file root if needed
			String rootFilename = files[i].getAbsolutePath();
			rootFilename = rootFilename.replace('\\', '/');
			if (!rootFilename.endsWith("/")) {
				rootFilename += "/";
			}
			v.addElement(rootFilename);
		}
		
		return v;
		
	}

	public void mkdir() throws IOException {
		file.mkdir();
		
	}

	public void openForRead() throws IOException {
		if (randomAccessFile == null) {
			//stream = new FileRandomAccessStream(file);
			randomAccessFile = new RandomAccessFile(file, "rws");
		}
	}

	public void openForWrite() throws IOException {
		if (randomAccessFile == null) {
			randomAccessFile = new RandomAccessFile(file, "rws");
		}
	}

	public void positionForWrite(long offset) throws IOException {
		if (randomAccessFile == null) {
			throw new IOException();
		}
		
		seek((int)offset);
	}

	public void rename(String newName) throws IOException {
		
		if (randomAccessFile != null) {
			randomAccessFile.close();
		}
		
		// FIXME Hack ?
		if (newName.startsWith("/")) {
			newName = newName.substring(1, newName.length());
		}
		File newFile = new File(newName);
		
//		int index = newName.lastIndexOf("/");
//		if (index != -1) {
//			newName = newName.substring(index + 1, newName.length());
//		}
//		
//		File newFile = new File(file.getParent(), newName);
		
		if(!file.renameTo(newFile)) {
			throw new IOException("Can't rename file");
		}
		
		file = newFile;
		
//		System.out.println("[DEBUG] FileHandlerImpl.rename(): " + file);
//		System.out.println("[DEBUG] FileHandlerImpl.rename(): " + newFile);
	}

	public void setHidden(boolean hidden) throws IOException {
		// TODO Auto-generated method stub
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] FileHandlerImpl.setHidden(): not implemented yet");
	}

	public void setReadable(boolean readable) throws IOException {
		// TODO Auto-generated method stub
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] FileHandlerImpl.setReadable: not implemented yet");
		
	}

	public void setWritable(boolean writable) throws IOException {
		// TODO Auto-generated method stub
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] FileHandlerImpl.setWritable: not implemented yet");
	}

	public long totalSize() {
		// TODO Auto-generated method stub
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] FileHandlerImpl.totalSize(): not implemented yet");
		return -1;
	}

	public void truncate(long byteOffset) throws IOException {
		if (randomAccessFile == null) {
			throw new IOException();
		}
		setLength((int)byteOffset);
	}

	public long usedSize() {
		// TODO Auto-generated method stub
		if (Logging.TRACE_ENABLED)
			System.out.println("[DEBUG] FileHandlerImpl.usedSize(): not implemented yet");
		return -1;
	}

	public int read(byte[] buf) throws IOException {
		return read(buf, 0, buf.length);
	}
	
	public int read(byte[] b, int off, int len) throws IOException {
		if (randomAccessFile == null) {
			throw new IOException();
		}
		//System.out.println("[DEBUG] FileHandlerImpl.read: file: " + file);
		return randomAccessFile.read(b, off, len);
	}

	public void seek(int pos) throws IOException {
		randomAccessFile.seek(pos);
		
	}

	public void setLength(int size) throws IOException {
		randomAccessFile.setLength(size);
		
	}
	
	public int write(byte[] b, int off, int len) throws IOException {
		randomAccessFile.write(b, off, len);
		return len;
	}

	public int write(byte[] buf) throws IOException {
		return write(buf, 0, buf.length);
	}
	
	public void flush() throws IOException {
		// TODO Auto-generated method stub
	}
	
	public void close() throws IOException {
		if (randomAccessFile != null) {
			randomAccessFile.close();
		}
	}

	public RandomAccessStream getRandomAccessStream() {
		return this;
	}

}
