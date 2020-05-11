package org.thenesis.midpath.test.suite.midp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import javax.microedition.io.Connector;
import javax.microedition.io.file.FileConnection;
import javax.microedition.io.file.FileSystemRegistry;

import org.thenesis.midpath.test.suite.AbstractTestSuite;

public class FileConnectionTestSuite extends AbstractTestSuite {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		FileConnectionTestSuite testSuite = new FileConnectionTestSuite("FileConnectionTestSuite");
		try {
			testSuite.testFile();
			testSuite.testDir();
			testSuite.testList();
			testSuite.testRandomAccess();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public FileConnectionTestSuite(String className) {
		super(className);
	}

	public void testFile() throws IOException {

		checkPoint("File");
		verbose("== File operation tests ==");

		FileConnection fconn = (FileConnection) Connector.open("file:////tmp/test.txt");
		boolean fileExists = fconn.exists();

		if (!fileExists) {
			debug("File doesn't exist. Creating it...");
			fconn.create();
			check(fconn.exists(), true, "createNewFile");
		} else {
			debug("File already exists.");
		}

		check(fconn.canRead(), "canRead");
		check(fconn.canWrite(), "canWrite");
		check(!fconn.isDirectory(), "isDirectory");
		//check(!fconn.isHidden(), "isHidden()");
		check(fconn.fileSize(), 0, "fileSize");
		check(fconn.getName(), "test.txt", "getName");
		debug("Path: " + fconn.getPath());
		debug("URL: " + fconn.getURL());

		fconn.rename("test2");
		check(fconn.exists(), "rename");

		fconn.delete();
		check(fconn.exists(), false, "delete");

	}

	public void testDir() throws IOException {

		checkPoint("Dir");
		verbose("==Directory operation tests==");

		FileConnection fconn = (FileConnection) Connector.open("file:////tmp/test/");
		boolean fileExists = fconn.exists();

		if (!fileExists) {
			debug("Dir doesn't exist. Creating it...");
			fconn.mkdir();
			check(fconn.exists(), true, "mkdir");
		} else {
			debug("Dir already exists.");
		}

		check(fconn.canRead(), "canRead");
		check(fconn.canWrite(), "canWrite");
		check(fconn.isDirectory(), "isDirectory");
		//check(!fconn.isHidden(), "isHidden()");
		check(fconn.getName(), "test/", "getName");

		fconn.rename("test2");
		check(fconn.exists(), "rename");

		fconn.delete();
		check(fconn.exists(), false, "delete");

	}

	public void testList() throws IOException {

		checkPoint("List files");
		verbose("==File listing tests==");
		
		Enumeration e = FileSystemRegistry.listRoots();
		String firstRoot = (String) e.nextElement();
		debug("First root: " + firstRoot);
		check(firstRoot != null, "listRoots");

		FileConnection fconn = (FileConnection) Connector.open("file:///" + firstRoot);
		Enumeration list = fconn.list();
		check(list.hasMoreElements(), "list()");
		String firstFile = (String)list.nextElement();
		debug("First file in the  list: " + firstFile);
		FileConnection fconn2 = (FileConnection) Connector.open("file:///" + firstRoot + "/" + firstFile);
		check(fconn2.exists());

	}
	
	public void testRandomAccess() throws IOException {
		
		checkPoint("Random access (Write/Read)");
		verbose("==Write/Read tests==");
		
		FileConnection fconn = (FileConnection) Connector.open("file:////tmp/ratest");
		
		// Clean
		if (!fconn.exists()) {
			fconn.delete();
			fconn.create();
		}
		OutputStream os = fconn.openOutputStream();
		
		byte[] buf = { 56, 34, 26 };
		os.write((byte)22);
		os.write(buf);
		os.close();
		
		fconn = (FileConnection) Connector.open("file:////tmp/ratest");
		InputStream is = fconn.openInputStream();
		check(is.read(), 22);
		is.read(buf);
		check(buf[0], 56);
		check(buf[1], 34);
		check(buf[2], 26);
		is.close();
		
		check(fconn.fileSize(), 4, "fileSize");
		
		fconn.truncate(3);
		check(fconn.fileSize(), 3, "truncate");
		
		//fconn.delete();
		
	
	}
	
	

}
